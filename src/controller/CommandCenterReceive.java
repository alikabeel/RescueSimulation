package controller;

import java.awt.Color;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.border.LineBorder;

import Communication.Client;
import Communication.Server;
import exceptions.BuildingAlreadyCollapsedException;
import exceptions.CannotTreatException;
import exceptions.CitizenAlreadyDeadException;
import exceptions.IncompatibleTargetException;
import model.disasters.Disaster;
import model.events.SOSListener;
import model.infrastructure.ResidentialBuilding;
import model.people.Citizen;
import model.people.CitizenState;
import model.units.Unit;
import simulation.Rescuable;
import simulation.Simulatable;
import simulation.Simulator;
import view.RescueSimulationButton;
import view.RescueSimulationComboBox;
import view.RescueSimulationReceived;
import view.RescueSimulationReceived;

public class CommandCenterReceive implements SOSListener,ActionListener {

	private RescueSimulationReceived GUI; 

	public RescueSimulationReceived getGUI() {
		return GUI;
	}
	public void setGUI(RescueSimulationReceived gUI) {
		GUI = gUI;
	}

	private Simulator engine;
	private ArrayList<ResidentialBuilding> visibleBuildings;
	private ArrayList<Citizen> visibleCitizens;
	private  int displayCurrentCycle;
	private int displayCasualties;
	private ArrayList<Unit> emergencyUnits;
	private boolean unitButtonPressed;
	private RescueSimulationButton clickedUnitButton;
	private ArrayList<Citizen> backUpCitizens;
	private  boolean isVisibleCitizen=true;
	private  boolean isLog=true;
	private  boolean isDisaster=true;
	private  boolean isCycle=true;
	private  boolean isCasualities=true;

	

	public void movedFrom(Simulatable s)
	{
		if (s instanceof Citizen)
		{
			Citizen c = (Citizen)s;
			int i = c.getLocation().getX();
			int j = c.getLocation().getY();
			this.GUI.removeFromComboBox(i, j, c.getImage());


		}
		if (s instanceof Unit)
		{
			Unit u = (Unit)s;
			int i = u.getLocation().getX();
			int j = u.getLocation().getY();
			this.GUI.removeFromComboBox(i, j, u.getImage());
		}
		if (s instanceof ResidentialBuilding)
		{
			ResidentialBuilding B = (ResidentialBuilding)s;
			int i = B.getLocation().getX();
			int j = B.getLocation().getY();
			this.GUI.removeFromComboBox(i, j, B.getImage());
			for(int c=0;c<B.getOccupants().size();c++) {
				this.movedFrom(B.getOccupants().get(c));
			}
		}

	}
	//depends on assignAddress in simulator
	public void movedTo(Simulatable s)
	{
		if (s instanceof Citizen)
		{
			Citizen c = (Citizen)s;
			int i = c.getLocation().getX();
			int j = c.getLocation().getY();
			this.GUI.addToComboBox(i, j, c.getImage());



		}
		if (s instanceof Unit)
		{
			Unit u = (Unit)s;
			int i = u.getLocation().getX();
			int j = u.getLocation().getY();
			this.GUI.addToComboBox(i, j, u.getImage());
		}
		if(s instanceof ResidentialBuilding) {
			ResidentialBuilding b=(ResidentialBuilding)s;
			int i=b.getLocation().getX();
			int j=b.getLocation().getY();
			this.GUI.addToComboBox(i, j, b.getImage());
			for(int u=0;u<b.getOccupants().size();u++) {
				Citizen c = (Citizen)b.getOccupants().get(u);
				if(!visibleCitizens.contains(c)) {
					int x = c.getLocation().getX();
					int y = c.getLocation().getY();
					this.GUI.addToComboBox(x, y, c.getImage());}
			}
		}
	}

	public boolean isUnitButtonPressed() {
		return unitButtonPressed;
	}

	public void setUnitButtonPressed(boolean unitButtonPressed) {
		this.unitButtonPressed = unitButtonPressed;
	}

	public ArrayList<Unit> getEmergencyUnits() {
		return emergencyUnits;
	}

	public void setEmergencyUnits(ArrayList<Unit> emergencyUnits) {
		this.emergencyUnits = emergencyUnits;
	}

	public int getDisplayCasualties() {
		return displayCasualties;
	}

	public void setDisplayCasualties(int displayCasualties) {
		this.displayCasualties = displayCasualties;
	}

	public int getDisplayCurrentCycle() {
		return displayCurrentCycle;
	}

	public void setDisplayCurrentCycle(int displayCurrentCycle) {
		this.displayCurrentCycle = displayCurrentCycle;
	}

	public CommandCenterReceive() throws Exception {
		GUI = new RescueSimulationReceived(this);
		engine = new Simulator(this);
		visibleBuildings = new ArrayList<ResidentialBuilding>();
		visibleCitizens = new ArrayList<Citizen>();
		backUpCitizens =new ArrayList<Citizen>();
		emergencyUnits = new ArrayList<Unit>();
		this.loadUnitsGUI();
	}

	@Override
	public void receiveSOSCall(Rescuable r) {

		if (r instanceof ResidentialBuilding) {



			if (!visibleBuildings.contains(r))
			{
				visibleBuildings.add((ResidentialBuilding) r);
				this.movedTo((Simulatable)r);
				for(int i=0;i<((ResidentialBuilding) r).getOccupants().size();i++) {
					backUpCitizens.add(((ResidentialBuilding) r).getOccupants().get(i));
				}
			}
		} else {

			if (!visibleCitizens.contains(r))
			{	if(!backUpCitizens.contains(r)) {
				this.movedTo((Simulatable)r);}
			visibleCitizens.add((Citizen) r);
			}
		}

	}



	@Override
	public void actionPerformed(ActionEvent e){
		if(e.getActionCommand().equals("Next Cycle"))
		{
			this.nextCycleHelper();

			return;
		}

		Object x = e.getSource();
		if (x instanceof RescueSimulationButton)
		{
			RescueSimulationButton b = (RescueSimulationButton)x;

			helperUnitButton(b.getUnit(),(RescueSimulationButton)e.getSource());
		}

		if (x instanceof RescueSimulationComboBox) {
			this.comboBoxHelper(x);

		}
	}


	public void helperUnitButton(Unit unit,RescueSimulationButton B)
	{	if(unit==null) {
		return;
	}
	String s =(this.unitButtonPressed)?"": unit.toString();
	GUI.infoSetter(s);
	if(!unitButtonPressed) {
		B.setBorderPainted(true);
		B.setBackground(Color.GREEN);
		B.setOpaque(true);
		clickedUnitButton=B;

	}
	else {
		clickedUnitButton.setBorderPainted(false);
		clickedUnitButton.setOpaque(false);
		clickedUnitButton=null;
	}
	this.unitButtonPressed=!this.unitButtonPressed;


	}

	public void comboBoxHelper(Object x)
	{
		GUI.infoSetter("");
		Rescuable r =null;
		RescueSimulationComboBox b = (RescueSimulationComboBox) x;
		if (b.first)
		{
			b.first = false;
			return;
		}
		ImageIcon im=(ImageIcon)b.getSelectedItem();
		for(int i=0;i<this.visibleBuildings.size();i++ ) {
			if(this.visibleBuildings.get(i).getImage()==im) {
				GUI.infoSetter(this.visibleBuildings.get(i).toString());
				r = this.visibleBuildings.get(i);
				break;
			}
			for(int j=0;j<this.visibleBuildings.get(i).getOccupants().size();j++) {
				if(this.visibleBuildings.get(i).getOccupants().get(j).getImage()==im) {
					GUI.infoSetter(this.visibleBuildings.get(i).getOccupants().get(j).toString());
					r = this.visibleBuildings.get(i).getOccupants().get(j);
					break;
				}
			}
		}
		for(int i=0;i<this.visibleCitizens.size();i++) {
			if(this.visibleCitizens.get(i).getImage()==im) {
				r= this.visibleCitizens.get(i);
				GUI.infoSetter(this.visibleCitizens.get(i).toString());
				break;
			}
		}
		for(int a=0;a<this.backUpCitizens.size();a++) {
			if(this.backUpCitizens.get(a).getImage()==im) {
				r= this.backUpCitizens.get(a);
				GUI.infoSetter(this.backUpCitizens.get(a).toString());
				break;
			}
		}


		for(int i=0;i<this.emergencyUnits.size();i++) {
			if(this.emergencyUnits.get(i).getImage()==im) {
				GUI.infoSetter(this.emergencyUnits.get(i).toString());
				return;
			}
		}
		if (r == null)
			return;
		if (unitButtonPressed)
		{
			Unit u = this.clickedUnitButton.getUnit();
			try {
				u.respond(r);
			} catch (IncompatibleTargetException e) {
				String t =(r.getDisaster()!=null)?r.getDisaster().getClass().getSimpleName():"no disaster";
				JOptionPane.showMessageDialog(GUI.getMainFrame(), "Unit of type "+u.getClass().getSimpleName()+" is incompatible with rescuable of type "+ r.getClass().getSimpleName() + " suffering from " + t);
			} catch (CannotTreatException e) {

				JOptionPane.showMessageDialog(GUI.getMainFrame(), "The rescuable target does not need rescuing or the chosen unit can't treat this type of disasters.");

			}
		}
		((RescueSimulationComboBox) x).setSelectedIndex(0);
		((RescueSimulationComboBox) x).revalidate();
	}
	public void buttonReseterHelper()
	{
		GUI.infoSetter("");
		if(clickedUnitButton != null)
		{
			clickedUnitButton.setBorderPainted(false);
			clickedUnitButton.setOpaque(false);
			clickedUnitButton=null;
		}

		this.unitButtonPressed=false;


	}
	public void nextCycleHelper()
	{
		buttonReseterHelper();
		engine.nextCycle();
		GUI.updateCycle(displayCurrentCycle);
		GUI.updateCasualties(displayCasualties);
		this.loadDisastersGUI();
		this.handleDeadDestructed();
		GUI.handleTreatingResponding();
		GUI.ValidateGUI();
		if(engine.checkGameOver()) {
			GUI.GameOverGetter();
			return;
		}
	}
	public void loadUnitsGUI() {
		for(int i=0;i<this.getEmergencyUnits().size();i++) {
			this.GUI.loadUnits(this.getEmergencyUnits().get(i));
			this.movedTo(this.getEmergencyUnits().get(i));
		}
	}
	public void loadDisastersGUI() {
		String S="";
		for(int i=0;i<this.visibleBuildings.size();i++) {
			if(this.visibleBuildings.get(i).getDisaster()!=null && this.visibleBuildings.get(i).getDisaster().isActive()) {
				S+=this.visibleBuildings.get(i).getDisaster().toString2()+"\n";
			}
		}
		for(int i=0;i<this.visibleCitizens.size();i++) {
			if(this.visibleCitizens.get(i).getDisaster()!=null && this.visibleCitizens.get(i).getDisaster().isActive()) {
				S+=this.visibleCitizens.get(i).getDisaster().toString2()+"\n";
			}
		}	
		this.GUI.writeDisaster(S);
	}


	public void logDisasterWriter(Disaster d)
	{

		String s = "Cycle "+this.displayCurrentCycle +": " ;
		Rescuable r = d.getTarget();
		if (r instanceof Citizen)
		{
			Citizen c = (Citizen)r;
			s+= "The citizen " + c.getName() + " was struck by " + d.getClass().getSimpleName()+" disaster.\n";
		}
		else
		{
			ResidentialBuilding b = (ResidentialBuilding)r;
			s+= "The building in location (" + b.getLocation().getX()+","+b.getLocation().getY() + ") was struck by " + d.getClass().getSimpleName()+" disaster.\n";
		}
		this.GUI.addLogInfo(s);
	}

	public void logDeathWriter(Citizen c)
	{
		String s = "Cycle "+this.displayCurrentCycle +": citizen "+c.getName()+" has died in position (" + c.getLocation().getX()+ ","+c.getLocation().getY() + ")\n";
		this.GUI.addLogInfo(s);
	}

	public void handleDeadDestructed() {
		for(int i=0;i<visibleBuildings.size();i++) {
			if(visibleBuildings.get(i).getStructuralIntegrity()==0) {
				this.movedFrom(visibleBuildings.get(i));
				visibleBuildings.get(i).setImage(RescueSimulationReceived.ImageResizer(new ImageIcon("Destruction.png"),GUI.comboBoxWidth,GUI.comboBoxHeight));
				this.movedTo(visibleBuildings.get(i));
			}
			for(int k=0;k<visibleBuildings.get(i).getOccupants().size();k++) {
				if(visibleBuildings.get(i).getOccupants().get(k).getState()==CitizenState.DECEASED) {
					Citizen c =visibleBuildings.get(i).getOccupants().get(k);
					if (!c.isDead())
					{
						this.logDeathWriter(c);
						c.setDead(true);
						this.movedFrom(c);
						c.setImage(RescueSimulationReceived.ImageResizer(new ImageIcon("Death.png"),GUI.comboBoxWidth,GUI.comboBoxHeight));
						this.movedTo(c);
					}
				}
			}
		}
		for(int j=0;j<visibleCitizens.size();j++) {
			if(visibleCitizens.get(j).getState()==CitizenState.DECEASED) {
				Citizen c =visibleCitizens.get(j);
				if (!c.isDead())
				{
					this.logDeathWriter(c);
					c.setDead(true);
					this.movedFrom(c);
					c.setImage(RescueSimulationReceived.ImageResizer(new ImageIcon("Death.png"),GUI.comboBoxWidth,GUI.comboBoxHeight));
					this.movedTo(c);
				}
			}
		}
	}
	public void handleDisasterException(Exception f) {
		if ( f instanceof BuildingAlreadyCollapsedException ) {
			BuildingAlreadyCollapsedException e= (BuildingAlreadyCollapsedException)f;
			Disaster d=e.getDisaster();
			ResidentialBuilding b = (ResidentialBuilding) d.getTarget();
			JOptionPane.showMessageDialog(GUI.getMainFrame(), "A disaster of type "+d.getClass().getSimpleName()+" did not strike as the building in location ("+b.getLocation().getX()+","+b.getLocation().getY()+") has already collapsed.");
		}
		if (f instanceof CitizenAlreadyDeadException ) {
			CitizenAlreadyDeadException e= (CitizenAlreadyDeadException)f;
			Disaster d=e.getDisaster();
			Citizen c = (Citizen) d.getTarget();
			JOptionPane.showMessageDialog(GUI.getMainFrame(), "A disaster of type "+d.getClass().getSimpleName()+" did not strike as the Citizen " +c.getName()+ " has already died.");

		}
	}

	public void setupHelp(Object o)
	{
		if (o instanceof ArrayList<?>)
		{
			ArrayList<?> x = (ArrayList<?>) o;
			if (x == null || x.size()==0)
			{
				return;
			}
			if (x.get(0) instanceof Citizen && isVisibleCitizen)
			{
				visibleCitizens = (ArrayList<Citizen>) o;
				isVisibleCitizen=false;
				for (Citizen c : visibleCitizens)
				{
					movedTo(c);
				}
			}
			if (x.get(0) instanceof ResidentialBuilding)
			{
				visibleBuildings = (ArrayList<ResidentialBuilding>) o;
				for (ResidentialBuilding b : visibleBuildings)
				{
					movedTo(b);
				}
			}
			if(x.get(0) instanceof Citizen && !isVisibleCitizen) {
				backUpCitizens= (ArrayList<Citizen>) o;
				for (Citizen c : backUpCitizens)
				{	boolean f=false;
					if(visibleCitizens.contains(c)) 
					f=true;
					for(int i=0;i<visibleBuildings.size();i++) {
						for(int j=0;j<visibleBuildings.get(i).getOccupants().size();j++) {
							if(visibleBuildings.get(i).getOccupants().contains(c)) {
								f=true;
								break;
							}
						}
					}
				
				if(!f)
					movedTo(c);
					
				}
			}
			if (x.get(0) instanceof Unit)
			{
				emergencyUnits = (ArrayList<Unit>) o;
				for (Unit u : emergencyUnits)
				{
					movedTo(u);
					GUI.loadUnits(u);
					GUI.handleTreatingResponding();
				}
			}
		}
		else if( o instanceof String && isLog) {
			String x =(String)o;
			this.GUI.addLogInfo(x);
			this.isLog=false;
		}
		else if(o instanceof String && isDisaster){
			this.GUI.writeDisaster((String) o);
			isDisaster=false;
		}
		else if(o instanceof Integer && isCycle){
			this.GUI.updateCycle((int)o);
			isCycle=false;

		}
		else if(o instanceof Integer && isCasualities){
			this.GUI.updateCasualties((int)o);
			isCasualities=false;

		}

	}

}
