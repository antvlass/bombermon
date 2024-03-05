package org.bomb.model;

import java.util.ArrayList;
import java.util.Random;

public abstract class Enemy extends GameObject{

	protected int speed,lives;
	protected boolean invinsible;
	
	protected Random r = new Random();
	protected boolean reloop=false;
	protected boolean firstTurn = true;
	
	public Enemy(int x, int y, int width, int height,ID id) {
		super(x, y, width, height);
		this.id=id;
		dx=0;
		dy=0;
	}

	/**
	 * @param b : joueur de la partie solo
	 * Accorde un score dff�rent en fonction du type d'ennemi
	 */
	public void giveScore(Bomberman b){
		if(id==ID.BasicE) b.addScore(100);//R�partition du score
		if(id==ID.FastE) b.addScore(200);
		if(id==ID.SmartE) b.addScore(300);
		if(id==ID.Boss) b.addScore(500);
	}
	
	/**
     * @param blocks : murs et briques qui composent le terrain
	 * @param player : joueur du solo
	 * @param handler : ensemble des ennemis
	 * M�thode de mise � jour de l'ennemi g�rant son mouvement, ses collisions et pour le boss,
	 * le tir des projectiles
	 */
	public abstract void update(ArrayList<Tile> blocks,Bomberman player,EnemyHandler handler);
	
	public int getLives(){
		return lives;
	}
	public boolean isInvinsible(){
		return invinsible;
	}
	public void setInvinsible(boolean flag){
		invinsible=flag;
	}
	public void setDamage(int dmg){
		lives-=dmg;
	}
    public boolean isAlive(){
 	if(lives<1) 
 		return false;
 	else 
 		return true;
    }
	

	/**
	 * @param player : joueur du solo
	 * @param range : port�e
	 * @return renvoies true si le joueur se trouve � port�e
	 */
	protected boolean inRange(Bomberman player,int range){
		boolean inZone =  false;
		int px = player.getX();
		int py = player.getY();
		int dx = Math.abs(px-x);
		int dy = Math.abs(py-y);
		double distance = Math.sqrt((dx*dx)+(dy*dy));
		if(distance<=range)
			inZone=true;
		return inZone;
	}
	
	/**
     * @param blocks : murs et briques qui composent le terrain
	 * @return renvoies true s'il y'a collision avec un bloc de liste en param�tre
	 */
	protected boolean tileCollision(ArrayList<Tile> blocks){
		boolean collision=false;
		for(Tile t : blocks){
			if(Bomberman.checkCollision(this,t) && t.getId()!=ID.Door)
				collision=true;
		}
		return collision;
	}
	
	/**
	 * Choisis une direction al�atoirement et modifie son dx/dy en fonction de cette direction
	 */
	protected void randomDirection(){
		int rand = r.nextInt(4);
		if(rand==0){//right
			dx=speed;
			dy=0;
			dir=2;
		}
		if(rand==1){//left
			dx=-speed;
			dy=0;
			dir=1;
		}
		if(rand==2){//down
			dy=speed;
			dx=0;
			dir=4;
		}
		if(rand==3){//up
			dy=-speed;
			dx=0;
			dir=3;
		}
	}
}
