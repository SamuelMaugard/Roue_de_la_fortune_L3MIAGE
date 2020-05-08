package core.roue;

public class CaseGain extends Case{
	
	public CaseGain(int valeur) {
		super(getValeurInt(valeur));
	}
	
	private static Valeur getValeurInt(int v) {
		switch(v) {
			case 100:
				return Valeur.cent;
			case 150:
				return Valeur.centcqt;
			case 250:
				return Valeur.deuxcentcqt;
			case 300:
				return Valeur.troiscent;
			case 400:
				return Valeur.quatrecent;
			case 500:
				return Valeur.cinqcent;
			case 1000:
				return Valeur.mille;
			case 1500:
				return Valeur.millecinqcent;
			case 2000:
				return Valeur.deuxmille;
			case 5000:
				return Valeur.cinqmille;
			case 7500:
				return Valeur.septmillecinqcent;
			case 10000:
				return Valeur.dixmille;
			case 15000:
				return Valeur.quizemille;
			case 20000:
				return Valeur.vingtmille;
			case 25000:
				return Valeur.vingtcinqmille;
			case 50000:
				return Valeur.cinquantemille;
			case 100000:
				return Valeur.centmille;
			default :
				return Valeur.zero;
		}
	}
}
