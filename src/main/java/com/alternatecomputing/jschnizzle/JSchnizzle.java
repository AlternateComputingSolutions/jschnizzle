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
package com.alternatecomputing.jschnizzle;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

import com.alternatecomputing.jschnizzle.action.AboutAction;
import com.alternatecomputing.jschnizzle.action.CopyToClipboardAction;
import com.alternatecomputing.jschnizzle.action.CreateActivityDiagramAction;
import com.alternatecomputing.jschnizzle.action.CreateClassDiagramAction;
import com.alternatecomputing.jschnizzle.action.CreateUseCaseDiagramAction;
import com.alternatecomputing.jschnizzle.action.DeleteDiagramAction;
import com.alternatecomputing.jschnizzle.action.EditDiagramAction;
import com.alternatecomputing.jschnizzle.action.ExitAction;
import com.alternatecomputing.jschnizzle.action.ExportImageAction;
import com.alternatecomputing.jschnizzle.action.LoadAction;
import com.alternatecomputing.jschnizzle.action.SaveAction;
import com.alternatecomputing.jschnizzle.action.SaveAsAction;
import com.alternatecomputing.jschnizzle.event.Dispatcher;
import com.alternatecomputing.jschnizzle.event.JSEvent;
import com.alternatecomputing.jschnizzle.event.Listener;
import com.alternatecomputing.jschnizzle.model.ApplicationModel;
import com.alternatecomputing.jschnizzle.model.Diagram;
import com.alternatecomputing.jschnizzle.model.DiagramType;
import com.alternatecomputing.jschnizzle.ui.ImagePanel;
import com.alternatecomputing.jschnizzle.util.UIUtils;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;

public class JSchnizzle implements Listener {
	private ApplicationModel applicationModel;
	private JFrame frame;
	private ImagePanel imagePanel;
	private AboutAction aboutAction;
	private CreateActivityDiagramAction createActivityDiagramAction;
	private CreateClassDiagramAction createClassDiagramAction;
	private CreateUseCaseDiagramAction createUseCaseDiagramAction;
	private EditDiagramAction editDiagramAction;
	private DeleteDiagramAction deleteDiagramAction;
	private ExitAction exitAction;
	private LoadAction loadAction;
	private SaveAction saveAction;
	private SaveAsAction saveAsAction;
	private CopyToClipboardAction copyToClipboardAction;
	private ExportImageAction exportImageAction;
	private JPopupMenu activityPopupMenu;
	private JPopupMenu classPopupMenu;
	private JPopupMenu useCasePopupMenu;
	private JPopupMenu imagePopupMenu;
	private JTextArea consoleTextArea;
	private JProgressBar progressBar;
	private JList activityScriptsList;
	private JList classScriptsList;
	private JList useCaseScriptsList;
	private JPanel mainPanel;

	private JSchnizzle() {
		applicationModel = ApplicationModel.getInstance();
		initializeLookAndFeel();
		frame = createMainFrame();
		initializeActions();
		frame.setJMenuBar(createMenuBar());
		createPopupMenus();
		frame.setVisible(true);
		Dispatcher.addListener(this);
	}

	private void createPopupMenus() {
		activityPopupMenu = createPopupMenu(createActivityDiagramAction);
		classPopupMenu = createPopupMenu(createClassDiagramAction);
		useCasePopupMenu = createPopupMenu(createUseCaseDiagramAction);
		imagePopupMenu = createImagePopupMenu();
	}

	private void initializeActions() {
		aboutAction = new AboutAction(frame);
		createActivityDiagramAction = new CreateActivityDiagramAction(frame);
		createClassDiagramAction = new CreateClassDiagramAction(frame);
		createUseCaseDiagramAction = new CreateUseCaseDiagramAction(frame);
		editDiagramAction = new EditDiagramAction(frame);
		editDiagramAction.setEnabled(false);
		deleteDiagramAction = new DeleteDiagramAction(frame);
		deleteDiagramAction.setEnabled(false);
		saveAction = new SaveAction(applicationModel);
		saveAction.setEnabled(false);
		saveAsAction = new SaveAsAction(applicationModel, frame);
		saveAsAction.setEnabled(false);
		loadAction = new LoadAction(applicationModel, frame, saveAction);
		exitAction = new ExitAction(applicationModel, frame, saveAction);
		copyToClipboardAction = new CopyToClipboardAction();
		copyToClipboardAction.setEnabled(false);
		exportImageAction = new ExportImageAction(frame);
		exportImageAction.setEnabled(false);
	}

	private void initializeLookAndFeel() {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
			return;
		} catch (Exception e) {
			try {
				UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
			} catch (UnsupportedLookAndFeelException e1) {
				UIUtils.logException(e1);
			}
		}
	}

	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenu fileNewMenu = new JMenu("New");
		fileNewMenu.add(new JMenuItem(createActivityDiagramAction));
		fileNewMenu.add(new JMenuItem(createClassDiagramAction));
		fileNewMenu.add(new JMenuItem(createUseCaseDiagramAction));
		fileMenu.add(fileNewMenu);
		fileMenu.add(new JMenuItem(deleteDiagramAction));
		fileMenu.add(new JMenuItem(exportImageAction));
		fileMenu.addSeparator();
		fileMenu.add(new JMenuItem(loadAction));
		fileMenu.add(new JMenuItem(saveAction));
		fileMenu.add(new JMenuItem(saveAsAction));
		fileMenu.addSeparator();
		fileMenu.add(new JMenuItem(exitAction));
		menuBar.add(fileMenu);
		JMenu editMenu = new JMenu("Edit");
		editMenu.add(new JMenuItem(editDiagramAction));
		editMenu.add(new JMenuItem(copyToClipboardAction));
		menuBar.add(editMenu);
		JMenu helpMenu = new JMenu("Help");
		JMenuItem aboutMenuItem = new JMenuItem(aboutAction);
		helpMenu.add(aboutMenuItem);
		menuBar.add(helpMenu);
		return menuBar;
	}

	private JFrame createMainFrame() {
		JFrame frame = new JXFrame();
		frame.setTitle("JSchnizzle");
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.setSize(800, 600);
		JPanel panel = createMainPanel();
		frame.add(panel);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				exitAction.actionPerformed(null);
			}
		});
		return frame;
	}

	private JPanel createMainPanel() {
		JPanel panel = new JXPanel(new FormLayout("4dlu, fill:min:grow, 4dlu", "4dlu, fill:min:grow, 4dlu, pref, 4dlu"));
		CellConstraints cc = new CellConstraints();

		JSplitPane verticalSplitPane = UIUtils.createUndecoratedSplitPane(JSplitPane.VERTICAL_SPLIT);
		verticalSplitPane.setDividerLocation(350);
		verticalSplitPane.setResizeWeight(1);
		verticalSplitPane.setDividerSize(8);
		panel.add(verticalSplitPane, cc.xy(2, 2));

		verticalSplitPane.add(createTopSubPanel());

		consoleTextArea = new JTextArea();
		JScrollPane scrollPane = new JScrollPane(consoleTextArea);
		scrollPane.setPreferredSize(new Dimension(0, 100));
		verticalSplitPane.add(UIUtils.createTitledPanel("Console", scrollPane));

		panel.add(createStatusArea(), cc.xy(2, 4));
		return panel;
	}

	private JPanel createTopSubPanel() {
		mainPanel = new JXPanel(new FormLayout("fill:min:grow", "fill:min:grow"));
		CellConstraints cc = new CellConstraints();

		JSplitPane horizontalSplitPane = UIUtils.createUndecoratedSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		horizontalSplitPane.setDividerLocation(200);
		horizontalSplitPane.setDividerSize(8);
		mainPanel.add(horizontalSplitPane, cc.xy(1, 1));

		JXTaskPaneContainer taskPaneContainer = new JXTaskPaneContainer();

		activityScriptsList = createScriptsList(applicationModel.getActivityScriptsModel(), mainPanel);
		JXTaskPane activityTaskPane = new JXTaskPane();
		activityTaskPane.setTitle("Activity Diagrams");
		activityTaskPane.setToolTipText("Activity Diagram Definitions");
		activityTaskPane.add(activityScriptsList);
		activityTaskPane.getContentPane().setBackground(Color.WHITE);
		MouseAdapter activityMouseAdapter = createDefinitionsMouseAdapter(DiagramType.Activity, mainPanel, activityScriptsList);
		activityScriptsList.addMouseListener(activityMouseAdapter);
		activityTaskPane.addMouseListener(activityMouseAdapter);
		taskPaneContainer.add(activityTaskPane);

		classScriptsList = createScriptsList(applicationModel.getClassScriptsModel(), mainPanel);
		JXTaskPane classTaskPane = new JXTaskPane();
		classTaskPane.setTitle("Class Diagrams");
		classTaskPane.setToolTipText("Class Diagram Definitions");
		classTaskPane.add(classScriptsList);
		classTaskPane.getContentPane().setBackground(Color.WHITE);
		MouseAdapter classMouseAdapter = createDefinitionsMouseAdapter(DiagramType.Class, mainPanel, classScriptsList);
		classScriptsList.addMouseListener(classMouseAdapter);
		classTaskPane.addMouseListener(classMouseAdapter);
		taskPaneContainer.add(classTaskPane);

		useCaseScriptsList = createScriptsList(applicationModel.getUseCaseScriptsModel(), mainPanel);
		JXTaskPane useCaseTaskPane = new JXTaskPane();
		useCaseTaskPane.setTitle("Use Case Diagrams");
		useCaseTaskPane.setToolTipText("Use Case Diagram Definitions");
		useCaseTaskPane.add(useCaseScriptsList);
		useCaseTaskPane.getContentPane().setBackground(Color.WHITE);
		MouseAdapter useCaseMouseAdapter = createDefinitionsMouseAdapter(DiagramType.UseCase, mainPanel, useCaseScriptsList);
		useCaseScriptsList.addMouseListener(useCaseMouseAdapter);
		useCaseTaskPane.addMouseListener(useCaseMouseAdapter);
		taskPaneContainer.add(useCaseTaskPane);

		horizontalSplitPane.add(UIUtils.createTitledPanel("Definitions", new JScrollPane(taskPaneContainer)));

		imagePanel = new ImagePanel();
		imagePanel.addDiagramPanelMouseListener(createImagePanelMouseAdapter());
		horizontalSplitPane.add(UIUtils.createTitledPanel("Diagram", imagePanel));

		return mainPanel;
	}

	private JList createScriptsList(final DefaultListModel scriptsModel, final JPanel mainPanel) {
		final JList scriptsList = new JList(scriptsModel);
		scriptsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scriptsList.setCellRenderer(new DiagramCellRenderer());
		scriptsList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting() == false) {
					int selectedIndex = scriptsList.getSelectedIndex();
					if (selectedIndex > -1) {
						Diagram diagram = (Diagram) scriptsModel.get(selectedIndex);
						imagePanel.setDiagram(diagram);
						editDiagramAction.setDiagram(diagram);
						editDiagramAction.setEnabled(true);
						deleteDiagramAction.setDiagram(diagram);
						deleteDiagramAction.setEnabled(true);
						copyToClipboardAction.setDiagram(diagram);
						copyToClipboardAction.setEnabled(true);
						exportImageAction.setDiagram(diagram);
						exportImageAction.setEnabled(true);
						mainPanel.revalidate();
					} else {
						imagePanel.setDiagram(null);
						editDiagramAction.setEnabled(false);
						editDiagramAction.setDiagram(null); // for safety
						deleteDiagramAction.setEnabled(false);
						deleteDiagramAction.setDiagram(null); // for safety
						copyToClipboardAction.setDiagram(null); // for safety
						copyToClipboardAction.setEnabled(false);
						exportImageAction.setDiagram(null);
						exportImageAction.setEnabled(false);
					}
				}
			}

		});
		return scriptsList;
	}

	private MouseAdapter createDefinitionsMouseAdapter(final DiagramType diagramType, final JPanel mainPanel, final JList scriptsList) {
		return new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				switch (diagramType) {
					case Activity:
						classScriptsList.clearSelection();
						useCaseScriptsList.clearSelection();
						break;
					case Class:
						activityScriptsList.clearSelection();
						useCaseScriptsList.clearSelection();
						break;
					case UseCase:
						activityScriptsList.clearSelection();
						classScriptsList.clearSelection();
						break;
				}
				checkForTriggerEvent(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				checkForTriggerEvent(e);
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				int selectedIndex = scriptsList.getSelectedIndex();
				if (selectedIndex > -1) {
					if (e.getClickCount() == 1) {
						refreshSelectedDiagram(diagramType, mainPanel, selectedIndex);
					} else if (e.getClickCount() == 2) {
						editDiagramAction.actionPerformed(null);
					}
				}
				super.mouseClicked(e);
			}

			private void checkForTriggerEvent(MouseEvent e) {
				if (e.isPopupTrigger()) {
					switch (diagramType) {
						case Activity:
							activityPopupMenu.show(e.getComponent(), e.getX(), e.getY());
							break;
						case Class:
							classPopupMenu.show(e.getComponent(), e.getX(), e.getY());
							break;
						case UseCase:
							useCasePopupMenu.show(e.getComponent(), e.getX(), e.getY());
							break;
					}
				}
			}
		};
	}

	private MouseAdapter createImagePanelMouseAdapter() {
		return new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				checkForTriggerEvent(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				checkForTriggerEvent(e);
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				Diagram diagram = imagePanel.getDiagram();
				if (diagram != null && e.getClickCount() == 2) {
					editDiagramAction.setDiagram(diagram);
					editDiagramAction.actionPerformed(null);
				}
				super.mouseClicked(e);
			}

			private void checkForTriggerEvent(MouseEvent e) {
				if (e.isPopupTrigger()) {
					imagePopupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		};
	}

	private void refreshSelectedDiagram(DiagramType diagramType, final JPanel mainPanel, int selectedIndex) {
		switch (diagramType) {
			case Activity:
				imagePanel.setDiagram((Diagram) applicationModel.getActivityScriptsModel().get(selectedIndex));
				break;
			case Class:
				imagePanel.setDiagram((Diagram) applicationModel.getClassScriptsModel().get(selectedIndex));
				break;
			case UseCase:
				imagePanel.setDiagram((Diagram) applicationModel.getUseCaseScriptsModel().get(selectedIndex));
				break;
		}
		mainPanel.revalidate();
	}

	private Component createStatusArea() {
		JPanel panel = new JXPanel(new FormLayout("fill:min:grow, 4dlu, pref", "pref"));
		CellConstraints cc = new CellConstraints();
		JLabel status = new JLabel(" ");
		status.setBorder(BorderFactory.createEtchedBorder());
		panel.add(status, cc.xy(1, 1));
		progressBar = new JProgressBar();
		panel.add(progressBar, cc.xy(3, 1));
		return panel;
	}

	private JPopupMenu createPopupMenu(Action createAction) {
		JPopupMenu menu = new JPopupMenu();
		menu.add(createAction);
		menu.add(editDiagramAction);
		menu.add(deleteDiagramAction);
		return menu;
	}

	private JPopupMenu createImagePopupMenu() {
		JPopupMenu menu = new JPopupMenu();
		menu.add(copyToClipboardAction);
		menu.add(exportImageAction);
		return menu;
	}

	public static void main(String[] args) {
		new JSchnizzle();
		UIUtils.logMessage("JSchnizzle started.");
	}

	private static class DiagramCellRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = -6057503164351481917L;

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			setText(((Diagram) value).getName());
			return this;
		}
	}

	public void onEvent(final JSEvent event) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					Object eventPayload = event.getPayload();
					switch (event.getEventType()) {
						case Log:
							consoleTextArea.append((new Date()).toString());
							consoleTextArea.append("  ");
							consoleTextArea.append(eventPayload.toString());
							consoleTextArea.append("\n");
							consoleTextArea.setCaretPosition(consoleTextArea.getText().length());
							break;
						case DiagramAdded:
							applicationModel.addDiagram((Diagram) eventPayload);
							break;
						case DiagramDeleted:
							applicationModel.deleteDiagram((Diagram) eventPayload);
							break;
						case DiagramDeleteAll:
							applicationModel.deleteAllDiagrams();
							break;
						case DiagramModified:
							switch (((Diagram) eventPayload).getType()) {
								case Activity:
									refreshSelectedDiagram(DiagramType.Activity, mainPanel, activityScriptsList.getSelectedIndex());
									break;
								case Class:
									refreshSelectedDiagram(DiagramType.Class, mainPanel, classScriptsList.getSelectedIndex());
									break;
								case UseCase:
									refreshSelectedDiagram(DiagramType.UseCase, mainPanel, useCaseScriptsList.getSelectedIndex());
									break;
							}
							applicationModel.markModelDirty();
							break;
						case ProgressCompleted:
							progressBar.setIndeterminate(false);
							break;
						case ProgressStarted:
							progressBar.setIndeterminate(true);
							break;
						case SelectDiagram:
							switch (((Diagram) eventPayload).getType()) {
								case Activity:
									activityScriptsList.setSelectedIndex(applicationModel.getActivityScriptsModel().indexOf((Diagram) eventPayload));
									break;
								case Class:
									classScriptsList.setSelectedIndex(applicationModel.getClassScriptsModel().indexOf((Diagram) eventPayload));
									break;
								case UseCase:
									useCaseScriptsList.setSelectedIndex(applicationModel.getUseCaseScriptsModel().indexOf((Diagram) eventPayload));
									break;
							}
							imagePanel.setDiagram((Diagram) eventPayload);
							break;
						case FileNameChanged:
							frame.setTitle("JSchnizzle - " + eventPayload);
							applicationModel.setFileName((String) eventPayload);
							applicationModel.markModelClean();
							break;
						default:
							break;
					}
				} catch (Exception e) {
					UIUtils.logException(e);
				} finally {
					updateActionStates();
				}
			}

		});
	}

	private void updateActionStates() {
		saveAction.setEnabled(applicationModel.isModelDirty() && applicationModel.getFileName() != null);
		saveAsAction.setEnabled(applicationModel.isModelDirty());
	}

}
