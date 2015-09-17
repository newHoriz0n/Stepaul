package game;

import java.awt.PageAttributes.OriginType;
import java.util.ArrayList;
import java.util.List;

public class SpielerInfo
{

	private int nahrungsVorrat;
	private List<Ant> ameisenOriginal;
	private List<ProxyAnt> ameisenProxy;

	private int[][] sichtbareWelt;

	private int erzeugteAmeisen;

	public SpielerInfo(int startNahrung)
	{
		this.nahrungsVorrat = startNahrung;
		this.ameisenOriginal = new ArrayList<Ant>();
		this.ameisenProxy = new ArrayList<ProxyAnt>();
		this.erzeugteAmeisen = 0;
		this.sichtbareWelt = new int[20][40];
		for (int i = 0; i < sichtbareWelt.length; i++) {
			for (int j = 0; j < sichtbareWelt[i].length; j++) {
				sichtbareWelt[i][j] = -1;
			}
		}
	}

	public int getNahrungsVorrat()
	{
		return this.nahrungsVorrat;
	}

	public void gewinneNahrung()
	{
		this.nahrungsVorrat++;
	}

	public void verbraucheNahrung()
	{
		this.nahrungsVorrat--;
	}

	public List<Ant> getOriginalAmeisen()
	{
		return ameisenOriginal;
	}

	public List<ProxyAnt> getProxyAmeisen()
	{

		List<ProxyAnt> proxyAmeisen = new ArrayList<ProxyAnt>();
		for (int i = 0; i < ameisenOriginal.size(); i++) {
			proxyAmeisen.add(new ProxyAnt(ameisenOriginal.get(i).getSpNr(), ameisenOriginal.get(i).getID(),
					ameisenOriginal.get(i).getX(), ameisenOriginal.get(i).getY(), ameisenOriginal.get(i).getTask(),
					ameisenOriginal.get(i).getSicht(), ameisenOriginal.get(i).wurdeBewegt(), ameisenOriginal.get(i)
							.getLetzteLaufrichtung(), ameisenOriginal.get(i).istAngehalten()));
		}
		this.ameisenProxy = proxyAmeisen;
		return this.ameisenProxy;
	}

	public void erzeugeAmeise(int spNr, int x, int y, String task)
	{
		ameisenOriginal.add(new Ant(spNr, erzeugteAmeisen, x, y, task));
		erzeugteAmeisen++;
		verbraucheNahrung();
	}

	public void ameiseVernichten(int id)
	{
		ameisenOriginal.remove(id);
	}

	public void speicherSicht(int[][] sichtbareWelt)
	{
		this.sichtbareWelt = sichtbareWelt;
	}

	public int[][] getSichtbareWelt()
	{
		return sichtbareWelt;
	}

	public void uebertrageCommandos()
	{
		if (ameisenOriginal.size() == ameisenProxy.size()) {
			for (int i = 0; i < ameisenOriginal.size(); i++) {
				ameisenOriginal.get(i).ueberTrageKommando(
						new AmeisenCommandoSet(ameisenProxy.get(i).getGewuenschteLaufRichtung(), ameisenProxy.get(i)
								.getTask()));
			}
		} else {
			throw new IllegalStateException();
		}
	}

	public boolean startFeldIstFrei(int startX, int startY)
	{

		boolean frei = true;

		for (int i = 0; i < ameisenOriginal.size(); i++) {
			if (ameisenOriginal.get(i).getX() == startX && ameisenOriginal.get(i).getY() == startY) {
				frei = false;
			}
		}

		return frei;

	}

}
