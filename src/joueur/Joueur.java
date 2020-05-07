package joueur;

import com.corundumstudio.socketio.SocketIOClient;

public class Joueur {

	private int gainManche;
	private String nom;
	private String choixAction;
	private int gainTotal;
	public SocketIOClient cl;
	
	public Joueur(String nom, SocketIOClient client) {
		setGainManche(0);
		this.setNom(nom);
		setGainTotal(0);
		setChoixAction("");
		cl = client;
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

	public String getChoixAction() {
		return choixAction;
	}

	public void setChoixAction(String choixAction) {
		this.choixAction = choixAction;
	}
}
