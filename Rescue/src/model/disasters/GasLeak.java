package model.disasters;

import exceptions.BuildingAlreadyCollapsedException;
import exceptions.CitizenAlreadyDeadException;
import model.infrastructure.ResidentialBuilding;


public class GasLeak extends Disaster {

	public GasLeak(int startCycle, ResidentialBuilding target) {
		super(startCycle, target);
	}
	
	@Override
	public void strike() throws BuildingAlreadyCollapsedException, CitizenAlreadyDeadException 
	{
		
		ResidentialBuilding target= (ResidentialBuilding)getTarget();
		if(target.getStructuralIntegrity()==0) {
			throw new BuildingAlreadyCollapsedException(this,"The Target building has already collapsed");
			
		}
		else {
		target.setGasLevel(target.getGasLevel()+10);
		super.strike();}
	}
	@Override
	public void cycleStep() {
		ResidentialBuilding target= (ResidentialBuilding)getTarget();
		target.setGasLevel(target.getGasLevel()+15);
		
	}
	public String toString() {
		return "Disaster Type: Gas Leak."+"\n"+super.toString();
	}

}
