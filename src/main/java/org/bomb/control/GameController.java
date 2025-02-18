package org.bomb.control;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.*;

import javax.swing.JOptionPane;

import org.bomb.BombGame;
import org.bomb.BombGame.GameState;
import org.bomb.model.*;

public class GameController implements KeyListener{

	//Instances principales du jeu
	private ArrayList<Bomberman> bombers;
	private ArrayList<Tile> blocks;
	private ArrayList<Bonus> bonus;
	private ArrayList<Tile> brokenBricks; 
	private Spawner spawner;
	private EnemyHandler handler;
	private Bomberman winner;
	private Bomberman player;

	//cheatCode
	private final String cheatCode= "pok";
	private ArrayList<String> cheat;
	private int cheatTime=0;
	private boolean cheatActivated;
	
	//Mouvements du joueur
	//left 0 ; right 1 ; up 3 ; down 4
	private final boolean[] keyPressedP1 = new boolean[4];
	private final boolean[] keyPressedP2 = new boolean[4];
	private final boolean[] keyPressedP3 = new boolean[4];
	private final boolean[] keyPressedP4 = new boolean[4];
	
	private int invinTime=0; //Compteur pour le temps d'invinsibilit�
	private int pauseCount=0; //Compteur pour la pause; permet de savoir si activ�e ou pas
	private GameState saveState; //Sauvegarde du GameState n�cessaire pour la pause
	
	private String highScore;

	private static final Map<GameState, String> STATE_SOUNDS = Map.of(
			GameState.Menu, "menu",
			GameState.MultiOver, "victory",
			GameState.SoloOver, "gameOver",
			GameState.ChooseMulti, "chooseMulti",
			GameState.ChooseSolo, "chooseSolo"
	);
	
	public GameController(){
		highScore = getHighScore();
	}

	public void initMulti(){
		Arrays.fill(keyPressedP1, false);
		Arrays.fill(keyPressedP2, false);
		Arrays.fill(keyPressedP3, false);
		Arrays.fill(keyPressedP4, false);
		Field mF = new MultiField();
		blocks = mF.getBlocks();
		bonus = mF.getBonus();
		brokenBricks = new ArrayList<>();
		bombers = new ArrayList<>();
		handler = new EnemyHandler();
	}
	
	public void initSolo(){
		Arrays.fill(keyPressedP1, false);
		Field sF = new SoloField();
		blocks = sF.getBlocks();
		bonus = sF.getBonus();
		brokenBricks = new ArrayList<>();
		bombers = new ArrayList<>();
		cheatActivated=false;
		cheat = new ArrayList<>();
		handler = new EnemyHandler();
		spawner = new Spawner(handler,bonus,blocks);
	}

	public ArrayList<Bomberman> getBombers(){
		return bombers;
	}
	public Bomberman getWinner(){
		return winner;
	}
	public Bomberman getBomber(int i){
		return bombers.get(i);
	}
	public ArrayList<Tile> getBlocks(){
		return blocks;
	}
	public ArrayList<Bonus> getBonus(){
		return bonus;
	}
	public ArrayList<Tile> getBrokenBricks(){
		return brokenBricks;
	}
	public Bomberman getPlayer(){
		return player;
	}
	public void setPlayer(Bomberman player){
		this.player=player;
	}
	public EnemyHandler getHandler(){
		return handler;
	}
	public Spawner getSpawner(){
		return spawner;
	}

	public boolean[] giveKeys(Bomberman b) {
		return switch (b.getId()) {
			case Player1 -> keyPressedP1;
			case Player2 -> keyPressedP2;
			case Player3 -> keyPressedP3;
			case Player4 -> keyPressedP4;
			default -> new boolean[4]; // Default empty keys
		};
	}

	private String makeWord(){
		StringBuilder word = new StringBuilder();
        for (String letter : cheat) {
            word.append(letter);
        }
		return word.toString();
	}

	private void checkCheat(String mot) {
		if (cheatCode.equals(mot)) { // Avoids potential NullPointerException
			for (Bomberman bomber : bombers) {
				bomber.toCheat(); // No need to use getBomber(i)
			}
			cheatActivated = true;
			cheat.clear();
		}
	}

	private void checkScore(int score, int level) {
		String[] highScoreData = highScore.split(":");
		int currentHighScore = Integer.parseInt(highScoreData[1]);

		if (score > currentHighScore) {
			String name = JOptionPane.showInputDialog("Nouveau record ! Entrez votre nom");
			highScore = name + ":" + score + ":" + level;

			File scoreFile = new File("highscore.dat");

			try (BufferedWriter writer = new BufferedWriter(new FileWriter(scoreFile))) {
				writer.write(highScore);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String getHighScore() {
		try (BufferedReader reader = new BufferedReader(new FileReader("highscore.dat"))) {
			String line = reader.readLine();
			return (line != null) ? line : "None:0:0"; // Prevent returning null
		} catch (IOException e) {
			return "None:0:0"; // Default score if file error occurs
		}
	}

	public void update(){
		switch (BombGame.STATE) {
			case Solo -> {
				restartSoundIfOver(cheatActivated ? "cheat" : "route");
				if (!cheatActivated) {
					if (++cheatTime % 300 == 0) { // Max 5 sec to enter cheat
						cheat.clear();
						cheatTime = 0;
					}
					checkCheat(makeWord());
				}
				if (player.isAlive()) {
					if (player.isInvinsible() && ++invinTime % 120 == 0) {
						player.setInvinsible(false);
						invinTime = 0;
					}
					player.update(blocks, bonus, handler, bombers, brokenBricks, giveKeys(player));
				} else {
					BombGame.STATE = GameState.SoloOver;
					BombGame.sound.soundEnd();
					BombGame.sound.play("gameOver");

					if (!cheatActivated) checkScore(player.getScore(), spawner.getLevel());
				}
				spawner.update();
				handler.update(blocks, player, bonus);
			}
			case Multi -> {
				restartSoundIfOver("battle");

				boolean resetInvincibility = (++invinTime % 120 == 0);
				if (resetInvincibility) invinTime = 0;

				for (Bomberman b : bombers) {
					if (resetInvincibility) b.setInvinsible(false);

					if (b.isAlive()) {
						b.update(blocks, bonus, handler, bombers, brokenBricks, giveKeys(b));
					}
				}
				// Safe removal of dead bombers
				bombers.removeIf(b -> !b.isAlive());

				if (bombers.size() == 1) {
					winner = bombers.getFirst();
					BombGame.STATE = GameState.MultiOver;
					BombGame.sound.soundEnd();
					BombGame.sound.play("victory");
				}
			}
			default -> playStateSound();
		}
	}

	private void playStateSound() {
		String soundName = STATE_SOUNDS.get(BombGame.STATE);
		if (soundName != null) restartSoundIfOver(soundName);
	}

	private void restartSoundIfOver(String name){
		if(BombGame.sound.isStopped()){
			BombGame.sound.play(name);
		}
	}

	@Override
	public void keyPressed(KeyEvent e){
		int key = e.getKeyCode();

		if(BombGame.STATE==GameState.Multi || BombGame.STATE==GameState.Solo){
			for(Bomberman b : bombers){
				if(b.getId() == ID.Player1){
					if (key == KeyEvent.VK_LEFT) keyPressedP1[0]=true;
					if (key == KeyEvent.VK_RIGHT) keyPressedP1[1]=true;
					if (key == KeyEvent.VK_UP) keyPressedP1[2]=true;
					if (key == KeyEvent.VK_DOWN) keyPressedP1[3]=true;
					if (key == KeyEvent.VK_M) b.dropMine();
					if (key == KeyEvent.VK_ENTER) b.dropBomb();
					if(key == KeyEvent.VK_SPACE && BombGame.STATE==GameState.Solo) b.shoot();//Les projectiles sont seulement disponibles en mode solo
				}

				if(b.getId() == ID.Player2){
					if (key == KeyEvent.VK_Q) keyPressedP2[0]=true;
					if (key == KeyEvent.VK_D) keyPressedP2[1]=true;
					if (key == KeyEvent.VK_Z) keyPressedP2[2]=true;
					if (key == KeyEvent.VK_S) keyPressedP2[3]=true;
					if (key == KeyEvent.VK_W) b.dropMine();
					if (key == KeyEvent.VK_SHIFT) b.dropBomb();
				}

				if(b.getId() == ID.Player3){
					if (key == KeyEvent.VK_C) keyPressedP3[0]=true;
					if (key == KeyEvent.VK_B) keyPressedP3[1]=true;
					if (key == KeyEvent.VK_G) keyPressedP3[2]=true;
					if (key == KeyEvent.VK_V) keyPressedP3[3]=true;
					if (key == KeyEvent.VK_N) b.dropMine();
					if (key == KeyEvent.VK_SPACE) b.dropBomb();
				}

				if(b.getId() == ID.Player4){
					if (key == KeyEvent.VK_J) keyPressedP4[0]=true;
					if (key == KeyEvent.VK_L) keyPressedP4[1]=true;
					if (key == KeyEvent.VK_I) keyPressedP4[2]=true;
					if (key == KeyEvent.VK_K) keyPressedP4[3]=true;
					if (key == KeyEvent.VK_O) b.dropMine();
					if (key == KeyEvent.VK_U) b.dropBomb();
				}
			}

			//CheatCode
			if(BombGame.STATE==GameState.Solo){
				if ( key == KeyEvent.VK_P) cheat.add(Character.toString(e.getKeyChar()));
				if ( key == KeyEvent.VK_O) cheat.add(Character.toString(e.getKeyChar()));
				if ( key == KeyEvent.VK_K) cheat.add(Character.toString(e.getKeyChar()));
			}
		}
		if(BombGame.STATE==GameState.Multi || BombGame.STATE==GameState.Solo || BombGame.STATE==GameState.Pause){
			if (key == KeyEvent.VK_ESCAPE){
				if(pauseCount==0){
					saveState = BombGame.STATE;
					BombGame.STATE=GameState.Pause;
					pauseCount=1;
				}
				else if(pauseCount==1){
					BombGame.STATE=saveState;
					pauseCount=0;
				}
			}
		}
    }
	@Override
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();

		if(BombGame.STATE==GameState.Multi || BombGame.STATE==GameState.Solo){
			for(Bomberman b : bombers){
				if(b.getId() == ID.Player1){
					if (key == KeyEvent.VK_LEFT) keyPressedP1[0]=false;
					if (key == KeyEvent.VK_RIGHT) keyPressedP1[1]=false;
					if (key == KeyEvent.VK_UP) keyPressedP1[2]=false;
					if(key == KeyEvent.VK_DOWN) keyPressedP1[3]=false;
				}
				if(b.getId() == ID.Player2){
					if (key == KeyEvent.VK_Q) keyPressedP2[0]=false;
					if (key == KeyEvent.VK_D) keyPressedP2[1]=false;
					if (key == KeyEvent.VK_Z) keyPressedP2[2]=false;
					if(key == KeyEvent.VK_S) keyPressedP2[3]=false;
				}
				if(b.getId() == ID.Player3){
					if (key == KeyEvent.VK_C) keyPressedP3[0]=false;
					if (key == KeyEvent.VK_B) keyPressedP3[1]=false;
					if (key == KeyEvent.VK_G) keyPressedP3[2]=false;
					if (key == KeyEvent.VK_V) keyPressedP3[3]=false;
				}
				if(b.getId() == ID.Player4){
					if (key == KeyEvent.VK_J) keyPressedP4[0]=false;
					if (key == KeyEvent.VK_L) keyPressedP4[1]=false;
					if (key == KeyEvent.VK_I) keyPressedP4[2]=false;
					if (key == KeyEvent.VK_K) keyPressedP4[3]=false;
				}
			}

		}
    }
	@Override
	public void keyTyped(KeyEvent arg0) {	
	}	
}
