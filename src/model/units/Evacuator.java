package model.units;

import javax.swing.ImageIcon;

import model.events.WorldListener;
import model.infrastructure.ResidentialBuilding;
import simulation.Address;
import view.RescueSimulationView;

public class Evacuator extends PoliceUnit {

	public Evacuator(String unitID, Address location, int stepsPerCycle,
			WorldListener worldListener, int maxCapacity) {
		super(unitID, location, stepsPerCycle, worldListener, maxCapacity);
this.setImage(RescueSimulationView.ImageResizer(new ImageIcon("police-car.png"),RescueSimulationView.comboBoxWidth,RescueSimulationView.comboBoxHeight));

	}

	@Override
	public void treat() {
		ResidentialBuilding target = (ResidentialBuilding) getTarget();
		if (target.getStructuralIntegrity() == 0
				|| target.getOccupants().size() == 0) {
			jobsDone();
			return;
		}

		for (int i = 0; getPassengers().size() != getMaxCapacity()
				&& i < target.getOccupants().size(); i++) {
			getPassengers().add(target.getOccupants().remove(i));
			i--;
		}

		setDistanceToBase(target.getLocation().getX()
				+ target.getLocation().getY());

	}
	public  String toString() {
		
		String s=super.toString();
		s+="Number of Passengers: "+this.getPassengers().size()+"."+"\n";
		if(!this.getPassengers().isEmpty()) {
			s+="Passengers Info: "+"\n";
		}
				
		for(int i=0;i<this.getPassengers().size();i++) {
			s+=this.getPassengers().get(i).toString()+"\n";
			s+="-------------------------------"+"\n";
			
		}
		return s;
	}

}
