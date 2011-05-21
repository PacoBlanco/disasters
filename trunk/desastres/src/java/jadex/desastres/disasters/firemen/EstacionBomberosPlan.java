package jadex.desastres.disasters.firemen;

import jadex.bdi.runtime.*;
import jadex.desastres.Environment;
import jadex.desastres.Position;
import jadex.desastres.WorldObject;

/**
 * Plan de BOMBEROS para llevar al bombero a la estacion de bomberos.
 * 
 * @author Ivan Rojo y Juan Luis Molina
 * 
 */
public class EstacionBomberosPlan extends Plan {

	/**
	 * Cuerpo del plan.
	 */
	public void body() {
		// Obtenemos un objeto de la clase entorno para poder usar sus metodos
		Environment env = (Environment) getBeliefbase().getBelief("env").getFact();

		// Posicion actual del bombero
		//Position pos = (Position) getBeliefbase().getBelief("pos").getFact();
		WorldObject agente = (WorldObject)getBeliefbase().getBelief("agente").getFact();
		Position pos = agente.getPosition();

		// Identifica la posicion del desastre
		Position destino = null;

		waitFor(1000);
	}
}