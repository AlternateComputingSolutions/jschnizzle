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

import java.awt.Image;
import java.io.IOException;
import java.io.Serializable;

import com.alternatecomputing.jschnizzle.util.ImageUtils;
import com.alternatecomputing.jschnizzle.util.UIUtils;

/**
 * Class to model a diagram and its metadata
 */
public class Diagram implements Serializable {
	private static final long serialVersionUID = -3878609436832624784L;
	private String name;
	private String note;
	private String script;
	private Image image;
	private String encodedImage;
	private DiagramType type;

	/**
	 * return the name of this diagram
	 * 
	 * @return diagram name
	 */
	public String getName() {
		return name;
	}

	/**
	 * set the name of this diagram
	 * 
	 * @param name diagram name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * return the note for this diagram
	 * 
	 * @return diagram note
	 */
	public String getNote() {
		return note;
	}

	/**
	 * set the note for this diagram
	 * 
	 * @param note diagram note
	 */
	public void setNote(String note) {
		this.note = note;
	}

	/**
	 * return the script for this diagram
	 * 
	 * @return diagram script
	 */
	public String getScript() {
		return script;
	}

	/**
	 * set the script for this diagram
	 * 
	 * @param script diagram script
	 */
	public void setScript(String script) {
		this.script = script;
	}

	/**
	 * return the image for this diagram. The method name does not follow the standard bean convention so that it is not
	 * serialized by the XMLEncoder when saving to a file.
	 * 
	 * @return diagram image
	 */
	public Image nonBeanImage() {
		return image;
	}

	/**
	 * set the image for this diagram.
	 * 
	 * @param image diagram image
	 */
	public void nonBeanImage(Image image) {
		this.image = image;
	}

	/**
	 * return the ascii-encoded representation of the image for this diagram
	 * 
	 * @return ascii-encoded representation of the diagram image
	 */
	public String getEncodedImage() {
		return encodedImage;
	}

	/**
	 * set the ascii-encoded representation of the image for this diagram
	 * 
	 * @param encodedImage ascii-encoded representation of the diagram image
	 */
	public void setEncodedImage(String encodedImage) {
		this.encodedImage = encodedImage;
		if (this.image == null) {
			try {
				this.image = ImageUtils.decodeImage(encodedImage);
			} catch (IOException e) {
				UIUtils.logException(e);
			}
		}
	}

	/**
	 * return the type of this diagram
	 * 
	 * @return diagram type
	 */
	public DiagramType getType() {
		return type;
	}

	/**
	 * set the type of this diagram
	 * 
	 * @param type diagram type
	 */
	public void setType(DiagramType type) {
		this.type = type;
	}

}
