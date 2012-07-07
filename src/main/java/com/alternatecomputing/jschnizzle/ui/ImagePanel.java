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
package com.alternatecomputing.jschnizzle.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alternatecomputing.jschnizzle.model.Diagram;
import com.alternatecomputing.jschnizzle.renderer.util.BufferedImageTranscoder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class ImagePanel extends JPanel {
	private static final long serialVersionUID = 8899749112300691634L;
	private static final Logger LOGGER = LoggerFactory.getLogger(ImagePanel.class);
	private Diagram diagram;
	private JPanel diagramPanel;
	private JSlider scaleSlider;
	private static final int INTERVALS = 10;

	public ImagePanel() {
		super();
		this.setLayout(new FormLayout("fill:min:grow, pref, 4dlu, pref, 4dlu", "fill:min:grow, 4dlu, pref, 4dlu"));
		CellConstraints cc = new CellConstraints();
		this.diagramPanel = new JPanel();
		this.diagramPanel.setLayout(new BorderLayout());
		this.diagramPanel.setBackground(Color.WHITE);
		JScrollPane jScrollPane = new JScrollPane(diagramPanel);
		add(jScrollPane, cc.xywh(1, 1, 5, 1));
		add(new JLabel("Scale:"), cc.xy(2, 3));
		scaleSlider = createScaleSlider(this);
		add(scaleSlider, cc.xy(4, 3));
	}

	private JSlider createScaleSlider(final Component panel) {
		final JSlider slider = new JSlider(SwingConstants.HORIZONTAL, 0, INTERVALS, INTERVALS / 2);
		slider.setMajorTickSpacing(2);
		slider.setMinorTickSpacing(1);
		slider.setSnapToTicks(true);
		slider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				if (diagram != null) {
					if (slider.getValue() == INTERVALS / 2) {
						setDiagram(diagram); // use original at full size
					} else {
						if (slider.getValueIsAdjusting()) {
							resizeImage(slider, Image.SCALE_FAST);
						} else {
							resizeFromSVG(slider);
						}
					}
					panel.validate();
				}
			}
		});
		return slider;
	}

	public void setDiagram(Diagram diagram) {
		if (diagramPanel.getComponentCount() != 0) {
			diagramPanel.removeAll();
		}
		this.diagram = diagram;
		scaleSlider.setValue(INTERVALS / 2);
		if (diagram != null) {
			BufferedImage image = (BufferedImage) diagram.nonBeanImage();
			if (image != null) {
				int width = image.getWidth();
				int height = image.getHeight();
				diagramPanel.setPreferredSize(new Dimension(width, height)); // reset size to remove sticky scrollbars
				if (diagram != null) {
					diagramPanel.add(new JLabel(new ImageIcon(diagram.nonBeanImage())), BorderLayout.CENTER);
				}
			}
		}
		diagramPanel.validate();
		diagramPanel.repaint();
	}

	private void resizeImage(final JSlider slider, int hints) {
		BufferedImage image = (BufferedImage) diagram.nonBeanImage();
		float width = image.getWidth() * slider.getValue() / INTERVALS * 2;
		float height = image.getHeight() * slider.getValue() / INTERVALS * 2;
		JLabel scaledComponent = new JLabel(new ImageIcon(image.getScaledInstance(Math.max(1, (int) width), Math.max(1, (int) height), hints)));
		synchronized (diagramPanel) {
			diagramPanel.setPreferredSize(new Dimension((int) width, (int) height));
			if (diagramPanel.getComponentCount() != 0) {
				diagramPanel.removeAll();
			}
			diagramPanel.add(scaledComponent, BorderLayout.CENTER);
		}
	}

	private void resizeFromSVG(JSlider slider) {
		BufferedImage image = (BufferedImage) diagram.nonBeanImage();
		float width = image.getWidth() * slider.getValue() / INTERVALS * 2;
		float height = image.getHeight() * slider.getValue() / INTERVALS * 2;
		byte[] svgBytes = diagram.getEncodedImage().getBytes();
		TranscoderInput input = new TranscoderInput(new ByteArrayInputStream(svgBytes));
		BufferedImageTranscoder imageTranscoder = new BufferedImageTranscoder();
		imageTranscoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, width);
	    imageTranscoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, height);
	    try {
			imageTranscoder.transcode(input, null);
			BufferedImage bufferedImage = imageTranscoder.getBufferedImage();
			JLabel scaledComponent = new JLabel(new ImageIcon(bufferedImage));
			synchronized (diagramPanel) {
				diagramPanel.setPreferredSize(new Dimension((int) width, (int) height));
				if (diagramPanel.getComponentCount() != 0) {
					diagramPanel.removeAll();
				}
				diagramPanel.add(scaledComponent, BorderLayout.CENTER);
			}
		} catch (TranscoderException e) {
			LOGGER.error("Error resizing SVG image", e);
		}
	}

	public Diagram getDiagram() {
		return diagram;
	}

	public synchronized void addDiagramPanelMouseListener(MouseListener l) {
		super.addMouseListener(l);
		diagramPanel.addMouseListener(l);
	}

}
