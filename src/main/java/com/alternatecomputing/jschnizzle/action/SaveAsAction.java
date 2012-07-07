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
import java.io.File;

import javax.swing.JFileChooser;

import com.alternatecomputing.jschnizzle.model.ApplicationModel;

/**
 * Action class to save a configuration file
 */
public class SaveAsAction extends SaveAction {
	private static final long serialVersionUID = 1L;
	private Component parent;

	/**
	 * constructor
	 * 
	 * @param applicationModel application model
	 * @param parent component on which to center the dialog
	 */
	public SaveAsAction(ApplicationModel applicationModel, Component parent) {
		super(applicationModel, "Save As", "Save diagram definitions under a different name");
		this.parent = parent;
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent actionEvent) {
		JFileChooser chooser = createFileChooser();
		String fileName = getFileName();
		if (fileName != null) {
			File directory = new File(fileName.substring(0, fileName.lastIndexOf(File.separatorChar)));
			chooser.setCurrentDirectory(directory);
		}
		int returnValue = chooser.showSaveDialog(parent);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			saveToFile(file);
		}
	}

}