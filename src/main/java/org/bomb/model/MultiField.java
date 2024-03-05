package org.bomb.model;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class MultiField extends Field{

	private int evolCount=0;
	
	public MultiField(){
		super();
		image = loadImg("/Level/multiplayer.png");
		createLevel(image,blocks);
		createBonus(bonus);
	}
	
	@Override
	protected void createBonus(ArrayList<Bonus> Bonus){
		for(Tile t : blocks){
			if(t.getId() == ID.Brick){
				float s = (float) r.nextFloat();
				if(s<=0.5){//Probabilt� de 50% d'avoir un bonus dans la brique
					Bonus b = new Bonus(t.getX(),t.getY(),50,50);
					if(b.getNumber()==8){
						evolCount++;
						if(evolCount<=3)//Pas plus que 3 bonus d'�volution par partie multi
							bonus.add(b);
					}
					else bonus.add(b);
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
				
				if(red == 255 && blue == 0 && green == 0)
					blocks.add(new Tile(x*50,(y+2)*50,50,50,ID.Wall));
				if(red == 0 && blue == 255 && green == 0){
					float s = (float) Math.random();
					if(s<=0.6) //Probabilt� de 60% que la case bleue soit une brique
						blocks.add(new Tile(x*50,(y+2)*50,50,50,ID.Brick));	//y+2 car barre de status
				}
			}
		}
	}
}
