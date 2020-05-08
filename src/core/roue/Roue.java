package core.roue;

import java.util.*;

public class Roue {

	private ArrayList<Case> cases;

	/**
	 *
	 * @param b si b(true), genere la roue simple si b(false), genere la roue de la finale
	 */
	public Roue(Boolean b) {
		cases = new ArrayList<Case>();
		if (b){
			generateRoue();
		}
		else {
			generateRoueFinale();
		}
	}

	private void generateRoue() {
		cases.add(new CaseGain(0));
		cases.add(new CaseGain(100));
		cases.add(new CaseGain(100));
		cases.add(new CaseGain(100));
		cases.add(new CaseGain(150));
		cases.add(new CaseGain(150));
		cases.add(new CaseGain(150));
		cases.add(new CaseGain(150));
		cases.add(new CaseGain(250));
		cases.add(new CaseGain(250));
		cases.add(new CaseGain(250));
		cases.add(new CaseGain(250));
		cases.add(new CaseGain(250));
		cases.add(new CaseGain(300));
		cases.add(new CaseGain(300));
		cases.add(new CaseGain(400));
		cases.add(new CaseGain(500));
		cases.add(new CaseGain(1000));
		cases.add(new CaseGain(1500));
		cases.add(new CaseGain(2000));

		cases.add(new CaseBanqueRoute());
		cases.add(new CaseBanqueRoute());
		cases.add(new CaseHoldUp());
		cases.add(new CasePasse());
	}

	private void generateRoueFinale() {
		cases.add(new CaseGain(5000));
		cases.add(new CaseGain(7500));
		cases.add(new CaseGain(10000));
		cases.add(new CaseGain(15000));
		cases.add(new CaseGain(20000));
		cases.add(new CaseGain(25000));
		cases.add(new CaseGain(50000));
		cases.add(new CaseGain(100000));
	}


	
	public Case lancerRoue () {
		Random r = new Random();
		return cases.get(r.nextInt(cases.size()));
	}

	@Override
	public String toString() {
		return "Roue{" +
				"cases=" + cases +
				'}';
	}
}