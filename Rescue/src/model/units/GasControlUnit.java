package model.units;

import javax.swing.ImageIcon;

import model.events.WorldListener;
import model.infrastructure.ResidentialBuilding;
import simulation.Address;
import view.RescueSimulationView;

public class GasControlUnit extends FireUnit {

	public GasControlUnit(String unitID, Address location, int stepsPerCycle,
			WorldListener worldListener) {
		super(unitID, location, stepsPerCycle, worldListener);
		this.setImage(RescueSimulationView.ImageResizer(new ImageIcon("GasControlUnit.png"),RescueSimulationView.comboBoxWidth,RescueSimulationView.comboBoxHeight));

	}

	public void treat() {
		getTarget().getDisaster().setActive(false);

		ResidentialBuilding target = (ResidentialBuilding) getTarget();
		if (target.getStructuralIntegrity() == 0) {
			jobsDone();
			return;
		} else if (target.getGasLevel() > 0) 
			target.setGasLevel(target.getGasLevel() - 10);

		if (target.getGasLevel() == 0)
			jobsDone();

	}

}
