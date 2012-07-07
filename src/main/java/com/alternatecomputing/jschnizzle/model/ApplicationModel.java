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
package com.alternatecomputing.jschnizzle.model;

import javax.swing.DefaultListModel;

public class ApplicationModel {
	private static ApplicationModel model = new ApplicationModel();
	private String fileName;
	private DefaultListModel activityScriptsModel;
	private DefaultListModel classScriptsModel;
	private DefaultListModel useCaseScriptsModel;
	private boolean isDirty;
	private Diagram selectedDiagram;

	public static ApplicationModel getInstance() {
		return model;
	}

	public ApplicationModel() {
		activityScriptsModel = new DefaultListModel();
		classScriptsModel = new DefaultListModel();
		useCaseScriptsModel = new DefaultListModel();
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public DefaultListModel getActivityScriptsModel() {
		return activityScriptsModel;
	}

	public DefaultListModel getClassScriptsModel() {
		return classScriptsModel;
	}

	public DefaultListModel getUseCaseScriptsModel() {
		return useCaseScriptsModel;
	}

	public void deleteDiagram(Diagram diagram) {
		switch (diagram.getType()) {
			case Activity:
				activityScriptsModel.removeElement(diagram);
				isDirty = true;
				break;
			case Class:
				classScriptsModel.removeElement(diagram);
				isDirty = true;
				break;
			case UseCase:
				useCaseScriptsModel.removeElement(diagram);
				isDirty = true;
				break;
		}
	}

	public void deleteAllDiagrams() {
		activityScriptsModel.clear();
		classScriptsModel.clear();
		useCaseScriptsModel.clear();
		isDirty = false;
	}

	public void addDiagram(Diagram diagram) {
		switch (diagram.getType()) {
			case Activity:
				activityScriptsModel.addElement(diagram);
				isDirty = true;
				break;
			case Class:
				classScriptsModel.addElement(diagram);
				isDirty = true;
				break;
			case UseCase:
				useCaseScriptsModel.addElement(diagram);
				isDirty = true;
				break;
		}
	}

	public boolean isModelDirty() {
		return isDirty;
	}

	public void markModelClean() {
		isDirty = false;
	}

	public void markModelDirty() {
		isDirty = true;
	}

	public Diagram getSelectedDiagram() {
		return selectedDiagram;
	}

	public void setSelectedDiagram(Diagram selectedDiagram) {
		this.selectedDiagram = selectedDiagram;
	}

}
