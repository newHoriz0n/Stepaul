package game;

public class Ant
{

	private int x;
	private int y;

	private int lastX;
	private int lastY;

	private boolean moved;
	private boolean bewegbar;

	private int SpNr;
	private int id;
	private String task;
	private ELaufRichtung gewuenschteLaufRichtung;
	private boolean angehalten;
	private ELaufRichtung letzteLaufrichtung;

	private int[][] sicht;

	private boolean stirbt;

	public Ant(int spNr, int id, int x, int y, String task)
	{
		this.x = x;
		this.y = y;
		this.lastX = x;
		this.lastY = y;
		this.task = task;
		this.SpNr = spNr;
		this.id = id;
		this.moved = false;
		this.letzteLaufrichtung = ELaufRichtung.Stehen;
		this.angehalten = false;
	}

	public int getID()
	{
		return this.id;
	}

	public void sterben()
	{
		this.stirbt = true;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public int getSpNr()
	{
		return SpNr;
	}

	public boolean stirbt()
	{
		return this.stirbt;
	}

	public String getTask()
	{
		return task;
	}

	public ELaufRichtung getGewuenschteLaufRichtung()
	{
		return gewuenschteLaufRichtung;
	}

	public void ueberTrageKommando(AmeisenCommandoSet acs)
	{

		this.task = acs.getTask();
		if (acs.getGewuenschteLaufRichtung() != ELaufRichtung.Stehen) {
			this.gewuenschteLaufRichtung = acs.getGewuenschteLaufRichtung();
			this.angehalten = false;
		} else {
			angehalten = true;
		}

	}

	public void setSicht(int[][] sicht)
	{
		this.sicht = sicht;
	}

	/**
	 * Gibt die Sicht radial im Umkreis von 5 Feldern der Ameise zurück. Die Sicht wird für die Ameise als int [][]
	 * abgespeichert. Jedes Feld der Matrix repräsentiert ein Feld um die Ameise herum. Dabei ist array [5][5] der
	 * Standpunkt der Ameise. Der eingetragene Wert für jedes Feld gibt das Objekt auf diesem Feld an. -1 = Keine
	 * Information, 0 = Wand, 1 = Frei, 2 - 5 = Ameisenhaufen, 6 = Nahrung, 7 = Freund, 8 = Feind.
	 */
	public int[][] getSicht()
	{
		return this.sicht;
	}

	public void move()
	{

		if (angehalten == false && bewegbar == true) {

			this.lastX = x;
			this.lastY = y;

			this.letzteLaufrichtung = gewuenschteLaufRichtung;

			if (gewuenschteLaufRichtung == ELaufRichtung.Hoch) {
				this.y = y - 1;
				this.moved = true;
			}
			if (gewuenschteLaufRichtung == ELaufRichtung.Rechts) {
				this.x = x + 1;
				this.moved = true;
			}
			if (gewuenschteLaufRichtung == ELaufRichtung.Runter) {
				this.y = y + 1;
				this.moved = true;
			}
			if (gewuenschteLaufRichtung == ELaufRichtung.Links) {
				this.x = x - 1;
				this.moved = true;
			}
		} else if (angehalten == false && bewegbar == false) {
			this.moved = true;
			this.letzteLaufrichtung = ELaufRichtung.Stehen;
		}

	}

	public void setGewuenschteLaufRichtung(ELaufRichtung elr)
	{
		this.gewuenschteLaufRichtung = elr;
	}

	public void zugRueckgaengigMachen()
	{
		// System.out.println("Ant zugRueckgaengigMachen check 1");
		// System.out.println(x);
		// System.out.println(lastX);
		// System.out.println(y);
		// System.out.println(lastY);
		this.x = lastX;
		this.y = lastY;
		this.moved = false;
	}

	public int[] getNaechstePosition()
	{

		int estX = this.x;
		int estY = this.y;

		if (gewuenschteLaufRichtung == ELaufRichtung.Hoch) {
			estY = estY - 1;
		}
		if (gewuenschteLaufRichtung == ELaufRichtung.Rechts) {
			estX = estX + 1;
		}
		if (gewuenschteLaufRichtung == ELaufRichtung.Runter) {
			estY = estY + 1;
		}
		if (gewuenschteLaufRichtung == ELaufRichtung.Links) {
			estX = estX - 1;
		}

		return new int[] { estX, estY };

	}

	public ELaufRichtung getLetzteLaufrichtung()
	{
		return letzteLaufrichtung;
	}

	public boolean wurdeBewegt()
	{
		return moved;
	}

	public void setBewegbar(boolean b)
	{
		this.bewegbar = b;
	}

	public boolean isBewegbar()
	{
		return bewegbar;
	}

	public boolean istAngehalten()
	{
		return angehalten;
	}

	public void vorbereiten()
	{
		this.moved = false;
		this.bewegbar = true;
		this.lastX = x;
		this.lastY = y;
		this.letzteLaufrichtung = gewuenschteLaufRichtung;
	}

}
