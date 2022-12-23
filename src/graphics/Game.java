package graphics;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JFrame;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class Game extends Canvas implements Runnable {
    public static JFrame frame;
    private Thread thread;
    private BufferedImage image;
    private BufferedImage[] player;
    private Spritesheet sheet;
    private boolean isRunning = true;
    private int x = 0;
    private int frames = 0;
    private int maxFrames = 20;
    private int curAnimation = 0, maxAnimations = 4;
    private final int WIDTH = 240;
    private final int HEIGHT = 160;
    private final int SCALE = 3;

    public Game() {
        this.sheet = new Spritesheet("/res/spritesheet.png");
        this.player = new BufferedImage[4];
        this.player[0] = sheet.getSprite(0, 0, 16, 16);
        this.player[1] = sheet.getSprite(16, 0, 16, 16);
        this.player[2] = sheet.getSprite(32, 0, 16, 16);
        this.player[3] = sheet.getSprite(48, 0, 16, 16);
        this.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        this.initFrame();
        this.image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    }

    public void initFrame() {
        frame = new JFrame("Game #1");
        frame.add(this);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public synchronized void start() {
        thread = new Thread(this);
        this.isRunning = true;
        thread.start();
    }

    public synchronized void stop() {
        isRunning = false;
        try {
            this.thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        Game game = new Game();
        game.start();
    }

    public void tick() {
        // this.x++;
        this.frames++;
        if (this.frames > this.maxFrames) {
            this.frames = 0;
            this.curAnimation++;
            if (this.curAnimation >= this.maxAnimations) {
                this.curAnimation = 0;
            }
        }
    }

    public void render() {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }
        Graphics g = image.getGraphics();
        g.setColor(new Color(255, 255, 255));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.red);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Animation Game!", 10, 40);

        /** Renderization the game */
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, WIDTH, HEIGHT);

        g2.rotate(Math.toRadians(45), 90 + 8, 90 + 8);
        g2.drawImage(player[this.curAnimation], 90, 90, null);
        g2.rotate(Math.toRadians(-45), 90 + 8, 90 + 8);
        g2.drawImage(player[this.curAnimation], 60, 120, null);

        g.dispose();
        g = bs.getDrawGraphics();
        g.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
        bs.show();
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        int frames = 0;
        double timer = System.currentTimeMillis();
        while (this.isRunning) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            if (delta >= 1) {
                this.tick();
                this.render();
                frames++;
                delta--;
            }
            if (System.currentTimeMillis() - timer >= 1000) {
                System.out.println("FPS: " + frames);
                frames = 0;
                timer += 1000;
            }
        }
        this.stop();
    }
}
