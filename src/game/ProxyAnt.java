package game;

public class ProxyAnt
{

	private int spNr;
	private int id;
	private int x;
	private int y;
	private String task;
	private ELaufRichtung gewuenschteLaufRichtung;
	// private boolean angehalten;
	private ELaufRichtung letzteLaufrichtung;

	private int[][] sicht;

	private boolean wurdeBewegt;

	public ProxyAnt(int spNr, int id, int x, int y, String task, int[][] sicht, boolean wurdeBewegt,
			ELaufRichtung letzteLaufrichtung, boolean istAngehalten)
	{
		this.spNr = spNr;
		this.id = id;
		this.x = x;
		this.y = y;
		this.task = task;
		this.gewuenschteLaufRichtung = ELaufRichtung.Stehen;
		this.sicht = sicht;
		this.wurdeBewegt = wurdeBewegt;
		this.letzteLaufrichtung = letzteLaufrichtung;
		// this.angehalten = angehalten;
	}

	/**
	 * Setzt die GewünschteLaufrichtung
	 * 
	 * @param rechts
	 */
	public void setLaufRichtung(ELaufRichtung richtung)
	{
		this.gewuenschteLaufRichtung = richtung;
	}

	/**
	 * Methode zur Steurung der Ameise
	 * 
	 * @param acs
	 */
	public void setCommando(AmeisenCommandoSet acs)
	{
		this.task = acs.getTask();
		if (acs.getGewuenschteLaufRichtung() != ELaufRichtung.Stehen) {
			this.gewuenschteLaufRichtung = acs.getGewuenschteLaufRichtung();
			// this.angehalten = false;
		} else {
			// angehalten = true;
		}
	}

	/**
	 * Setzt die Aufgabe der Ameise
	 * 
	 * @param task
	 */
	public void setAufgabe(String task2)
	{
		this.task = task2;
	}

	/**
	 * Gibt eine eindeutige ID der Ameise zurueck
	 * 
	 * @return
	 */
	public int getID()
	{
		return id;
	}

	/**
	 * Gibt die x-Position der Ameise zurueck.
	 * 
	 * @return
	 */
	public int getX()
	{
		return x;
	}

	/**
	 * Gibt die y-Position der Ameise zurueck.
	 * 
	 * @return
	 */
	public int getY()
	{
		return y;
	}

	/**
	 * Gibt die vom Spieler eingegebene Aufgabe zurueck.
	 * 
	 * @return
	 */
	public String getTask()
	{
		return task;
	}

	/**
	 * Gibt die vom Spieler eingegebene Gewuenschte Laufrichtung zurueck.
	 * 
	 * @return
	 */
	public ELaufRichtung getGewuenschteLaufRichtung()
	{
		return gewuenschteLaufRichtung;
	}

	/**
	 * Gibt die Laufrichtung des letzten Zuges zurueck.
	 * 
	 * @return
	 */
	public ELaufRichtung getLetzteLaufrichtung()
	{
		return letzteLaufrichtung;
	}

	/**
	 * Gibt die Nummer des Spielers zurueck, der die Ameise zugeordnet ist.
	 * 
	 * @return
	 */
	public int getSpNr()
	{
		return spNr;
	}

	/**
	 * Gibt die Sicht radial im Umkreis von 5 Feldern der Ameise zurück. Die Sicht wird für die Ameise als int [][]
	 * abgespeichert. Jedes Feld der Matrix repräsentiert ein Feld um die Ameise herum. Dabei ist array [5][5] der
	 * Standpunkt der Ameise. Der eingetragene Wert für jedes Feld gibt das Objekt auf diesem Feld an. -1 = Keine
	 * Information, 0 = Wand, 1 = Frei, 2 - 5 = Ameisenhaufen, 6 = Nahrung, 7 = Freund, 8 = Feind.
	 * 
	 * @param sicht
	 */
	public int[][] getSicht()
	{
		return sicht;
	}

	public boolean wurdeBewegt()
	{
		return wurdeBewegt;
	}

}
