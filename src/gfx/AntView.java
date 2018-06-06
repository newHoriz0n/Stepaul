package gfx;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import game.AntsExe;
import game.Spieler;

import javax.swing.JPanel;

public class AntView extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean initFinished;

	private AntsExe ae;

	private int feldGroesse;

	private Color wand;
	private Color frei;
	private Color[] spielerFarben;
	private Color nahrung;
	private Color marker;

	private Color speedCtrlBar;
	private Color speedCtrlSlider;
	private int speedCtrlPosition;
	private Font speedCtrlFont;

	private int offX;
	private int offY;

	private Font infoFont;

	public AntView(final AntsExe ae) {

		ladeFarben();

		this.ae = ae;

		this.feldGroesse = 30;

		this.offX = 70;
		this.offY = 70;

		this.infoFont = new Font("Courir", Font.PLAIN, 20);
		this.speedCtrlFont = new Font("Sans", Font.PLAIN, 16);

		this.speedCtrlPosition = offY + feldGroesse + (ae.getWelt().getWeltArray().length - 2) * feldGroesse;

		addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
				checkSetSpeedCtrl(arg0);
			}

			private void checkSetSpeedCtrl(MouseEvent arg0) {
				if (arg0.getX() > offX + 5 + (ae.getWelt().getWeltArray()[0].length + 1) * feldGroesse
						&& arg0.getX() < offX + 5 + (ae.getWelt().getWeltArray()[0].length + 2) * feldGroesse
						&& arg0.getY() > offY + feldGroesse
						&& arg0.getY() < offY + feldGroesse + (ae.getWelt().getWeltArray().length - 2) * feldGroesse) {

					speedCtrlPosition = arg0.getY();
					ae.setUpdateRate(1.0 - ((double) (arg0.getY() - (offY + feldGroesse))
							/ (double) ((ae.getWelt().getWeltArray().length - 2) * feldGroesse)));

				}
			}
		});

	}

	/**
	 * Laed die Spielfarben
	 */
	private void ladeFarben() {
		this.wand = new Color(50, 50, 50);
		this.frei = new Color(240, 240, 240);
		this.spielerFarben = new Color[] { new Color(204, 51, 51), new Color(0, 102, 204), new Color(255, 204, 51),
				new Color(0, 204, 51) };
		this.nahrung = new Color(0, 0, 0);
		this.marker = Color.MAGENTA;
		this.speedCtrlBar = new Color(50, 50, 50);
		this.speedCtrlSlider = Color.LIGHT_GRAY;
	}

	public void paint(Graphics g) {
		
		if (getHeight() > 0) {
			// drawSpielerSicht(g);
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, getWidth(), getHeight());
			initFinished = true;
			System.out.println(getWidth());
			System.out.println("Init Finished");
		}

		drawWelt(g);

		drawNahrung(g);

		drawSpieler(g);

		drawSpielerInfo(g);

		drawSpeedControl(g);

		drawSpielerMarker(g);
	}

	private void drawSpielerMarker(Graphics g) {

		for (Spieler s : ae.getSpielerListe()) {
			if (s.getMarker() != null) {
				for (int[] m : s.getMarker()) {
					g.setColor(marker);
					g.fillRect(offX + 10 + m[0] * feldGroesse, offY + 10 + m[1] * feldGroesse, 10, 10);
				}
			}
		}

	}

	private void drawSpielerSicht(Graphics g) {

		for (int i = 0; i < ae.getSpielerInfos().get(0).getSichtbareWelt().length; i++) {
			for (int j = 0; j < ae.getSpielerInfos().get(0).getSichtbareWelt()[0].length; j++) {

				if (ae.getSpielerInfos().get(0).getSichtbareWelt()[i][j] == -1) {
					g.setColor(Color.GRAY);
				}
				if (ae.getSpielerInfos().get(0).getSichtbareWelt()[i][j] == 0) {
					g.setColor(Color.BLACK);
				}
				if (ae.getSpielerInfos().get(0).getSichtbareWelt()[i][j] == 1) {
					g.setColor(Color.WHITE);
				}
				if (ae.getSpielerInfos().get(0).getSichtbareWelt()[i][j] == 2) {
					g.setColor(Color.GREEN);
				}
				if (ae.getSpielerInfos().get(0).getSichtbareWelt()[i][j] == 3) {
					g.setColor(Color.YELLOW);
				}
				if (ae.getSpielerInfos().get(0).getSichtbareWelt()[i][j] == 4) {
					g.setColor(Color.YELLOW);
				}
				if (ae.getSpielerInfos().get(0).getSichtbareWelt()[i][j] == 5) {
					g.setColor(Color.YELLOW);
				}
				if (ae.getSpielerInfos().get(0).getSichtbareWelt()[i][j] == 6) {
					g.setColor(Color.PINK);
				}
				if (ae.getSpielerInfos().get(0).getSichtbareWelt()[i][j] == 7) {
					g.setColor(Color.BLUE);
				}
				if (ae.getSpielerInfos().get(0).getSichtbareWelt()[i][j] == 8) {
					g.setColor(Color.RED);
				}

				g.fillRect(50 + 30 * j, 50 + 30 * i, 30, 30);

			}
		}

	}

	private void drawTestSicht(Graphics g) {

		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 1400, 800);

		int[][] sicht = new int[21][21];

		int[][] welt = new int[21][21];

		welt[5][6] = 1;
		welt[6][6] = 1;
		welt[7][6] = 1;
		welt[8][6] = 1;

		welt[15][16] = 1;
		welt[16][16] = 1;
		welt[17][16] = 1;
		welt[18][16] = 1;

		int aufloesung = 2000;

		for (int i = 0; i < aufloesung; i++) {
			double angle = (Math.PI * 2) / (double) (aufloesung) * i;
			boolean wand = false;
			int dist = 1;
			int posX = 10;
			int posY = 10;
			while (wand == false && dist <= 10) {
				int newPosX = posX + Math.round((float) Math.cos(angle) * dist);
				int newPosY = posY - Math.round((float) Math.sin(angle) * dist);

				if (welt[newPosY][newPosX] == 0) {
					sicht[newPosY][newPosX] = -1;
					dist++;
				}
				if (welt[newPosY][newPosX] == 1) {
					sicht[newPosY][newPosX] = 1;
					wand = true;
				}
			}

		}

		for (int i = 0; i < sicht.length; i++) {
			for (int j = 0; j < sicht[0].length; j++) {
				g.setColor(Color.WHITE);
				if (sicht[i][j] == 0) {
					g.setColor(Color.GRAY);
				}
				if (sicht[i][j] == 1) {
					g.setColor(Color.BLACK);
				}
				if (sicht[i][j] == -1) {
					g.setColor(Color.WHITE);
				}
				g.fillRect(30 + j * 30, 30 + i * 30, 30, 30);
			}
		}

	}

	private void drawSpeedControl(Graphics g) {

		g.setColor(speedCtrlBar);

		g.fillRect(offX + 5 + (ae.getWelt().getWeltArray()[0].length + 1) * feldGroesse, offY + feldGroesse,
				feldGroesse - 10, (ae.getWelt().getWeltArray().length - 2) * feldGroesse);
		g.setColor(Color.WHITE);
		g.drawLine(offX + 5 + (ae.getWelt().getWeltArray()[0].length + 1) * feldGroesse, speedCtrlPosition,
				offX + 5 + (ae.getWelt().getWeltArray()[0].length + 2) * feldGroesse, speedCtrlPosition);
		g.setFont(speedCtrlFont);
		g.drawString("" + (int) (1000 / ae.getUpdateRate()) + " FPS",
				offX + 5 + (ae.getWelt().getWeltArray()[0].length + 2) * feldGroesse + 10, speedCtrlPosition);

	}

	private void drawSpielerInfo(Graphics g) {

		g.setFont(infoFont);

		g.setColor(spielerFarben[0]);
		g.drawString("" + ae.getSpielerInfos().get(0).getOriginalAmeisen().size() + " | "
				+ ae.getSpielerInfos().get(0).getNahrungsVorrat(), offX + 5 + feldGroesse, offY + 21);

		if (ae.getSpielerInfos().size() > 1) {
			g.setColor(spielerFarben[1]);
			g.drawString(
					"" + ae.getSpielerInfos().get(1).getOriginalAmeisen().size() + " | "
							+ ae.getSpielerInfos().get(1).getNahrungsVorrat(),
					offX + 5 + feldGroesse * (ae.getWelt().getWeltArray()[0].length - 4),
					offY + 21 + feldGroesse * (ae.getWelt().getWeltArray().length - 1));

			if (ae.getSpielerInfos().size() > 2) {
				g.setColor(spielerFarben[2]);
				g.drawString(
						"" + ae.getSpielerInfos().get(2).getOriginalAmeisen().size() + " | "
								+ ae.getSpielerInfos().get(2).getNahrungsVorrat(),
						offX + 5 + feldGroesse, offY + 21 + feldGroesse * (ae.getWelt().getWeltArray().length - 1));

				if (ae.getSpielerInfos().size() > 3) {
					g.setColor(spielerFarben[3]);
					g.drawString(
							"" + ae.getSpielerInfos().get(3).getOriginalAmeisen().size() + " | "
									+ ae.getSpielerInfos().get(3).getNahrungsVorrat(),
							offX + 5 + feldGroesse * (ae.getWelt().getWeltArray()[0].length - 4), offY + 21);
				}

			}

		}
	}

	/**
	 * Zeichnet die Ameisen der Spieler
	 * 
	 * @param g
	 */
	private void drawSpieler(Graphics g) {

		for (int i = 0; i < ae.getSpielerInfos().size(); i++) {
			g.setColor(spielerFarben[i]);
			for (int j = 0; j < ae.getSpielerInfos().get(i).getOriginalAmeisen().size(); j++) {
				g.fillRect(offX + 5 + ae.getSpielerInfos().get(i).getOriginalAmeisen().get(j).getX() * feldGroesse,
						offY + 5 + ae.getSpielerInfos().get(i).getOriginalAmeisen().get(j).getY() * feldGroesse, 20,
						20);
			}
		}
	}

	private void drawNahrung(Graphics g) {

		for (int i = 0; i < ae.getWelt().getWeltArray().length; i++) {
			for (int j = 0; j < ae.getWelt().getWeltArray()[0].length; j++) {
				if (ae.getWelt().getWeltArray()[i][j] == 6) {
					g.setColor(nahrung);
					g.fillRect(offX + 10 + j * feldGroesse, offY + 10 + i * feldGroesse, 10, 10);
				}
			}
		}

	}

	private void drawWelt(Graphics g) {
		for (int i = 0; i < ae.getWelt().getWeltArray().length; i++) {
			for (int j = 0; j < ae.getWelt().getWeltArray()[0].length; j++) {

				if (ae.getWelt().getWeltArray()[i][j] == 0) {
					g.setColor(wand);
					g.fillRect(offX + j * feldGroesse, offY + i * feldGroesse, feldGroesse, feldGroesse);
				}
				if (ae.getWelt().getWeltArray()[i][j] == 1 || ae.getWelt().getWeltArray()[i][j] == 6) {
					g.setColor(frei);
					g.fillRect(offX + j * feldGroesse, offY + i * feldGroesse, feldGroesse, feldGroesse);
				}
				if (ae.getWelt().getWeltArray()[i][j] == 2) {
					g.setColor(new Color((int) (spielerFarben[0].getRed() * 0.5),
							(int) (spielerFarben[0].getGreen() * 0.5), (int) (spielerFarben[0].getBlue() * 0.5)));
					g.fillRect(offX + j * feldGroesse, offY + i * feldGroesse, feldGroesse, feldGroesse);
				}
				if (ae.getWelt().getWeltArray()[i][j] == 3) {
					g.setColor(new Color((int) (spielerFarben[1].getRed() * 0.5),
							(int) (spielerFarben[1].getGreen() * 0.5), (int) (spielerFarben[1].getBlue() * 0.5)));

					g.fillRect(offX + j * feldGroesse, offY + i * feldGroesse, feldGroesse, feldGroesse);
				}
				if (ae.getWelt().getWeltArray()[i][j] == 4) {
					g.setColor(new Color((int) (spielerFarben[2].getRed() * 0.5),
							(int) (spielerFarben[2].getGreen() * 0.5), (int) (spielerFarben[2].getBlue() * 0.5)));

					g.fillRect(offX + j * feldGroesse, offY + i * feldGroesse, feldGroesse, feldGroesse);
				}
				if (ae.getWelt().getWeltArray()[i][j] == 5) {
					g.setColor(new Color((int) (spielerFarben[3].getRed() * 0.5),
							(int) (spielerFarben[3].getGreen() * 0.5), (int) (spielerFarben[3].getBlue() * 0.5)));

					g.fillRect(offX + j * feldGroesse, offY + i * feldGroesse, feldGroesse, feldGroesse);
				}
			}
		}
	}

}
