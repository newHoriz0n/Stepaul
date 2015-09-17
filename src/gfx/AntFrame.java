package gfx;

import game.AntsExe;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

public class AntFrame extends JFrame
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AntsExe ae;
	private AntView av;

	private Image offscreen;
	private Graphics bufferedGraphics;

	public AntFrame()
	{

		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setExtendedState(MAXIMIZED_BOTH);
		// setBackground(Color.BLACK);

		this.ae = new AntsExe();
		this.av = new AntView(ae);

		add(av);

		addKeyListener(new KeyListener()
		{

			@Override
			public void keyTyped(KeyEvent arg0)
			{
			}

			@Override
			public void keyReleased(KeyEvent arg0)
			{
			}

			@Override
			public void keyPressed(KeyEvent arg0)
			{
				if (arg0.getKeyCode() == 32) {
					ae.togglePause();
				}
			}
		});

	}

	public void update()
	{
		ae.run();

		offscreen = createImage(this.getWidth(), this.getHeight());
		Graphics g = offscreen.getGraphics();
		av.paint(g);
		bufferedGraphics = av.getGraphics();
		bufferedGraphics.drawImage(offscreen, 0, 0, av);
		repaint();
	}

	public static void main(String[] args)
	{
		AntFrame af = new AntFrame();

		long frameDauer = af.ae.getUpdateRate();
		long lastUpdate = 0;

		while (true) {
			if (lastUpdate + frameDauer < System.currentTimeMillis()) {
				if (!af.ae.isPaused()) {
					af.update();
					frameDauer = af.ae.getUpdateRate();
					lastUpdate = System.currentTimeMillis();
				}
			}
		}

	}

}
