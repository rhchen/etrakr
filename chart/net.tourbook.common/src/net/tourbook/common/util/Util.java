/*******************************************************************************
 * Copyright (C) 2005, 2014  Wolfgang Schramm and Contributors
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA
 *******************************************************************************/
package net.tourbook.common.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;

import net.tourbook.common.UI;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.joda.time.DateTime;
import org.xml.sax.Attributes;

public class Util {

	private static final String	URL_SPACE								= " ";					//$NON-NLS-1$
	private static final String	URL_SPACE_REPLACEMENT					= "%20";				//$NON-NLS-1$

//	public static final String	UNIQUE_ID_SUFFIX_CICLO_TOUR				= "83582";				//$NON-NLS-1$
	public static final String	UNIQUE_ID_SUFFIX_GARMIN_FIT				= "12653";				//$NON-NLS-1$
	public static final String	UNIQUE_ID_SUFFIX_GARMIN_TCX				= "42984";				//$NON-NLS-1$
	public static final String	UNIQUE_ID_SUFFIX_GPX					= "31683";				//$NON-NLS-1$
	public static final String	UNIQUE_ID_SUFFIX_NMEA					= "32481";				//$NON-NLS-1$
	public static final String	UNIQUE_ID_SUFFIX_POLAR_HRM				= "63193";				//$NON-NLS-1$
	public static final String	UNIQUE_ID_SUFFIX_POLAR_PDD				= "76913";				//$NON-NLS-1$
	public static final String	UNIQUE_ID_SUFFIX_POLAR_TRAINER			= "13457";				//$NON-NLS-1$
	public static final String	UNIQUE_ID_SUFFIX_SPORT_TRACKS_FITLOG	= "24168";				//$NON-NLS-1$
	public static final String	UNIQUE_ID_SUFFIX_SUUNTO2				= "92145";				//$NON-NLS-1$
	public static final String	UNIQUE_ID_SUFFIX_SUUNTO3				= "73198";				//$NON-NLS-1$

	/*
	 * default xml attributes
	 */
	public static final String	ATTR_ROOT_DATETIME						= "Created";			//$NON-NLS-1$
	public static final String	ATTR_ROOT_VERSION_MAJOR					= "VersionMajor";		//$NON-NLS-1$
	public static final String	ATTR_ROOT_VERSION_MINOR					= "VersionMinor";		//$NON-NLS-1$
	public static final String	ATTR_ROOT_VERSION_MICRO					= "VersionMicro";		//$NON-NLS-1$
	public static final String	ATTR_ROOT_VERSION_QUALIFIER				= "VersionQualifier";	//$NON-NLS-1$

	public static final String	ATTR_COLOR_RED							= "red";				//$NON-NLS-1$
	public static final String	ATTR_COLOR_GREEN						= "green";				//$NON-NLS-1$
	public static final String	ATTR_COLOR_BLUE							= "blue";				//$NON-NLS-1$

	public static int adjustScaleValueOnMouseScroll(final MouseEvent event) {

		// accelerate with Ctrl + Shift key
		final int accelerator = getKeyAccelerator(event);

		final Scale scale = (Scale) event.widget;
		final int newValueDiff = event.count > 0 ? accelerator : -accelerator;

		return scale.getSelection() + newValueDiff;
	}

	public static void adjustSpinnerValueOnMouseScroll(final MouseEvent event) {

		final int accelerator = getKeyAccelerator(event);

		final Spinner spinner = (Spinner) event.widget;
		final int newValue = ((event.count > 0 ? 1 : -1) * accelerator);

		spinner.setSelection(spinner.getSelection() + newValue);
	}

	public static void closeReader(final BufferedReader reader) {

		if (reader != null) {
			try {
				reader.close();
			} catch (final IOException e) {
				StatusUtil.showStatus(e);
			}
		}
	}

	public static void closeSql(final Connection conn) {

		if (conn != null) {
			try {
				conn.close();
			} catch (final SQLException e) {
				SQLUtils.showSQLException(e);
			}
		}
	}

	public static void closeSql(final Statement stmt) {

		if (stmt != null) {
			try {
				stmt.close();
			} catch (final SQLException e) {
				SQLUtils.showSQLException(e);
			}
		}
	}

	public static void closeWriter(final Writer writer) {

		if (writer != null) {
			try {
				writer.close();
			} catch (final Exception e) {
				StatusUtil.showStatus(e);
			}
		}
	}

	/**
	 * @param text
	 * @return Returns MD5 for the text or <code>null</code> when an error occures.
	 */
	public static String computeMD5(final String text) {

		MessageDigest md5Checker;
		try {
			md5Checker = MessageDigest.getInstance("MD5"); //$NON-NLS-1$
		} catch (final NoSuchAlgorithmException e) {
			return null;
		}

		final byte[] textBytes = text.getBytes();
		for (final byte textByte : textBytes) {
			md5Checker.update(textByte);
		}

		final byte[] digest = md5Checker.digest();

		final StringBuilder buf = new StringBuilder();
		for (final byte element : digest) {
			if ((element & 0xFF) < 0x10) {
				buf.append('0');
			}
			buf.append(Integer.toHexString(element & 0xFF));
		}

		return buf.toString();
	}

	public static float[][] convertDoubleToFloat(final double[][] doubleSeries) {

		final float[][] floatSeries = new float[doubleSeries.length][];

		for (int serieIndex = 0; serieIndex < doubleSeries.length; serieIndex++) {

			final double[] doubleValues = doubleSeries[serieIndex];
			if (doubleValues != null) {

				final float[] floatSerie = floatSeries[serieIndex] = new float[doubleValues.length];

				for (int floatIndex = 0; floatIndex < floatSerie.length; floatIndex++) {
					floatSeries[serieIndex][floatIndex] = (float) doubleValues[floatIndex];
				}
			}
		}
		return floatSeries;
	}

	public static double[] convertIntToDouble(final int[] intValues) {

		if (intValues == null) {
			return null;
		}

		if (intValues.length == 0) {
			return new double[0];
		}

		final double[] doubleValues = new double[intValues.length];

		for (int valueIndex = 0; valueIndex < intValues.length; valueIndex++) {
			doubleValues[valueIndex] = intValues[valueIndex];
		}

		return doubleValues;
	}

	public static float[] convertIntToFloat(final int[] intValues) {

		if (intValues == null) {
			return null;
		}

		if (intValues.length == 0) {
			return new float[0];
		}

		final float[] floatValues = new float[intValues.length];

		for (int valueIndex = 0; valueIndex < intValues.length; valueIndex++) {
			floatValues[valueIndex] = intValues[valueIndex];
		}

		return floatValues;
	}

	public static float[][] convertIntToFloat(final int[][] intValues) {

		if (intValues == null) {
			return null;
		}

		if (intValues.length == 0 || intValues[0].length == 0) {
			return new float[0][0];
		}

		final float[][] floatValues = new float[intValues.length][intValues[0].length];

		for (int outerIndex = 0; outerIndex < intValues.length; outerIndex++) {

			final int[] intOuterValues = intValues[outerIndex];
			final float[] floutOuterValues = floatValues[outerIndex];

			for (int innerIndex = 0; innerIndex < intOuterValues.length; innerIndex++) {
				floutOuterValues[innerIndex] = intOuterValues[innerIndex];
			}
		}

		return floatValues;
	}

	/**
	 * @param sourceString
	 * @param lookFor
	 * @return Returns the number of characters which are found in the string or -1 when the string
	 *         is <code>null</code>
	 */
	public static int countCharacter(final String sourceString, final char lookFor) {

		if (sourceString == null) {
			return -1;
		}

		int count = 0;

		for (int i = 0; i < sourceString.length(); i++) {
			final char c = sourceString.charAt(i);
			if (c == lookFor) {
				count++;
			}
		}

		return count;
	}

	/**
	 * Creates a {@link DateTime} from the number: YYYYMMDDhhmmss
	 * 
	 * @param yyyymmddhhmmss
	 * @return
	 */
	public static DateTime createDateTimeFromYMDhms(final long yyyymmddhhmmss) {

		final int year = (int) (yyyymmddhhmmss / 10000000000L) % 10000;
		final int month = (int) (yyyymmddhhmmss / 100000000) % 100;
		final int day = (int) (yyyymmddhhmmss / 1000000) % 100;
		final int hour = (int) (yyyymmddhhmmss / 10000) % 100;
		final int minute = (int) (yyyymmddhhmmss / 100 % 100);
		final int second = (int) (yyyymmddhhmmss % 100);

		return new DateTime(year, month, day, hour, minute, second, 0);
	}

	/**
	 * creates a double array backup
	 * 
	 * @param original
	 * @return Returns a copy of a <code>double[]</code> or <code>null</code> when the original data
	 *         is <code>null</code>.
	 */
	public static double[] createDoubleCopy(final double[] original) {

		double[] backup = null;

		if (original != null) {
			final int serieLength = original.length;
			backup = new double[serieLength];
			System.arraycopy(original, 0, backup, 0, serieLength);
		}

		return backup;
	}

	/**
	 * creates a float array backup
	 * 
	 * @param original
	 * @return Returns a copy of a <code>float[]</code> or <code>null</code> when the original data
	 *         is <code>null</code>.
	 */
	public static float[] createFloatCopy(final float[] original) {

		float[] backup = null;

		if (original != null) {
			final int serieLength = original.length;
			backup = new float[serieLength];
			System.arraycopy(original, 0, backup, 0, serieLength);
		}

		return backup;
	}

	/**
	 * creates a int array backup
	 * 
	 * @param original
	 * @return the backup array or <code>null</code> when the original data is <code>null</code>
	 */
	public static int[] createIntegerCopy(final int[] original) {

		int[] backup = null;

		if (original != null) {
			final int serieLength = original.length;
			backup = new int[serieLength];
			System.arraycopy(original, 0, backup, 0, serieLength);
		}

		return backup;
	}

	public static void delayThread(final int delayTime) {
		try {
			Thread.sleep(delayTime);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void deleteTempFile(final File tempFile) {

		if (tempFile == null || tempFile.exists() == false) {
			return;
		}

		try {

			if (tempFile.delete() == false) {
				StatusUtil.log(String.format("Temp file cannot be deleted: %s", tempFile.getAbsolutePath())); //$NON-NLS-1$
			}

		} catch (final SecurityException e) {
			StatusUtil.showStatus(String.format("Temp file cannot be deleted: %s", tempFile.getAbsolutePath())); //$NON-NLS-1$
		}
	}

	public static Resource disposeResource(final Resource resource) {
		if (resource != null && !resource.isDisposed()) {
			resource.dispose();
		}
		return null;
	}

	public static String dumpMemento(final XMLMemento xmlMemento) {

		final StringBuilder sb = new StringBuilder();
		for (final String attr : xmlMemento.getAttributeKeys()) {
			sb.append(attr);
			sb.append(UI.SYMBOL_EQUAL);
			sb.append(xmlMemento.getString(attr));
			sb.append(UI.COMMA_SPACE);
		}

		return sb.toString();
	}

	public static String encodeSpace(final String urlString) {
		return urlString.replaceAll(URL_SPACE, URL_SPACE_REPLACEMENT);
	}

	public static boolean getBoolean(final Object data) {

		boolean is = false;

		if (data instanceof Boolean) {
			is = (Boolean) data;
		}

		return is;
	}

	private static int getKeyAccelerator(final MouseEvent event) {

		boolean isCtrlKey;
		boolean isShiftKey;

		if (UI.IS_OSX) {
			isCtrlKey = (event.stateMask & SWT.MOD1) > 0;
			isShiftKey = (event.stateMask & SWT.MOD3) > 0;
//			isAltKey = (event.stateMask & SWT.MOD3) > 0;
		} else {
			isCtrlKey = (event.stateMask & SWT.MOD1) > 0;
			isShiftKey = (event.stateMask & SWT.MOD2) > 0;
//			isAltKey = (event.stateMask & SWT.MOD3) > 0;
		}

		// accelerate with Ctrl + Shift key
		int accelerator = isCtrlKey ? 10 : 1;
		accelerator *= isShiftKey ? 5 : 1;

		return accelerator;
	}

	public static double getMajorDecimalValue(final double graphUnit) {

		double unit = graphUnit;
		int multiplier = 1;

		while (unit > 1000) {
			multiplier *= 10;
			unit /= 10;
		}

		unit = graphUnit / multiplier;

		unit = //
		unit == 1000 ? 5000 : unit == 500 ? 1000 : unit == 200 ? 1000 : //
				unit == 100 ? 500 : unit == 50 ? 200 : unit == 20 ? 100 : //
						unit == 10 ? 50 : unit == 5 ? 20 : unit == 2 ? 10 : //
								unit == 1 ? 5 : unit == 0.5 ? 2.0 : unit == 0.2 ? 1 : //
										unit == 0.1 ? 0.5 : unit == 0.05 ? 0.2 : unit == 0.02 ? 0.1 : //
												unit == 0.01 ? 0.05 : unit == 0.005 ? 0.02 : unit == 0.002
														? 0.01
														: 0.005;

		unit *= multiplier;

		return unit;
	}

	public static long getMajorSimpleNumberValue(final long graphUnit) {

		double unit = graphUnit;
		int multiplier = 1;

		while (unit > 1000) {
			multiplier *= 10;
			unit /= 10;
		}

		unit = graphUnit / multiplier;

		unit = //
		unit == 1000 ? 5000 : //
				unit == 500 ? 1000 : //
						unit == 200 ? 1000 : //
								unit == 100 ? 500 : //
										unit == 50 ? 200 : //
												unit == 20 ? 100 : //
														unit == 10 ? 50 : //
																unit == 5 ? 20 : //
																		unit == 2 ? 10 : //
																				unit == 1 ? 5 : //
																						1;

		unit *= multiplier;

		return (long) unit;
	}

	/**
	 * @param unitValue
	 * @param is24HourFormatting
	 * @return Returns minUnitValue rounded to the number 60/30/20/10/5/2/1
	 */
	public static long getMajorTimeValue(final long unitValue, final boolean is24HourFormatting) {

		long unit = unitValue;
		int multiplier = 1;

		while (unit > 240) {
			multiplier *= 60;
			unit /= 60;
		}

		if (is24HourFormatting) {

			if (multiplier >= 3600) {

				unit = //
						//
				unit >= 360 ? 3600 : //
						unit >= 60 ? 360 : //
								unit >= 15 ? 60 : //
										12;

			} else {

				unit = //
						//
				unit >= 120 ? 720 : //
						unit >= 30 ? 360 : //
								unit >= 15 ? 120 : //
										unit >= 10 ? 60 : //
												unit >= 5 ? 30 : //
														unit >= 2 ? 10 : //
																5;
			}

		} else {

			if (multiplier >= 3600) {

				unit = //
						//
				unit >= 720 ? 3600 : //
						unit >= 240 ? 720 : //
								unit >= 120 ? 240 : //
										unit >= 24 ? 120 : //
												10;
			} else {

				// multiplier < 1 hour (3600 sec)

				unit = //
						//
				unit >= 120 ? 600 : //
						unit >= 60 ? 300 : //
								unit >= 30 ? 120 : //
										unit >= 15 ? 60 : //
												unit >= 10 ? 60 : //
														unit >= 5 ? 20 : //
																10;
			}
		}

		unit *= multiplier;

		return unit;
	}

	/**
	 * @param defaultUnitValue
	 * @param is24HourFormatting
	 * @return Returns minUnitValue rounded to the number 60/30/20/10/5/2/1
	 */
	public static long getMajorTimeValue24(final long defaultUnitValue) {

		double unit = defaultUnitValue;
		int multiplier = 1;

		while (unit > 3600) {
			multiplier *= 3600;
			unit /= 3600;
		}

		double unitRounded = unit;

		if (multiplier >= 3600) {

			unitRounded = //
			//
			unitRounded >= 6 ? 24 : //
					unitRounded >= 2 ? 12 : //
							unitRounded >= 1 ? 6 : //
									1;

		} else {

			unitRounded = //
			//
			unitRounded >= 3600 ? 3600 * 6 : unitRounded >= 1800 ? 3600 * 3 : unitRounded >= 1200 ? 7200 : //
					unitRounded >= 600 ? 3600 : //
							unitRounded >= 300 ? 1800 : //
									unitRounded >= 120 ? 600 : //
											unitRounded >= 60 ? 300 : //
													unitRounded >= 30 ? 180 : //
															unitRounded >= 20 ? 60 : //
																	unitRounded >= 10 ? 30 : //
																			unitRounded >= 5 ? 20 : //
																					unitRounded >= 2 ? 10 : //
																							5;
		}

		final long unitFinal = (long) (unitRounded * multiplier);

//		System.out.println(UI.timeStampNano());
//		System.out.println(UI.timeStampNano() + ("\tdefaultUnitValue\t" + defaultUnitValue));
//		System.out.println(UI.timeStampNano() + ("\tunit\t\t\t" + unit));
//		System.out.println(UI.timeStampNano() + ("\tunitRounded\t\t" + unitRounded));
//		System.out.println(UI.timeStampNano() + ("\tunitFinal\t\t" + unitFinal));
//		System.out.println(UI.timeStampNano() + ("\tmultiplier\t\t" + multiplier));
//		// TODO remove SYSTEM.OUT.PRINTLN

		return unitFinal;
	}

	public static int getNumberOfDigits(int number) {

		int counter = 0;

		while (number > 0) {
			counter++;
			number = number / 10;
		}

		return counter;
	}

	public static String getSQLExceptionText(final SQLException e) {

		final StringBuilder sb = new StringBuilder()//
				.append("SQLException") //$NON-NLS-1$
				.append(UI.NEW_LINE2)
				.append("SQLState: " + (e).getSQLState()) //$NON-NLS-1$
				.append(UI.NEW_LINE)
				.append("Severity: " + (e).getErrorCode()) //$NON-NLS-1$
				.append(UI.NEW_LINE)
				.append("Message: " + (e).getMessage()) //$NON-NLS-1$
				.append(UI.NEW_LINE);

		return sb.toString();
	}

	/**
	 * @param state
	 * @param key
	 * @param defaultValue
	 * @return Returns a string value from {@link IDialogSettings}. When the key is not found, the
	 *         default value is returned.
	 */
	public static String[] getStateArray(final IDialogSettings state, final String key, final String[] defaultValue) {

		if (state == null) {
			return defaultValue;
		}

		final String[] stateValue = state.getArray(key);

		return stateValue == null ? defaultValue : stateValue;
	}

	/**
	 * @param state
	 * @param key
	 * @param defaultValue
	 * @return Returns a boolean value from {@link IDialogSettings}. When the key is not found, the
	 *         default value is returned.
	 */
	public static boolean getStateBoolean(final IDialogSettings state, final String key, final boolean defaultValue) {

		if (state == null) {
			return defaultValue;
		}

		return state.get(key) == null ? defaultValue : state.getBoolean(key);
	}

	/**
	 * @param state
	 * @param stateKey
	 * @param defaultValue
	 * @param combo
	 * @param defaultComboIndex
	 *            Combo default index when retrieved state index is too large.
	 * @return
	 */
	public static int getStateCombo(final IDialogSettings state,
									final String stateKey,
									final int defaultValue,
									final Combo combo,
									final int defaultComboIndex) {

		int comboIndex = getStateInt(state, stateKey, defaultValue);

		final int maxItems = combo.getItemCount();

		if (comboIndex >= maxItems) {

			// combo index is too large

			if (defaultComboIndex >= maxItems) {
				comboIndex = 0;
			} else {
				comboIndex = defaultComboIndex;
			}
		}

		return comboIndex;
	}

	/**
	 * @param state
	 * @param key
	 * @param defaultValue
	 * @return Returns a float value from {@link IDialogSettings}. When the key is not found, the
	 *         default value is returned.
	 */
	public static double getStateDouble(final IDialogSettings state, final String key, final double defaultValue) {

		if (state == null) {
			return defaultValue;
		}

		try {
			return state.get(key) == null ? defaultValue : state.getDouble(key);
		} catch (final NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * @param state
	 * @param key
	 * @param defaultValue
	 * @return Returns a float value from {@link IDialogSettings}. When the key is not found, the
	 *         default value is returned.
	 */
	public static float getStateFloat(final IDialogSettings state, final String key, final float defaultValue) {

		if (state == null) {
			return defaultValue;
		}

		try {
			return state.get(key) == null ? defaultValue : state.getFloat(key);
		} catch (final NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * @param combo
	 *            combo box, the items in the combo box must correspond to the items in the states
	 *            array
	 * @param states
	 *            array which contains all states
	 * @param defaultState
	 *            state when an item is not selected in the combo box
	 * @return Returns the state which is selected in the combo box
	 */
	public static String getStateFromCombo(final Combo combo, final String[] states, final String defaultState) {

		final int selectedIndex = combo.getSelectionIndex();

		String selectedState;

		if (selectedIndex == -1) {
			selectedState = defaultState;
		} else {
			selectedState = states[selectedIndex];
		}

		return selectedState;
	}

	/**
	 * @param state
	 * @param key
	 * @param defaultValue
	 * @return Returns an integer value from {@link IDialogSettings}. When the key is not found, the
	 *         default value is returned.
	 */
	public static int getStateInt(final IDialogSettings state, final String key, final int defaultValue) {

		if (state == null) {
			return defaultValue;
		}

		try {
			return state.get(key) == null ? defaultValue : state.getInt(key);
		} catch (final NumberFormatException e) {
			return defaultValue;
		}
	}

	public static int[] getStateIntArray(final IDialogSettings state, final String key, final int[] defaultValue) {

		if (state == null) {
			return defaultValue;
		}

		final String[] stringValues = state.getArray(key);

		if (stringValues == null) {
			return defaultValue;
		}

		final ArrayList<Integer> intValues = new ArrayList<Integer>();

		for (final String stringValue : stringValues) {

			try {

				final int intValue = Integer.parseInt(stringValue);

				intValues.add(intValue);

			} catch (final NumberFormatException e) {
				// just ignore
			}
		}

		int intIndex = 0;
		final int[] intintValues = new int[intValues.size()];
		for (final Integer intValue : intValues) {
			intintValues[intIndex++] = intValue;
		}

		return intintValues;
	}

	/**
	 * @param state
	 * @param key
	 * @param defaultValue
	 * @return Returns a long value from {@link IDialogSettings}. When the key is not found, the
	 *         default value is returned.
	 */
	public static long getStateLong(final IDialogSettings state, final String key, final long defaultValue) {

		if (state == null) {
			return defaultValue;
		}

		try {
			return state.get(key) == null ? defaultValue : state.getLong(key);
		} catch (final NumberFormatException e) {
			return defaultValue;
		}
	}

	public static long[] getStateLongArray(final IDialogSettings state, final String key, final long[] defaultValue) {

		if (state == null) {
			return defaultValue;
		}

		final String[] stringValues = state.getArray(key);

		if (stringValues == null) {
			return defaultValue;
		}

		final ArrayList<Long> longValues = new ArrayList<Long>();

		for (final String stringValue : stringValues) {

			try {

				final long longValue = Long.parseLong(stringValue);

				longValues.add(longValue);

			} catch (final NumberFormatException e) {
				// just ignore
			}
		}

		int intIndex = 0;
		final long[] longlongValues = new long[longValues.size()];
		for (final Long longValue : longValues) {
			longlongValues[intIndex++] = longValue;
		}

		return longlongValues;
	}

	public static RGB getStateRGB(final IDialogSettings state, final String key, final RGB defaultRGB) {

		if (state == null) {
			return defaultRGB;
		}

		final int[] defaultValue = { defaultRGB.red, defaultRGB.green, defaultRGB.blue };

		final int[] colorValues = getStateIntArray(state, key, defaultValue);

		return new RGB(colorValues[0], colorValues[1], colorValues[2]);
	}

	/**
	 * @param state
	 * @param key
	 * @param defaultValue
	 * @return Returns a string value from {@link IDialogSettings}. When the key is not found, the
	 *         default value is returned.
	 */
	public static String getStateString(final IDialogSettings state, final String key, final String defaultValue) {

		if (state == null) {
			return defaultValue;
		}

		final String stateValue = state.get(key);

		return stateValue == null ? defaultValue : stateValue;
	}

	/**
	 * Remove duplicate items from a list and add a new item.
	 * 
	 * @param allItems
	 * @param newItem
	 * @param maxItems
	 * @return
	 */
	public static String[] getUniqueItems(final String[] allItems, final String newItem, final int maxItems) {

		final ArrayList<String> newItems = new ArrayList<String>();

		newItems.add(newItem);

		for (final String oldItem : allItems) {

			// ignore duplicate entries
			if (newItem.equals(oldItem) == false) {
				newItems.add(oldItem);
			}

			if (maxItems > 0) {
				if (newItems.size() >= maxItems) {
					break;
				}
			}
		}

		return newItems.toArray(new String[newItems.size()]);
	}

	public static long getValueScaling(final double graphUnit) {

		if (graphUnit > 1 || graphUnit < 1) {

			double scaledValue = 1;

			if (graphUnit < 1) {
				scaledValue = 1 / graphUnit;
			} else {
				scaledValue = graphUnit;
			}

			long valueScale = 1;

			while (scaledValue > 1) {
				valueScale *= 10;
				scaledValue /= 10;
			}

			// add an additional factor to prevent rounding problems
			valueScale *= 1000;

			return valueScale;

		} else {

			// add an additional factor to prevent rounding problems
			return 1000;
		}
	}

	public static boolean getXmlBoolean(final IMemento xmlMemento, final String key, final Boolean defaultValue) {

		Boolean value = xmlMemento.getBoolean(key);

		if (value == null) {
			value = defaultValue;
		}

		return value;
	}

	private static Float getXmlFloat(final IMemento xmlMemento, final String key, final Float defaultValue) {

		Float value = xmlMemento.getFloat(key);

		if (value == null) {
			value = defaultValue;
		}

		return value;
	}

	/**
	 * @param xmlMemento
	 * @param key
	 * @param defaultValue
	 * @param minValue
	 *            Float min value.
	 * @param maxValue
	 *            Float max value.
	 * @return
	 */
	public static float getXmlFloatFloat(	final XMLMemento xmlMemento,
											final String key,
											final float defaultValue,
											final float minValue,
											final float maxValue) {

		final Float value = getXmlFloat(xmlMemento, key, defaultValue);

		if (value < minValue) {
			return minValue;
		}

		if (value > maxValue) {
			return maxValue;
		}

		return value;
	}

	/**
	 * @param xmlMemento
	 * @param key
	 * @param defaultValue
	 * @param minValue
	 *            Integer min value.
	 * @param maxValue
	 *            Integer max value.
	 * @return
	 */
	public static float getXmlFloatInt(	final IMemento xmlMemento,
										final String key,
										final float defaultValue,
										final int minValue,
										final int maxValue) {

		final Float value = getXmlFloat(xmlMemento, key, defaultValue);

		if (value < minValue) {
			return minValue;
		}

		if (value > maxValue) {
			return maxValue;
		}

		return value;
	}

	public static int getXmlInteger(final IMemento xmlMemento,
									final String key,
									final int defaultValue,
									final int minValue,
									final int maxValue) {

		final int value = getXmlInteger(xmlMemento, key, defaultValue);

		if (value < minValue) {
			return minValue;
		}

		if (value > maxValue) {
			return maxValue;
		}

		return value;
	}

	public static int getXmlInteger(final IMemento xmlMemento, final String key, final Integer defaultValue) {

		Integer value = xmlMemento.getInteger(key);

		if (value == null) {
			value = defaultValue;
		}

		return value;
	}

	/**
	 * @param xmlMemento
	 * @param defaultValue
	 * @return Returns {@link RGB} from the attributes red, green and blue attributes.
	 */
	public static RGB getXmlRgb(final IMemento xmlMemento, final RGB defaultValue) {

		final int red = getXmlInteger(xmlMemento, ATTR_COLOR_RED, defaultValue.red);
		final int green = getXmlInteger(xmlMemento, ATTR_COLOR_GREEN, defaultValue.green);
		final int blue = getXmlInteger(xmlMemento, ATTR_COLOR_BLUE, defaultValue.blue);

		return new RGB(red, green, blue);
	}

	public static String getXmlString(final IMemento xmlConfig, final String key, final String defaultValue) {

		String value = xmlConfig.getString(key);

		if (value == null) {
			value = defaultValue;
		}

		return value;
	}

	/**
	 * found here: http://www.odi.ch/prog/design/datetime.php
	 * 
	 * @param cal
	 * @return
	 */
	public static int getYearForWeek(final Calendar cal) {

		final int year = cal.get(Calendar.YEAR);
		final int week = cal.get(Calendar.WEEK_OF_YEAR);
		final int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

		if (week == 1 && dayOfMonth > 20) {
			return year + 1;
		}

		if (week >= 52 && dayOfMonth < 10) {
			return year - 1;
		}

		return year;
	}

	/**
	 * @param event
	 * @return Returns <code>true</code> when the <Ctrl> or <Command> key is pressed.
	 */
	public static boolean isCtrlKeyPressed(final SelectionEvent event) {

		boolean isCtrl;

		if (UI.IS_OSX) {
			isCtrl = (event.stateMask & SWT.COMMAND) != 0;
		} else {
			isCtrl = (event.stateMask & SWT.MOD1) != 0;
		}

		return isCtrl;
	}

	/**
	 * @param fileName
	 * @return Returns <code>true</code> when fileName is a valid directory
	 */
	public static boolean isDirectory(String fileName) {

		fileName = fileName.trim();
		final File file = new File(fileName);

		return file.isDirectory();
	}

	/**
	 * Open link in the browser
	 */
	public static void openLink(final Shell shell, String href) {

		// format the href for an html file (file:///<filename.html>
		// required for Mac only.
		if (href.startsWith("file:")) { //$NON-NLS-1$
			href = href.substring(5);
			while (href.startsWith("/")) { //$NON-NLS-1$
				href = href.substring(1);
			}
			href = "file:///" + href; //$NON-NLS-1$
		}

		final IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();

		try {

			final IWebBrowser browser = support.getExternalBrowser();
			browser.openURL(new URL(urlEncodeForSpaces(href.toCharArray())));

		} catch (final MalformedURLException e) {
			StatusUtil.showStatus(e);
		} catch (final PartInitException e) {
			StatusUtil.showStatus(e);
		}
	}

	/**
	 * Parses SAX attribute
	 * 
	 * @param attributes
	 * @param attributeName
	 * @return Returns double value or {@link Double#MIN_VALUE} when attribute is not available or
	 *         cannot be parsed.
	 */
	public static double parseDouble(final Attributes attributes, final String attributeName) {

		try {
			final String valueString = attributes.getValue(attributeName);
			if (valueString != null) {
				return Double.parseDouble(valueString);
			}
		} catch (final NumberFormatException e) {
			// do nothing
		}
		return Double.MIN_VALUE;
	}

	public static double parseDouble(final String textValue) {

		try {
			if (textValue != null) {
				return Double.parseDouble(textValue);
			} else {
				return Double.MIN_VALUE;
			}

		} catch (final NumberFormatException e) {
			return Double.MIN_VALUE;
		}
	}

	/**
	 * Parses SAX attribute
	 * 
	 * @param attributes
	 * @param attributeName
	 * @return Returns float value or {@link Float#MIN_VALUE} when attribute is not available or
	 *         cannot be parsed.
	 */
	public static float parseFloat(final Attributes attributes, final String attributeName) {

		try {
			final String valueString = attributes.getValue(attributeName);
			if (valueString != null) {
				return Float.parseFloat(valueString);
			}
		} catch (final NumberFormatException e) {
			// do nothing
		}
		return Float.MIN_VALUE;
	}

	public static float parseFloat(final String textValue) {

		try {
			if (textValue != null) {
				return Float.parseFloat(textValue);
			} else {
				return Float.MIN_VALUE;
			}

		} catch (final NumberFormatException e) {
			return Float.MIN_VALUE;
		}
	}

	/**
	 * Parses SAX attribute
	 * 
	 * @param attributes
	 * @param attributeName
	 * @return Returns integer value or {@link Integer#MIN_VALUE} when attribute is not available or
	 *         cannot be parsed.
	 */
	public static int parseInt(final Attributes attributes, final String attributeName) {

		try {
			final String valueString = attributes.getValue(attributeName);
			if (valueString != null) {
				return Integer.parseInt(valueString);
			}
		} catch (final NumberFormatException e) {
			// do nothing
		}
		return Integer.MIN_VALUE;
	}

	/**
	 * Parses SAX attribute
	 * 
	 * @param attributes
	 * @param attributeName
	 * @return Returns integer value or 0 when attribute is not available or cannot be parsed.
	 */
	public static int parseInt0(final Attributes attributes, final String attributeName) {

		try {
			final String valueString = attributes.getValue(attributeName);
			if (valueString != null) {
				return Integer.parseInt(valueString);
			}
		} catch (final NumberFormatException e) {
			// do nothing
		}
		return 0;
	}

	/**
	 * Parses SAX attribute
	 * 
	 * @param attributes
	 * @param attributeName
	 * @return Returns long value or {@link Long#MIN_VALUE} when attribute is not available or
	 *         cannot be parsed.
	 */
	public static long parseLong(final Attributes attributes, final String attributeName) {

		try {
			final String valueString = attributes.getValue(attributeName);
			if (valueString != null) {
				return Long.parseLong(valueString);
			}
		} catch (final NumberFormatException e) {
			// do nothing
		}
		return Long.MIN_VALUE;
	}

	/**
	 * Load a text file.
	 * 
	 * @param fileName
	 * @return Returns the content from a text file
	 */
	public static String readContentFromFile(final String fileName) {

		String content = UI.EMPTY_STRING;

		try {

			final FileInputStream stream = new FileInputStream(fileName);

			content = readContentFromStream(stream);

		} catch (final FileNotFoundException e) {
			StatusUtil.showStatus(e);
		} catch (final IOException e) {
			StatusUtil.showStatus(e);
		}

		return content;
	}

	/**
	 * To convert the InputStream to String we use the BufferedReader.readLine() method. We iterate
	 * until the BufferedReader return null which means there's no more data to read. Each line will
	 * appended to a StringBuilder and returned as String.
	 */
	public static String readContentFromStream(final InputStream inputStream) throws IOException {

		if (inputStream != null) {

			final StringBuilder sb = new StringBuilder();
			String line;

			try {

				final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, UI.UTF_8));

				while ((line = reader.readLine()) != null) {
					sb.append(line).append(UI.NEW_LINE);
				}
			} finally {
				inputStream.close();
			}
			return sb.toString();
		} else {
			return UI.EMPTY_STRING;
		}
	}

	/**
	 * @param unitValue
	 * @return Returns unit value rounded to the number of 50/20/10/5/2/1
	 */
	public static double roundDecimalValue(final double unitValue) {

		double unit = unitValue;
		int multiplier = 1;

		while (unit > 100) {
			multiplier *= 10;
			unit /= 10;
		}

//		unit = unitValue / multiplier;

		unit = unit > 50 ? 50 : //
				unit > 20 ? 20 : //
						unit > 10 ? 10 : //
								unit > 5 ? 5 : //
										unit > 2 ? 2 : //
												unit > 1 ? 1 : //
														unit > 0.5 ? 0.5 : //
																unit > 0.2 ? 0.2 : //
																		unit > 0.1 ? 0.1 : //
																				unit > 0.05 ? 0.05 : //
																						unit > 0.02 ? 0.02 : //
																								0.01;

		unit *= multiplier;

		return unit;
	}

	/**
	 * Round floating value to an inverse long value.
	 * 
	 * @param graphValue
	 * @param graphUnit
	 * @return
	 */
	public static long roundFloatToUnitInverse(final float graphValue, final float graphUnit) {

		if (graphUnit < 1) {

			if (graphValue < 0) {

				final float value1 = graphValue / graphUnit;
				final float value2 = value1 - 0.5f;
				final long value3 = (long) (value2);

				return value3;

			} else {

				final float value1 = graphValue / graphUnit;
				final float value2 = value1 + 0.5f;
				final long value3 = (long) (value2);

				return value3;
			}

		} else {

			// graphUnit > 1

			return (long) (graphValue * graphUnit);
		}
	}

	/**
	 * Round number of units to a 'month suitable' format.
	 * 
	 * @param defaultUnitValue
	 * @return
	 */
	public static int roundMonthUnits(final int defaultUnitValue) {

		float unit = defaultUnitValue;

		while (unit > 144) {
			unit /= 12;
		}

		unit = //
				//
		unit >= 12 ? 12 : //
				unit >= 6 ? 6 : //
						unit >= 4 ? 4 : //
								unit >= 3 ? 3 : //
										unit >= 2 ? 2 : //
												1;
		return (int) unit;
	}

	public static int roundSimpleNumberUnits(final long graphDefaultUnit) {

		float unit = graphDefaultUnit;
		int multiplier = 1;

		while (unit > 20) {
			multiplier *= 10;
			unit /= 10;
		}

		unit = //
				//
		unit >= 10 ? 10 : //
				unit >= 5 ? 5 : //
						unit >= 2 ? 2 : //
								1;

		unit *= multiplier;

		return (int) unit;
	}

	/**
	 * @param defaultUnitValue
	 * @return Returns unit rounded to the number 60/30/20/10/5/2/1
	 */
	public static long roundTime24h(final long defaultUnitValue) {

		float unit = defaultUnitValue;
		int multiplier = 1;

		while (unit > 3600) {
			multiplier *= 3600;
			unit /= 3600;
		}

		float unitRounded = unit;

		if (multiplier >= 3600) {

			// > 1h

			unitRounded = //
							//
			unitRounded >= 24 ? 48 : //
					unitRounded >= 12 ? 24 : //
							unitRounded >= 6 ? 12 : //
									unitRounded >= 3 ? 6 : //
											unitRounded >= 2 ? 2 : //
													1;

		} else {

			// <  1h

			unitRounded = //
							//
			unitRounded >= 1800 ? 1800 : //
					unitRounded >= 1200 ? 1200 : //
							unitRounded >= 600 ? 600 : //
									unitRounded >= 240 ? 300 : //
											unitRounded >= 120 ? 120 : //
													unitRounded >= 60 ? 60 : //
															unitRounded >= 30 ? 30 : //
																	unitRounded >= 10 ? 20 : //
																			unitRounded >= 5 ? 10 : //
																					unitRounded >= 2 ? 5 : //
																							unitRounded > 1 ? 2 : //
																									1;
		}

		final long unitFinal = (long) (unitRounded * multiplier);

//		System.out.println(UI.timeStampNano());
//		System.out.println(UI.timeStampNano() + ("\tdefaultUnitValue\t" + defaultUnitValue));
//		System.out.println(UI.timeStampNano() + ("\tunit\t\t\t" + unit));
//		System.out.println(UI.timeStampNano() + ("\tunitRounded\t\t" + unitRounded));
//		System.out.println(UI.timeStampNano() + ("\tunitFinal\t\t" + unitFinal));
//		System.out.println(UI.timeStampNano() + ("\tmultiplier\t\t" + multiplier));
//		// TODO remove SYSTEM.OUT.PRINTLN

		return unitFinal;
	}

	/**
	 * @param defaultUnitValue
	 * @param is24HourFormatting
	 * @return Returns minUnitValue rounded to the number 60/30/20/10/5/2/1
	 */
	public static float roundTimeValue(final float defaultUnitValue, final boolean is24HourFormatting) {

		float unit = defaultUnitValue;
		int multiplier = 1;

		if (is24HourFormatting) {

			while (unit > 120) {
				multiplier *= 60;
				unit /= 60;
			}

			if (multiplier >= 3600) {

				unit = //
						//
				unit >= 120 ? 120 : //
						unit >= 60 ? 60 : //
								unit >= 30 ? 30 : //
										unit >= 15 ? 15 : //
												unit >= 12 ? 12 : //
														unit > 6 ? 6 : //
																unit > 5 ? 5 : //
																		unit > 2 ? 2 : //
																				1;

			} else {

				unit = //
						//
				unit >= 120 ? 120 : //
						unit >= 60 ? 60 : //
								unit >= 30 ? 30 : //
										unit >= 15 ? 15 : //
												unit >= 10 ? 10 : //
														5;
			}

		} else {

			if (unit > 3600) {

				// unit > 1 hour (>3600 sec)

				while (unit > 3600) {
					multiplier *= 60;
					unit /= 60;
				}

				if (multiplier >= 3600) {

					// multiplier >= 1 hour

					unit = //
							//
					unit > 720 ? 720 : //
							unit > 240 ? 240 : //
									unit > 120 ? 120 : //
											unit > 24 ? 24 : //
													10;
//					unit = //
//							//
//					unit > 1000 ? 1000 : //
//							unit > 500 ? 500 : //
//									unit > 100 ? 100 : //
//											unit > 50 ? 50 : //
//													10;

				} else {

					// multiplier < 1 hour (3600 sec)

					unit = //
							//
					unit > 3000 ? 3000 : //
							unit > 2400 ? 2400 : //
									unit > 600 ? 600 : //
											unit > 300 ? 300 : //
													60;
				}

			} else {

				// unit < 1 hour (< 3600 sec)

				while (unit > 120) {
					multiplier *= 60;
					unit /= 60;
				}

				unit = //
						//
				unit > 120 ? 120 : //
						unit > 60 ? 60 : //
								unit > 30 ? 30 : //
										unit > 15 ? 15 : //
												unit > 10 ? 10 : //
														unit > 5 ? 5 : //
																unit > 2 ? 2 : //
																		1;
			}
		}

		unit *= multiplier;

		return (long) unit;
	}

	/**
	 * Round floating value by removing the trailing part, which causes problem when creating units.
	 * For the value 200.00004 the .00004 part will be removed
	 * 
	 * @param graphValue
	 * @param graphUnit
	 * @return
	 */
	public static double roundValueToUnit(final double graphValue, final double graphUnit, final boolean isMinValue) {

		if (graphUnit < 1) {

			if (graphValue < 0) {

				final double gvDiv1 = graphValue / graphUnit;
				final int gvDiv2 = (int) (gvDiv1 - 0.5f);
				final double gvDiv3 = gvDiv2 * graphUnit;

				return gvDiv3;

			} else {

				final double gvDiv1 = graphValue / graphUnit;
				final int gvDiv2 = (int) (gvDiv1 + (isMinValue ? -0.5f : 0.5f));
				final double gvDiv3 = gvDiv2 * graphUnit;

				return gvDiv3;
			}

		} else {

			// graphUnit >= 1

			if (graphValue < 0) {

				final double gvDiv1 = graphValue * graphUnit;
				final long gvDiv2 = (long) (gvDiv1 + (isMinValue ? -0.5f : 0.5f));
				final double gvDiv3 = gvDiv2 / graphUnit;

				return gvDiv3;

			} else {

				// graphValue >= 0

				final double gvDiv1 = graphValue * graphUnit;
				final long gvDiv2 = (long) (gvDiv1 + 0.5f);
				final double gvDiv3 = gvDiv2 / graphUnit;

				return gvDiv3;
			}
		}
	}

	/**
	 * Selects an item in the combo box which is retrieved from a state.
	 * 
	 * @param state
	 * @param stateKey
	 * @param comboStates
	 *            this array must must have the same number of entries as the combo box has items
	 * @param defaultState
	 * @param combo
	 */
	public static void selectStateInCombo(	final IDialogSettings state,
											final String stateKey,
											final String[] comboStates,
											final String defaultState,
											final Combo combo) {

		final String stateValue = Util.getStateString(state, stateKey, defaultState);

		int stateIndex = 0;
		for (final String comboStateValue : comboStates) {
			if (stateValue.equals(comboStateValue)) {
				break;
			}

			stateIndex++;
		}

		combo.select(stateIndex);
	}

	/**
	 * Selects a text item in the combo box. When text item is not available, the first item is
	 * selected
	 * 
	 * @param combo
	 * @param comboItems
	 * @param selectedItem
	 *            Text which should be selected in the combo box
	 */
	public static void selectTextInCombo(final Combo combo, final String[] comboItems, final String selectedItem) {

		int comboIndex = 0;

		for (final String comboStateValue : comboItems) {
			if (selectedItem.equals(comboStateValue)) {
				break;
			}

			comboIndex++;
		}

		combo.select(comboIndex);
	}

	/**
	 * Set the state for an integer array, integer values are converted into string value.
	 * 
	 * @param state
	 * @param stateKey
	 * @param intValues
	 */
	public static void setState(final IDialogSettings state, final String stateKey, final int[] intValues) {

		final String[] stateIndices = new String[intValues.length];

		for (int index = 0; index < intValues.length; index++) {
			stateIndices[index] = Integer.toString(intValues[index]);
		}

		state.put(stateKey, stateIndices);
	}

	public static void setState(final IDialogSettings state, final String stateKey, final long[] values) {

		final String[] stateIndices = new String[values.length];

		for (int index = 0; index < values.length; index++) {
			stateIndices[index] = Long.toString(values[index]);
		}

		state.put(stateKey, stateIndices);
	}

	public static void setState(final IDialogSettings state, final String stateKey, final RGB rgb) {

		final String[] stateValues = new String[3];

		stateValues[0] = Integer.toString(rgb.red);
		stateValues[1] = Integer.toString(rgb.green);
		stateValues[2] = Integer.toString(rgb.blue);

		state.put(stateKey, stateValues);
	}

	/**
	 * @param xmlColor
	 * @param tagName
	 * @param rgb
	 */
	public static void setXmlRgb(final IMemento xmlColor, final String tagName, final RGB rgb) {

		final IMemento colorValue = xmlColor.createChild(tagName);
		{
			colorValue.putInteger(ATTR_COLOR_RED, rgb.red);
			colorValue.putInteger(ATTR_COLOR_GREEN, rgb.green);
			colorValue.putInteger(ATTR_COLOR_BLUE, rgb.blue);
		}
	}

	/**
	 * Open view
	 * 
	 * @param viewId
	 * @param isActivateView
	 *            View is activated when <code>true</code>, otherwise it is only visible.
	 * @return Returns the opened/activated view.
	 */
	public static IViewPart showView(final String viewId, final boolean isActivateView) {

		try {

			final IWorkbench wb = PlatformUI.getWorkbench();
			if (wb == null) {
				return null;
			}

			final IWorkbenchWindow wbWin = wb.getActiveWorkbenchWindow();
			if (wbWin == null) {
				return null;
			}

			final IWorkbenchPage page = wbWin.getActivePage();
			if (page == null) {
				return null;
			}

			final int activationMode = isActivateView ? IWorkbenchPage.VIEW_ACTIVATE : IWorkbenchPage.VIEW_VISIBLE;

			return page.showView(viewId, null, activationMode);

		} catch (final PartInitException e) {
			StatusUtil.showStatus(e);
		}

		return null;
	}

	/**
	 * This method encodes the url, removes the spaces from the url and replaces the same with
	 * <code>"%20"</code>. This method is required to fix Bug 77840.
	 * 
	 * @since 3.0.2
	 */
	private static String urlEncodeForSpaces(final char[] input) {
		final StringBuffer retu = new StringBuffer(input.length);
		for (final char element : input) {
			if (element == ' ') {
				retu.append("%20"); //$NON-NLS-1$
			} else {
				retu.append(element);
			}
		}
		return retu.toString();
	}

	/**
	 * Writes a XML memento into a XML file.
	 * 
	 * @param xmlMemento
	 * @param xmlFile
	 */
	public static void writeXml(final XMLMemento xmlMemento, final File xmlFile) {

		BufferedWriter writer = null;

		try {

			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(xmlFile), UI.UTF_8));

			xmlMemento.save(writer);

		} catch (final Exception e) {
			StatusUtil.log(e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (final IOException e) {
					StatusUtil.log(e);
				}
			}
		}
	}
}
