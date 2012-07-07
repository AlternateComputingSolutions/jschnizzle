/*
 * Copyright 2012 Alternate Computing Solutions Inc.
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
package com.alternatecomputing.jschnizzle.renderer;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.StringTokenizer;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alternatecomputing.jschnizzle.model.Diagram;
import com.alternatecomputing.jschnizzle.model.DiagramStyle;
import com.alternatecomputing.jschnizzle.model.DiagramType;
import com.alternatecomputing.jschnizzle.renderer.util.BufferedImageTranscoder;

public class YUMLRenderer implements Renderer {
	private static final Logger LOGGER = LoggerFactory.getLogger(YUMLRenderer.class);
	private DiagramStyle[] styles = new DiagramStyle[] {
			new DiagramStyle("Boring", "nofunky;"),
			new DiagramStyle("Plain", "plain;"),
			new DiagramStyle("Scruffy", "scruffy;")
			};

	public BufferedImage render(Diagram diagram) {
		String script = diagram.getScript();
		if (script == null) {
			throw new RendererException("no script defined.");
		}
		StringTokenizer st = new StringTokenizer(script.trim(), "\n");
		StringBuilder buffer = new StringBuilder();
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (token.startsWith("#")) {
				continue; // skip over comments
			}
			buffer.append(token.trim());
			if (st.hasMoreTokens()) {
				buffer.append(", ");
			}
		}
		buffer.append(".svg");

		String style = diagram.getStyle().getValue();
		String baseURL = getBaseURL();
		try {
			HttpClient client = new HttpClient();
            String proxyHost = System.getProperty("http.proxyHost");
            String proxyPort = System.getProperty("http.proxyPort");
            if (StringUtils.isNotBlank(proxyHost) && StringUtils.isNotBlank(proxyPort)) {
                client.getHostConfiguration().setProxy(proxyHost, Integer.parseInt(proxyPort));
            }

			String postURI = baseURL + "diagram/" + style + "/" + diagram.getType().getUrlModifier() + "/";
			LOGGER.debug(postURI);
			PostMethod postMethod = new PostMethod(postURI );
			postMethod.addParameter("dsl_text", buffer.toString());
			client.executeMethod( postMethod );
			String svgResourceName = postMethod.getResponseBodyAsString();
			postMethod.releaseConnection();
			LOGGER.debug(svgResourceName);

			String getURI = baseURL + svgResourceName;
			LOGGER.debug(getURI);
			GetMethod getMethod = new GetMethod(getURI);
			client.executeMethod(getMethod);
			String svgContents = getMethod.getResponseBodyAsString();
			getMethod.releaseConnection();
			LOGGER.debug(svgContents);

			diagram.setEncodedImage(svgContents);
			TranscoderInput input = new TranscoderInput(new ByteArrayInputStream(svgContents.getBytes()));
			BufferedImageTranscoder imageTranscoder = new BufferedImageTranscoder();
		    imageTranscoder.transcode(input, null);

		    return imageTranscoder.getBufferedImage();
		} catch (MalformedURLException e) {
			throw new RendererException(e);
		} catch (IOException e) {
			throw new RendererException(e);
		} catch (TranscoderException e) {
			throw new RendererException(e);
		}
	}

	public boolean canRender(Diagram diagram) {
		DiagramType type = diagram.getType();
		return DiagramType.Activity.equals(type) || DiagramType.Class.equals(type) || DiagramType.UseCase.equals(type);
	}

	public DiagramStyle[] getStylesForType(DiagramType diagramType) {
		return styles;
	}

	private String getBaseURL() {
		String baseURL = System.getProperty("yuml.url");
		if (baseURL == null) {
			baseURL = "http://yuml.me/";
		}
		return baseURL;
	}

}
