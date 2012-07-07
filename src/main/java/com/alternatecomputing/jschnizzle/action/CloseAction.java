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

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alternatecomputing.jschnizzle.event.Dispatcher;
import com.alternatecomputing.jschnizzle.event.EventType;
import com.alternatecomputing.jschnizzle.event.JSEvent;
import com.alternatecomputing.jschnizzle.model.ApplicationModel;

/**
 * Action class to close a configuration file
 */
public class CloseAction extends AbstractFileAction {
	private static final long serialVersionUID = -7154598817626516893L;
	private static final Logger LOGGER = LoggerFactory.getLogger(CloseAction.class);
	private Component parent;
	private ApplicationModel applicationModel;
	private ActionListener saveAction;

	/**
	 * constructor
	 *
	 * @param applicationModel application model
	 * @param parent component on which to center the dialog
	 * @param saveAction action to save changes if needed prior to loading a new configuration
	 */
	public CloseAction(ApplicationModel applicationModel, Component parent, ActionListener saveAction) {
		super("Close", null);
		putValue(SHORT_DESCRIPTION, "Close diagram definitions");
		this.applicationModel = applicationModel;
		this.parent = parent;
		this.saveAction = saveAction;
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent actionEvent) {
		if (applicationModel.isModelDirty()) {
			int n = JOptionPane.showConfirmDialog(parent, "There are unsaved diagram definition changes.\nWould you like to save these first?", "Save changes?", JOptionPane.YES_NO_CANCEL_OPTION);
			if (n == JOptionPane.YES_OPTION) {
				saveAction.actionPerformed(null);
			} else if (n == JOptionPane.CANCEL_OPTION) {
				return;
			}
		}
		try {
			Dispatcher.dispatchEvent(new JSEvent(EventType.ProgressStarted, null, null));
			Dispatcher.dispatchEvent(new JSEvent(EventType.DiagramDeleteAll, this, null));
			LOGGER.info("File '" + applicationModel.getFileName() + "' closed.");
			Dispatcher.dispatchEvent(new JSEvent(EventType.FileNameChanged, this, null));
		} finally {
			Dispatcher.dispatchEvent(new JSEvent(EventType.ProgressCompleted, null, null));
		}
	}

}