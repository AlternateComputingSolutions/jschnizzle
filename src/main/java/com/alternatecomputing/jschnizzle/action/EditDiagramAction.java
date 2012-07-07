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
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXHeader;

import com.alternatecomputing.jschnizzle.model.Diagram;
import com.alternatecomputing.jschnizzle.util.UIUtils;

/**
 * Action class to edit an existing diagram
 */
public class EditDiagramAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private Frame owner;
	private Diagram diagram;

	/**
	 * constructor
	 * 
	 * @param owner frame on which the dialog will be centered
	 */
	public EditDiagramAction(Frame owner) {
		super("Edit Diagram", null);
		putValue(SHORT_DESCRIPTION, "Edit an existing diagram");
		this.owner = owner;
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		JDialog dialog = new JDialog(owner, "Edit " + diagram.getType() + " Diagram", true);
		JXHeader header = new JXHeader("Edit " + diagram.getType() + " Diagram", "Enter updated details for this " + diagram.getType() + " diagram.");
		JPanel panel = UIUtils.createDiagramPanel(header, diagram, dialog, false);
		dialog.add(panel);
		dialog.setSize(600, 400);
		UIUtils.centerComponent(dialog, owner);
		dialog.setVisible(true);
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
