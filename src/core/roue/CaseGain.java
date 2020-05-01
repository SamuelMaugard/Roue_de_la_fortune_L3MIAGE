package core.roue;

public class CaseGain extends Case{
	
	public CaseGain(int valeur) {
		super(getValeurInt(valeur));
	}
	
	private static Valeur getValeurInt(int v) {
		switch(v) {
			default :
				return Valeur.zero;
		}
	}
}
