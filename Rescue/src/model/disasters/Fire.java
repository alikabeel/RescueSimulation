package model.disasters;

import exceptions.BuildingAlreadyCollapsedException;
import exceptions.CitizenAlreadyDeadException;
import model.infrastructure.ResidentialBuilding;


public class Fire extends Disaster {

	public Fire(int startCycle, ResidentialBuilding target) {
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
		target.setFireDamage(target.getFireDamage()+10);
		super.strike();}
	}

	@Override
	public void cycleStep() {
		ResidentialBuilding target= (ResidentialBuilding)getTarget();
		target.setFireDamage(target.getFireDamage()+10);
		
	}
	public String toString() {
		return "Disaster Type: Fire."+"\n"+super.toString();
	}

}
