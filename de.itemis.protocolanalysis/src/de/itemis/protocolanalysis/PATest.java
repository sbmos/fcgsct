package de.itemis.protocolanalysis;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.common.util.BasicEList;
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
import org.yakindu.sct.model.sgraph.Entry;
import org.yakindu.sct.model.sgraph.Region;
import org.yakindu.sct.model.sgraph.Statechart;
import org.yakindu.sct.model.sgraph.Transition;
import org.yakindu.sct.model.sgraph.Vertex;

import com.google.inject.Injector;

public class PATest {

	ComponentModel model;
	
	@Before
	/**
	 * Basic setup. Loads models from example files into one common resource set.
	 * CoMo is used as glue and link to Franca and SCT
	 */
	public void setupModels() {
//		new org.eclipse.emf.mwe.utils.StandaloneSetup().setPlatformUri("../");
		Injector injector = new ComoStandaloneSetup().createInjectorAndDoEMFRegistration();
		XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
		resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
		Resource resource = resourceSet.getResource(
			    URI.createURI("file://Users/braunstein/git/fcgsct/de.itemis.protocolanalysis.example/Test.ycm"), true);
		Resource resource2 = resourceSet.getResource(
			    URI.createURI("file://Users/braunstein/git/fcgsct/de.itemis.protocolanalysis.example/robotarm.fidl"), true);
		Resource resource3 = resourceSet.getResource(
			    URI.createURI("file://Users/braunstein/git/fcgsct/de.itemis.protocolanalysis.example/RobotArm.sct"), true);
		model = (ComponentModel) resource.getContents().get(0);
		
		assertNotNull("Component model couldn't be loaded and is null", model);
	}
	
	@Test
	/**
	 * Test if the component model is loaded and if proxies for Franca and SCT are resolved correctly,
	 * meaning if they are accessible through CoMo
	 */
	public void modelsLoaded() {
		
		EList<Definition> definitions = model.getPackage().getDefinitions();
		
		assertTrue("No defintions found", !definitions.isEmpty());
		
		Statechart aStatechart = null;
		FInterface aFInterface = null;
		
//		for (Definition definition : definitions) {
		Definition definition = definitions.get(0);
		if (definition instanceof Component) {
			Component component = (Component) definition;
			ComponentMapping componentMapping = component.getComponentMapping();
			if (componentMapping instanceof ExternalModelMapping) {
				ExternalModelMapping externalModelMapping = (ExternalModelMapping) componentMapping;
				RefValue value = externalModelMapping.getValue();
				if (value.getValue() instanceof Statechart) {
					aStatechart = (Statechart) value.getValue();
//					System.out.println(EmfFormatter.objToStr(aStatechart));
				}

			}

			for (Port port : component.getPorts()) {
				if (port.getPortMapping() instanceof InterfaceMapping) {
					InterfaceMapping interfaceMapping = (InterfaceMapping) port
							.getPortMapping();
					aFInterface = interfaceMapping.getInterface();
					break;
					// System.out.println("### " + port.getName() + ": " +
					// fInterface.getName());
					// System.out.println(EmfFormatter.objToStr(fInterface));
				}
			}
		}
		
		assertNotNull("Couldn't find statechart model", aStatechart);
		assertNotNull("Couldn't find Franca model", aFInterface);
		
		assertEquals("Couldn't resolve statechart model", "RobotArm", aStatechart.getName());
		assertEquals("Couldn't resolve Franca model", "RobotArm", aFInterface.getName());
	}
	
	@Test
	public void traverseAllStates() {
		Statechart statechart = getFirstStatechart();
		Region mainRegion = statechart.getRegions().get(0);
		assertEquals("main region", mainRegion.getName());
		Entry entry = null;
		for (Vertex vertex : mainRegion.getVertices()) {
			if (vertex instanceof Entry) {
				entry = (Entry) vertex;
				break;
			}
		}
		assertNotNull(entry);
		Set<Vertex> trace = new HashSet<Vertex>();
		traverse(entry.getOutgoingTransitions(), trace);
	}
	
	private void traverse(EList<Transition> transitions, Set<Vertex> trace) {
		for (Transition transition : transitions) {
			
			Vertex source = transition.getSource();
			Vertex target = transition.getTarget();
			
			if (!trace.contains(source)) {
				System.out.println(source.getName() + " -- [" + transition.getSpecification() + "] --> " + target.getName());
				Set<Vertex> fork = new HashSet<>(trace);
				fork.add(source);
				traverse(target.getOutgoingTransitions(), trace);
			}
		}
	}

	private Statechart getFirstStatechart() {
		Component component = (Component) model.getPackage().getDefinitions().get(0);
		ExternalModelMapping componentMapping = (ExternalModelMapping) component.getComponentMapping();
		Statechart statechart = (Statechart) componentMapping.getValue().getValue();
		assertNotNull(statechart);
		return statechart;
	}
	
}
