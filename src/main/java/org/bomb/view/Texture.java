package org.bomb.view;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Texture {

	///IMAGES
	
	//Joueur
	public BufferedImage[] fireImg = new BufferedImage[24];
	public BufferedImage[] waterImg = new BufferedImage[24];
	public BufferedImage[] grassImg = new BufferedImage[24];
	public BufferedImage[] flyImg = new BufferedImage[24];
	public BufferedImage[] cheatImg = new BufferedImage[8];
	
	public Animation[][] fire = new Animation[3][4]; //Chaque pok�mon a 3 �volutions et chaque evol a 4 animations (left,right,up,down)
	public Animation[][] water = new Animation[3][4]; 
	public Animation[][] grass = new Animation[3][4]; 
	public Animation[][] fly = new Animation[3][4]; 

	public Animation[] cheat = new Animation[4]; 


	//Ennemis
	public Animation[] animBE = new Animation[4]; 
	public Animation[] animFE = new Animation[4]; 
	public Animation[] animSE = new Animation[4]; 
	public Animation[] animBoss = new Animation[4]; 
	
	
	//Objets
	public BufferedImage wall;
	public BufferedImage brick;
	public BufferedImage broken;
	public BufferedImage mine;
	public BufferedImage blast;
	public BufferedImage door;
	public BufferedImage bomb;
	public BufferedImage life;
	public BufferedImage backG;
	public BufferedImage[] projectiles = new BufferedImage[6]; //0=feu;1=eau;2=plante;3=vol;4=boss
	public BufferedImage[] bonus = new BufferedImage[9];
	
	//Interface
	public BufferedImage player[] = new BufferedImage[5];
	public BufferedImage menuImg;
	public BufferedImage helpImg;
	public BufferedImage itemsImg;
	public BufferedImage controlsImg;
	public BufferedImage statusMulti2Img; 
	public BufferedImage statusMulti3Img; 
	public BufferedImage statusMulti4Img; 
	public BufferedImage statusSoloImg; 
	public BufferedImage pauseImg;
	public BufferedImage gameOverImg;
	public BufferedImage soloOverImg;
	public BufferedImage chooseMultiImg;
	public BufferedImage chooseSoloImg;
	public BufferedImage chooseNumber;
	
	

	public Texture(){//1er contrustructeur pour l'interface (ne varie jamais lors du jeu)
		menuImg = loadImg("/Menu/menu.png");
		helpImg = loadImg("/Menu/aide.png");
		itemsImg = loadImg("/Menu/items.png");
		pauseImg = loadImg("/Menu/pause.png");
		controlsImg = loadImg("/Menu/touches.png");
		chooseMultiImg = loadImg("/Menu/chooseMulti.png");
		chooseSoloImg = loadImg("/Menu/chooseSolo.png");
		chooseNumber = loadImg("/Menu/nombre.png");
		gameOverImg = loadImg("/Menu/gameOver.png");
		soloOverImg = loadImg("/Menu/soloOver.png");
		for(int i=1;i<=5;i++) player[i-1] = loadImg("/Menu/player"+i+".png");
		blast = loadImg("/Objets/explosion.png");
		bomb = loadImg("/Objets/bomb.png");
		mine = loadImg("/Objets/mine.png");
		life = loadImg("/Objets/life.png");
		
		for(int i=1; i<=9;i++) bonus[i-1] = loadImg("/Objets/Bonus/bonus"+i+".png");
		for(int i=1; i<=6;i++) projectiles[i-1] = loadImg("/Objets/Projectiles/projectile"+i+".png");
		
		fireImg = fillImgs("Player","F",3,24);
		waterImg = fillImgs("Player","E",3,24);
		grassImg = fillImgs("Player","P",3,24);
		flyImg = fillImgs("Player","V",3,24);
		cheatImg = fillImgs("Player","Cheat",1,8);
		
		for(int i=0;i<3;i++){
			fire[i] = createAnim(fireImg,i*8);
			water[i] = createAnim(waterImg,i*8);
			grass[i] = createAnim(grassImg,i*8);
			fly[i] = createAnim(flyImg,i*8);
		}
		
		
		cheat = createAnim(cheatImg,0);
		animBE = createAnim(fillImgs("Ennemi","BasicE",1,8),0);
		animFE = createAnim(fillImgs("Ennemi","FastE",1,8),0);
		animSE = createAnim(fillImgs("Ennemi","SmartE",1,8),0);
		animBoss = createAnim(fillImgs("Ennemi","Boss",1,8),0);
	}
	
	
	public Texture(int number){//2eme constructeur pour les objets (peut varier en fonction de la partie : aleatoire)
		String name = "";
		if(number==0) name="Forest";
		if(number==1)  name = "Desert";
		if (number==2) name = "Snow";
		if (number==3) name = "Cave";

		wall = loadImg("/Objets/"+name+"/wall.png");
		brick = loadImg("/Objets/"+name+"/brick.png");
		broken = loadImg("/Objets/"+name+"/broken.png");
		backG = loadImg("/Objets/"+name+"/BG.png");
		door = loadImg("/Objets/"+name+"/door.png");
		statusMulti2Img = loadImg("/Menu/Status/"+name+"Multi.png");
		statusMulti3Img = loadImg("/Menu/Status/"+name+"Multi3.png");
		statusMulti4Img = loadImg("/Menu/Status/"+name+"Multi4.png");
		statusSoloImg = loadImg("/Menu/Status/"+name+"Solo.png");
	}
	
	
	private BufferedImage[] fillImgs(String folder,String name,int evol,int length){
		BufferedImage[] images = new BufferedImage[length];
		for(int i=1;i<=evol;i++){
			for(int j=1; j<=2;j++){
				images[j-1+(i-1)*8]=loadImg("/"+folder+"/"+name+i+"left"+j+".png");
				images[j+1+(i-1)*8]=loadImg("/"+folder+"/"+name+i+"right"+j+".png");
				images[j+3+(i-1)*8]=loadImg("/"+folder+"/"+name+i+"up"+j+".png");
				images[j+5+(i-1)*8]=loadImg("/"+folder+"/"+name+i+"down"+j+".png");
			}	
		}
		return images;
	}
	
	private Animation[] createAnim(BufferedImage[] images, int startValue){
		Animation[] anim = new Animation[4];
		anim[0] = new Animation(5,images[0+startValue],images[1+startValue]);
		anim[1] = new Animation(5,images[2+startValue],images[3+startValue]);
		anim[2] = new Animation(5,images[4+startValue],images[5+startValue]);
		anim[3] = new Animation(5,images[6+startValue],images[7+startValue]);
		return anim;
	}
	
	private BufferedImage loadImg(String name){
		BufferedImage image=null;
		try {
			image = ImageIO.read(getClass().getResourceAsStream(name));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
	
}
	


