package gsi.simulator;

import gsi.simulator.Logger.Logger;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import gsi.disasters.Disaster;
import gsi.disasters.DisasterType;
import gsi.disasters.StateType;
import gsi.disasters.SizeType;
import gsi.disasters.DensityType;
import gsi.disasters.Person;
import gsi.disasters.InjuryDegree;
import gsi.simulator.rest.EventsManagement;

/**
 *
 * @author Luis Delgado
 */
public class Simulator {

    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger("gsi.simulator");
    /**
     * List containing the disasters generated in the simulation
     */
    private List<Disaster> disasters;
    /**
     * Queue where events are inserted
     */
    private EventQueue queue;
    /**
     * Random generator
     */
    private RandomGenerator generator;
    /**
     * Parameters
     */
    private Parameters parameters;
    /**
     * Saves if the simulation is paused
     */
     private boolean pause;

    /**
     * Constructor for empty simulator
     */
    public Simulator() {
        this.disasters = new ArrayList<Disaster>();
        this.queue = new EventQueue();
    }

    /**
     * Constructor with file parameters
     */
    public Simulator(Parameters parameters) {
        this.parameters = parameters;
        this.generator = new RandomGenerator(parameters);
        this.disasters = new ArrayList<Disaster>();
        this.queue = new EventQueue();
    }

    /**
     * Runs a simulation loop which lasts the specified time
     * @param howLong length of the simulation
     */
    private void simulateLoop(int howLong) {
        pause = false;
        Event currentEvent = Event.generateNewFire(0);
        Event firstRefresh = Event.generateRefresh(null, generator.refreshPeriod());
        queue.insert(firstRefresh);

        while (currentEvent.getInstant() <= howLong && !pause) {
            //Generation of a new fire
            if (currentEvent.isFireGeneration()) {
                SizeType size = generator.fireDefineSize(); // Size of the fire
                int strength = generator.fireDefineStrength(); // Strength of the fire
                DensityType density = generator.trafficDefineDensity(); // Traffic density around the fire
                int initialSlight = generator.initialSlightVictims(); //Number of slight victims
                int initialSerious = generator.initialSeriousVictims(); //Number of serious victims
                int initialTrapped = generator.initialTrappedVictims(); //Number of trapped victims
                int initialDead = generator.initialDeadVictims(); //Number of deads

                //List to insert in the Disaster instance
                List<Person> slight = new ArrayList<Person>();
                List<Person> serious = new ArrayList<Person>();
                List<Person> trapped = new ArrayList<Person>();
                List<Person> dead = new ArrayList<Person>();

                //Random latitude and longitude (in Madrid!)
                double latitude = generator.randomLatitude();
                double longitude = generator.randomLongitude();

                //Saves the disaster in the DB and gets its id
                int idDisaster = EventsManagement.insertFire(latitude, longitude,
                        "Simulated Fire", size, density);

                //Saves the people in the DB
                if (initialSlight > 0) {
                    int id = EventsManagement.insertResourcesOrVictims("slight", "Simulated slight",
                            initialSlight,
                            latitude + parameters.SLIGHT_LATITUDE_DIFFERENCE,
                            longitude + parameters.SLIGHT_LONGITUDE_DIFFERENCE,
                            idDisaster);
                    for (int i = 0; i < initialSlight; i++) {
                        //All the Person's of the same type will have the same id
                        slight.add(VictimManager.generateSlightVictim(id, parameters));
                    }
                }
                if (initialSerious > 0) {
                    int id = EventsManagement.insertResourcesOrVictims("serious", "Simulated serious",
                            initialSerious,
                            latitude + parameters.SERIOUS_LATITUDE_DIFFERENCE,
                            longitude + parameters.SERIOUS_LONGITUDE_DIFFERENCE,
                            idDisaster);
                    for (int i = 0; i < initialSerious; i++) {
                        serious.add(VictimManager.generateSeriousVictim(id, parameters));
                    }
                }
                if (initialTrapped > 0) {
                    int id = EventsManagement.insertResourcesOrVictims("trapped", "Simulated trapped",
                            initialTrapped,
                            latitude + parameters.TRAPPED_LATITUDE_DIFFERENCE,
                            longitude + parameters.TRAPPED_LONGITUDE_DIFFERENCE,
                            idDisaster);
                    for (int i = 0; i < initialTrapped; i++) {
                        trapped.add(VictimManager.generateDefaultTrapped(id));
                    }
                }
                if (initialDead > 0) {
                    int id = EventsManagement.insertResourcesOrVictims("dead", "Simulated dead",
                            initialDead,
                            latitude + parameters.DEAD_LATITUDE_DIFFERENCE,
                            longitude + parameters.DEAD_LONGITUDE_DIFFERENCE,
                            idDisaster);
                    for (int i = 0; i < initialDead; i++) {
                        dead.add(VictimManager.generateDefaultDead(id));
                    }
                }

                //Generates the Disaster object
                Disaster newDisaster = new Disaster(idDisaster, DisasterType.FIRE, 
                        "First fire", "INFO", "DESCRIPTION", "ADDRESS", 0, 0,
                        StateType.ACTIVE, size, density, slight, serious, dead,
                        trapped, 0, 0, 0, strength);

                //Saves it in the disasters list of the simulator
                disasters.add(newDisaster);

                //Generates the next generation fire event
                queue.insert(Event.generateNewFire(currentEvent.getInstant() + generator.fireGeneratePeriod()));
            } //Refresh fires and victims
            else if (currentEvent.isRefresh()) {

                for (Disaster currentDisaster : disasters) {

                    //Code that refreshes the fire
                    if (currentDisaster.isActive()) {
                        if (currentDisaster.isEnoughFiremen()) {
                            currentDisaster.setState(StateType.CONTROLLED);
                            //TODO: I don't know if it's changed this way
                            //EventsManagement.modify(currentDisaster.getId(), "state", "controlled");
                        } else {
                            generator.increaseRandomStrength(currentDisaster);
                        }
                    } else if (currentDisaster.isControlled()) {
                        if (currentDisaster.getStrength() > 0) {
                            generator.reduceRandomStrength(currentDisaster);
                        }
                        if (currentDisaster.getStrength() < 0) {
                            currentDisaster.setStrength(0);
                        }
                        if ((currentDisaster.getStrength() == 0)
                                && (currentDisaster.getTrappedNum() == 0)
                                && (currentDisaster.getSeriousNum() == 0)
                                && (currentDisaster.getSlightNum() == 0)) {
                            currentDisaster.setState(StateType.ERASED);
                            EventsManagement.delete(currentDisaster.getId());
                        }
                    }

                    /* Refreshes the victims if its active or controlled (not erased)
                     * and the number of ambulances is not enough */
                    if (!currentDisaster.isErased()) {
                        //Save the previous amount of each kind of victim to update then
                        int slightBefore = currentDisaster.getSlightNum();
                        int seriousBefore = currentDisaster.getSeriousNum();
                        int trappedBefore = currentDisaster.getTrappedNum();
                        int deadBefore = currentDisaster.getDeadNum();

                        //Save the previous marker id's
                        int slightBeforeId = -1;
                        int seriousBeforeId = -1;
                        int trappedBeforeId = -1;
                        int deadBeforeId = -1;
                        if(slightBefore > 0) {
                            slightBeforeId = currentDisaster.getSlight().get(0).getId();
                        }
                        if(seriousBefore > 0) {
                            seriousBeforeId = currentDisaster.getSerious().get(0).getId();
                        }
                        if(trappedBefore > 0) {
                            trappedBeforeId = currentDisaster.getTrapped().get(0).getId();
                        }
                        if(deadBefore > 0) {
                            deadBeforeId = currentDisaster.getDead().get(0).getId();
                        }

                        //Tries trapped to victim
                        for (Person trapped : currentDisaster.getTrapped()) {
                            if (generator.doesTrappedPassToVictim (currentDisaster.getStrength())) {
                                trapped.setInjuryDegree(InjuryDegree.SLIGHT);
                                currentDisaster.getTrapped().remove(trapped);
                                currentDisaster.addSlight(trapped);
                                if(slightBefore > 0) {
                                    trapped.setId(slightBeforeId);
                                }
                            }
                        }

                        //Refreshes the serious
                        if (currentDisaster.isEnoughAmbulances()) {
                            for (Person serious : currentDisaster.getSerious()) {
                                int decrease = generator.healthPointsDecrease(currentDisaster.getStrength());
                                serious.reduceHealthPoints(decrease);
                                //If he/she dies
                                if (serious.getInjuryDegree() == InjuryDegree.DEAD) {
                                    currentDisaster.getSlight().remove(serious);
                                    currentDisaster.addDead(serious);
                                    if(deadBefore > 0) {
                                        serious.setId(deadBeforeId);
                                    }
                                }
                            }

                            for (Person slight : currentDisaster.getSlight()) {
                                int decrease = generator.healthPointsDecrease(currentDisaster.getStrength());
                                slight.reduceHealthPoints(decrease);
                                //If he/she gets serious
                                if (slight.getInjuryDegree() == InjuryDegree.SERIOUS) {
                                    currentDisaster.getSlight().remove(slight);
                                    currentDisaster.addSerious(slight);
                                    if(seriousBefore > 0) {
                                        slight.setId(deadBeforeId);
                                    }
                                }
                                //If he/she dies
                                if (slight.getInjuryDegree() == InjuryDegree.DEAD) {
                                    currentDisaster.getSlight().remove(slight);
                                    currentDisaster.addDead(slight);
                                    if(deadBefore > 0) {
                                        slight.setId(deadBeforeId);
                                    }
                                }
                            }
                        }

                        //Update the markers

                        //SLIGHT
                        //There weren't slight victims and now there are
                        if(currentDisaster.getSlightNum() > 0 &&  slightBefore == 0) {
                            EventsManagement.insertResourcesOrVictims("slight", "NAMES",
                                    currentDisaster.getSlightNum(),
                                    currentDisaster.getLatitude() + parameters.SLIGHT_LATITUDE_DIFFERENCE,
                                    currentDisaster.getLatitude() + parameters.SLIGHT_LONGITUDE_DIFFERENCE,
                                    currentDisaster.getId());
                        }
                        //There were slight victims and now there aren't
                        if(currentDisaster.getSlightNum() == 0 &&  slightBefore > 0) {
                            EventsManagement.delete(slightBeforeId);
                        }
                        //The number of slight victims has changed
                        if(currentDisaster.getSlightNum() !=  slightBefore && slightBefore > 0) {
                            EventsManagement.modify(slightBeforeId, "quantity",
                                    "" + currentDisaster.getSlightNum());
                        }

                        //SERIOUS
                        //There weren't serious victims and now there are
                        if(currentDisaster.getSeriousNum() > 0 &&  seriousBefore == 0) {
                            EventsManagement.insertResourcesOrVictims("serious", "NAMES",
                                    currentDisaster.getSeriousNum(),
                                    currentDisaster.getLatitude() + parameters.SERIOUS_LATITUDE_DIFFERENCE,
                                    currentDisaster.getLatitude() + parameters.SERIOUS_LONGITUDE_DIFFERENCE,
                                    currentDisaster.getId());
                        }
                        //There were serious victims and now there aren't
                        if(currentDisaster.getSeriousNum() == 0 &&  seriousBefore > 0) {
                            EventsManagement.delete(seriousBeforeId);
                        }
                        //The number of serious victims has changed
                        if(currentDisaster.getSeriousNum() !=  seriousBefore && seriousBefore > 0) {
                            EventsManagement.modify(seriousBeforeId, "quantity",
                                    "" + currentDisaster.getSeriousNum());
                        }


                        //TRAPPED
                        //Trapped victims can't be generated before the creation of the fire
                        //There were trapped victims and now there aren't
                        if(currentDisaster.getTrappedNum() == 0 &&  trappedBefore > 0) {
                            EventsManagement.delete(trappedBeforeId);
                        }
                        //The number of trapped victims has changed
                        if(currentDisaster.getTrappedNum() !=  trappedBefore && trappedBefore > 0) {
                            EventsManagement.modify(trappedBeforeId, "quantity",
                                    "" + currentDisaster.getTrappedNum());
                        }

                        //DEAD
                        //There weren't dead victims and now there are
                        if(currentDisaster.getDeadNum() > 0 &&  deadBefore == 0) {
                            EventsManagement.insertResourcesOrVictims("dead", "NAMES",
                                    currentDisaster.getDeadNum(),
                                    currentDisaster.getLatitude() + parameters.DEAD_LATITUDE_DIFFERENCE,
                                    currentDisaster.getLatitude() + parameters.DEAD_LONGITUDE_DIFFERENCE,
                                    currentDisaster.getId());
                        }
                        //Dead cannot change to another Degree (unfortunately ^^)
                        //The number of dead victims has changed
                        if(currentDisaster.getDeadNum() !=  deadBefore && deadBefore > 0) {
                            EventsManagement.modify(deadBeforeId, "quantity",
                                    "" + currentDisaster.getDeadNum());
                        }
                    }
                }

                //Every refresh generates the next refresh event
                queue.insert(Event.generateRefresh(currentEvent, generator.refreshPeriod()));
            }       
        }
        LOGGER.info(currentEvent.toString());
        double lastEventInstant = currentEvent.getInstant();
        currentEvent = queue.extract();
        //Sleeps the thread if the simulation is in real time
        if(parameters.REAL_TIME) {
            try {
                Thread.sleep((long) (currentEvent.getInstant() - lastEventInstant) * 1000);
            }
            catch (InterruptedException e) {
                System.err.println("Error interrupting the Thread: " + e.getMessage());
            }
        }
    }

    /**
     * Runs the simulation
     */
    public void runSimulation() {
        //Inicialization
        Simulator sim = new Simulator();
        LOGGER.info("Simulation beginning. Length = " + sim.generator.params.LENGTH);

         //Simulation loop running
        sim.simulateLoop(sim.generator.params.LENGTH);
        LOGGER.info("End of simulation");
    }

    /**
     * Pauses the simulation
     */
    public void pauseSimulation() {
        this.pause = true;
    }

    /**
     * Prepares and launches the simulation
     */
    public static void main() throws IOException {
        //Inicialization
        Simulator sim = new Simulator();
        LOGGER.info("Simulation beginning. Length = " + sim.generator.params.LENGTH);

         //Simulation loop running
        sim.simulateLoop(sim.generator.params.LENGTH);
        LOGGER.info("End of simulation");
    }
}

