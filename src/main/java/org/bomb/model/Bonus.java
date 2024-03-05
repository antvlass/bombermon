package org.bomb.model;

import java.util.Random;

public class Bonus extends GameObject{
	private BonusType type;
	private int number;
	
	//Tous les types de bonus du jeu
	private enum BonusType{
		//Bonus
		Life,
		RangeUp,
		More,
		Mine,
		Speed,
		//Malus
		RangeDown, 
		Less, 
		//Rare
		Projectile,
		Evolution;
	}
	
	public Bonus(int x, int y, int width, int height) {
		super(x, y, width, height);
		randomEffect();
	}
	
	public Bonus(int x, int y, int width, int height,ID id){//2eme constructeur dans le cas o� on veut pr�ciser le type du bonus
		super(x, y, width, height);
		this.id=id;
		if(id==ID.Evolution){
			type = BonusType.Evolution;
			this.number=8;
		}
		if(id==ID.Projectile){
			type = BonusType.Projectile;
			this.number=9;
		}
	}
	
	public int getNumber(){
		return number;
	}

	/**
	 * @param b : bomberman qui a pris le bonus
	 * Modifie les attributs du bomberman en question en fonction du type du bonus
	 */
	public void getEffect(Bomberman b){
		switch(this.type){
		case Life : b.setDamage(-1);break;
		case More : b.addMore(1);break;
		case Less : b.addMore(-1);break;
		case RangeUp : b.addRange(1);break;
		case RangeDown : b.addRange(-1);break;
		case Mine : b.addMine(1);break;
		case Speed : b.setSpeed(5);break;
		case Evolution : b.evolution();break;
		case Projectile : b.addProjectile(5);break;
		}
	}

	/**
	 * Attribue un effet al�atoire au bonus
	 */
	private void randomEffect(){
		Random r = new Random();
		int number = r.nextInt(8);  
		switch(number){
			case 0 : {
				type = BonusType.Life; 
				id = ID.Bonus;
				this.number=1;
			}break;
		
			case 1 : {
				type = BonusType.More; 
				id = ID.Bonus;
				this.number=2;
			}break;

			case 2 : {
				type = BonusType.Less;
				id = ID.Malus;
				this.number=3;
			}break;
			
			case 3 : {
				type = BonusType.RangeUp; 
				id = ID.Bonus;
				this.number=4;
			}break;
		
			case 4 : {
				type = BonusType.RangeDown; 
				id = ID.Malus;
				this.number=5;
			}break;
		
			case 5 : {
				type = BonusType.Mine;
				id = ID.Bonus;
				this.number=6;
			}break;
			
			case 6 : {
				type = BonusType.Speed;
				id = ID.Bonus;
				this.number=7;
			}break;
			
			case 7 : {
				type = BonusType.Evolution;
				id = ID.Evolution;
				this.number=8;
			}break;
		}
	}
}
