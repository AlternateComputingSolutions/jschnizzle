package com.alternatecomputing.jschnizzle.util;

import java.beans.Encoder;
import java.beans.Expression;
import java.beans.PersistenceDelegate;

/**
 * This class is needed because the XMLEncoder in java 1.5 doesn't support enums. Source:
 * http://weblogs.java.net/blog/2006/08/07/how-encode-enums
 */
public class EnumPersistenceDelegate extends PersistenceDelegate {
	protected boolean mutatesTo(Object oldInstance, Object newInstance) {
		return oldInstance == newInstance;
	}

	protected Expression instantiate(Object oldInstance, Encoder out) {
		Enum<?> e = (Enum<?>) oldInstance;
		return new Expression(e, e.getClass(), "valueOf", new Object[] { e.name() });
	}
}
