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
import java.beans.XMLDecoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.alternatecomputing.jschnizzle.event.Dispatcher;
import com.alternatecomputing.jschnizzle.event.EventType;
import com.alternatecomputing.jschnizzle.event.JSEvent;
import com.alternatecomputing.jschnizzle.model.ApplicationModel;
import com.alternatecomputing.jschnizzle.model.Diagram;
import com.alternatecomputing.jschnizzle.util.UIUtils;

/**
 * Action class to load a configuration file
 */
public class LoadAction extends AbstractFileAction {
	private static final long serialVersionUID = 1L;
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
	public LoadAction(ApplicationModel applicationModel, Component parent, ActionListener saveAction) {
		super("Load", null);
		putValue(SHORT_DESCRIPTION, "Load diagram definitions");
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
		JFileChooser chooser = createFileChooser();
		String fileName = applicationModel.getFileName();
		if (fileName != null) {
			File directory = new File(fileName.substring(0, fileName.lastIndexOf(File.separatorChar)));
			chooser.setCurrentDirectory(directory);
		}
		int returnValue = chooser.showOpenDialog(parent);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			try {
				Dispatcher.dispatchEvent(new JSEvent(EventType.ProgressStarted, null, null));
				XMLDecoder decoder = new XMLDecoder(new FileInputStream(file));
				List<Diagram> diagrams = (List<Diagram>) decoder.readObject();
				decoder.close();
				Dispatcher.dispatchEvent(new JSEvent(EventType.DiagramDeleteAll, this, null));
				for (Iterator<Diagram> iterator = diagrams.iterator(); iterator.hasNext();) {
					Diagram diagram = (Diagram) iterator.next();
					Dispatcher.dispatchEvent(new JSEvent(EventType.DiagramAdded, this, diagram));
				}
				fileName = file.getCanonicalPath();
				UIUtils.logMessage("File '" + fileName + "' loaded successfully.");
				Dispatcher.dispatchEvent(new JSEvent(EventType.SelectDiagram, this, diagrams.iterator().next()));
				Dispatcher.dispatchEvent(new JSEvent(EventType.FileNameChanged, this, fileName));
			} catch (FileNotFoundException e) {
				UIUtils.logException(e);
			} catch (IOException e) {
				UIUtils.logException(e);
			} finally {
				Dispatcher.dispatchEvent(new JSEvent(EventType.ProgressCompleted, null, null));
			}
		}
	}

}