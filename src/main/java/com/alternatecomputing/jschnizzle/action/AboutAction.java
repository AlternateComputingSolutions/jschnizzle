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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;

import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXImagePanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alternatecomputing.jschnizzle.util.UIUtils;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Action class for presenting the about dialog
 */
public class AboutAction extends AbstractAction {
	private static final long serialVersionUID = 6753966509469848156L;
	private static final Logger LOGGER = LoggerFactory.getLogger(AboutAction.class);
	private Frame owner;

	/**
	 * constructor
	 *
	 * @param owner owner of the presented about dialog
	 */
	public AboutAction(Frame owner) {
		super("About", null);
		putValue(SHORT_DESCRIPTION, "About JSchnizzle");
		this.owner = owner;
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		JDialog dialog = new JDialog(owner, "About JSchnizzle", true);
		JPanel panel = createAboutPanel(dialog);
		dialog.add(panel);
		dialog.setSize(600, 400);
		UIUtils.centerComponent(dialog, owner);
		dialog.setVisible(true);
	}

	/**
	 * create the about panel
	 *
	 * @param dialog component that will contain this panel
	 * @return about panel
	 */
	private JPanel createAboutPanel(final JDialog dialog) {
		FormLayout layout = new FormLayout("fill:min:grow, pref", "pref, 4dlu, fill:min:grow, 4dlu, pref");
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		JXHeader header = new JXHeader("JSchnizzle", "");
		builder.add(header, cc.xywh(1, 1, 2, 1));
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Version", createVersionPanel());
		tabbedPane.addTab("About", createNoticePanel());
		tabbedPane.addTab("License", createLicensePanel());
		tabbedPane.addTab("Acknowledgements", createAcknowledgementsPanel());
		builder.add(tabbedPane, cc.xywh(1, 3, 2, 1));
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});
		builder.add(ButtonBarFactory.buildOKBar(okButton), cc.xy(2, 5));
		return builder.getPanel();
	}

	/**
	 * create the panel containing the notice information
	 *
	 * @return notice panel
	 */
	private JPanel createNoticePanel() {
		return createContentPanel("notice.txt");
	}

	/**
	 * create the panel containing the license information
	 *
	 * @return license panel
	 */
	private JPanel createLicensePanel() {
		return createContentPanel("LICENSE-2.0.txt");
	}

	/**
	 * create the panel containing the acknowledgement information
	 *
	 * @return acknowledgement panel
	 */
	private JPanel createAcknowledgementsPanel() {
		return createContentPanel("acknowledgements.txt");
	}

	/**
	 * create the panel containing the version information
	 *
	 * @return version panel
	 */
	private Component createVersionPanel() {
		JPanel p = createBasicPanel();
		FormLayout layout = new FormLayout("fill:min:grow, pref, 10dlu, pref, 4dlu, pref, fill:min:grow", "fill:min:grow, 20dlu, pref, 4dlu, pref, 4dlu, pref, 20dlu, fill:min:grow");
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		CellConstraints cc = new CellConstraints();
		JXImagePanel imagePanel = new JXImagePanel();
		imagePanel.setBackground(Color.WHITE);
		try {
			BufferedImage image = ImageIO.read(Thread.currentThread().getContextClassLoader().getResource("logo.jpg"));
			imagePanel.setImage(image);
			imagePanel.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
		} catch (IOException e) {
			LOGGER.error("Error reading logo image from classpath.", e);
		}
		builder.add(imagePanel, cc.xywh(2, 2, 1, 7));
		Properties buildInfo = getBuildInfo();
		builder.add(new JLabel("Version:"), cc.xy(4, 3));
		builder.add(new JLabel(buildInfo.getProperty("Implementation-Version")), cc.xy(6, 3));
		builder.add(new JLabel("Build Rev:"), cc.xy(4, 5));
		builder.add(new JLabel(buildInfo.getProperty("Implementation-Build")), cc.xy(6, 5));
		builder.add(new JLabel("Build Date:"), cc.xy(4, 7));
		builder.add(new JLabel(buildInfo.getProperty("Implementation-Build-Date").replace("\"", "")), cc.xy(6, 7));
		JPanel panel = builder.getPanel();
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createEtchedBorder());
		p.add(panel, cc.xy(1, 1));
		return p;
	}

	/**
	 * @return
	 */
	private Properties getBuildInfo() {
		Properties buildInfo = new Properties();
		InputStream inStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("metadata.properties");
		try {
			buildInfo.load(inStream);
		} catch (IOException e) {
			LOGGER.error("Error retrieving build information.", e);
		} finally {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {
					LOGGER.error("Unexpected error while loading build information.", e);
				}
			}
		}
		return buildInfo;
	}

	/**
	 * create a panel containing content from the given resource location
	 *
	 * @param resourceLocation location for content
	 * @return panel
	 */
	private JPanel createBasicPanel() {
		FormLayout layout = new FormLayout("fill:min:grow", "fill:min:grow");
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();
		return builder.getPanel();
	}

	private JPanel createContentPanel(String resourceLocation) {
		JPanel panel = createBasicPanel();
		CellConstraints cc = new CellConstraints();
		JTextPane textPane = new JTextPane();
		URL aboutURL = Thread.currentThread().getContextClassLoader().getResource(resourceLocation);
		try {
			textPane.setPage(aboutURL);
		} catch (IOException e) {
			LOGGER.error("Error loading content from classpath location: " + resourceLocation, e);
		}
		panel.add(new JScrollPane(textPane), cc.xy(1, 1));
		return panel;
	}

}