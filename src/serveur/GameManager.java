package serveur;

import core.mot.*;
import joueur.Joueur;

import java.util.ArrayList;

public class GameManager {

	private ArrayList<Joueur> joueurs;
	private Phrase phrase;
	private ListePhrase liste;
	private Serveur server;
	
	public GameManager(Serveur serveur) {
		this.server=serveur;
		this.joueurs = new ArrayList<Joueur>();
		liste = new ListePhrase();
		phrase = new Phrase(/*liste.getPhrase()*/"");
	}
	
	public void addJoueur(Joueur joueur) {
		joueurs.add(joueur);
	}

	public void newGame() {
		System.out.println("\nLe jeu commence");		
	}
}
