package core.roue;

import java.util.*;

public class Roue {

	private ArrayList<Case> cases;
	
	public Roue() {
		cases = new ArrayList<Case>();
		generateRoue();
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
	
	public Case lancerRoue () {
		Random r = new Random();
		return cases.get(r.nextInt(cases.size()));
	}
	
}