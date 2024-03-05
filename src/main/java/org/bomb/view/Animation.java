package org.bomb.view;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Animation {

	private int speed;
	private int frames;
	
	private int index = 0;
	private int count = 0;
	
	private BufferedImage[] images;
	private BufferedImage currentImg;
	
	public Animation(int speed, BufferedImage... args){
		this.speed=speed; //Plus speed petit plus animation rapide
		images = new BufferedImage[args.length];
		for(int i=0;i<args.length;i++){
			images[i] = args[i];
		}
		frames = args.length;
	}
	
	/**
	 * Mise � jour de l'animation
	 * A placer dans un boucle
	 */
	public void runAnimation(){
		index++;
		if(index > speed){
			index=0;
			nextFrame();
		}
	}
	
	private void nextFrame(){
		for(int i =0;i<frames;i++){
			if(count==i)
				currentImg = images[i];
		}
		count++;
		if(count > frames)
			count=0;
	}

	/**
	 * @param g
	 * @param x
	 * @param y
	 * @param width
	 * @param heigth
	 * Rendu graphique de l'animation
	 */
	public void drawAnimation(Graphics g,int x,int y,int width,int heigth){
		g.drawImage(currentImg, x, y, width, heigth,null);
	}
}
