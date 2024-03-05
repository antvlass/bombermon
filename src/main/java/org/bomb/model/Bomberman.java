package org.bomb.model;
import java.awt.Rectangle;
import java.util.ArrayList;

import org.bomb.BombGame;
import org.bomb.view.Sound;


public class Bomberman extends GameObject{
	
	private int speed=2;
	
	//Objets
	private ArrayList<Bomb> bombs;
	private ArrayList<Mine> mines;
	private ArrayList<Projectile> projectiles;
	
	private String type; //Attribut li� au choix du pok�mon

	//Param�tres li�s au bonus
	private String name;
	private Integer evol; //stade d'�voltion
	private boolean maxEvol; //true si stade d'�volution maximal atteint (3)
	private int lives=3;
	private int bombRange=1;
	private int maxBombs = 1;
	private boolean invinsible=false;
	private int mineCount=0;
	private int projectileCount=5;
	private int minRange = 1; //Valeur minimale de port�e des bombes
	private int minBombs = 1; //Valeur minimale du nombre de bombes que le joueur peut poser simultan�ment
	private int projectileRange=3;
	
	//Param�tres solo
	private int score = 0;
	private boolean cheatActivated=false;
	
	
	public Bomberman(int x,int y,int width,int height,ID id,String type,Integer evol){
		super(x,y,width,height);
		this.id=id;
		this.type=type;
		this.evol=evol;
		name=type+evol.toString();
		maxEvol=false;
	    bombs= new ArrayList<Bomb>();
	    mines= new ArrayList<Mine>();
	    projectiles= new ArrayList<Projectile>(); 
	    dir=4;
	}
	
	/**
	 * Actions � effectuer dans le cas o� le cheatCode est activ�
	 */
	public void toCheat(){
		BombGame.sound.soundEnd();
		BombGame.sound.play("cheat");
		lives=10;
		projectileCount=35;
		mineCount=10;
		maxBombs=5;
		bombRange=7;
		projectileRange=7;
		speed=5;
		type= "Cheat";
		cheatActivated=true;
	}

	public void addMine(int number){
		this.mineCount+=number;
	}
	public void addProjectile(int number){
		this.projectileCount+=number;
	}
	public void addScore(int score){
		this.score+=score;
	}
    public boolean isAlive(){
 	if(lives<1) 
 		return false;
 	else 
 		return true;
    }
    
	public void addRange(int range){
		if(bombRange+range <= minRange)
			bombRange=minRange;
		else
			bombRange+=range;	
	}
	public void addMore(int more){
		if(maxBombs+more <= minBombs)
			maxBombs=minBombs;
		else if(maxBombs+more <= 5) //Nombre de bombes simultan�es limit� � 5
			maxBombs+=more;	
	}
	
	/**
	 * Actions � effectuer si le joueur prend un bonus d'�volution
	 * D�pend de son stade d'�volution
	 */
	public void evolution(){
		if(!cheatActivated){
			if(evol==1){
				evol=2;
				minRange=2;
				bombRange++;
				minBombs=2;
				maxBombs++;
				projectileCount+=2;
			}
			else if(evol==2){
				evol=3;
				minRange=3;
				bombRange++;
				minBombs=3;
				maxBombs++;
				projectileCount+=2;
			}
			name=type+evol.toString();
		}
	}
	
	
    /**
     * @param b : bonus que le joueur a pris
     * Joue le son correspondant au bonus pris par le joueur
     */
    private void playBonusEffect(Bonus b){
    	Sound s = new Sound();
    	if(b.getId()==ID.Bonus || b.getId()==ID.Projectile)
    		s.playSound("bonus");
    	if(b.getId()==ID.Malus)
    		s.playSound("malus");
    	if(b.getId()==ID.Evolution && !cheatActivated && !maxEvol)
    		s.playSound("evolution");	
    }
    
    /**
     * @param p : projectile tir� par le joueur
     * Joue le son correspondant au type du joueur lors d'un tir de projectile
     */
    private void playProjectileEffect(Projectile p){
    	Sound s = new Sound();
    	if(p.getId()==ID.FProjectile)
    		s.playSound("fire");
    	if(p.getId()==ID.EProjectile)
    		s.playSound("bubble");
    	if(p.getId()==ID.PProjectile)
    		s.playSound("grass");	
    	if(p.getId()==ID.VProjectile)
    		s.playSound("fly");	
    	if(p.getId()==ID.CheatProjectile)
    		s.playSound("psy");	
    }
 
    /**
     * @param val : valeur � arrondir au multiple de 50 (hauteur et largeur d'un objet) le plus proche
     * @return renvoies le multiple de 50 le plus proche de val
     * Oblige le joueur � poser les bombes et les mines sur des cases
     */
    private int inCase(int val){
    	return (int) 50*(val/50);
    }
	
	/**
	 * @param object1
	 * @param object2
	 * @return renvoie true si l'object 1 et l'object 2 sont en collision
	 */
	public static boolean checkCollision(GameObject object1, GameObject object2){
		Rectangle o1 = object1.getBounds();
		Rectangle o2 = object2.getBounds();
		if(o1.intersects(o2))
			return true;
		else 
			return false;
	}

	public void dropBomb(){
    	Bomb bomb = new Bomb(inCase(x),inCase(y),50,50,this,bombRange);
    	if(bombs.size() < maxBombs){
    		bombs.add(bomb);
    	}
	}
	
	public void dropMine(){
		Mine mine = new Mine(inCase(x),inCase(y),50,50,this);
		if(mineCount > 0){
			mines.add(mine);
			mineCount--;
		}
	}
	
	public void shoot(){
		Projectile p = new Projectile(inCase(x),inCase(y),50,50,dir,type,this,projectileRange,10);
		if(projectileCount > 0){
			projectiles.add(p);
			playProjectileEffect(p);
			projectileCount--;
		}
	}

	
    /**
     * @param blocks : murs et briques qui composent le terrain
     * @param bonus : ensemble des bonus disponbiles sur le terrain
     * @param handler : ensemble des ennemis
     * @param bombers : les bombermans pr�sents sur le terrain
     * @param brokenBricks : briques qui ont �t�es d�truites par les explosions
     * @param keyPressed : liste de bool�ens correpondant aux entr�es clavier de l'utilisateur
     * M�thode de mise � jour du joueur. Contr�le son mouvement et les interactions avec les objets voisins
     * Appelle �galement les m�thodes de mise � jour des objets en lien avec celui-ci (bombes,mines,projectiles)
     */
    public void update(ArrayList<Tile> blocks, ArrayList<Bonus> bonus,EnemyHandler handler,ArrayList<Bomberman> bombers,ArrayList<Tile> brokenBricks,boolean[] keyPressed) {
    	//Mouvement
    	move(blocks,bombers,keyPressed);
    	
    	//Objets
    	for(int i=0;i<bombs.size();i++){
    		bombs.get(i).update(blocks, bombers, brokenBricks, handler);
    		if(bombs.get(i).toRemove())
    			bombs.remove(bombs.get(i));
    	}
    	
    	for(int i=0;i<projectiles.size();i++){
    		projectiles.get(i).update(blocks,handler,this);
    		if(projectiles.get(i).toRemove())
    			projectiles.remove(i);
    	}
    	
    	for(int i=0;i<mines.size();i++){
    		mines.get(i).update(bombers, handler);
    		if(mines.get(i).toRemove())
    			mines.remove(i);
    	}
    	
    	//Collisions interaction
    	for(int i = 0; i<bonus.size();i++){
    		Bonus tempB = bonus.get(i);
    		if(checkCollision(this,tempB)){
    			bonus.remove(tempB);
    			tempB.getEffect(this);
    			playBonusEffect(tempB);
    			if(evol==3) maxEvol=true;
    		}
    	}
    	for(int i=0;i<handler.getEnemies().size();i++){
    		Enemy e = handler.getEnemies().get(i);
    		if(checkCollision(this,e) && !this.isInvinsible()){
    			this.setDamage(1);
				this.setInvinsible(true); 
				Sound s = new Sound();
				s.playSound("hit");
    		}
    	}
    }
  
    /**
     * @param blocks : murs et briques qui composent le terrain
     * @param bombers : les bombermans pr�sents sur le terrain
     * @param keyPressed : liste de bool�ens correpondant aux entr�es clavier de l'utilisateur
     * Le mouvement se fait case par case, c'est-�-dire que l'utilisateur ne peut modifier le mouvement 
     * de son bomberman que si celui se trouve sur un case multiple de 50
     */
    private void move(ArrayList<Tile> blocks, ArrayList<Bomberman> bombers, boolean[] keyPressed){
    	if(x%50==0 && y%50==0){
    		if(keyPressed[0]){
    			dx=-speed;
    			dy=0;
    			dir=1;
    		}
    		else if(keyPressed[1]){
    			dx=speed;
    			dy=0;
    			dir=2;
    		}
    		else if(keyPressed[2]){
    			dx=0;
    			dy=-speed;
    			dir=3;
    		}
    		else if(keyPressed[3]){
    			dx=0;
    			dy=speed;
    			dir=4;
    		}
    		else {
    			dx=0;
    			dy=0;
    		}
    		y+=dy;
        	x+=dx;
    	}
    	else{
	    	y+=dy;
	    	x+=dx;
    	}
    	
    	//Collisions changement vitesse
    	for(Tile t : blocks){
    		if(checkCollision(this,t)){
    			x-=dx;
    			y-=dy;
    		}
    	}
    	for(Bomberman b : bombers){
	    	for(Bomb bomb : b.getBombs()){
	    		if(checkCollision(this,bomb) && bomb.getOwner()!=this){//Les bombes ne sont pas obstacles pour son possesseur 
	    			x-=dx;
	    			y-=dy;
	    		}
	    	}
    	} 
    }

	public int getScore(){
		return score;
	}
	public boolean isInvinsible(){
		return invinsible;
	}
	public void setInvinsible(boolean flag){
		this.invinsible=flag;
	}
	public int getLives(){
		return lives;
	}
	public void setDamage(int dmg){
		this.lives-=dmg;
	}
    public ArrayList<Bomb> getBombs(){
    	return bombs;
    }
    public ArrayList<Mine> getMines(){
    	return mines;
    }
    public ArrayList<Projectile> getProjectiles(){
    	return projectiles;
    }
	public void setSpeed(int speed){
		this.speed=speed;
	}
	public int getMineCount(){
		return mineCount;
	}
	public int getProjectileCount(){
		return projectileCount;
	}
	public int getBombRange(){
		return bombRange;
	}
	public int getMaxBombs(){
		return maxBombs;
	}
	public String getName(){
		return name;
	}
	public String getType(){
		return type;
	}
	public int getEvol(){
		return evol;
	}
}




