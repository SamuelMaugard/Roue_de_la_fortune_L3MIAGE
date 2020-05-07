package serveur;

import core.mot.*;
import joueur.Joueur;

import java.util.ArrayList;

public class GameManager {

	private ArrayList<Joueur> joueurs;
	private Phrase phrase;
	private ListePhrase liste;
	private Serveur server;
	private boolean estTrouve;
	
	public GameManager(Serveur serveur) {
		this.server = serveur;
		this.joueurs = new ArrayList<Joueur>();
		liste = new ListePhrase();
		phrase = new Phrase(/*liste.getPhrase()*/"PHRASE A TROUVER");
		estTrouve = false;
	}
	
	public void addJoueur(Joueur joueur) {
		joueurs.add(joueur);
	}

	public void newGame() throws InterruptedException {
		System.out.println("\n Le bon nombre de personne est présent \n");
		System.out.println("La partie commence\n");
		
		
			System.out.println("-------- La manche 1 commence-------\n");
			mancheRapide();
			mancheLongue();
	}

	private void mancheRapide() throws InterruptedException {
		System.out.println("manche rapide : ");
		System.out.println("Vous devez trouver le plus rapidement la phrase suivante :\n");
		System.out.println(phrase.toString());
		server.getSocketServeur().getBroadcastOperations().sendEvent("manche_rapide",phrase.toString());
		while(estTrouve==false) {
			Thread.sleep(5000);
			phrase.decouvreUneLettre();
			System.out.println(phrase.toString());
			server.getSocketServeur().getBroadcastOperations().sendEvent("maj_manche_rapide",phrase.toString());
		}
	}

	private void mancheLongue() {
		System.out.println("manche longue : ");
		// TODO a faire manche longue
	}
	
	public Phrase getPhrase() {
		return phrase;
	}
	
	public void setEstTrouve(boolean b) {
		estTrouve=b;
	}

	public int getNumberplayers() {
		return joueurs.size();
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
