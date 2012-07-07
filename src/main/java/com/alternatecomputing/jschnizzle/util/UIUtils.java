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
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alternatecomputing.jschnizzle.event.Dispatcher;
import com.alternatecomputing.jschnizzle.event.EventType;
import com.alternatecomputing.jschnizzle.event.JSEvent;
import com.alternatecomputing.jschnizzle.model.Diagram;
import com.alternatecomputing.jschnizzle.model.DiagramStyle;
import com.alternatecomputing.jschnizzle.renderer.Renderer;
import com.alternatecomputing.jschnizzle.renderer.RendererFactory;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * General UI utilities and convenience methods
 */
public class UIUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(UIUtils.class);

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
	 * @param dialog component that this panel will be embedded into
	 * @param isNew whether this will be used for a new diagram or to update an existing one
	 * @return diagram panel
	 */
	public static JPanel createDiagramPanel(JXHeader header, final Diagram diagram, final JDialog dialog, final boolean isNew) {
		final String originalScript = diagram.getScript();
		final DiagramStyle originalStyle = diagram.getStyle();
		FormLayout layout = new FormLayout("right:max(40dlu;pref), 4dlu, fill:min:grow", "pref, 4dlu, pref, 4dlu, pref, 4dlu, pref, 4dlu, pref, fill:pref:grow, 4dlu, pref");
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

		builder.add(new JLabel("Style:"), cc.xy(1, 7));
		Renderer renderer = RendererFactory.getRendererForDiagram(diagram);
		final JComboBox<DiagramStyle> styleComboBox = new JComboBox<DiagramStyle>(renderer.getStylesForType(diagram.getType()));
		if (originalScript != null) {
		styleComboBox.setSelectedItem(originalStyle);
		} else {
			styleComboBox.setSelectedIndex(0);
		}
		builder.add(styleComboBox, cc.xy(3, 7));

		builder.add(new JLabel("Script:"), cc.xy(1, 9));
		final JTextArea scriptTextArea = new JTextArea(diagram.getScript() == null ? "" : diagram.getScript());
		builder.add(new JScrollPane(scriptTextArea), cc.xywh(3, 9, 1, 2));

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				diagram.setName(nameTextField.getText());
				diagram.setNote(noteTextField.getText());
				diagram.setStyle((DiagramStyle) styleComboBox.getSelectedItem());
				diagram.setScript(scriptTextArea.getText());
				Dispatcher.dispatchEvent(new JSEvent(EventType.ProgressStarted, null, null));
				Thread t = new Thread() {
					public void run() {
						try {
							if (needsRendering()) {
								LOGGER.info("Rendering diagram '" + diagram.getName() + "'...");
								Renderer renderer = RendererFactory.getRendererForDiagram(diagram);
								BufferedImage image = renderer.render(diagram);
								diagram.nonBeanImage(image);
								LOGGER.info("Diagram '" + diagram.getName() + "' successfully rendered.");
							}
							if (isNew) {
								LOGGER.info("Diagram '" + diagram.getName() + "' created.");
								Dispatcher.dispatchEvent(new JSEvent(EventType.DiagramAdded, this, diagram));
								Dispatcher.dispatchEvent(new JSEvent(EventType.SelectDiagram, this, diagram));
							} else {
								LOGGER.info("Diagram '" + diagram.getName() + "' modified.");
								Dispatcher.dispatchEvent(new JSEvent(EventType.DiagramModified, this, diagram));
							}
						} catch (Exception e) {
							LOGGER.error("Error processing user action.", e);
						} finally {
							Dispatcher.dispatchEvent(new JSEvent(EventType.ProgressCompleted, null, null));
						}
					}

					private boolean needsRendering() {
						return (!diagram.getScript().equals(originalScript)) || (!diagram.getStyle().equals(originalStyle));
					}
				};
				t.start();
				dialog.dispose();
			}
		});
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});
		builder.add(ButtonBarFactory.buildOKCancelBar(okButton, cancelButton), cc.xy(3, 12));
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

}
