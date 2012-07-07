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

import java.awt.event.ActionEvent;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alternatecomputing.jschnizzle.event.Dispatcher;
import com.alternatecomputing.jschnizzle.event.EventType;
import com.alternatecomputing.jschnizzle.event.JSEvent;
import com.alternatecomputing.jschnizzle.model.ApplicationModel;
import com.alternatecomputing.jschnizzle.model.Diagram;
import com.alternatecomputing.jschnizzle.model.DiagramType;
import com.alternatecomputing.jschnizzle.util.EnumPersistenceDelegate;

/**
 * Action class to save a configuration file
 */
public class SaveAction extends AbstractFileAction {
	private static final long serialVersionUID = 4747699144945828010L;
	private static final Logger LOGGER = LoggerFactory.getLogger(SaveAction.class);
	private final ApplicationModel applicationModel;

	/**
	 * constructor
	 *
	 * @param applicationModel application model
	 */
	public SaveAction(ApplicationModel applicationModel) {
		this(applicationModel, "Save", "Save diagram definitions");
	}

	public SaveAction(ApplicationModel applicationModel, String title, String description) {
		super(title, null);
		this.applicationModel = applicationModel;
		putValue(SHORT_DESCRIPTION, description);
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent actionEvent) {
		String fileName = applicationModel.getFileName();
		if (fileName != null) {
			File file = new File(fileName);
			saveToFile(file);
		}
	}

	protected final void saveToFile(File file) {
		try {
			if (!file.getName().toLowerCase().endsWith(FILE_EXTENSION)) {
				file = new File(file.getCanonicalPath() + FILE_EXTENSION);
			}
			Dispatcher.dispatchEvent(new JSEvent(EventType.ProgressStarted, null, null));
			XMLEncoder encoder = new XMLEncoder(new FileOutputStream(file));
			encoder.setPersistenceDelegate(DiagramType.class, new EnumPersistenceDelegate());
			List<Diagram> diagrams = new LinkedList<Diagram>();
			addActivityDiagrams(diagrams);
			addClassDiagrams(diagrams);
			addUseCaseDiagrams(diagrams);
			addSequenceDiagrams(diagrams);
			encoder.writeObject(diagrams);
			encoder.flush();
			encoder.close();
			LOGGER.info("File '" + file.getCanonicalPath() + "' saved successfully.");
			Dispatcher.dispatchEvent(new JSEvent(EventType.FileNameChanged, this, file.getCanonicalPath()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			Dispatcher.dispatchEvent(new JSEvent(EventType.ProgressCompleted, null, null));
		}
	}

	/**
	 * add use case diagrams to the given model
	 *
	 * @param diagrams collection of diagrams
	 */
	private void addUseCaseDiagrams(Collection<Diagram> diagrams) {
		Enumeration<Diagram> elements = (Enumeration<Diagram>) applicationModel.getUseCaseScriptsModel().elements();
		while (elements.hasMoreElements()) {
			diagrams.add(elements.nextElement());
		}
	}

	/**
	 * add class diagrams to the given model
	 *
	 * @param diagrams collection of diagrams
	 */
	private void addClassDiagrams(Collection<Diagram> diagrams) {
		Enumeration<Diagram> elements = (Enumeration<Diagram>) applicationModel.getClassScriptsModel().elements();
		while (elements.hasMoreElements()) {
			diagrams.add(elements.nextElement());
		}
	}

	/**
	 * add activity diagrams to the given model
	 *
	 * @param diagrams collection of diagrams
	 */
	private void addActivityDiagrams(Collection<Diagram> diagrams) {
		Enumeration<Diagram> elements = (Enumeration<Diagram>) applicationModel.getActivityScriptsModel().elements();
		while (elements.hasMoreElements()) {
			diagrams.add(elements.nextElement());
		}
	}

	/**
	 * add sequence diagrams to the given model
	 *
	 * @param diagrams collection of diagrams
	 */
	private void addSequenceDiagrams(Collection<Diagram> diagrams) {
		Enumeration<Diagram> elements = (Enumeration<Diagram>) applicationModel.getSequenceScriptsModel().elements();
		while (elements.hasMoreElements()) {
			diagrams.add(elements.nextElement());
		}
	}

	protected final String getFileName() {
		return applicationModel.getFileName();
	}
}