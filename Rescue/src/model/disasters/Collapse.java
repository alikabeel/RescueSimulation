package model.disasters;

import exceptions.BuildingAlreadyCollapsedException;
import exceptions.CitizenAlreadyDeadException;
import model.infrastructure.ResidentialBuilding;


public class Collapse extends Disaster {

	public Collapse(int startCycle, ResidentialBuilding target) {
		super(startCycle, target);
		
	}
	public void strike() throws BuildingAlreadyCollapsedException, CitizenAlreadyDeadException 
	{
		ResidentialBuilding target= (ResidentialBuilding)getTarget();	
		if(target.getStructuralIntegrity()==0) {
			throw new BuildingAlreadyCollapsedException(this,"The Target building has already collapsed");
			
		}
		else {
		target.setFoundationDamage(target.getFoundationDamage()+10);
		super.strike();}
	}
	public void cycleStep()
	{
		ResidentialBuilding target= (ResidentialBuilding)getTarget();
		target.setFoundationDamage(target.getFoundationDamage()+10);
	}
	public String toString() {
		return "Disaster Type: Collapse."+"\n"+super.toString();
	}

}
