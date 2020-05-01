package core.mot;

public class Phrase {

	private String rep; // phrase complete pour comparer
	private String aTrouver; // phrase avec les trous 
	
	public Phrase(String rep) {
		this.rep = rep;
		aTrouver = ""; // generer avec les tirets
	}
	
	public String toString() {
		// TODO a faire avec les espaces entre les tirets (boucle sur les caractères)
		return "";
	}
}
