package kis;

import game.AmeisenCommandoSet;
import game.ELaufRichtung;
import game.ProxyAnt;
import game.Spieler;

import java.util.List;
import java.util.Random;

public class EasyBotKI extends Spieler {

	private Random r = new Random();
	
	@Override
	public void updateKI(List<ProxyAnt> proxyAnts, int nahrungsVorrat, int [][] sichtbareWelt) {
				
		this.neueAmeiseAnforden();

		for (ProxyAnt ant : proxyAnts) {

			easyBot(ant);

		}

	}

	private void easyBot(ProxyAnt ant) {


		if (ant.wurdeBewegt() == true) {
			ant.setCommando(new AmeisenCommandoSet(ant.getLetzteLaufrichtung(), ""));
		} else {
			
			int richtung = r.nextInt(4);
			
			if(richtung == 0) {
				ant.setCommando(new AmeisenCommandoSet(ELaufRichtung.Hoch, ""));				
			}
			if(richtung == 1) {
				ant.setCommando(new AmeisenCommandoSet(ELaufRichtung.Rechts, ""));				
			}
			if(richtung == 2) {
				ant.setCommando(new AmeisenCommandoSet(ELaufRichtung.Runter, ""));				
			}
			if(richtung == 3) {
				ant.setCommando(new AmeisenCommandoSet(ELaufRichtung.Links, ""));				
			}
			
		}
	}

	@Override
	public List<int[]> getMarker()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
}
