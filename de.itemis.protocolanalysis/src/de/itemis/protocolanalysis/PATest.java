package de.itemis.protocolanalysis;

import static org.junit.Assert.*;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.util.EmfFormatter;
import org.franca.core.franca.FInterface;
import org.junit.Before;
import org.junit.Test;
import org.yakindu.component.model.ComoStandaloneSetup;
import org.yakindu.component.model.como.Component;
import org.yakindu.component.model.como.ComponentMapping;
import org.yakindu.component.model.como.ComponentModel;
import org.yakindu.component.model.como.Definition;
import org.yakindu.component.model.como.ExternalModelMapping;
import org.yakindu.component.model.como.InterfaceMapping;
import org.yakindu.component.model.como.Port;
import org.yakindu.component.model.como.RefValue;
import org.yakindu.sct.model.sgraph.Statechart;

import com.google.inject.Injector;

public class PATest {

	ComponentModel model;
	
	@Before
	public void setup() {
//		new org.eclipse.emf.mwe.utils.StandaloneSetup().setPlatformUri("../");
		Injector injector = new ComoStandaloneSetup().createInjectorAndDoEMFRegistration();
		XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
		resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
		Resource resource = resourceSet.getResource(
			    URI.createURI("file://Users/braunstein/Workspaces/franca_sct_contract_evaluation/de.itemis.protocolanalysis.example/Test.ycm"), true);
		Resource resource2 = resourceSet.getResource(
			    URI.createURI("file://Users/braunstein/Workspaces/franca_sct_contract_evaluation/de.itemis.protocolanalysis.example/robotarm.fidl"), true);
		Resource resource3 = resourceSet.getResource(
			    URI.createURI("file://Users/braunstein/Workspaces/franca_sct_contract_evaluation/de.itemis.protocolanalysis.example/RobotArm.sct"), true);
		model = (ComponentModel) resource.getContents().get(0);
	}
	
	@Test
	public void test() {
		assertNotNull(model);
		
		EList<Definition> definitions = model.getPackage().getDefinitions();
		
		for (Definition definition : definitions) {
			if (definition instanceof Component) {
				Component component = (Component) definition;
				ComponentMapping componentMapping = component.getComponentMapping();
				if (componentMapping instanceof ExternalModelMapping) {
					ExternalModelMapping externalModelMapping = (ExternalModelMapping) componentMapping;
					RefValue value = externalModelMapping.getValue();
					if (value.getValue() instanceof Statechart) {
						Statechart statechart = (Statechart) value.getValue();
						System.out.println(EmfFormatter.objToStr(statechart));
					}
					
				}
				
				for (Port port : component.getPorts()) {
					if (port.getPortMapping() instanceof InterfaceMapping) {
						InterfaceMapping interfaceMapping = (InterfaceMapping) port.getPortMapping();
						FInterface fInterface = interfaceMapping.getInterface();
						System.out.println("### " + port.getName() + ": " + fInterface.getName());
						System.out.println(EmfFormatter.objToStr(fInterface));
					}
				}
			}
		}
	}

}
