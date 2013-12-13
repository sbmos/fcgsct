/*******************************************************************************
 * Copyright (c) 2012 Rohit Agrawal
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * CONTRIBUTORS:
 * 		Rohit Agrawal (initial contribution)
 * 
 *******************************************************************************/


package de.itemis.protocolanalysis.behavior;

import org.eclipse.emf.ecore.EObject;
import org.yakindu.component.model.como.InterfaceElementMapping;
//import org.yakindu.sct.model.stext.stext.EventDefinition; //was Message
import org.yakindu.sct.model.stext.stext.InterfaceScope; //was InterfaceItem

public class HandledMessage {
	private InterfaceScope ifitem;
	private InterfaceElementMapping msg;
	private EObject origin;

	public HandledMessage(InterfaceScope ifitem, InterfaceElementMapping msg, EObject origin) {
		this.ifitem = ifitem;
		this.msg = msg;
		this.origin = origin;
	}

	public InterfaceScope getIfitem() {
		return ifitem;
	}

	public InterfaceElementMapping getMsg() {
		return msg;
	}

	public EObject getOrigin() {
		return origin;
	}
}