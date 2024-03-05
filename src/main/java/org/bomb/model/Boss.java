package org.bomb.model;

import java.util.ArrayList;

import org.bomb.view.Sound;

public class Boss extends Enemy {

	private ArrayList<Projectile> projectiles;

	public Boss(int x, int y, int width, int height,ID id) {
		super(x, y, width, height, id);
		projectiles=new ArrayList<Projectile>();
		speed=1;
		lives=4;
		invinsible=false;
	}
	public ArrayList<Projectile> getProjectiles(){
		return projectiles;
	}

	private void shoot(){
		Projectile p = new Projectile(x,y,50,50,dir,"B",this,5,5);
		projectiles.add(p);
	}
	
	@Override
	public void update(ArrayList<Tile> blocks, Bomberman player,EnemyHandler handler) {
		//Objets
		for(int i=0;i<projectiles.size();i++){
    		projectiles.get(i).update(blocks,handler,player);
    		if(projectiles.get(i).toRemove())
    			projectiles.remove(i);
    	}
		
		//Mouvement
		move(player, blocks);
	}

	/**
	 * @param player : bomberman de la partie solo
	 * @param blocks : murs et briques composant le terrain
	 * Le boss se d�place al�atoirement par d�faut. Si le joueur s'en approche trop (5 cases de distance),
	 * le boss va le suivre. Si le joueur se trouve sur la m�me ligne (vertical ou horizontal) que le boss, 
	 * le boss va tirer des projectiles dans la direction
	 */
	private void move(Bomberman player,ArrayList<Tile> blocks) {
		if(x%50==0 && y%50==0){
			if(inRange(player,250)){//d�tection 5 cases
				if(x<player.getX()){//right
					dx=speed;
					dy=0;
					dir=2;
				}
				if(x>player.getX()){ //left
					dx=-speed;
					dy=0;
					dir=1;
				}
				if(y<player.getY()){//down
					dy=speed;
					dx=0;
					dir=4;
				}
				if(y>player.getY()){//up
					dy=-speed;
					dx=0;
					dir=3;
				}
				y+=dy;
	        	x+=dx;
	        	if((player.getX()==x || player.getY() == y) && !tileCollision(blocks)){//Tire seuelement si sur la m�me ligne et qu'il n'y a pas collision avec un block
	        		shoot();
	        		Sound s = new Sound();
	        		s.playSound("shadow");
	        	}
	    			
			}
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
