package model.disasters;

import exceptions.BuildingAlreadyCollapsedException;
import exceptions.CitizenAlreadyDeadException;
import model.infrastructure.ResidentialBuilding;
import model.people.Citizen;
import model.people.CitizenState;
import simulation.Rescuable;
import simulation.Simulatable;

public abstract class Disaster implements Simulatable{
	private int startCycle;
	private Rescuable target;
	private boolean active;
	public Disaster(int startCycle, Rescuable target) {
		this.startCycle = startCycle;
		this.target = target;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public int getStartCycle() {
		return startCycle;
	}
	public Rescuable getTarget() {
		return target;
	}
	public void strike() throws BuildingAlreadyCollapsedException, CitizenAlreadyDeadException 
	{
		
		if(this.target instanceof Citizen) {
			Citizen C=(Citizen) this.target;
			if(C.getHp()==0 || C.getState()==CitizenState.DECEASED) {
				throw new CitizenAlreadyDeadException(this,"The Target Citizen is already Dead");
			}
		}
		else if(this.target instanceof ResidentialBuilding) {
			ResidentialBuilding R=(ResidentialBuilding)this.target;
			if(R.getStructuralIntegrity()==0) {
				throw new BuildingAlreadyCollapsedException(this,"The Target building has already collapsed");
			}
		}
		target.struckBy(this);
		active=true;}
	public String toString() {
		return "Start Cycle: "+this.startCycle+"."+"\n"
		+"Active: "+ this.isActive()+"."+"\n";
	}
	public String toString2() {
		String s= this.getClass().getSimpleName()+"\n"+this.getTarget().getClass().getSimpleName()+": ";
		if(this.getTarget() instanceof Citizen) {
			Citizen C = (Citizen)this.getTarget();
			s+=C.getName()+" at location (" +C.getLocation().getX()+","+C.getLocation().getY()+").";
		}
		else if(this.getTarget() instanceof ResidentialBuilding) {
			ResidentialBuilding B = (ResidentialBuilding)this.getTarget();
			s+="Building located at ("+B.getLocation().getX()+","+B.getLocation().getY()+").";
		}
		return s;
	}
}
