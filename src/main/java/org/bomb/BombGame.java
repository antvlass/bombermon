package org.bomb;

import org.bomb.control.GameController;
import org.bomb.view.GamePanel;
import org.bomb.view.GameWindow;
import org.bomb.view.Sound;

public class BombGame implements Runnable{
    private Thread thread;
    public boolean running;
    private final GameController controller;
    private final GamePanel panel;

    public static Sound sound = new Sound();
    public static GameState STATE;

    public static enum GameState{
        //Diff�rents phases du jeu
        Solo,
        Multi,
        Menu,
        Help,
        Controls,
        Items,
        Pause,
        SoloOver,
        MultiOver,
        ChooseMulti,
        ChooseNumber,
        ChooseSolo;
    }

    public BombGame(){
        controller = new GameController();
        panel = new GamePanel(controller);
        new GameWindow(850,870,panel,this);
    }

    /**
     * Boucle principale du jeu permet l'appel des m�thodes de rendu et de mise � jour
     */
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfUpdates = 60.0; //Nbr d'updates par seconde
        double ns = 1000000000/amountOfUpdates;
        double delta = 0;
        STATE = GameState.Menu; //On d�marre la jeu dans le menu
        sound.play("menu");
        while(running){
            long now = System.nanoTime();
            delta+= (now-lastTime)/ns;
            lastTime=now;
            while(delta>=1){//Maintenir 60 updates par seconde
                controller.update();
                delta--;
            }
            panel.repaint();
        }
        stop();
    }

    /**
     * Lancement d'un nouveau thread
     */
    public synchronized void start(){
        if(running) return;
        thread = new Thread(this);
        thread.start();
        running=true;

    }
    /**
     * Arr�t de notre thread
     */
    public synchronized void stop(){
        if(!running) return;
        try {
            thread.join();
            running=false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }




    public static void main(String[] args) {
        new BombGame();

    }
}

