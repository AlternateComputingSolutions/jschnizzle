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
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alternatecomputing.jschnizzle.model.Diagram;
import com.alternatecomputing.jschnizzle.model.DiagramStyle;
import com.alternatecomputing.jschnizzle.model.DiagramType;
import com.alternatecomputing.jschnizzle.renderer.util.BufferedImageTranscoder;
import com.alternatecomputing.jschnizzle.util.UIUtils;

public class WebSequenceRenderer implements Renderer {
	private static final Logger LOGGER = LoggerFactory.getLogger(UIUtils.class);
	private DiagramStyle[] styles = new DiagramStyle[]{
			new DiagramStyle("Plain UML", "default"),
			new DiagramStyle("Green Earth", "earth"),
			new DiagramStyle("Blue Modern", "modern-blue"),
			new DiagramStyle("MSCGen", "mscgen"),
			new DiagramStyle("Omegapple", "omegapple"),
			new DiagramStyle("QSD", "qsd"),
			new DiagramStyle("Rose", "rose"),
			new DiagramStyle("Round Green", "roundgreen"),
			new DiagramStyle("Napkin", "napkin")
			};

	public BufferedImage render(Diagram diagram) {
		String script = diagram.getScript();
		if (script == null) {
			throw new RendererException("no script defined.");
		}

		String style = diagram.getStyle().getValue();
		String baseURL = getBaseURL();

		try {
			// build parameter string
			String data = "style=" + style + "&format=svg&message=" + URLEncoder.encode(script, "UTF-8");

			// send the request
			URL url = new URL(baseURL);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

			// write parameters
			writer.write(data);
			writer.flush();

			// get the response
			StringBuffer answer = new StringBuffer();
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				answer.append(line);
			}
			writer.close();
			reader.close();

			JSONObject json = JSONObject.fromString(answer.toString());

			HttpClient client = new HttpClient();
            String proxyHost = System.getProperty("http.proxyHost");
            String proxyPort = System.getProperty("http.proxyPort");
            if (StringUtils.isNotBlank(proxyHost) && StringUtils.isNotBlank(proxyPort)) {
                client.getHostConfiguration().setProxy(proxyHost, Integer.parseInt(proxyPort));
            }
			String getURI = baseURL + json.getString("img");
			GetMethod getMethod = new GetMethod(getURI);
			client.executeMethod(getMethod);
			String svgContents = getMethod.getResponseBodyAsString();
			getMethod.releaseConnection();
			LOGGER.debug(svgContents);

			diagram.setEncodedImage(svgContents);
			TranscoderInput input = new TranscoderInput(new ByteArrayInputStream(svgContents.getBytes()));
			BufferedImageTranscoder imageTranscoder = new BufferedImageTranscoder();
		    imageTranscoder.transcode(input, null);

		    // log any errors to the UI console
		    JSONArray errors = json.getJSONArray("errors");
		    for (int eIdx = 0; eIdx < errors.length(); ++eIdx) {
		    	LOGGER.error("JSON error: " + errors.getString(eIdx));
		    }
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
		return DiagramType.Sequence.equals(diagram.getType());
	}

	public DiagramStyle[] getStylesForType(DiagramType diagramType) {
		return styles;
	}

	private String getBaseURL() {
		String baseURL = System.getProperty("websequence.url");
		if (baseURL == null) {
			baseURL = "http://www.websequencediagrams.com/";
		}
		return baseURL;
	}

}
