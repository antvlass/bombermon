package org.bomb.view;
import javax.swing.JFrame;

import org.bomb.BombGame;

import java.io.Serial;

public class GameWindow extends JFrame{

	@Serial
	private static final long serialVersionUID = 1L;

	public GameWindow(int width,int height,GamePanel panel,BombGame game){
		this.setTitle("Bomberman Pokemon");
	    this.setSize(width,height);
	    this.setResizable(false);
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    this.setLocationRelativeTo(null);

	    
	    this.setContentPane(panel);
	    this.setVisible(true); 
	    game.start();//Lancement du notre thread
	}

}
