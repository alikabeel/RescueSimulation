package model.units;

import javax.swing.ImageIcon;

import model.events.WorldListener;
import model.infrastructure.ResidentialBuilding;
import simulation.Address;
import view.RescueSimulationView;

public class FireTruck extends FireUnit {

	public FireTruck(String unitID, Address location, int stepsPerCycle,
			WorldListener worldListener) {
		super(unitID, location, stepsPerCycle, worldListener);
this.setImage(RescueSimulationView.ImageResizer(new ImageIcon("FireTruck.png"),RescueSimulationView.comboBoxWidth,RescueSimulationView.comboBoxHeight));
	}

	@Override
	public void treat() {
		getTarget().getDisaster().setActive(false);

		ResidentialBuilding target = (ResidentialBuilding) getTarget();
		if (target.getStructuralIntegrity() == 0) {
			jobsDone();
			return;
		} else if (target.getFireDamage() > 0)

			target.setFireDamage(target.getFireDamage() - 10);

		if (target.getFireDamage() == 0)

			jobsDone();

	}

}
