package org.bomb.model;
import java.awt.Rectangle;


public abstract class GameObject {
	
	protected int x,y,width,height,dx,dy;
	protected ID id = null;
	protected boolean toRemove;
	protected int dir; //Left = 1 ; Right = 2 ; Up = 3 ; Down = 4;
	
	public GameObject(int x,int y,int width,int height){
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
	}
	
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
	public void setX(int x){
		this.x=x;
	}
	public void setY(int y){
		this.y=y;
	}
	public int getWidth(){
		return width;
	}
	public int getHeight(){
		return height;
	}
	public ID getId(){
		return id;
	}
	public void setId(ID id){
		this.id=id;
	}
	public int getDx(){
		return dx;
	}
	public int getDy(){
		return dy;
	}
	public int getDir(){
		return dir;
	}
	public Rectangle getBounds(){
		return new Rectangle(x, y, width, height); 
	}
	public boolean toRemove(){
		return toRemove;
	}
	public void setRemove(boolean flag){
		this.toRemove=flag;
	}
	
}
