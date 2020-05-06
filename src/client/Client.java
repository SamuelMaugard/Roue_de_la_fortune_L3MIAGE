package client;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.WebSocket;
import java.net.URISyntaxException;
import java.util.*;


public class Client {

    Socket mSocket;
    String serverAddress;
    String pseudo;

    public Client() {
        serverAddress = askServer();
        pseudo = askPseudo();
        setUpClient();
        setUpEventsListeners();
        mSocket.connect();
    }

    public void setUpClient() {
        try {
            IO.Options opts = new IO.Options();
            opts.transports = new String[] { WebSocket.NAME };
            opts.reconnection = false;
            opts.forceNew = true;
            mSocket = IO.socket(serverAddress, opts);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public String askServer() {
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Entrez l'adresse du serveur host.");
        System.out.println("(http://127.0.0.1:10101/ en local.)");
        return keyboard.next();
    }

    public String askPseudo() {
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Entrez votre pseudo:");
        return keyboard.nextLine();
    }

    public String reponseMancheRapide() {
    	Scanner keyboard = new Scanner(System.in);
    	String rep = keyboard.nextLine();
    	rep+=" "+pseudo;
    	return rep;
    }	

    public void setUpEventsListeners() {

        //Custom listeners
        mSocket.on("pseudo", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                mSocket.emit("reponse-pseudo", pseudo);
            }
        });
        // reponse manche rapide
        mSocket.on("manche_rapide", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
            	System.out.println("Debut de la manche rapide :");
            	System.out.println("trouver la phrase suivante :\n");
            	System.out.println((String)objects[0]);
            	String rep = reponseMancheRapide();
            	mSocket.emit("reponse_manche_rapide",rep);
            }
        });
        // maj reponse
        mSocket.on("maj_manche_rapide", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
            	System.out.println((String)objects[0]);
            }
        });
        
        //connection listeners
        mSocket.on("connect", new Emitter.Listener() {
            @Override
            public void call(Object... objects) { System.out.println("Connecté"); }
        });
        mSocket.on("connect_failed", new Emitter.Listener() {
            @Override
            public void call(Object... objects) { System.out.println("Connexion échoué"); }
        });
        mSocket.on("disconnect", new Emitter.Listener() {
            @Override
            public void call(Object... objects) { System.out.println("Déconnexion"); }
        });
    }

    public static void main(String[] args) {
        Client cl = new Client();
    }
}
