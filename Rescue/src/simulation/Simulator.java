package simulation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;

import controller.CommandCenter;
import exceptions.BuildingAlreadyCollapsedException;
import exceptions.CitizenAlreadyDeadException;
import model.disasters.Collapse;
import model.disasters.Disaster;
import model.disasters.Fire;
import model.disasters.GasLeak;
import model.disasters.Infection;
import model.disasters.Injury;
import model.events.SOSListener;
import model.events.WorldListener;
import model.infrastructure.ResidentialBuilding;
import model.people.Citizen;
import model.people.CitizenState;
import model.units.Ambulance;
import model.units.DiseaseControlUnit;
import model.units.Evacuator;
import model.units.FireTruck;
import model.units.GasControlUnit;
import model.units.Unit;
import model.units.UnitState;
import view.RescueSimulationView;

public class Simulator implements WorldListener,Serializable {
	private int currentCycle;
	private ArrayList<ResidentialBuilding> buildings;
	private ArrayList<Citizen> citizens;
	private ArrayList<Unit> emergencyUnits;
	private ArrayList<Disaster> plannedDisasters;
	private ArrayList<Disaster> executedDisasters;
	private Address[][] world;
	private SOSListener emergencyService;

	public Simulator(SOSListener l) throws Exception {
		emergencyService = l;
		
		buildings = new ArrayList<ResidentialBuilding>();
		citizens = new ArrayList<Citizen>();
		emergencyUnits = new ArrayList<Unit>();
		plannedDisasters = new ArrayList<Disaster>();
		executedDisasters = new ArrayList<Disaster>();

		world = new Address[10][10];
		for (int i = 0; i < 10; i++)
			for (int j = 0; j < 10; j++)
				world[i][j] = new Address(i, j);

		loadUnits("units.csv");
		loadBuildings("buildings.csv");
		loadCitizens("citizens.csv");
		loadDisasters("disasters.csv");
		for (int i = 0; i < buildings.size(); i++) {
			ResidentialBuilding building = buildings.get(i);
			for (int j = 0; j < citizens.size(); j++) {
				Citizen citizen = citizens.get(j);
				if (citizen.getLocation() == building.getLocation())
					building.getOccupants().add(citizen);
			}
		}
	}

	private void loadUnits(String path) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(path));
		String line = br.readLine();
		while (line != null) {
			String[] info = line.split(",");
			String id = info[1];
			int steps = Integer.parseInt(info[2]);
			switch (info[0]) {
			case "AMB": {
				Ambulance a = new Ambulance(id, world[0][0], steps, this);
				emergencyUnits.add(a);

			}
			break;
			case "DCU": {
				DiseaseControlUnit d = new DiseaseControlUnit(id, world[0][0],
						steps, this);
				emergencyUnits.add(d);
			}
			break;
			case "EVC": {
				Evacuator e = new Evacuator(id, world[0][0], steps, this,
						Integer.parseInt(info[3]));
				emergencyUnits.add(e);
			}
			break;
			case "FTK": {
				FireTruck f = new FireTruck(id, world[0][0], steps, this);
				emergencyUnits.add(f);
			}
			break;
			case "GCU": {
				GasControlUnit g = new GasControlUnit(id, world[0][0], steps,
						this);
				emergencyUnits.add(g);
			}
			break;

			}
			line = br.readLine();
		}
		br.close();

	}

	private void loadBuildings(String path) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(path));
		String line = br.readLine();
		while (line != null) {
			String[] info = line.split(",");
			int x = Integer.parseInt(info[0]);
			int y = Integer.parseInt(info[1]);
			ResidentialBuilding b = new ResidentialBuilding(world[x][y]);
			b.setEmergencyService(emergencyService);
			buildings.add(b);
			line = br.readLine();
		}
		br.close();
	}

	private void loadCitizens(String path) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(path));
		String line = br.readLine();
		while (line != null) {
			String[] info = line.split(",");
			int x = Integer.parseInt(info[0]);
			int y = Integer.parseInt(info[1]);
			String id = info[2];
			String name = info[3];
			int age = Integer.parseInt(info[4]);
			Citizen c = new Citizen(world[x][y], id, name, age, this);
			c.setEmergencyService(emergencyService);
			citizens.add(c);
			line = br.readLine();
		}
		br.close();
	}

	private void loadDisasters(String path) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(path));
		String line = br.readLine();
		while (line != null) {
			String[] info = line.split(",");
			int startCycle = Integer.parseInt(info[0]);
			ResidentialBuilding building = null;
			Citizen citizen = null;
			if (info.length == 3)
				citizen = getCitizenByID(info[2]);
			else {
				int x = Integer.parseInt(info[2]);
				int y = Integer.parseInt(info[3]);
				building = getBuildingByLocation(world[x][y]);
			}
			switch (info[1]) {
			case "INJ":
				plannedDisasters.add(new Injury(startCycle, citizen));
				break;
			case "INF":
				plannedDisasters.add(new Infection(startCycle, citizen));
				break;
			case "FIR":
				plannedDisasters.add(new Fire(startCycle, building));
				break;
			case "GLK":
				plannedDisasters.add(new GasLeak(startCycle, building));
				break;
			}
			line = br.readLine();
		}
		br.close();
	}

	private Citizen getCitizenByID(String id) {
		for (int i = 0; i < citizens.size(); i++) {
			if (citizens.get(i).getNationalID().equals(id))
				return citizens.get(i);
		}
		return null;
	}

	private ResidentialBuilding getBuildingByLocation(Address location) {
		for (int i = 0; i < buildings.size(); i++) {
			if (buildings.get(i).getLocation() == location)
				return buildings.get(i);
		}
		return null;
	}

	@Override
	public void assignAddress(Simulatable s, int x, int y) {
		//MS3: movedfrom and to was added
		CommandCenter CC = (CommandCenter)this.emergencyService;
		CC.movedFrom(s);
		if (s instanceof Citizen)
			((Citizen) s).setLocation(world[x][y]);
		
		else
			((Unit) s).setLocation(world[x][y]);
		//CC.handleDeadDestructed();
		CC.movedTo(s);

	}

	public void setEmergencyService(SOSListener emergency) {
		this.emergencyService = emergency;
	}

	public void nextCycle() {

		currentCycle++;
	//MS3: 
		//Added to Show Current Cycle
			CommandCenter C= (CommandCenter)this.emergencyService;
			C.setDisplayCurrentCycle(this.currentCycle);
		//end
		
		//Calculates casualties
			int total = 0;
			for (Citizen c : this.citizens)
			{
				if (c.getState()==CitizenState.DECEASED)
					total++;
			}
			C.setDisplayCasualties(total);
		//end
	//end

		for (int i = 0; i < plannedDisasters.size(); i++) {
			Disaster d = plannedDisasters.get(i);
			if (d.getStartCycle() == currentCycle) {
				plannedDisasters.remove(d);
				i--;
				if (d instanceof Fire) {
					try {
					handleFire(d);}
					catch (Exception e) {
						C.handleDisasterException(e);	
						}
					}
				else if (d instanceof GasLeak) {
					try {
					handleGas(d);}
					catch (Exception e) {
						C.handleDisasterException(e);	
						}}
				else {
					try {
					d.strike();}
					catch (Exception e) {
					C.handleDisasterException(e);	
					}
					executedDisasters.add(d);
				}
			}
		}

		for (int i = 0; i < buildings.size(); i++) {
			ResidentialBuilding b = buildings.get(i);
			if (b.getFireDamage() >= 100) {
				b.getDisaster().setActive(false);
				b.setFireDamage(0);
				Collapse c = new Collapse(currentCycle, b);
				try {
				c.strike();}
				catch(Exception e) {
					C.handleDisasterException(e);	
				}
				executedDisasters.add(c);
			}
		}

		for (int i = 0; i < emergencyUnits.size(); i++) {
			emergencyUnits.get(i).cycleStep();
		}

		for (int i = 0; i < executedDisasters.size(); i++) {
			Disaster d = executedDisasters.get(i);
			if (d.getStartCycle() < currentCycle && d.isActive())
				d.cycleStep();
		}

		for (int i = 0; i < buildings.size(); i++) {
			buildings.get(i).cycleStep();
		}

		for (int i = 0; i < citizens.size(); i++) {
			citizens.get(i).cycleStep();
		}
		

	}

	private void handleGas(Disaster d) throws BuildingAlreadyCollapsedException, CitizenAlreadyDeadException {
		ResidentialBuilding b = (ResidentialBuilding) d.getTarget();
		if (b.getFireDamage() != 0) {
			b.setFireDamage(0);
			Collapse c = new Collapse(currentCycle, b);
			c.strike();
			executedDisasters.add(c);
		} else {
			d.strike();
			executedDisasters.add(d);
		}
	}

	private void handleFire(Disaster d) throws BuildingAlreadyCollapsedException, CitizenAlreadyDeadException {
		ResidentialBuilding b = (ResidentialBuilding) d.getTarget();
		if (b.getGasLevel() == 0) {
			d.strike();
			executedDisasters.add(d);
		} else if (b.getGasLevel() < 70) {
			b.setFireDamage(0);
			Collapse c = new Collapse(currentCycle, b);
			c.strike();
			executedDisasters.add(c);
		} else
			b.setStructuralIntegrity(0);

	}

	public boolean checkGameOver() {

		if (plannedDisasters.size() != 0)
			return false;
		for (int i = 0; i < executedDisasters.size(); i++) {
			if (executedDisasters.get(i).isActive()) {

				Disaster d = executedDisasters.get(i);
				Rescuable r = d.getTarget();
				if (r instanceof Citizen) {
					Citizen c = (Citizen) r;
					if (c.getState() != CitizenState.DECEASED)
						return false;
				} else {

					ResidentialBuilding b = (ResidentialBuilding) r;
			if (b.getStructuralIntegrity() != 0)
						return false;
				}

			}

		}

		for (int i = 0; i < emergencyUnits.size(); i++) {
			if (emergencyUnits.get(i).getState() != UnitState.IDLE)
				return false;
		}

		return true;
	}

	public int calculateCasualties() {
		int count = 0;
		for (int i = 0; i < citizens.size(); i++) {
			if (citizens.get(i).getState() == CitizenState.DECEASED)
				count++;
		}
		return count;

	}

	public ArrayList<Unit> getEmergencyUnits() {

		return emergencyUnits;
	}

}
