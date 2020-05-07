package serveur;

import core.TimeOut;
import core.mot.*;
import joueur.Joueur;
import java.util.ArrayList;

public class GameManager {

	private ArrayList<Joueur> joueurs;
	private Phrase phrase;
	private ListePhrase liste;
	private Serveur server;
	private String premierJoueur;
	private boolean estTrouve;
	
	public GameManager(Serveur serveur) {
		this.server = serveur;
		this.joueurs = new ArrayList<Joueur>();
		liste = new ListePhrase();
		phrase = new Phrase(liste.getPhrase());
		premierJoueur = "";
		estTrouve = false;
	}
	
	public void addJoueur(Joueur joueur) {
		joueurs.add(joueur);
	}

	public void newGame() throws InterruptedException {
		System.out.println("\nLe bon nombre de personne est présent\n");
		System.out.println("La partie commence\n");
		manche();
		finale();
	}

	public void manche() {
		mancheRapide();
	}
	
	public void finale() {
		
	}
	
	private void mancheRapide() {
		System.out.println("manche rapide : ");
		System.out.println("Vous devez trouver le plus rapidement la phrase suivante :\n");
		System.out.println(phrase.toString());
		server.getSocketServeur().getBroadcastOperations().sendEvent("manche_rapide",phrase.toString());
		new TimeOut(2, this, "MancheRapide");

	}

	public void updateMancheRapide() {
		if(estTrouve==false) {

			phrase.decouvreUneLettre();

			System.out.println(phrase.toString());
			server.getSocketServeur().getBroadcastOperations().sendEvent("maj_manche_rapide",phrase.toString());
			new TimeOut(2, this, "MancheRapide");
		}
	}

	public void mancheLongue() {
		resetPhrase();
		System.out.println("manche longue : ");
		// demander au joueur un choix (consonne, voyelle, reponse)
		server.getSocketServeur().getBroadcastOperations().sendEvent("choix_joueur",premierJoueur);
		choixJoueur();
		
		// si le joueur c'est tromper on passe à l'autre
	}
	
	public void choixJoueur() {
		if(getJoueur(premierJoueur).getChoixAction().equals("")) {
			new TimeOut(5,this,"ChoixJoueur");
		}
	}
	public void resetPhrase() {
		phrase.resetPhrase(liste.getPhrase());
	}
	
	public Phrase getPhrase() {
		return phrase;
	}
	
	public void setPremierJoueur(String s) {
		premierJoueur=s;
	}
	
	public void setEstTrouve(boolean b) {
		estTrouve=b;
	}
	
	public int getNumberplayers() {
		return joueurs.size();
	}
	
	public String getPremierJoueur() {
		return premierJoueur;
	}

	public Joueur getJoueur(String string) {
		if(joueurs.get(0).getNom().equals(string)) {
			return  joueurs.get(0);
		}
		else {
			return joueurs.get(1);
		}
	}
}
