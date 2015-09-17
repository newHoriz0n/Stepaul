package game;

import java.util.Random;

public class Welt
{

	private int breite;
	private int hoehe;
	private double nachTunnelWiederTunnel; // Wert für die Wahrscheinlichkeit,
														// dass eine
	// Wand weiterverläuft
	private double nachWandTunnel; // Wert für die Anzahl der zu erzeugenden
												// neuen
												// Wände in %

	private double nahrungsVorkommen;

	private int[][] weltArray;

	private int nahrungsAnzahl;

	public Welt(int breite, int hoehe, double nachTunnelWiederTunnel, double nachWandTunnel, double nahrungsVorkommen)
	{

		this.breite = breite * 2 - 1;
		this.hoehe = hoehe * 2 - 1;
		this.nachTunnelWiederTunnel = nachTunnelWiederTunnel;
		this.nachWandTunnel = nachWandTunnel;
		this.nahrungsVorkommen = nahrungsVorkommen;

		// this.weltArray = createWeltOffen();
		// this.weltArray = createWeltLabyrinth();
		// this.weltArray = createWeltTunnel();
		this.weltArray = createWeltOffenII(0.05, 4, 0.2, true);
		// createMirrorWelt();
		createDoubleMirroredWelt();
		createRandWand();
		createStartFelder();
		// fuelleZentrum();

	}

	private void createRandWand()
	{
		for (int i = 0; i < weltArray[0].length; i++) {
			for (int j = 0; j < weltArray.length; j++) {
				if (i == 0 || i == breite - 1 || j == 0 || j == hoehe - 1) {
					weltArray[j][i] = 0;
				}
			}
		}

	}

	private void createDoubleMirroredWelt()
	{

		int[][] quarter = new int[(hoehe + 1) / 2][(breite + 1) / 2];
		// kopiere oben links
		for (int i = 0; i < quarter.length; i++) {
			for (int j = 0; j < quarter[i].length; j++) {
				quarter[i][j] = weltArray[i][j];
			}
		}
		for (int i = 0; i < quarter.length; i++) {
			for (int j = 0; j < quarter[i].length; j++) {
				weltArray[i][weltArray[0].length - 1 - j] = quarter[i][j];
				weltArray[weltArray.length - 1 - i][weltArray[0].length - 1 - j] = quarter[i][j];
				weltArray[weltArray.length - 1 - i][j] = quarter[i][j];
			}
		}

	}

	private void fuelleZentrum()
	{

		weltArray[hoehe / 2][breite / 2] = 0;
		weltArray[hoehe / 2][breite / 2 - 1] = 0;
		weltArray[hoehe / 2][breite / 2 + 1] = 0;
		weltArray[hoehe / 2 - 1][breite / 2] = 0;
		weltArray[hoehe / 2 - 1][breite / 2 - 1] = 0;
		weltArray[hoehe / 2 - 1][breite / 2 + 1] = 0;
		weltArray[hoehe / 2 + 1][breite / 2] = 0;
		weltArray[hoehe / 2 + 1][breite / 2 - 1] = 0;
		weltArray[hoehe / 2 + 1][breite / 2 + 1] = 0;

	}

	private int[][] createWeltTunnel()
	{

		int[][] welt = new int[hoehe][breite];
		Random r = new Random();

		int posX = 1;
		int posY = 1;

		for (int i = 1; i < welt.length - 1; i++) {
			for (int j = 1; j < welt[0].length - 1; j++) {

				int dir = 0;

				if (i == 3 && j == 3) {
					dir = 1;
					erweitereTunnel(welt, j, i, dir);
					dir = 2;
					erweitereTunnel(welt, j, i, dir);
				}
				if (i == hoehe - 4 && j == 3) {
					dir = 1;
					erweitereTunnel(welt, j, i, dir);
					dir = 0;
					erweitereTunnel(welt, j, i, dir);
				}
				if (i == hoehe - 4 && j == breite - 4) {
					dir = 0;
					erweitereTunnel(welt, j, i, dir);
					dir = 3;
					erweitereTunnel(welt, j, i, dir);
				}
				if (i == 3 && j == breite - 4) {
					dir = 3;
					erweitereTunnel(welt, j, i, dir);
					dir = 2;
					erweitereTunnel(welt, j, i, dir);
				}

				if (i % 2 == 1 && j % 2 == 1) {

					double val = r.nextDouble();

					if (val < nachWandTunnel) {

						posX = j;
						posY = i;

						welt[i][j] = 0;

						dir = r.nextInt(4);

						if (dir == 0) { // Oben
							posY = posY - 1;
						}
						if (dir == 1) { // Rechts
							posX = posX + 1;
						}
						if (dir == 2) { // Unten
							posY = posY + 1;
						}
						if (dir == 3) { // Links
							posX = posX - 1;
						}

						welt[posY][posX] = 0;

						boolean weiter = true;

						while (weiter == true) {

							if (posX % 2 == 1 && posY % 2 == 1) {
								dir = r.nextInt(4);
							}

							if (dir == 0) { // Oben
								posY = posY - 1;
							}
							if (dir == 1) { // Rechts
								posX = posX + 1;
							}
							if (dir == 2) { // Unten
								posY = posY + 1;
							}
							if (dir == 3) { // Links
								posX = posX - 1;
							}

							if (posX >= 1 && posY >= 1 && posX < breite - 1 && posY < hoehe - 1) {
								welt[posY][posX] = 1;
							}

							if (r.nextDouble() > nachTunnelWiederTunnel) {
								weiter = false;
							}

						}

					}
				}

			}
		}

		return welt;

	}

	private int[][] erweitereTunnel(int[][] welt, int x, int y, int dir)
	{

		Random r = new Random();

		int posX = x;
		int posY = y;

		welt[y][x] = 1;

		if (dir == 0) { // Oben
			posY = posY - 1;
		}
		if (dir == 1) { // Rechts
			posX = posX + 1;
		}
		if (dir == 2) { // Unten
			posY = posY + 1;
		}
		if (dir == 3) { // Links
			posX = posX - 1;
		}

		welt[posY][posX] = 1;

		boolean weiter = true;

		while (weiter == true) {

			if (posX % 2 == 1 && posY % 2 == 1) {
				dir = r.nextInt(4);
				System.out.println(dir);
			}

			if (dir == 0) { // Oben
				posY = posY - 1;
			}
			if (dir == 1) { // Rechts
				posX = posX + 1;
			}
			if (dir == 2) { // Unten
				posY = posY + 1;
			}
			if (dir == 3) { // Links
				posX = posX - 1;
			}

			if (posX >= 1 && posY >= 1 && posX < breite - 1 && posY < hoehe - 1) {
				welt[posY][posX] = 1;
			}

			if (r.nextDouble() > nachTunnelWiederTunnel) {
				weiter = false;
			}

		}

		return welt;

	}

	private void createStartFelder()
	{

		weltArray[1][1] = 1;
		weltArray[1][2] = 1;
		weltArray[1][3] = 1;
		weltArray[2][1] = 1;
		weltArray[2][2] = 2;
		weltArray[2][3] = 1;
		weltArray[3][1] = 1;
		weltArray[3][2] = 1;
		weltArray[3][3] = 1;

		weltArray[1][breite - 2] = 1;
		weltArray[1][breite - 3] = 1;
		weltArray[1][breite - 4] = 1;
		weltArray[2][breite - 2] = 1;
		weltArray[2][breite - 3] = 5;
		weltArray[2][breite - 4] = 1;
		weltArray[3][breite - 2] = 1;
		weltArray[3][breite - 3] = 1;
		weltArray[3][breite - 4] = 1;

		weltArray[hoehe - 2][1] = 1;
		weltArray[hoehe - 2][2] = 1;
		weltArray[hoehe - 2][3] = 1;
		weltArray[hoehe - 3][1] = 1;
		weltArray[hoehe - 3][2] = 4;
		weltArray[hoehe - 3][3] = 1;
		weltArray[hoehe - 4][1] = 1;
		weltArray[hoehe - 4][2] = 1;
		weltArray[hoehe - 4][3] = 1;

		weltArray[hoehe - 2][breite - 2] = 1;
		weltArray[hoehe - 2][breite - 3] = 1;
		weltArray[hoehe - 2][breite - 4] = 1;
		weltArray[hoehe - 3][breite - 2] = 1;
		weltArray[hoehe - 3][breite - 3] = 3;
		weltArray[hoehe - 3][breite - 4] = 1;
		weltArray[hoehe - 4][breite - 2] = 1;
		weltArray[hoehe - 4][breite - 3] = 1;
		weltArray[hoehe - 4][breite - 4] = 1;

	}

	private int[][] createWeltLabyrinth()
	{

		// 0 = Wand;
		// 1 = keine Wand;

		int[][] welt = new int[hoehe][breite];

		Random r = new Random();

		int posX = 1;
		int posY = 1;

		for (int i = 0; i < hoehe; i++) {

			if (i % 2 == 1) {
				posX = 1;
				boolean letztesWarTunnel = false;
				while (posX < breite - 1) {

					double val = r.nextDouble();

					if (letztesWarTunnel == false) {
						if (val < nachWandTunnel) {
							welt[i][posX] = 1;
							letztesWarTunnel = true;
						}
					} else if (letztesWarTunnel == true) {
						if (val < nachTunnelWiederTunnel) {
							welt[i][posX] = 1;
							letztesWarTunnel = true;
						}
					}
					posX++;
				}
			}
		}

		for (int i = 0; i < breite; i++) {

			if (i % 2 == 1) {
				posY = 1;
				boolean letztesWarTunnel = false;
				while (posY < hoehe - 1) {

					double val = r.nextDouble();

					if (welt[posY][i] == 0) {
						if (letztesWarTunnel == false) {
							if (val < nachWandTunnel) {
								welt[posY][i] = 1;
								letztesWarTunnel = true;
							}
						} else if (letztesWarTunnel == true) {
							if (val < nachTunnelWiederTunnel) {
								welt[posY][i] = 1;
								letztesWarTunnel = true;
							}
						}
					}
					posY++;
				}
			}
		}

		return welt;

	}

	private void createMirrorWelt()
	{

		int[][] mirrorWelt = new int[hoehe][breite];

		// Spiegelung
		for (int i = 0; i < mirrorWelt.length; i++) {
			for (int j = 0; j < mirrorWelt[0].length; j++) {
				mirrorWelt[i][j] = weltArray[hoehe - i - 1][breite - j - 1];
			}
		}
		int ueberschreib = 0;
		double v = (double) breite / (double) hoehe;
		for (int i = 0; i < weltArray.length; i++) {
			if (i < weltArray.length / 2) {
				ueberschreib = Math.round((float) (v * i));
			} else {
				ueberschreib = weltArray[0].length - Math.round((float) (v * (weltArray.length - i - 1)));
			}
			for (int j = 0; j < ueberschreib; j++) {
				weltArray[i][j] = mirrorWelt[i][j];
			}
		}
	}

	/**
	 * 
	 * @param wegdichte
	 *           zw. 0...1
	 * @param wegbreite
	 *           1...
	 * @param wegbreiteToleranz
	 *           zw. 0...1
	 * @param wand
	 *           erzeugt Wände wenn true, sonst Gänge
	 * @return
	 */
	private int[][] createWeltOffenII(double wegdichte, int wegbreite, double wegbreiteToleranz, boolean wand)
	{

		int[][] welt = new int[hoehe][breite];
		if (wand) {
			for (int i = 0; i < welt.length; i++) {
				for (int j = 0; j < welt[i].length; j++) {
					welt[i][j] = 1;
				}
			}
		}
		Random r = new Random();

		int mauerteile = (int) (breite * hoehe * wegdichte);

		for (int i = 0; i < mauerteile; i++) {
			int x = r.nextInt(breite);
			int y = r.nextInt(hoehe);

			double trueWegbreite = ((r.nextDouble() * wegbreiteToleranz * wegbreite) + (1 - wegbreiteToleranz) * wegbreite) * 0.5;

			for (int j = x - wegbreite; j < x + wegbreite; j++) {
				for (int k = y - wegbreite; k < y + wegbreite; k++) {
					if (j >= 0 && j < breite && k >= 0 && k < hoehe) {
						double dist = Math.sqrt(Math.pow(j - x, 2) + Math.pow(k - y, 2));
						if (dist < trueWegbreite) {
							welt[k][j] = 1;
							if (wand) {
								welt[k][j] = 0;
							}
						}
					}
				}
			}

		}

		return welt;

	}

	private int[][] createWeltOffen()
	{

		// 0 = Wand;
		// 1 = keine Wand;

		int[][] welt = new int[hoehe][breite];

		Random r = new Random();

		int posX = -1;
		int posY = -1;

		for (int i = 0; i < breite; i++) {
			for (int j = 0; j < hoehe; j++) {

				double val = r.nextDouble();

				if (val < nachWandTunnel) {

					posX = i;
					posY = j;
					welt[j][i] = 1;

					boolean weiter = true;

					if (r.nextDouble() > nachTunnelWiederTunnel) {
						weiter = false;
					}

					while (weiter) {

						int dir = r.nextInt(4);

						if (dir == 0) { // Oben
							posY = posY - 1;
						}
						if (dir == 1) { // Rechts
							posX = posX + 1;
						}
						if (dir == 2) { // Unten
							posY = posY + 1;
						}
						if (dir == 3) { // Links
							posX = posX - 1;
						}

						if (posX >= 0 && posY >= 0 && posX < breite && posY < hoehe) {
							welt[posY][posX] = 1;
						}

						if (r.nextDouble() > nachTunnelWiederTunnel) {
							weiter = false;
						}

					}

				}

			}
		}

		for (int i = 0; i < welt[0].length; i++) {
			for (int j = 0; j < welt.length; j++) {
				if (i == 0 || i == breite - 1 || j == 0 || j == hoehe - 1) {
					welt[j][i] = 0;
				}
			}
		}

		return welt;

	}

	public void updateWelt()
	{

		erzeugeNahrung();

	}

	/**
	 * Platziert zufällig Nahrungsteilchen in der Welt
	 */
	private void erzeugeNahrung()
	{

		Random r = new Random();

		if (nahrungsAnzahl < breite * hoehe * nahrungsVorkommen * 0.5) {
			int x = r.nextInt(breite);
			int y = r.nextInt(hoehe);
			if (weltArray[y][x] == 1) {
				nahrungsAnzahl++;
				weltArray[y][x] = 6;
			}
		}

	}

	/**
	 * Erzeugt eine Stringrepräsentation der Welt ohne Ameisen
	 */
	public String toString()
	{

		String s = "";
		for (int i = 0; i < weltArray.length; i++) {
			for (int j = 0; j < weltArray[0].length; j++) {
				s += getZeichen(weltArray[i][j]) + " ";
			}
			s += "\n";
		}

		return s;

	}

	public String getZeichen(int i)
	{
		if (i == 0) {
			return "X";
		}
		if (i == 1) {
			return " ";
		}
		if (i == 2) {
			return "1";
		}
		if (i == 3) {
			return "2";
		}
		if (i == 4) {
			return "3";
		}
		if (i == 5) {
			return "4";
		}
		return "?";
	}

	public int[][] getWeltArray()
	{
		return weltArray;
	}

	public int[][] getStartPositionen()
	{

		int[] s1 = { 2, 2 };
		int[] s2 = { breite - 3, hoehe - 3 };
		int[] s3 = { 2, hoehe - 3 };
		int[] s4 = { breite - 2, 2 };

		int[][] starts = { s1, s2, s3, s4 };

		return starts;

	}

	public void ernteWeltFeld(int x, int y)
	{
		weltArray[y][x] = 1;
		nahrungsAnzahl--;
	}

}
