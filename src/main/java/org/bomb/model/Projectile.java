package org.bomb.model;

import java.util.ArrayList;

import org.bomb.view.Sound;

public class Projectile extends GameObject {

	private int range,speed;
	private final int xOrigin,yOrigin;
	private String type;
	private GameObject owner;


	public Projectile(int x, int y, int width, int height,int dir,String type,GameObject owner,int range,int speed) {
		super(x, y, width, height);
		xOrigin = x;
		yOrigin = y;
		this.dir=dir;
		this.type=type;
		this.owner=owner;
		this.speed=speed;
		toRemove=false;
		this.range=50*range; //range en nbr de cases
		giveId();
	}
	
	public void update(ArrayList<Tile> blocks,EnemyHandler handler,Bomberman player){
		if(dir==1){
			x+=-speed;
		}
		if(dir==2){
			x+=speed;
		}
		if(dir==3){
			y+=-speed;
		}
		if(dir==4){
			y+=speed;
		}
		
		if(range<Math.abs(xOrigin-x) || range<Math.abs(yOrigin-y))
			toRemove=true;
			
		for(Tile t : blocks){
			if(Bomberman.checkCollision(t, this))
				toRemove=true;
		}
		if(owner.getId()==ID.Player1){
			for(int i=0;i<handler.getEnemies().size();i++){
				Enemy e = handler.getEnemies().get(i);
				if(Bomberman.checkCollision(e, this)){
					toRemove=true;
					e.setDamage(1);
				}
			}
		}
		if(owner.getId()==ID.Boss){
			if(Bomberman.checkCollision(player, this) && !player.isInvinsible()){
				toRemove=true;
				player.setDamage(1);
				player.setInvinsible(true);
				Sound s = new Sound();
				s.playSound("hit");
			}
		}
		
	}
	
	private void giveId(){
		if(type=="F") id=ID.FProjectile;
		if(type=="E") id=ID.EProjectile;
		if(type=="P") id=ID.PProjectile;
		if(type=="V") id=ID.VProjectile;
		if(type=="B") id=ID.Boss;
		if(type=="Cheat") id=ID.CheatProjectile;
	}

}
