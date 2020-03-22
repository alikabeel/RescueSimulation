package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

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
public class M2PublicTests {

	private boolean hascalledBuilding;
	private boolean hascalledCitizen;
	private boolean hascalledPlannedDisaster;
	private boolean hascalledExecutedDisaster;
	private boolean hascalledUnit;
	private int callcount;
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
	private static boolean struckByCalled = false;

	private boolean hascalledInjuryDisaster;
	private boolean hascalledInfectionDisaster;
	private boolean hascalledCollapseDisaster;
	private boolean hascalledGasDisaster;
	private boolean hascalledFireDisaster;

	Infection testInfection = new Infection(1, testCitizen1);
	Injury testInjury = new Injury(2, testCitizen2);

	private static boolean called = false;
	private static Rescuable target = null;

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

	private void citizenCall(Simulator s) throws Exception {

		hascalledCitizen = false;
		Citizen c = new Citizen(testAddress1, id1, name1, age1, null) {
			public void cycleStep() {
				callcount++;
				hascalledCitizen = true;
				count.put("Citizen", callcount);

			}
		};

		final Field citizensField = Simulator.class
				.getDeclaredField("citizens");
		citizensField.setAccessible(true);
		((ArrayList<Object>) citizensField.get(s)).clear();
		((ArrayList<Object>) citizensField.get(s)).add(c);

	}

	private void buildingsCall(Simulator s) throws Exception {

		hascalledBuilding = false;
		ResidentialBuilding b = new ResidentialBuilding(testAddress1) {
			@Override
			public void cycleStep() {
				callcount++;
				hascalledBuilding = true;
				count.put("Building", callcount);

			}
		};

		final Field buildingField = Simulator.class
				.getDeclaredField("buildings");
		buildingField.setAccessible(true);
		((ArrayList<Object>) buildingField.get(s)).clear();
		b.setEmergencyService(sos);
		((ArrayList<Object>) buildingField.get(s)).add(b);
	}

	private void plannedDisastersCall(Simulator s) throws Exception {

		hascalledPlannedDisaster = false;
		final Field currentcyclefield = Simulator.class
				.getDeclaredField("currentCycle");
		currentcyclefield.setAccessible(true);

		currentcyclefield.set(s, 2);

		applyDisaster(testGasLeak, testBuilding1);

		testBuilding1.setGasLevel(0);

		Rescuable[] target = { testCitizen1, testCitizen2, testBuilding2,
				testBuilding1 };
		int index = new Random().nextInt(2 - 0 + 1);
		Disaster d = new Disaster(3, target[index]) {
			@Override
			public void strike() {
				callcount++;
				hascalledPlannedDisaster = true;
				count.put("PlannedDisaster", callcount);

			}

			@Override
			public void cycleStep() {

			}

		};
		testFire = new Fire(3, testBuilding1) {
			public void strike() {
				callcount++;
				hascalledPlannedDisaster = true;
				count.put("PlannedDisaster", callcount);

			}
		};

		final Field disasterField = Simulator.class
				.getDeclaredField("plannedDisasters");
		disasterField.setAccessible(true);

		disasterField.setAccessible(true);
		((ArrayList<Object>) disasterField.get(s)).clear();
		if (index == 2) {
			((ArrayList<Object>) disasterField.get(s)).add(testFire);
		} else {
			((ArrayList<Object>) disasterField.get(s)).add(d);
		}

	}

	private void executedDisastersCall(Simulator s) throws Exception {

		hascalledExecutedDisaster = false;
		final Field currentcyclefield = Simulator.class
				.getDeclaredField("currentCycle");
		currentcyclefield.setAccessible(true);
		currentcyclefield.set(s, 2);
		int cycle = (int) currentcyclefield.get(s);

		Disaster d1 = new Disaster(cycle - 1, testCitizen1) {

			public void cycleStep() {
				callcount++;
				hascalledExecutedDisaster = true;
				count.put("ExecutedDisaster", callcount);
			}
		};

		final Field disasterField = Simulator.class
				.getDeclaredField("executedDisasters");
		disasterField.setAccessible(true);
		d1.setActive(true);
		((ArrayList<Object>) disasterField.get(s)).clear();
		((ArrayList<Object>) disasterField.get(s)).add(d1);

	}

	private void unitsCall(Simulator s) throws Exception {

		hascalledUnit = false;
		Unit u = new Unit(id1, testAddress1, 2, null) {

			public void treat() {

			}

			public void cycleStep() {
				callcount++;
				hascalledUnit = true;
				count.put("Unit", callcount);
			}
		};

		final Field unitsField = Simulator.class
				.getDeclaredField("emergencyUnits");
		unitsField.setAccessible(true);
		((ArrayList<Object>) unitsField.get(s)).clear();
		((ArrayList<Object>) unitsField.get(s)).add(u);
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

	private Disaster someRandomDisaster(ResidentialBuilding b) {
		int type = (int) (Math.random() * 3);
		int start = (int) (Math.random() * 11);
		switch (type) {
		case 0:
			return new Collapse(start, b);
		case 1:
			return new Fire(start, b);
		}
		return new GasLeak(start, b);
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

	// TEST

	@Test(timeout = 1000)
	public void testConstructorUnit() throws Exception {
		Class[] inputs = { String.class, Address.class, int.class,
				WorldListener.class };
		testConstructorExists(Class.forName(unitPath), inputs);
	}

	@Test(timeout = 1000)
	public void testConstructorPoliceUnit() throws Exception {
		Class[] inputs = { String.class, Address.class, int.class,
				WorldListener.class, int.class };
		testConstructorExists(Class.forName(policeUnitPath), inputs);
	}

	@Test(timeout = 3000)
	public void testUnitTreat() throws Exception {

		testExistsInClass(Class.forName(unitPath), "treat", true, void.class);

	}

	@Test(timeout = 1000)
	public void testConstructorFireUnit() throws Exception {
		Class[] inputs = { String.class, Address.class, int.class,
				WorldListener.class };
		testConstructorExists(Class.forName(fireUnitPath), inputs);
	}

	@Test(timeout = 1000)
	public void testConstructorMedicalUnit() throws Exception {
		Class[] inputs = { String.class, Address.class, int.class,
				WorldListener.class };
		testConstructorExists(Class.forName(medicalUnitPath), inputs);
	}

	@Test(timeout = 1000)
	public void testConstructorAmbulanceUnit() throws Exception {
		Class[] inputs = { String.class, Address.class, int.class,
				WorldListener.class };
		testConstructorExists(Class.forName(ambulancePath), inputs);
	}

	@Test(timeout = 3000)
	public void testEvacuatorTreat() throws Exception {
		testExistsInClass(Class.forName(evacuatorPath), "treat", true,
				void.class);
	}

	@Test(timeout = 3000)
	public void testFireTruckTreat() throws Exception {
		testExistsInClass(Class.forName(fireTruckPath), "treat", true,
				void.class);
	}

	@Test(timeout = 3000)
	public void testGasControlUnitTreat() throws Exception {
		testExistsInClass(Class.forName(gasControlUnitPath), "treat", true,
				void.class);
	}

	@Test(timeout = 3000)
	public void testUnitCycleStep() throws Exception {
		testExistsInClass(Class.forName(unitPath), "cycleStep", true,
				void.class);
	}

	@Test(timeout = 3000)
	public void testUnitJobsDone() throws Exception {
		testExistsInClass(Class.forName(unitPath), "jobsDone", true, void.class);
	}

	@Test(timeout = 3000)
	public void testUnitRespond() throws Exception {
		testExistsInClass(Class.forName(unitPath), "respond", true, void.class,
				Class.forName(rescuablePath));
	}

	@Test(timeout = 1000)
	public void testInstanceVariableUnitListener() throws Exception {
		testInstanceVariableIsPresent(Class.forName(unitPath), "worldListener",
				true);
		testInstanceVariableIsPrivate(Class.forName(unitPath), "worldListener");
		testGetterMethodExistsInClass(Class.forName(unitPath),
				"getWorldListener", Class.forName(worldListenerPath), true);
		testSetterMethodExistsInClass(Class.forName(unitPath),
				"setWorldListener", Class.forName(worldListenerPath), true);

		testInstanceVariableIsPresent(Class.forName(ambulancePath),
				"worldListener", false);
		testGetterMethodExistsInClass(Class.forName(ambulancePath),
				"getWorldListener", Class.forName(worldListenerPath), false);
		testSetterMethodExistsInClass(Class.forName(ambulancePath),
				"setWorldListener", Class.forName(worldListenerPath), false);

		testInstanceVariableIsPresent(Class.forName(diseaseControlUnitPath),
				"worldListener", false);
		testGetterMethodExistsInClass(Class.forName(diseaseControlUnitPath),
				"getWorldListener", Class.forName(worldListenerPath), false);
		testSetterMethodExistsInClass(Class.forName(diseaseControlUnitPath),
				"setWorldListener", Class.forName(worldListenerPath), false);

		testInstanceVariableIsPresent(Class.forName(evacuatorPath),
				"worldListener", false);
		testGetterMethodExistsInClass(Class.forName(evacuatorPath),
				"getWorldListener", Class.forName(worldListenerPath), false);
		testSetterMethodExistsInClass(Class.forName(evacuatorPath),
				"setWorldListener", Class.forName(worldListenerPath), false);

		testInstanceVariableIsPresent(Class.forName(fireTruckPath),
				"worldListener", false);
		testGetterMethodExistsInClass(Class.forName(fireTruckPath),
				"getWorldListener", Class.forName(worldListenerPath), false);
		testSetterMethodExistsInClass(Class.forName(fireTruckPath),
				"setWorldListener", Class.forName(worldListenerPath), false);

		testInstanceVariableIsPresent(Class.forName(gasControlUnitPath),
				"worldListener", false);
		testGetterMethodExistsInClass(Class.forName(gasControlUnitPath),
				"getWorldListener", Class.forName(worldListenerPath), false);
		testSetterMethodExistsInClass(Class.forName(gasControlUnitPath),
				"setWorldListener", Class.forName(worldListenerPath), false);
	}

	@Test(timeout = 1000)
	public void testInstanceVariableUnitDistanceToTarget() throws Exception {
		testInstanceVariableIsPresent(Class.forName(unitPath),
				"distanceToTarget", true);
		testInstanceVariableIsPrivate(Class.forName(unitPath),
				"distanceToTarget");
	}

	@Test(timeout = 1000)
	public void testInstanceVariableUnitDistanceToTargetGetterAndSetter()
			throws Exception {
		testSetterMethodExistsInClass(Class.forName(unitPath),
				"setDistanceToTarget", int.class, true);
	}

	@Test(timeout = 1000)
	public void testInstanceVariableUnitDistanceToTargetSetterLogic()
			throws Exception {
		int d = 2;
		Unit u = new Unit("uni1", new Address(4, 3), 3, null) {
			@Override
			public void treat() {

			}
		};
		u.setDistanceToTarget(d);
		assertTrue(
				"Unit setDistanceToTarget should change the value of the distance correctly.",
				checkValueLogic(u, "distanceToTarget", d));
	}

	@Test(timeout = 1000)
	public void testInstanceVariableUnitWorldListenerSetterLogic()
			throws Exception {
		WorldListener wl = new Simulator(sos);
		Unit u = new Unit("unit1", new Address(3, 4), 3, null) {
			@Override
			public void treat() {

			}
		};
		u.setWorldListener(wl);
		assertTrue(
				"Unit setWorldListener should change the value of the worldListener correctly.",
				checkValueLogic(u, "worldListener", wl));
	}

	@Test(timeout = 1000)
	public void testInstanceVariableUnitWorldListenerGetterLogic()
			throws Exception {
		WorldListener wl = new Simulator(sos);
		Unit u = new Unit("unit1", new Address(3, 4), 3, null) {
			@Override
			public void treat() {

			}
		};
		u.setWorldListener(wl);
		assertTrue(
				"Unit getWorldListener should return the correct worldListener.",
				checkValueLogic(u, "worldListener", u.getWorldListener()));
	}

	@Test(timeout = 1000)
	public void testUnitJobsDoneLogic() throws Exception {
		WorldListener wl = new Simulator(sos);
		Unit u = new Unit("unit1", new Address(3, 4), 3, null) {
			@Override
			public void treat() {

			}
		};
		u.setWorldListener(wl);
		unitRespond(u, new Citizen(u.getLocation(), "123", "test1", 15, wl), 0);
		u.jobsDone();
		assertEquals("Unit jobsDone should nullify its target", null,
				u.getTarget());
		assertEquals("Unit jobsDone should return the unit to IDLE state",
				UnitState.IDLE, u.getState());
	}

	@Test(timeout = 1000)
	public void testUnitRespondFromRespondingLogic() throws Exception {
		Unit u = new Unit("unit1", new Address(3, 4), 3, null) {
			@Override
			public void treat() {

			}
		};
		Citizen c1 = new Citizen(new Address(3, 7), "1", "citizen1", 15, null);
		Citizen c2 = new Citizen(new Address(3, 9), "2", "citizen2", 15, null);
		Disaster d = new Injury(3, c1);
		dshelper(d);
		Disaster d2 = new Injury(3, c2);
		dshelper(d2);
		u.respond(c1);
		u.respond(c2);
		assertEquals("Unit respond should update the target correctly.", c2,
				u.getTarget());
		assertTrue(
				"Unit respond should update the distanceToTarget correctly.",
				checkValueLogic(u, "distanceToTarget", 5));
		assertEquals("Unit respond should update the unitState Correctly.",
				UnitState.RESPONDING, u.getState());
	}

	@Test(timeout = 1000)
	public void testUnitRespondFromTreatingLogic() throws Exception {
		Simulator s = new Simulator(sos);

		Unit u = new Ambulance("ambulance1", new Address(3, 4), 3, null);
		u.setWorldListener(s);

		Citizen c1 = new Citizen(new Address(3, 5), "1", "citizen1", 15, null);
		Disaster d = new Injury(3, c1);
		dshelper(d);
		c1.setHp(60);
		c1.setBloodLoss(20);

		u.respond(c1);

		u.cycleStep();
		u.cycleStep();

		Citizen c2 = new Citizen(new Address(3, 9), "2", "citizen2", 15, null);
		u.respond(c2);

		assertEquals("Unit respond should update the target correctly.", c2,
				u.getTarget());
		assertTrue(
				"Unit respond should update the distanceToTarget correctly.",
				checkValueLogic(u, "distanceToTarget", 4));
		assertEquals("Unit respond should update the unitState Correctly.",
				UnitState.RESPONDING, u.getState());
	}

	@Test(timeout = 1000)
	public void testCitizenDisasterAfterUnitRespondFromTreatingLogic()
			throws Exception {

		Simulator s = new Simulator(sos);
		Unit u = new Ambulance("ambulance1", new Address(3, 4), 3, null);
		u.setWorldListener(s);

		Citizen c1 = new Citizen(new Address(3, 5), "1", "citizen1", 15, null);
		Disaster d = new Injury(3, c1);
		dshelper(d);
		c1.setHp(60);
		c1.setBloodLoss(25);

		u.respond(c1);

		u.treat();
		u.setState(UnitState.TREATING);

		assertTrue(
				"Ambulance should not change the current disaster, it should only activate or deactivate the existing one.",
				checkValueLogic(c1, "disaster", d));
		assertTrue(
				"Ambulance should deactivate the disaster upon starting the treatment process",
				checkValueLogic(d, "active", false));
		Citizen c2 = new Citizen(new Address(3, 9), "2", "citizen2", 15, null);
		c2.setBloodLoss(25);
		u.respond(c2);
		assertTrue(
				"Ambulance should not change the current disaster, it should only activate or deactivate the existing one.",
				checkValueLogic(c1, "disaster", d));
		assertTrue(
				"Ambulance should reactivate the disaster upon starting the treatment process",
				checkValueLogic(d, "active", true));

	}

	@Test(timeout = 1000)
	public void testAmbulanceTreatLogic() throws Exception {
		Simulator s = new Simulator(sos);
		Ambulance u = new Ambulance("ambulance1", new Address(3, 4), 3, null);
		u.setWorldListener(s);
		Citizen c1 = new Citizen(new Address(3, 9), "1", "citizen1", 15, null);
		Disaster d = new Injury(3, c1);
		dshelper(d);
		c1.setBloodLoss(50);
		unitRespond(u, c1, 5);

		ArrayList<Integer> bloodLossValues = new ArrayList<Integer>();
		bloodLossValues.add(40);
		bloodLossValues.add(30);
		bloodLossValues.add(20);
		bloodLossValues.add(10);
		bloodLossValues.add(0);
		for (int i = 0; i < 5; i++) {
			u.treat();
			assertTrue("Values of blood loss were wrong, expected "
					+ bloodLossValues.get(i) + " but was " + c1.getBloodLoss()
					+ ".", bloodLossValues.get(i) == c1.getBloodLoss());
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

	@Test(timeout = 1000)
	public void testMedicalUnitHealLogic() throws Exception {
		Simulator s = new Simulator(sos);
		MyMedicalUnit u = new MyMedicalUnit("medicalUnit1", new Address(3, 4),
				3);
		u.setWorldListener(s);
		Citizen c1 = new Citizen(new Address(3, 9), "1", "citizen1", 15, null);
		Disaster d = new Injury(3, c1);
		dshelper(d);
		c1.setHp(40);
		unitRespond(u, c1, 5);

		ArrayList<Integer> hpValues = new ArrayList<Integer>();
		hpValues.add(50);
		hpValues.add(60);
		hpValues.add(70);
		hpValues.add(80);
		hpValues.add(90);
		hpValues.add(100);

		for (int i = 0; i < 6; i++) {
			u.heal();
			assertTrue(
					"Values of citizen hp were wrong, expected "
							+ hpValues.get(i) + " but was " + c1.getHp() + ".",
					hpValues.get(i) == c1.getHp());
		}
		assertTrue(
				"Ambulance should not change the current disaster, it should only activate or deactivate the existing one.",
				checkValueLogic(c1, "disaster", d));
		assertTrue("Units should become IDLE after completing their treatment",
				checkValueLogic(u, "state", UnitState.IDLE));
	}

	@Test(timeout = 3000)
	public void testUnitCallsTreat() throws Exception {
		Simulator s = new Simulator(sos);
		Unit u = new Unit("unit1", new Address(3, 4), 3, null) {
			@Override
			public void treat() {
				treatCalled = true;
			}
		};
		u.setWorldListener(s);
		Citizen c1 = new Citizen(new Address(3, 4), "1", "citizen1", 15, null);
		Disaster d = new MyDisaster(0, c1);
		dshelper(d);
		c1.setWorldListener(s);
		unitRespond(u, c1, 0);
		u.cycleStep();

		assertTrue(
				"The method cycleStep should call the method treat if the unit arrives to valid target",
				treatCalled);
		treatCalled = false;
	}

	@Test(timeout = 1000)
	public void testUnitCycleStepRespondingLogic() throws Exception {
		Simulator s = new Simulator(sos);
		Address ad = new Address(3, 3);
		Unit u1 = new Unit("unit1", ad, 3, null) {
			@Override
			public void treat() {

			}
		};
		u1.setWorldListener(s);
		Citizen c1 = new Citizen(new Address(3, 9), "1", "citizen1", 15, null);
		u1.respond(c1);
		u1.cycleStep();

		assertTrue("respond should change the unitState to RESPONDING.",
				checkValueLogic(u1, "state", UnitState.RESPONDING));
		assertTrue(
				"respond should not change the unit location until it reaches target destination.",
				checkValueLogic(u1, "location", ad));
		assertTrue("respond should assign the unit target correctly.",
				checkValueLogic(u1, "target", c1));
		assertTrue(
				"respond should change the unit distanceToTarget correctly.",
				checkValueLogic(u1, "distanceToTarget", 3));
	}

	@Test(timeout = 1000)
	public void testUnitCycleStepRespondingAndArrivedLogic() throws Exception {
		Simulator s = new Simulator(sos);

		//Address ad = new Address(3, 9);
		Address ad= getAddressFromWorld(s, 3, 9);
		Unit u1 = new Unit("unit1", ad, 3, null) {
			public void treat() {

				try {
					Method m = Class.forName(unitPath).getDeclaredMethod(
							"treat");
					if (!Modifier.isAbstract(m.getModifiers())) {

						MethodHandle h = MethodHandles.lookup().findSpecial(
								this.getClass().getSuperclass(), "treat",
								MethodType.methodType(void.class),
								this.getClass());
						h.invoke(this);
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		};
		u1.setWorldListener(s);
		Citizen c1 = new Citizen(ad, "1", "citizen1", 15, null);

		Disaster d = new MyDisaster(3, c1);
		dshelper(d);
		u1.respond(c1);
		u1.cycleStep();
		Address resLocation = getAddressFromWorld(s, ad.getX(), ad.getY());

		assertTrue("Treat should change the unitState to TREATING",
				checkValueLogic(u1, "state", UnitState.TREATING));
		assertTrue(
				"Once the unit arrives to the target location its location should be updated.",
				checkValueLogic(u1, "location", resLocation));
		assertTrue("Treat should not change the unit target.",
				checkValueLogic(u1, "target", c1));
		assertTrue("Unit distanceToTarget should be 0 upon arrival.",
				checkValueLogic(u1, "distanceToTarget", 0));

	}

	@Test(timeout = 1000)
	public void testFireTruckTreatLogic() throws Exception {
		Simulator s = new Simulator(sos);
		FireTruck u = new FireTruck("firetruck1", new Address(3, 4), 3, null);
		u.setWorldListener(s);
		ResidentialBuilding c1 = new ResidentialBuilding(new Address(3, 9));
		Disaster d = new Fire(3, c1);
		dshelper(d);
		c1.setFireDamage(50);

		unitRespond(u, c1, 5);

		ArrayList<Integer> fireDamageValues = new ArrayList<Integer>();
		fireDamageValues.add(40);
		fireDamageValues.add(30);
		fireDamageValues.add(20);
		fireDamageValues.add(10);
		fireDamageValues.add(0);

		for (int i = 0; i < 5; i++) {
			u.treat();
			assertTrue(
					"Values of building fire damage were wrong, expected "
							+ fireDamageValues.get(i) + " but was "
							+ c1.getFireDamage() + ".",
					fireDamageValues.get(i) == c1.getFireDamage());
		}
		u.cycleStep();
		assertTrue(
				"FireTruck should not change the disaster, it should activate it or deactivate it only.",
				checkValueLogic(c1, "disaster", d));
		assertTrue("FireTruck treat should deactivate the disaster.",
				checkValueLogic(d, "active", false));
		assertTrue(
				"FireTruck unit state should be changed to IDLE once completing the treatment.",
				checkValueLogic(u, "state", UnitState.IDLE));

	}

	@Test(timeout = 3000)
	public void testEvacuatorTreatLogicCycle0DistanceToTarget()
			throws Exception {
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

	}

	@Test(timeout = 3000)
	public void testEvacuatorTreatLogicCycle0UnitLocation() throws Exception {
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

		assertEquals(
				"Unit location should be updated upon arrival to target location.",
				ad, u.getLocation());

	}

	@Test(timeout = 3000)
	public void testEvacuatorTreatLogicCycle0UnitState() throws Exception {
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
		assertEquals(
				"Unit state should remain responding until 1 cycle after arrival.",
				UnitState.RESPONDING, u.getState());

	}

	@Test(timeout = 3000)
	public void testEvacuatorTreatLogicCycle0PassengersSize() throws Exception {
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

		assertEquals(
				"Evacuator should start evacuating citizen the next cycle after arrival.",
				0, u.getPassengers().size());

	}

	@Test(timeout = 3000)
	public void testEvacuatorTreatLogicCycle0OccupantsSize() throws Exception {
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
		assertEquals(
				"Number of citizens remaining in the building should not be changed till evacuation starts.",
				5, b.getOccupants().size());

	}

	@Test(timeout = 3000)
	public void testEvacuatorTreatLogicCycle0CitizensLocations()
			throws Exception {
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
	public void testEvacuatorTreatLogicCycle1DistanceToBase() throws Exception {
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

	}

	@Test(timeout = 3000)
	public void testEvacuatorTreatLogicCycle1UnitLocation() throws Exception {
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

		assertEquals("Unit location should not be updated while en route.", ad,
				u.getLocation());
		System.out.println(u);

	}

	@Test(timeout = 3000)
	public void testEvacuatorTreatLogicCycle1UnitState() throws Exception {
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
				"Unit state should be updated to Treating when the unit is treating.",
				UnitState.TREATING, u.getState());

	}

	@Test(timeout = 3000)
	public void testEvacuatorTreatLogicCycle1PassengersSize() throws Exception {
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
				"Evacuator should start evacuating citizen the next cycle after arrival.",
				2, u.getPassengers().size());

	}

	@Test(timeout = 3000)
	public void testEvacuatorTreatLogicCycle1OccupantsSize() throws Exception {
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
				"Number of citizens remaining in the building should not be changed till evacuation starts.",
				3, b.getOccupants().size());

	}

	@Test(timeout = 3000)
	public void testEvacuatorTreatLogicCycle1CitizensLocation()
			throws Exception {
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
	public void testEvacuatorTreatLogicCycle2DistanceToBase() throws Exception {
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

	}

	@Test(timeout = 3000)
	public void testEvacuatorTreatLogicCycle2UnitLocation() throws Exception {
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

		assertEquals("Unit location should not be updated while en route.", ad,
				u.getLocation());

	}

	@Test(timeout = 3000)
	public void testEvacuatorTreatLogicCycle2UnitState() throws Exception {
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
				"Unit state should not be updated as long as the unit is treating.",
				UnitState.TREATING, u.getState());

	}

	@Test(timeout = 3000)
	public void testEvacuatorTreatLogicCycle2PassengersSize() throws Exception {
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
				"Evacuator should not drop the citizens until it reaches the base.",
				2, u.getPassengers().size());

	}

	@Test(timeout = 3000)
	public void testEvacuatorTreatLogicCycle2OccupantsSize() throws Exception {
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
				"Number of citizens remaining in the building should not be changed while the evacuator is en route.",
				3, b.getOccupants().size());

	}

	@Test(timeout = 3000)
	public void testEvacuatorTreatLogicCycle2CitizensLocations()
			throws Exception {
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
	public void testEvacuatorTreatLogicCycle4DistanceToBase() throws Exception {
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
		for (int i = 0; i < 5; i++) {
			u.cycleStep();
			b.setStructuralIntegrity(b.getStructuralIntegrity() - 10);
		}
		if (u.getDistanceToBase() != 0)
			u.cycleStep();
		assertEquals(
				"Evacuator distanceToBase should be updated while returning to Base.",
				0, u.getDistanceToBase());

	}

	@Test(timeout = 3000)
	public void testEvacuatorTreatLogicCycle4DistanceToTarget()
			throws Exception {
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
		for (int i = 0; i < 5; i++) {
			u.cycleStep();
			b.setStructuralIntegrity(b.getStructuralIntegrity() - 10);
		}
		if (!checkValueLogic(u, "distanceToTarget", 12))
			u.cycleStep();

		assertTrue("Unit distanceToTarget should be updated after each cycle.",
				checkValueLogic(u, "distanceToTarget", 12));

	}

	@Test(timeout = 3000)
	public void testEvacuatorTreatLogicCycle4UnitLocation() throws Exception {
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
		if (!u.getLocation().equals(safe))
			u.cycleStep();

		assertEquals("Unit location should not be updated while en route.",
				safe, u.getLocation());

	}

	@Test(timeout = 3000)
	public void testEvacuatorTreatLogicCycle4UnitState() throws Exception {
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
		for (int i = 0; i < 5; i++) {
			u.cycleStep();
			b.setStructuralIntegrity(b.getStructuralIntegrity() - 10);
		}
		if (u.getState() != UnitState.TREATING)
			u.cycleStep();

		assertEquals(
				"Unit state should be not be updated as long as the unit is treating.",
				UnitState.TREATING, u.getState());

	}

	@Test(timeout = 3000)
	public void testEvacuatorTreatLogicCycle4PassengersSize() throws Exception {
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
		for (int i = 0; i < 5; i++) {
			u.cycleStep();
			b.setStructuralIntegrity(b.getStructuralIntegrity() - 10);
		}
		if (u.getPassengers().size() != 0)
			u.cycleStep();

		assertEquals("Evacuator should drop citizens upon reaching the base.",
				0, u.getPassengers().size());

	}

	@Test(timeout = 3000)
	public void testEvacuatorTreatLogicCycle4OccupantsSize() throws Exception {
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
		for (int i = 0; i < 5; i++) {
			u.cycleStep();
			b.setStructuralIntegrity(b.getStructuralIntegrity() - 10);
		}
		if (b.getOccupants().size() != 3)
			u.cycleStep();

		assertEquals(
				"Number of citizens remaining in the building should not be changed till evacuation restarts.",
				3, b.getOccupants().size());

	}

	@Test(timeout = 3000)
	public void testEvacuatorTreatLogicCycle4CitizensLocations()
			throws Exception {
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
		boolean checkIfCycleStepNeeded = true;

		checkIfCycleStepNeeded = checkIfCycleStepNeeded
				&& (citizensToBeTested.get(0).getLocation().equals(safe));
		checkIfCycleStepNeeded = checkIfCycleStepNeeded
				&& (citizensToBeTested.get(1).getLocation().equals(safe));
		checkIfCycleStepNeeded = checkIfCycleStepNeeded
				&& (citizensToBeTested.get(2).getLocation().equals(ad));
		checkIfCycleStepNeeded = checkIfCycleStepNeeded
				&& (citizensToBeTested.get(3).getLocation().equals(ad));
		checkIfCycleStepNeeded = checkIfCycleStepNeeded
				&& (citizensToBeTested.get(4).getLocation().equals(ad));

		if (!checkIfCycleStepNeeded)
			u.cycleStep();

		assertEquals("first Citizen should be in the location " + safe.getX()
				+ " " + safe.getY() + " but was "
				+ citizensToBeTested.get(0).getLocation().getX() + " "
				+ citizensToBeTested.get(0).getLocation().getY(), safe,
				citizensToBeTested.get(0).getLocation());

		assertEquals("second Citizen should be in the location " + safe.getX()
				+ " " + safe.getY() + " but was "
				+ citizensToBeTested.get(1).getLocation().getX() + " "
				+ citizensToBeTested.get(1).getLocation().getY(), safe,
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
	public void testEvacuatorTreatLogicCycle4CitizensStates() throws Exception {
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
		for (int i = 0; i < 5; i++) {
			u.cycleStep();
			b.setStructuralIntegrity(b.getStructuralIntegrity() - 10);
		}
		boolean checkIfCycleStepNeeded = true;

		checkIfCycleStepNeeded = checkIfCycleStepNeeded
				&& (citizensToBeTested.get(0).getState()
						.equals(CitizenState.RESCUED));
		checkIfCycleStepNeeded = checkIfCycleStepNeeded
				&& (citizensToBeTested.get(1).getState()
						.equals(CitizenState.RESCUED));
		checkIfCycleStepNeeded = checkIfCycleStepNeeded
				&& !(citizensToBeTested.get(2).getState()
						.equals(CitizenState.RESCUED));
		checkIfCycleStepNeeded = checkIfCycleStepNeeded
				&& !(citizensToBeTested.get(3).getState()
						.equals(CitizenState.RESCUED));

		if (!checkIfCycleStepNeeded)
			u.cycleStep();
		assertEquals("first Citizen state should be rescued but was "
				+ citizensToBeTested.get(0).getState(), CitizenState.RESCUED,
				citizensToBeTested.get(0).getState());

		assertEquals("second Citizen state should be rescued but was "
				+ citizensToBeTested.get(1).getState(), CitizenState.RESCUED,
				citizensToBeTested.get(1).getState());

		if (citizensToBeTested.get(2).getState() == CitizenState.RESCUED)
			fail("third Citizen state should not be changed to rescued until safely evacuated to base.");

		if (citizensToBeTested.get(3).getState() == CitizenState.RESCUED)
			fail("fourth Citizen state should not be changed to rescued until safely evacuated to base.");

	}

	@Test(timeout = 3000)
	public void testEvacuatorTreatLogicCycle8DistanceToTarget()
			throws Exception {
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
		for (int i = 0; i < 9; i++) {
			u.cycleStep();
			b.setStructuralIntegrity(b.getStructuralIntegrity() - 10);
		}
		if (!checkValueLogic(u, "distanceToTarget", 0))
			u.cycleStep();

		assertTrue("Unit distanceToTarget should be updated after each cycle.",
				checkValueLogic(u, "distanceToTarget", 0));

	}

	@Test(timeout = 3000)
	public void testEvacuatorTreatLogicCycle8DistanceToBase() throws Exception {
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
		for (int i = 0; i < 9; i++) {
			u.cycleStep();
			b.setStructuralIntegrity(b.getStructuralIntegrity() - 10);
		}
		if (u.getDistanceToBase() != 12)
			u.cycleStep();
		assertEquals(
				"Evacuator distanceToBase should be updated while returning to Base.",
				12, u.getDistanceToBase());

	}

	@Test(timeout = 3000)
	public void testEvacuatorTreatLogicCycle8UnitLocation() throws Exception {
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
		for (int i = 0; i < 9; i++) {
			u.cycleStep();
			b.setStructuralIntegrity(b.getStructuralIntegrity() - 10);
		}
		if (!u.getLocation().equals(ad))
			u.cycleStep();

		assertEquals("Unit location should not be updated while en route.", ad,
				u.getLocation());

	}

	@Test(timeout = 3000)
	public void testEvacuatorTreatLogicCycle8UnitState() throws Exception {
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
		for (int i = 0; i < 9; i++) {
			u.cycleStep();
			b.setStructuralIntegrity(b.getStructuralIntegrity() - 10);
		}
		if (u.getState() != UnitState.TREATING)
			u.cycleStep();
		assertEquals(
				"Unit state should be not be updated as long as the unit is treating.",
				UnitState.TREATING, u.getState());

	}

	@Test(timeout = 3000)
	public void testEvacuatorTreatLogicCycle8PassengersSize() throws Exception {
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
		for (int i = 0; i < 9; i++) {
			u.cycleStep();
			b.setStructuralIntegrity(b.getStructuralIntegrity() - 10);
		}
		if (u.getPassengers().size() != 2)
			u.cycleStep();
		assertEquals("Evacuator should drop citizens upon reaching the base.",
				2, u.getPassengers().size());

	}

	@Test(timeout = 3000)
	public void testEvacuatorTreatLogicCycle8OccupantsSize() throws Exception {
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
		for (int i = 0; i < 9; i++) {
			u.cycleStep();
			b.setStructuralIntegrity(b.getStructuralIntegrity() - 10);
		}
		if (b.getOccupants().size() != 1)
			u.cycleStep();
		assertEquals(
				"Number of citizens remaining in the building should not be changed till evacuation restarts.",
				1, b.getOccupants().size());

	}

	@Test(timeout = 3000)
	public void testEvacuatorTreatLogicCycle8CitizensLocations()
			throws Exception {
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
		boolean checkIfCycleStepNeeded = true;

		checkIfCycleStepNeeded = checkIfCycleStepNeeded
				&& (citizensToBeTested.get(0).getLocation().equals(safe));
		checkIfCycleStepNeeded = checkIfCycleStepNeeded
				&& (citizensToBeTested.get(1).getLocation().equals(safe));
		checkIfCycleStepNeeded = checkIfCycleStepNeeded
				&& (citizensToBeTested.get(2).getLocation().equals(ad));
		checkIfCycleStepNeeded = checkIfCycleStepNeeded
				&& (citizensToBeTested.get(3).getLocation().equals(ad));
		checkIfCycleStepNeeded = checkIfCycleStepNeeded
				&& (citizensToBeTested.get(4).getLocation().equals(ad));

		if (!checkIfCycleStepNeeded)
			u.cycleStep();

		assertEquals("first Citizen should be in the location " + safe.getX()
				+ " " + safe.getY() + " but was "
				+ citizensToBeTested.get(0).getLocation().getX() + " "
				+ citizensToBeTested.get(0).getLocation().getY(), safe,
				citizensToBeTested.get(0).getLocation());

		assertEquals("second Citizen should be in the location " + safe.getX()
				+ " " + safe.getY() + " but was "
				+ citizensToBeTested.get(1).getLocation().getX() + " "
				+ citizensToBeTested.get(1).getLocation().getY(), safe,
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
	public void testEvacuatorTreatLogicCycle8CitizensStates() throws Exception {
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
		for (int i = 0; i < 9; i++) {
			u.cycleStep();
			b.setStructuralIntegrity(b.getStructuralIntegrity() - 10);
		}
		boolean checkIfCycleStepNeeded = true;

		checkIfCycleStepNeeded = checkIfCycleStepNeeded
				&& (citizensToBeTested.get(0).getState()
						.equals(CitizenState.RESCUED));
		checkIfCycleStepNeeded = checkIfCycleStepNeeded
				&& (citizensToBeTested.get(1).getState()
						.equals(CitizenState.RESCUED));
		checkIfCycleStepNeeded = checkIfCycleStepNeeded
				&& !(citizensToBeTested.get(2).getState()
						.equals(CitizenState.RESCUED));
		checkIfCycleStepNeeded = checkIfCycleStepNeeded
				&& !(citizensToBeTested.get(3).getState()
						.equals(CitizenState.RESCUED));

		if (!checkIfCycleStepNeeded)
			u.cycleStep();
		assertEquals("first Citizen state should be rescued but was "
				+ citizensToBeTested.get(0).getState(), CitizenState.RESCUED,
				citizensToBeTested.get(0).getState());

		assertEquals("second Citizen state should be rescued but was "
				+ citizensToBeTested.get(1).getState(), CitizenState.RESCUED,
				citizensToBeTested.get(1).getState());

		if (citizensToBeTested.get(2).getState() == CitizenState.RESCUED)
			fail("third Citizen state should not be changed to rescued until safely evacuated to base.");

		if (citizensToBeTested.get(3).getState() == CitizenState.RESCUED)
			fail("fourth Citizen state should not be changed to rescued until safely evacuated to base.");

	}

	@Test(timeout = 3000)
	public void testEvacuatorTreatLogic() throws Exception {
		Simulator s = new Simulator(sos);
		Evacuator u = new Evacuator("evacuator1", getAddressFromWorld(s, 3, 4),
				5, null, 2);
		u.setWorldListener(s);
		Address ad = getAddressFromWorld(s, 3, 9);
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
		for (int i = 0; i < 10; i++) {
			u.cycleStep();
			b.setStructuralIntegrity(b.getStructuralIntegrity() - 10);

		}

		Address safeSpot = getAddressFromWorld(s, 0, 0);
		assertEquals("Number of citizens remaining in the building was wrong",
				1, b.getOccupants().size());

		assertEquals("first Citizen should be in the location 0,0 but was "
				+ citizensToBeTested.get(0).getLocation().getX() + " "
				+ citizensToBeTested.get(0).getLocation().getY(), safeSpot,
				citizensToBeTested.get(0).getLocation());
		assertEquals("first Citizen state should be rescued but was "
				+ citizensToBeTested.get(0).getState(), CitizenState.RESCUED,
				citizensToBeTested.get(0).getState());

		assertEquals("second Citizen should be in the location 0,0 but was "
				+ citizensToBeTested.get(1).getLocation().getX() + " "
				+ citizensToBeTested.get(1).getLocation().getY(), safeSpot,
				citizensToBeTested.get(1).getLocation());
		assertEquals("second Citizen state should be rescued but was "
				+ citizensToBeTested.get(1).getState(), CitizenState.RESCUED,
				citizensToBeTested.get(1).getState());

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
		assertEquals("fifth Citizen state should be rescued but was "
				+ citizensToBeTested.get(4).getState(), CitizenState.DECEASED,
				citizensToBeTested.get(4).getState());

	}

	@Test(timeout = 5000)
	public void testStrikeDisaster() throws Exception {
		testExistsInClass(Disaster.class, "strike", true, void.class);

		class TestDisaster extends Disaster {
			public TestDisaster(int startCycle, Rescuable target) {
				super(startCycle, target);
			}

			@Override
			public void cycleStep() {

			}
		}

		class TestRescuable implements Rescuable {
			public TestRescuable() {

			}

			@Override
			public Disaster getDisaster() {
				return null;
			}

			@Override
			public Address getLocation() {
				return null;
			}

			@Override
			public void struckBy(Disaster d) {
				struckByCalled = true;
			}
		}

		TestRescuable testRescuable0 = new TestRescuable();
		TestDisaster testDisaster3 = new TestDisaster(0, testRescuable0);

		testDisaster3.strike();
		if (!struckByCalled)
			fail("Method \"strike\" in class \"Disaster\" should trigger method \"struckBy\"");
		struckByCalled = false;

		if (!testDisaster3.isActive())
			fail("Method \"strike\" in class \"Disaster\" should set the disaster to active");

	}

	@Test(timeout = 5000)
	public void testStrikeInjury() throws Exception {
		testExistsInClass(Injury.class, "strike", true, void.class);

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
		Injury injury2 = new Injury(0, citizen0);

		injury2.strike();
		assertTrue(
				"Method \"strike\" in class \"Injury\" should set the initial \"bloodLoss\" to 30",
				citizen0.getBloodLoss() == 30);

	}

	@Test(timeout = 5000)
	public void testStrikeFire() throws Exception {
		testExistsInClass(Fire.class, "strike", true, void.class);

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
		Fire fire2 = new Fire(0, residentialBuilding2);

		fire2.strike();
		System.out.println(residentialBuilding2);
		assertTrue(
				"Method \"strike\" in class \"Fire\" should set the initial \"fireDamage\" to 10",
				residentialBuilding2.getFireDamage() == 10);

	}

	@Test(timeout = 5000)
	public void testCycleStepInfection() throws Exception {
		testExistsInClass(Infection.class, "cycleStep", true, void.class);

		int int0 = 0;
		int int9 = 9;
		Address address1 = new Address(int0, int9);
		String String7 = "izw";
		Citizen citizen0 = new Citizen(address1, String7, String7, int9, null);
		Infection infection2 = new Infection(0, citizen0);
		infection2.setActive(true);

		for (int i = 1; i <= 5; i++) {
			infection2.cycleStep();
			assertTrue(
					"Method \"cycleStep\" in class \"Infection\" should set the \"toxicity\" to "
							+ i * 15 + " by cycle " + i + " but was "
							+ citizen0.getToxicity(),
					citizen0.getToxicity() == i * 15);
		}

	}

	@Test(timeout = 5000)
	public void testCycleStepCollapse() throws Exception {
		testExistsInClass(Collapse.class, "cycleStep", true, void.class);

		int int8 = 8;
		int int5 = 5;
		Address address0 = new Address(int8, int5);
		ResidentialBuilding residentialBuilding2 = new ResidentialBuilding(
				address0);
		Collapse collapse2 = new Collapse(0, residentialBuilding2);
		collapse2.setActive(true);

		for (int i = 1; i <= 5; i++) {
			collapse2.cycleStep();
			assertTrue(
					"Method \"cycleStep\" in class \"Collapse\" should set the \"foundationDamage\" to "
							+ i
							* 10
							+ " by cycle "
							+ i
							+ " but was "
							+ residentialBuilding2.getFoundationDamage(),
					residentialBuilding2.getFoundationDamage() == i * 10);
		}

	}

	@Test(timeout = 5000)
	public void testCycleStepGasLeak() throws Exception {

		testExistsInClass(GasLeak.class, "cycleStep", true, void.class);

		int int8 = 8;
		int int5 = 5;
		Address address0 = new Address(int8, int5);
		ResidentialBuilding residentialBuilding2 = new ResidentialBuilding(
				address0);
		GasLeak gasLeak2 = new GasLeak(0, residentialBuilding2);
		gasLeak2.setActive(true);

		for (int i = 1; i <= 5; i++) {
			gasLeak2.cycleStep();
			assertTrue(
					"Method \"cycleStep\" in class \"GasLeak\" should set the \"gasLevel\" to "
							+ i * 15 + " by cycle " + i + " but was "
							+ residentialBuilding2.getGasLevel(),
					residentialBuilding2.getGasLevel() == i * 15);
		}
	}

	@Test(timeout = 1000)
	public void testCompoundDisaster0() throws Exception {
		init();
		Simulator s = new Simulator(sos);

		applyDisaster(testFire, testBuilding1);
		final Field disasterField = Simulator.class
				.getDeclaredField("plannedDisasters");
		disasterField.setAccessible(true);

		((ArrayList<Object>) disasterField.get(s)).clear();
		((ArrayList<Object>) disasterField.get(s)).add(testGasLeak);
		Field currentcyclefield = Simulator.class
				.getDeclaredField("currentCycle");
		currentcyclefield.setAccessible(true);
		currentcyclefield.set(s, 2);
		int currentCycle = currentcyclefield.getInt(s);
		final Field buildingField = Simulator.class
				.getDeclaredField("buildings");
		buildingField.setAccessible(true);
		((ArrayList<Object>) buildingField.get(s)).clear();
		testBuilding1.setGasLevel(0);
		testBuilding1.setFireDamage(10);

		((ArrayList<Object>) buildingField.get(s)).add(testBuilding1);
		final Field e_disasterField = Simulator.class
				.getDeclaredField("executedDisasters");
		e_disasterField.setAccessible(true);
		((ArrayList<Object>) e_disasterField.get(s)).add(testFire);
		clearArrayList(s);

		s.nextCycle();

		assertEquals(
				"The Disaster should be removed from planned disasters arraylist after striking ",
				0, ((ArrayList<Object>) disasterField.get(s)).size());

		String temp = "when a building is suffering from a fire disaster and should now be struck by a gas leak,";
		assertEquals(temp
				+ "The disaster should be added to executed disasters ", 2,
				((ArrayList<Object>) e_disasterField.get(s)).size());
		Disaster d = (Disaster) ((ArrayList<Object>) e_disasterField.get(s))
				.get(1);
		assertEquals(
				temp
						+ "The added disaster in the executed disaster should be Collapse",
				Collapse.class, d.getClass());
		assertEquals(
				temp
						+ "The target of the collapse disaster should be the same as the one in the planned disaster ",
				testBuilding1, d.getTarget());
		assertEquals(
				temp
						+ "The target of the collapse disaster should be the same as the one in the planned disaster ",
				currentCycle + 1, d.getStartCycle());

	}

	@Test(timeout = 1000)
	public void testCompoundDisaster1() throws Exception {
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
		testBuilding1.setGasLevel(0);
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

		String temp = "when a building is suffering from a gas leak disaster and should now be struck by a fire,and the building gaslevel is 0 then:";
		assertEquals(temp
				+ "the Disaster should be added to executed disasters ", 2,
				((ArrayList<Object>) e_disasterField.get(s)).size());

	}

	@Test(timeout = 1000)
	public void testCompoundDisaster2() throws Exception {
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
		int currentCycle = currentcyclefield.getInt(s);
		final Field buildingField = Simulator.class
				.getDeclaredField("buildings");
		buildingField.setAccessible(true);
		((ArrayList<Object>) buildingField.get(s)).clear();
		testBuilding1.setGasLevel(50);
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

		String temp = "when a building is suffering from a gas leak disaster and should now be struck by a fire,and the building gaslevel is strictly between 0 & 70 then:";
		assertEquals(temp
				+ "the disaster should be added to executed disasters ", 2,
				((ArrayList<Object>) e_disasterField.get(s)).size());
		Disaster d = (Disaster) ((ArrayList<Object>) e_disasterField.get(s))
				.get(1);
		assertEquals(
				temp
						+ "The added disaster in the executed disaster should be Collapse",
				Collapse.class, d.getClass());
		assertEquals(
				temp
						+ "The target of the collapse disaster should be the same as the one in the planned disaster ",
				testBuilding1, d.getTarget());
		assertEquals(
				temp
						+ "The target of the collapse disaster should be the same as the one in the planned disaster ",
				currentCycle + 1, d.getStartCycle());

	}

	@Test(timeout = 1000)
	public void testAssignAddress() throws Exception {
		init();
		Simulator s = new Simulator(sos);
		final Field worldField = Simulator.class.getDeclaredField("world");
		worldField.setAccessible(true);
		Address[][] correctWorld = ((Address[][]) worldField.get(s));
		s.assignAddress(testAmbulance, 2, 3);
		assertEquals("Assign address ", correctWorld[2][3],
				testAmbulance.getLocation());
		s.assignAddress(testCitizen1, 4, 5);
		assertEquals("Assign address ", correctWorld[4][5],
				testCitizen1.getLocation());

	}

	@Test(timeout = 1000)
	public void testCalculateCasualtiesLogic() throws Exception {
		init();
		Simulator s = new Simulator(sos);
		final Field citizensField = Simulator.class
				.getDeclaredField("citizens");
		citizensField.setAccessible(true);
		((ArrayList<Object>) citizensField.get(s)).clear();
		testCitizen1.setState(CitizenState.DECEASED);
		testCitizen2.setState(CitizenState.DECEASED);
		testCitizen3.setState(CitizenState.IN_TROUBLE);
		testCitizen4.setState(CitizenState.RESCUED);
		((ArrayList<Object>) citizensField.get(s)).add(testCitizen1);
		((ArrayList<Object>) citizensField.get(s)).add(testCitizen2);
		((ArrayList<Object>) citizensField.get(s)).add(testCitizen3);
		((ArrayList<Object>) citizensField.get(s)).add(testCitizen4);
		int value = s.calculateCasualties();
		int actual = 2;
		assertEquals("casulties", actual, value);

	}

	@Test(timeout = 1000)
	public void testNextStepIsCalledInSimulator() throws Exception {
		init();
		Simulator s = new Simulator(sos);
		count = new HashMap<>();
		buildingsCall(s);
		plannedDisastersCall(s);
		unitsCall(s);
		citizenCall(s);
		executedDisastersCall(s);
		s.nextCycle();
		assertTrue(
				"nextCycle method in Simulator class should call cycleStep method in Units class ",
				hascalledUnit);
		assertTrue(
				"nextCycle method in Simulator class should call strike method over planned disasters ",
				hascalledPlannedDisaster);
		assertTrue(
				"nextCycle method in Simulator class should call cycleStep method in Citizen class ",
				hascalledCitizen);
		assertTrue(
				"nextCycle method in Simulator class should call cycleStep method in Building class ",
				hascalledBuilding);
		assertTrue(
				"nextCycle method in Simulator class should call cycleStep method over active disasters ",
				hascalledExecutedDisaster);
		String[] test = { "PlannedDisaster", "Unit", "ExecutedDisaster" };
		for (int i = 0; i < test.length; i++) {
			int key = count.get(test[i]);
			assertEquals(test[i] + " should be called number " + (i + 1)
					+ " in next cycle", i + 1, key);
		}

	}

	@Test(timeout = 1000)
	public void testGameOverCase1() throws Exception {
		init();
		Simulator s = new Simulator(sos);
		Disaster d = new Disaster(1, testCitizen1) {

			@Override
			public void cycleStep() {

			}
		};

		final Field disasterField = Simulator.class
				.getDeclaredField("plannedDisasters");
		disasterField.setAccessible(true);

		((ArrayList<Object>) disasterField.get(s)).clear();
		((ArrayList<Object>) disasterField.get(s)).add(d);

		assertFalse(
				"check gameOver in class simulator should return false when there are still planned disasters",
				s.checkGameOver());
		((ArrayList<Object>) disasterField.get(s)).clear();
		assertTrue(
				"check gameOver in class simulator should return true when there aren't any planned disasters",
				s.checkGameOver());

	}

	@Test(timeout = 1000)
	public void testGameOverCase2() throws Exception {
		init();
		Simulator s = new Simulator(sos);

		final Field disasterField = Simulator.class
				.getDeclaredField("plannedDisasters");
		disasterField.setAccessible(true);

		((ArrayList<Object>) disasterField.get(s)).clear();
		final Field executeDisasterField = Simulator.class
				.getDeclaredField("executedDisasters");
		executeDisasterField.setAccessible(true);
		testinjInjury.setActive(true);

		((ArrayList<Object>) executeDisasterField.get(s)).clear();
		((ArrayList<Object>) executeDisasterField.get(s)).add(testinjInjury);

		assertFalse(
				"check gameOver in class simulator should return false when there are still active disasters",
				s.checkGameOver());
		((ArrayList<Object>) executeDisasterField.get(s)).clear();
		assertTrue(
				"check gameOver in class simulator should return true when there aren't any active disasters",
				s.checkGameOver());
	}

	@Test(timeout = 1000)
	public void testGameOverCase3() throws Exception {
		init();
		Simulator s = new Simulator(sos);
		Unit u = new Unit(id1, testAddress1, 2, null) {

			public void treat() {

			}

			public void cycleStep() {

			}

		};
		final Field disasterField = Simulator.class
				.getDeclaredField("plannedDisasters");
		disasterField.setAccessible(true);
		final Field executeDisasterField = Simulator.class
				.getDeclaredField("executedDisasters");
		executeDisasterField.setAccessible(true);
		((ArrayList<Object>) disasterField.get(s)).clear();
		((ArrayList<Object>) executeDisasterField.get(s)).clear();
		final Field unitsField = Simulator.class
				.getDeclaredField("emergencyUnits");
		unitsField.setAccessible(true);
		((ArrayList<Object>) unitsField.get(s)).clear();
		u.setState(UnitState.TREATING);
		((ArrayList<Object>) unitsField.get(s)).add(u);
		assertFalse(
				"check gameOver in class simulator should return false when there are units that aren't IDLE",
				s.checkGameOver());
		u.setState(UnitState.RESPONDING);
		((ArrayList<Object>) unitsField.get(s)).clear();
		((ArrayList<Object>) unitsField.get(s)).add(u);
		assertFalse(
				"check gameOver in class simulator should return false when there are units that aren't IDLE",
				s.checkGameOver());
		u.setState(UnitState.IDLE);
		((ArrayList<Object>) unitsField.get(s)).clear();
		((ArrayList<Object>) unitsField.get(s)).add(u);
		assertTrue(
				"check gameOver in class simulator should return True when there are units that are IDLE",
				s.checkGameOver());

	}

	@Test(timeout = 1000)
	public void testCurrentCycleLogic() throws Exception {
		init();
		Simulator s = new Simulator(sos);
		final Field currentcyclefield = Simulator.class
				.getDeclaredField("currentCycle");
		currentcyclefield.setAccessible(true);
		currentcyclefield.set(s, 3);

		clearArrayList(s);

		s.nextCycle();
		assertEquals(
				"Current cycle variable in class Simulator should be incremented after calling nextCycle",
				4, currentcyclefield.get(s));

	}

	@Test(timeout = 1000)
	public void testSimulatorInitializeUnitListener() throws Exception {
		Simulator s = new Simulator(sos);
		final Field unitsField = Simulator.class
				.getDeclaredField("emergencyUnits");
		unitsField.setAccessible(true);
		ArrayList<Unit> testunit = (ArrayList<Unit>) unitsField.get(s);
		for (int i = 0; i < testunit.size(); i++) {
			boolean cond = testunit.get(i).getWorldListener() != null;
			assertTrue(
					"Wotldlistener value in class unit should be instantiated after calling simulator",
					cond);
		}
	}

	@Test(timeout = 1000)
	public void testSimulatorInitializeBuildingListener() throws Exception {
		Simulator s = new Simulator(sos);
		final Field buildingsField = Simulator.class
				.getDeclaredField("buildings");
		buildingsField.setAccessible(true);
		ArrayList<ResidentialBuilding> testBuilding = (ArrayList<ResidentialBuilding>) buildingsField
				.get(s);
		final Field listenerField = ResidentialBuilding.class
				.getDeclaredField("emergencyService");
		listenerField.setAccessible(true);

		for (int i = 0; i < testBuilding.size(); i++) {

			boolean cond = listenerField.get(testBuilding.get(i)) != null;
			assertTrue(
					"SOS Listener value in class Building should be instantiated after calling simulator",
					cond);
		}
	}

	@Test(timeout = 1000)
	public void testConstructorSimulator() throws Exception {
		Class[] inputs = { SOSListener.class };
		testConstructorExists(Simulator.class, inputs);
	}

	@Test(timeout = 1000)
	public void testInstanceVariableEmergencyUnitsGetterAndSetter()
			throws Exception {
		testGetterMethodExistsInClass(Simulator.class, "getEmergencyUnits",
				ArrayList.class, true);
		testSetterMethodExistsInClass(Simulator.class, "setEmergencyUnits",
				ArrayList.class, false);
	}

	@Test(timeout = 1000)
	public void testInstanceVariableEmergencyServiceGetterAndSetter()
			throws Exception {
		testGetterMethodExistsInClass(Simulator.class, "getEmergencyService",
				SOSListener.class, false);
		testSetterMethodExistsInClass(Simulator.class, "setEmergencyService",
				SOSListener.class, true);
	}

	@Test(timeout = 1000)
	public void testInstanceVariableSimulatorEmergencyService()
			throws Exception {
		testInstanceVariableIsPresent(Simulator.class, "emergencyService", true);
		testInstanceVariableIsPrivate(Simulator.class, "emergencyService");
	}

	@Test(timeout = 1000)
	public void NonActiveDisasterCallStrike() throws Exception {
		Simulator s = new Simulator(sos);
		hascalledGasDisaster = false;
		hascalledInfectionDisaster = false;
		hascalledInjuryDisaster = false;
		hascalledFireDisaster = false;
		hascalledCollapseDisaster = false;

		final Field currentcyclefield = Simulator.class
				.getDeclaredField("currentCycle");
		currentcyclefield.setAccessible(true);

		currentcyclefield.set(s, 2);
		testGasLeak = new GasLeak(2, testBuilding1) {
			public void cycleStep() {
				hascalledGasDisaster = true;

			}
		};
		testFire = new Fire(2, testBuilding1) {
			public void cycleStep() {
				hascalledFireDisaster = true;

			}
		};
		testCollapse = new Collapse(2, testBuilding1) {
			public void cycleStep() {
				hascalledCollapseDisaster = true;

			}
		};
		testInjury = new Injury(2, testCitizen1) {
			public void cycleStep() {
				hascalledInjuryDisaster = true;

			}
		};
		testInfection = new Infection(2, testCitizen1) {
			public void cycleStep() {
				hascalledInfectionDisaster = true;

			}
		};

		final Field disasterField = Simulator.class
				.getDeclaredField("executedDisasters");
		disasterField.setAccessible(true);
		((ArrayList<Object>) disasterField.get(s)).clear();
		testInjury.setActive(false);
		testInfection.setActive(false);
		testFire.setActive(false);
		testGasLeak.setActive(false);
		testCollapse.setActive(false);
		((ArrayList<Object>) disasterField.get(s)).add(testInjury);
		((ArrayList<Object>) disasterField.get(s)).add(testInfection);
		((ArrayList<Object>) disasterField.get(s)).add(testFire);
		((ArrayList<Object>) disasterField.get(s)).add(testGasLeak);
		((ArrayList<Object>) disasterField.get(s)).add(testCollapse);
		s.nextCycle();
		assertFalse(
				"If the Injury disaster is not active then cycleStep method shouldn't be called",
				hascalledInjuryDisaster);
		assertFalse(
				"If the Infection disaster is not active then cycleStep method shouldn't be called",
				hascalledInfectionDisaster);
		assertFalse(
				"If the GasLeak disaster is not active then cycleStep method shouldn't be called",
				hascalledGasDisaster);
		assertFalse(
				"If the Collapse disaster is not active then cycleStep method shouldn't be called",
				hascalledCollapseDisaster);
		assertFalse(
				"If the Fire disaster is not active then cycleStep method shouldn't be called",
				hascalledFireDisaster);
	}

	@Test(timeout = 1000)
	public void testCitizenInstanceVariableEmergencyService() throws Exception {

		testInstanceVariableIsPresent(Citizen.class, "emergencyService", true);
		testInstanceVariableIsPrivate(Citizen.class, "emergencyService");

	}

	@Test(timeout = 1000)
	public void testCitizenInstanceVariableEmergencyServiceGetterAndSetter()
			throws Exception {

		testGetterMethodExistsInClass(Citizen.class, "getEmergencyService",
				SOSListener.class, false);
		testSetterMethodExistsInClass(Citizen.class, "setEmergencyService",
				SOSListener.class, true);
		testSetterLogic(someRandomCitizen(), "emergencyService",
				new CommandCenter(), SOSListener.class);
	}

	@Test(timeout = 1000)
	public void testCitizenStruckBySetsDisasterCorrectly() throws Exception {

		Citizen ct = someRandomCitizen();

		ArrayList<Disaster> disasters = new ArrayList<Disaster>();
		int start = (int) (Math.random() * 50);
		disasters.add(new Injury(start, ct));
		start = (int) (Math.random() * 50);
		disasters.add(new Infection(start, ct));

		CommandCenter cc = new CommandCenter();

		Field emergencyService = null;
		try {

			emergencyService = Citizen.class
					.getDeclaredField("emergencyService");
			emergencyService.setAccessible(true);
			emergencyService.set(ct, cc);

		} catch (NoSuchFieldException e) {
			fail("Class Citizen should have emergencyService instance variable");
		}

		try {

			for (Disaster dis : disasters) {

				Method struckBy = Citizen.class.getDeclaredMethod("struckBy",
						Disaster.class);

				struckBy.invoke(ct, dis);

				Field disaster = null;
				try {

					disaster = Citizen.class.getDeclaredField("disaster");
					disaster.setAccessible(true);
					Disaster disasterValue = (Disaster) disaster.get(ct);

					assertEquals(
							"struckBy method in Citizen class should set the disaster instance variable to the disaster passed to the method",
							dis, disasterValue);

					disaster.set(ct, null);

				} catch (NoSuchFieldException e) {
					fail("Class Citizen should have disaster instance variable");

				}
			}
		} catch (NoSuchMethodException e) {
			fail("Citizen class should have struckBy method that takes Disaster as an input");
		}

	}

	@Test(timeout = 1000)
	public void testCitizenStruckByNotifiesListener() throws Exception {

		CommandCenter cc = new CommandCenter() {
			@Override
			public void receiveSOSCall(Rescuable b) {
				called = true;
				target = b;
			}
		};

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

				assertTrue(
						"If a disaster strikes any Citizen, the Citizen should notify its SOS listener",
						called);
				assertEquals(
						"If a disaster strikes any Citizen, the Citizen should notify its SOS listener while declaring itself as the target",
						ct, target);
				called = false;
				target = null;

			}
		} catch (NoSuchFieldException e) {
			fail("Class Citizen should have emergencyService instance variable");
		}
	}

	@Test(timeout = 1000)
	public void testCitizenBloodLossSetterBounds() throws Exception {

		Citizen ct = someRandomCitizen();
		int r = (int) (Math.random() * 100) + 100;
		ct.setBloodLoss(r);

		Field bloodLoss = null;
		try {

			bloodLoss = Citizen.class.getDeclaredField("bloodLoss");
			bloodLoss.setAccessible(true);
			int bloodLossValue = (int) bloodLoss.get(ct);

			assertEquals(
					"The bloodLoss value of any Citizen can not be set to more than 100.",
					100, bloodLossValue);

			ct = someRandomCitizen();
			r = (int) (Math.random() * 100) * -1;
			ct.setBloodLoss(r);

			bloodLossValue = (int) bloodLoss.get(ct);

			assertEquals(
					"The bloodLoss value of any Citizen can not be set to less than 0.",
					0, bloodLossValue);

		} catch (NoSuchFieldException e) {
			fail("Class Citizen should have bloodLoss instance variable");

		}

	}

	@Test(timeout = 1000)
	public void testCitizenBloodLossSetterKillsWhenOneHundred() {

		Citizen ct = someRandomCitizen();
		int r = 100;
		ct.setBloodLoss(r);

		assertEquals(
				"If the bloodLoss value of any Citizen is set to 100 or more, that Citizen should die and their HP updated accordingly.",
				0, ct.getHp());

		assertEquals(
				"If the bloodLoss value of any Citizen is set to 100 or more, that Citizen should die and their state updated accordingly.",
				CitizenState.DECEASED, ct.getState());

		ct = someRandomCitizen();
		r = (int) (Math.random() * 100) + 100;
		ct.setBloodLoss(r);

		assertEquals(
				"If the bloodLoss value of any Citizen is set to 100 or more, that Citizen should die and their HP updated accordingly.",
				0, ct.getHp());

		assertEquals(
				"If the bloodLoss value of any Citizen is set to 100 or more, that Citizen should die and their state updated accordingly.",
				CitizenState.DECEASED, ct.getState());

	}

	@Test(timeout = 1000)
	public void testCitizenBloodLossSetterDoesNotDamageHP() {

		Citizen ct = someRandomCitizen();
		int r = (int) (Math.random() * 50) + 25;
		ct.setHp(r);

		ct.setBloodLoss((int) (Math.random() * 50) + 25);

		assertEquals(
				"When setting the value of the bloodLoss of any Citizen to anything below 100, the Citizen's hp should not change.",
				r, ct.getHp());

	}

	@Test(timeout = 1000)
	public void testCitizenHPSetterBounds() throws Exception {

		Citizen ct = someRandomCitizen();
		int r = (int) (Math.random() * 100) + 100;
		ct.setHp(r);

		Field hp = null;
		try {

			hp = Citizen.class.getDeclaredField("hp");
			hp.setAccessible(true);
			int hpValue = (int) hp.get(ct);

			assertEquals(
					"The hp value of any Citizen can not be set to more than 100.",
					100, hpValue);

			ct = someRandomCitizen();
			r = (int) (Math.random() * 100) * -1;
			ct.setHp(r);

			hpValue = (int) hp.get(ct);

			assertEquals(
					"The hp value of any Citizen can not be set to less than 0.",
					0, hpValue);

		} catch (NoSuchFieldException e) {
			fail("Class Citizen should have hp instance variable");

		}

	}

	@Test(timeout = 1000)
	public void testCitizenHPSetterUpdatesStateOnDeath() throws Exception {

		Citizen ct = someRandomCitizen();
		int r = 0;

		ct.setHp(r);

		assertEquals(
				"If the hp value of any Citizen is set to 0 or less, that Citizen should die and their state updated accordingly.",
				CitizenState.DECEASED, ct.getState());

		ct = someRandomCitizen();
		r = (int) (Math.random() * -100) - 1;

		ct.setHp(r);

		assertEquals(
				"If the hp value of any Citizen is set to 0 or less, that Citizen should die and their state updated accordingly.",
				CitizenState.DECEASED, ct.getState());

	}

	@Test(timeout = 1000)
	public void testCitizenCycleStepWhenHealthy() {

		Citizen ct = someRandomCitizen();
		int r = (int) (Math.random() * 50) + 25;
		ct.setHp(r);

		ct.cycleStep();

		assertEquals(
				"Calling cycleStep on a Citizen with no bloodLoss or toxicity should not affect their HP.",
				r, ct.getHp());

	}

	@Test(timeout = 1000)
	public void testCitizenCycleStepWithToxicityUnderThirty() {

		for (int i = 0; i < 50; i++) {

			Citizen ct = someRandomCitizen();
			int rHP = (int) (Math.random() * 50) + 25;
			ct.setHp(rHP);

			int rT = (int) (Math.random() * 29) + 1;
			ct.setToxicity(rT);

			ct.cycleStep();

			assertEquals(
					"Calling cycleStep on a Citizen with toxicity greater than 0 and less than 30 should decrease their HP by 5",
					5, rHP - ct.getHp());

		}
	}

	@Test(timeout = 1000)
	public void testCitizenCycleStepWithToxicityBetweenThirtyAndSeventy() {

		for (int i = 0; i < 50; i++) {

			Citizen ct = someRandomCitizen();
			int rHP = (int) (Math.random() * 50) + 25;
			ct.setHp(rHP);

			int rT = (int) (Math.random() * 39) + 30;
			ct.setToxicity(rT);

			ct.cycleStep();

			assertEquals(
					"Calling cycleStep on a Citizen with toxicity greater than or equal 30 and less than 70 should decrease their HP by 10",
					10, rHP - ct.getHp());

		}
	}

	@Test(timeout = 1000)
	public void testCitizenCycleStepWithToxicityBetweenSeventyAndHundred() {

		for (int i = 0; i < 50; i++) {

			Citizen ct = someRandomCitizen();
			int rHP = (int) (Math.random() * 50) + 25;
			ct.setHp(rHP);

			int rT = (int) (Math.random() * 29) + 70;
			ct.setToxicity(rT);

			ct.cycleStep();

			assertEquals(
					"Calling cycleStep on a Citizen with toxicity greater than or equal 70 and less than 100 should decrease their HP by 15",
					15, rHP - ct.getHp());

		}
	}

	@Test(timeout = 1000)
	public void testInstanceVariableEmergencyServiceResidentialBuilding()
			throws Exception {
		testInstanceVariableIsPresent(Class.forName(buildingPath),
				"emergencyService", true);
		testInstanceVariableIsPrivate(Class.forName(buildingPath),
				"emergencyService");
	}

	@Test(timeout = 1000)
	public void testInstanceVariableResidentialBuildingEmergencyServiceGetterAndSetter()
			throws Exception {
		testGetterMethodExistsInClass(Class.forName(buildingPath),
				"getEmergencyService", Class.forName(sosListenerPath), false);
		testSetterMethodExistsInClass(Class.forName(buildingPath),
				"setEmergencyService", Class.forName(sosListenerPath), true);
	}

	@Test(timeout = 1000)
	public void testResidentialBuildingCycleStep()
			throws ClassNotFoundException {
		try {
			ResidentialBuilding.class.getDeclaredMethod("cycleStep");
		} catch (NoSuchMethodException e) {
			fail("Any ResidentialBuilding is Simulatable so, it should have cycleStep method.");
		}
	}

	@Test(timeout = 1000)
	public void testResidentialBuildingStruckBy() throws ClassNotFoundException {
		try {
			ResidentialBuilding.class.getDeclaredMethod("struckBy",
					Disaster.class);
		} catch (NoSuchMethodException e) {
			fail("Any ResidentialBuilding is Rescuable so, it should have struckBy method.");
		}
	}

	@Test(timeout = 1000)
	public void testResidentialBuildingEmergencyServiceSetterLogic()
			throws Exception {
		CommandCenter c = new CommandCenter();
		Address d = someRandomAddress();
		ResidentialBuilding b = new ResidentialBuilding(d);
		b.setEmergencyService(c);
		testSetterLogic(b, "emergencyService", c, SOSListener.class);
	}

	@Test(timeout = 1000)
	public void testResidentialBuildingFoundationDamageSetterUpperBound()
			throws IllegalArgumentException, IllegalAccessException {
		Address d = someRandomAddress();
		ResidentialBuilding b = new ResidentialBuilding(d);
		int r = (int) (Math.random() * 100) + 100;
		b.setFoundationDamage(r);
		Field foundationDamage = null;
		try {
			foundationDamage = ResidentialBuilding.class
					.getDeclaredField("foundationDamage");
			foundationDamage.setAccessible(true);

		} catch (NoSuchFieldException e) {
			fail("Class ResidentialBuilding should have foundationDamage instance variable");

		}
	}

	@Test(timeout = 1000)
	public void testResidentialBuildingFoundationDamageSetterChangesIntegrity()
			throws IllegalArgumentException, IllegalAccessException {
		Address d = someRandomAddress();
		ResidentialBuilding b = new ResidentialBuilding(d);
		int r = (int) (Math.random() * 100) + 100;
		b.setFoundationDamage(r);
		Field structuralIntegrity = null;
		try {
			structuralIntegrity = ResidentialBuilding.class
					.getDeclaredField("structuralIntegrity");
			structuralIntegrity.setAccessible(true);

			int structuralIntegrityValue = (int) structuralIntegrity.get(b);
			assertEquals(
					"If the foundation damage value in ResidentialBuilding class is set to be 100 or over, its structural integrity should be set to 0",
					0, structuralIntegrityValue);
		} catch (NoSuchFieldException e) {
			fail("Class ResidentialBuilding should have structuralIntegrity instance variable");

		}
	}

	@Test(timeout = 1000)
	public void testResidentialBuildingFireDamageSetterUpperBound()
			throws IllegalArgumentException, IllegalAccessException {
		Address d = someRandomAddress();
		ResidentialBuilding b = new ResidentialBuilding(d);
		int r = (int) (Math.random() * 100) + 100;
		b.setFireDamage(r);
		Field fireDamage = null;
		try {
			fireDamage = ResidentialBuilding.class
					.getDeclaredField("fireDamage");
			fireDamage.setAccessible(true);

			int fireDamageValue = (int) fireDamage.get(b);
			assertEquals(
					"If the fire damage value in ResidentialBuilding class is set to be over 100, it should be set to 100",
					100, fireDamageValue);
		} catch (NoSuchFieldException e) {
			fail("Class ResidentialBuilding should have fireDamage instance variable");

		}
	}

	@Test(timeout = 1000)
	public void testResidentialBuildingFireDamageSetterLowerBound()
			throws IllegalArgumentException, IllegalAccessException {
		Address d = someRandomAddress();
		ResidentialBuilding b = new ResidentialBuilding(d);
		int r = (int) (Math.random() * 100 - 100);
		b.setFireDamage(r);
		Field fireDamage = null;
		try {
			fireDamage = ResidentialBuilding.class
					.getDeclaredField("fireDamage");
			fireDamage.setAccessible(true);

			int fireDamageValue = (int) fireDamage.get(b);
			assertEquals(
					"If the fire damage value in ResidentialBuilding class is set to be below 0, it should be set to 0",
					0, fireDamageValue);
		} catch (NoSuchFieldException e) {
			fail("Class ResidentialBuilding should have fireDamage instance variable");

		}
	}

	@Test(timeout = 1000)
	public void testResidentialBuildingStructuralIntegritySetterLowerBound()
			throws IllegalArgumentException, IllegalAccessException {
		Address d = someRandomAddress();
		ResidentialBuilding b = new ResidentialBuilding(d);
		int r = (int) (Math.random() * 100 - 100);
		b.setStructuralIntegrity(r);
		Field structuralIntegrity = null;
		try {
			structuralIntegrity = ResidentialBuilding.class
					.getDeclaredField("structuralIntegrity");
			structuralIntegrity.setAccessible(true);

			int structuralIntegrityValue = (int) structuralIntegrity.get(b);
			assertEquals(
					"If the structural Integrity value in ResidentialBuilding class is set to be below 0, it should be set to 0",
					0, structuralIntegrityValue);
		} catch (NoSuchFieldException e) {
			fail("Class ResidentialBuilding should have structuralIntegrity instance variable");

		}
	}

	@Test(timeout = 1000)
	public void testResidentialBuildingStructuralIntegrityLessOrEqualZeroKillsAllOccupants()
			throws IllegalArgumentException, IllegalAccessException {
		Address d = someRandomAddress();
		ResidentialBuilding b = new ResidentialBuilding(d);
		int r = (int) (Math.random() * 100 - 100);
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
			b.setStructuralIntegrity(r);
			ArrayList<Citizen> occupantsAfter = (ArrayList<Citizen>) occupants
					.get(b);
			for (int i = 0; i < occupantsAfter.size(); i++) {
				Citizen c = occupantsAfter.get(i);
				try {
					Field hp = Citizen.class.getDeclaredField("hp");
					hp.setAccessible(true);
					int hpValue = (int) hp.get(c);
					assertEquals(
							"if the structural integrity of a building is set to 0 or below, all of its occupants should die",
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
	public void testResidentialBuildingFoundationDamageDoesnotAffectOccupants()
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
			b.setFoundationDamage(r);
			ArrayList<Citizen> occupantsAfter = (ArrayList<Citizen>) occupants
					.get(b);
			for (int i = 0; i < occupantsAfter.size(); i++) {
				Citizen c = occupantsAfter.get(i);
				try {
					Field hp = Citizen.class.getDeclaredField("hp");
					hp.setAccessible(true);
					int hpValue = (int) hp.get(c);
					assertEquals(
							"if the foundation damage of a building is set to be greater than 0 and less than 100, non of its occupants hp should be affected",
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
	public void testResidentialBuildingFireDamageDoesnotAffectOccupants()
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
			b.setFireDamage(r);
			ArrayList<Citizen> occupantsAfter = (ArrayList<Citizen>) occupants
					.get(b);
			for (int i = 0; i < occupantsAfter.size(); i++) {
				Citizen c = occupantsAfter.get(i);
				try {
					Field hp = Citizen.class.getDeclaredField("hp");
					hp.setAccessible(true);
					int hpValue = (int) hp.get(c);
					assertEquals(
							"if the fire damage of a building is set to be greater than 0 and less than 100, non of its occupants hp should be affected",
							100, hpValue);
				} catch (NoSuchFieldException e) {
					fail("Class Citizen should have hp instance variable");
				}
			}
		} catch (NoSuchFieldException e) {
			fail("Class ResidentialBuilding should have occupants instance variable");
		}
		System.out.println(b);
	}

	@Test(timeout = 1000)
	public void testResidentialBuildingStruckBySetsDisasterCorrectly()
			throws Exception {
		Address d = someRandomAddress();
		ResidentialBuilding b = new ResidentialBuilding(d);
		Disaster dis = someRandomDisaster(b);
		CommandCenter c = new CommandCenter();
		System.out.println(b);
		Field emergencyService = null;
		try {
			emergencyService = ResidentialBuilding.class
					.getDeclaredField("emergencyService");
			emergencyService.setAccessible(true);
			emergencyService.set(b, c);
		} catch (NoSuchFieldException e) {
			fail("Class ResidentialBuilding should have emergencyService instance variable");

		}
		try {
			Method struckBy = ResidentialBuilding.class.getDeclaredMethod(
					"struckBy", Disaster.class);
			struckBy.invoke(b, dis);
			Field disaster = null;
			try {
				disaster = ResidentialBuilding.class
						.getDeclaredField("disaster");
				disaster.setAccessible(true);

				Disaster disasterValue = (Disaster) disaster.get(b);
				assertEquals(
						"struckBy method in residential building class should set the disaster instance variable to the disaster passed to the method",
						dis, disasterValue);
			} catch (NoSuchFieldException e) {
				fail("Class ResidentialBuilding should have disaster instance variable");

			}
		} catch (NoSuchMethodException e) {
			fail("ResidentialBuilding class should have struckBy method that takes Disaster as an input");
		}

	}

	@Test(timeout = 1000)
	public void testResidentialBuildingStruckByNotifiesListener()
			throws Exception {
		CommandCenter c = new CommandCenter() {
			@Override
			public void receiveSOSCall(Rescuable b) {
				called = true;
				target = b;
			}
		};
		Address d = someRandomAddress();
		ResidentialBuilding b = new ResidentialBuilding(d);
		try {
			Field emergencyService = ResidentialBuilding.class
					.getDeclaredField("emergencyService");
			emergencyService.setAccessible(true);
			emergencyService.set(b, c);
			Disaster dis = someRandomDisaster(b);
			b.struckBy(dis);
			assertTrue(
					"If a disaster strikes any building, the building should notify its SOS listener",
					called);
			assertEquals(
					"If a disaster strikes any building, the building should notify its SOS listener while passing itself as a parameter",
					b, target);
			target = null;
			called = false;
		} catch (NoSuchFieldException e) {
			fail("Class ResidentialBuilding should have emergencyService instance variable");
		}
	}

	@Test(timeout = 1000)
	public void testResidentialBuildingCycleStep1()
			throws IllegalArgumentException, IllegalAccessException {
		Address d = someRandomAddress();
		ResidentialBuilding b = new ResidentialBuilding(d);
		int r1 = (int) (Math.random() * 99) + 1;
		int r2 = (int) (Math.random() * 99) + 1;
		int r3 = (int) (Math.random() * 99) + 1;
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
			try {
				Field fireDamage = ResidentialBuilding.class
						.getDeclaredField("fireDamage");
				fireDamage.setAccessible(true);
				fireDamage.set(b, r1);
			} catch (NoSuchFieldException e) {
				fail("Class ResidentialBuilding should have fireDamage instance variable");
				return;
			}
			try {
				Field gasLevel = ResidentialBuilding.class
						.getDeclaredField("gasLevel");
				gasLevel.setAccessible(true);
				gasLevel.set(b, r2);
			} catch (NoSuchFieldException e) {
				fail("Class ResidentialBuilding should have gasLevel instance variable");
				return;
			}
			try {
				Field foundationDamage = ResidentialBuilding.class
						.getDeclaredField("foundationDamage");
				foundationDamage.setAccessible(true);
				foundationDamage.set(b, r3);
			} catch (NoSuchFieldException e) {
				fail("Class ResidentialBuilding should have foundationDamage instance variable");
				return;
			}
			ArrayList<Citizen> occupantsAfter = (ArrayList<Citizen>) occupants
					.get(b);
			b.cycleStep();
			for (int i = 0; i < occupantsAfter.size(); i++) {
				Citizen c = occupantsAfter.get(i);
				try {
					Field hp = Citizen.class.getDeclaredField("hp");
					hp.setAccessible(true);
					int hpValue = (int) hp.get(c);
					assertEquals(
							"Calling cycleStep on a building should not affect the hp of the citizens regardless of the its fire damage and gas lavel",
							100, hpValue);
				} catch (NoSuchFieldException e) {
					fail("Class Citizen should have hp instance variable");
				}
			}
			try {
				Field fireDamage = ResidentialBuilding.class
						.getDeclaredField("fireDamage");
				fireDamage.setAccessible(true);
				int fireAfter = (int) fireDamage.get(b);
				assertEquals(
						"Calling cycleStep on a building does not affect its fire damage.",
						r1, fireAfter);
			} catch (NoSuchFieldException e) {
				fail("Class ResidentialBuilding should have fireDamage instance variable");
				return;
			}
			try {
				Field gasLevel = ResidentialBuilding.class
						.getDeclaredField("gasLevel");
				gasLevel.setAccessible(true);
				int gasAfter = (int) gasLevel.get(b);
				assertEquals(
						"Calling cycleStep on a building does not affect its gas level.",
						r2, gasAfter);
			} catch (NoSuchFieldException e) {
				fail("Class ResidentialBuilding should have gasLevel instance variable");
				return;
			}
			try {
				Field foundationDamage = ResidentialBuilding.class
						.getDeclaredField("foundationDamage");
				foundationDamage.setAccessible(true);
				int foundationAfter = (int) foundationDamage.get(b);
				assertEquals(
						"Calling cycleStep on a building does not affect its foundation damage.",
						r3, foundationAfter);
			} catch (NoSuchFieldException e) {
				fail("Class ResidentialBuilding should have gasLevel instance variable");
				return;
			}

		} catch (NoSuchFieldException e) {
			fail("Class ResidentialBuilding should have occupants instance variable");
		}
		System.out.println(b);
	}

	@Test(timeout = 1000)
	public void testResidentialBuildingCycleStep6()
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
			int r2 = (int) ((Math.random() * 30) + 70);
			fireDamage.set(b, r2);
			b.cycleStep();
			int structuralAfter = (int) structuralIntegrity.get(b);
			assertTrue(
					"Calling cycleStep on a building with fire damage greater than or equal 70 should decrease its structural integrity by 7",
					r1 - structuralAfter == 7);
		}

	}

}
