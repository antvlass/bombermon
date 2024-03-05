package org.bomb.model;

import java.util.ArrayList;

public class BasicEnemy extends Enemy{

	public BasicEnemy(int x, int y, int width, int height, ID id) {
		super(x, y, width, height, id);
		if(id == ID.BasicE) speed=1;
		else if(id == ID.FastE) speed=2;
		lives=1;
		invinsible=false;
	}

	@Override
	public void update(ArrayList<Tile> blocks,Bomberman player,EnemyHandler handler) {
		if(x%50==0 && y%50==0){//Mouvement case par case
			if(reloop || firstTurn){
				firstTurn=false;
				reloop=false;
				randomDirection();
			}
    		y+=dy;
        	x+=dx;
    	}
    	else{
    		y+=dy;
    		x+=dx;
    	}
		if(tileCollision(blocks)){//D�s qu'il y'a collision avec un bloc, l'ennemi change de direction
				y-=dy;
		    	x-=dx;
		    	reloop=true;
		}
	}

}
