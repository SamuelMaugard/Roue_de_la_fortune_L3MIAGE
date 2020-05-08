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
		holdUp("holdUp"),
		passe("passe"),
		cinqmille("5000"),
		septmillecinqcent("7500"),
		dixmille("10000"),
		quizemille("15000"),
		vingtmille("20000"),
		vingtcinqmille("25000"),
		cinquantemille("50000"),
		centmille("100000");

		private String valeur;
		
		Valeur(String valeur) {
			this.valeur = valeur;
		}
		
		public String getNom() {
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

	@Override
	public String toString() {
		return "Case{" +
				"valeur=" + valeur +
				'}';
	}
}

