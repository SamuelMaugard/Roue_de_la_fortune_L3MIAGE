package core.roue;

public abstract class Case {

	public enum Valeur{
		zero("0"),
		cent("100"),
		centcqt("150"),
		deuxcentcqt("250"),
		troiscent("300"),
		quatrecent("400"),
		cinqcent("500"),
		mille("1000"),
		millecinqcent("1500"),
		deuxmille("2000"),
		banqueroute("banqueroute"),
		passe("passe"),
		holdUp("HoldUp");
		
		private String valeur;
		
		Valeur(String valeur) {
			this.valeur = valeur;
		}
		
		String getNom() {
			return valeur;
		}
	}
	
	private Valeur valeur;
	
	public Case(Valeur valeur) {
		this.valeur = valeur;
	}

	public Valeur getValeur() {
		return valeur;
	}
}

