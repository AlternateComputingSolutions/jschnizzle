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
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alternatecomputing.jschnizzle.event.Dispatcher;
import com.alternatecomputing.jschnizzle.event.EventType;
import com.alternatecomputing.jschnizzle.event.JSEvent;
import com.alternatecomputing.jschnizzle.model.Diagram;

/**
 * Action class to export a diagram image to a file
 */
public class ExportImageAction extends AbstractAction {
	private static final long serialVersionUID = 5869985593277327191L;
	private static final Logger LOGGER = LoggerFactory.getLogger(ExportImageAction.class);
	private Component parent;
	private Diagram diagram;

	/**
	 * constructor
	 *
	 * @param parent
	 *            component on which to center the dialog
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
				if (file.getName().toLowerCase().endsWith(".jpg") || file.getName().toLowerCase().endsWith(".svg") || file.isDirectory()) {
					return true;
				} else {
					return false;
				}
			}

			@Override
			public String getDescription() {
				return "JPEG and SVG Images";
			}
		});
		int returnValue = chooser.showSaveDialog(parent);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			FileOutputStream fos = null;
			try {
				Dispatcher.dispatchEvent(new JSEvent(EventType.ProgressStarted, null, null));
				if (!file.getName().toLowerCase().endsWith(".jpg") && !file.getName().toLowerCase().endsWith(".svg")) {
					file = new File(file.getCanonicalPath() + ".jpg");
				}
				fos = new FileOutputStream(file);
				if (file.getName().toLowerCase().endsWith(".jpg")) {
					exportAsJPEG(fos);
				} else {
					exportAsSVG(fos);
				}
				fos.flush();
				LOGGER.info("Diagram '" + diagram.getName() + "' image successfully saved to file " + file.getCanonicalPath() + "'.");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (TranscoderException e) {
				e.printStackTrace();
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						// ignored
					}
				}
				Dispatcher.dispatchEvent(new JSEvent(EventType.ProgressCompleted, null, null));
			}
		}
	}

	private void exportAsJPEG(OutputStream outputStream) throws TranscoderException {
		JPEGTranscoder t = new JPEGTranscoder();
		t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(.8));
		InputStream in = new ByteArrayInputStream(diagram.getEncodedImage().getBytes());
		TranscoderInput input = new TranscoderInput(in);
		TranscoderOutput output = new TranscoderOutput(outputStream);
		t.transcode(input, output);
	}

	private void exportAsSVG(OutputStream outputStream) throws IOException {
		outputStream.write(diagram.getEncodedImage().getBytes());
	}

	/**
	 * set the diagram on which the action will perform
	 *
	 * @param diagram
	 *            diagram
	 */
	public void setDiagram(Diagram diagram) {
		this.diagram = diagram;
	}

}