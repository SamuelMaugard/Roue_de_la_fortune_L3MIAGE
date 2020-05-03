package core.mot;

public class Phrase {

	private String rep; // phrase complete pour comparer
	private String aTrouver; // phrase avec les trous 
	private char[] lettrePropose;
	private int indexChar;
	
	public Phrase(String rep) {
		this.rep = rep;
		aTrouver = genererPhrase();
		lettrePropose = new char[26];
		indexChar=0;
	}
	
	
	/**
	 * @return la chaine de caractère avec les tirets
	 */
	private String genererPhrase() {
		String s="";
		for(int i=0;i<rep.length();i++) {
			if(rep.charAt(i)>='A' && rep.charAt(i)<='Z') {
				s+="_";
			}
			else {
				s+=rep.charAt(i);
			}
		}
		return s;
	}


	/**
	 * La méthode remplace les caractères de la phrase à trouver
	 * @param caractere proposé par le joueur
	 * @return le nb de caractere trouver pour pouvoir calculer les gains
	 */
	public int remplacerLettre(char caractere) {
		int nbLettre = 0;
		char tab[]= aTrouver.toCharArray();
		if(verifDejaPropose(caractere))
			return -1;	
		lettrePropose[indexChar]=caractere;
		indexChar++;
		for(int i=0;i<rep.length();i++) {
			if(rep.charAt(i)==(char)((int)caractere-32) || rep.charAt(i)==caractere) {
				tab[i]=rep.charAt(i);
			} 
		}
		aTrouver= String.valueOf(tab);
		return nbLettre;
	}
	
	/**
	 * @param caractere proposé par le joueur 
	 * @return si le caractère a deja été proposer ou pas
	 */
	private boolean verifDejaPropose(char caractere) {
		boolean estDejaProp = false;
		for(int i=0;i<lettrePropose.length;i++) {
			if(lettrePropose[i] ==caractere)
				estDejaProp=true;
		}
		return estDejaProp;
	}


	public String toString() {
		// TODO a faire avec les espaces entre les tirets (boucle sur les caractères)
		return "";
	}
}
