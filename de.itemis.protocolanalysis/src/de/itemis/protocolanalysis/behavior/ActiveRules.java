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
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.Platform;
import org.eclipse.etrice.core.genmodel.etricegen.ExpandedActorClass;
import org.eclipse.etrice.core.room.GeneralProtocolClass;
import org.eclipse.etrice.core.room.InSemanticsRule;
import org.eclipse.etrice.core.room.ProtocolClass;
import org.eclipse.etrice.core.room.ProtocolSemantics;
import org.eclipse.etrice.core.room.util.RoomHelpers;
import org.franca.core.franca.FTransition; //was SemanticsRule
import org.yakindu.sct.model.stext.stext.InterfaceScope; //was InterfaceItem

import de.itemis.protocolanalysis.Activator;

public class ActiveRules {
	private HashMap<InterfaceScope, List<FTransition>> rules;
	private static boolean traceRules = false;
	private static int traceLevel = 0;
	static {
		if (Activator.getDefault().isDebugging()) {
			String value = Platform
					.getDebugOption("org.eclipse.etrice.abstractexec.behavior/trace/rules");
			if (value != null && value.equalsIgnoreCase(Boolean.toString(true))) {
				traceRules = true;
			}
			value = Platform
					.getDebugOption("org.eclipse.etrice.abstractexec.behavior/trace/rules/level");
			if (value != null) {
				traceLevel = Integer.parseInt(value);
			}
		}
	}

	// private static final int TRACE_RESULT = 1;
	private static final int TRACE_DETAILS = 2;

	public ActiveRules() {
		rules = new HashMap<InterfaceScope, List<FTransition>>();
	}

	private ActiveRules(HashMap<InterfaceScope, List<FTransition>> r) {
		rules = r;
	}

	public Set<InterfaceScope> getPortList() {
		return rules.keySet();
	}

	public List<FTransition> getRulesForPort(InterfaceScope port) {
		return rules.get(port);
	}

	// checks for currently active rules against a message list and modifies the
	// rules which can be merged with the destination node
	public List<HandledMessage> consumeMessages(List<HandledMessage> msgList) {
		List<HandledMessage> wrongMsgList = new ArrayList<HandledMessage>();
		for (HandledMessage msg : msgList) {
			List<FTransition> localRules = rules.get(msg.getIfitem());
			if (localRules != null) {
				FTransition match = null;
				for (FTransition rule : localRules) {
//					rule.getTrigger().getEvent().get
					if (rule.getMsg() == msg.getMsg()) {
						match = rule;
						break;
					}
				}

				if (match != null) {
					if (traceRules && traceLevel >= TRACE_DETAILS)
						System.out.println("  found match for "
								+ msg.getMsg().getName());

					// discard all alternatives
					localRules.clear();

					if (match.getFollowUps().isEmpty()) {
						// all rules of this branch consumed
						// restart at root of semantic rules
						ProtocolSemantics semantics = ((ProtocolClass)msg.getIfitem().getGeneralProtocol()).getSemantics();
						localRules.addAll(semantics.getRules());
					}
					else {
						// and add all follow ups
						localRules.addAll(match.getFollowUps());
					}
					
				} else {
					// TODO: issue a warning?
					wrongMsgList.add(msg);
				}
			}
		}
		return wrongMsgList;
	}

	// merges the rules with the destination active rules
	public boolean merge(ActiveRules ar) {
		boolean added_at_least_one = false;
		for (Entry<InterfaceScope, List<FTransition>> entry : ar.rules
				.entrySet()) {
			for (FTransition rule : entry.getValue()) {
				InterfaceScope ifitem = entry.getKey();
				if (rules.containsKey(ifitem)) {
					if (!rules.get(ifitem).contains(rule)) {
						rules.get(ifitem).add(rule);
						added_at_least_one = true;
					}
				} else {
					List<FTransition> tempList = new ArrayList<FTransition>();
					tempList.add(rule);
					rules.put(ifitem, tempList);
					added_at_least_one = true;
				}
			}
		}

		if (traceRules && traceLevel >= TRACE_DETAILS)
			System.out.println("  merge changed rules");

		return added_at_least_one;
	}

	public ActiveRules createCopy() {
		HashMap<InterfaceScope, List<FTransition>> newRules = new HashMap<InterfaceScope, List<FTransition>>();
		for (InterfaceScope ifitem : rules.keySet()) {
			newRules.put(ifitem,
					new ArrayList<FTransition>(rules.get(ifitem)));
		}
		return new ActiveRules(newRules);
	}

	public void buildInitLocalRules(ExpandedActorClass xpAct) {
		List<InterfaceScope> portList = RoomHelpers.getAllInterfaceItems(xpAct
				.getActorClass());
		for (InterfaceScope ifitem : portList) {
			GeneralProtocolClass gpc = ifitem.getGeneralProtocol();
			if (gpc instanceof ProtocolClass) {
				ProtocolClass pc = (ProtocolClass) gpc;
				if (pc.getSemantics() != null)
					rules.put(ifitem, new ArrayList<FTransition>(pc
							.getSemantics().getRules()));
			}
		}
	}

	public void print() {
		for (InterfaceScope port : rules.keySet()) {
			System.out.println("      Rules for Port " + port.getName() + ":");
			for (FTransition rule : rules.get(port)) {
				printRule(rule, "        ");
			}
		}
	}

	public void printRule(FTransition rule, String indent) {
		if (rule instanceof InSemanticsRule)
			System.out.println(indent + "in: " + rule.getMsg().getName());
		else
			System.out.println(indent + "out: " + rule.getMsg().getName());
		// recursion
		for (FTransition sr : rule.getFollowUps()) {
			printRule(sr, indent + "  ");
		}
	}
}
