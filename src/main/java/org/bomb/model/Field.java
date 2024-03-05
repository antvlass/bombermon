package org.bomb.model;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

public abstract class Field {
	protected ArrayList<Tile> blocks;
	protected ArrayList<Bonus> bonus;
	protected BufferedImage image;
	protected Random r = new Random();
	
	public Field(){
		blocks = new ArrayList<Tile>();
		bonus = new ArrayList<Bonus>();
	}
	
	public ArrayList<Tile> getBlocks(){
		return blocks;
	}
	public ArrayList<Bonus> getBonus(){
		return bonus;
	}
	
	protected BufferedImage loadImg(String name) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(getClass().getResourceAsStream(name));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
	
	/**
	 * @param Bonus : liste des bonus � compl�ter
	 * Cr�ation de la liste des bonus en fonction des blocs du terrain
	 */
	protected abstract void createBonus(ArrayList<Bonus> Bonus);
	
	
	/**
	 * @param image : image correspondant au terrain
	 * @param blocks : liste des blocs � compl�ter
	 * Cr�ation du terrain � partir des couleurs d'une image externe
	 */
	protected abstract void createLevel(BufferedImage image, ArrayList<Tile> blocks);
}
