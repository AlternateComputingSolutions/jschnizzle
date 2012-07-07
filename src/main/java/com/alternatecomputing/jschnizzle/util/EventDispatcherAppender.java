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
package com.alternatecomputing.jschnizzle.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.encoder.Encoder;

import com.alternatecomputing.jschnizzle.event.Dispatcher;
import com.alternatecomputing.jschnizzle.event.EventType;
import com.alternatecomputing.jschnizzle.event.JSEvent;

/**
 *
 */
public class EventDispatcherAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {
	protected Encoder<ILoggingEvent> encoder;
	private ByteArrayOutputStream os;

	/**
	 *
	 */
	public EventDispatcherAppender() {
		super();
		os = new ByteArrayOutputStream();
	}

	/**
	 * @see ch.qos.logback.core.AppenderBase#append(java.lang.Object)
	 */
	@Override
	protected void append(ILoggingEvent eventObject) {
		try {
			os.reset();
			encoder.doEncode(eventObject);
			Dispatcher.dispatchEvent(new JSEvent(EventType.Log, null, os.toString()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setEncoder(Encoder<ILoggingEvent> encoder) {
		this.encoder = encoder;
		try {
			encoder.init(os);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
