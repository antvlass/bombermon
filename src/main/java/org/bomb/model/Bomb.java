package org.bomb.model;

import java.util.ArrayList;
import org.bomb.view.Sound;


public class Bomb extends GameObject{
	private int power;
	private Bomberman owner;
	private ArrayList<BombBlast> blast = new ArrayList<BombBlast>();
	private ArrayList<Tile> blockTouched = new ArrayList<Tile>();
	
	private int explosionDelay=180,showDelay;//delai de 3 secondes (180/60=3)
	private boolean instantExplosion,exploded;
	
	public Bomb(int x,int y,int width,int height,Bomberman owner,int power){
		super(x,y,width,height);
		this.owner=owner;
		this.power=power;
		showDelay=8; //8/60 secondes d'affichage des explosions
		instantExplosion=false;
		exploded=false;
	}
	public ArrayList<BombBlast> getBlast(){
		return blast;
	}
	public Bomberman getOwner(){
		return owner;
	}
	public void setInstantExplosion(boolean flag){
		this.instantExplosion=flag;
	}
	public int getDelay(){
		return explosionDelay;
	}
	
	/**
	 * @param blocks : murs et briques qui composent le terrain
	 * @param bombers : les bombermans pr�sents sur le terrain
	 * @param brokenBricks : briques qui ont �t�es d�truites par les explosions
	 * @param handler : ensemble des ennemis
	 * M�thode de mise � jour de la bombe. Une fois son compteur arriv� � 0, il y'a cr�ation d'une explosion et
	 * v�rification des collisions �ventuelles avec les autres objets du jeu
	 */
	public void update(ArrayList<Tile> blocks,ArrayList<Bomberman> bombers,ArrayList<Tile> brokenBricks,EnemyHandler handler){
		explosionDelay--;
		if(instantExplosion && !exploded){//Si cette bombe est touch�e par l'explosion d'une autre : explosion instantan�e
			destruction(blocks);
			explosionCollision(blocks,brokenBricks,bombers,handler);	
			exploded=true;
			Sound s = new Sound();
			s.playSound("explosion");
		}
		if(explosionDelay==0 && !exploded){
			destruction(blocks);
			explosionCollision(blocks,brokenBricks,bombers,handler);
			exploded=true;
			Sound s = new Sound();
			s.playSound("explosion");
		}
		if(exploded){
			showDelay--;
			if(showDelay==0){
				for(Tile t : brokenBricks) blocks.remove(t);
				toRemove=true;
				blast.clear();
			}
		}
	}

	/**
	 * @param blocks : murs et briques qui composent le terrain
	 * Cr�ation de la liste contenant les explosions
	 */
	private void destruction(ArrayList<Tile> blocks){
		blast.add(new BombBlast(x,y,50,50));
		blast.addAll(createBlast(blocks, 1, 0));
		blast.addAll(createBlast(blocks, -1, 0));
		blast.addAll(createBlast(blocks, 0, 1));
		blast.addAll(createBlast(blocks, 0, -1));
	}


	/**
	 * @param blocks : murs et briques qui composent le terrain
	 * @param bombers : les bombermans pr�sents sur le terrain
	 * @param brokenBricks : briques qui ont �t�es d�truites par les explosions
	 * @param handler : ensemble des ennemis
	 * V�rification des �ventuelles collisions avec d'autres objets et interactions avec ces derniers (d�g�ts, suppression,...)
	 */
	private void explosionCollision(ArrayList<Tile> blocks,ArrayList<Tile> brokenBricks, ArrayList<Bomberman> bombers, EnemyHandler handler){
		for(BombBlast bb : blast){
			for(Bomberman b : bombers){
				if(Bomberman.checkCollision(bb,b) && !b.isInvinsible()){
					b.setDamage(1);
					b.setInvinsible(true); // Evite que si le joueur reste dans la zone d'explosion il perde tt ses vies (frames d'invinsibilit�)
					Sound s = new Sound();
					s.playSound("damage");
				}
				for(Bomb bomb : b.getBombs()){
					if(Bomberman.checkCollision(bb,bomb) && this != bomb) //Cette bombe-ci va obligatoirement se toucher elle-m�me
							bomb.setInstantExplosion(true);
				}
				for(int i = 0; i<b.getMines().size();i++){
		    		Mine tempM = b.getMines().get(i);
		    		if(Bomberman.checkCollision(bb,tempM))
		    			tempM.setRemove(true);	    		
				}
			}
			for(int i =0;i< handler.getEnemies().size();i++){
				Enemy tempE = handler.getEnemies().get(i);
				if(Bomberman.checkCollision(bb,tempE) && !tempE.isInvinsible()){
					tempE.setDamage(1);
					tempE.setInvinsible(true);
				}
			}
		}
		brokenBricks.addAll(blockTouched);		
	}
	

	/**
	 *
	 * @param blocks : murs et briques qui composent le terrain
	 * @param dx dy vaut 1 0, 0 1, -1 0, 0 -1
	 * @return revoies la liste des explosions
	 */
	private ArrayList<BombBlast> createBlast(ArrayList<Tile> blocks, int dx, int dy){
		ArrayList<BombBlast> blast = new ArrayList<BombBlast>();
		for(int i=1;i<=power;i++){
			BombBlast tempB = new BombBlast(x+dx*i*50,y+dy*i*50, 50, 50); 
			for(Tile t : blocks){
				if(Bomberman.checkCollision(t, tempB)){
					if(t.getId() == ID.Brick)
						blockTouched.add(t);
					return blast;
				}	
			}
			blast.add(tempB);
		}
		return blast;
	}
}

