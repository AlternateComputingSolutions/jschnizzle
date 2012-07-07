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
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import com.alternatecomputing.jschnizzle.event.Dispatcher;
import com.alternatecomputing.jschnizzle.event.EventType;
import com.alternatecomputing.jschnizzle.event.JSEvent;
import com.alternatecomputing.jschnizzle.model.Diagram;
import com.alternatecomputing.jschnizzle.util.UIUtils;

/**
 * Action class to export a diagram image to a file
 */
public class ExportImageAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private Component parent;
	private Diagram diagram;

	/**
	 * constructor
	 * 
	 * @param parent component on which to center the dialog
	 */
	public ExportImageAction(Component parent) {
		super("Export Image", null);
		this.parent = parent;
		putValue(SHORT_DESCRIPTION, "Export the diagram image to file");
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent actionEvent) {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setFileFilter(new FileFilter() {

			@Override
			public boolean accept(File file) {
				if (file.getName().toLowerCase().endsWith(".jpg") || file.isDirectory()) {
					return true;
				} else {
					return false;
				}
			}

			@Override
			public String getDescription() {
				return "JPEG Images";
			}
		});
		int returnValue = chooser.showSaveDialog(parent);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			try {
				if (!file.getName().toLowerCase().endsWith(".jpg")) {
					file = new File(file.getCanonicalPath() + ".jpg");
				}
				Dispatcher.dispatchEvent(new JSEvent(EventType.ProgressStarted, null, null));
				FileOutputStream fos = new FileOutputStream(file);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ImageIO.write((BufferedImage) diagram.nonBeanImage(), "jpg", bos);
				byte[] bytes = bos.toByteArray();
				fos.write(bytes);
				fos.flush();
				fos.close();
				UIUtils.logMessage("Diagram '" + diagram.getName() + "' image successfully saved to file " + file.getCanonicalPath() + "'.");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				Dispatcher.dispatchEvent(new JSEvent(EventType.ProgressCompleted, null, null));
			}
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