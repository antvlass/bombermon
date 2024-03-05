package org.bomb.model;

import java.util.ArrayList;

public class SmartEnemy extends Enemy{
 
	public SmartEnemy(int x, int y, int width, int height, ID id) {
		super(x, y, width, height, id);
		speed=1;
		lives=1;
		invinsible=false;
	}
	
	@Override
	public void update(ArrayList<Tile> blocks, Bomberman player,EnemyHandler handler) {
		if(x%50==0 && y%50==0){
		///Mouvement  si joueur dans la zone
			if(inRange(player,150)){//d�tection 3 cases
				if(x<player.getX()){
					dx=speed;
					dy=0;
				}
				if(x>player.getX()){ 
					dx=-speed;
					dy=0;
				}
				if(y<player.getY()){
					dy=speed;
					dx=0;
				}
				if(y>player.getY()){
					dy=-speed;
					dx=0;
				}
				y+=dy;
	        	x+=dx;
			}
		
		///Mouvement par d�faut si joueur pas dans la zone
			else{
				if(reloop || firstTurn){
					firstTurn=false;
					reloop=false;
					randomDirection();
				}
	    		y+=dy;
	        	x+=dx;
	    	}
		}
	  	else{
	    	y+=dy;
	    	x+=dx;
	    }
		if(tileCollision(blocks)){
				y-=dy;
		    	x-=dx;
		    	reloop=true;
		}
	}

}
