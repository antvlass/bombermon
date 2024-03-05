package org.bomb.view;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JPanel;

import org.bomb.control.GameController;
import org.bomb.BombGame;
import org.bomb.BombGame.GameState;
import org.bomb.model.Bomb;
import org.bomb.model.BombBlast;
import org.bomb.model.Bomberman;
import org.bomb.model.Bonus;
import org.bomb.model.Boss;
import org.bomb.model.Enemy;
import org.bomb.model.ID;
import org.bomb.model.Mine;
import org.bomb.model.Projectile;
import org.bomb.model.Tile;

public class GamePanel extends JPanel implements MouseListener{
	
	private static final long serialVersionUID = 1L;
	private GameController controller;
	private ArrayList<Tile> backG;
	
	private Texture tex;
	private Texture texRandom;
	
	//Attributs li�s au choix du pok�mon
	private int chooseCount1=0,chooseCount2=0;
	private Color[] rectangleColor1 = new Color[4];
	private Color[] rectangleColor2 = new Color[4];
	private String type1,type2;
	private int numberPlayers;

	//Son
	private Sound s = new Sound();
	
	public GamePanel(GameController controller){
		this.controller=controller;	
		this.addKeyListener(controller);
		this.addMouseListener(this);
		this.setFocusable(true);
	    this.setDoubleBuffered(true);
	    createBackG();
	    tex = new Texture();
	    for(int i=0;i<4;i++) rectangleColor1[i]=Color.white;
	    for(int i=0;i<4;i++) rectangleColor2[i]=Color.white;
	}
	
	//Renvoies une texture al�atoire
	private Texture randomLevelTexture(){
	    Random r = new Random();
	    int number = r.nextInt(4);
	    return new Texture(number);
	}
	
	/**
	 * @param type : type du pok�mon
	 * @return renvoies un nombre en fonction du type du pok�mon
	 */
	private int stringToInt(String type){
		int number=0;
		if(type=="E") number=1;
		if(type=="P") number=2;
		if(type=="V") number=3;
		if(type=="Cheat") number=4;
		return number;
	}
	
	//Cr�ation du fond d'�cran constitu� de cases
	private void createBackG(){
		backG = new ArrayList<Tile>();
		for(int j=2;j <= 16;j+=1){
			for(int i=0;i <=16;i+=1){
				Tile tempT = new Tile(i*50,j*50,50,50,ID.Wall);
				backG.add(tempT);
			}
		}
	}
	
	/**
	 * @param mx : position en x de la souris
	 * @param my : positio en y de la souris
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return renvoies true si la souris se trouve dans la r�gion delimit�e par (x,y,width,height)
	 */
	private boolean mouseOver(int mx, int my,int x,int y, int width, int height){
		boolean isOver = false;
		if((mx > x && mx < x+width) && (my > y && my < y+height))
			isOver=true;
		return isOver;
	}
	
	/**
	 * @param g
	 * @param e
	 * Dessine l'animation de l'ennemi en fonction de son ID
	 */
	private void enemyAnim(Graphics g,Enemy e){
		Animation[] anim = new Animation[4];
		if(e.getId()==ID.BasicE) anim = tex.animBE;
		if(e.getId()==ID.FastE) anim = tex.animFE;
		if(e.getId()==ID.SmartE) anim = tex.animSE;
		if(e.getId()==ID.Boss) anim = tex.animBoss;
		
		anim[0].runAnimation();anim[1].runAnimation();anim[2].runAnimation();anim[3].runAnimation();
		
		if(e.getDx()<0)
			anim[0].drawAnimation(g, e.getX(), e.getY(),e.getWidth(),e.getHeight());
		if(e.getDx()>0)
			anim[1].drawAnimation(g, e.getX(), e.getY(),e.getWidth(),e.getHeight());
		if(e.getDy()<0)
			anim[2].drawAnimation(g, e.getX(), e.getY(),e.getWidth(),e.getHeight());
		if(e.getDy()>0)
			anim[3].drawAnimation(g, e.getX(), e.getY(),e.getWidth(),e.getHeight());
	}
	
	/**
	 * @param g
	 * @param b
	 * Dessine le bomberman en fonction de son type et de son �volution ou si le cheat est activ�
	 */
	private void bomberAnim(Graphics g, Bomberman b){
		Animation[] anim = new Animation[4];
		BufferedImage[] images = new BufferedImage[24];
		int animPause = 0;
		
		if(b.getType().equals("F")){
			if(b.getEvol()==1) anim=tex.fire[0]; 
			else if(b.getEvol()==2){anim=tex.fire[1]; animPause=8;}
			else{anim=tex.fire[2];animPause=16;}
			images=tex.fireImg;
		}
		if(b.getType().equals("E")){
			if(b.getEvol()==1) anim=tex.water[0]; 
			else if(b.getEvol()==2){anim=tex.water[1]; animPause=8;}
			else{anim=tex.water[2];animPause=16;}
			images=tex.waterImg;
		}
		if(b.getType().equals("P")){
			if(b.getEvol()==1) anim=tex.grass[0]; 
			else if(b.getEvol()==2){anim=tex.grass[1]; animPause=8;}
			else{anim=tex.grass[2];animPause=16;}
			images=tex.grassImg;
		}
		if(b.getType().equals("V")){
			if(b.getEvol()==1) anim=tex.fly[0]; 
			else if(b.getEvol()==2){anim=tex.fly[1]; animPause=8;}
			else{anim=tex.fly[2];animPause=16;}
			images=tex.flyImg;
		}
		if(b.getType().equals("Cheat")){
			anim=tex.cheat;
			images=tex.cheatImg;
			animPause = 0;
		}
		
		anim[0].runAnimation();anim[1].runAnimation();anim[2].runAnimation();anim[3].runAnimation();

		if(b.getDir()==1){
			if(b.getDx()<0)
				anim[0].drawAnimation(g, b.getX(),b.getY(),b.getWidth(),b.getHeight());
			else
				g.drawImage(images[animPause], b.getX(),b.getY(),b.getWidth(),b.getHeight(),null);
		}
		else if(b.getDir()==2){
			if(b.getDx()>0)
				anim[1].drawAnimation(g, b.getX(),b.getY(),b.getWidth(),b.getHeight());
			else
				g.drawImage(images[2+animPause], b.getX(),b.getY(),b.getWidth(),b.getHeight(),null);
		}
		else if(b.getDir()==3){
			if(b.getDy()<0)
				anim[2].drawAnimation(g, b.getX(),b.getY(),b.getWidth(),b.getHeight());
			else
				g.drawImage(images[4+animPause], b.getX(),b.getY(),b.getWidth(),b.getHeight(),null);
		}
		else{
			if(b.getDy()>0)
				anim[3].drawAnimation(g, b.getX(),b.getY(),b.getWidth(),b.getHeight());
			else
				g.drawImage(images[6+animPause], b.getX(),b.getY(),b.getWidth(),b.getHeight(),null);
		}

	}

	/**
	 * @param g
	 * Dessine tous les objets du jeu
	 */
	private void drawObjects(Graphics g){
		//Statiques
		for(Tile bg : backG) 
			g.drawImage(texRandom.backG, bg.getX(), bg.getY(), bg.getWidth(), bg.getHeight(), null);
		for(int i=0;i<controller.getBrokenBricks().size();i++){
			Tile broB = controller.getBrokenBricks().get(i);
			g.drawImage(texRandom.broken, broB.getX(), broB.getY(), broB.getWidth(), broB.getHeight(), null);
		}
		for(int i = 0;i<controller.getBonus().size();i++){
			Bonus tempB = controller.getBonus().get(i);
			g.drawImage(tex.bonus[tempB.getNumber()-1], tempB.getX(), tempB.getY(), tempB.getWidth(), tempB.getHeight(), null);
		}		
		
		for(int i=0;i<controller.getBlocks().size();i++){
			Tile t = controller.getBlocks().get(i);
			if(t.getId()==ID.Wall) g.drawImage(texRandom.wall, t.getX(), t.getY(), t.getWidth(), t.getHeight(), null);
			else if(t.getId()==ID.Brick) g.drawImage(texRandom.brick, t.getX(), t.getY(), t.getWidth(), t.getHeight(), null);	
			else if(t.getId()==ID.Door) g.drawImage(texRandom.door, t.getX(), t.getY(), t.getWidth(), t.getHeight(), null);
		}

		//Mobiles
		for(int k=0;k<controller.getBombers().size();k++){
			Bomberman bomber = controller.getBomber(k);
			for(int i=0;i<bomber.getBombs().size();i++){
				Bomb bomb  = bomber.getBombs().get(i);
				g.drawImage(tex.bomb, bomb.getX(), bomb.getY(), bomb.getWidth(), bomb.getHeight(), null);	
				
				for(int j=0;j<bomb.getBlast().size();j++){
					BombBlast blast = bomb.getBlast().get(j);
					g.drawImage(tex.blast, blast.getX(), blast.getY(), blast.getWidth(), blast.getHeight(), null);
				}
			}
			for(int i=0;i<bomber.getMines().size();i++){
				Mine m = bomber.getMines().get(i);
				g.drawImage(tex.mine, m.getX(), m.getY(), m.getWidth(), m.getHeight(), null);
			}
			for(int i=0;i<bomber.getProjectiles().size();i++){
				Projectile p = bomber.getProjectiles().get(i);
				if(p.getId()==ID.FProjectile) g.drawImage(tex.projectiles[0], p.getX(), p.getY(), p.getWidth(), p.getHeight(), null);
				if(p.getId()==ID.EProjectile) g.drawImage(tex.projectiles[1], p.getX(), p.getY(), p.getWidth(), p.getHeight(), null);
				if(p.getId()==ID.PProjectile) g.drawImage(tex.projectiles[2], p.getX(), p.getY(), p.getWidth(), p.getHeight(), null);
				if(p.getId()==ID.VProjectile) g.drawImage(tex.projectiles[3], p.getX(), p.getY(), p.getWidth(), p.getHeight(), null);
				if(p.getId()==ID.CheatProjectile) g.drawImage(tex.projectiles[5], p.getX(), p.getY(), p.getWidth(), p.getHeight(), null);
			}
			bomberAnim(g,bomber);
		}

		for(int i=0;i<controller.getHandler().getEnemies().size();i++){
			Enemy e = controller.getHandler().getEnemies().get(i);
			if(e.getId()==ID.Boss){
				for(int j=0;j<((Boss) e).getProjectiles().size();j++){
					Projectile p = ((Boss) e).getProjectiles().get(j);
					g.drawImage(tex.projectiles[4], p.getX(), p.getY(), p.getWidth(), p.getHeight(), null);
				}
				for(int j=0;j<((Boss) e).getLives();j++){
					g.drawImage(tex.life, (e.getX()-30)+j*20, e.getY()-25, 50,50,null);
				}
			}
			enemyAnim(g,e);
		}
	}
	
	@Override
	public void paintComponent(Graphics g){
		//super.paintComponent(g);

		if(BombGame.STATE == GameState.Multi){
			if(numberPlayers==2){
				g.drawImage(texRandom.statusMulti2Img, 0, 0,null);
				g.setColor(Color.black);
				g.setFont(new Font("Calibri",Font.PLAIN,15));
				for(int i=0;i<controller.getBombers().size();i++){
					Bomberman b = controller.getBomber(i);
					if(b.getId()==ID.Player1){
						String player1 = b.getType();
						g.drawImage(tex.player[stringToInt(player1)], 320, 25, 50,50,null);
						g.drawString("x"+b.getLives(),78,56);
						g.drawString("x"+b.getMineCount(),148,56);
						g.drawString("x"+b.getMaxBombs(),212,56);
						g.drawString("x"+b.getBombRange(),274,56);
					}
					if(b.getId()==ID.Player2){
						String player2 = b.getType();				
						g.drawImage(tex.player[stringToInt(player2)], 495, 25, 50,50,null);
						g.drawString("x"+b.getLives(),590,56);
						g.drawString("x"+b.getMineCount(),660,56);
						g.drawString("x"+b.getMaxBombs(),725,56);
						g.drawString("x"+b.getBombRange(),785,56);
					}
				}
	
			}
			
			if(numberPlayers==3){
				g.drawImage(texRandom.statusMulti3Img, 0, 0,null);
				g.setColor(Color.black);
				g.setFont(new Font("Calibri",Font.PLAIN,12));
				for(int i=0;i<controller.getBombers().size();i++){
					Bomberman b = controller.getBomber(i);
					if(b.getId()==ID.Player1){
						g.drawString("x"+b.getLives(),165,38);
						g.drawString("x"+b.getMaxBombs(),215,38);
						g.drawString("x"+b.getBombRange(),165,72);
						g.drawString("x"+b.getMineCount(),215,72);
					}
					if(b.getId()==ID.Player2){
						g.drawString("x"+b.getLives(),462,38);
						g.drawString("x"+b.getMaxBombs(),512,40);
						g.drawString("x"+b.getBombRange(),462,75);
						g.drawString("x"+b.getMineCount(),512,75);
					}
					if(b.getId()==ID.Player3){
						g.drawString("x"+b.getLives(),765,38);
						g.drawString("x"+b.getMaxBombs(),816,40);
						g.drawString("x"+b.getBombRange(),765,75);
						g.drawString("x"+b.getMineCount(),815,75);
					}
				}
			}
			
			if(numberPlayers==4){
				g.drawImage(texRandom.statusMulti4Img, 0, 0,null);
				g.setColor(Color.black);
				g.setFont(new Font("Calibri",Font.PLAIN,15));
				for(int i=0;i<controller.getBombers().size();i++){
					Bomberman b = controller.getBomber(i);
					if(b.getId()==ID.Player1) g.drawString("x"+b.getLives(),125,58);
					if(b.getId()==ID.Player2) g.drawString("x"+b.getLives(),350,58);
					if(b.getId()==ID.Player3) g.drawString("x"+b.getLives(),575,58);
					if(b.getId()==ID.Player4) g.drawString("x"+b.getLives(),805,58);
				}
			}
			
			//Objets
			drawObjects(g);
		}
	
		if(BombGame.STATE == GameState.Solo){
			g.drawImage(texRandom.statusSoloImg, 0, 0,null);
			String player = controller.getPlayer().getType();
			g.drawImage(tex.player[stringToInt(player)], 40, 25, 50,50,null);
			g.setColor(Color.black);
			g.setFont(new Font("Calibri",Font.PLAIN,15));
			g.drawString("x"+controller.getPlayer().getLives(),135,58);
			g.drawString("x"+controller.getPlayer().getMineCount(),205,58);
			g.drawString("x"+controller.getPlayer().getMaxBombs(),270,58);
			g.drawString("x"+controller.getPlayer().getBombRange(),330,58);
			g.drawString("x"+controller.getPlayer().getProjectileCount(),395,58);
			g.setFont(new Font("Calibri",Font.PLAIN,40));
			g.drawString(controller.getSpawner().getLevel()+"",570,65);
			g.setFont(new Font("Calibri",Font.PLAIN,30));
			g.drawString(controller.getPlayer().getScore()+"",750,63);
			
			//Objets
			drawObjects(g);

		}
		
		if(BombGame.STATE == GameState.Pause){
			g.drawImage(tex.pauseImg, 0,0,850 ,850,null);
		}
		
		if(BombGame.STATE == GameState.Menu){
			g.drawImage(tex.menuImg, 0, 0,850 ,850,null);
		}
		
		if(BombGame.STATE == GameState.Help){
			g.drawImage(tex.helpImg, 0, 0,850 ,850,null);
		}
		
		if(BombGame.STATE == GameState.Items){
			g.drawImage(tex.itemsImg, 0, 0,850 ,850,null);
		}
		
		if(BombGame.STATE == GameState.Controls){
			g.drawImage(tex.controlsImg, 0, 0,850 ,850,null);
		}
			
		if(BombGame.STATE == GameState.MultiOver){
			g.drawImage(tex.gameOverImg, 0, 0,850 ,850,null);
			String winner=null;
			if(controller.getWinner()!=null){
				winner = controller.getWinner().getType();
				g.drawImage(tex.player[stringToInt(winner)], 320, 260, 200,200,null);
			}
			else{
				g.setFont(new Font("Calibri",Font.PLAIN,100)); 
				g.drawString("�galit� !",280,400); //Si les 2 derniers joueurs perdent leur vie en m�me temps il y'a �galit�
				g.setColor(Color.white);
				g.fillRect(320, 520, 200, 100);
			}
		}
		
		if(BombGame.STATE == GameState.SoloOver){
			g.drawImage(tex.soloOverImg, 0, 0,850 ,850,null);
			g.setColor(Color.black);
			g.setFont(new Font("Calibri",Font.PLAIN,35));
			g.drawString(controller.getSpawner().getLevel()+"",700,615);
			g.drawString(controller.getHighScore().split(":")[0],330 ,268);
			g.drawString(controller.getHighScore().split(":")[2],310,410);
			g.setFont(new Font("Calibri",Font.PLAIN,30));
			g.drawString(controller.getPlayer().getScore()+"",300,614);
			g.drawString(controller.getHighScore().split(":")[1],310,345);	
		}
		
		
		if(BombGame.STATE == GameState.ChooseNumber){
			g.drawImage(tex.chooseNumber, 0, 0,850 ,850,null);
		}
		
		if(BombGame.STATE == GameState.ChooseMulti){
			g.drawImage(tex.chooseMultiImg, 0, 0,850 ,850,null);
		
			g.setColor(rectangleColor1[0]);
			g.drawRect(125, 218, 90, 90); //F
			g.setColor(rectangleColor1[1]);
			g.drawRect(315, 220, 90, 90); //E
			g.setColor(rectangleColor1[2]);
			g.drawRect(500, 225, 90, 90); //P
			g.setColor(rectangleColor1[3]);
			g.drawRect(665, 218, 90, 90); //V
			
			g.setColor(rectangleColor2[0]);
			g.drawRect(125, 425, 90, 90); //F
			g.setColor(rectangleColor2[1]);
			g.drawRect(315, 425, 90, 90); //E
			g.setColor(rectangleColor2[2]);
			g.drawRect(495, 425, 90, 90);  //P
			g.setColor(rectangleColor2[3]);
			g.drawRect(665, 425, 90, 90);  //V
			
			
		}
		
		if(BombGame.STATE == GameState.ChooseSolo){
			g.drawImage(tex.chooseSoloImg, 0, 0,850 ,850,null);
			g.setColor(rectangleColor1[0]);
			g.drawRect(120, 420, 90, 90); 
			g.setColor(rectangleColor1[1]);
			g.drawRect(290, 420, 90, 90);
			g.setColor(rectangleColor1[2]);
			g.drawRect(465, 420, 90, 90);
			g.setColor(rectangleColor1[3]);
			g.drawRect(640, 420, 90, 90);
			g.setColor(Color.black);
			g.setFont(new Font("Calibri",Font.PLAIN,35));
			g.drawString(controller.getHighScore().split(":")[0],460 ,210);
			g.drawString(controller.getHighScore().split(":")[2],435,345);
			g.setFont(new Font("Calibri",Font.PLAIN,30));
			g.drawString(controller.getHighScore().split(":")[1],435,285);
		}
		
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		int mx = e.getX();
		int my = e.getY();
		//////////////MENU////////////// 
		if(BombGame.STATE==GameState.Menu){
			if(mouseOver(mx,my,300, 320, 250, 95)){
				s.playSound("click");
				BombGame.STATE=GameState.ChooseSolo;
				BombGame.sound.soundEnd();
				BombGame.sound.play("chooseSolo");
			}
			if(mouseOver(mx,my,300, 440, 250, 95)){
				s.playSound("click");
				BombGame.STATE=GameState.ChooseNumber;
			}
			if(mouseOver(mx,my,300, 570, 250, 95)){
				s.playSound("click");
				BombGame.STATE=GameState.Help;
			}
			if(mouseOver(mx,my,300, 710, 250, 95)){
				System.exit(0);
			}
		}
		//////////////HELP//////////////
		if(BombGame.STATE==GameState.Help || BombGame.STATE==GameState.Controls || BombGame.STATE==GameState.Items || BombGame.STATE==GameState.ChooseNumber){
			if(mouseOver(mx,my,300, 730, 250, 95)){
				s.playSound("click");
				BombGame.STATE=GameState.Menu;
			}
		}
		if(BombGame.STATE==GameState.Help){
			if(mouseOver(mx,my,130, 590, 180, 60)){
				s.playSound("click");
				BombGame.STATE=GameState.Items;
			}
			if(mouseOver(mx,my,560, 590, 180, 50)){
				s.playSound("click");
				BombGame.STATE=GameState.Controls;
			}
		}
		if(BombGame.STATE==GameState.ChooseMulti || BombGame.STATE==GameState.ChooseSolo){
			if(mouseOver(mx,my,300, 730, 250, 95)){
				s.playSound("click");
				BombGame.STATE=GameState.Menu;
				BombGame.sound.soundEnd();
				BombGame.sound.play("menu");
				for(int i=0;i<4;i++) rectangleColor1[i]=Color.white;
			    for(int i=0;i<4;i++) rectangleColor2[i]=Color.white;
			    chooseCount1=0;chooseCount2=0;
			}
		}
		
		//////////////GAMEOVER//////////////
		if(BombGame.STATE==GameState.MultiOver){
			if(mouseOver(mx,my,300, 730, 250, 95)){
				s.playSound("click");
				BombGame.STATE=GameState.Menu;
				BombGame.sound.soundEnd();
				BombGame.sound.play("menu");
			}
		}
		
		if(BombGame.STATE==GameState.SoloOver){
			if(mouseOver(mx,my,70, 715, 250, 95)){
				s.playSound("click");
				BombGame.STATE=GameState.Menu;
				BombGame.sound.soundEnd();
				BombGame.sound.play("menu");
			}
			if(mouseOver(mx,my,520, 715, 250, 95)){
				texRandom = randomLevelTexture();
				controller.initSolo();
				controller.getBombers().add(new Bomberman(8*50,15*50,50,50,ID.Player1,type1,1));
				controller.setPlayer(controller.getBombers().get(0));
				BombGame.STATE = GameState.Solo;
				BombGame.sound.soundEnd();
				BombGame.sound.play("route");
			}
		}
		
		if(BombGame.STATE==GameState.ChooseNumber){
			if(mouseOver(mx,my,385,245,75,50)){
				s.playSound("click");
				BombGame.sound.soundEnd();
				BombGame.sound.play("chooseMulti");
				numberPlayers=2;
				BombGame.STATE=GameState.ChooseMulti;
			}
			if(mouseOver(mx,my,385,380,75,50)){ 
				texRandom = randomLevelTexture();
				controller.initMulti();
				controller.getBombers().add(new Bomberman(50,150,50,50,ID.Player1,"F",1));
				controller.getBombers().add(new Bomberman(15*50,150,50,50,ID.Player2,"E",1));
				controller.getBombers().add(new Bomberman(50,15*50,50,50,ID.Player3,"P",1));
				numberPlayers=3;
				BombGame.STATE = GameState.Multi;
				BombGame.sound.soundEnd();
				BombGame.sound.play("battle");
			}
			if(mouseOver(mx,my,385,520,75,50)){ 
				texRandom = randomLevelTexture();
				controller.initMulti();
				controller.getBombers().add(new Bomberman(50,150,50,50,ID.Player1,"F",1));
				controller.getBombers().add(new Bomberman(15*50,150,50,50,ID.Player2,"E",1));
				controller.getBombers().add(new Bomberman(50,15*50,50,50,ID.Player3,"P",1));
				controller.getBombers().add(new Bomberman(15*50,15*50,50,50,ID.Player4,"V",1));
				numberPlayers=4;
				BombGame.STATE = GameState.Multi;
				BombGame.sound.soundEnd();
				BombGame.sound.play("battle");
			}
		}
		
		
		
		//////////////CHOIX JOUEUR Versus//////////////
		if(BombGame.STATE==GameState.ChooseMulti){
			if(mouseOver(mx,my,125, 218, 90, 90)){
				rectangleColor1[1]=Color.white;rectangleColor1[2]=Color.white;rectangleColor1[3]=Color.white;
				rectangleColor1[0]=Color.red;
				chooseCount1=1;
				type1 = "F";
			}
			if(mouseOver(mx,my,315, 220, 90, 90)){
				rectangleColor1[0]=Color.white;rectangleColor1[2]=Color.white;rectangleColor1[3]=Color.white;
				rectangleColor1[1]=Color.blue;
				chooseCount1=1;
				type1 = "E";
			}
			if(mouseOver(mx,my,500, 225, 90, 90)){
				rectangleColor1[0]=Color.white;rectangleColor1[1]=Color.white;rectangleColor1[3]=Color.white;
				rectangleColor1[2]=Color.green;
				chooseCount1=1;
				type1 = "P";
			}
			if(mouseOver(mx,my,665, 218, 90, 90)){
				rectangleColor1[0]=Color.white;rectangleColor1[1]=Color.white;rectangleColor1[2]=Color.white;
				rectangleColor1[3]=Color.cyan;
				chooseCount1=1;
				type1 = "V";
			}
			if(mouseOver(mx,my,125, 425, 90, 90)){
				rectangleColor2[1]=Color.white;rectangleColor2[2]=Color.white;rectangleColor2[3]=Color.white;
				rectangleColor2[0]=Color.red;
				chooseCount2=1;
				type2 = "F";
			}
			if(mouseOver(mx,my,315, 425, 90, 90)){
				rectangleColor2[0]=Color.white;rectangleColor2[2]=Color.white;rectangleColor2[3]=Color.white;
				rectangleColor2[1]=Color.blue;
				chooseCount2=1;
				type2 = "E";
			}
			if(mouseOver(mx,my,495, 425, 90, 90)){
				rectangleColor2[1]=Color.white;rectangleColor2[0]=Color.white;rectangleColor2[3]=Color.white;
				rectangleColor2[2]=Color.green;
				chooseCount2=1;
				type2 = "P";
			}
			if(mouseOver(mx,my,665, 425, 90, 90)){
				rectangleColor2[1]=Color.white;rectangleColor2[0]=Color.white;rectangleColor2[2]=Color.white;
				rectangleColor2[3]=Color.cyan;
				chooseCount2=1;
				type2 = "V";
			}
			
			if(mouseOver(mx,my,300, 560, 250, 75) && (chooseCount1 + chooseCount2 == 2)){ 
				texRandom = randomLevelTexture();
				controller.initMulti();
				controller.getBombers().add(new Bomberman(50,150,50,50,ID.Player1,type1,1));
				controller.getBombers().add(new Bomberman(15*50,15*50,50,50,ID.Player2,type2,1));
				BombGame.STATE = GameState.Multi;
				BombGame.sound.soundEnd();
				BombGame.sound.play("battle");
			}
		}
		
		//////////////CHOIX JOUEUR SOLO//////////////
		if(BombGame.STATE==GameState.ChooseSolo){
			if(mouseOver(mx,my,120, 420, 90, 90)){
				rectangleColor1[1]=Color.white;rectangleColor1[2]=Color.white;rectangleColor1[3]=Color.white;
				rectangleColor1[0]=Color.red;
				chooseCount1=1;
				type1 = "F";
			}
			if(mouseOver(mx,my,290, 420, 90, 90)){
				rectangleColor1[0]=Color.white;rectangleColor1[2]=Color.white;rectangleColor1[3]=Color.white;
				rectangleColor1[1]=Color.blue;
				chooseCount1=1;
				type1 = "E";
			}
			if(mouseOver(mx,my,465, 420, 90, 90)){
				rectangleColor1[0]=Color.white;rectangleColor1[1]=Color.white;rectangleColor1[3]=Color.white;
				rectangleColor1[2]=Color.green;
				chooseCount1=1;
				type1 = "P";
			}
			if(mouseOver(mx,my,640, 420, 90, 90)){
				rectangleColor1[0]=Color.white;rectangleColor1[1]=Color.white;rectangleColor1[2]=Color.white;
				rectangleColor1[3]=Color.cyan;
				chooseCount1=1;
				type1 = "V";
			}
			
			if(mouseOver(mx,my,300, 560, 250, 75) && chooseCount1 == 1){ 
				texRandom = randomLevelTexture();
				controller.initSolo();
				controller.getBombers().add(new Bomberman(8*50,15*50,50,50,ID.Player1,type1,1));
				controller.setPlayer(controller.getBombers().get(0));
				BombGame.STATE = GameState.Solo;
				BombGame.sound.soundEnd();
				BombGame.sound.play("route");
			}
		}
	}


	@Override
	public void mouseClicked(MouseEvent arg0) {	
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
	}


}
