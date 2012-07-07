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

import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.alternatecomputing.jschnizzle.model.Diagram;
import com.alternatecomputing.jschnizzle.model.ImageSelection;
import com.alternatecomputing.jschnizzle.util.UIUtils;

/**
 * Action to copy a diagram image to the system clipboard.
 */
public class CopyToClipboardAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private Diagram diagram;

	/**
	 * constructor
	 */
	public CopyToClipboardAction() {
		super("Copy Image to Clipboard", null);
		putValue(SHORT_DESCRIPTION, "Copy the diagram image to the clipboard");
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		ImageSelection imageSelection = new ImageSelection(diagram.nonBeanImage());
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(imageSelection, null);
		UIUtils.logMessage("Diagram '" + diagram.getName() + "' image successfully saved to the clipboard.");
	}

	/**
	 * set the diagram containing the image to be copied to the system clipboard
	 * 
	 * @param diagram diagram
	 */
	public void setDiagram(Diagram diagram) {
		this.diagram = diagram;
	}

}
