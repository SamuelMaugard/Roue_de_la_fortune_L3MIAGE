package serveur;

import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;

import com.corundumstudio.socketio.listener.ExceptionListener;
import core.ConsoleColors;
import core.roue.Case;
import io.netty.channel.ChannelHandlerContext;
import joueur.Joueur;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

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
                System.out.println(ConsoleColors.CYAN + "Connexion de ("+socketIOClient.getRemoteAddress()+")" + ConsoleColors.RESET);
                socketIOClient.sendEvent("pseudo");
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
            	if(checkPseudo(rep.split(" ")[rep.split(" ").length-1])) {
            		socketIOClient.sendEvent("pas_ton_tour",rep.split(" ")[rep.split(" ").length-1]);
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
            	consonne(rep);
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
            	voyelle(rep);
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
            		game.setPremierJoueur(game.joueurAdverse().getNom());
            		game.tourJoueur();
            	}
            }
        });
    }
    
    private void consonne(String rep) {
    	int nbLettre = game.getPhrase().remplacerLettre(rep.charAt(0));
    	if(nbLettre>0) {
    		int gain = game.getJoueur(game.getPremierJoueur()).getGainManche()+(nbLettre*game.getGainPotentiel());
    		game.getJoueur(game.getPremierJoueur()).setGainManche(gain);
    		String gainJ1 = "Joueur "+game.getJoueurs().get(0).getNom()+", gain : "+game.getJoueurs().get(0).getGainManche();
    		String gainJ2 = "Joueur "+game.getJoueurs().get(1).getNom()+", gain : "+game.getJoueurs().get(1).getGainManche();
    		getSocketServeur().getBroadcastOperations().sendEvent("maj_gain_phrase",gainJ1,gainJ2,game.getPremierJoueur(),game.getPhrase().toString());
    		game.tourJoueur();
    	}
    	else {
    		game.setPremierJoueur(game.joueurAdverse().getNom());
    		game.tourJoueur();
    	}
    }
    
    public void voyelle(String rep) {
    	int nbLettre = game.getPhrase().remplacerLettre(rep.charAt(0));
    	if(nbLettre>0) {
    		getSocketServeur().getBroadcastOperations().sendEvent("choix_joueur",game.getPremierJoueur(),game.getPhrase().toString());
    	}
    	else {
    		game.setPremierJoueur(game.joueurAdverse().getNom());
    		game.tourJoueur();
    	}
    }
    
    private void reponseMancheRapide(String rep,SocketIOClient socketIOClient) {
    	String[] repJoueur = rep.split(" ");
        String reponse = "";
        for(int i=0;i<repJoueur.length-1;i++) {
        	reponse+=repJoueur[i]+" ";
        }
        reponse = reponse.substring(0,reponse.length()-1);
        if(reponse.equals(game.getPhrase().getPhraseJuste())) {
        	System.out.println(repJoueur[repJoueur.length-1]+" a trouvé la reponse");
        	game.setEstTrouve(true);
        	String infoClient = repJoueur[repJoueur.length-1]+" a trouvé la réponse";
            game.getJoueur(repJoueur[repJoueur.length-1]).setGainManche(500);
            infoClient+=" et a gagné "+game.getJoueur(repJoueur[repJoueur.length-1]).getGainManche()+" de gain";
            server.getBroadcastOperations().sendEvent("fin_manche_rapide",infoClient);
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
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        System.out.println("\n--------------------------------------");
        System.out.println("Se connecter:");
        System.out.println("LOCAL: http://127.0.0.1:10101/");
        System.out.println("EXTERNE: http://" + inetAddress.getHostAddress() + ":10101/");
        System.out.println("--------------------------------------");

    }
    
    public SocketIOServer getSocketServeur() {
    	return server;
    }

    public static void main(String[] args) {
        Serveur serv = new Serveur();
    }
}
