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
package com.alternatecomputing.jschnizzle.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.border.DropShadowBorder;

import com.alternatecomputing.jschnizzle.event.Dispatcher;
import com.alternatecomputing.jschnizzle.event.EventType;
import com.alternatecomputing.jschnizzle.event.JSEvent;
import com.alternatecomputing.jschnizzle.model.Diagram;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * General UI utilities and convenience methods
 */
public class UIUtils {

	/**
	 * center the given component on the given parent
	 * 
	 * @param component component to be centered
	 * @param parent component whose position the centering will be based upon
	 */
	public static void centerComponent(Component component, Component parent) {
		Point location = parent.getLocation();
		int x = location.x + (parent.getWidth() / 2) - (component.getWidth() / 2);
		int y = location.y + (parent.getHeight() / 2) - (component.getHeight() / 2);
		component.setLocation(x, y);
	}

	/**
	 * create a diagram panel
	 * 
	 * @param header header for the panel
	 * @param diagram model to be bound to the UI
	 * @param parent component that this panel will be embedded into
	 * @param isNew whether this will be used for a new diagram or to update an existing one
	 * @return diagram panel
	 */
	public static JPanel createDiagramPanel(JXHeader header, final Diagram diagram, final Component parent, final boolean isNew) {
		final String originalScript = diagram.getScript();
		FormLayout layout = new FormLayout("right:max(40dlu;pref), 4dlu, fill:min:grow", "pref, 4dlu, pref, 4dlu, pref, 4dlu, pref, fill:pref:grow, 4dlu, pref");
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		builder.add(header, cc.xywh(1, 1, 3, 1));
		builder.add(new JLabel("Name:"), cc.xy(1, 3));
		final JTextField nameTextField = new JTextField(diagram.getName() == null ? "" : diagram.getName());
		builder.add(nameTextField, cc.xy(3, 3));

		builder.add(new JLabel("Note:"), cc.xy(1, 5));
		final JTextField noteTextField = new JTextField(diagram.getNote() == null ? "" : diagram.getNote());
		builder.add(noteTextField, cc.xy(3, 5));
		builder.add(new JLabel("Script:"), cc.xy(1, 7));
		final JTextArea scriptTextArea = new JTextArea(diagram.getScript() == null ? "" : diagram.getScript());
		builder.add(new JScrollPane(scriptTextArea), cc.xywh(3, 7, 1, 2));
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				diagram.setName(nameTextField.getText());
				diagram.setNote(noteTextField.getText());
				diagram.setScript(scriptTextArea.getText());
				Dispatcher.dispatchEvent(new JSEvent(EventType.ProgressStarted, null, null));
				Thread t = new Thread() {
					public void run() {
						try {
							if (!scriptTextArea.getText().equals(originalScript)) {
								UIUtils.logMessage("Rendering diagram '" + diagram.getName() + "'...");
								ImageUtils.populateImage(diagram);
								UIUtils.logMessage("Diagram '" + diagram.getName() + "' successfully rendered.");
							}
							if (isNew) {
								UIUtils.logMessage("Diagram '" + diagram.getName() + "' created.");
								Dispatcher.dispatchEvent(new JSEvent(EventType.DiagramAdded, this, diagram));
								Dispatcher.dispatchEvent(new JSEvent(EventType.SelectDiagram, this, diagram));
							} else {
								UIUtils.logMessage("Diagram '" + diagram.getName() + "' modified.");
								Dispatcher.dispatchEvent(new JSEvent(EventType.DiagramModified, this, diagram));
							}
						} catch (Exception e) {
							logException(e);
						} finally {
							Dispatcher.dispatchEvent(new JSEvent(EventType.ProgressCompleted, null, null));
						}
					}
				};
				t.start();
				parent.setVisible(false);
			}
		});
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parent.setVisible(false);
			}
		});
		builder.add(ButtonBarFactory.buildOKCancelBar(okButton, cancelButton), cc.xy(3, 10));
		return builder.getPanel();
	}

	/**
	 * create a titled panel with the given name
	 * 
	 * @param name panel name
	 * @return titled panel
	 */
	public static JPanel createTitledPanel(String name) {
		JPanel panel = new JXTitledPanel(name);
		panel.setBorder(new DropShadowBorder(Color.BLACK, 8, 0.5f, 7, false, true, true, true));
		return panel;
	}

	/**
	 * create a titled panel for the given component
	 * 
	 * @param name panel name
	 * @param component component to wrap in the panel
	 * @return titled panel
	 */
	public static JPanel createTitledPanel(String name, Component component) {
		JPanel panel = new JXTitledPanel(name);
		panel.add(component);
		panel.setBorder(new DropShadowBorder(Color.BLACK, 8, 0.5f, 7, false, true, true, true));
		return panel;
	}

	/**
	 * create an undecorated JSplitPane
	 * 
	 * @param orientation split pane orientation
	 * @return undecorated split pane
	 */
	public static JSplitPane createUndecoratedSplitPane(int orientation) {
		JSplitPane pane = new JSplitPane(orientation);
		pane.setOpaque(false);
		BasicSplitPaneUI ui = new BasicSplitPaneUI();
		pane.setUI(ui);
		ui.getDivider().setBorder(null);
		pane.setBorder(BorderFactory.createEmptyBorder());
		return pane;
	}

	/**
	 * create a log event with the given message
	 * 
	 * @param message message
	 */
	public static void logMessage(String message) {
		Dispatcher.dispatchEvent(new JSEvent(EventType.Log, null, message));
	}

	/**
	 * create a log event for the given exception
	 * 
	 * @param exception exception
	 */
	public static void logException(Exception exception) {
		Writer stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);
		exception.printStackTrace(writer);
		Dispatcher.dispatchEvent(new JSEvent(EventType.Log, null, stringWriter.toString()));
	}

}
