package model.infrastructure;

import java.util.ArrayList;

import javax.swing.ImageIcon;

import controller.CommandCenter;
import model.disasters.Disaster;
import model.disasters.GasLeak;
import model.events.SOSListener;
import model.people.Citizen;
import simulation.Address;
import simulation.Rescuable;
import simulation.Simulatable;
import view.RescueSimulationView;

public class ResidentialBuilding implements Rescuable, Simulatable 
{

	private Address location;
	private int structuralIntegrity;
	private int fireDamage;
	private int gasLevel;
	private int foundationDamage;
	private ArrayList<Citizen> occupants;
	private Disaster disaster;
	private SOSListener emergencyService;
	private ImageIcon image;
	public ImageIcon getImage() {
		return image;
	}
	public void setImage(ImageIcon image) {
		this.image = image;
	}
	public ResidentialBuilding(Address location) {
		this.location = location;
		this.structuralIntegrity=100;
		occupants= new ArrayList<Citizen>();
		this.image=this.image=RescueSimulationView.ImageResizer(new ImageIcon("Building"+(int)(Math.random()*6+1)+".png"), RescueSimulationView.comboBoxWidth, RescueSimulationView.comboBoxHeight);
				

	}
	public int getStructuralIntegrity() {
		return structuralIntegrity;
	}
	public void setStructuralIntegrity(int structuralIntegrity) {
		this.structuralIntegrity = structuralIntegrity;
		if(structuralIntegrity<=0)
		{
			this.structuralIntegrity=0;
			for(int i = 0 ; i< occupants.size(); i++)
				occupants.get(i).setHp(0);
		}
	}
	public int getFireDamage() {
		return fireDamage;
	}
	public void setFireDamage(int fireDamage) {
		this.fireDamage = fireDamage;
		if(fireDamage<=0)
			this.fireDamage=0;
		else if(fireDamage>=100)
			this.fireDamage=100;
	}
	public int getGasLevel() {
		return gasLevel;
	}
	public void setGasLevel(int gasLevel) {
		this.gasLevel = gasLevel;
		if(this.gasLevel<=0)
			this.gasLevel=0;
		else if(this.gasLevel>=100)
		{
			this.gasLevel=100;
			for(int i = 0 ; i < occupants.size(); i++)
			{
				occupants.get(i).setHp(0);
			}
		}
	}
	public int getFoundationDamage() {
		return foundationDamage;
	}
	public void setFoundationDamage(int foundationDamage) {
		this.foundationDamage = foundationDamage;
		if(this.foundationDamage>=100)
		{
			
			setStructuralIntegrity(0);
		}
			
	}
	public Address getLocation() {
		return location;
	}
	public ArrayList<Citizen> getOccupants() {
		return occupants;
	}
	public Disaster getDisaster() {
		return disaster;
	}
	public void setEmergencyService(SOSListener emergency) {
		this.emergencyService = emergency;
	}
	@Override
	public void cycleStep() {
	
		if(foundationDamage>0)
		{
			
			int damage= (int)((Math.random()*6)+5);
			setStructuralIntegrity(structuralIntegrity-damage);
			
		}
		if(fireDamage>0 &&fireDamage<30)
			setStructuralIntegrity(structuralIntegrity-3);
		else if(fireDamage>=30 &&fireDamage<70)
			setStructuralIntegrity(structuralIntegrity-5);
		else if(fireDamage>=70)
			setStructuralIntegrity(structuralIntegrity-7);
		
	}
	
	@Override
	public void struckBy(Disaster d) {
		//MS3
		//log writer
		CommandCenter CC = (CommandCenter)this.emergencyService;
		CC.logDisasterWriter(d);
		//end
		if(disaster!=null)
			disaster.setActive(false);
		disaster=d;
		emergencyService.receiveSOSCall(this);
	}
	public  String toString() {
		String s="Location: (" +this.getLocation().getX()+","+this.getLocation().getY()+")."+"\n"
				+"Structural Integrity: "+this.getStructuralIntegrity()+"."+"\n"
				+"Fire Damage: "+this.getFireDamage()+"."+"\n"
				+"Gas Level: "+this.getGasLevel()+"."+"\n"
				+"Foundation Damage: "+this.getFoundationDamage()+"."+"\n"
				+"Number of Occupants: "+this.getOccupants().size()+"."+"\n"+"-------------------------------"+"\n";;
		if(!this.occupants.isEmpty()) {
			
			s+="Occupants Info: "+"\n";
		}
				
		for(int i=0;i<this.occupants.size();i++) {
			s+=this.occupants.get(i).toString()+"\n";
			s+="-------------------------------"+"\n";
			
		}
		if(this.getDisaster()!=null) {
			s+="Disaster on Building: "+"\n"+this.getDisaster();
		}
		return s;
	}
}
