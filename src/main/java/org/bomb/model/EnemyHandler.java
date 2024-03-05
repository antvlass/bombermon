package org.bomb.model;

import java.util.ArrayList;

public class EnemyHandler {

	private ArrayList<Enemy> enemies;
	private int invinTime=0;
	
	public EnemyHandler(){
		enemies = new ArrayList<Enemy>();
	}

	/**
	 * @param blocks : murs et briques composant le terrain
	 * @param player : bomberman de la partie solo
	 * @param bonus : ensemble des bonus dipsonibles sur le terrain
	 * Mise � jour de tous les ennemis
	 */
	public void update(ArrayList<Tile> blocks,Bomberman player,ArrayList<Bonus> bonus){
		invinTime++;
		for(int i = 0; i<enemies.size();i++){
			Enemy e = enemies.get(i);
			if(invinTime%60==0){
				e.setInvinsible(false);
				invinTime=0;
			}
			e.update(blocks,player,this);
			if(!e.isAlive()){
				if(e.getId()==ID.Boss){
					bonus.add(new Bonus(e.getX(),e.getY(),50,50,ID.Evolution));//Lorsque le boss est �limin�, il l�che le bonus d'�volution
				}
				e.giveScore(player);
				this.removeObject(e);
			}
		}

	}
	
	public ArrayList<Enemy> getEnemies(){
		return enemies;
	}
	
	public void addObject(Enemy ennemi){
		this.enemies.add(ennemi);
	}
	
	public void removeObject(Enemy ennemi){
		this.enemies.remove(ennemi);
	}
}
