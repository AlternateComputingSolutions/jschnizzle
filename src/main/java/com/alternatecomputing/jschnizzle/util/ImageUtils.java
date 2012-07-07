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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.alternatecomputing.jschnizzle.model.Diagram;

/**
 * Image utilities
 */
public class ImageUtils {

	/**
	 * populates the image of the given diagram
	 * 
	 * @param diagram diagram
	 */
	public static void populateImage(Diagram diagram) {
		String script = diagram.getScript();
		if (script == null) {
			return;
		}
		StringTokenizer st = new StringTokenizer(script.trim(), "\n");
		StringBuilder buffer = new StringBuilder();
		while (st.hasMoreTokens()) {
			buffer.append(st.nextToken().trim());
			if (st.hasMoreTokens()) {
				buffer.append(", ");
			}
		}
		buffer.append(".jpg");
		try {
			String urlString = "http://yuml.me/diagram/scruffy/" + diagram.getType().getUrlModifier() + "/" + buffer.toString();
			urlString = urlString.replaceAll(" ", "%20");
			URL url = new URL(urlString);
			BufferedImage image = ImageIO.read(url);
			diagram.nonBeanImage(image);
			diagram.setEncodedImage(encodeImage(image));
		} catch (MalformedURLException e) {
			UIUtils.logException(e);
		} catch (IOException e) {
			UIUtils.logException(e);
		}
	}

	/**
	 * create an ascii-encoded representation of the given image
	 * 
	 * @param image image
	 * @return ascii-encoded representation
	 * @throws IOException if there are errors getting the image content
	 */
	private static String encodeImage(BufferedImage image) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ImageIO.write(image, "jpg", bos);
		byte[] bytes = bos.toByteArray();
		BASE64Encoder base64Encoder = new BASE64Encoder();
		return base64Encoder.encode(bytes);
	}

	/**
	 * create an image from the given ascii-encoded representation
	 * 
	 * @param encoded ascii-encoded image representation
	 * @return image
	 * @throws IOException if there are errors creating the image
	 */
	public static BufferedImage decodeImage(String encoded) throws IOException {
		BASE64Decoder decoder = new BASE64Decoder();
		byte[] bytes = decoder.decodeBuffer(encoded);
		ByteArrayInputStream input = new ByteArrayInputStream(bytes);
		return ImageIO.read(input);
	}

}
