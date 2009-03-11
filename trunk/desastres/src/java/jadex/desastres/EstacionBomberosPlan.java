package jadex.desastres;
import java.util.Iterator;
import java.util.Map;

import jadex.runtime.*;

/**
 * Plan de BOMBEROS para llevar al bombero a la estaci�n de bomberos.
 * 
 * @author Iv�n Rojo
 * 
 */
public class EstacionBomberosPlan extends Plan {

	/**
	 * Cuerpo del plan.
	 */
	public void body() {
		// Obtenemos un objeto de la clase entorno para poder usar sus m�todos.
		//System.out.println("** firemen: Hello world!  :D ");
		Environment env = (Environment) getBeliefbase().getBelief("env").getFact();

		// Posicion actual del bombero
		Position pos = (Position) getBeliefbase().getBelief("pos").getFact();

		// Identifica la posici�n del desastre
		Position destino = null;

		
		waitFor(500);
		
	}

}