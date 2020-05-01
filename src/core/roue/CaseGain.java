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
			default :
				return Valeur.zero;
		}
	}
}
