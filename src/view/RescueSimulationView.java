package view;
import javax.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import controller.CommandCenter;
import model.units.Unit;
import model.units.UnitState;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;


public class RescueSimulationView implements Serializable{
	private RescueSimulationFrame mainFrame;
	private RescueSimulationPanel[][] myMapLocations;
	private RescueSimulationComboBox[][] myComboLocations;
	private RescueSimulationPanel gameOverPanel;
	private RescueSimulationPanel mainPanel;
	private RescueSimulationPanel gameOnPanel;
	private CardLayout Card;
	private RescueSimulationPanel eastPanel;
	private RescueSimulationPanel westPanel;
	private RescueSimulationPanel mapPanel;
	private RescueSimulationButton nextCycleButton;
	private RescueSimulationButton receiveCommunicationsButton;
	private RescueSimulationButton sendCommunicationsButton;
	private RescueSimulationLabel cycleNumberLabel;
	private RescueSimulationLabel numberOfCasualitiesLabel;
	private RescueSimulationPanel unitsPanel;
	private RescueSimulationScrollPane logScrollPane;
	private RescueSimulationScrollPane infoScrollPane;
	private RescueSimulationScrollPane disasterScrollPane;
	private JScrollPane test;
	private JScrollPane test2;
	private RescueSimulationPanel unitsPanelFirstDiv;
	private RescueSimulationPanel unitsPanelSecondDiv;
	private RescueSimulationPanel CycleCasualtiesPanel;
	private RescueSimulationPanel mainMapPanel;
	private ActionListener emergencyService;
	private ArrayList<RescueSimulationButton> unitsIdle;
	private ArrayList<RescueSimulationButton> unitsTreating;
	public static int comboBoxWidth=100;
	public static int comboBoxHeight=100;



	public void handleTreatingResponding() {
		for(int i=0;i<unitsIdle.size();i++) {
			if(unitsIdle.get(i).getUnit().getState()==UnitState.TREATING ||unitsIdle.get(i).getUnit().getState()==UnitState.RESPONDING) {
				unitsTreating.add(unitsIdle.get(i));
				unitsPanelFirstDiv.remove(unitsIdle.get(i));
				unitsIdle.remove(unitsIdle.get(i));
				unitsPanelSecondDiv.add(unitsTreating.get(unitsTreating.size()-1));
			}
		}
		for(int j=0;j<unitsTreating.size();j++) {
			if(unitsTreating.get(j).getUnit().getState()==UnitState.IDLE) {
				unitsIdle.add(unitsTreating.get(j));
				unitsPanelSecondDiv.remove(unitsTreating.get(j));
				unitsTreating.remove(unitsTreating.get(j));
				unitsPanelFirstDiv.add(unitsIdle.get(unitsIdle.size()-1));
			}
		}
	}

	public RescueSimulationFrame getMainFrame() {
		return mainFrame;
	}

	public void addLogInfo(String s)
	{
		this.logScrollPane.addInfo(s);
	}
	public ArrayList<RescueSimulationButton> getUnits() {
		return unitsIdle;
	}

	public void updateCycle(int c)
	{
		cycleNumberLabel.setText("Cycle Number: "+c);
	}

	public void updateCasualties(int c)
	{
		this.numberOfCasualitiesLabel.setText("Number of Casualties: "+c);
	}

	public RescueSimulationView(CommandCenter l) {

		this.emergencyService = l;
		//Creating the mainFrame that holds all componenets
		mainFrame=new RescueSimulationFrame();
		mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		mainFrame.setUndecorated(true);
		//Giving the Frame title
		mainFrame.setTitle("Rescue Simulation Game");
		//Setting Default Close option to close
		mainFrame.setDefaultCloseOperation(3);
		// Make the frame Visible and thus accessable
		mainFrame.setVisible(true);
		//Setting Size to be Full Screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		mainFrame.setSize(screenSize);
		//Creating a Panel to be mainPanel and be the first layer
		mainPanel=new RescueSimulationPanel();
		Card=new CardLayout();
		mainPanel.setLayout(Card);
		gameOnPanel = new RescueSimulationPanel();
		gameOverPanel = new RescueSimulationPanel();
		gameOnPanel.setLayout(new RescueSimulationGridLayout(1,3));
		mainFrame.add(mainPanel);
		mainPanel.add(gameOnPanel);
		mainPanel.add(gameOverPanel);
		//Creating three panels to divde gameOn panel into east and west and middle
		eastPanel=new RescueSimulationPanel();
		westPanel=new RescueSimulationPanel();
		mapPanel=new RescueSimulationPanel();

		eastPanel.setPreferredSize(new Dimension((int)screenSize.getWidth()*2/10,(int)screenSize.getHeight()));
		westPanel.setPreferredSize(new Dimension((int)screenSize.getWidth()*2/10,(int)screenSize.getHeight()));
		mapPanel.setPreferredSize(new Dimension((int)screenSize.getWidth()*6/10,(int)screenSize.getHeight()));
		gameOnPanel.add(westPanel);
		gameOnPanel.add(mapPanel);
		gameOnPanel.add(eastPanel);

		// dividing the east panel into units panel and logpanel
		eastPanel.setLayout(new RescueSimulationGridLayout(2,1));
		unitsPanel =new RescueSimulationPanel();
		logScrollPane=new RescueSimulationScrollPane();
		eastPanel.add(unitsPanel);
		eastPanel.add(logScrollPane);



		// dividing the west panel into infopanel and disasters panel	
		westPanel.setLayout(new RescueSimulationGridLayout(2,1));
		infoScrollPane = new RescueSimulationScrollPane();
		titleSetter(infoScrollPane, "Information");
		disasterScrollPane=new RescueSimulationScrollPane();
		titleSetter(disasterScrollPane, "Disaster");
		westPanel.add(infoScrollPane);
		westPanel.add(disasterScrollPane);


		//	dividing the units panel into two panels, free and responding
		unitsPanelFirstDiv=new RescueSimulationPanel();
		test = new JScrollPane(unitsPanelFirstDiv);
		unitsPanelFirstDiv.setLayout(new GridLayout(4,0));
		unitsPanelSecondDiv=new RescueSimulationPanel();
		test2=new JScrollPane(unitsPanelSecondDiv);
		unitsPanelSecondDiv.setLayout(new GridLayout(4,0));
		unitsPanel.setLayout(new RescueSimulationGridLayout(2, 1));
		unitsPanel.add(this.test);
		titleSetter(unitsPanelFirstDiv,"Available Units");
		titleSetter(unitsPanelSecondDiv, "Treating Units");
		titleSetter(logScrollPane, "Game log");		
		this.unitsPanel.add(this.test2);
		this.unitsIdle= new ArrayList<RescueSimulationButton>();
		this.unitsTreating=new ArrayList<RescueSimulationButton>();
		//Dividing the Map Panel
		CycleCasualtiesPanel= new RescueSimulationPanel();
		mainMapPanel=new RescueSimulationPanel();
		CycleCasualtiesPanel.setPreferredSize(new Dimension((int)screenSize.getWidth()*6/10,(int)(Math.ceil(screenSize.getHeight()/20))));
		mainMapPanel.setPreferredSize(new Dimension((int)screenSize.getWidth()*6/10,(int)Math.ceil(screenSize.getHeight()*19/20)));
		mapPanel.add(CycleCasualtiesPanel);
		mapPanel.add(mainMapPanel);
		titleSetter(mainMapPanel, "Map");		
		//dividing the casualtiespanel into three
		CycleCasualtiesPanel.setLayout(new RescueSimulationGridLayout(1,5));
		nextCycleButton =new RescueSimulationButton("Next Cycle");
		nextCycleButton.addActionListener(this.emergencyService);
		sendCommunicationsButton =new RescueSimulationButton("Send Communications");
		sendCommunicationsButton.addActionListener(this.emergencyService);
		receiveCommunicationsButton =new RescueSimulationButton("Receive Communications");
		receiveCommunicationsButton.addActionListener(this.emergencyService);
		numberOfCasualitiesLabel=new RescueSimulationLabel("Number of Casualties: 0");
		numberOfCasualitiesLabel.setVerticalAlignment(JLabel.CENTER);
		numberOfCasualitiesLabel.setHorizontalAlignment(JLabel.CENTER);
		numberOfCasualitiesLabel.setHorizontalTextPosition(0);
		cycleNumberLabel=new RescueSimulationLabel("Cycle Number: 0");
		cycleNumberLabel.setHorizontalAlignment(JLabel.CENTER);
		cycleNumberLabel.setVerticalAlignment(JLabel.CENTER);
		CycleCasualtiesPanel.add(numberOfCasualitiesLabel);
		CycleCasualtiesPanel.add(nextCycleButton);
		CycleCasualtiesPanel.add(sendCommunicationsButton);
		CycleCasualtiesPanel.add(receiveCommunicationsButton);
		CycleCasualtiesPanel.add(cycleNumberLabel);
		//Setting the Map to Have Panels to be able to access
		mainMapPanel.setLayout(new GridLayout(10,10));
		myMapLocations = new RescueSimulationPanel[10][10];

		for(int m = 0; m < 10; m++) {
			for(int n = 0; n < 10; n++) {
				myMapLocations[m][n] = new RescueSimulationPanel();
				//This may seen meaningless but it makes the Button Fills the Whole Panel which is required
				myMapLocations[m][n].setLayout(new GridLayout(1,1));
				mainMapPanel.add(myMapLocations[m][n]);
			}
		}
		this.LoadComboBox();
		this.comboBoxWidth=this.myComboLocations[0][0].getWidth();
		this.comboBoxHeight=this.myComboLocations[0][0].getHeight();
		

	}



	public static ImageIcon ImageResizer( ImageIcon icon ,int width,int height) {
		Image img=icon.getImage();
		Image newimg = img.getScaledInstance(width, height,  java.awt.Image.SCALE_SMOOTH ) ;
		return new ImageIcon(newimg);


	}
	public void LoadComboBox() {
		myComboLocations=new RescueSimulationComboBox[10][10];
		for(int i=0;i<10;i++) {
			for(int j=0;j<10;j++) {
				this.myComboLocations[i][j]=new RescueSimulationComboBox();
				this.myMapLocations[i][j].add(this.myComboLocations[i][j]);
				this.myComboLocations[i][j].addActionListener(this.emergencyService);
				
			}
		}

		mainFrame.setVisible(true);
	}
	public  void GameOverGetter() {
		//GameOver Screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		gameOverPanel.setLayout(new RescueSimulationGridLayout(2,1));
		RescueSimulationLabel L=new RescueSimulationLabel();
		ImageIcon t= ImageResizer(new ImageIcon("GameOver.png"),(int)screenSize.getWidth(),(int)screenSize.getHeight());
		L.setIcon(t);
		L.setPreferredSize(new Dimension((int)screenSize.getWidth(),(int)screenSize.getHeight()*15/20));
		CommandCenter C =(CommandCenter)this.emergencyService;
		JTextArea A= new JTextArea("Congratualtions! You have saved alot of people but you lost: " + C.getDisplayCasualties()+" Citizen");
		Font font1 = new Font("SansSerif", Font.BOLD,25);
		
		A.setFont(font1);
		A.setForeground(Color.red);
		A.setCaretColor(Color.red);
		A.setBackground(Color.BLACK);
		A.setEditable(false);
		gameOverPanel.add(A);
		gameOverPanel.add(L);
		Card.next(mainPanel);

	}


	public void loadUnits(Unit unit) {
		ImageIcon image = unit.getImage();
		unitsIdle.add(new RescueSimulationButton());
		unitsIdle.get(unitsIdle.size()-1).setIcon(image);
		unitsIdle.get(unitsIdle.size()-1).setOpaque(false);
		unitsIdle.get(unitsIdle.size()-1).setContentAreaFilled(false);
		unitsIdle.get(unitsIdle.size()-1).setBorderPainted(false);
		unitsIdle.get(unitsIdle.size()-1).addActionListener(this.emergencyService);
		unitsIdle.get(unitsIdle.size()-1).setUnit(unit);
		this.unitsPanelFirstDiv.add(unitsIdle.get(unitsIdle.size()-1));
		mainFrame.setVisible(true);
	}
	public void infoSetter(String s) {
		this.infoScrollPane.setInfo(s);
	}
	public void addToComboBox(int i, int j, ImageIcon I)
	{
		RescueSimulationComboBox x = myComboLocations[i][j];
		x.addItem(I);
		this.infoSetter("");
		x.first=true;
		x.setSelectedIndex(0);
	}

	public void removeFromComboBox(int i, int j, ImageIcon I)
	{
		RescueSimulationComboBox x = myComboLocations[i][j];
		x.removeItem(I);
	}
	public void writeDisaster(String s) {
		this.disasterScrollPane.setInfo(s);
	}

	public void ValidateGUI() {
		mainFrame.validate();
		mainFrame.repaint();
		gameOverPanel.validate();
		gameOverPanel.repaint();
		mainPanel.validate();
		mainPanel.repaint();
		gameOnPanel.validate();
		gameOnPanel.repaint();
		eastPanel.repaint();
		eastPanel.validate();
		westPanel.validate();
		westPanel.repaint();
		mapPanel.repaint();
		mapPanel.validate();
		nextCycleButton.validate();
		nextCycleButton.repaint();
		cycleNumberLabel.repaint();
		cycleNumberLabel.validate();
		CycleCasualtiesPanel.validate();
		CycleCasualtiesPanel.repaint();
		cycleNumberLabel.repaint();
		cycleNumberLabel.validate();
		numberOfCasualitiesLabel.validate();
		numberOfCasualitiesLabel.repaint();
		unitsPanel.validate();
		unitsPanel.repaint();
		logScrollPane.repaint();
		logScrollPane.validate();
		infoScrollPane.validate();
		infoScrollPane.repaint();
		disasterScrollPane.validate();
		disasterScrollPane.repaint();
		unitsPanelFirstDiv.repaint();
		unitsPanelFirstDiv.validate();
		unitsPanelFirstDiv.validate();
		unitsPanelFirstDiv.repaint();
		unitsPanelSecondDiv.repaint();
		unitsPanelSecondDiv.validate();
		CycleCasualtiesPanel.validate();
		CycleCasualtiesPanel.repaint();
		mainMapPanel.repaint();
		mainMapPanel.validate();
		for(int i=0;i<myMapLocations.length;i++) {
			for(int j=0;j<myMapLocations[i].length;j++) {
				myMapLocations[i][j].validate();
				myMapLocations[i][j].repaint();
			}
		}
		for(int k=0;k<myComboLocations.length;k++) {
			for(int m=0;m<myComboLocations[k].length;m++) {
				myComboLocations[k][m].revalidate();
				myComboLocations[k][m].repaint();

			}

		}
		for(int t=0;t<unitsIdle.size();t++) {
			unitsIdle.get(t).revalidate();
			unitsIdle.get(t).repaint();
		}

	}
	public void titleSetter(Object x,String s) {
		TitledBorder T=BorderFactory.createTitledBorder(s);
		T.setTitleJustification(TitledBorder.CENTER);
		T.setTitleFont(new Font("times new roman", Font.BOLD, 16));
		if(x instanceof RescueSimulationPanel)
			((RescueSimulationPanel)x).setBorder(T);
		else 
			((RescueSimulationScrollPane)x).setBorder(T);

	}
	public String getlog() {
		return logScrollPane.getInfo();
	}
	public String getDisaster() {
		return disasterScrollPane.getInfo();
	}

}



