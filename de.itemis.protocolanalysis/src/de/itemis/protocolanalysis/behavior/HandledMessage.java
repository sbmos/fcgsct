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
import org.eclipse.etrice.core.room.InterfaceItem;
import org.eclipse.etrice.core.room.Message;

public class HandledMessage {
	private InterfaceItem ifitem;
	private Message msg;
	private EObject origin;

	public HandledMessage(InterfaceItem ifitem, Message msg, EObject origin) {
		this.ifitem = ifitem;
		this.msg = msg;
		this.origin = origin;
	}

	public InterfaceItem getIfitem() {
		return ifitem;
	}

	public Message getMsg() {
		return msg;
	}

	public EObject getOrigin() {
		return origin;
	}
}