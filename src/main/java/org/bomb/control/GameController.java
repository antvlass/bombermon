package org.bomb.control;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

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
	private String cheatCode= "pok";  
	private ArrayList<String> cheat;
	private int cheatTime=0;
	private boolean cheatActivated;
	
	//Mouvements du joueur
	//left 0 ; right 1 ; up 3 ; down 4
	private boolean[] keyPressedP1 = new boolean[4];
	private boolean[] keyPressedP2 = new boolean[4];
	private boolean[] keyPressedP3 = new boolean[4];
	private boolean[] keyPressedP4 = new boolean[4];
	
	private int invinTime=0; //Compteur pour le temps d'invinsibilit�
	private int pauseCount=0; //Compteur pour la pause; permet de savoir si activ�e ou pas
	private GameState saveState; //Sauvegarde du GameState n�cessaire pour la pause
	
	private String highScore;
	
	public GameController(){
		highScore = getHighScore();
	}

	/**
	 * La s�paration de l'initialisation des instances principales du jeu du contructeur permet de facilement
	 * pouvoir relancer une partie de solo ou mutlijoueur
	 */
	public void initMulti(){
		for(int i=0;i<4;i++){
			keyPressedP1[i]=false;
			keyPressedP2[i]=false;
			keyPressedP3[i]=false;
			keyPressedP4[i]=false;
		}
		Field mF = new MultiField();
		blocks = mF.getBlocks();
		bonus = mF.getBonus();
		brokenBricks = new ArrayList<Tile>();
		bombers = new ArrayList<Bomberman>();
		handler = new EnemyHandler();
	}
	
	public void initSolo(){
		for(int i=0;i<4;i++){
			keyPressedP1[i]=false;
		}
		Field sF = new SoloField();
		blocks = sF.getBlocks();
		bonus = sF.getBonus();
		brokenBricks = new ArrayList<Tile>();
		bombers = new ArrayList<Bomberman>();
		cheatActivated=false;
		cheat = new ArrayList<String>();
		handler = new EnemyHandler();
		spawner = new Spawner(handler,bonus,blocks);
	}
	
	//Getteurs
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
	
	/**
	 * @param b : bomberman auquel on doit envoyer la liste des touches activ�es
	 * @return renvoies la liste de bool�ens li� au bomberman en question
	 */
	public boolean[] giveKeys(Bomberman b){
		boolean[] keyPressed = new boolean[4];
		if(b.getId()==ID.Player1) keyPressed=keyPressedP1;
		if(b.getId()==ID.Player2) keyPressed=keyPressedP2;
		if(b.getId()==ID.Player3) keyPressed=keyPressedP3;
		if(b.getId()==ID.Player4) keyPressed=keyPressedP4;
		return keyPressed;
	}
	
	/**
	 * @return renvoies le mot form� par les lettres contenues dans la liste cheat
	 */
	private String makeWord(){
		String word = "";
		for (int i=0;i<cheat.size();i++){
			String letter = cheat.get(i);
			word+=letter;
		}
		return word;
	}

	/**
	 * @param mot : v�rifacation que ce param�tre corresponde � notre cheatCode 
	 */
	private void checkCheat(String mot){
		if (mot.equals(cheatCode)){
			for (int i = 0; i<bombers.size(); i++) getBomber(i).toCheat();
			cheatActivated=true;
			cheat.clear();
		}	
	}
	
	/**
	 * @param score : score obtenu pour la partie
	 * @param level : niveau atteint pour la partie
	 * Cette m�thode v�rifie si le score de la partie est sup�rieur au highscore sauv� dans 
	 * le fichier highscore.dat. Si c'est le cas, demande � l'utilisateur d'inscire son nom 
	 * dans une bo�te de dialogue. Son score devient ainsi le nouveau highscore
	 * Si toutefois le fichier highScore.dat n'existe pas, la m�thode cr�e un nouveau fichier
	 */
	private void checkScore(int score,int level){
		if(score > Integer.parseInt(highScore.split(":")[1])){
			String name = JOptionPane.showInputDialog("Nouveau record ! Entrez votre nom");
			highScore = name + ":" + score + ":" + level;
			
			File scoreFile = new File("res/highscore.dat");
			if(scoreFile.exists()){
				try {
					scoreFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			FileWriter writeFile = null;
			BufferedWriter writer =null;
			try{
				writeFile = new FileWriter(scoreFile);
				writer = new BufferedWriter(writeFile);
				writer.write(this.highScore);
			}
			catch(Exception e){}
			finally{
				try {
					if(writer != null) writer.close();
					} 
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * @return renvoies le highScore contenu dans le fichier highscore.dat
	 */
	public String getHighScore(){
		FileReader readFile = null;
		BufferedReader reader = null;
		try{
			readFile = new FileReader("res/highscore.dat");
			reader = new BufferedReader(readFile);
			return reader.readLine();
		}
		catch(Exception e){
			return"None:0:0";
		}
		finally{
			try {
				if(reader != null) reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * M�thode de mise � jour de tous les objets du jeu en fonction du GameState
	 * dans lequel on se trouve. S'occupe �galement des th�mes sonores en les relan�ant
	 * si n�cessaire.
	 * Cette m�thode est appel�e continuellement par la classe BombGame
	 */
	public void update(){

		if(BombGame.STATE==GameState.Solo){
			restartSoundIfOver(!cheatActivated ? "route" : "cheat");

			if(!cheatActivated){
				cheatTime++;
				checkCheat(makeWord());
				if(cheatTime%300==0){//d�lai maximal de 5 secondes pour entrer le cheatCode
					cheat.clear();
					cheatTime=0;
				}
			}
			
			if(player.isAlive()){
				if(player.isInvinsible()){
					invinTime++;
					if(invinTime%120==0){//2sec d'invinsibilit�
						player.setInvinsible(false);
						invinTime=0;
					}
				}
				player.update(blocks,bonus,handler,bombers,brokenBricks,giveKeys(player));
			}
			else{
				BombGame.STATE=GameState.SoloOver;
				BombGame.sound.soundEnd();
				BombGame.sound.play("gameOver");
				if(!cheatActivated) checkScore(player.getScore(),spawner.getLevel()); //Si le cheatcode est activ�, on ne peut pas modifier le highscore
			}
			spawner.update();
			handler.update(blocks,player,bonus);
		}
		
		
		if(BombGame.STATE==GameState.Multi){
			restartSoundIfOver("battle");

			invinTime++;
			for(int i=0;i<bombers.size();i++){
				Bomberman b = bombers.get(i);
				if(invinTime%120==0){//2sec d'invinsibilit�
					b.setInvinsible(false);
					invinTime=0;
				}
				if(b.isAlive()) b.update(blocks,bonus,handler,bombers,brokenBricks,giveKeys(b));
				else bombers.remove(b);
				
				if(bombers.size()==1){
					winner=bombers.getFirst();
					BombGame.STATE=GameState.MultiOver;
					BombGame.sound.soundEnd();
					BombGame.sound.play("victory");
				}
			}
		}

		else if(BombGame.STATE==GameState.Menu) restartSoundIfOver("menu");
		else if(BombGame.STATE==GameState.MultiOver) restartSoundIfOver("victory");
		else if(BombGame.STATE==GameState.SoloOver) restartSoundIfOver("gameOver");
		else if(BombGame.STATE==GameState.ChooseMulti) restartSoundIfOver("chooseMulti");
		else if(BombGame.STATE==GameState.ChooseSolo) restartSoundIfOver("chooseSolo");
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
