package disasters.caronte.intervencionincendios;

import disasters.caronte.*;
import disasters.caronte.ontology.Incendio;
import jadex.bdi.runtime.IGoal;

/**
 * Plan de recomendaci&oacute;n para evacuar un incendio.
 * 
 * @author Juan Luis Molina Nogales
 */
public class EvaluarIncendioPlan extends CarontePlan{
	/**
	 * Cuerpo del plan EvaluarIncendio.
	 */
	public void body(){
		System.out.println("Evaluando incendio");
		Incendio inc = (Incendio) enviarRespuestaObjeto("ack_atender_incendio", "OK");
		getBeliefbase().getBelief("incendio_actual").setFact(inc.getId());
		getBeliefbase().getBelief("numero_epi").setFact(1);
		
		/***********************************************************************
		 * POR COMPLETAR
		 **********************************************************************/
		
		IGoal apagarIncendio = createGoal("apagar_incendio");
		dispatchSubgoalAndWait(apagarIncendio);
	}
}
