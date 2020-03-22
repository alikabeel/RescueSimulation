package view;

import javax.swing.JButton;

import model.units.Unit;

public class RescueSimulationButton extends JButton {
	
	private Unit unit;

	public RescueSimulationButton() {
		
	}
public RescueSimulationButton(String s) {
		super(s);
	}

public Unit getUnit() {
	return unit;
}
public void setUnit(Unit unit) {
	this.unit = unit;
}

}
