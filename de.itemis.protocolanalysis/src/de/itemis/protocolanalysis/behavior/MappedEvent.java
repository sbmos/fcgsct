package de.itemis.protocolanalysis.behavior;

import org.franca.core.franca.FEventOnIf;
import org.yakindu.sct.model.stext.stext.EventDefinition;
import org.yakindu.sct.model.stext.stext.InterfaceScope;
import org.yakindu.sct.model.stext.stext.OperationDefinition;
import org.yakindu.sct.model.stext.stext.VariableDefinition;

/**
 * Maps Franca constructs on SCT interface concepts. 
 * 
 * 
 * @author Sören Braunstein
 *
 */
public class MappedEvent implements Comparable<MappedEvent>{

	/*
	 * Contains a FModelElement which can be:
	 * - FBroadcast
	 * - FAttribute
	 * - FMethod
	 * 
	 */
	private FEventOnIf fEventOnIf;
	
	private EventDefinition eventDefinition;
//	private VariableDefinition variableDefinition;
//	private OperationDefinition operationDefinition;
	
	@Override
	public int compareTo(MappedEvent o) {

		InterfaceScope interfaceScope = (InterfaceScope) eventDefinition.eContainer();
//		interfaceScope.
		
		return 0;
	}

}
