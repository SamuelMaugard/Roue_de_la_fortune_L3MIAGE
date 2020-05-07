package joueur;

public class Joueur {

	private int gainManche;
	private String nom;
	private int gainTotal;
	
	public Joueur(String nom) {
		setGainManche(0);
		this.setNom(nom);
		setGainTotal(0);
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public int getGainManche() {
		return gainManche;
	}

	public void setGainManche(int gainManche) {
		this.gainManche = gainManche;
	}

	public int getGainTotal() {
		return gainTotal;
	}

	public void setGainTotal(int gainTotal) {
		this.gainTotal = gainTotal;
	}
}
