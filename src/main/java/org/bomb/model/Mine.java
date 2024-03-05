package org.bomb.model;

import java.util.ArrayList;

import org.bomb.view.Sound;

public class Mine extends GameObject{

	private Bomberman owner;

	
	public Mine(int x, int y, int width, int height,Bomberman owner) {
		super(x, y, width, height);
		this.owner=owner;
		toRemove=false;
	}
	
	/**
	 * @param bombers : tous les bombermans de la partie
	 * @param handler : ensemble des ennemis
	 * M�thode de mise � jour de l'objet. Ajoute un d�g�t s'il y'a collision avec un des 2 param�tres
	 */
	public void update(ArrayList<Bomberman> bombers,EnemyHandler handler){
		
		for(int i=0;i<handler.getEnemies().size();i++){
			Enemy e = handler.getEnemies().get(i);
			if(Bomberman.checkCollision(e, this)){
				toRemove=true;
				e.setDamage(1);
				Sound s = new Sound();
				s.playSound("mine");
			}
		}
		
		for(Bomberman b : bombers)
			if(Bomberman.checkCollision(b,this) && b!=owner){
				toRemove=true;
				b.setDamage(1);
				b.setInvinsible(true);
				Sound s = new Sound();
				s.playSound("mine");
			}
	}
	
}
