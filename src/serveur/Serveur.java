package serveur;

import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;

import core.ConsoleColors;
import joueur.Joueur;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Serveur {

    private SocketIOServer server;
    private GameManager game;

    public Serveur() {
        Configuration config = new Configuration();
        config.setHostname("127.0.0.1");
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
                System.out.println("Déconnexion de ("+socketIOClient.getRemoteAddress()+")");
            }
        });
        //Réponse pseudo
        server.addEventListener("reponse-pseudo", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient socketIOClient, String res, AckRequest ackRequest){
                game.addJoueur(new Joueur(res));
                System.out.println(ConsoleColors.GREEN + res + " a rejoint la partie " +
                        "(" + game.getNumberplayers()+"/2)" + ConsoleColors.RESET
                );
                if(game.getNumberplayers() == 2) {
                    System.out.println(ConsoleColors.CYAN + "--------------------------------------" + ConsoleColors.RESET);
                	game.newGame();
                }
            }
        });
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
    
    

    public static void main(String[] args) {
        Serveur serv = new Serveur();
    }
}
