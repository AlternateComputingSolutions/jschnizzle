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

import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * Abstract class for file-based actions
 */
public abstract class AbstractFileAction extends AbstractAction {
	private static final long serialVersionUID = -1063716741829397534L;
	protected static final String FILE_EXTENSION = ".jsl";

	/**
	 * constructor
	 *
	 * @see javax.swing.AbstractAction#AbstractAction(String, Icon)
	 */
	public AbstractFileAction(String string, Icon icon) {
		super(string, icon);
	}

	/**
	 * create a configured file chooser instance
	 *
	 * @return file chooser
	 */
	protected JFileChooser createFileChooser() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setFileFilter(new FileFilter() {

			@Override
			public boolean accept(File file) {
				if (file.getName().toLowerCase().endsWith(FILE_EXTENSION) || file.isDirectory()) {
					return true;
				} else {
					return false;
				}
			}

			@Override
			public String getDescription() {
				return "JSchnizzle Definition Files";
			}
		});
		return chooser;
	}
}