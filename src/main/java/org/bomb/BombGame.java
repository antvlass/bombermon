package org.bomb;

import javax.swing.SwingUtilities;
import org.bomb.control.GameController;
import org.bomb.view.GamePanel;
import org.bomb.view.GameWindow;
import org.bomb.view.Sound;

public class BombGame implements Runnable {
    private Thread thread;
    private volatile boolean running = false; // Ensure thread-safety for the game loop
    private final GameController controller;
    private final GamePanel panel;
    public static Sound sound = new Sound();

    public static GameState STATE;

    public enum GameState {
        Solo, Multi, Menu, Help, Controls, Items, Pause, SoloOver, MultiOver, ChooseMulti, ChooseNumber, ChooseSolo;
    }

    public BombGame() {
        controller = new GameController();
        panel = new GamePanel(controller);
        new GameWindow(850, 870, panel, this);
    }

    /** Game loop */
    public void run() {
        long lastTime = System.nanoTime();
        long fpsTimer = System.currentTimeMillis();
        double tickRate = 60.0; // 60 FPS
        double ns = 1_000_000_000.0 / tickRate;
        double delta = 0;
        int frames = 0;

        STATE = GameState.Menu; // ✅ Keeping it static
        sound.play("menu");

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            while (delta >= 1) {
                controller.update();
                delta--;
            }

            // Ensure UI updates on the EDT (Event Dispatch Thread)
            SwingUtilities.invokeLater(panel::repaint);
            frames++;

            // FPS Display (debugging)
            if (System.currentTimeMillis() - fpsTimer >= 1000) {
                System.out.println("FPS: " + frames);
                frames = 0;
                fpsTimer += 1000;
            }

            try {
                Thread.sleep(16); // Limit CPU usage
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        stop();
    }

    public synchronized void start() {
        if (running) return;
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public synchronized void stop() {
        if (!running) return;
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new BombGame().start();  // Ensure the game starts correctly with static STATE
    }
}