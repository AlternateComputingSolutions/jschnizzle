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

/**
 * Immutable application event class
 */
public class JSEvent {
	private EventType eventType;
	private Object source;
	private Object payload;

	/**
	 * constructor
	 * 
	 * @param eventType application event type
	 * @param source source of the event
	 * @param payload event payload
	 */
	public JSEvent(EventType eventType, Object source, Object payload) {
		this.eventType = eventType;
		this.source = source;
		this.payload = payload;
	}

	/**
	 * return the type of event
	 * 
	 * @return the event type
	 */
	public EventType getEventType() {
		return eventType;
	}

	/**
	 * return the source of the event
	 * 
	 * @return the event source
	 */
	public Object getSource() {
		return source;
	}

	/**
	 * return the payload of the event
	 * 
	 * @return the event payload
	 */
	public Object getPayload() {
		return payload;
	}

}
