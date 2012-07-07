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

import java.util.Collection;
import java.util.LinkedList;

import com.alternatecomputing.jschnizzle.model.Diagram;

public class RendererFactory {
	private static Collection<Renderer> renderers = new LinkedList<Renderer>();

	public static void registerRenderer(Renderer renderer) {
		renderers.add(renderer);
	}

	public static Renderer getRendererForDiagram(Diagram diagram) {
		for (Renderer renderer : renderers) {
			if (renderer.canRender(diagram)) {
				return renderer;
			}
		}
		throw new RendererException("no registered renderers support diagram type '" + diagram.getType() + "'");
	}
}
