package client;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.WebSocket;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URISyntaxException;

public class Client {

    Socket mSocket;
    String serverAddress;
    String pseudo;
    private JLabel titre;
    private JTextField textField;
    private JLabel contentLabel;
    private JButton btnOK;
    private ActionListener currentListener;

    public Client() {
        //Window params
        JFrame fenetre = new JFrame();
        fenetre.setTitle("Roue de la fortune");
        fenetre.setSize(new Dimension(500,500));
        fenetre.setResizable(false);

        //Main Panel
        JPanel mainPanel = new JPanel();
        BorderLayout bl = new BorderLayout();
        mainPanel.setLayout(bl);

        //Titre
        titre = new JLabel("Roue de la fortune");
        titre.setHorizontalAlignment(JTextField.CENTER);
        mainPanel.add(titre, BorderLayout.PAGE_START);

        //Contents
        JPanel contentPanel = new JPanel();
        JScrollPane scrPane = new JScrollPane(contentPanel);

        //contentPanel.setLayout(new ScrollPaneLayout());
        contentLabel = new JLabel("");
        contentPanel.add(contentLabel);
        mainPanel.add(scrPane, BorderLayout.CENTER);

        //inputs
        JPanel panInputs = new JPanel();
        panInputs.setLayout(new FlowLayout());
        textField = new JTextField("");
        textField.setPreferredSize(new Dimension(200,27));
        panInputs.add(textField);
        btnOK = new JButton("OK");
        panInputs.add(btnOK);
        mainPanel.add(panInputs, BorderLayout.PAGE_END);

        fenetre.setContentPane(mainPanel);
        fenetre.setVisible(true);
        fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        fenetre.setContentPane(mainPanel);

        askServer();
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

    public void askServer() {
        addToContent("Entrez l'adresse du serveur host.<br>" + "(http://127.0.0.1:10101/ en local.)<br>");
        textField.setText("http://127.0.0.1:10101/");
        ActionListener servLog = new ActionListener(){
            public void actionPerformed(ActionEvent event){
                String val = textField.getText();
                textField.setText("");
                serverAddress = val;
                addToContent("Serveur choisi: " + val + "<br>");
                askPseudo();
            }
        };
        currentListener = servLog;
        btnOK.addActionListener(servLog);
    }

    public void askPseudo() {
        addToContent("Entrez votre pseudo:<br>");
        ActionListener pseudoListener = new ActionListener(){
            public void actionPerformed(ActionEvent event){
                String val = textField.getText();
                textField.setText("");
                pseudo = val;
                addToContent("Pseudo choisi: " + val + "<br>");
                setUpClient();
                setUpEventsListeners();
                mSocket.connect();
            }
        };
        btnOK.removeActionListener(currentListener);
        currentListener = pseudoListener;
        btnOK.addActionListener(pseudoListener);
    }

    public void addToContent(String s) {
        String content = contentLabel.getText();
        if(content.length() >= 13) {
            content = content.substring(6, content.length()-7);
        }
        content = "<html>" + content + s + "</html>";
        contentLabel.setText(content);
    }

    public void reponseMancheRapide() {
        ActionListener reponseListener = new ActionListener(){
            public void actionPerformed(ActionEvent event){
                String val = textField.getText();
                textField.setText("");
                addToContent("Réponse envoyé: " + val + "<br>");
                mSocket.emit("reponse_manche_rapide",   val + " " + pseudo);
            }
        };
        btnOK.removeActionListener(currentListener);
        currentListener = reponseListener;
        btnOK.addActionListener(reponseListener);
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
            	addToContent("Debut de la manche rapide :<br>");
                addToContent("trouver la phrase suivante :<br>");
                addToContent((String)objects[0] + "<br>");
            	reponseMancheRapide();
            }
        });
        // maj reponse
        mSocket.on("maj_manche_rapide", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
            	addToContent((String)objects[0] + "<br>");
            }
        });
        
        //connection listeners
        mSocket.on("connect", new Emitter.Listener() {
            @Override
            public void call(Object... objects) { addToContent("Connecté<br>"); }
        });
        mSocket.on("connect_failed", new Emitter.Listener() {
            @Override
            public void call(Object... objects) { addToContent("Connexion échoué<br>"); }
        });
        mSocket.on("disconnect", new Emitter.Listener() {
            @Override
            public void call(Object... objects) { addToContent("Déconnexion<br>"); }
        });
    }

    public static void main(String[] args) {
        Client cl = new Client();
    }
}
