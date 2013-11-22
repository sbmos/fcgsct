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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.etrice.core.room.ActorClass;
import org.eclipse.etrice.core.room.DetailCode;
import org.eclipse.etrice.core.room.InterfaceItem;
import org.eclipse.etrice.core.room.Message;
import org.eclipse.etrice.generator.base.DefaultTranslationProvider;
import org.eclipse.etrice.generator.base.DetailCodeTranslator;

/**
 * @author Henrik Rentz-Reichert
 *
 */
public class ActionCodeAnalyzer {

	private class Collector extends DefaultTranslationProvider {
		
		private ArrayList<HandledMessage> mifs = new ArrayList<HandledMessage>();
		
		public ArrayList<HandledMessage> getMifs() {
			return mifs;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.etrice.generator.base.DefaultTranslationProvider#getInterfaceItemMessageText(org.eclipse.etrice.core.room.InterfaceItem, org.eclipse.etrice.core.room.Message, java.util.ArrayList, java.lang.String, java.lang.String)
		 */
		@Override
		public String getInterfaceItemMessageText(InterfaceItem item,
				Message msg, ArrayList<String> args, String index, String orig) {
			
			HandledMessage sm = new HandledMessage(item, msg, origin);
			mifs.add(sm);
			
			return orig;
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.etrice.generator.base.DefaultTranslationProvider#translateMembers()
		 */
		@Override
		public boolean translateMembers() {
			return true;
		}

		// create a new list and leave previous unchanged
		public void begin() {
			mifs = new ArrayList<HandledMessage>();
		}
	}
	
	private Collector collector;
	private DetailCodeTranslator translator;
	private DetailCode origin;

	public ActionCodeAnalyzer(ActorClass ac) {
		collector = new Collector();
		translator = new DetailCodeTranslator(ac, collector);
	}
	
	public List<HandledMessage> analyze(DetailCode dc) {
		origin = dc;
		collector.begin();
		translator.translateDetailCode(dc);
		return collector.getMifs();
	}
}
