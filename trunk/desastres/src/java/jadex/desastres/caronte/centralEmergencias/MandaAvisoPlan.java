package jadex.desastres.caronte.centralEmergencias;

import jadex.bdi.runtime.*;
import jadex.desastres.*;

/**
 * Plan de la central para avisar al resto de los agentes.
 *
 * @author Juan Luis Molina
 *
 */
public class MandaAvisoPlan extends EnviarMensajePlan {

	/**
	 * Cuerpo del plan.
	 */
	public void body() {
		// Obtenemos un objeto de la clase entorno para poder usar sus metodos.
		Environment env = (Environment) getBeliefbase().getBelief("env").getFact();

		String recibido = enviarRespuesta("ack_aviso_geriatrico", "Aviso recibido");
		env.printout("CC central: Ack mandado",0);
		
		String recibido2[] = recibido.split("-");
		Integer idDes = new Integer(recibido2[0]);
		String emergencia = recibido2[1];
		String herido = recibido2[2];
		Disaster des = env.getEvent(idDes);

		getBeliefbase().getBelief("idEmergencia").setFact(idDes);

		env.printout("CC central: Avisando a agentes... (en espera)...", 0);

		String resultado1 = enviarMensaje("ambulanceCaronte", "aviso", idDes.toString()+"-"+herido);
		env.printout("CC central: Respuesta recibida de la ambulancia: " + resultado1, 0);

		if (des.getSize().equals("small") == false) {
			String resultado2 = enviarMensaje("policeCaronte", "aviso", idDes.toString());
			env.printout("CC central: Respuesta recibida de la policia: " + resultado2, 0);
			String resultado3 = enviarMensaje("firemenCaronte", "aviso", idDes.toString()+"-"+emergencia);
			env.printout("CC central: Respuesta recibida del bombero: " + resultado3, 0);
		}

		env.printout("CC central: Agentes avisados!!", 0);

		IGoal esperaSolucion = createGoal("esperaSolucion");
		dispatchSubgoalAndWait(esperaSolucion);
	}
}