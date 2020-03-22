package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

import controller.CommandCenter;
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
import model.units.MedicalUnit;
import model.units.Unit;
import model.units.UnitState;
import simulation.Address;
import simulation.Rescuable;
import simulation.Simulator;


@SuppressWarnings({ "rawtypes", "unchecked" })
public class M2PrivateTests {
	Address testAddress1 = new Address(0, 0);
	Address testAddress2 = new Address(1, 1);
	Address testAddress3 = new Address(2, 3);
	Address testAddress4 = new Address(4, 4);

	String id1 = "1";
	String id2 = "2";
	String id3 = "3";
	String id4 = "4";

	String name1 = "test1";
	String name2 = "test2";
	String name3 = "test3";
	String name4 = "test4";

	int age1 = 24;
	int age2 = 25;
	int age3 = 26;
	int age4 = 27;

	Citizen testCitizen1 = new Citizen(testAddress1, id1, name1, age1, null);
	Citizen testCitizen2 = new Citizen(testAddress2, id2, name2, age2, null);
	Citizen testCitizen3 = new Citizen(testAddress3, id3, name3, age3, null);
	Citizen testCitizen4 = new Citizen(testAddress4, id4, name4, age4, null);

	ResidentialBuilding testBuilding1 = new ResidentialBuilding(testAddress1);
	ResidentialBuilding testBuilding2 = new ResidentialBuilding(testAddress2);
	ResidentialBuilding testBuilding3 = new ResidentialBuilding(testAddress3);
	ResidentialBuilding testBuilding4 = new ResidentialBuilding(testAddress4);

	Ambulance testAmbulance = new Ambulance(id1, testAddress1, 1, null);
	Evacuator testEvacutor = new Evacuator(id2, testAddress1, 1, null, 5);
	FireTruck testFireTruck = new FireTruck(id3, testAddress1, 1, null);
	DiseaseControlUnit testDiseaseContorlUnit = new DiseaseControlUnit(id4,
			testAddress1, 1, null);
	GasControlUnit testGasContorlUnit = new GasControlUnit("5", testAddress1,
			1, null);

	Collapse testCollapse = new Collapse(1, testBuilding1);
	Fire testFire = new Fire(3, testBuilding1);
	GasLeak testGasLeak = new GasLeak(3, testBuilding1);
	Infection testiInfection = new Infection(1, testCitizen1);
	Injury testinjInjury = new Injury(2, testCitizen2);

	static final String buildingPath = "model.infrastructure.ResidentialBuilding";
	static final String disasterPath = "model.disasters.Disaster";
	static final String sosListenerPath = "model.events.SOSListener";

	static String simulatorPath = "simulation.Simulator";
	static String addressPath = "simulation.Address";
	static String rescuablePath = "simulation.Rescuable";
	static String simulatablePath = "simulation.Simulatable";
	static String residentialBuildingPath = "model.infrastructure.ResidentialBuilding";
	static String citizenStatePath = "model.people.CitizenState";
	static String unitStatePath = "model.units.UnitState";
	static String citizenPath = "model.people.Citizen";
	static String unitPath = "model.units.Unit";
	static String policeUnitPath = "model.units.PoliceUnit";
	static String fireUnitPath = "model.units.FireUnit";
	static String medicalUnitPath = "model.units.MedicalUnit";
	static String evacuatorPath = "model.units.Evacuator";
	static String fireTruckPath = "model.units.FireTruck";
	static String gasControlUnitPath = "model.units.GasControlUnit";
	static String ambulancePath = "model.units.Ambulance";
	static String diseaseControlUnitPath = "model.units.DiseaseControlUnit";
	static String collapsePath = "model.disasters.Collapse";
	static String firePath = "model.disasters.Fire";
	static String gasLeakPath = "model.disasters.GasLeak";
	static String infectionPath = "model.disasters.Infection";
	static String injuryPath = "model.disasters.Injury";
	static String commandCenterPath = "controller.CommandCenter";
	static String worldListenerPath = "model.events.WorldListener";
	static String sosResponderPath = "model.events.SOSResponder";

	public static boolean treatCalled = false;
	public static boolean healCalled = false;
	@SuppressWarnings("unused")
	private static boolean struckByCalled = false;

	

	Infection testInfection = new Infection(1, testCitizen1);
	Injury testInjury = new Injury(2, testCitizen2);


	HashMap<String, Integer> count;
	SOSListener sos = new SOSListener() {

		@Override
		public void receiveSOSCall(Rescuable r) {

		}
	};

	// HELPERS
	private boolean checkValueLogic(Object createdObject, String name,
			Object value) throws Exception {
		Field f = null;
		Class curr = createdObject.getClass();
		while (f == null) {
			if (curr == Object.class)
				fail("Class " + createdObject.getClass().getSimpleName()
						+ " should have the instance variable \"" + name
						+ "\".");
			try {
				f = curr.getDeclaredField(name);
			} catch (NoSuchFieldException e) {
				curr = curr.getSuperclass();
			}
		}
		f.setAccessible(true);
		if (f.get(createdObject) == null && value == null)
			return true;
		return (f.get(createdObject).equals(value));
	}

	private void testExistsInClass(Class aClass, String methodName,
			boolean implementedMethod, Class returnType, Class... inputTypes) {

		Method[] methods = aClass.getDeclaredMethods();

		if (implementedMethod) {
			assertTrue(
					"The " + methodName + " method in class "
							+ aClass.getSimpleName()
							+ " should be implemented.",
					containsMethodName(methods, methodName));
		} else {
			assertFalse(
					"The "
							+ methodName
							+ " method in class "
							+ aClass.getSimpleName()
							+ " should not be implemented, only inherited from super class.",
					containsMethodName(methods, methodName));
			return;
		}
		Method m = null;
		boolean found = true;
		try {
			m = aClass.getDeclaredMethod(methodName, inputTypes);
		} catch (Exception e) {
			found = false;
		}

		String inputsList = "";
		for (Class inputType : inputTypes) {
			inputsList += inputType.getSimpleName() + ", ";
		}
		if (inputsList.equals(""))
			assertTrue(aClass.getSimpleName() + " class should have "
					+ methodName + " method that takes no parameters.", found);
		else {
			if (inputsList.charAt(inputsList.length() - 1) == ' ')
				inputsList = inputsList.substring(0, inputsList.length() - 2);
			assertTrue(aClass.getSimpleName() + " class should have "
					+ methodName + " method that takes " + inputsList
					+ " parameter(s).", found);
		}

		assertTrue("incorrect return type for " + methodName + " method in "
				+ aClass.getSimpleName() + ".",
				m.getReturnType().equals(returnType));

	}

	



	

	

	private void testConstructorExists(Class aClass, Class[] inputs) {
		boolean thrown = false;
		try {
			aClass.getConstructor(inputs);
		} catch (NoSuchMethodException e) {
			thrown = true;
		}
		if (inputs.length > 0) {
			String msg = "";
			int i = 0;
			do {
				msg += inputs[i].getSimpleName() + " and ";
				i++;
			} while (i < inputs.length);
			msg = msg.substring(0, msg.length() - 4);
			assertFalse(
					"Missing constructor with " + msg + " parameter"
							+ (inputs.length > 1 ? "s" : "") + " in "
							+ aClass.getSimpleName() + " class.", thrown);
		} else
			assertFalse(
					"Missing constructor with zero parameters in "
							+ aClass.getSimpleName() + " class.", thrown);
	}

	private void clearArrayList(Simulator s) throws Exception {
		final Field unitField = Simulator.class
				.getDeclaredField("emergencyUnits");
		unitField.setAccessible(true);
		((ArrayList<Object>) unitField.get(s)).clear();
	}

	private void applyDisaster(Disaster d, ResidentialBuilding b)
			throws Exception {
		final Field disasterField = ResidentialBuilding.class
				.getDeclaredField("disaster");
		disasterField.setAccessible(true);
		disasterField.set(b, d);
	}

	private void init() {
		testBuilding1.setEmergencyService(sos);
		testBuilding2.setEmergencyService(sos);
		testBuilding3.setEmergencyService(sos);
		testBuilding4.setEmergencyService(sos);
		testBuilding1.setStructuralIntegrity(0);
		testCitizen1.setEmergencyService(sos);
		testCitizen2.setEmergencyService(sos);
		testCitizen3.setEmergencyService(sos);
		testCitizen4.setEmergencyService(sos);
	}

	private void testGetterMethodExistsInClass(Class aClass, String methodName,
			Class returnedType, boolean writeVariable) {
		Method m = null;
		boolean found = true;
		try {
			m = aClass.getDeclaredMethod(methodName);
		} catch (Exception e) {
			found = false;
		}
		String varName = "";
		if (returnedType == boolean.class)
			varName = methodName.substring(2).toLowerCase();
		else
			varName = methodName.substring(3).toLowerCase();
		if (writeVariable) {
			assertTrue("The \"" + varName + "\" instance variable in class "
					+ aClass.getSimpleName() + " is a READ variable.", found);
			assertTrue("Incorrect return type for " + methodName
					+ " method in " + aClass.getSimpleName() + " class.", m
					.getReturnType().isAssignableFrom(returnedType));
		} else {
			assertFalse("The \"" + varName + "\" instance variable in class "
					+ aClass.getSimpleName() + " is not a READ variable.",
					found);
		}
	}

	private void testSetterMethodExistsInClass(Class aClass, String methodName,
			Class inputType, boolean writeVariable) {
		Method[] methods = aClass.getDeclaredMethods();
		String varName = methodName.substring(3).toLowerCase();
		if (writeVariable) {
			assertTrue("The \"" + varName + "\" instance variable in class "
					+ aClass.getSimpleName() + " is a WRITE variable.",
					containsMethodName(methods, methodName));
		} else {
			assertFalse("The \"" + varName + "\" instance variable in class "
					+ aClass.getSimpleName() + " is not a WRITE variable.",
					containsMethodName(methods, methodName));
			return;
		}
		Method m = null;
		boolean found = true;
		try {
			m = aClass.getDeclaredMethod(methodName, inputType);
		} catch (NoSuchMethodException e) {
			found = false;
		}
		assertTrue(aClass.getSimpleName() + " class should have " + methodName
				+ " method that takes one " + inputType.getSimpleName()
				+ " parameter.", found);
		assertTrue("Incorrect return type for " + methodName + " method in "
				+ aClass.getSimpleName() + ".",
				m.getReturnType().equals(Void.TYPE));
	}

	private static boolean containsMethodName(Method[] methods, String name) {
		for (Method method : methods) {
			if (method.getName().equals(name))
				return true;
		}
		return false;
	}

	private void testInstanceVariableIsPresent(Class aClass, String varName,
			boolean implementedVar) throws SecurityException {
		boolean thrown = false;
		try {
			aClass.getDeclaredField(varName);
		} catch (NoSuchFieldException e) {
			thrown = true;
		}
		if (implementedVar) {
			assertFalse("There should be \"" + varName
					+ "\" instance variable in class " + aClass.getSimpleName()
					+ ".", thrown);
		} else {
			assertTrue(
					"The instance variable \"" + varName
							+ "\" should not be declared in class "
							+ aClass.getSimpleName() + ".", thrown);
		}
	}

	private void testInstanceVariableIsPrivate(Class aClass, String varName)
			throws NoSuchFieldException, SecurityException {
		Field f = aClass.getDeclaredField(varName);
		assertEquals("The \"" + varName + "\" instance variable in class "
				+ aClass.getSimpleName()
				+ " should not be accessed outside that class.", 2,
				f.getModifiers());
	}

	private static void unitRespond(Unit u, Rescuable r, int dist)
			throws IllegalArgumentException, IllegalAccessException {
		if (r != null && u.getState() == UnitState.TREATING) {

			Disaster curr = r.getDisaster();
			curr.setActive(true);
		}

		Field targetField = null;
		Class curr0 = u.getClass();
		while (targetField == null) {
			if (curr0 == Object.class)
				fail("Class " + u.getClass().getSimpleName()
						+ " should have the instance variable \"" + "disaster"
						+ "\".");
			try {
				targetField = curr0.getDeclaredField("target");
			} catch (NoSuchFieldException e) {
				curr0 = curr0.getSuperclass();
			}
		}
		targetField.setAccessible(true);
		targetField.set(u, r);

		u.setState(UnitState.RESPONDING);
		int distanceToTarget = dist;

		Field f = null;
		Class curr = u.getClass();
		while (f == null) {
			if (curr == Object.class)
				fail("Class " + u.getClass().getSimpleName()
						+ " should have the instance variable \"" + "disaster"
						+ "\".");
			try {
				f = curr.getDeclaredField("distanceToTarget");
			} catch (NoSuchFieldException e) {
				curr = curr.getSuperclass();
			}
		}
		f.setAccessible(true);
		f.set(u, distanceToTarget);

	}

	private static void dshelper(Disaster d) throws IllegalArgumentException,
			IllegalAccessException {
		Rescuable target = d.getTarget();
		if (target instanceof Citizen) {
			Citizen c = (Citizen) target;
			Field f = null;
			Class curr = c.getClass();
			while (f == null) {
				if (curr == Object.class)
					fail("Class " + c.getClass().getSimpleName()
							+ " should have the instance variable \""
							+ "disaster" + "\".");
				try {
					f = curr.getDeclaredField("disaster");
				} catch (NoSuchFieldException e) {
					curr = curr.getSuperclass();
				}
			}
			f.setAccessible(true);
			f.set(c, d);
		} else {
			ResidentialBuilding b = (ResidentialBuilding) target;
			Field f = null;
			Class curr = b.getClass();
			while (f == null) {
				if (curr == Object.class)
					fail("Class " + b.getClass().getSimpleName()
							+ " should have the instance variable \""
							+ "disaster" + "\".");
				try {
					f = curr.getDeclaredField("disaster");
				} catch (NoSuchFieldException e) {
					curr = curr.getSuperclass();
				}
			}
			f.setAccessible(true);
			f.set(b, d);
		}
		d.setActive(true);
	}

	private static Address getAddressFromWorld(Simulator s, int x, int y)
			throws IllegalArgumentException, IllegalAccessException {

		Field f = null;
		Class curr = s.getClass();
		while (f == null) {
			if (curr == Object.class)
				fail("Class " + s.getClass().getSimpleName()
						+ " should have the instance variable \"" + "world"
						+ "\".");
			try {
				f = curr.getDeclaredField("world");
			} catch (NoSuchFieldException e) {
				curr = curr.getSuperclass();
			}
		}
		f.setAccessible(true);
		Address myWorld[][] = (Address[][]) f.get(s);
		return myWorld[x][y];
	}

	private void testSetterLogic(Object createdObject, String name,
			Object value, Class type) throws Exception {
		Field f = null;
		Class curr = createdObject.getClass();
		while (f == null) {

			if (curr == Object.class)
				fail("Class " + createdObject.getClass().getSimpleName()
						+ " should have the instance variable \"" + name
						+ "\".");
			try {
				f = curr.getDeclaredField(name);
			} catch (NoSuchFieldException e) {
				curr = curr.getSuperclass();
			}
		}
		f.setAccessible(true);
		Character c = name.charAt(0);
		String methodName = "set" + Character.toUpperCase(c)
				+ name.substring(1, name.length());
		Method m = createdObject.getClass().getMethod(methodName, type);
		m.invoke(createdObject, value);
		assertEquals(
				"The method \"" + methodName + "\" in class "
						+ createdObject.getClass().getSimpleName()
						+ " should set the correct value of variable \"" + name
						+ "\".", value, f.get(createdObject));
	}

	private Address someRandomAddress() {
		int x = (int) (Math.random() * 11);
		int y = (int) (Math.random() * 11);
		return new Address(x, y);
	}

	

	private boolean AllInRange(ArrayList<Integer> values, int lower, int upper) {
		for (int i = 0; i < values.size(); i++)
			if (!(values.get(i) >= lower && values.get(i) <= upper))
				return false;
		return true;
	}

	private boolean isRandom(ArrayList<Integer> values) {
		int similar = 0;
		for (int i = 0; i < values.size(); i++) {
			for (int j = i + 1; j < values.size(); j++) {
				if (values.get(i).intValue() == values.get(j).intValue())
					similar++;
			}

		}
		if (similar == values.size())
			return false;
		else
			return true;

	}
	private Citizen someRandomCitizen() {

		Address add = someRandomAddress();
		String id = "ID_" + (Math.random() * 2309);
		String name = "Citizen_" + (Math.random() * 2387);
		int age = (int) (Math.random() * 50);

		return new Citizen(add, id, name, age, null);
	}

	public class MyDisaster extends Disaster {

		public MyDisaster(int i, Citizen c1) {
			super(i, c1);
		}

		@Override
		public void cycleStep() {

		}

	}

	class MyMedicalUnit extends MedicalUnit {

		public MyMedicalUnit(String unitID, Address location, int stepsPerCycle) {
			super(unitID, location, stepsPerCycle, null);
		}

		@Override
		public void treat() {

		}

		public void cycleStep() {

		}

	}

	// TESTs :
	@Test(timeout = 1000)
	public void testConstructorDiseaseControlUnit() throws Exception {
		Class[] inputs = { String.class, Address.class, int.class,
				WorldListener.class };
		testConstructorExists(Class.forName(diseaseControlUnitPath), inputs);
	}

	@Test(timeout = 1000)
	public void testConstructorFireTruckUnit() throws Exception {
		Class[] inputs = { String.class, Address.class, int.class,
				WorldListener.class };
		testConstructorExists(Class.forName(fireTruckPath), inputs);
	}

	@Test(timeout = 1000)
	public void testConstructorEvacuatorUnit() throws Exception {
		Class[] inputs = { String.class, Address.class, int.class,
				WorldListener.class, int.class };
		testConstructorExists(Class.forName(evacuatorPath), inputs);
	}

	@Test(timeout = 3000)
	public void testAmbulanceTreat() throws Exception {
		testExistsInClass(Class.forName(ambulancePath), "treat", true,
				void.class);
	}

	@Test(timeout = 3000)
	public void testDiseaseControlUnitTreat() throws Exception {
		testExistsInClass(Class.forName(diseaseControlUnitPath), "treat", true,
				void.class);
	}

	@Test(timeout = 1000)
	public void testUnitRespondFromIdleLogic() throws Exception {
		Unit u = new Unit("unit1", new Address(3, 4), 3, null) {
			@Override
			public void treat() {

			}
		};
		Citizen c = new Citizen(new Address(3, 7), "1", "citizen1", 15, null);
		u.respond(c);
		assertEquals("Unit respond should initialize the target correctly.", c,
				u.getTarget());
		assertTrue(
				"Unit respond should update the distanceToTarget correctly.",
				checkValueLogic(u, "distanceToTarget", 3));
		assertEquals("Unit respond should update the unitState correctly.",
				UnitState.RESPONDING, u.getState());
	}
	

	@Test(timeout = 1000)
	public void testDiseaseControlUnitTreatLogic() throws Exception {
		Simulator s = new Simulator(sos);
		DiseaseControlUnit u = new DiseaseControlUnit("DiseaseControlUnit1", new Address(3, 4), 3, null);
		u.setWorldListener(s);
		Citizen c1 = new Citizen(new Address(3, 9), "1", "citizen1", 15, null);
		Disaster d = new Infection(3, c1);
		dshelper(d);
		c1.setToxicity(50);
		unitRespond(u, c1, 5);

		ArrayList<Integer> toxicityValues = new ArrayList<Integer>();
		toxicityValues.add(40);
		toxicityValues.add(30);
		toxicityValues.add(20);
		toxicityValues.add(10);
		toxicityValues.add(0);
		for (int i = 0; i < 5; i++) {
			u.treat();
			assertTrue("Values of toxicity were wrong, expected "
					+ toxicityValues.get(i) + " but was " + c1.getToxicity()
					+ ".", toxicityValues.get(i) == c1.getToxicity());
		}

		assertTrue(
				"Ambulance should not change the current disaster, it should only activate or deactivate the existing one.",
				checkValueLogic(c1, "disaster", d));

		assertTrue(
				"Ambulance should deactivate the disaster upon starting the treatment process",
				checkValueLogic(d, "active", false));
		assertTrue(
				"Ambulance should mark the citizen as RESCUED after treating their injury.",
				checkValueLogic(c1, "state", CitizenState.RESCUED));
	}
	
	
	@Test(timeout = 3000)
	public void testAmbulanceCallsHeal() throws Exception {
		Simulator s = new Simulator(sos);

		Ambulance u = new Ambulance("ambulance1", new Address(3, 4), 3, null) {

			@Override
			public void heal() {
				healCalled = true;
			}
		};
		u.setWorldListener(s);
		Citizen c1 = new Citizen(new Address(3, 4), "1", "citizen1", 15, null);
		Disaster d = new Injury(3, c1);
		dshelper(d);
		c1.setBloodLoss(10);
		unitRespond(u, c1, 0);
		u.cycleStep();
		u.cycleStep();

		assertTrue(
				"The method cycleStep should call the method heal if the unit arrives to valid target",
				healCalled);
		healCalled = false;
	}
	
	@Test(timeout = 1000)
	public void testUnitCycleStepIdleLogic() throws Exception {
		Simulator s = new Simulator(sos);
		Address ad = new Address(3, 4);
		Unit u1 = new Unit("unit1", ad, 3, null) {
			@Override
			public void treat() {

			}
		};
		u1.setWorldListener(s);
		u1.cycleStep();
		assertTrue("cycleStep should not change the unit state if it is idle.",
				checkValueLogic(u1, "state", UnitState.IDLE));
		assertTrue(
				"cycleStep should not change the unit location if it is idle.",
				checkValueLogic(u1, "location", ad));
		assertTrue(
				"cycleStep should not change the unit target if it is idle.",
				checkValueLogic(u1, "target", null));
		assertTrue(
				"cycleStep should not change the unit distanceToTarget if it is idle.",
				checkValueLogic(u1, "distanceToTarget", 0));
	}
	

	@Test(timeout = 1000)
	public void testGasControlUnitTreatLogic() throws Exception {
		Simulator s = new Simulator(sos);
		GasControlUnit u = new GasControlUnit("GCU1", new Address(3, 4), 3,
				null);
		u.setWorldListener(s);
		ResidentialBuilding c1 = new ResidentialBuilding(new Address(3, 9));
		Disaster d = new GasLeak(3, c1);
		dshelper(d);
		c1.setGasLevel(50);

		unitRespond(u, c1, 5);

		ArrayList<Integer> gasLevelValues = new ArrayList<Integer>();
		gasLevelValues.add(40);
		gasLevelValues.add(30);
		gasLevelValues.add(20);
		gasLevelValues.add(10);
		gasLevelValues.add(0);

		for (int i = 0; i < 5; i++) {
			u.treat();
			assertTrue("Values of building gas level were wrong, expected "
					+ gasLevelValues.get(i) + " but was " + c1.getGasLevel()
					+ ".", gasLevelValues.get(i) == c1.getGasLevel());
		}
		u.cycleStep();
		assertTrue(
				"GasControlUnit should not change the disaster, it should activate it or deactivate it only.",
				checkValueLogic(c1, "disaster", d));
		assertTrue("GasControlUnit treat should deactivate the disaster.",
				checkValueLogic(d, "active", false));
		assertTrue(
				"GasControlUnit unit state should be changed to IDLE once completing the treatment.",
				checkValueLogic(u, "state", UnitState.IDLE));

	}
	

	@Test(timeout = 3000)
	public void testEvacuatorTreatLogicCycle0() throws Exception {
		Simulator s = new Simulator(sos);
		Address ad = getAddressFromWorld(s, 3, 9);

		Evacuator u = new Evacuator("evacuator1", getAddressFromWorld(s, 3, 4),
				5, null, 2);
		u.setWorldListener(s);

		ResidentialBuilding b = new ResidentialBuilding(ad);
		b.setFoundationDamage(10);
		ArrayList<Citizen> citizensToBeTested = new ArrayList<Citizen>();
		for (int i = 1; i <= 5; i++) {
			Citizen c = new Citizen(ad, i + "", "citizen" + i, 15 + i, null);
			c.setWorldListener(s);
			citizensToBeTested.add(c);
			b.getOccupants().add(c);
		}
		Disaster d = new Collapse(3, b);
		dshelper(d);

		unitRespond(u, b, 5);
		for (int i = 0; i < 1; i++) {
			u.cycleStep();
			b.setStructuralIntegrity(b.getStructuralIntegrity() - 10);
		}
		assertTrue("Unit distanceToTarget should be updated after each cycle.",
				checkValueLogic(u, "distanceToTarget", 0));

		assertEquals(
				"Unit location should be updated upon arrival to target location.",
				ad, u.getLocation());
		assertEquals(
				"Unit state should remain responding until 1 cycle after arrival.",
				UnitState.RESPONDING, u.getState());

		assertEquals(
				"Evacuator should start evacuating citizen the next cycle after arrival.",
				0, u.getPassengers().size());
		assertEquals(
				"Number of citizens remaining in the building should not be changed till evacuation starts.",
				5, b.getOccupants().size());

		assertEquals("first Citizen should be in the location " + ad.getX()
				+ " " + ad.getY() + " but was "
				+ citizensToBeTested.get(0).getLocation().getX() + " "
				+ citizensToBeTested.get(0).getLocation().getY(), ad,
				citizensToBeTested.get(0).getLocation());

		assertEquals("second Citizen should be in the location " + ad.getX()
				+ " " + ad.getY() + " but was "
				+ citizensToBeTested.get(1).getLocation().getX() + " "
				+ citizensToBeTested.get(1).getLocation().getY(), ad,
				citizensToBeTested.get(1).getLocation());

		assertEquals("third Citizen should be in the location " + ad.getX()
				+ " " + ad.getY() + " but was "
				+ citizensToBeTested.get(2).getLocation().getX() + " "
				+ citizensToBeTested.get(2).getLocation().getY(), ad,
				citizensToBeTested.get(2).getLocation());

		assertEquals("fourth Citizen should be in the location " + ad.getX()
				+ " " + ad.getY() + " but was "
				+ citizensToBeTested.get(3).getLocation().getX() + " "
				+ citizensToBeTested.get(3).getLocation().getY(), ad,
				citizensToBeTested.get(3).getLocation());

		assertEquals("fifth Citizen should be in the location " + ad.getX()
				+ " " + ad.getY() + " but was "
				+ citizensToBeTested.get(4).getLocation().getX() + " "
				+ citizensToBeTested.get(4).getLocation().getY(), ad,
				citizensToBeTested.get(4).getLocation());

	}
	@Test(timeout = 3000)
	public void testEvacuatorTreatLogicCycle1() throws Exception {
		Simulator s = new Simulator(sos);
		Address ad = getAddressFromWorld(s, 3, 9);

		Evacuator u = new Evacuator("evacuator1", getAddressFromWorld(s, 3, 4),
				5, null, 2);
		u.setWorldListener(s);

		ResidentialBuilding b = new ResidentialBuilding(ad);
		b.setFoundationDamage(10);
		ArrayList<Citizen> citizensToBeTested = new ArrayList<Citizen>();
		for (int i = 1; i <= 5; i++) {
			Citizen c = new Citizen(ad, i + "", "citizen" + i, 15 + i, null);
			c.setWorldListener(s);
			citizensToBeTested.add(c);
			b.getOccupants().add(c);
		}
		Disaster d = new Collapse(3, b);
		dshelper(d);

		unitRespond(u, b, 5);
		for (int i = 0; i < 2; i++) {
			u.cycleStep();
			b.setStructuralIntegrity(b.getStructuralIntegrity() - 10);
		}

		assertEquals(
				"Evacuator distanceToBase should be updated before returning to Base.",
				12, u.getDistanceToBase());
		assertEquals("Unit location should not be updated while en route.", ad,
				u.getLocation());
		assertEquals(
				"Unit state should be updated to Treating when the unit is treating.",
				UnitState.TREATING, u.getState());
		assertEquals(
				"Evacuator should start evacuating citizen the next cycle after arrival.",
				2, u.getPassengers().size());
		assertEquals(
				"Number of citizens remaining in the building should not be changed till evacuation starts.",
				3, b.getOccupants().size());

		assertEquals("first Citizen should be in the location " + ad.getX()
				+ " " + ad.getY() + " but was "
				+ citizensToBeTested.get(0).getLocation().getX() + " "
				+ citizensToBeTested.get(0).getLocation().getY(), ad,
				citizensToBeTested.get(0).getLocation());

		assertEquals("second Citizen should be in the location " + ad.getX()
				+ " " + ad.getY() + " but was "
				+ citizensToBeTested.get(1).getLocation().getX() + " "
				+ citizensToBeTested.get(1).getLocation().getY(), ad,
				citizensToBeTested.get(1).getLocation());

		assertEquals("third Citizen should be in the location " + ad.getX()
				+ " " + ad.getY() + " but was "
				+ citizensToBeTested.get(2).getLocation().getX() + " "
				+ citizensToBeTested.get(2).getLocation().getY(), ad,
				citizensToBeTested.get(2).getLocation());

		assertEquals("fourth Citizen should be in the location " + ad.getX()
				+ " " + ad.getY() + " but was "
				+ citizensToBeTested.get(3).getLocation().getX() + " "
				+ citizensToBeTested.get(3).getLocation().getY(), ad,
				citizensToBeTested.get(3).getLocation());

		assertEquals("fifth Citizen should be in the location " + ad.getX()
				+ " " + ad.getY() + " but was "
				+ citizensToBeTested.get(4).getLocation().getX() + " "
				+ citizensToBeTested.get(4).getLocation().getY(), ad,
				citizensToBeTested.get(4).getLocation());

	}

	@Test(timeout = 3000)
	public void testEvacuatorTreatLogicCycle2() throws Exception {
		Simulator s = new Simulator(sos);
		Address ad = getAddressFromWorld(s, 3, 9);

		Evacuator u = new Evacuator("evacuator1", getAddressFromWorld(s, 3, 4),
				5, null, 2);
		u.setWorldListener(s);

		ResidentialBuilding b = new ResidentialBuilding(ad);
		b.setFoundationDamage(10);
		ArrayList<Citizen> citizensToBeTested = new ArrayList<Citizen>();
		for (int i = 1; i <= 5; i++) {
			Citizen c = new Citizen(ad, i + "", "citizen" + i, 15 + i, null);
			c.setWorldListener(s);
			citizensToBeTested.add(c);
			b.getOccupants().add(c);
		}
		Disaster d = new Collapse(3, b);
		dshelper(d);

		unitRespond(u, b, 5);
		for (int i = 0; i < 3; i++) {
			u.cycleStep();
			b.setStructuralIntegrity(b.getStructuralIntegrity() - 10);
		}

		assertEquals(
				"Evacuator distanceToBase should be updated while returning to Base.",
				7, u.getDistanceToBase());

		assertEquals("Unit location should not be updated while en route.", ad,
				u.getLocation());
		assertEquals(
				"Unit state should not be updated as long as the unit is treating.",
				UnitState.TREATING, u.getState());
		assertEquals(
				"Evacuator should not drop the citizens until it reaches the base.",
				2, u.getPassengers().size());

		assertEquals(
				"Number of citizens remaining in the building should not be changed while the evacuator is en route.",
				3, b.getOccupants().size());

		assertEquals("first Citizen should be in the location " + ad.getX()
				+ " " + ad.getY() + " but was "
				+ citizensToBeTested.get(0).getLocation().getX() + " "
				+ citizensToBeTested.get(0).getLocation().getY(), ad,
				citizensToBeTested.get(0).getLocation());

		assertEquals("second Citizen should be in the location " + ad.getX()
				+ " " + ad.getY() + " but was "
				+ citizensToBeTested.get(1).getLocation().getX() + " "
				+ citizensToBeTested.get(1).getLocation().getY(), ad,
				citizensToBeTested.get(1).getLocation());

		assertEquals("third Citizen should be in the location " + ad.getX()
				+ " " + ad.getY() + " but was "
				+ citizensToBeTested.get(2).getLocation().getX() + " "
				+ citizensToBeTested.get(2).getLocation().getY(), ad,
				citizensToBeTested.get(2).getLocation());

		assertEquals("fourth Citizen should be in the location " + ad.getX()
				+ " " + ad.getY() + " but was "
				+ citizensToBeTested.get(3).getLocation().getX() + " "
				+ citizensToBeTested.get(3).getLocation().getY(), ad,
				citizensToBeTested.get(3).getLocation());

		assertEquals("fifth Citizen should be in the location " + ad.getX()
				+ " " + ad.getY() + " but was "
				+ citizensToBeTested.get(4).getLocation().getX() + " "
				+ citizensToBeTested.get(4).getLocation().getY(), ad,
				citizensToBeTested.get(4).getLocation());

	}
	

	@Test(timeout = 3000)
	public void testEvacuatorTreatLogicCycle4() throws Exception {
		Simulator s = new Simulator(sos);
		Address ad = getAddressFromWorld(s, 3, 9);
		Address safe = getAddressFromWorld(s, 0, 0);
		Evacuator u = new Evacuator("evacuator1", getAddressFromWorld(s, 3, 4),
				5, null, 2);
		u.setWorldListener(s);

		ResidentialBuilding b = new ResidentialBuilding(ad);
		b.setFoundationDamage(10);
		ArrayList<Citizen> citizensToBeTested = new ArrayList<Citizen>();
		for (int i = 1; i <= 5; i++) {
			Citizen c = new Citizen(ad, i + "", "citizen" + i, 15 + i, null);
			c.setWorldListener(s);
			citizensToBeTested.add(c);
			b.getOccupants().add(c);
		}
		Disaster d = new Collapse(3, b);
		dshelper(d);

		unitRespond(u, b, 5);
		for (int i = 0; i < 5; i++) {
			u.cycleStep();
			b.setStructuralIntegrity(b.getStructuralIntegrity() - 10);
		}
		if (!checkValueLogic(u, "distanceToTarget", 12))
			u.cycleStep();
		assertEquals(
				"Evacuator distanceToBase should be updated while returning to Base.",
				0, u.getDistanceToBase());
		assertTrue("Unit distanceToTarget should be updated after each cycle.",
				checkValueLogic(u, "distanceToTarget", 12));

		assertEquals("Unit location should not be updated while en route.",
				safe, u.getLocation());
		assertEquals(
				"Unit state should be not be updated as long as the unit is treating.",
				UnitState.TREATING, u.getState());
		assertEquals("Evacuator should drop citizens upon reaching the base.",
				0, u.getPassengers().size());

		assertEquals(
				"Number of citizens remaining in the building should not be changed till evacuation restarts.",
				3, b.getOccupants().size());

		assertEquals("first Citizen should be in the location " + safe.getX()
				+ " " + safe.getY() + " but was "
				+ citizensToBeTested.get(0).getLocation().getX() + " "
				+ citizensToBeTested.get(0).getLocation().getY(), safe,
				citizensToBeTested.get(0).getLocation());
		assertEquals("first Citizen state should be rescued but was "
				+ citizensToBeTested.get(0).getState(), CitizenState.RESCUED,
				citizensToBeTested.get(0).getState());

		assertEquals("second Citizen should be in the location " + safe.getX()
				+ " " + safe.getY() + " but was "
				+ citizensToBeTested.get(1).getLocation().getX() + " "
				+ citizensToBeTested.get(1).getLocation().getY(), safe,
				citizensToBeTested.get(1).getLocation());

		assertEquals("second Citizen state should be rescued but was "
				+ citizensToBeTested.get(1).getState(), CitizenState.RESCUED,
				citizensToBeTested.get(1).getState());

		assertEquals("third Citizen should be in the location " + ad.getX()
				+ " " + ad.getY() + " but was "
				+ citizensToBeTested.get(2).getLocation().getX() + " "
				+ citizensToBeTested.get(2).getLocation().getY(), ad,
				citizensToBeTested.get(2).getLocation());
		if (citizensToBeTested.get(2).getState() == CitizenState.RESCUED)
			fail("third Citizen state should not be changed to rescued until safely evacuated to base.");

		assertEquals("fourth Citizen should be in the location " + ad.getX()
				+ " " + ad.getY() + " but was "
				+ citizensToBeTested.get(3).getLocation().getX() + " "
				+ citizensToBeTested.get(3).getLocation().getY(), ad,
				citizensToBeTested.get(3).getLocation());

		if (citizensToBeTested.get(3).getState() == CitizenState.RESCUED)
			fail("fourth Citizen state should not be changed to rescued until safely evacuated to base.");

		assertEquals("fifth Citizen should be in the location " + ad.getX()
				+ " " + ad.getY() + " but was "
				+ citizensToBeTested.get(4).getLocation().getX() + " "
				+ citizensToBeTested.get(4).getLocation().getY(), ad,
				citizensToBeTested.get(4).getLocation());

	}
	@Test(timeout = 3000)
	public void testEvacuatorTreatLogicCycle8() throws Exception {
		Simulator s = new Simulator(sos);
		Address ad = getAddressFromWorld(s, 3, 9);
		Address safe = getAddressFromWorld(s, 0, 0);
		Evacuator u = new Evacuator("evacuator1", getAddressFromWorld(s, 3, 4),
				5, null, 2);
		u.setWorldListener(s);

		ResidentialBuilding b = new ResidentialBuilding(ad);
		b.setFoundationDamage(10);
		ArrayList<Citizen> citizensToBeTested = new ArrayList<Citizen>();
		for (int i = 1; i <= 5; i++) {
			Citizen c = new Citizen(ad, i + "", "citizen" + i, 15 + i, null);
			c.setWorldListener(s);
			citizensToBeTested.add(c);
			b.getOccupants().add(c);
		}
		Disaster d = new Collapse(3, b);
		dshelper(d);

		unitRespond(u, b, 5);
		for (int i = 0; i < 9; i++) {
			u.cycleStep();
			b.setStructuralIntegrity(b.getStructuralIntegrity() - 10);
		}
		if (u.getDistanceToBase() != 12)
			u.cycleStep();
		assertEquals(
				"Evacuator distanceToBase should be updated while returning to Base.",
				12, u.getDistanceToBase());
		assertTrue("Unit distanceToTarget should be updated after each cycle.",
				checkValueLogic(u, "distanceToTarget", 0));

		assertEquals("Unit location should not be updated while en route.", ad,
				u.getLocation());
		assertEquals(
				"Unit state should be not be updated as long as the unit is treating.",
				UnitState.TREATING, u.getState());
		assertEquals("Evacuator should drop citizens upon reaching the base.",
				2, u.getPassengers().size());

		assertEquals(
				"Number of citizens remaining in the building should not be changed till evacuation restarts.",
				1, b.getOccupants().size());

		assertEquals("first Citizen should be in the location " + safe.getX()
				+ " " + safe.getY() + " but was "
				+ citizensToBeTested.get(0).getLocation().getX() + " "
				+ citizensToBeTested.get(0).getLocation().getY(), safe,
				citizensToBeTested.get(0).getLocation());
		assertEquals("first Citizen state should be rescued but was "
				+ citizensToBeTested.get(0).getState(), CitizenState.RESCUED,
				citizensToBeTested.get(0).getState());

		assertEquals("second Citizen should be in the location " + safe.getX()
				+ " " + safe.getY() + " but was "
				+ citizensToBeTested.get(1).getLocation().getX() + " "
				+ citizensToBeTested.get(1).getLocation().getY(), safe,
				citizensToBeTested.get(1).getLocation());

		assertEquals("second Citizen state should be rescued but was "
				+ citizensToBeTested.get(1).getState(), CitizenState.RESCUED,
				citizensToBeTested.get(1).getState());

		assertEquals("third Citizen should be in the location " + ad.getX()
				+ " " + ad.getY() + " but was "
				+ citizensToBeTested.get(2).getLocation().getX() + " "
				+ citizensToBeTested.get(2).getLocation().getY(), ad,
				citizensToBeTested.get(2).getLocation());
		if (citizensToBeTested.get(2).getState() == CitizenState.RESCUED)
			fail("third Citizen state should not be changed to rescued until safely evacuated to base.");

		assertEquals("fourth Citizen should be in the location " + ad.getX()
				+ " " + ad.getY() + " but was "
				+ citizensToBeTested.get(3).getLocation().getX() + " "
				+ citizensToBeTested.get(3).getLocation().getY(), ad,
				citizensToBeTested.get(3).getLocation());

		if (citizensToBeTested.get(3).getState() == CitizenState.RESCUED)
			fail("fourth Citizen state should not be changed to rescued until safely evacuated to base.");

		assertEquals("fifth Citizen should be in the location " + ad.getX()
				+ " " + ad.getY() + " but was "
				+ citizensToBeTested.get(4).getLocation().getX() + " "
				+ citizensToBeTested.get(4).getLocation().getY(), ad,
				citizensToBeTested.get(4).getLocation());

	}

	@Test(timeout = 5000)
	public void testStrikeInfection() throws Exception {
		testExistsInClass(Infection.class, "strike", true, void.class);

		int int0 = 0;
		int int9 = 9;
		Address address1 = new Address(int0, int9);
		String String7 = "izw";
		Citizen citizen0 = new Citizen(address1, String7, String7, int9, null) {
			@Override
			public void struckBy(Disaster d) {
				struckByCalled = true;
			}
		};
		Infection infection2 = new Infection(0, citizen0);

		infection2.strike();
		assertTrue(
				"Method \"strike\" in class \"Infection\" should set the initial \"toxicity\" to 25",
				citizen0.getToxicity() == 25);

	}
	@Test(timeout = 5000)
	public void testStrikeGasLeak() throws Exception {
		testExistsInClass(GasLeak.class, "strike", true, void.class);

		int int8 = 8;
		int int5 = 5;
		Address address0 = new Address(int8, int5);
		ResidentialBuilding residentialBuilding2 = new ResidentialBuilding(
				address0) {
			@Override
			public void struckBy(Disaster d) {
				struckByCalled = true;
			}
		};
		GasLeak gasLeak2 = new GasLeak(0, residentialBuilding2);

		gasLeak2.strike();
		assertTrue(
				"Method \"strike\" in class \"GasLeak\" should set the initial \"gasLevel\" to 10",
				residentialBuilding2.getGasLevel() == 10);

	}
	@Test(timeout = 5000)
	public void testStrikeCollapse() throws Exception {
		testExistsInClass(Collapse.class, "strike", true, void.class);

		int int8 = 8;
		int int5 = 5;
		Address address0 = new Address(int8, int5);
		ResidentialBuilding residentialBuilding2 = new ResidentialBuilding(
				address0) {
			@Override
			public void struckBy(Disaster d) {
				struckByCalled = true;
			}
		};
		Collapse collapse2 = new Collapse(0, residentialBuilding2);

		collapse2.strike();
		assertTrue(
				"Method \"strike\" in class \"Collapse\" should set the initial \"foundationDamage\" to 10",
				residentialBuilding2.getFoundationDamage() == 10);

	}

	@Test(timeout = 5000)
	public void testCycleStepInjury() throws Exception {
		testExistsInClass(Injury.class, "cycleStep", true, void.class);
		int int0 = 0;
		int int9 = 9;
		Address address1 = new Address(int0, int9);
		String String7 = "izw";
		Citizen citizen0 = new Citizen(address1, String7, String7, int9, null);
		Injury injury2 = new Injury(0, citizen0);
		injury2.setActive(true);

		for (int i = 1; i <= 5; i++) {
			injury2.cycleStep();
			assertTrue(
					"Method \"cycleStep\" in class \"Injury\" should set the \"bloodLoss\" to "
							+ i * 10 + " by cycle " + i + " but was "
							+ citizen0.getBloodLoss(),
					citizen0.getBloodLoss() == i * 10);
		}
	}
	@Test(timeout = 5000)
	public void testCycleStepFire() throws Exception {
		testExistsInClass(Fire.class, "cycleStep", true, void.class);

		int int8 = 8;
		int int5 = 5;
		Address address0 = new Address(int8, int5);
		ResidentialBuilding residentialBuilding2 = new ResidentialBuilding(
				address0);
		Fire fire2 = new Fire(0, residentialBuilding2);
		fire2.setActive(true);

		for (int i = 1; i <= 5; i++) {
			fire2.cycleStep();
			assertTrue(
					"Method \"cycleStep\" in class \"Fire\" should set the \"fireDamage\" to "
							+ i * 10 + " by cycle " + i + " but was "
							+ residentialBuilding2.getFireDamage(),
					residentialBuilding2.getFireDamage() == i * 10);
		}
	}
	

	@Test(timeout = 1000)
	public void testCompoundDisaster3() throws Exception {
		init();
		Simulator s = new Simulator(sos);
		applyDisaster(testGasLeak, testBuilding1);
		final Field disasterField = Simulator.class
				.getDeclaredField("plannedDisasters");
		disasterField.setAccessible(true);

		((ArrayList<Object>) disasterField.get(s)).clear();
		((ArrayList<Object>) disasterField.get(s)).add(testFire);
		final Field currentcyclefield = Simulator.class
				.getDeclaredField("currentCycle");
		currentcyclefield.setAccessible(true);
		currentcyclefield.set(s, 2);
		final Field buildingField = Simulator.class
				.getDeclaredField("buildings");
		buildingField.setAccessible(true);
		((ArrayList<Object>) buildingField.get(s)).clear();
		testBuilding1.setGasLevel(70);
		((ArrayList<Object>) buildingField.get(s)).add(testBuilding1);
		final Field e_disasterField = Simulator.class
				.getDeclaredField("executedDisasters");
		e_disasterField.setAccessible(true);
		((ArrayList<Object>) e_disasterField.get(s)).add(testGasLeak);
		clearArrayList(s);

		s.nextCycle();
		assertEquals(
				"The Disaster should be removed from planned disasters arraylist after striking ",
				0, ((ArrayList<Object>) disasterField.get(s)).size());
		ResidentialBuilding b = (ResidentialBuilding) ((ArrayList<Object>) buildingField
				.get(s)).get(0);
		String temp = "when a building is suffering from a gas leak disaster and should now be struck by a fire,and the building gaslevel is greater than or equal 70 then:";
		assertEquals(
				temp
						+ "The Structural Integrity value of the building should be 0 ",
				0, b.getStructuralIntegrity());

	}
	


	
	@Test(timeout = 1000)
	public void testGameOverCase4() throws Exception {
		init();
		Simulator s = new Simulator(sos);
		Unit u = new Unit(id1, testAddress1, 2, null) {

			public void treat() {

			}

			public void cycleStep() {

			}

		};

		testFire.setActive(true);
		final Field disasterField = Simulator.class
				.getDeclaredField("plannedDisasters");
		disasterField.setAccessible(true);
		final Field executeDisasterField = Simulator.class
				.getDeclaredField("executedDisasters");
		executeDisasterField.setAccessible(true);
		((ArrayList<Object>) disasterField.get(s)).clear();

		((ArrayList<Object>) executeDisasterField.get(s)).clear();
		((ArrayList<Object>) executeDisasterField.get(s)).add(testFire);
		final Field unitsField = Simulator.class
				.getDeclaredField("emergencyUnits");
		unitsField.setAccessible(true);
		((ArrayList<Object>) unitsField.get(s)).clear();
		u.setState(UnitState.IDLE);
		((ArrayList<Object>) unitsField.get(s)).clear();
		((ArrayList<Object>) unitsField.get(s)).add(u);
		assertTrue(
				"check gameOver in class simulator should return True when there are units that aren't IDLE",
				s.checkGameOver());
	}

	@Test(timeout = 1000)
	public void testSimulatorInitializeCitizenListener() throws Exception {
		Simulator s = new Simulator(sos);
		final Field citizenField = Simulator.class.getDeclaredField("citizens");
		citizenField.setAccessible(true);
		ArrayList<Citizen> testCitizen = (ArrayList<Citizen>) citizenField
				.get(s);
		final Field listenerField = Citizen.class
				.getDeclaredField("emergencyService");
		listenerField.setAccessible(true);

		for (int i = 0; i < testCitizen.size(); i++) {
			boolean cond = testCitizen.get(i).getWorldListener() != null;
			assertTrue(
					"Wotldlistener value in class Citizen should be instantiated after calling simulator",
					cond);

			cond = listenerField.get(testCitizen.get(i)) != null;
			assertTrue(
					"SOS Listener value in class Citizen should be instantiated after calling simulator",
					cond);
		}
	}

	@Test(timeout = 1000)
	public void testCitizenInstanceVariableListener() throws Exception {

		testInstanceVariableIsPresent(Citizen.class, "worldListener", true);
		testInstanceVariableIsPrivate(Citizen.class, "worldListener");

	}

	@Test(timeout = 1000)
	public void testCitizenInstanceVariableEmergencyListener() throws Exception {

		testGetterMethodExistsInClass(Citizen.class, "getWorldListener",
				WorldListener.class, true);
		testSetterMethodExistsInClass(Citizen.class, "setWorldListener",
				WorldListener.class, true);
		testSetterLogic(someRandomCitizen(), "worldListener", new Simulator(
				new CommandCenter()), WorldListener.class);
	}

	@Test(timeout = 1000)
	public void testCitizenStruckByUpdatesState() throws Exception {

		CommandCenter cc = new CommandCenter();

		Citizen ct = someRandomCitizen();

		ArrayList<Disaster> disasters = new ArrayList<Disaster>();
		int start = (int) (Math.random() * 50);
		disasters.add(new Injury(start, ct));
		start = (int) (Math.random() * 50);
		disasters.add(new Infection(start, ct));

		try {

			Field emergencyService = Citizen.class
					.getDeclaredField("emergencyService");
			emergencyService.setAccessible(true);
			emergencyService.set(ct, cc);

			Method struckBy = Citizen.class.getDeclaredMethod("struckBy",
					Disaster.class);

			for (Disaster dis : disasters) {

				struckBy.invoke(ct, dis);

				assertEquals(
						"If a disaster strikes any Citizen, the state of the Citizen should be updated accordingly",
						CitizenState.IN_TROUBLE, ct.getState());

			}
		} catch (NoSuchFieldException e) {
			fail("Class Citizen should have emergencyService instance variable");
		}
	}
	@Test(timeout = 1000)
	public void testCitizenToxicitySetterBounds() throws Exception {

		Citizen ct = someRandomCitizen();
		int r = (int) (Math.random() * 100) + 100;
		ct.setToxicity(r);

		Field toxicity = null;
		try {

			toxicity = Citizen.class.getDeclaredField("toxicity");
			toxicity.setAccessible(true);
			int toxicityValue = (int) toxicity.get(ct);

			assertEquals(
					"The toxicity value of any Citizen can not be set to more than 100.",
					100, toxicityValue);

			ct = someRandomCitizen();
			r = (int) (Math.random() * 100) * -1;
			ct.setToxicity(r);

			toxicityValue = (int) toxicity.get(ct);

			assertEquals(
					"The toxicity value of any Citizen can not be set to less than 0.",
					0, toxicityValue);

		} catch (NoSuchFieldException e) {
			fail("Class Citizen should have toxicity instance variable");

		}

	}

	@Test(timeout = 1000)
	public void testCitizenToxicitySetterKillsWhenOneHundred() {

		Citizen ct = someRandomCitizen();
		int r = 100;
		ct.setToxicity(r);

		assertEquals(
				"If the toxicity value of any Citizen is set to 100 or more, that Citizen should die and their HP updated accordingly.",
				0, ct.getHp());

		assertEquals(
				"If the toxicity value of any Citizen is set to 100 or more, that Citizen should die and their state updated accordingly.",
				CitizenState.DECEASED, ct.getState());

		ct = someRandomCitizen();
		r = (int) (Math.random() * 100) + 100;
		ct.setToxicity(r);

		assertEquals(
				"If the toxicity value of any Citizen is set to 100 or more, that Citizen should die and their HP updated accordingly.",
				0, ct.getHp());

		assertEquals(
				"If the toxicity value of any Citizen is set to 100 or more, that Citizen should die and their state updated accordingly.",
				CitizenState.DECEASED, ct.getState());

	}

	@Test(timeout = 1000)
	public void testCitizenToxicitySetterDoesNotDamageHP() {

		Citizen ct = someRandomCitizen();
		int r = (int) (Math.random() * 50) + 25;
		ct.setHp(r);

		ct.setToxicity((int) (Math.random() * 50) + 25);

		assertEquals(
				"When setting the value of the toxicity of any Citizen to anything below 100, the Citizen's hp should not change.",
				r, ct.getHp());

	}
	
	

	@Test(timeout = 1000)
	public void testCitizenCycleStepWithBloodLossUnderThirty() {

		for (int i = 0; i < 50; i++) {

			Citizen ct = someRandomCitizen();
			int rHP = (int) (Math.random() * 50) + 25;
			ct.setHp(rHP);

			int rBL = (int) (Math.random() * 29) + 1;
			ct.setBloodLoss(rBL);

			ct.cycleStep();

			assertEquals(
					"Calling cycleStep on a Citizen with bloodLoss greater than 0 and less than 30 should decrease their HP by 5",
					5, rHP - ct.getHp());

		}
	}

	@Test(timeout = 1000)
	public void testCitizenCycleStepWithBloodLossBetweenThirtyAndSeventy() {

		for (int i = 0; i < 50; i++) {

			Citizen ct = someRandomCitizen();
			int rHP = (int) (Math.random() * 50) + 25;
			ct.setHp(rHP);

			int rBL = (int) (Math.random() * 39) + 30;
			ct.setBloodLoss(rBL);

			ct.cycleStep();

			assertEquals(
					"Calling cycleStep on a Citizen with bloodLoss greater than or equal 30 and less than 70 should decrease their HP by 10",
					10, rHP - ct.getHp());

		}
	}

	@Test(timeout = 1000)
	public void testCitizenCycleStepWithBloodLossBetweenSeventyAndHundred() {

		for (int i = 0; i < 50; i++) {

			Citizen ct = someRandomCitizen();
			int rHP = (int) (Math.random() * 50) + 25;
			ct.setHp(rHP);

			int rBL = (int) (Math.random() * 29) + 70;
			ct.setBloodLoss(rBL);

			ct.cycleStep();

			assertEquals(
					"Calling cycleStep on a Citizen with bloodLoss greater than or equal 70 and less than 100 should decrease their HP by 15",
					15, rHP - ct.getHp());

		}
	}


	@Test(timeout = 1000)
	public void testResidentialBuildingGasLevelSetterUpperBound()
			throws IllegalArgumentException, IllegalAccessException {
		Address d = someRandomAddress();
		ResidentialBuilding b = new ResidentialBuilding(d);
		int r = (int) (Math.random() * 100) + 100;
		b.setGasLevel(r);
		Field gasLevel = null;
		try {
			gasLevel = ResidentialBuilding.class.getDeclaredField("gasLevel");
			gasLevel.setAccessible(true);

			int gasLevelValue = (int) gasLevel.get(b);
			assertEquals(
					"If the gas level value in ResidentialBuilding class is set to be over 100, it should be set to 100",
					100, gasLevelValue);
		} catch (NoSuchFieldException e) {
			fail("Class ResidentialBuilding should have gasLevel instance variable");

		}
	}

	@Test(timeout = 1000)
	public void testResidentialBuildingGasLevelSetterLowerBound()
			throws IllegalArgumentException, IllegalAccessException {
		ResidentialBuilding b = new ResidentialBuilding(new Address(5, 7));
		int r = (int) (Math.random() * 100 - 100);
		b.setGasLevel(r);
		Field gasLevel = null;
		try {
			gasLevel = ResidentialBuilding.class.getDeclaredField("gasLevel");
			gasLevel.setAccessible(true);

			int gasLevelValue = (int) gasLevel.get(b);
			assertEquals(
					"If the gas level value in ResidentialBuilding class is set to be below 0, it should be set to 0",
					0, gasLevelValue);
		} catch (NoSuchFieldException e) {
			fail("Class ResidentialBuilding should have gasLevel instance variable");

		}
	}

	@Test(timeout = 1000)
	public void testResidentialBuildingGasLevelSetterGreaterOrEqualOneHundredKillsAllOccupants()
			throws IllegalArgumentException, IllegalAccessException {
		Address d = someRandomAddress();
		ResidentialBuilding b = new ResidentialBuilding(d);
		int r = (int) (Math.random() * 100) + 100;
		ArrayList<Citizen> occupantsList = new ArrayList<Citizen>();
		Citizen c1 = new Citizen(d, "123", "C1", 10, null);
		Citizen c2 = new Citizen(d, "456", "C2", 20, null);
		Citizen c3 = new Citizen(d, "789", "C3", 30, null);
		occupantsList.add(c1);
		occupantsList.add(c2);
		occupantsList.add(c3);
		Field occupants = null;
		try {
			occupants = ResidentialBuilding.class.getDeclaredField("occupants");
			occupants.setAccessible(true);
			occupants.set(b, occupantsList);
			b.setGasLevel(r);
			ArrayList<Citizen> occupantsAfter = (ArrayList<Citizen>) occupants
					.get(b);
			for (int i = 0; i < occupantsAfter.size(); i++) {
				Citizen c = occupantsAfter.get(i);
				try {
					Field hp = Citizen.class.getDeclaredField("hp");
					hp.setAccessible(true);
					int hpValue = (int) hp.get(c);
					assertEquals(
							"if the gas level of a building is set to 100 or above, all of its occupants should die",
							0, hpValue);
				} catch (NoSuchFieldException e) {
					fail("Class Citizen should have hp instance variable");
				}
			}
		} catch (NoSuchFieldException e) {
			fail("Class ResidentialBuilding should have occupants instance variable");
		}
	}

	@Test(timeout = 1000)
	public void testResidentialBuildingGasLevelSetterLessThanOneHundredDoesnotAffectccupants()
			throws IllegalArgumentException, IllegalAccessException {
		Address d = someRandomAddress();
		ResidentialBuilding b = new ResidentialBuilding(d);
		int r = (int) (Math.random() * 99) + 1;
		ArrayList<Citizen> occupantsList = new ArrayList<Citizen>();
		Citizen c1 = new Citizen(d, "123", "C1", 10, null);
		Citizen c2 = new Citizen(d, "456", "C2", 20, null);
		Citizen c3 = new Citizen(d, "789", "C3", 30, null);
		occupantsList.add(c1);
		occupantsList.add(c2);
		occupantsList.add(c3);
		Field occupants = null;
		try {
			occupants = ResidentialBuilding.class.getDeclaredField("occupants");
			occupants.setAccessible(true);
			occupants.set(b, occupantsList);
			b.setGasLevel(r);
			ArrayList<Citizen> occupantsAfter = (ArrayList<Citizen>) occupants
					.get(b);
			for (int i = 0; i < occupantsAfter.size(); i++) {
				Citizen c = occupantsAfter.get(i);
				try {
					Field hp = Citizen.class.getDeclaredField("hp");
					hp.setAccessible(true);
					int hpValue = (int) hp.get(c);
					assertEquals(
							"if the gas level of a building is set to be less than 100 , non of its occupants hp should be affected",
							100, hpValue);
				} catch (NoSuchFieldException e) {
					fail("Class Citizen should have hp instance variable");
				}
			}
		} catch (NoSuchFieldException e) {
			fail("Class ResidentialBuilding should have occupants instance variable");
		}
	}

	
	@Test(timeout = 1000)
	public void testResidentialBuildingCycleStep2()
			throws IllegalArgumentException, IllegalAccessException {
		Address d = someRandomAddress();
		ResidentialBuilding b = new ResidentialBuilding(d);
		try {
			Field structuralIntegerity = ResidentialBuilding.class
					.getDeclaredField("structuralIntegrity");
			structuralIntegerity.setAccessible(true);
			int structuralAfter = (int) structuralIntegerity.get(b);
			assertEquals(
					"Calling cycleStep on a building with no foundation or fire damage should not affect its structural integrity.",
					100, structuralAfter);
		} catch (NoSuchFieldException e) {
			fail("Class ResidentialBuilding should have structuralIntegrity instance variable");
			return;
		}
	}
	
	@Test(timeout = 1000)
	public void testResidentialBuildingCycleStep3()
			throws IllegalArgumentException, IllegalAccessException {
		Address d = someRandomAddress();
		ResidentialBuilding b = new ResidentialBuilding(d);
		ArrayList<Integer> values = new ArrayList<Integer>();
		Field foundationDamage = null;
		Field structuralIntegrity = null;
		try {
			foundationDamage = ResidentialBuilding.class
					.getDeclaredField("foundationDamage");
		} catch (NoSuchFieldException e) {
			fail("Class ResidentialBuilding should have foundationDamage instance variable");
			return;
		}
		foundationDamage.setAccessible(true);
		try {
			structuralIntegrity = ResidentialBuilding.class
					.getDeclaredField("structuralIntegrity");
			structuralIntegrity.setAccessible(true);
		} catch (NoSuchFieldException e) {
			fail("Class ResidentialBuilding should have structuralIntegrity instance variable");
			return;
		}
		for (int i = 0; i < 50; i++) {
			structuralIntegrity.set(b, 100);
			int r = (int) ((Math.random() * 80) + 1);
			foundationDamage.set(b, r);
			b.cycleStep();
			int structuralAfter = (int) structuralIntegrity.get(b);
			values.add(100 - structuralAfter);

			assertTrue(
					"Calling cycleStep on a building with foundation damage greater than 0 should decrease its structural integrity",
					structuralAfter < 100);
		}
		assertTrue(
				"Calling cycleStep on a building with foundation damage greater than 0 should decrease its structural integrity with a value between 5 and 10",
				AllInRange(values, 5, 10));
		assertTrue(
				"Decreasing the structural integrity of a building that has a foundation damage should be done with random values",
				isRandom(values));
	}

	@Test(timeout = 1000)
	public void testResidentialBuildingCycleStep4()
			throws IllegalArgumentException, IllegalAccessException {
		Address d = someRandomAddress();
		ResidentialBuilding b = new ResidentialBuilding(d);
		Field fireDamage = null;
		Field structuralIntegrity = null;
		try {
			fireDamage = ResidentialBuilding.class
					.getDeclaredField("fireDamage");
		} catch (NoSuchFieldException e) {
			fail("Class ResidentialBuilding should have fireDamage instance variable");
			return;
		}
		fireDamage.setAccessible(true);
		try {
			structuralIntegrity = ResidentialBuilding.class
					.getDeclaredField("structuralIntegrity");
			structuralIntegrity.setAccessible(true);
		} catch (NoSuchFieldException e) {
			fail("Class ResidentialBuilding should have structuralIntegrity instance variable");
			return;
		}
		for (int i = 0; i < 50; i++) {
			int r1 = (int) ((Math.random() * 80) + 20);
			structuralIntegrity.set(b, r1);
			int r2 = (int) ((Math.random() * 29) + 1);
			fireDamage.set(b, r2);
			b.cycleStep();
			int structuralAfter = (int) structuralIntegrity.get(b);
			assertTrue(
					"Calling cycleStep on a building with fire damage greater than 0 and less than 30 should decrease its structural integrity by 3",
					r1 - structuralAfter == 3);
		}

	}
	
	@Test(timeout = 1000)
	public void testResidentialBuildingCycleStep5()
			throws IllegalArgumentException, IllegalAccessException {
		Address d = someRandomAddress();
		ResidentialBuilding b = new ResidentialBuilding(d);
		Field fireDamage = null;
		Field structuralIntegrity = null;
		try {
			fireDamage = ResidentialBuilding.class
					.getDeclaredField("fireDamage");
		} catch (NoSuchFieldException e) {
			fail("Class ResidentialBuilding should have fireDamage instance variable");
			return;
		}
		fireDamage.setAccessible(true);
		try {
			structuralIntegrity = ResidentialBuilding.class
					.getDeclaredField("structuralIntegrity");
			structuralIntegrity.setAccessible(true);
		} catch (NoSuchFieldException e) {
			fail("Class ResidentialBuilding should have structuralIntegrity instance variable");
			return;
		}
		for (int i = 0; i < 50; i++) {
			int r1 = (int) ((Math.random() * 80) + 20);
			structuralIntegrity.set(b, r1);
			int r2 = (int) ((Math.random() * 40) + 30);
			fireDamage.set(b, r2);
			b.cycleStep();
			int structuralAfter = (int) structuralIntegrity.get(b);
			assertTrue(
					"Calling cycleStep on a building with fire damage greater than or equal 30 and less than 70 should decrease its structural integrity by 5",
					r1 - structuralAfter == 5);
		}

	}
}
