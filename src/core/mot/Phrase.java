package core.mot;

import java.util.*;

public class Phrase {

	private String rep; // phrase complete pour comparer
	private String aTrouver; // phrase avec les trous 
	private char[] lettrePropose;
	private int indexChar;

	/**
	 * @param rep full maj
	 */
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
				nbLettre++;
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

	public String getPhraseATrouver() {
		return aTrouver;
	}
	
	public String getPhraseJuste() {
		return rep;
	}


	public void decouvreUneLettre() {
		if(aTrouver.equals(rep)) {
			
		}
		else {
			Random rdm = new Random();
			ArrayList<Integer> indexChar = new ArrayList<Integer>();
			for(int i=0;i<aTrouver.length();i++) {
				if(aTrouver.charAt(i)=='_') {
					indexChar.add(i);
				}
			}
			char tab[]= aTrouver.toCharArray();
			int index = indexChar.get(rdm.nextInt(indexChar.size()));
			tab[index]=rep.charAt(index);
			aTrouver= String.valueOf(tab);
		}
	}

	/**
	 * @return true si il reste des consonnes
	 */
	public boolean resteConsones(){
		int consonneRep = 0 ;
		int consonneATrouver = 0;

		//nombre de consonnes dans la reponsse
		for(int i = 0; i < rep.length(); i++) {
			char c = rep.charAt(i);
			if(c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U' || c == 'Y') {
			}
			else if((c >= 'A' && c <= 'Z')) {
				consonneRep++;
			}
		}
		//nombre de consonnes dans la reponsse
		for(int i = 0; i < aTrouver.length(); i++) {
			char c = aTrouver.charAt(i);
			if(c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U' || c == 'Y') {
			}
			else if((c >= 'A' && c <= 'Z')) {
				consonneATrouver++;
			}
		}

		if (consonneRep - consonneATrouver == 0){
			return false;
		}
		return true;
	}

	/**
	 * @return true si il reste des voyelle
	 */
	public boolean resteVoyelle(){
		int voyelleRep = 0 ;
		int voyelleATrouver = 0;

		//nombre de voyelle dans la reponsse
		for(int i = 0; i < rep.length(); i++) {
			char c = rep.charAt(i);
			if(c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U' || c == 'Y') {
				voyelleRep++;
			}
		}
		//nombre de voyelle dans la reponsse
		for(int i = 0; i < aTrouver.length(); i++) {
			char c = aTrouver.charAt(i);
			if(c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U' || c == 'Y') {
				voyelleATrouver++;
			}
		}
		if (voyelleRep - voyelleATrouver == 0){
			return false;
		}
		return true;
	}
	
	public String toString() {
		String s = "";
		for (int i = 0; i < aTrouver.length(); i++) {
			s+=aTrouver.charAt(i)+" ";
		}
		return s;
	}


	public void resetPhrase(String phrase) {
		rep = phrase;
		lettrePropose = new char[26];
		aTrouver = genererPhrase();
	}
}
