package model.units;

import javax.swing.ImageIcon;

import exceptions.CannotTreatException;
import exceptions.IncompatibleTargetException;
import model.events.WorldListener;
import model.infrastructure.ResidentialBuilding;
import model.people.Citizen;
import model.people.CitizenState;
import simulation.Address;
import simulation.Rescuable;
import view.RescueSimulationView;

public class Ambulance extends MedicalUnit {

	public Ambulance(String unitID, Address location, int stepsPerCycle,
			WorldListener worldListener) {
		super(unitID, location, stepsPerCycle, worldListener);
		this.setImage(RescueSimulationView.ImageResizer(new ImageIcon("Ambulance.png"),RescueSimulationView.comboBoxWidth,RescueSimulationView.comboBoxHeight));
	}

	@Override
	public void treat() {
		getTarget().getDisaster().setActive(false);

		Citizen target = (Citizen) getTarget();
		if (target.getHp() == 0) {
			jobsDone();
			return;
		} else if (target.getBloodLoss() > 0) {
			target.setBloodLoss(target.getBloodLoss() - getTreatmentAmount());
			if (target.getBloodLoss() == 0)
				target.setState(CitizenState.RESCUED);
		}

		else if (target.getBloodLoss() == 0)

			heal();

	}

	public void respond(Rescuable r) throws IncompatibleTargetException, CannotTreatException {
		if(!this.canTreat(r)) {
			throw new CannotTreatException(this,r,"The selected target suffers no disaster or is Safe");
		}
		else if(!this.isCompatable(r)) {
			throw new IncompatibleTargetException(this,r,"The Unit "+ this.getUnitID()+" is not a compatable unit with the selected Target.Please Consider choosing another Unit.");
		}
		else {
		if (getTarget() != null && ((Citizen) getTarget()).getBloodLoss() > 0
				&& getState() == UnitState.TREATING)
			reactivateDisaster();
		finishRespond(r);}
	}

}
