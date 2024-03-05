package org.bomb.model;

import java.util.ArrayList;
import java.util.Random;

public class Spawner {
	
	private int level;
	private EnemyHandler handler;
	private ArrayList<Bonus> bonus;
	private ArrayList<Tile> blocks;
	private ArrayList<Tile> doors;
	private int bonusCount;
	private int doorX,doorY;
	
	public Spawner(EnemyHandler handler,ArrayList<Bonus> bonus,ArrayList<Tile> blocks){
		this.handler=handler;
		this.bonus=bonus;
		this.blocks=blocks;
		this.level=0;
		doors = findDoors(this.blocks);
	}
	
	/**
	 * M�thode de gestion des niveaux
	 * Ajoute les ennemis en fonction du niveau 
	 */
	public void update(){
		if(handler.getEnemies().size()==0){
			level++;
			//LEVEL 1 (2basic)
			if(level==1){
				for(int i=1;i<=2;i++){
					doorCoord();
					handler.addObject(new BasicEnemy(doorX,doorY,50,50,ID.BasicE));
				}
			}
					
			///LEVEL2 (2basic+1fast)
			else if (level==2){
				for(int i=1;i<=2;i++){
					doorCoord();
					handler.addObject(new BasicEnemy(doorX,doorY,50,50,ID.BasicE));
				}
				doorCoord();
				handler.addObject(new BasicEnemy(doorX,doorY,50,50,ID.FastE));
			}
		
			///LEVEL3 (3smart)
			else if(level==3){
				for(int i=1;i<=3;i++){
					doorCoord();
					handler.addObject(new SmartEnemy(doorX,doorY,50,50,ID.SmartE));
				}
			}
		
			////LEVEL4 (2basic+2fast+1smart)
			else if(level==4){
				for(int i=1;i<=2;i++){
					doorCoord();
					handler.addObject(new BasicEnemy(doorX,doorY,50,50,ID.BasicE));
					doorCoord();
					handler.addObject(new BasicEnemy(doorX,doorY,50,50,ID.FastE));
				}
				doorCoord();
				handler.addObject(new SmartEnemy(doorX,doorY,50,50,ID.SmartE));
			}
		
			else if(level==5){//Level 5 juste boss
			}
			
			else if(level==6){//3 smart + 3 fast
				for(int i=1;i<=3;i++){
					doorCoord();
					handler.addObject(new SmartEnemy(doorX,doorY,50,50,ID.SmartE));
					doorCoord();
					handler.addObject(new BasicEnemy(doorX,doorY,50,50,ID.FastE));
				}
			}
			
			else if(level==7){ //2 fast,basic,smart
				for(int i=1;i<=2;i++){
					doorCoord();
					handler.addObject(new SmartEnemy(doorX,doorY,50,50,ID.SmartE));
					doorCoord();
					handler.addObject(new BasicEnemy(doorX,doorY,50,50,ID.FastE));
					doorCoord();
					handler.addObject(new BasicEnemy(doorX,doorY,50,50,ID.BasicE));
				}
			}
			
			else if(level==8){//4 fast et 4 smart
				for(int i=1;i<=4;i++){
					doorCoord();
					handler.addObject(new SmartEnemy(doorX,doorY,50,50,ID.SmartE));
					doorCoord();
					handler.addObject(new BasicEnemy(doorX,doorY,50,50,ID.FastE));
				}
			}
			
			else if(level==9){//4 de chaque
				for(int i=1;i<=4;i++){
					doorCoord();
					handler.addObject(new SmartEnemy(doorX,doorY,50,50,ID.SmartE));
					doorCoord();
					handler.addObject(new BasicEnemy(doorX,doorY,50,50,ID.FastE));
					doorCoord();
					handler.addObject(new BasicEnemy(doorX,doorY,50,50,ID.BasicE));
				}
			}
			
			else if(level==10){//5 fast , 1 smart + 1 boss
				for(int i=1;i<=5;i++){
					doorCoord();
					handler.addObject(new BasicEnemy(doorX,doorY,50,50,ID.FastE));
				}
				doorCoord();
				handler.addObject(new SmartEnemy(doorX,doorY,50,50,ID.SmartE));
			}
			
			
			else{//Si on atteint plus que vague 10 : 4 de chaque
				for(int i=1;i<=4;i++){
					doorCoord();
					handler.addObject(new BasicEnemy(doorX,doorY,50,50,ID.BasicE));
					doorCoord();
					handler.addObject(new BasicEnemy(doorX,doorY,50,50,ID.FastE));
					doorCoord();
					handler.addObject(new SmartEnemy(doorX,doorY,50,50,ID.SmartE));
				}
			}
			
			if(level%2==0 && bonusCount==0)
				bonus.add(new Bonus(400,450,50,50,ID.Projectile));//Bonus projectile tt les 2 vagues
			if(level%5==0){//Boss tt les 5 vagues
				doorCoord();
				handler.addObject(new Boss(doorX,doorY,50,50,ID.Boss));
			}
		}
	}
	
	/**
	 * @param blocks : murs,briques et portes composant le terrain
	 * @return revoies une liste avec les portes
	 */
	private ArrayList<Tile> findDoors(ArrayList<Tile> blocks){
		ArrayList<Tile> doors = new ArrayList<Tile>();
		for(Tile t : blocks){
			if(t.getId()==ID.Door)
				doors.add(t);
		}
		return doors;
	}

	/**
	 * Choisis une porte al�atoire pour l'apparition de l'ennemi
	 */
	private void doorCoord(){
		Random r = new Random();
		int number = r.nextInt(doors.size());
		doorX = doors.get(number).getX();
		doorY = doors.get(number).getY();
	}
	
	public int getLevel(){
		return level;
	}
	public void addLevel(int number){
		level+=number;
	}
}
