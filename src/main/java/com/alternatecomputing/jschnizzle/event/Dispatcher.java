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
package com.alternatecomputing.jschnizzle.event;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Application event dispatcher
 */
public class Dispatcher {
	private static Collection<Listener> listeners = new LinkedList<Listener>();

	/**
	 * add an application event listener
	 * 
	 * @param listener listener
	 */
	public static void addListener(Listener listener) {
		listeners.add(listener);
	}

	/**
	 * remove an application event listener
	 * 
	 * @param listener listener
	 */
	public static void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	/**
	 * dispatch the given application event to all registered listeners
	 * 
	 * @param event application event
	 */
	public static void dispatchEvent(JSEvent event) {
		for (Listener listener : listeners) {
			listener.onEvent(event);
		}
	}

}
