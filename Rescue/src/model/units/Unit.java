package model.units;

import javax.swing.ImageIcon;

import exceptions.BuildingAlreadyCollapsedException;
import exceptions.CannotTreatException;
import exceptions.CitizenAlreadyDeadException;
import exceptions.IncompatibleTargetException;
import exceptions.UnitException;
import model.disasters.Collapse;
import model.disasters.Disaster;
import model.disasters.Fire;
import model.disasters.GasLeak;
import model.disasters.Infection;
import model.disasters.Injury;
import model.events.SOSResponder;
import model.events.WorldListener;
import model.infrastructure.ResidentialBuilding;
import model.people.Citizen;
import simulation.Address;
import simulation.Rescuable;
import simulation.Simulatable;

public abstract class Unit implements Simulatable, SOSResponder {
	private String unitID;
	private UnitState state;
	private Address location;
	private Rescuable target;
	private int distanceToTarget;
	private int stepsPerCycle;
	private WorldListener worldListener;
	private ImageIcon image;


	public ImageIcon getImage() {
		return image;
	}

	public void setImage(ImageIcon image) {
		this.image = image;
	}

	public Unit(String unitID, Address location, int stepsPerCycle,
			WorldListener worldListener) {
		this.unitID = unitID;
		this.location = location;
		this.stepsPerCycle = stepsPerCycle;
		this.state = UnitState.IDLE;
		this.worldListener = worldListener;
	}

	public void setWorldListener(WorldListener listener) {
		this.worldListener = listener;
	}

	public WorldListener getWorldListener() {
		return worldListener;
	}

	public UnitState getState() {
		return state;
	}

	public void setState(UnitState state) {
		this.state = state;
	}

	public Address getLocation() {
		return location;
	}

	public void setLocation(Address location) {
		this.location = location;
	}

	public String getUnitID() {
		return unitID;
	}

	public Rescuable getTarget() {
		return target;
	}

	public int getStepsPerCycle() {
		return stepsPerCycle;
	}

	public void setDistanceToTarget(int distanceToTarget) {
		this.distanceToTarget = distanceToTarget;
	}

	@Override
	public void respond(Rescuable r) throws IncompatibleTargetException, CannotTreatException {
		if(!this.canTreat(r)) {
			throw new CannotTreatException(this,r,"The selected target suffers no disaster or is Safe");
		}
		else if(!this.isCompatable(r)) {
			throw new IncompatibleTargetException(this,r,"The Unit "+ this.unitID+" is not a compatable unit with the selected Target.Please Consider choosing another Unit.");
		}
		else {
		if (target != null && state == UnitState.TREATING)
			reactivateDisaster();
		finishRespond(r);
		}
	}

	public void reactivateDisaster() {
		Disaster curr = target.getDisaster();
		curr.setActive(true);
	}

	public void finishRespond(Rescuable r) {
		target = r;
		state = UnitState.RESPONDING;
		Address t = r.getLocation();
		distanceToTarget = Math.abs(t.getX() - location.getX())
				+ Math.abs(t.getY() - location.getY());

	}

	public abstract void treat();
	  boolean isCompatable(Rescuable r) {
		if(r instanceof Citizen) {
			Citizen C=(Citizen)r;
			if(this instanceof Ambulance && C.getDisaster() instanceof Injury) {
				return true;
			}
			else if(this instanceof DiseaseControlUnit && C.getDisaster() instanceof Infection) {
				return true;
			}
		}
		else if(r instanceof ResidentialBuilding) {
			if(this instanceof GasControlUnit && r.getDisaster() instanceof GasLeak) {
				return true;
			}
			else if(this instanceof Evacuator && r.getDisaster() instanceof Collapse) {
				return true;
			}
			else if(this instanceof FireTruck && r.getDisaster() instanceof Fire) {
				return true;
			}
		}
		return false;
	}
	  
	boolean canTreat(Rescuable r) {
		if(r instanceof Citizen) {
			Citizen c=(Citizen)r;
			if(this instanceof Ambulance && c.getBloodLoss()==0 && c.getHp()==100) {
				return false;
			}
			else if(this instanceof DiseaseControlUnit && c.getToxicity()==0 && c.getHp()==100) {
				return false;
			}
		}
		else if(r instanceof ResidentialBuilding) {
			ResidentialBuilding R=(ResidentialBuilding) r;
			if(this instanceof FireTruck &&R.getFireDamage()==0) {
				return false;
			}
			else if(this instanceof GasControlUnit && R.getGasLevel()==0) {
				return false;
			}
			else if( this instanceof Evacuator &&R.getFoundationDamage()==0 ) {
				return false;
			}
		}
		return true;
	}
	public void cycleStep() {
		if (state == UnitState.IDLE)
			return;
		if (distanceToTarget > 0) {
			distanceToTarget = distanceToTarget - stepsPerCycle;
			if (distanceToTarget <= 0) {
				distanceToTarget = 0;
				Address t = target.getLocation();
				worldListener.assignAddress(this, t.getX(), t.getY());
			}
		} else {
			state = UnitState.TREATING;
			treat();
		}
	}

	public void jobsDone() {
		target = null;
		state = UnitState.IDLE;

	}
	public  String toString() {
		String s="Unit ID: "+this.getUnitID()+"."+"\n"
				+"Unit Type: "+this.getClass().getSimpleName()+"."+"\n"
				+"Location: (" +this.getLocation().getX()+","+this.getLocation().getY()+")."+"\n"
				+"Steps per Cycle: "+this.getStepsPerCycle()+"."+"\n"
				+"Unit State: "+this.getState()+"."+"\n";
		if(this.target!=null) {
			if(this.target instanceof ResidentialBuilding) {
			s+="Target: "+this.getTarget().getClass().getSimpleName()+"."+"\n";
			s+="Target Location: ("+this.getTarget().getLocation().getX()+","+this.getTarget().getLocation().getY()+")"+"."+"\n";}
			else {
				s+="Target: "+this.getTarget().getClass().getSimpleName()+" "+((Citizen)this.getTarget()).getName()+"."+"\n";
				s+="Target Location: ("+this.getTarget().getLocation().getX()+","+this.getTarget().getLocation().getY()+")"+"."+"\n";}
			}
					return s;
	}
}
