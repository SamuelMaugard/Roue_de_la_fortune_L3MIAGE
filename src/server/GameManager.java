package server;

import com.corundumstudio.socketio.SocketIOClient;

import core.mot.*;
import core.roue.*;
import core.roue.Case;
import core.roue.Roue;
import joueur.Joueur;
import java.util.ArrayList;
import core.TimeOut;
import core.ConsoleColors;

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
    public TimeOut timer;
	
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
		gainPotentiel = Integer.parseInt(rouefinale.lancerRoue().getValeur().getNom());
		System.out.println(gainPotentiel);
		resetPhrase();
		lettresFinale();
		System.out.println(gagnant()+" va en finale");
		server.getSocketServeur().getBroadcastOperations().sendEvent("finale",phrase.toString(),gagnant()); 
		// TODO demander la reponse au joueur (temps limit�)
	}
	
	public void lettresFinale() {
		phrase.remplacerLettre('r');
		phrase.remplacerLettre('s');
		phrase.remplacerLettre('t');
		phrase.remplacerLettre('l');
		phrase.remplacerLettre('n');
		phrase.remplacerLettre('e');
	}
	
	public String gagnant() {
		if(joueurs.get(0).getGainTotal() > joueurs.get(1).getGainTotal()) {
			return joueurs.get(0).getNom();
		}
		return joueurs.get(1).getNom();
	}
	
	private void mancheRapide() {
		System.out.println("--------- Manche "+nbManche+" --------");
		System.out.println("manche rapide : ");
		System.out.println("Vous devez trouver le plus rapidement la phrase suivante :\n");
		System.out.println(phrase.toString());
		server.getSocketServeur().getBroadcastOperations().sendEvent("manche_rapide",phrase.toString(),nbManche+"");
		timer = new TimeOut(2, this, "MancheRapide");

	}

	public void updateMancheRapide() {
		if(estTrouve==false) {

			phrase.decouvreUneLettre();

			System.out.println(phrase.toString());
			server.getSocketServeur().getBroadcastOperations().sendEvent("maj_manche_rapide",phrase.toString());
			timer  = new TimeOut(2, this, "MancheRapide");
		}
	}

	public void mancheLongue() {
		timer.getTimer().purge();
		resetPhrase();
		System.out.println("manche longue : ");
		
		tourJoueur();
	}
	
	public void tourJoueur() {
		if(phrase.resteConsones() && phrase.resteVoyelle()) {
			Case c = roue.lancerRoue();
			int effet = effetCase(c);
			if(effet==-1) {
				tourJoueur();
			}
			else {
				gainPotentiel=effet;
				server.getSocketServeur().getBroadcastOperations().sendEvent("choix_joueur",premierJoueur,phrase.toString());

			}
		}
		else if(phrase.resteConsones() && !phrase.resteVoyelle()) {
			Case c = roue.lancerRoue();
			int effet = effetCase(c);
			if(effet==-1) {
				tourJoueur();
			}
			else {
				gainPotentiel=effet;
				server.getSocketServeur().getBroadcastOperations().sendEvent("choix_cons_rep",premierJoueur,phrase.toString());

			}
		}
		else if(!phrase.resteConsones() && phrase.resteVoyelle()) {
			server.getSocketServeur().getBroadcastOperations().sendEvent("choix_voy_rep",premierJoueur,phrase.toString());

		}
		else {
			server.getSocketServeur().getBroadcastOperations().sendEvent("choix_rep",premierJoueur,phrase.toString());

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
	
	public boolean getEstTrouve() {
		return estTrouve;
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
				for(SocketIOClient s : server.getSocketServeur().getAllClients()) {
					deletePlayerBySocket(s);
					s.disconnect();
				}
				resetGame();
			}
		}
	}
	
	public ArrayList<Joueur> getJoueurs(){
		return joueurs;
	}

	public void finDeFinale() {
		timer.getTimer().purge();
		server.getSocketServeur().getBroadcastOperations().sendEvent("fin_finale",gagnant(),getJoueur(gagnant()).getGainTotal()+"");
	}

	public void setTimerFinale() {
		timer = new TimeOut(30,this,"Finale");
	}

	public void stopTimerFinale() {
		timer.getTimer().cancel();
	}
	
	public void resetGame() {
		gainPotentiel=0;
		nbManche=1;
		estTrouve=false;
		premierJoueur="";
		liste = new ListePhrase();
		phrase = new Phrase(liste.getPhrase());
		timer.getTimer().cancel();
	}


}
