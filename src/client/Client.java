package client;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.WebSocket;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
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
	private JScrollPane scrPane;
	private int nbChoix;

	public Client() {
		//Window params
		JFrame fenetre = new JFrame();
		fenetre.setTitle("Roue de la fortune");
		fenetre.setSize(new Dimension(600,500));
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
		scrPane = new JScrollPane(contentPanel);
		scrPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		scrPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				e.getAdjustable().setValue(e.getAdjustable().getMaximum());
			}
		});
		contentLabel = new JLabel("");
		//contentLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, contentLabel.getFont().getSize()));
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
			mSocket = IO.socket("http://"+serverAddress+":10101/", opts);
		} catch (URISyntaxException e) {
			e.getStackTrace();
			resetContent();
			addToContent("Addresse non valide");
			askServer();
		}
	}

	public void askServer() {
		addToContent("Entrez l'adresse du serveur host.");
		addToContent("(127.0.0.1 en local.)");
		addToContent("(109.210.118.18 sur le serveur hébergé.)");
		textField.setText("109.210.118.18");
		ActionListener servLog = new ActionListener(){
			public void actionPerformed(ActionEvent event){
				if(textField.getText().length() > 0) {
					String val = textField.getText();
					textField.setText("");
					serverAddress = val;
					addToContent("Serveur choisi: " + val);
					askPseudo();
				}
			}
		};
		currentListener = servLog;
		btnOK.addActionListener(servLog);
	}

	public void askPseudo() {
		addToContent("Entrez votre pseudo:");
		ActionListener pseudoListener = new ActionListener(){
			public void actionPerformed(ActionEvent event){
				if(textField.getText().length() > 0) {
					String val = textField.getText();
					textField.setText("");
					if(val.length() > 20) {
						addToContent("Veuillez proposer un pseudo avec moins de 20 char");
						askPseudo();
					} else {
						pseudo = val;
						addToContent("Pseudo choisi: " + val);
						setUpClient();
						setUpEventsListeners();
						mSocket.connect();
					}

				}

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
		content = "<html>" + content + "<pre>" + s + "</pre>" + "</html>";

		contentLabel.setText(content);
	}

	public void resetContent() {
		contentLabel.setText("<html></html>");
	}

	public void reponseMancheRapide() {
		ActionListener reponseListener = new ActionListener(){
			public void actionPerformed(ActionEvent event){
				if(textField.getText().length() > 0) {
					String val = textField.getText();
					val=val.toUpperCase();
					textField.setText("");
					addToContent("Réponse envoyé: " + val);
					val+="\t"+pseudo;
					addToContent(val);
					mSocket.emit("reponse_manche_rapide",val);
				}

			}
		};
		btnOK.removeActionListener(currentListener);
		currentListener = reponseListener;
		btnOK.addActionListener(reponseListener);
	}

	public void choixJoueur(int i) {
		ActionListener choixListener = new ActionListener(){
			public void actionPerformed(ActionEvent event){
				if(textField.getText().length() > 0) {
					String val = textField.getText();
					textField.setText("");
					val=val.toUpperCase();
					switch(val) {
					case "C":
						if(i==2 || i==3) {
							mSocket.emit("consonne",pseudo);
						} else {
							addToContent("choix erroné veuillez recommencer");
							choixJoueur(i);
						}
						break;
					case "V":
						if(i==3 || i==1) {
							mSocket.emit("voyelle",pseudo);
						} else {
							addToContent("choix erroné veuillez recommencer");
							choixJoueur(i);
						}
						break;
					case "R":
						mSocket.emit("reponse",pseudo);
						break;
					default:
						addToContent("choix erroné veuillez recommencer");
						choixJoueur(i);
					}
				}
			}
		};
		btnOK.removeActionListener(currentListener);
		currentListener = choixListener;
		btnOK.addActionListener(choixListener);
	}

	public void reponseMancheLongue() {
		ActionListener reponseListener = new ActionListener(){
			public void actionPerformed(ActionEvent event){
				if(textField.getText().length() > 0) {
					String val = textField.getText();
					textField.setText("");
					val=val.toUpperCase();
					mSocket.emit("reponse_prop",val);
				}
			}
		};
		btnOK.removeActionListener(currentListener);
		currentListener = reponseListener;
		btnOK.addActionListener(reponseListener);
	}

	public void consonne() {
		ActionListener consonneListener = new ActionListener(){
			public void actionPerformed(ActionEvent event){
				if(textField.getText().length() > 0) {
					String val = textField.getText();
					textField.setText("");
					val=val.toLowerCase();
					if(isConsonne(val)) {
						mSocket.emit("consonne_prop",val);
					}
					else {
						addToContent("Veuillez rentrez une valeur correcte");
						consonne();
					}
				}
			}
		};
		btnOK.removeActionListener(currentListener);
		currentListener = consonneListener;
		btnOK.addActionListener(consonneListener);
	}

	public boolean isConsonne(String val) {
		if(val.length()>1 && val.length()==0) {
			return false;
		}
		else if(val.charAt(0)=='a' || val.charAt(0)=='e' || val.charAt(0)=='i' || val.charAt(0)=='o' || val.charAt(0)=='u' || val.charAt(0)=='y') {
			return false;
		}
		return true;
	}

	private void voyelle() {
		ActionListener voyelleListener = new ActionListener(){
			public void actionPerformed(ActionEvent event){
				if(textField.getText().length() > 0) {
					String val = textField.getText();
					textField.setText("");
					val=val.toLowerCase();
					if(isVoyelle(val)) {
						mSocket.emit("voyelle_prop",val);
					}
					else {
						addToContent("Veuillez rentrez une valeur correcte");
						voyelle();
					}
				}
			}
		};
		btnOK.removeActionListener(currentListener);
		currentListener = voyelleListener;
		btnOK.addActionListener(voyelleListener);
	}

	public boolean isVoyelle(String val) {
		if(val.length()>1 && val.length()==0) {
			return false;
		}
		else if(val.charAt(0)=='a' || val.charAt(0)=='e' || val.charAt(0)=='i' || val.charAt(0)=='o' || val.charAt(0)=='u' || val.charAt(0)=='y') {
			return true;
		}
		return false;
	}

	public void reponse() {
		ActionListener reponseListener = new ActionListener(){
			public void actionPerformed(ActionEvent event){
				if(textField.getText().length() > 0) {
					String val = textField.getText();
					textField.setText("");
					val=val.toUpperCase();
					mSocket.emit("reponse_prop",val);
				}
			}
		};
		btnOK.removeActionListener(currentListener);
		currentListener = reponseListener;
		btnOK.addActionListener(reponseListener);
	}

	public void setUpEventsListeners() {

		//Ask pseudo
		mSocket.on("pseudo", new Emitter.Listener() {
			@Override
			public void call(Object... objects) {
				mSocket.emit("reponse-pseudo", pseudo);
			}
		});
		//Pseudo updated by the server
		mSocket.on("pseudo-update", new Emitter.Listener() {
			@Override
			public void call(Object... objects) {
				pseudo = (String)objects[0];
				titre.setText("Roue de la fortune | " + pseudo);
			}
		});
		//connection rejected by the server
		mSocket.on("reject", new Emitter.Listener() {
			@Override
			public void call(Object... objects) {
				resetContent();
				addToContent("Connection refusé");
				mSocket.disconnect();
			}
		});
		// reponse manche rapide
		mSocket.on("manche_rapide", new Emitter.Listener() {
			@Override
			public void call(Object... objects) {
				addToContent("----------- "+(String)objects[1]+" -----------");
				addToContent("Debut de la manche rapide :");
				addToContent("trouver la phrase suivante :");
				addToContent((String)objects[0]);
				reponseMancheRapide();
			}
		});
		// mauvaise rep
		mSocket.on("mauvaise_reponse_rapide", new Emitter.Listener() {
			@Override
			public void call(Object... objects) {
				reponseMancheRapide();
			}
		});
		// maj reponse manche rapide
		mSocket.on("maj_manche_rapide", new Emitter.Listener() {
			@Override
			public void call(Object... objects) {
				addToContent((String)objects[0]);
			}
		});
		// fin manche rapide
		mSocket.on("fin_manche_rapide", new Emitter.Listener() {
			@Override
			public void call(Object... objects) {
				if(!((String)objects[1]).equals(pseudo)) {
					btnOK.removeActionListener(currentListener);
				}
				addToContent((String)objects[0]);
				addToContent("<br>");
				addToContent("Debut de manche longue");
			}
		});
		// choix joueur
		mSocket.on("choix_joueur", new Emitter.Listener() {
			@Override
			public void call(Object... objects) {
				String nom =(String)objects[0];
				if(nom.equals(pseudo)) {
					addToContent((String)objects[1]);
					addToContent("Choisissez un action à effectuer : ");
					addToContent("c : proposer une consonne");
					addToContent("v : proposer une voyelle");
					addToContent("r : proposer une reponse");
					nbChoix=3;
					choixJoueur(3);
				}
				else {
					addToContent((String)objects[1]);
					addToContent((String)objects[0]+" doit choisir une action");
				}
			}
		});
		// choix_cons_rep
		mSocket.on("choix_cons_rep", new Emitter.Listener() {
			@Override
			public void call(Object... objects) {
				String nom =(String)objects[0];
				if(nom.equals(pseudo)) {
					addToContent((String)objects[1]);
					addToContent("Il n'y a plus de voyelle : ");
					addToContent("Choisissez un action à effectuer : ");
					addToContent("c : proposer une consonne");
					addToContent("r : proposer une reponse");
					nbChoix=2;
					choixJoueur(2);
				}
				else {
					addToContent((String)objects[1]);
					addToContent((String)objects[0]+" doit choisir une action");
				}
			}
		});
		// choix voy_rep
		mSocket.on("choix_voy_rep", new Emitter.Listener() {
			@Override
			public void call(Object... objects) {
				String nom =(String)objects[0];
				if(nom.equals(pseudo)) {
					addToContent((String)objects[1]);
					addToContent("Il n'y a plus de consonne : ");
					addToContent("Choisissez un action à effectuer : ");
					addToContent("v : proposer une voyelle");
					addToContent("r : proposer une reponse");
					nbChoix=1;
					choixJoueur(1);
				}
				else {
					addToContent((String)objects[1]);
					addToContent((String)objects[0]+" doit choisir une action");
				}
			}
		});
		// rep
		mSocket.on("choix_rep", new Emitter.Listener() {
			@Override
			public void call(Object... objects) {
				String nom =(String)objects[0];
				if(nom.equals(pseudo)) {
					addToContent((String)objects[1]);
					addToContent("Il n'y a plus de voyelle et de consonne  ");
					addToContent("Veuillez proposez une reponse ");
					reponseMancheLongue();
				}
				else {
					addToContent((String)objects[1]);
					addToContent((String)objects[0]+" doit choisir une action");
				}
			}
		});

		// banqueroute
		mSocket.on("banqueroute", new Emitter.Listener() {
			@Override
			public void call(Object... objects) { 
				addToContent((String)objects[0]+" a fait tourné la roue et obtenu une case banqueroute.");
				addToContent("Ces gains lors de cette manchesont remis à 0");
				addToContent("Ce n'est plus à lui de jouer");
			}
		});
		// passe
		mSocket.on("passe", new Emitter.Listener() {
			@Override
			public void call(Object... objects) { 
				addToContent((String)objects[0]+" a fait tourné la roue et obtenu une case passe.");
				addToContent("Ce n'est plus à lui de jouer");
			}
		});
		// holdUp
		mSocket.on("holdUp", new Emitter.Listener() {
			@Override
			public void call(Object... objects) { 
				addToContent((String)objects[0]+" a fait tourné la roue et obtenu une case holdUp.");
				addToContent("Il va pouvoir gagner les gains de son adversaire qui sont de "+(String)objects[1]);
			}
		});
		// gain
		mSocket.on("gain", new Emitter.Listener() {
			@Override
			public void call(Object... objects) { 
				addToContent((String)objects[0]+" a fait tourné la roue et obtenu une case gain.");
				addToContent("Il va pouvoir gagner les gains de la case qui sont de "+(String)objects[1]);
			}
		});

		// maj manche longue
		mSocket.on("maj_gain_phrase", new Emitter.Listener() {
			@Override
			public void call(Object... objects) {
				addToContent("Gain des joueurs");
				addToContent((String)objects[0]);
				addToContent((String)objects[1]);
				addToContent("C'est à "+(String)objects[2]+" de jouer");
				if(!((String)objects[2]).equals(pseudo)) {
					addToContent((String)objects[3]);
				}
			}
		});

		// proposer consonne
		mSocket.on("consonne", new Emitter.Listener() {
			@Override
			public void call(Object... objects) {
				String nom =(String)objects[0];
				if(nom.equals(pseudo)) {
					addToContent("Proposez une consonne");
					consonne();
				}
				else {
					addToContent((String)objects[0]+" propose une consonne");
				}
			}
		});
		// lettre prop
		mSocket.on("lettre_prop", new Emitter.Listener() {
			@Override
			public void call(Object... objects) {
				addToContent((String)objects[1]+" a proposé la lettre : "+(String)objects[0]);

			}
		});

		// notbuy voyelle
		mSocket.on("notbuy_voyelle", new Emitter.Listener() {
			@Override
			public void call(Object... objects) {
				String nom =(String)objects[1];
				if(nom.equals(pseudo)) {
					addToContent((String)objects[1]+(String)objects[0]);
					addToContent("Veuillez faire un autre choix :");
					choixJoueur(nbChoix);
				}
				else {
					addToContent((String)objects[1]+(String)objects[0]);
				}
			}
		});
		// proposer voyelle
		mSocket.on("voyelle", new Emitter.Listener() {
			@Override
			public void call(Object... objects) {
				String nom =(String)objects[0];
				if(nom.equals(pseudo)) {
					addToContent("Proposez une voyelle");
					voyelle();
				}
				else {
					addToContent((String)objects[0]+" propose une voyelle");
				}
			}
		});

		// proposer reponse
		mSocket.on("reponse", new Emitter.Listener() {
			@Override
			public void call(Object... objects) {
				String nom =(String)objects[0];
				if(nom.equals(pseudo)) {
					addToContent("Proposez une reponse");
					reponse();
				}
				else {
					addToContent((String)objects[0]+" propose une reponse");
				}
			}
		});
		// bonne reponse
		mSocket.on("bonne_reponse", new Emitter.Listener() {
			@Override
			public void call(Object... objects) { 
				addToContent((String)objects[0]+" a trouvé la bonne réponse et gagne "+(String)objects[1]+" de gain");
			}
		});
		// mauvaise reponse
		mSocket.on("mauvaise_reponse", new Emitter.Listener() {
			@Override
			public void call(Object... objects) { 
				addToContent((String)objects[0]+" a proposé une mauvaise réponse"); 
			}
		});

		// pas ton tour
		mSocket.on("pas_ton_tour", new Emitter.Listener() {
			@Override
			public void call(Object... objects) { 
				if(((String)objects[0]).equals(pseudo)) {
					btnOK.removeActionListener(currentListener);
					addToContent((String)objects[0]+" ce n'est plus a vous de jouez");
				}
			}
		});

		//finale
		mSocket.on("finale", new Emitter.Listener() {
			@Override
			public void call(Object... objects) { 
				String phrase = (String) objects[0];
				String gagnant = (String) objects[1];
				if(gagnant.equals(pseudo)) {
					addToContent("<br>");
					addToContent("------- Manche Finale -------");
					addToContent(phrase); 
					addToContent("Nous avons ajouté les lettres les plus utilisés de l'alphabet qui sont :");
					addToContent("r, s, t, l, n et e");
					addToContent("Proposer 3 consonnes et 1 voyelle de cette façon: b c f a");
					addToContent("(voyelle en dernier)");
					propositionLettreFinale();
				}
				else {
					addToContent(gagnant+ " est en finale"); 
				}
			}
		});
		// reponse finale
		mSocket.on("reponse_finale", new Emitter.Listener() {
			@Override
			public void call(Object... objects) { 
				String phrase = (String) objects[0];
				addToContent(phrase);
				addToContent("Vous avez 30s pour trouver la bonne réponse");
				propositionFinale();
			}
		});
		// mauvaise rep finale
		mSocket.on("mauvaise_rep_finale", new Emitter.Listener() {
			@Override
			public void call(Object... objects) { 
				String phrase = (String) objects[0];
				addToContent(phrase);
				propositionFinale();
			}
		});
		// bonne reponse finale
		mSocket.on("bonne_rep_finale", new Emitter.Listener() {
			@Override
			public void call(Object... objects) { 
				String nom = (String) objects[0];
				String gain = (String) objects[1];
				addToContent(nom+" a gagné "+gain+" de gains");
				addToContent("Partie Terminée");
			}
		});
		// fin finale
		mSocket.on("fin_finale", new Emitter.Listener() {
			@Override
			public void call(Object... objects) { 
				String nom = (String) objects[0];
				String gain = (String) objects[1];
				addToContent(nom+" n'a pas reussi a trouver la réponse.");
				addToContent("il repart donc avec les gains qu'il a eu pendant les autres manches");
				addToContent("Gains de "+nom+" : "+gain);
				addToContent("Partie Terminée");
				if(nom.equals(pseudo)) {
					addToContent("Voulez vous reocommencez une partie ?");
					addToContent("o : oui");
					addToContent("n : non");
					recommencez();
				}
			}
		});

		//connection listeners
		mSocket.on("connect", new Emitter.Listener() {
			@Override
			public void call(Object... objects) { addToContent("Connecté"); }
		});
		mSocket.on("connect_failed", new Emitter.Listener() {
			@Override
			public void call(Object... objects) {
				resetContent();
				addToContent("Connexion échoué");
				askServer();
			}
		});
		mSocket.on("connect_error", new Emitter.Listener() {
			@Override
			public void call(Object... objects) {
				resetContent();
				addToContent("Connexion échoué");
				askServer();
			}
		});
		mSocket.on("disconnect", new Emitter.Listener() {
			@Override
			public void call(Object... objects) {
				addToContent("Déconnexion");
				askServer();
			}
		});
	}

	public void propositionLettreFinale() {
		ActionListener propositionLettreFinaleListener = new ActionListener(){
			public void actionPerformed(ActionEvent event){
				if(textField.getText().length() > 0) {
					String[] tab = textField.getText().split(" ");
					for(int i=0; i<tab.length; i++) {
						if(i<3 && !isConsonne(tab[i])) {
							addToContent("Veuillez faire une proposition correcte");
							propositionLettreFinale();
						}
						else if(i==3 && !isVoyelle(tab[i])) {
							addToContent("Veuillez faire une proposition correcte");
							propositionLettreFinale();
						}
					}
					String rep = textField.getText();
					textField.setText("");
					mSocket.emit("proposition_finale",rep);
				}
			}
		};
		btnOK.removeActionListener(currentListener);
		currentListener = propositionLettreFinaleListener;
		btnOK.addActionListener(propositionLettreFinaleListener);
	}

	public void propositionFinale() {
		ActionListener propositionFinaleListener = new ActionListener(){
			public void actionPerformed(ActionEvent event){
				if(textField.getText().length() > 0) {
					String rep = textField.getText();
					textField.setText("");
					mSocket.emit("reponse_finale",rep.toUpperCase());
				}
			}
		};
		btnOK.removeActionListener(currentListener);
		currentListener = propositionFinaleListener;
		btnOK.addActionListener(propositionFinaleListener);
	}

	public void recommencez() {
		ActionListener recommencezListener = new ActionListener(){
			public void actionPerformed(ActionEvent event){
				if(textField.getText().length() > 0) {
					String val = textField.getText();
					textField.setText("");
					val=val.toUpperCase();
					switch(val) {
					case "O":
						mSocket.emit("oui");
						break;
					case "N":
						mSocket.emit("non");
						break;
					}
				}
			}
		};
		btnOK.removeActionListener(currentListener);
		currentListener = recommencezListener;
		btnOK.addActionListener(recommencezListener);
	}

	public static void main(String[] args) {
		Client cl = new Client();
	}
}
