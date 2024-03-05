package org.bomb.model;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class SoloField extends Field{

	public SoloField(){
		super();
		image = randomLevel();
		createLevel(image,blocks);
		createBonus(bonus);
	}
	
	private BufferedImage randomLevel(){
		BufferedImage img = null;
		Random r = new Random();
		int i = r.nextInt(7);//7 terrains disponibles dont 1 est choisi al�atoirement
		img = loadImg("/Level/levelSolo"+i+".png");
		return img;
	}
	
	@Override
	protected void createBonus(ArrayList<Bonus> Bonus){
		for(Tile t : blocks){
			if(t.getId() == ID.Brick){
				float s = (float) r.nextFloat();
				if(s<=0.5){//Probabilit� de 50% que le bloc contienne un bonus
					Bonus b = new Bonus(t.getX(),t.getY(),50,50);
					if(b.getNumber()!= 8) bonus.add(b); //Pas mettre de bonus evolution dans les briques
				}
			}
		}
	}
	
	@Override
	protected void createLevel(BufferedImage image, ArrayList<Tile> blocks){
		int w = image.getWidth();
		int h = image.getHeight();
		for(int y = 0; y< h; y++){
			for(int x = 0; x<w;x++){
				int pixel = image.getRGB(x, y);
				int red = (pixel >> 16) & 0xff;
				int green = (pixel >> 8) & 0xff;
				int blue = (pixel) & 0xff;
				
				if(red == 255 && blue == 0 && green == 0)//Si rouge : c'est un mur 
					blocks.add(new Tile(x*50,(y+2)*50,50,50,ID.Wall));
				if(red == 0 && blue == 255 && green == 0)//Si bleu c'est une brique cassable
					blocks.add(new Tile(x*50,(y+2)*50,50,50,ID.Brick));
				if(red == 0 && blue == 0 && green == 255)//Si vert c'est un spawn (door)
					blocks.add(new Tile(x*50,(y+2)*50,50,50,ID.Door));	
			}
		}
	}
	

	
}
