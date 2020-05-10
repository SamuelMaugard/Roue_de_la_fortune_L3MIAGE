package server;

import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;

import com.corundumstudio.socketio.listener.ExceptionListener;
import core.ConsoleColors;
import core.TimeOut;
import core.roue.Case;
import io.netty.channel.ChannelHandlerContext;
import joueur.Joueur;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class Serveur {

    private SocketIOServer server;
    private GameManager game;

    public Serveur() {
        Configuration config = new Configuration();
        config.setHostname("127.0.0.1");
        config.setExceptionListener(new ExceptionListener() {
            @Override
            public void onEventException(Exception e, List<Object> list, SocketIOClient socketIOClient) {}
            @Override
            public void onDisconnectException(Exception e, SocketIOClient socketIOClient) {}
            @Override
            public void onConnectException(Exception e, SocketIOClient socketIOClient) {}
            @Override
            public void onPingException(Exception e, SocketIOClient socketIOClient) {}
            @Override
            public boolean exceptionCaught(ChannelHandlerContext ctx, Throwable e) { return true; }
        });
        config.setPort(10101);
        SocketConfig socketConfig = config.getSocketConfig();
        socketConfig.setReuseAddress(true);
        server = new SocketIOServer(config);
        setUpEventsListeners();
        game = new GameManager(this);
        server.start();
        try { Thread.sleep(1000);
        } catch (InterruptedException e) { e.printStackTrace(); }
        dispIP();
        System.out.println("\nAttente des joueurs ...");

    }

    public void setUpEventsListeners() {
        //Connexion
        server.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient socketIOClient) {
                if(game.getJoueurs().size() >=2) {
                    socketIOClient.sendEvent("reject");
                } else {
                    System.out.println(ConsoleColors.CYAN + "Connexion de ("+socketIOClient.getRemoteAddress()+")" + ConsoleColors.RESET);
                    socketIOClient.sendEvent("pseudo");
                }
            }
        });
        //Déconnexion
        server.addDisconnectListener(new DisconnectListener() {
            @Override
            public void onDisconnect(SocketIOClient socketIOClient) {
                game.deletePlayerBySocket(socketIOClient);
            }
        });
        //Réponse pseudo
        server.addEventListener("reponse-pseudo", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient socketIOClient, String res, AckRequest ackRequest) throws InterruptedException{
                for(Joueur j :game.getJoueurs()) {
                    if(res.equals(j.getNom())) {
                        res += "-2";
                    }
                }
                socketIOClient.sendEvent("pseudo-update", res);
                game.addJoueur(new Joueur(res, socketIOClient));
                System.out.println(ConsoleColors.GREEN + res + " a rejoint la partie " +
                        "(" + game.getNumberplayers()+"/2)" + ConsoleColors.RESET
                );
                if(game.getNumberplayers() == 2) {
                    System.out.println(ConsoleColors.CYAN + "--------------------------------------" + ConsoleColors.RESET);
                	game.newGame();
                }
            }
        }); 
        // reponse manche rapide
        server.addEventListener("reponse_manche_rapide", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient socketIOClient, String rep, AckRequest ackRequest){
            	if(checkPseudo(rep.split("\t")[rep.split("\t").length-1])) {
            		socketIOClient.sendEvent("pas_ton_tour",rep.split("\t")[rep.split("\t").length-1]);
            	}
            	else {
                	reponseMancheRapide(rep,socketIOClient);
            	}
            }
        });
        
        // choixConsonne
        server.addEventListener("consonne", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient socketIOClient, String rep, AckRequest ackRequest){
            	if(!checkPseudo(rep)) {
            		System.out.println(rep);
                	game.getJoueur(game.getPremierJoueur()).setChoixAction("C");
                	getSocketServeur().getBroadcastOperations().sendEvent("consonne",game.getPremierJoueur());
            	}
            	else {
            		socketIOClient.sendEvent("pas_ton_tour",rep);
            	}
            }
        });
        // proposition consonne
        server.addEventListener("consonne_prop", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient socketIOClient, String rep, AckRequest ackRequest){
            	consonne(rep,socketIOClient);
            }
        });
        
        // choixVoyelle
        server.addEventListener("voyelle", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient socketIOClient, String rep, AckRequest ackRequest) {
            	if(!checkPseudo(rep)) {
            		if(game.getJoueur(game.getPremierJoueur()).getGainManche()<200) {
                		getSocketServeur().getBroadcastOperations().sendEvent("notbuy_voyelle"," ne peut pas acheter de voyelle il doit faire un autre choix",game.getPremierJoueur());
                		getSocketServeur().getBroadcastOperations().sendEvent("choix_joueur",game.getPremierJoueur(),game.getPhrase().toString());
                	}
                	else {
                    	game.getJoueur(game.getPremierJoueur()).setChoixAction("V");
                    	int gainManche = game.getJoueur(game.getPremierJoueur()).getGainManche()-200;
                    	game.getJoueur(game.getPremierJoueur()).setGainManche(gainManche);
                    	getSocketServeur().getBroadcastOperations().sendEvent("voyelle",game.getPremierJoueur());
                	}
            	}
            	else {
            		socketIOClient.sendEvent("pas_ton_tour",rep);
            	}
            }
        });
        // proposition voyelle
        server.addEventListener("voyelle_prop", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient socketIOClient, String rep, AckRequest ackRequest){
            	voyelle(rep,socketIOClient);
            }
        });   
        
        // choixReponse
        server.addEventListener("reponse", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient socketIOClient, String rep, AckRequest ackRequest){
            	if(!checkPseudo(rep)) {
                	game.getJoueur(game.getPremierJoueur()).setChoixAction("R");
                	getSocketServeur().getBroadcastOperations().sendEvent("reponse",game.getPremierJoueur());
            	}
            	else {
            		socketIOClient.sendEvent("pas_ton_tour",rep);
            	}
            }
        });
        // proposition reponse
        server.addEventListener("reponse_prop", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient socketIOClient, String rep, AckRequest ackRequest){
            	if(rep.equals(game.getPhrase().getPhraseJuste())) {
            		game.getJoueur(game.getPremierJoueur()).setGainTotal(game.getJoueur(game.getPremierJoueur()).getGainManche());
            		getSocketServeur().getBroadcastOperations().sendEvent("bonne_reponse",game.getPremierJoueur(),game.getJoueur(game.getPremierJoueur()).getGainTotal()+"");
            		for(Joueur j : game.getJoueurs()) {
            			j.setGainManche(0);
            		}
            		game.incrementNbManche();
            		if(game.getNbManche()>4) {
            			game.finale();
            		}
            		else {
            			game.getPhrase().resetPhrase(game.getListe().getPhrase());
            			game.setEstTrouve(false);
            			game.manche();
            		}
            	}
            	else {
            		getSocketServeur().getBroadcastOperations().sendEvent("mauvaise_reponse",game.getPremierJoueur());
            		socketIOClient.sendEvent("pas_ton_tour",game.getPremierJoueur());
            		game.setPremierJoueur(game.joueurAdverse().getNom());
            		game.tourJoueur();
            	}
            }
        });
        
        // proposition finale
        server.addEventListener("proposition_finale", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient socketIOClient, String rep, AckRequest ackRequest){
            	lettresFinales(rep,socketIOClient);
            }
        });
        // reponse finale
        server.addEventListener("reponse_finale", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient socketIOClient, String rep, AckRequest ackRequest){
            	System.out.println(rep);
            	if(rep.equals(game.getPhrase().getPhraseJuste())) {
            		game.stopTimerFinale();
            		int gainJoueur = game.getJoueur(game.gagnant()).getGainTotal();
            		gainJoueur += game.getGainPotentiel();
            		game.getJoueur(game.gagnant()).setGainTotal(gainJoueur);
            		getSocketServeur().getBroadcastOperations().sendEvent("bonne_rep_finale",game.gagnant(),gainJoueur+"");
                    server.getBroadcastOperations().sendEvent("fin_finale",game.gagnant(),game.getJoueur(game.gagnant()).getGainTotal()+"");
            	}
            	else {
            		socketIOClient.sendEvent("mauvaise_rep_finale",game.getPhrase().toString());
            	}
            }
        });
        // non pas de partie recommencez
        server.addEventListener("non", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient socketIOClient, String rep, AckRequest ackRequest){
                for(SocketIOClient s : server.getAllClients()) {
                    game.deletePlayerBySocket(s);
                    s.disconnect();
                }
            }
        });
        // oui partie 
        server.addEventListener("oui", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient socketIOClient, String rep, AckRequest ackRequest) throws InterruptedException{
            	game.resetGame();
            	game.newGame();
            }
        });
    }
    
    private void consonne(String rep, SocketIOClient socketIOClient) {
    	int nbLettre = game.getPhrase().remplacerLettre(rep.charAt(0));
    	getSocketServeur().getBroadcastOperations().sendEvent("lettre_prop",rep,game.getPremierJoueur());
    	if(nbLettre>0) {
    		int gain = game.getJoueur(game.getPremierJoueur()).getGainManche()+(nbLettre*game.getGainPotentiel());
    		game.getJoueur(game.getPremierJoueur()).setGainManche(gain);
    		String gainJ1 = "Joueur "+game.getJoueurs().get(0).getNom()+", gain : "+game.getJoueurs().get(0).getGainManche();
    		String gainJ2 = "Joueur "+game.getJoueurs().get(1).getNom()+", gain : "+game.getJoueurs().get(1).getGainManche();
    		getSocketServeur().getBroadcastOperations().sendEvent("maj_gain_phrase",gainJ1,gainJ2,game.getPremierJoueur(),game.getPhrase().toString());
    		game.tourJoueur();
    	}
    	else {
    		socketIOClient.sendEvent("pas_ton_tour",game.getPremierJoueur());
    		game.setPremierJoueur(game.joueurAdverse().getNom());
    		game.tourJoueur();
    	}
    }
    
    public void voyelle(String rep, SocketIOClient socketIOClient) {
    	int nbLettre = game.getPhrase().remplacerLettre(rep.charAt(0));
    	getSocketServeur().getBroadcastOperations().sendEvent("lettre_prop",rep,game.getPremierJoueur());
    	if(nbLettre>0) {
    		if(game.getPhrase().resteConsones() && game.getPhrase().resteVoyelle()) {
    			getSocketServeur().getBroadcastOperations().sendEvent("choix_joueur",game.getPremierJoueur(),game.getPhrase().toString());
    		}
    		else if(game.getPhrase().resteConsones() && !game.getPhrase().resteVoyelle()){
    			getSocketServeur().getBroadcastOperations().sendEvent("choix_cons_rep",game.getPremierJoueur(),game.getPhrase().toString());
    		}
    		else if(!game.getPhrase().resteConsones() && game.getPhrase().resteVoyelle()) {
    			getSocketServeur().getBroadcastOperations().sendEvent("choix_voy_rep",game.getPremierJoueur(),game.getPhrase().toString());
    		}
    		else {
    			getSocketServeur().getBroadcastOperations().sendEvent("choix_rep",game.getPremierJoueur(),game.getPhrase().toString());
    		}
    	}
    	else {
        	socketIOClient.sendEvent("pas_ton_tour",game.getPremierJoueur());
    		game.setPremierJoueur(game.joueurAdverse().getNom());
    		game.tourJoueur();
    	}
    }
    
    private void lettresFinales(String rep, SocketIOClient socketIOClient) {
		remplacerLettres(rep);
		game.setTimerFinale();
    	socketIOClient.sendEvent("reponse_finale",game.getPhrase().toString());
	}
    
    private void remplacerLettres(String rep) {
    	String[] tab = rep.split(" ");
    	for(int i=0;i<tab.length;i++) {
    		System.out.println(tab[i]);
    		game.getPhrase().remplacerLettre(tab[i].charAt(0));
    	}
	}

	private void reponseMancheRapide(String rep,SocketIOClient socketIOClient) {
    	String[] repJoueur = rep.split("\t");
        String reponse = repJoueur[0];
        if(reponse.equals(game.getPhrase().getPhraseJuste())) {
        	System.out.println(repJoueur[repJoueur.length-1]+" a trouvé la reponse");
        	game.setEstTrouve(true);
        	String infoClient = repJoueur[repJoueur.length-1]+" a trouvé la réponse";
            game.getJoueur(repJoueur[repJoueur.length-1]).setGainManche(500);
            infoClient+=" et a gagné "+game.getJoueur(repJoueur[repJoueur.length-1]).getGainManche()+" de gain";
            server.getBroadcastOperations().sendEvent("fin_manche_rapide",infoClient,repJoueur[repJoueur.length-1]);
            game.setPremierJoueur(repJoueur[repJoueur.length-1]);
            
            //Début de la manche longue
            game.mancheLongue();
        }
        else {
        	System.out.println(repJoueur[repJoueur.length-1]+" a proposé une mauvaise reponse veuillez ressayer");
            server.getBroadcastOperations().sendEvent("maj_manche_rapide", repJoueur[repJoueur.length-1]+" a proposé une mauvaise reponse veuillez ressayer");
            socketIOClient.sendEvent("mauvaise_reponse_rapide");
        }
	}
    


	private boolean checkPseudo(String rep) {
		System.out.println(rep);
		System.out.println(game.getEstTrouve() && rep.equals(game.getPremierJoueur()));
		return (game.getEstTrouve() && !rep.equals(game.getPremierJoueur()));
	}

    public void dispIP() {
        System.out.println("\n--------------------------------------");
        System.out.println("Se connecter:");
        System.out.println("LOCAL: 127.0.0.1");
        System.out.println("--------------------------------------");

    }
    
    public SocketIOServer getSocketServeur() {
    	return server;
    }

    public static void main(String[] args) {
        Serveur serv = new Serveur();
    }
}
