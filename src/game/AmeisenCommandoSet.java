package game;

public class AmeisenCommandoSet {

	private ELaufRichtung gewuenschteLaufRichtung;
	private String task;

	public AmeisenCommandoSet(ELaufRichtung zielRichtung, String task) {
		this.gewuenschteLaufRichtung = zielRichtung;
		this.task = task;
	}

	public ELaufRichtung getGewuenschteLaufRichtung() {
		return gewuenschteLaufRichtung;
	}

	public String getTask() {
		return task;
	}

}
