/*
 * Copyright 2010 Alternate Computing Solutions Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alternatecomputing.jschnizzle.action;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import com.alternatecomputing.jschnizzle.event.Dispatcher;
import com.alternatecomputing.jschnizzle.event.EventType;
import com.alternatecomputing.jschnizzle.event.JSEvent;
import com.alternatecomputing.jschnizzle.model.Diagram;
import com.alternatecomputing.jschnizzle.util.UIUtils;

/**
 * Action class to delete a diagram
 */
public class DeleteDiagramAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private Frame owner;
	private Diagram diagram;

	/**
	 * constructor
	 * 
	 * @param owner frame on which the dialog will be centered
	 */
	public DeleteDiagramAction(Frame owner) {
		super("Delete Diagram", null);
		putValue(SHORT_DESCRIPTION, "Delete this diagram");
		this.owner = owner;
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		int n = JOptionPane.showConfirmDialog(owner, "Are you sure you would like to delete the diagram '" + diagram.getName() + "'?", "Delete '" + diagram.getName() + "'?", JOptionPane.YES_NO_OPTION);
		if (n == JOptionPane.YES_OPTION) {
			Dispatcher.dispatchEvent(new JSEvent(EventType.DiagramDeleted, null, diagram));
			UIUtils.logMessage("Diagram '" + diagram.getName() + "' deleted.");
		}
	}

	/**
	 * set the diagram on which the action will perform
	 * 
	 * @param diagram diagram
	 */
	public void setDiagram(Diagram diagram) {
		this.diagram = diagram;
	}

}