package controller;

import java.awt.Color;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

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
import view.RescueSimulationView;

public class CommandCenter implements SOSListener,ActionListener,Serializable {

	private RescueSimulationView GUI; 
	private Simulator engine;
	private ArrayList<ResidentialBuilding> visibleBuildings;
	private ArrayList<Citizen> visibleCitizens;
	private ArrayList<Unit> emergencyUnits;
	private  int displayCurrentCycle;
	private int displayCasualties;
	private boolean unitButtonPressed;
	private RescueSimulationButton clickedUnitButton;
	private ArrayList<Citizen> backUpCitizens;
	private static Server receive;


	public RescueSimulationView getGUI() {
		return GUI;
	}
	public void setGUI(RescueSimulationView gUI) {
		GUI = gUI;
	}



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

	public CommandCenter() throws Exception {
		GUI = new RescueSimulationView(this);
		engine = new Simulator(this);
		visibleBuildings = new ArrayList<ResidentialBuilding>();
		visibleCitizens = new ArrayList<Citizen>();
		backUpCitizens =new ArrayList<Citizen>();
		emergencyUnits = engine.getEmergencyUnits();
		this.loadUnitsGUI();
		this.receiveComHelper();
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

	public static void main(String[] args) throws Exception
	{

		CommandCenter CC = new CommandCenter();


	}

	@Override
	public void actionPerformed(ActionEvent e){
		if(e.getActionCommand().equals("Next Cycle"))
		{
			this.nextCycleHelper();

			return;
		}
		if(e.getActionCommand().equals("Send Communications"))
		{
			this.sendComHelper();

			return;
		}
		if(e.getActionCommand().equals("Receive Communications"))
		{
			receive.setVisible(true);
			receive.setOnline(true);
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
				visibleBuildings.get(i).setImage(RescueSimulationView.ImageResizer(new ImageIcon("Destruction.png"),GUI.comboBoxWidth,GUI.comboBoxHeight));
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
						c.setImage(RescueSimulationView.ImageResizer(new ImageIcon("Death.png"),GUI.comboBoxWidth,GUI.comboBoxHeight));
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
					c.setImage(RescueSimulationView.ImageResizer(new ImageIcon("Death.png"),GUI.comboBoxWidth,GUI.comboBoxHeight));
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

	public void receiveComHelper()
	{
		receive = new Server(this);
		Thread receiver= new Thread(receive);
		receive.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if(receive.isConnected())
					receive.sendMessage("END");
				receive.setVisible(false);    
				receive.setOnline(false);
			}
		});
		receiver.start();
	}

	public void sendComHelper()
	{
		Client send;
		String Ip =JOptionPane.showInputDialog(null,"Please type in the IP you want to connect to:", null);
		if(Ip==null || Ip.isEmpty()) {
			return;
		}
		CountDownLatch latch = new CountDownLatch(1);
		send = new Client(Ip, latch);
		send.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if(!send.notConnected)
					send.sendMessage("END");
				send.dispose();
			}
		});
		Thread sender = new Thread(send);
		sender.start();
		try {
			latch.await();
		} catch (InterruptedException e) {
		}
		//DONT TOUCH THIS PART ORDER IS CRUSIAL AS I RECEIVE IN SAME ORDER
		if(send.notConnected)
			return;
		send.sendData(visibleBuildings);
		send.sendData(visibleCitizens);
		send.sendData(emergencyUnits);
		send.sendData(backUpCitizens);
		send.sendData(GUI.getlog());
		send.sendData(GUI.getDisaster());
		send.sendData(this.getDisplayCurrentCycle());
		send.sendData(this.getDisplayCasualties());
	}


}
