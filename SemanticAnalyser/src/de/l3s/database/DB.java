/**
 * 
 */
package de.l3s.database;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author tereza
 *
 */
public class DB {
	private static final String BUNDLE_NAME = "de.l3s.database.db"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private DB() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
