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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import com.alternatecomputing.jschnizzle.model.ApplicationModel;
import com.alternatecomputing.jschnizzle.util.UIUtils;

/**
 * Action class to exit the application
 */
public class ExitAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private Component parent;
	private ApplicationModel applicationModel;
	private ActionListener saveAction;

	/**
	 * constructor
	 * 
	 * @param applicationModel application model
	 * @param parent component on which to center the dialog
	 * @param saveAction action class for saving the model prior to exiting if needed
	 */
	public ExitAction(ApplicationModel applicationModel, Component parent, ActionListener saveAction) {
		super("Exit", null);
		putValue(SHORT_DESCRIPTION, "Exit JSchnizzle");
		this.applicationModel = applicationModel;
		this.parent = parent;
		this.saveAction = saveAction;
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (applicationModel.isModelDirty()) {
			int n = JOptionPane.showConfirmDialog(parent, "There are unsaved diagram definition changes.\nWould you like to save these first?", "Save changes?", JOptionPane.YES_NO_CANCEL_OPTION);
			if (n == JOptionPane.YES_OPTION) {
				saveAction.actionPerformed(null);
			} else if (n == JOptionPane.CANCEL_OPTION) {
				return;
			}
		}
		int n = JOptionPane.showConfirmDialog(parent, "Are you sure you would like to exit?", "Exit?", JOptionPane.YES_NO_OPTION);
		if (n == JOptionPane.YES_OPTION) {
			UIUtils.logMessage("JSchnizzle shut down.");
			System.exit(0);
		}
	}

}