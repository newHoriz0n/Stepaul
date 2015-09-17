package game;

import java.util.List;

public abstract class Spieler
{

	private boolean requestNeueAmeise;

	public Spieler()
	{
		this.requestNeueAmeise = false;
	}

	protected void neueAmeiseAnforden()
	{
		this.requestNeueAmeise = true;
	}

	public void resetAnforderung()
	{
		this.requestNeueAmeise = false;
	}

	public boolean willNeueAmeise()
	{
		return requestNeueAmeise;
	}

	/**
	 * 
	 * Wird jede Spielrunde vom Master aufgerufen und die, an die ProxyAnt aus antlist per setCommando gegebenen
	 * Laufkommandos, ausgef�hrt. Die hier unter task gegebenen Befehle werden nicht ausgelesen, bieten allerdings die
	 * M�glichkeit, spezifische Informationen der Ameise zu speichern. Wird this.neueAmeiseAnfordern() aufgerufen, wird
	 * vom Master in der n�chsten Runde eine neue Ameise auf dem eigenen Ameisenhaufen erzeugt, falls er frei ist.
	 * 
	 * @param antlist
	 *           : Liste der eigenen Ameisen
	 * @param nahrungsVorrat
	 *           : Anzahl der im Ameisenhaufen gesammelten Nahrungsst�cke
	 * @param sichtbareWelt
	 *           : Array, in dem die aktuelle Sicht aller eigenen Ameisen gespeichert ist. -1 = Keine Information, 0 =
	 *           Wand, 1 = Frei, 2 - 5 = Ameisenhaufen, 6 = Nahrung, 7 = Freund, 8 = Feind.
	 * 
	 * 
	 */
	public abstract void updateKI(List<ProxyAnt> antlist, int nahrungsVorrat, int[][] sichtbareWelt);

	/**
	 * zur�ckgegebene marker werden in der Karte angezeigt.
	 * 
	 * @return
	 */
	public abstract List<int []> getMarker();
	
}
