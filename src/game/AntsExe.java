package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kis.EasyBotKI;
import kis.PaulKI;

public class AntsExe
{

	private Welt welt;
	private List<Spieler> spielerListe;
	private List<SpielerInfo> spielerInfos;

	private long updateRate;
	private boolean paused;

	public AntsExe()
	{
		this.welt = new Welt(20, 10, 0.85, 0.3, 0.05);
		this.spielerListe = new ArrayList<Spieler>();
		this.spielerInfos = new ArrayList<SpielerInfo>();

		this.updateRate = 1000;

		spielerListe.add(new PaulKI());
		spielerInfos.add(new SpielerInfo(5));
		spielerListe.add(new EasyBotKI());
		spielerInfos.add(new SpielerInfo(15));
		spielerListe.add(new EasyBotKI());
		spielerInfos.add(new SpielerInfo(15));
		spielerListe.add(new EasyBotKI());
		spielerInfos.add(new SpielerInfo(15));

	}

	public void run()
	{

		welt.updateWelt();

		calcSpielZuege();

		// drawWelt();

	}

	private String calcSpielerSymbol(int j2)
	{
		if (j2 == 0) {
			return "+ ";
		}
		if (j2 == 1) {
			return "- ";
		}
		if (j2 == 2) {
			return "* ";
		}
		if (j2 == 3) {
			return ": ";
		}
		return "? ";
	}

	/**
	 * Einsprungspunkt für die Berechnung der Spielzüge aller Spieler
	 */
	private void calcSpielZuege()
	{

		for (int i = 0; i < spielerListe.size(); i++) {

			checkErzeugeAmeise(i); // Erzeugt sofern möglich eine neue Ameise unter
											// dem Kommando des entsprechenden Spielers.
			berechneSicht(i);
			spielerListe.get(i).updateKI(spielerInfos.get(i).getProxyAmeisen(), spielerInfos.get(i).getNahrungsVorrat(),
					spielerInfos.get(i).getSichtbareWelt());
			uebertrageKommandos(i); // Übertragt die durch die KI per setCommando
			// and die ProxyAmeisen gegebenen Kommandos an
			// die OriginalAmeisen
			bereiteAmeisenVor(i);
			berechneBewegungen2(i); // Bewegt sofern möglich die Ameisen in die gewuenschte Laufrichtung
			berechneSicht(i);
			checkNahrungsaufnahme(i);
			checkKampf(i); //
		}
		entvoelkern();
	}

	private void bereiteAmeisenVor(int spNr)
	{

		for (int j = 0; j < spielerInfos.get(spNr).getOriginalAmeisen().size(); j++) {
			spielerInfos.get(spNr).getOriginalAmeisen().get(j).vorbereiten();
		}

	}

	private void berechneBewegungen2(int spNr)
	{

		checkMauer(spielerInfos.get(spNr).getOriginalAmeisen());
		for (int i = 0; i < spielerInfos.get(spNr).getOriginalAmeisen().size(); i++) {
			checkSequenzielleKollision(spielerInfos.get(spNr).getOriginalAmeisen(), i);
			spielerInfos.get(spNr).getOriginalAmeisen().get(i).move();
		}

	}

	private void checkSequenzielleKollision(List<Ant> originalAmeisen, int antID)
	{

		for (int i = 0; i < originalAmeisen.size(); i++) {
			if (i != antID) {
				if (originalAmeisen.get(i).getX() == originalAmeisen.get(antID).getNaechstePosition()[0]
						&& originalAmeisen.get(i).getY() == originalAmeisen.get(antID).getNaechstePosition()[1]) {
					originalAmeisen.get(antID).setBewegbar(false);
				}
			}
		}

	}

	private void checkMauer(List<Ant> originalAmeisen)
	{
		for (int i = 0; i < originalAmeisen.size(); i++) {
			originalAmeisen.get(i).setBewegbar(
					kannLaufen(originalAmeisen.get(i).getX(), originalAmeisen.get(i).getY(), originalAmeisen.get(i)
							.getGewuenschteLaufRichtung()));
		}
	}

	private void berechneSicht(int spielerNr)
	{
		for (int j = 0; j < spielerInfos.get(spielerNr).getOriginalAmeisen().size(); j++) {
			berechneSichtAmeiseRadial(spielerInfos.get(spielerNr).getOriginalAmeisen().get(j));
		}

		for (int i = 0; i < spielerInfos.get(spielerNr).getOriginalAmeisen().size(); i++) {
			uebertrageSichtAufKarte(spielerNr, spielerInfos.get(spielerNr).getOriginalAmeisen().get(i));
		}

	}

	private void uebertrageSichtAufKarte(int spielerNr, Ant ant)
	{

		int[][] sichtbareWelt = new int[20][40];
		for (int i = 0; i < sichtbareWelt.length; i++) {
			for (int j = 0; j < sichtbareWelt[i].length; j++) {
				sichtbareWelt[i][j] = -1;
			}

		}

		for (int i = 0; i < ant.getSicht().length; i++) {
			for (int j = 0; j < ant.getSicht()[0].length; j++) {

				if (ant.getX() - 5 + j >= 0 && ant.getX() - 5 + j < sichtbareWelt[0].length && ant.getY() - 5 + i >= 0
						&& ant.getY() - 5 + i < sichtbareWelt.length) {
					sichtbareWelt[ant.getY() - 5 + i][ant.getX() - 5 + j] = ant.getSicht()[i][j];
				}

			}
		}
		sichtbareWelt[ant.getY()][ant.getX()] = 7;

		spielerInfos.get(spielerNr).speicherSicht(sichtbareWelt);

	}

	/**
	 * Berechnet für eine Ameise die Sicht radial im Umkreis von 5 Feldern. Die Sicht wird der Ameise als int [][]
	 * übergeben. Jedes Feld der Matrix repräsentiert ein Feld um die Ameise herum. Dabei ist array [5][5] der Standpunkt
	 * der Ameise. Der eingetragene Wert für jedes Feld gibt das Objekt auf diesem Feld an. -1 = Keine Information, 0 =
	 * Wand, 1 = Frei, 2 - 5 = Ameisenhaufen, 6 = Nahrung, 7 = Freund, 8 = Feind.
	 * 
	 * @param ant
	 */
	private void berechneSichtAmeiseRadial(Ant ant)
	{

		int[][] sicht = new int[11][11];

		for (int i = 0; i < sicht.length; i++) {
			for (int j = 0; j < sicht[0].length; j++) {
				sicht[i][j] = -1;
			}
		}

		int aufloesung = 40;

		for (int i = 0; i < aufloesung; i++) {
			double angle = (Math.PI * 2) / (double) (aufloesung) * i;
			double dx = Math.cos(angle);
			double dy = Math.sin(angle);
			boolean wand = false;
			int dist = 1;
			int posX = ant.getX();
			int posY = ant.getY();

			while (wand == false && dist <= 5) {

				int newPosX = posX + Math.round((float) (dx * (double) dist));
				int newPosY = posY - Math.round((float) (dy * (double) dist));

				if (newPosX >= 0 && newPosY >= 0 && newPosX < welt.getWeltArray()[0].length
						&& newPosY < welt.getWeltArray().length) {

					if (welt.getWeltArray()[newPosY][newPosX] == 1) {

						boolean ameiseImWeg = false;
						for (int k = 0; k < spielerInfos.size(); k++) {
							for (int j = 0; j < spielerInfos.get(k).getOriginalAmeisen().size(); j++) {
								if (spielerInfos.get(k).getOriginalAmeisen().get(j).getX() == newPosX
										&& spielerInfos.get(k).getOriginalAmeisen().get(j).getY() == newPosY) {
									if (k == ant.getSpNr()) {
										sicht[5 + Math.round((float) (-dy * (double) dist))][5 + Math
												.round((float) (dx * (double) dist))] = 7;
										ameiseImWeg = true;
									} else {
										sicht[5 + Math.round((float) (-dy * (double) dist))][5 + Math
												.round((float) (dx * (double) dist))] = 8;
										ameiseImWeg = true;
									}
								}
							}
						}

						if (ameiseImWeg == true) {
							wand = true;
						} else {
							sicht[5 + Math.round((float) (-dy * (double) dist))][5 + Math.round((float) (dx * (double) dist))] = 1;
							dist++;
						}

					} else {
						sicht[5 + Math.round((float) (-dy * (double) dist))][5 + Math.round((float) (dx * (double) dist))] = welt
								.getWeltArray()[newPosY][newPosX];
						wand = true;
					}

				} else {
					wand = true;
				}

			}

		}
		ant.setSicht(sicht);

	}

	public boolean kannLaufen(int xStart, int yStart, ELaufRichtung elr)
	{

		if (elr == ELaufRichtung.Hoch) {
			if (welt.getWeltArray()[yStart - 1][xStart] != 0) {
				return true;
			}
		}
		if (elr == ELaufRichtung.Rechts) {
			if (welt.getWeltArray()[yStart][xStart + 1] != 0) {
				return true;
			}
		}
		if (elr == ELaufRichtung.Runter) {
			if (welt.getWeltArray()[yStart + 1][xStart] != 0) {
				return true;
			}
		}
		if (elr == ELaufRichtung.Links) {
			if (welt.getWeltArray()[yStart][xStart - 1] != 0) {
				return true;
			}
		}

		// System.out.println("AntsExe kannLaufen nein");

		return false;

	}

	private void checkKampf(int spNr)
	{

		for (int i = 0; i < spielerInfos.get(spNr).getOriginalAmeisen().size(); i++) {
			for (int j = 0; j < spielerInfos.size(); j++) {
				if (j != spNr) {
					for (int j2 = 0; j2 < spielerInfos.get(j).getOriginalAmeisen().size(); j2++) {
						if (spielerInfos.get(spNr).getOriginalAmeisen().get(i).getX() == spielerInfos.get(j)
								.getOriginalAmeisen().get(j2).getX()
								&& spielerInfos.get(spNr).getOriginalAmeisen().get(i).getY() == spielerInfos.get(j)
										.getOriginalAmeisen().get(j2).getY()) {
							spielerInfos.get(spNr).getOriginalAmeisen().get(i).sterben();
							spielerInfos.get(j).getOriginalAmeisen().get(j2).sterben();
							// System.out.println("AntsExe checkKampf check1");
						}
					}
				}
			}
		}

	}

	public void entvoelkern()
	{

		for (int i = 0; i < spielerInfos.size(); i++) {

			List<Integer> removes = new ArrayList<Integer>();
			for (int j = 0; j < spielerInfos.get(i).getOriginalAmeisen().size(); j++) {
				if (spielerInfos.get(i).getOriginalAmeisen().get(j).stirbt()) {
					removes.add(j);
				}
			}

			// System.out.println("AntsExe entvoelkern removes.size() " +
			// removes.size() );

			Collections.sort(removes);

			for (int j = removes.size() - 1; j >= 0; j--) {
				// System.out.println(removes.get(j));
				spielerInfos.get(i).ameiseVernichten(removes.get(j));
			}

		}

	}

	private void uebertrageKommandos(int i)
	{

		spielerInfos.get(i).uebertrageCommandos();

	}

	private void checkErzeugeAmeise(int i)
	{
		if (spielerListe.get(i).willNeueAmeise()) {
			if (spielerInfos.get(i).getNahrungsVorrat() > 0
					&& spielerInfos.get(i)
							.startFeldIstFrei(welt.getStartPositionen()[i][0], welt.getStartPositionen()[i][1])) {

				spielerInfos.get(i).erzeugeAmeise(i, welt.getStartPositionen()[i][0], welt.getStartPositionen()[i][1], "");
				spielerListe.get(i).resetAnforderung();
			}
		}
	}

	private void checkNahrungsaufnahme(int spielerID)
	{

		for (int j = 0; j < spielerInfos.get(spielerID).getOriginalAmeisen().size(); j++) {
			if (welt.getWeltArray()[spielerInfos.get(spielerID).getOriginalAmeisen().get(j).getY()][spielerInfos
					.get(spielerID).getOriginalAmeisen().get(j).getX()] == 6) {
				spielerInfos.get(spielerID).gewinneNahrung();
				welt.ernteWeltFeld(spielerInfos.get(spielerID).getOriginalAmeisen().get(j).getX(),
						spielerInfos.get(spielerID).getOriginalAmeisen().get(j).getY());
			}
		}

	}

	public Welt getWelt()
	{
		return welt;
	}

	public List<SpielerInfo> getSpielerInfos()
	{
		return spielerInfos;
	}

	public void setUpdateRate(double percentage)
	{

		int min = 1000;
		int max = 10;

		this.updateRate = (long) (min - (percentage * (min - max)));

	}

	public void togglePause()
	{
		this.paused = !paused;
	}

	public boolean isPaused()
	{
		return paused;
	}

	public long getUpdateRate()
	{
		return updateRate;
	}
	
	public List<Spieler> getSpielerListe()
	{
		return spielerListe;
	}

	public static void main(String[] args) throws InterruptedException
	{

		AntsExe ae = new AntsExe();
		while (true) {
			ae.run();
			Thread.sleep(ae.updateRate);
		}

	}

}
