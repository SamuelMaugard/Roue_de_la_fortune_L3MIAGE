package serveur;

import com.corundumstudio.socketio.SocketIOClient;
import core.ConsoleColors;
import core.TimeOut;
import core.mot.*;
import core.roue.Case;
import core.roue.Roue;
import joueur.Joueur;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class GameManager {

	private ArrayList<Joueur> joueurs;
	private Phrase phrase;
	private ListePhrase liste;
	private Serveur server;
	private String premierJoueur;
	private Roue roue;
	private Roue rouefinale;
	private boolean estTrouve;
	private int gainPotentiel;
	private int nbManche;
	
	public GameManager(Serveur serveur) {
		this.server = serveur;
		this.joueurs = new ArrayList<Joueur>();
		liste = new ListePhrase();
		phrase = new Phrase(liste.getPhrase());
		premierJoueur = "";
		estTrouve = false;
		nbManche=1;
		roue = new Roue(true);
		rouefinale = new Roue(false);
	}
	
	public void addJoueur(Joueur joueur) {
		joueurs.add(joueur);
	}

	public void newGame() throws InterruptedException {
		System.out.println("\nLe bon nombre de personne est présent\n");
		System.out.println("La partie commence\n");
		manche();
	}

	public void manche() {
		mancheRapide();
	}
	
	public void finale() {
		System.out.println("on est en finale");
	}
	
	private void mancheRapide() {
		System.out.println("--------- Manche "+nbManche+" --------");
		System.out.println("manche rapide : ");
		System.out.println("Vous devez trouver le plus rapidement la phrase suivante :\n");
		System.out.println(phrase.toString());
		server.getSocketServeur().getBroadcastOperations().sendEvent("manche_rapide",phrase.toString(),nbManche+"");
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
		
		tourJoueur();
		
		// si le joueur c'est tromper on passe à l'autre
	}
	
	public void tourJoueur() {
		Case c = roue.lancerRoue();
		int effet = effetCase(c);
		if(effet==-1) {
			tourJoueur();
		}
		else {
			gainPotentiel=effet;
			server.getSocketServeur().getBroadcastOperations().sendEvent("choix_joueur",premierJoueur,phrase.toString());
			choixJoueur();
		}
	}

	private Integer effetCase(Case c) {
		switch(c.getValeur().name()) {
			case "banqueroute":
				server.getSocketServeur().getBroadcastOperations().sendEvent("banqueroute",premierJoueur);
				getJoueur(premierJoueur).setGainManche(0);
				premierJoueur = joueurAdverse().getNom();
				return -1;
			case "passe":
				server.getSocketServeur().getBroadcastOperations().sendEvent("passe",premierJoueur);
				premierJoueur = joueurAdverse().getNom();
				return -1;
			case "holdUp":
				server.getSocketServeur().getBroadcastOperations().sendEvent("holdUp",premierJoueur,joueurAdverse().getGainManche()+"");
				return joueurAdverse().getGainManche();
			default:
				server.getSocketServeur().getBroadcastOperations().sendEvent("gain",premierJoueur,c.getValeur().getNom());
				return Integer.parseInt(c.getValeur().getNom());
		}
	}

	public Joueur joueurAdverse() {
		if(premierJoueur.equals(joueurs.get(0).getNom()))
			return joueurs.get(1);
		return joueurs.get(0);
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

	public Roue getRoue() {
		return roue;
	}
	
	public int getGainPotentiel() {
		return gainPotentiel;
	}
	
	public ListePhrase getListe() {
		return liste;
	}
	
	public Joueur getJoueur(String string) {
		if(joueurs.get(0).getNom().equals(string)) {
			return  joueurs.get(0);
		}
		else {
			return joueurs.get(1);
		}
	}
	
	public int getNbManche() {
		return nbManche;
	}
	
	public void incrementNbManche() {
		nbManche++;
	}

	public void deletePlayerBySocket(SocketIOClient so) {
		for(int i=0; i<joueurs.size();i++) {
			if(joueurs.get(i).cl == so) {
				String n = joueurs.get(i).getNom();
				joueurs.remove(i);
				System.out.println(ConsoleColors.GREEN + "Déconnexion de " + n + " ("+joueurs.size()+"/2)" + ConsoleColors.RESET);
			}
		}
	}
	
	public ArrayList<Joueur> getJoueurs(){
		return joueurs;
	}
}
