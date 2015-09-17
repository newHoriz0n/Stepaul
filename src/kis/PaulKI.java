package kis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import game.AmeisenCommandoSet;
import game.ELaufRichtung;
import game.ProxyAnt;
import game.Spieler;

public class PaulKI extends Spieler
{

	private int[][] fixeWelt;
	private int[][] geruchsWelt;
	private WegBeschreibung[][] wege;

	private List<Stuetzpunkt> stuetzpunkte;

	private int nextAntID;

	private double pSammler;
	private double pAngreifer;
	private double pPionier;
	private double pWache;

	private int gesammelt;
	private int maxWeglaenge;

	public PaulKI()
	{

		nextAntID = 1;

		this.stuetzpunkte = new ArrayList<Stuetzpunkt>();
	}

	@Override
	public void updateKI(List<ProxyAnt> antlist, int nahrungsVorrat, int[][] sichtbareWelt)
	{

		if (fixeWelt == null) {
			this.fixeWelt = new int[sichtbareWelt.length][sichtbareWelt[0].length];
			this.geruchsWelt = new int[sichtbareWelt.length][sichtbareWelt[0].length];
			this.wege = new WegBeschreibung[sichtbareWelt.length][sichtbareWelt[0].length];
			for (int i = 0; i < fixeWelt.length; i++) {
				for (int j = 0; j < fixeWelt[i].length; j++) {
					fixeWelt[i][j] = -1;
					wege[i][j] = new WegBeschreibung();
				}
			}
		}

		checkNeueAmeise(antlist);

		berechneFunktionen(antlist.size() + nahrungsVorrat);

		speichereFixeWelt(sichtbareWelt);
		speichereGeruchsWelt(antlist);
		speichereBewegungen(antlist);
		speichereWege(antlist);
		berechneBewegung(antlist);

		// printFixeWelt();
		// printGeruchsWelt();

		neueAmeiseAnforden();

	}

	private void berechneFunktionen(int anz)
	{

		pSammler = 0.6;
		pPionier = 0.2;
		pWache = 0.1;
		pAngreifer = 0;

		if (anz >= 5) {
			pAngreifer = (double) anz / 60.0;
			pSammler = 0.3;
			pWache = 0.1;
			pPionier = 0.1;
		}

	}

	private void speichereBewegungen(List<ProxyAnt> antlist)
	{
		for (ProxyAnt a : antlist) {

			String[] infos = getAntInfos(a);
			String weg = infos[2];
			if (weg.equals("-")) {
				weg = "";
			} else {
				weg += getLetzteLaufrichtung(a);
			}

			a.setAufgabe(infos[0] + ":" + infos[1] + ":" + weg + ":" + infos[3] + ":" + infos[4]);

		}
	}

	private void checkNeueAmeise(List<ProxyAnt> antlist)
	{
		for (ProxyAnt a : antlist) {
			if (a.getTask() == "") {
				if (antlist.size() == 1) {
					this.stuetzpunkte.add(new Stuetzpunkt(0, new int[] { a.getX(), a.getY() }));
				}
				String func = Funktionen.Pionier.toString();
				if (nextAntID >= 2 && stuetzpunkte.size() > 1) {
					func = Funktionen.Soldat.toString();
				}
				a.setAufgabe("" + nextAntID + ":" + func + ":-:-:0");
				nextAntID++;
			}
		}
	}

	private void speichereFixeWelt(int[][] sichtbareWelt)
	{
		for (int i = 0; i < sichtbareWelt.length; i++) {
			for (int j = 0; j < sichtbareWelt[i].length; j++) {
				if (sichtbareWelt[i][j] >= 0 && sichtbareWelt[i][j] <= 5) {
					fixeWelt[i][j] = sichtbareWelt[i][j];
				}
			}
		}
	}

	private void speichereGeruchsWelt(List<ProxyAnt> antlist)
	{
		for (ProxyAnt a : antlist) {
			geruchsWelt[a.getY()][a.getX()]++;
		}
	}

	private void speichereWege(List<ProxyAnt> antlist)
	{

		for (ProxyAnt a : antlist) {

			String[] infos = getAntInfos(a);

			// System.out.println(infos[2]);

			if (infos[4].equals("0") && wege[a.getY()][a.getX()].getWeg() != null) {
				if (wege[a.getY()][a.getX()].getWeg().length < infos[2].length()) {
					reorientiereAmeise(a, wege[a.getY()][a.getX()].getWeg());
				}
			}

			for (int i = 0; i < a.getSicht().length; i++) {
				for (int j = 0; j < a.getSicht()[i].length; j++) {
					if (a.getX() - 5 + j >= 0 && a.getX() - 5 + j < wege[i].length && a.getY() - 5 + i >= 0
							&& a.getY() - 5 + i < wege.length) {
						wege[a.getY() - 5 + i][a.getX() - 5 + j].neuerWeg(getAmeisenLaufweg2IntArray(infos[2]), new int[] {
								j - 5, i - 5 });
					}
				}
			}

			if (infos[2].length() > maxWeglaenge) {
				maxWeglaenge = infos[2].length();
				if (stuetzpunkte.size() > 1) {
					stuetzpunkte.remove(1);
					Stuetzpunkt s = new Stuetzpunkt(1, new int[] { a.getX(), a.getY() });
					s.addStuetzpunkt(0, getWegRueckwaerts(wege[a.getY()][a.getX()]));
					stuetzpunkte.add(s);
					if (stuetzpunkte.get(0).getWegeMap().replace(1, wege[a.getY()][a.getX()]) != null)
						;

				} else {

					Stuetzpunkt s = new Stuetzpunkt(1, new int[] { a.getX(), a.getY() });
					s.addStuetzpunkt(0, getWegRueckwaerts(wege[a.getY()][a.getX()]));
					stuetzpunkte.add(s);
					if (stuetzpunkte.get(0).getWegeMap().put(1, wege[a.getY()][a.getX()]) != null)
						;

				}
			}

		}

	}

	/**
	 * Überschreibt den bisherigen Weg der Ameise, wenn der Weg zum aktuellen Standort vom Ameisenhaufen kürzer ist as
	 * der Weg der Ameise.
	 * 
	 * @param a
	 * @param wegNeu
	 */
	private void reorientiereAmeise(ProxyAnt a, int[] wegNeu)
	{

		String s = "";
		for (int i = 0; i < wegNeu.length; i++) {
			s += "" + wegNeu[i];
		}

		String id = getAntInfos(a)[0];
		String order = getAntInfos(a)[1];
		String weg = s;
		String ziel = getAntInfos(a)[3];
		String start = getAntInfos(a)[4];

		a.setAufgabe(id + ":" + order + ":" + weg + ":" + ziel + ":" + start);

	}

	/**
	 * Gibt die in Task gespeicherten Infos der Ameise zurück. 0 = id, 1 = order, 2 = weg, 3 = ziel, 4 = startStützpunkt
	 * 
	 * @param task
	 * @return
	 */
	private String[] getAntInfos(ProxyAnt a)
	{
		String[] taskInfos = a.getTask().split(":");
		return taskInfos;

	}

	private int[] getAmeisenLaufweg2IntArray(String weg)
	{

		int[] wegInts = new int[weg.length()];
		if (!weg.contains("-")) {
			if (wegInts.length > 0) {
				String[] wegs = weg.split("");
				for (int i = 0; i < wegs.length; i++) {
					wegInts[i] = Integer.parseInt(wegs[i]);
				}
			}
		}
		return wegInts;
	}

	private void berechneBewegung(List<ProxyAnt> antlist)
	{

		for (ProxyAnt a : antlist) {

			String[] infos = getAntInfos(a);

			String id = infos[0];
			String order = infos[1];
			String weg = infos[2];
			String ziel = infos[3];
			String start = infos[4];

			if (order.equals(Funktionen.Wache.toString())) {

				Random r = new Random();

				double f = r.nextDouble();

				double hAngreifer = pAngreifer;
				double hSammler = hAngreifer + pSammler;
				double hWache = hSammler + pWache;
				double hPionier = hWache + pPionier;

				order = Funktionen.Angreifer.toString();
				if (f > hAngreifer) {
					order = Funktionen.Sammler.toString();
				}
				if (f > hSammler) {
					order = Funktionen.Wache.toString();
				}
				if (f > hWache) {
					order = Funktionen.Pionier.toString();
				}
				if (f > hPionier) {
					order = Funktionen.Wache.toString();
				}

			}

			if (order.equals(Funktionen.Pionier.toString())) {

				List<int[]> nahrungInSicht = getNahrungInSicht(a);
				if (nahrungInSicht.size() == 0) {
					berechneZufaelligenLauf(a);
				} else {
					if (gesammelt == 0 && stuetzpunkte.size() == 1) {

					}
					laufeRichtungNahrung(a, nahrungInSicht);
				}

			} else if (order.equals(Funktionen.Soldat.toString())) {
				if (a.getLetzteLaufrichtung() == ELaufRichtung.Stehen && ziel.equals("-")) {
					ziel = getIntArray2WegString(stuetzpunkte.get(0).getWegeMap()
							.get(stuetzpunkte.get(0).getWegeMap().size()).getWeg());
				} else if (a.getLetzteLaufrichtung() != ELaufRichtung.Stehen) {
					ziel = ziel.substring(1, ziel.length());
				} else {
					Random r = new Random();
					if (r.nextDouble() < 0.05) {
						order = Funktionen.Wache.toString();
					}
				}

				if (ziel.length() > 0) {
					a.setLaufRichtung(charToLaufRichtung(ziel.charAt(0)));
				} else {
					order = Funktionen.Wache.toString();
				}
			} else if (order.equals(Funktionen.Sammler.toString())) {
				List<int[]> nahrungInSicht = getNahrungInSicht(a);
				if (nahrungInSicht.size() == 0) {
					berechneZufaelligenLauf(a);
				} else {
					laufeRichtungNahrung(a, nahrungInSicht);
				}
			} else if (order.equals(Funktionen.Angreifer.toString())) {
				List<int[]> feindInSicht = getFeindInSicht(a);
				if (feindInSicht.size() == 0) {
					berechneZufaelligenLauf(a);
				} else {
					laufeRichtungFeind(a, feindInSicht);
					if (stuetzpunkte.size() == 2) {
						Stuetzpunkt s = new Stuetzpunkt(2, new int[] { a.getX(), a.getY() });
						s.addStuetzpunkt(0, getWegRueckwaerts(wege[a.getY()][a.getX()]));
						stuetzpunkte.add(s);
						stuetzpunkte.get(0).addStuetzpunkt(2, wege[a.getY()][a.getX()]);
					}
				}
			}

			String neuerTask = id + ":" + order + ":" + weg + ":" + ziel + ":" + start;
			a.setAufgabe(neuerTask);

		}
	}

	private void laufeRichtungFeind(ProxyAnt a, List<int[]> feindeInSicht)
	{
		if (a.getLetzteLaufrichtung() != ELaufRichtung.Stehen) {
			int[] naechsterFeind = getNaechstesZiel(a, feindeInSicht);
			laufeDirektZuAntRelKoordinate(a, naechsterFeind[0], naechsterFeind[1]);
		} else {
			berechneZufaelligenLauf(a);
		}
	}

	private List<int[]> getFeindInSicht(ProxyAnt a)
	{
		List<int[]> feindKoords = new ArrayList<>();
		for (int i = 0; i < a.getSicht().length; i++) {
			for (int j = 0; j < a.getSicht()[i].length; j++) {
				if (a.getSicht()[i][j] == 8) {
					feindKoords.add(new int[] { a.getX() - 5 + j, a.getY() - 5 + i });
				}
			}
		}
		return feindKoords;
	}

	/**
	 * Setzt die Laufrichtung der Ameise in Richtung Nahrung
	 * 
	 * @param a
	 * @param nahrungsstuecke
	 */
	private void laufeRichtungNahrung(ProxyAnt a, List<int[]> nahrungsstuecke)
	{

		if (a.getLetzteLaufrichtung() != ELaufRichtung.Stehen) {
			int[] naechstesNahrungsstueck = getNaechstesZiel(a, nahrungsstuecke);
			laufeDirektZuAntRelKoordinate(a, naechstesNahrungsstueck[0], naechstesNahrungsstueck[1]);
		} else {
			berechneZufaelligenLauf(a);
		}
	}

	/**
	 * Setzt die Laufrichtung der Ameise in Richtung der gegebenen Koordinaten
	 * 
	 * @param a
	 * @param dx
	 * @param dy
	 */
	private void laufeDirektZuAntRelKoordinate(ProxyAnt a, int dx, int dy)
	{
		// TODO: umgehe gezielt Ecken
		if (Math.abs(dx) > Math.abs(dy)) {
			if (dx > 0) {
				a.setLaufRichtung(ELaufRichtung.Rechts);
			} else {
				a.setLaufRichtung(ELaufRichtung.Links);
			}
		} else {
			if (dy > 0) {
				a.setLaufRichtung(ELaufRichtung.Runter);
			} else {
				a.setLaufRichtung(ELaufRichtung.Hoch);
			}
		}
	}

	/**
	 * Berechnet die Koordinaten des nächsten Nahrungsstücks zur Ameise relativ zur Ameise
	 * 
	 * @param a
	 * @param nahrungsstuecke
	 * @return
	 */
	private int[] getNaechstesZiel(ProxyAnt a, List<int[]> ziele)
	{
		int minDist = 11;
		int[] ziel = null;
		for (int[] z : ziele) {
			int[] koords = welt2AntKoords(z, a);
			int dist = Math.abs(koords[0]) + Math.abs(koords[1]);
			if (dist < minDist) {
				ziel = koords;
			}
		}

		return ziel;
	}

	/**
	 * Bestimmt eine zufällige Laufrichtung der Ameise
	 * 
	 * @param a
	 */
	private void berechneZufaelligenLauf(ProxyAnt a)
	{

		if (a.getLetzteLaufrichtung() == ELaufRichtung.Stehen) {
			setRandomLaufRichtung(a);
		} else {
			if (hatWandVorSich(a)) {
				setRandomLaufRichtung(a);
			} else {
				a.setLaufRichtung(a.getLetzteLaufrichtung());
			}
		}

	}

	/**
	 * Berechnet, ob die Ameise bei gleicher Laufrichtung wie im letzten Zug, gegen eine Wand läuft
	 * 
	 * @param a
	 * @return
	 */
	private boolean hatWandVorSich(ProxyAnt a)
	{
		if (a.getLetzteLaufrichtung() == ELaufRichtung.Rechts && a.getSicht()[5][6] == 0) {
			return true;
		}
		if (a.getLetzteLaufrichtung() == ELaufRichtung.Runter && a.getSicht()[6][5] == 0) {
			return true;
		}
		if (a.getLetzteLaufrichtung() == ELaufRichtung.Links && a.getSicht()[5][4] == 0) {
			return true;
		}
		if (a.getLetzteLaufrichtung() == ELaufRichtung.Hoch && a.getSicht()[4][5] == 0) {
			return true;
		}
		return false;
	}

	/**
	 * Setzt die Laufrichtung der Ameise zufällig so, dass der nächste Schritt nicht blockiert wird.
	 * 
	 * @param a
	 */
	private void setRandomLaufRichtung(ProxyAnt a)
	{

		boolean ok = false;
		Random r = new Random();

		while (ok == false) {
			int dir = r.nextInt(4);

			if (dir == 0 && a.getSicht()[5][6] != 0) {
				a.setLaufRichtung(ELaufRichtung.Rechts);
				ok = true;
			}
			if (dir == 1 && a.getSicht()[6][5] != 0) {
				a.setLaufRichtung(ELaufRichtung.Runter);
				ok = true;
			}
			if (dir == 2 && a.getSicht()[5][4] != 0) {
				a.setLaufRichtung(ELaufRichtung.Links);
				ok = true;
			}
			if (dir == 3 && a.getSicht()[4][5] != 0) {
				a.setLaufRichtung(ELaufRichtung.Hoch);
				ok = true;
			}

		}

	}

	/**
	 * Sucht die Umgebung der Ameise nach Nahrung ab und gibt die Koordinaten von Nahrung in der Nähe relativ zur Welt
	 * zurück.
	 * 
	 * @param a
	 * @return
	 */
	private List<int[]> getNahrungInSicht(ProxyAnt a)
	{
		List<int[]> nahrungsKoords = new ArrayList<>();
		for (int i = 0; i < a.getSicht().length; i++) {
			for (int j = 0; j < a.getSicht()[i].length; j++) {
				if (a.getSicht()[i][j] == 6) {
					nahrungsKoords.add(new int[] { a.getX() - 5 + j, a.getY() - 5 + i });
				}
			}
		}
		return nahrungsKoords;
	}

	/**
	 * Wandelt Weltkoordinaten in Ameisenkoordinaten um.
	 * 
	 * @param weltKoords
	 * @return
	 */
	private int[] welt2AntKoords(int[] weltKoords, ProxyAnt a)
	{
		return new int[] { weltKoords[0] - a.getX(), weltKoords[1] - a.getY() };
	}

	private ELaufRichtung charToLaufRichtung(char charAt)
	{
		if (charAt == '0') {
			return ELaufRichtung.Rechts;
		}
		if (charAt == '1') {
			return ELaufRichtung.Runter;
		}
		if (charAt == '2') {
			return ELaufRichtung.Links;
		}
		if (charAt == '3') {
			return ELaufRichtung.Hoch;
		}

		return ELaufRichtung.Stehen;
	}

	private String getIntArray2WegString(int[] weg)
	{
		String s = "";
		for (int i = 0; i < weg.length; i++) {
			s += weg[i];
		}
		return s;
	}

	/**
	 * Gibt die letzte Laufrichtung der Ameise als String zurück. 0 = Rechts, 1 = Runter, 2 = Links, 3 = Hoch,
	 * 
	 * @param a
	 * @return
	 */
	private String getLetzteLaufrichtung(ProxyAnt a)
	{
		if (a.getLetzteLaufrichtung() != null) {
			switch (a.getLetzteLaufrichtung()) {
			case Rechts:
				return "0";

			case Runter:
				return "1";

			case Links:
				return "2";

			case Hoch:
				return "3";

			default:
				return "";
			}
		}
		return "";
	}

	private WegBeschreibung getWegRueckwaerts(WegBeschreibung weg)
	{

		WegBeschreibung neu = new WegBeschreibung();

		int[] neuerWeg = new int[weg.getWeg().length];
		for (int i = 0; i < neuerWeg.length; i++) {
			neuerWeg[i] = (weg.getWeg()[i] + 2) % 4;
		}
		neu.neuerWeg(neuerWeg, new int[] { 0, 0 });

		return neu;
	}

	private void printFixeWelt()
	{
		String s = "";

		for (int i = 0; i < fixeWelt.length; i++) {
			for (int j = 0; j < fixeWelt[i].length; j++) {
				if (fixeWelt[i][j] != -1) {
					s += fixeWelt[i][j] + " ";
				} else {
					s += "X ";
				}
			}
			s += "\n";
		}

		System.out.println(s);
	}

	private void printSichtbareWelt(int[][] sichtbareWelt)
	{

		String s = "";

		for (int i = 0; i < sichtbareWelt.length; i++) {
			for (int j = 0; j < sichtbareWelt[i].length; j++) {
				s += sichtbareWelt[i][j] + " ";
			}
			s += "\n";
		}

		System.out.println(s);

	}

	private void printGeruchsWelt()
	{
		String s = "";
		for (int i = 0; i < geruchsWelt.length; i++) {
			for (int j = 0; j < geruchsWelt[i].length; j++) {
				s += geruchsWelt[i][j] + " ";
			}
			s += "\n";
		}

		System.out.println(s);

	}

	@Override
	public List<int[]> getMarker()
	{
		List<int[]> marker = new ArrayList<>();
		System.out.println("sp:");
		for (Stuetzpunkt s : stuetzpunkte) {
			marker.add(s.getPos());
			String string = "";
			for (int i = 0; i < s.getPos().length; i++) {
				string += s.getPos()[i] + ",";
			}
			System.out.println(string);
		}
		return marker;
	}

	class Stuetzpunkt
	{

		private int[] pos;
		private int id;
		private HashMap<Integer, WegBeschreibung> wegeMap;

		public Stuetzpunkt(int id, int[] pos)
		{
			this.pos = pos;
			this.id = id;
			this.wegeMap = new HashMap<Integer, WegBeschreibung>();
		}

		public void addStuetzpunkt(int neueID, WegBeschreibung weg)
		{
			this.wegeMap.put(neueID, weg);
		}

		public int getId()
		{
			return id;
		}

		public HashMap<Integer, WegBeschreibung> getWegeMap()
		{
			return wegeMap;
		}

		public int[] getPos()
		{
			return pos;
		}

	}

	class WegBeschreibung
	{

		private int[] weg;

		public WegBeschreibung(int[] weg)
		{
			this.weg = weg;
		}

		public WegBeschreibung()
		{
		}

		public void neuerWeg(int[] neu, int[] restweg)
		{

			int[] gesNeuerWeg = new int[neu.length + Math.abs(restweg[0]) + Math.abs(restweg[1])];
			for (int i = 0; i < neu.length; i++) {
				gesNeuerWeg[i] = neu[i];
			}
			int index = neu.length;
			while (Math.abs(restweg[0]) > 0 || Math.abs(restweg[1]) > 0) {
				if (Math.abs(restweg[0]) >= Math.abs(restweg[1])) {
					if (restweg[0] > 0) {
						gesNeuerWeg[index] = 0;
						restweg[0]--;
					} else {
						gesNeuerWeg[index] = 2;
						restweg[0]++;
					}
				} else {
					if (restweg[1] > 0) {
						gesNeuerWeg[index] = 1;
						restweg[1]--;
					} else {
						gesNeuerWeg[index] = 3;
						restweg[1]++;
					}
				}
				index++;
			}

			if (weg != null) {
				if (neu.length < weg.length) {
					weg = gesNeuerWeg;
				}
			} else {
				weg = gesNeuerWeg;
			}

		}

		public int[] getWeg()
		{
			return weg;
		}

	}

	enum Funktionen
	{
		Pionier, Soldat, Wache, Sammler, Angreifer;
	}

}
