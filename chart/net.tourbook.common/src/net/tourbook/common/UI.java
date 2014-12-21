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
package net.tourbook.common;

import java.awt.Font;
import java.nio.charset.Charset;
import java.util.ArrayList;

import net.tourbook.common.weather.IWeather;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TableViewerFocusCellManager;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IMemento;
import org.epics.css.dal.Timestamp;
import org.epics.css.dal.Timestamp.Format;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class UI {

	public static final char			SPACE									= ' ';
	public static final char			NEW_LINE								= '\n';
	public static final char			TAB										= '\t';

	public static final char			SYMBOL_BRACKET_LEFT						= '(';
	public static final char			SYMBOL_BRACKET_RIGHT					= ')';

	public static final String			COMMA_SPACE								= ", ";																	//$NON-NLS-1$
	public static final String			DASH									= "-";																		//$NON-NLS-1$
	public static final String			DASH_WITH_SPACE							= " - ";																	//$NON-NLS-1$
	public static final String			DASH_WITH_DOUBLE_SPACE					= "   -   ";																//$NON-NLS-1$
	public static final String			DIMENSION								= " x ";																	//$NON-NLS-1$
	public static final String			EMPTY_STRING							= "";																		//$NON-NLS-1$
	public static final String			NEW_LINE1								= "\n";																	//$NON-NLS-1$
	public static final String			NEW_LINE2								= "\n\n";																	//$NON-NLS-1$
	public static final String			NEW_LINE3								= "\n\n\n";																//$NON-NLS-1$
	public static final String			SPACE1									= " ";																		//$NON-NLS-1$
	public static final String			SPACE2									= "  ";																	//$NON-NLS-1$
	public static final String			SPACE4									= "    ";																	//$NON-NLS-1$
	public static final String			STRING_0								= "0";																		//$NON-NLS-1$

	public static final String			SYMBOL_ARROW_UP							= "\u2191";																//$NON-NLS-1$
	public static final String			SYMBOL_ARROW_DOWN						= "\u2193";																//$NON-NLS-1$
	public static final String			SYMBOL_AVERAGE							= "\u00f8";																//$NON-NLS-1$
	public static final String			SYMBOL_AVERAGE_WITH_SPACE				= "\u00f8 ";																//$NON-NLS-1$
	public static final String			SYMBOL_DASH								= "\u2212";																//$NON-NLS-1$
	public static final String			SYMBOL_DEGREE							= "\u00B0";																//$NON-NLS-1$
	public static final String			SYMBOL_DIFFERENCE						= "\u0394";																//$NON-NLS-1$
	public static final String			SYMBOL_DIFFERENCE_WITH_SPACE			= "\u0394 ";																//$NON-NLS-1$
	public static final String			SYMBOL_DOUBLE_HORIZONTAL				= "\u2550";																//$NON-NLS-1$
	public static final String			SYMBOL_DOUBLE_VERTICAL					= "\u2551";																//$NON-NLS-1$
	public static final String			SYMBOL_ELLIPSIS							= "\u2026";																//$NON-NLS-1$
	public static final String			SYMBOL_FIGURE_DASH						= "\u2012";																//$NON-NLS-1$
	public static final String			SYMBOL_FOOT_NOTE						= "\u20F0";																//$NON-NLS-1$
	public static final String			SYMBOL_FULL_BLOCK						= "\u2588";																//$NON-NLS-1$
	public static final String			SYMBOL_IDENTICAL_TO						= "\u2261";																//$NON-NLS-1$
	public static final String			SYMBOL_INFINITY_MAX						= "\u221E";																//$NON-NLS-1$
	public static final String			SYMBOL_INFINITY_MIN						= "-\u221E";																//$NON-NLS-1$
	public static final String			SYMBOL_SUM_WITH_SPACE					= "\u2211 ";																//$NON-NLS-1$
	public static final String			SYMBOL_SUN								= "\u263C";																//$NON-NLS-1$
	public static final String			SYMBOL_TAU								= "\u03c4";																//$NON-NLS-1$

	public static final String			SYMBOL_COLON							= ":";																		//$NON-NLS-1$
	public static final String			SYMBOL_DOT								= ".";																		//$NON-NLS-1$
	public static final String			SYMBOL_EQUAL							= "=";																		//$NON-NLS-1$
	public static final String			SYMBOL_EXCLAMATION_POINT				= "!";																		//$NON-NLS-1$
	public static final String			SYMBOL_GREATER_THAN						= ">";																		//$NON-NLS-1$
	public static final String			SYMBOL_LESS_THAN						= "<";																		//$NON-NLS-1$
	public static final String			SYMBOL_PERCENTAGE						= "%";																		//$NON-NLS-1$
	public static final String			SYMBOL_QUESTION_MARK					= "?";																		//$NON-NLS-1$
	public static final String			SYMBOL_STAR								= "*";																		//$NON-NLS-1$
	public static final String			SYMBOL_UNDERSCORE						= "_";																		//$NON-NLS-1$
	public static final String			SYMBOL_WIND_WITH_SPACE					= "W ";																	//$NON-NLS-1$

	public static final int				FORM_FIRST_COLUMN_INDENT				= 16;

	/**
	 * The ellipsis is the string that is used to represent shortened text.
	 * 
	 * @since 3.0
	 */
	public static final String			ELLIPSIS								= "...";																	//$NON-NLS-1$
	public static final String			ELLIPSIS_WITH_SPACE						= " ... ";																	//$NON-NLS-1$

	private static final char[]			INVALID_FILENAME_CHARS					= new char[] {
			'\\',
			'/',
			':',
			'*',
			'?',
			'"',
			'<',
			'>',
			'|',																};

	public static final boolean			IS_LINUX								= "gtk".equals(SWT.getPlatform());											//$NON-NLS-1$
	public static final boolean			IS_OSX									= "carbon".equals(SWT.getPlatform()) || "cocoa".equals(SWT.getPlatform());	//$NON-NLS-1$ //$NON-NLS-2$
	public static final boolean			IS_WIN									= "win32".equals(SWT.getPlatform()) || "wpf".equals(SWT.getPlatform());	//$NON-NLS-1$ //$NON-NLS-2$

	public static final String			UTF_8									= "UTF-8";																	//$NON-NLS-1$
	public static final String			UTF_16									= "UTF-16";																//$NON-NLS-1$
	public static final String			ISO_8859_1								= "ISO-8859-1";															//$NON-NLS-1$

	public static final Charset			UTF8_CHARSET							= Charset.forName("UTF-8");												//$NON-NLS-1$

	public static final String			MENU_SEPARATOR_ADDITIONS				= "additions";																//$NON-NLS-1$

	/**
	 * Layout hint for a description field
	 */
	public static final int				DEFAULT_DESCRIPTION_WIDTH				= 350;
	public static final int				DEFAULT_FIELD_WIDTH						= 40;

	/*
	 * Contains the unit label in the currenty measurement system for the distance values
	 */
	public static String				UNIT_LABEL_DISTANCE;
	public static String				UNIT_LABEL_DISTANCE_SMALL;
	public static String				UNIT_LABEL_ALTITUDE;
	public static String				UNIT_LABEL_ALTIMETER;
	public static String				UNIT_LABEL_TEMPERATURE;
	public static String				UNIT_LABEL_SPEED;
	public static String				UNIT_LABEL_PACE;

	public static final String			UNIT_LABEL_TIME							= "h";																		//$NON-NLS-1$
	public static final String			UNIT_LABEL_DIRECTION					= "\u00B0";																//$NON-NLS-1$

	/*
	 * Labels for the different measurement systems
	 */
	public static final String			UNIT_METER								= "m";																		//$NON-NLS-1$
	public static final String			UNIT_ALTITUDE_M							= "m";																		//$NON-NLS-1$
	public static final String			UNIT_DISTANCE_KM						= "km";																	//$NON-NLS-1$
	public static final String			UNIT_SPEED_KM_H							= "km/h";																	//$NON-NLS-1$
	public static final String			UNIT_TEMPERATURE_C						= "\u00B0C";																//$NON-NLS-1$
	public static final String			UNIT_ALTIMETER_M_H						= "m/h";																	//$NON-NLS-1$
	public static final String			UNIT_PACE_MIN_P_KM						= "min/km";																//$NON-NLS-1$
	public static final String			UNIT_WEIGHT_KG							= "kg";																	//$NON-NLS-1$
	public static final String			UNIT_MBYTES								= "MByte";																	//$NON-NLS-1$

	public static final String			UNIT_DISTANCE_YARD						= "yd";																	//$NON-NLS-1$
	public static final String			UNIT_ALTITUDE_FT						= "ft";																	//$NON-NLS-1$
	public static final String			UNIT_DISTANCE_MI						= "mi";																	//$NON-NLS-1$
	public static final String			UNIT_SPEED_MPH							= "mph";																	//$NON-NLS-1$
	public static final String			UNIT_TEMPERATURE_F						= "\u00B0F";																//$NON-NLS-1$
	public static final String			UNIT_ALTIMETER_FT_H						= "ft/h";																	//$NON-NLS-1$
	public static final String			UNIT_PACE_MIN_P_MILE					= "min/mi";																//$NON-NLS-1$

	public static final String			UNIT_LABEL_POWER						= "Watt";																	//$NON-NLS-1$
	public static final String			UNIT_LABEL_MS							= "ms";																	//$NON-NLS-1$

	/*
	 * SET_FORMATTING_OFF
	 */
	public	static final long beforeCET		= new DateTime(1893, 4, 1, 0, 0, 0, DateTimeZone.UTC).getMillis();
	public	static final long afterCETBegin	= new DateTime(1893, 4, 1, 0, 6, 32, DateTimeZone.UTC).getMillis();
	/*
	 * SET_FORMATTING_ON
	 */

	public static final int				BERLIN_HISTORY_ADJUSTMENT				= 6 * 60 + 32;

	public static final int				DAY_IN_SECONDS							= 24 * 60 * 60;

	/**
	 * The dialog settings key name for stored dialog x location.
	 * 
	 * @since 3.2
	 */
	private static final String			DIALOG_ORIGIN_X							= "DIALOG_X_ORIGIN";														//$NON-NLS-1$

	/**
	 * The dialog settings key name for stored dialog y location.
	 * 
	 * @since 3.2
	 */
	private static final String			DIALOG_ORIGIN_Y							= "DIALOG_Y_ORIGIN";														//$NON-NLS-1$

	/**
	 * The dialog settings key name for stored dialog width.
	 * 
	 * @since 3.2
	 */
	private static final String			DIALOG_WIDTH							= "DIALOG_WIDTH";															//$NON-NLS-1$

	/**
	 * The dialog settings key name for stored dialog height.
	 * 
	 * @since 3.2
	 */
	private static final String			DIALOG_HEIGHT							= "DIALOG_HEIGHT";															//$NON-NLS-1$

	/**
	 * The dialog settings key name for the font used when the dialog height and width was stored.
	 * 
	 * @since 3.2
	 */
	private static final String			DIALOG_FONT_DATA						= "DIALOG_FONT_NAME";														//$NON-NLS-1$

	public static final Font			AWT_FONT_ARIAL_8						= Font.decode("Arial-plain-8");											//$NON-NLS-1$
	public static final Font			AWT_FONT_ARIAL_10						= Font.decode("Arial-plain-10");											//$NON-NLS-1$
	public static final Font			AWT_FONT_ARIAL_12						= Font.decode("Arial-plain-12");											//$NON-NLS-1$
	public static final Font			AWT_FONT_ARIAL_14						= Font.decode("Arial-plain-14");											//$NON-NLS-1$
	public static final Font			AWT_FONT_ARIAL_16						= Font.decode("Arial-plain-16");											//$NON-NLS-1$
	public static final Font			AWT_FONT_ARIAL_18						= Font.decode("Arial-plain-18");											//$NON-NLS-1$
	public static final Font			AWT_FONT_ARIAL_20						= Font.decode("Arial-plain-20");											//$NON-NLS-1$
	public static final Font			AWT_FONT_ARIAL_24						= Font.decode("Arial-plain-24");											//$NON-NLS-1$
	public static final Font			AWT_FONT_ARIAL_48						= Font.decode("Arial-plain-48");											//$NON-NLS-1$
	public static final Font			AWT_FONT_ARIAL_BOLD_12					= Font.decode("Arial-bold-12");											//$NON-NLS-1$
	public static final Font			AWT_FONT_ARIAL_BOLD_24					= Font.decode("Arial-bold-24");											//$NON-NLS-1$

	/*
	 * image keys for images which are stored in the image registry
	 */
	public static final String			IMAGE_ACTION_PHOTO_FILTER				= "IMAGE_ACTION_PHOTO_FILTER";												//$NON-NLS-1$
	public static final String			IMAGE_ACTION_PHOTO_FILTER_NO_PHOTOS		= "IMAGE_ACTION_PHOTO_FILTER_NO_PHOTOS";									//$NON-NLS-1$
	public static final String			IMAGE_ACTION_PHOTO_FILTER_WITH_PHOTOS	= "IMAGE_ACTION_PHOTO_FILTER_WITH_PHOTOS";									//$NON-NLS-1$
	public static final String			IMAGE_ACTION_PHOTO_FILTER_DISABLED		= "IMAGE_ACTION_PHOTO_FILTER_DISABLED";									//$NON-NLS-1$
	public static final String			IMAGE_CONFIGURE_COLUMNS					= "IMAGE_CONFIGURE_COLUMNS";												//$NON-NLS-1$
	public static final String			IMAGE_EMPTY_16							= "_empty16";																//$NON-NLS-1$

	public final static ImageRegistry	IMAGE_REGISTRY;

	static {

		IMAGE_REGISTRY = CommonActivator.getDefault().getImageRegistry();

		IMAGE_REGISTRY.put(IMAGE_ACTION_PHOTO_FILTER, //
				CommonActivator.getImageDescriptor(Messages.Image_Action_PhotoFilter));
		IMAGE_REGISTRY.put(IMAGE_ACTION_PHOTO_FILTER_NO_PHOTOS, //
				CommonActivator.getImageDescriptor(Messages.Image_Action_PhotoFilterNoPhotos));
		IMAGE_REGISTRY.put(IMAGE_ACTION_PHOTO_FILTER_WITH_PHOTOS, //
				CommonActivator.getImageDescriptor(Messages.Image_Action_PhotoFilterWithPhotos));
		IMAGE_REGISTRY.put(IMAGE_ACTION_PHOTO_FILTER_DISABLED,//
				CommonActivator.getImageDescriptor(Messages.Image_Action_PhotoFilter_Disabled));

		IMAGE_REGISTRY.put(IMAGE_CONFIGURE_COLUMNS, //
				CommonActivator.getImageDescriptor(Messages.Image__ConfigureColumns));
		IMAGE_REGISTRY.put(IMAGE_EMPTY_16, //
				CommonActivator.getImageDescriptor(Messages.Image___Empty16));

		// weather images
		IMAGE_REGISTRY.put(IWeather.WEATHER_ID_CLEAR, //
				CommonActivator.getImageDescriptor(Messages.Image__weather_sunny));
		IMAGE_REGISTRY.put(IWeather.WEATHER_ID_PART_CLOUDS, //
				CommonActivator.getImageDescriptor(Messages.Image__weather_cloudy));
		IMAGE_REGISTRY.put(IWeather.WEATHER_ID_OVERCAST, //
				CommonActivator.getImageDescriptor(Messages.Image__weather_clouds));
		IMAGE_REGISTRY.put(IWeather.WEATHER_ID_LIGHTNING,//
				CommonActivator.getImageDescriptor(Messages.Image__weather_lightning));
		IMAGE_REGISTRY.put(IWeather.WEATHER_ID_RAIN,//
				CommonActivator.getImageDescriptor(Messages.Image__weather_rain));
		IMAGE_REGISTRY.put(IWeather.WEATHER_ID_SNOW,//
				CommonActivator.getImageDescriptor(Messages.Image__weather_snow));
		IMAGE_REGISTRY.put(IWeather.WEATHER_ID_SCATTERED_SHOWERS,//
				CommonActivator.getImageDescriptor(Messages.Image__Weather_ScatteredShowers));
		IMAGE_REGISTRY.put(IWeather.WEATHER_ID_SEVERE_WEATHER_ALERT,//
				CommonActivator.getImageDescriptor(Messages.Image__Weather_Severe));

	}

	public static void addSashColorHandler(final Sash sash) {

		sash.addMouseTrackListener(new MouseTrackListener() {

			@Override
			public void mouseEnter(final MouseEvent e) {
				sash.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW));
			}

			@Override
			public void mouseExit(final MouseEvent e) {
				sash.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
			}

			@Override
			public void mouseHover(final MouseEvent e) {}
		});

		sash.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {

				// hide background when sash is dragged

				if (e.detail == SWT.DRAG) {
					sash.setBackground(null);
				} else {
					sash.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW));
				}
			}
		});
	}

	public static void adjustScaleValueOnMouseScroll(final MouseEvent event) {

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

		final Scale scale = (Scale) event.widget;
		final int increment = scale.getIncrement();
		final int oldValue = scale.getSelection();
		final int valueDiff = ((event.count > 0 ? increment : -increment) * accelerator);

		scale.setSelection(oldValue + valueDiff);
	}

	public static void adjustSpinnerValueOnMouseScroll(final MouseEvent event) {

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

		final Spinner spinner = (Spinner) event.widget;
		final int newValue = ((event.count > 0 ? 1 : -1) * accelerator);

		spinner.setSelection(spinner.getSelection() + newValue);
	}

	/**
	 * @return Returns a cursor which is hidden. This cursor must be disposed.
	 */
	public static Cursor createHiddenCursor() {

		// create a cursor with a transparent image

		final Display display = Display.getDefault();

		final Color white = display.getSystemColor(SWT.COLOR_WHITE);
		final Color black = display.getSystemColor(SWT.COLOR_BLACK);

		final PaletteData palette = new PaletteData(new RGB[] { white.getRGB(), black.getRGB() });

		final ImageData sourceData = new ImageData(16, 16, 1, palette);
		sourceData.transparentPixel = 0;

		return new Cursor(display, sourceData, 0, 0);
	}

	/**
	 * Creates one action in a toolbar.
	 * 
	 * @param parent
	 * @param action
	 */
	public static void createToolbarAction(final Composite parent, final Action action) {

		final ToolBar toolbar = new ToolBar(parent, SWT.FLAT);

		final ToolBarManager tbm = new ToolBarManager(toolbar);
		tbm.add(action);
		tbm.update(true);
	}

	public static Color disposeResource(final Color resource) {
		if ((resource != null) && !resource.isDisposed()) {
			resource.dispose();
		}
		return null;
	}

	public static Cursor disposeResource(final Cursor resource) {
		if ((resource != null) && !resource.isDisposed()) {
			resource.dispose();
		}
		return null;
	}

	/**
	 * disposes a resource
	 * 
	 * @param image
	 * @return
	 */
	public static Image disposeResource(final Image resource) {
		if ((resource != null) && !resource.isDisposed()) {
			resource.dispose();
		}
		return null;
	}

	public static void dumpSuperClasses(final Object o) {

		Class<?> subclass = o.getClass();
		Class<?> superclass = subclass.getSuperclass();

		while (superclass != null) {

			final String className = superclass.getCanonicalName();

			System.out.println(className);

			subclass = superclass;
			superclass = subclass.getSuperclass();
		}
	}

	public static String FormatDoubleMinMax(final double value) {

		if (value == -Double.MAX_VALUE) {
			return SYMBOL_INFINITY_MIN;
		} else if (value == Double.MAX_VALUE) {
			return SYMBOL_INFINITY_MAX;
		}

		return Double.toString(value);
	}

	public static String FormatDoubleMinMaxElevationMeter(final double value) {

		if (value == -Double.MAX_VALUE) {
			return SYMBOL_INFINITY_MIN;
		} else if (value == Double.MAX_VALUE) {
			return SYMBOL_INFINITY_MAX;
		}

		return Long.toString((long) (value / 1000)) + UI.SPACE + UI.UNIT_DISTANCE_KM;
	}

	/**
	 * Get best-fit size for an image drawn in an area of maxX, maxY
	 * 
	 * @param imageWidth
	 * @param imageHeight
	 * @param canvasWidth
	 * @param canvasHeight
	 * @return
	 */
	public static Point getBestFitCanvasSize(	final int imageWidth,
												final int imageHeight,
												final int canvasWidth,
												final int canvasHeight) {

		final double widthRatio = (double) imageWidth / (double) canvasWidth;
		final double heightRatio = (double) imageHeight / (double) canvasHeight;

		final double bestRatio = widthRatio > heightRatio ? widthRatio : heightRatio;

		final int newWidth = (int) (imageWidth / bestRatio);
		final int newHeight = (int) (imageHeight / bestRatio);

		return new Point(newWidth, newHeight);
	}

	/**
	 * @param degreeDirection
	 * @return Returns cardinal direction
	 */
	public static String getCardinalDirectionText(final int degreeDirection) {

		return IWeather.windDirectionText[getCardinalDirectionTextIndex(degreeDirection)];
	}

	/**
	 * @param degreeDirection
	 * @return Returns cardinal direction index for {@link IWeather#windDirectionText}
	 */
	public static int getCardinalDirectionTextIndex(final int degreeDirection) {

		final float degree = (degreeDirection + 22.5f) / 45.0f;

		final int directionIndex = ((int) degree) % 8;

		return directionIndex;
	}

	public static Rectangle getDisplayBounds(final Composite composite, final Point location) {

		Rectangle displayBounds;
		final Monitor[] allMonitors = composite.getDisplay().getMonitors();

		if (allMonitors.length > 1) {
			// By default present in the monitor of the control
			displayBounds = composite.getMonitor().getBounds();
			final Point p = new Point(location.x, location.y);

			// Search on which monitor the event occurred
			Rectangle tmp;
			for (final Monitor element : allMonitors) {
				tmp = element.getBounds();
				if (tmp.contains(p)) {
					displayBounds = tmp;
					break;
				}
			}

		} else {
			displayBounds = composite.getDisplay().getBounds();
		}

		return displayBounds;
	}

	/**
	 * @param allVisibleItems
	 * @param allExpandedItems
	 * @return Returns {@link TreePath}'s which are expanded and open (not hidden).
	 */
	public static TreePath[] getExpandedOpenedItems(final Object[] allVisibleItems, final TreePath[] allExpandedItems) {

		final ArrayList<TreePath> expandedOpened = new ArrayList<TreePath>();

		for (final TreePath expandedPath : allExpandedItems) {

			/*
			 * The last expanded segment must be in the visible list otherwise it is hidden.
			 */
			final Object lastExpandedItem = expandedPath.getLastSegment();

			for (final Object visibleItem : allVisibleItems) {

				if (lastExpandedItem == visibleItem) {

					expandedOpened.add(expandedPath);
					break;
				}
			}
		}

		return expandedOpened.toArray(new TreePath[expandedOpened.size()]);
	}

	/**
	 * This is a copy with modifications from {@link org.eclipse.jface.dialogs.Dialog}
	 * 
	 * @param statePrefix
	 */
	public static Point getInitialLocation(	final IDialogSettings state,
											final String statePrefix,
											final Shell shell,
											final Shell parentShell) {

		Point result = shell.getLocation();

		try {
			final int x = state.getInt(statePrefix + DIALOG_ORIGIN_X);
			final int y = state.getInt(statePrefix + DIALOG_ORIGIN_Y);
			result = new Point(x, y);

			// The coordinates were stored relative to the parent shell.
			// Convert to display coordinates.
			if (parentShell != null) {
				final Point parentLocation = parentShell.getLocation();
				result.x += parentLocation.x;
				result.y += parentLocation.y;
			}
		} catch (final NumberFormatException e) {}

		// No attempt is made to constrain the bounds. The default
		// constraining behavior in Window will be used.
		return result;
	}

	public static boolean isCtrlKey(final MouseEvent event) {

		boolean isCtrlKey;

		if (UI.IS_OSX) {
			isCtrlKey = (event.stateMask & SWT.MOD1) > 0;
		} else {
			isCtrlKey = (event.stateMask & SWT.MOD1) > 0;
		}

		return isCtrlKey;
	}

	public static boolean isShiftKey(final MouseEvent event) {

		boolean isShiftKey;

		if (UI.IS_OSX) {
			isShiftKey = (event.stateMask & SWT.MOD3) > 0;
		} else {
			isShiftKey = (event.stateMask & SWT.MOD2) > 0;
		}

		return isShiftKey;
	}

	/**
	 * Opens the control context menu, the menue is aligned below the control to the right side
	 * 
	 * @param control
	 *            Controls which menu is opened
	 */
	public static void openControlMenu(final Control control) {

		final Rectangle rect = control.getBounds();
		Point pt = new Point(rect.x, rect.y + rect.height);
		pt = control.getParent().toDisplay(pt);

		final Menu contextMenu = control.getMenu();

		if (contextMenu != null && contextMenu.isDisposed() == false) {
			contextMenu.setLocation(pt.x, pt.y);
			contextMenu.setVisible(true);
		}
	}

	public static void resetInitialLocation(final IDialogSettings _state, final String statePrefix) {

		_state.put(statePrefix + DIALOG_ORIGIN_X, (String) null);
		_state.put(statePrefix + DIALOG_ORIGIN_Y, (String) null);
	}

	/**
	 * Restore the sash weight from a memento
	 * 
	 * @param sashForm
	 * @param state
	 * @param weightKey
	 * @param sashDefaultWeight
	 */
	public static void restoreSashWeight(	final SashForm sashForm,
											final IDialogSettings state,
											final String weightKey,
											final int[] sashDefaultWeight) {

		final int[] sashWeights = sashForm.getWeights();
		final int[] newWeights = new int[sashWeights.length];

		for (int weightIndex = 0; weightIndex < sashWeights.length; weightIndex++) {

			try {

				final int mementoWeight = state.getInt(weightKey + Integer.toString(weightIndex));

				newWeights[weightIndex] = mementoWeight;

			} catch (final Exception e) {

				try {
					newWeights[weightIndex] = sashDefaultWeight[weightIndex];

				} catch (final ArrayIndexOutOfBoundsException e2) {
					newWeights[weightIndex] = 100;
				}
			}

		}

		sashForm.setWeights(newWeights);
	}

	/**
	 * Restore the sash weight from a memento
	 * 
	 * @param sash
	 * @param fMemento
	 * @param weightKey
	 * @param sashDefaultWeight
	 */
	public static void restoreSashWeight(	final SashForm sash,
											final IMemento fMemento,
											final String weightKey,
											final int[] sashDefaultWeight) {

		final int[] sashWeights = sash.getWeights();
		final int[] newWeights = new int[sashWeights.length];

		for (int weightIndex = 0; weightIndex < sashWeights.length; weightIndex++) {

			final Integer mementoWeight = fMemento.getInteger(weightKey + Integer.toString(weightIndex));

			if (mementoWeight == null) {
				try {
					newWeights[weightIndex] = sashDefaultWeight[weightIndex];

				} catch (final ArrayIndexOutOfBoundsException e) {
					newWeights[weightIndex] = 100;
				}
			} else {
				newWeights[weightIndex] = mementoWeight;
			}
		}

		sash.setWeights(newWeights);
	}

	/**
	 * This is a copy with modifications from {@link org.eclipse.jface.dialogs.Dialog}
	 * 
	 * @param statePrefix
	 */
	public static void saveDialogBounds(final IDialogSettings state,
										final String statePrefix,
										final Shell shell,
										final Shell parentShell) {

		if (state != null) {

			final Point shellLocation = shell.getLocation();
			final Point shellSize = shell.getSize();

			if (parentShell != null) {
				final Point parentLocation = parentShell.getLocation();
				shellLocation.x -= parentLocation.x;
				shellLocation.y -= parentLocation.y;
			}

			state.put(statePrefix + DIALOG_ORIGIN_X, shellLocation.x);
			state.put(statePrefix + DIALOG_ORIGIN_Y, shellLocation.y);

			state.put(statePrefix + DIALOG_WIDTH, shellSize.x);
			state.put(statePrefix + DIALOG_HEIGHT, shellSize.y);

			final FontData[] fontDatas = JFaceResources.getDialogFont().getFontData();
			if (fontDatas.length > 0) {
				state.put(statePrefix + DIALOG_FONT_DATA, fontDatas[0].toString());
			}
		}
	}

	/**
	 * Store the weights for the sash in a memento
	 * 
	 * @param sashForm
	 * @param state
	 * @param weightKey
	 */
	public static void saveSashWeight(final SashForm sashForm, final IDialogSettings state, final String weightKey) {

		final int[] weights = sashForm.getWeights();

		for (int weightIndex = 0; weightIndex < weights.length; weightIndex++) {
			state.put(weightKey + Integer.toString(weightIndex), weights[weightIndex]);
		}
	}

	/**
	 * Store the weights for the sash in a memento
	 * 
	 * @param sash
	 * @param memento
	 * @param weightKey
	 */
	public static void saveSashWeight(final SashForm sash, final IMemento memento, final String weightKey) {

		final int[] weights = sash.getWeights();

		for (int weightIndex = 0; weightIndex < weights.length; weightIndex++) {
			memento.putInteger(weightKey + Integer.toString(weightIndex), weights[weightIndex]);
		}
	}

	public static void setBackgroundColorForAllChildren(final Control parent, final Color bgColor) {

		parent.setBackground(bgColor);

		if (parent instanceof Composite) {

			final Control[] children = ((Composite) parent).getChildren();

			for (final Control child : children) {

				if (child != null && child.isDisposed() == false //

						// exclude controls which look ugly
						&& !child.getClass().equals(Combo.class)
						&& !child.getClass().equals(Spinner.class)
				//
				) {

					setBackgroundColorForAllChildren(child, bgColor);
				}
			}
		}
	}

	/**
	 * Initialize cell editing.
	 * 
	 * @param viewer
	 */
	public static void setCellEditSupport(final TableViewer viewer) {

		final TableViewerFocusCellManager focusCellManager = new TableViewerFocusCellManager(
				viewer,
				new FocusCellOwnerDrawHighlighter(viewer));

		final ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(viewer) {
			@Override
			protected boolean isEditorActivationEvent(final ColumnViewerEditorActivationEvent event) {

				return (event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL)
						|| (event.eventType == ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION)
						|| ((event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED) && (event.keyCode == SWT.CR))
						|| (event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC);
			}
		};

		TableViewerEditor.create(//
				viewer,
				focusCellManager,
				actSupport,
				ColumnViewerEditor.TABBING_HORIZONTAL //
						| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR //
						| ColumnViewerEditor.TABBING_VERTICAL
						| ColumnViewerEditor.KEYBOARD_ACTIVATION);
	}

	public static void setColorForAllChildren(final Control parent, final Color fgColor, final Color bgColor) {

		parent.setForeground(fgColor);
		parent.setBackground(bgColor);

		if (parent instanceof Composite) {

			final Control[] children = ((Composite) parent).getChildren();

			for (final Control child : children) {

				if (child != null && child.isDisposed() == false //

						// exclude controls which look ugly
						&& !child.getClass().equals(Combo.class)
						&& !child.getClass().equals(Spinner.class)
				//
				) {

					setColorForAllChildren(child, fgColor, bgColor);
				}
			}
		}
	}

	/**
	 * set width for all controls in one column to the max width value
	 */
	public static void setEqualizeColumWidths(final ArrayList<Control> columnControls) {
		UI.setEqualizeColumWidths(columnControls, 0);
	}

	public static void setEqualizeColumWidths(final ArrayList<Control> columnControls, final int additionalSpace) {

		int maxWidth = 0;

		// get max width from all first columns controls
		for (final Control control : columnControls) {

			if (control.isDisposed()) {
				// this should not happen, but it did during testing
				return;
			}

			final int controlWidth = control.getSize().x;

			final int width = controlWidth + additionalSpace;

			maxWidth = width > maxWidth ? width : maxWidth;
		}

		// set width for all first column controls
		for (final Control control : columnControls) {
			((GridData) control.getLayoutData()).widthHint = maxWidth;
		}
	}

	public static GridData setFieldWidth(final Composite parent, final StringFieldEditor field, final int width) {
		final GridData gd = new GridData();
		gd.widthHint = width;
		field.getTextControl(parent).setLayoutData(gd);
		return gd;
	}

	/**
	 * copy from {@link CTabItem}
	 * 
	 * @param gc
	 * @param text
	 * @param width
	 * @param isUseEllipses
	 * @return
	 */
	public static String shortenText(final GC gc, final String text, final int width, final boolean isUseEllipses) {
		return isUseEllipses ? //
				shortenText(gc, text, width, ELLIPSIS)
				: shortenText(gc, text, width, UI.EMPTY_STRING);
	}

	public static String shortenText(final GC gc, String text, final int width, final String ellipses) {

		if (gc.textExtent(text, 0).x <= width) {
			return text;
		}

		final int ellipseWidth = gc.textExtent(ellipses, 0).x;
		final int length = text.length();
		final TextLayout layout = new TextLayout(gc.getDevice());
		layout.setText(text);

		int end = layout.getPreviousOffset(length, SWT.MOVEMENT_CLUSTER);
		while (end > 0) {
			text = text.substring(0, end);
			final int l = gc.textExtent(text, 0).x;
			if (l + ellipseWidth <= width) {
				break;
			}
			end = layout.getPreviousOffset(end, SWT.MOVEMENT_CLUSTER);
		}
		layout.dispose();
		return end == 0 ? text.substring(0, 1) : text + ellipses;
	}

	/**
	 * copied from {@link Dialog} <br>
	 * <br>
	 * Shortens the given text <code>textValue</code> so that its width in pixels does not exceed
	 * the width of the given control. Overrides characters in the center of the original string
	 * with an ellipsis ("...") if necessary. If a <code>null</code> value is given,
	 * <code>null</code> is returned.
	 * 
	 * @param textValue
	 *            the original string or <code>null</code>
	 * @param control
	 *            the control the string will be displayed on
	 * @return the string to display, or <code>null</code> if null was passed in
	 * @since 3.0
	 */
	public static String shortenText(final String textValue, final Control control) {
		if (textValue == null) {
			return null;
		}
		final GC gc = new GC(control);
		final int maxWidth = control.getBounds().width - 5;
		final int maxExtent = gc.textExtent(textValue).x;
		if (maxExtent < maxWidth) {
			gc.dispose();
			return textValue;
		}
		final int length = textValue.length();
		final int charsToClip = Math.round(0.95f * length * (1 - ((float) maxWidth / maxExtent)));
		final int pivot = length / 2;
		int start = pivot - (charsToClip / 2);
		int end = pivot + (charsToClip / 2) + 1;
		while (start >= 0 && end < length) {
			final String s1 = textValue.substring(0, start);
			final String s2 = textValue.substring(end, length);
			final String s = s1 + ELLIPSIS + s2;
			final int l = gc.textExtent(s).x;
			if (l < maxWidth) {
				gc.dispose();
				return s;
			}
			start--;
			end++;
		}
		gc.dispose();
		return textValue;
	}

	public static String shortenText(	final String text,
										final Control control,
										final int width,
										final boolean isUseEllipses) {

		String shortText;
		final GC gc = new GC(control);
		{
			shortText = shortenText(gc, text, width, isUseEllipses);
		}
		gc.dispose();

		return shortText;
	}

	public static String shortenText(final String text, final int textWidth, final boolean isShowBegin) {

		int beginIndex;
		int endIndex;

		final int textLength = text.length();

		if (isShowBegin) {

			beginIndex = 0;
			endIndex = textLength > textWidth ? textWidth : textLength;

		} else {

			beginIndex = textLength - textWidth;
			beginIndex = beginIndex < 0 ? 0 : beginIndex;

			endIndex = textLength;
		}

		String shortedText = text.substring(beginIndex, endIndex);

		// add ellipsis when text is too long
		if (textLength > textWidth) {

			if (isShowBegin) {
				shortedText = shortedText + UI.ELLIPSIS;
			} else {
				shortedText = UI.ELLIPSIS + shortedText;
			}
		}

		return shortedText;
	}

	public static String timeStamp() {
		return (new Timestamp()).toString(Format.Log);
	}

	public static String timeStampNano() {
		return (new Timestamp()).toString();
	}

	/**
	 * Update colors for all decendants.
	 * 
	 * @param child
	 * @param bgColor
	 * @param fgColor
	 */
	public static void updateChildColors(final Control child, final Color fgColor, final Color bgColor) {

		/*
		 * ignore these controls because they do not look very good on Linux & OSX
		 */
		if (child instanceof Spinner || child instanceof Combo) {
			return;
		}

		/*
		 * Toolbar action render awfully on Win7.
		 */
		if (child instanceof ToolBar) {
//			return;
		}

		child.setForeground(fgColor);
		child.setBackground(bgColor);

		if (child instanceof Composite) {
			final Control[] children = ((Composite) child).getChildren();
			for (final Control element : children) {

				if (element != null && element.isDisposed() == false) {
					updateChildColors(element, fgColor, bgColor);
				}
			}
		}
	}

	public static void updateScrolledContent(final Composite composite) {

		Composite child = composite;
		Composite parent = composite.getParent();

		while (parent != null) {

			// go up until the first scrolled container

			if (parent instanceof ScrolledComposite) {

				final ScrolledComposite scrolledContainer = (ScrolledComposite) parent;

				/*
				 * update layout: both methods must be called because the size can be modified and a
				 * layout with resized controls MUST be done !!!!
				 */
				scrolledContainer.setMinSize(child.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				scrolledContainer.layout(true, true);

				break;
			}

			child = parent;
			parent = parent.getParent();
		}
	}

	public static VerifyListener verifyFilenameInput() {
		return new VerifyListener() {
			@Override
			public void verifyText(final VerifyEvent e) {

				// check invalid chars
				for (final char invalidChar : INVALID_FILENAME_CHARS) {
					if (invalidChar == e.character) {
						e.doit = false;
						return;
					}
				}
			}
		};
	}

	public static void verifyIntegerInput(final Event e, final boolean canBeNegative) {

		// check backspace and del key
		if (e.character == SWT.BS || e.character == SWT.DEL) {
			return;
		}

		// check '-' key
		if (canBeNegative && e.character == '-') {
			return;
		}

		try {
			Integer.parseInt(e.text);
		} catch (final NumberFormatException ex) {
			e.doit = false;
		}
	}

	public static boolean verifyIntegerValue(final String valueString) {

		if (valueString.trim().length() == 0) {
			return false;
		}

		try {
			Integer.parseInt(valueString);
			return true;
		} catch (final NumberFormatException ex) {
			return false;
		}
	}

	public static VerifyListener verifyListenerInteger(final boolean canBeNegative) {

		return new VerifyListener() {
			@Override
			public void verifyText(final VerifyEvent e) {

				// check backspace and del key
				if (e.character == SWT.BS || e.character == SWT.DEL) {
					return;
				}

				// check '-' key
				if (canBeNegative && e.character == '-') {
					return;
				}

				try {
					Integer.parseInt(e.text);
				} catch (final NumberFormatException ex) {
					e.doit = false;
				}
			}
		};
	}

	public static VerifyListener verifyListenerTypeLong() {

		return new VerifyListener() {
			@Override
			public void verifyText(final VerifyEvent e) {
				if (e.text.equals(UI.EMPTY_STRING)) {
					return;
				}
				try {
					Long.parseLong(e.text);
				} catch (final NumberFormatException e1) {
					e.doit = false;
				}
			}
		};
	}

// this conversion is not working for all png images, found SWT2Dutil.java
//
//	/**
//	 * Converts a Swing BufferedImage into a lightweight ImageData object for SWT
//	 *
//	 * @param bufferedImage
//	 *            the image to be converted
//	 * @param originalImagePathName
//	 * @return An ImageData that represents the same image as bufferedImage
//	 */
//	public static ImageData convertAWTimageIntoSWTimage(final BufferedImage bufferedImage, final String imagePathName) {
//
//		try {
//
//			if (bufferedImage.getColorModel() instanceof DirectColorModel) {
//				final DirectColorModel colorModel = (DirectColorModel) bufferedImage.getColorModel();
//				final PaletteData palette = new PaletteData(
//						colorModel.getRedMask(),
//						colorModel.getGreenMask(),
//						colorModel.getBlueMask());
//				final ImageData data = new ImageData(
//						bufferedImage.getWidth(),
//						bufferedImage.getHeight(),
//						colorModel.getPixelSize(),
//						palette);
//				final WritableRaster raster = bufferedImage.getRaster();
//				final int[] pixelArray = new int[3];
//				for (int y = 0; y < data.height; y++) {
//					for (int x = 0; x < data.width; x++) {
//						raster.getPixel(x, y, pixelArray);
//						final int pixel = palette.getPixel(new RGB(pixelArray[0], pixelArray[1], pixelArray[2]));
//						data.setPixel(x, y, pixel);
//					}
//				}
//				return data;
//
//			} else if (bufferedImage.getColorModel() instanceof IndexColorModel) {
//
//				final IndexColorModel colorModel = (IndexColorModel) bufferedImage.getColorModel();
//				final int size = colorModel.getMapSize();
//				final byte[] reds = new byte[size];
//				final byte[] greens = new byte[size];
//				final byte[] blues = new byte[size];
//				colorModel.getReds(reds);
//				colorModel.getGreens(greens);
//				colorModel.getBlues(blues);
//				final RGB[] rgbs = new RGB[size];
//				for (int i = 0; i < rgbs.length; i++) {
//					rgbs[i] = new RGB(reds[i] & 0xFF, greens[i] & 0xFF, blues[i] & 0xFF);
//				}
//				final PaletteData palette = new PaletteData(rgbs);
//				final ImageData data = new ImageData(
//						bufferedImage.getWidth(),
//						bufferedImage.getHeight(),
//						colorModel.getPixelSize(),
//						palette);
//				data.transparentPixel = colorModel.getTransparentPixel();
//				final WritableRaster raster = bufferedImage.getRaster();
//				final int[] pixelArray = new int[1];
//				for (int y = 0; y < data.height; y++) {
//					for (int x = 0; x < data.width; x++) {
//						raster.getPixel(x, y, pixelArray);
//						data.setPixel(x, y, pixelArray[0]);
//					}
//				}
//				return data;
//
//			} else if (bufferedImage.getColorModel() instanceof ComponentColorModel) {
//
//				final ComponentColorModel colorModel = (ComponentColorModel) bufferedImage.getColorModel();
//
//				//ASSUMES: 3 BYTE BGR IMAGE TYPE
//
//				final PaletteData palette = new PaletteData(0x0000FF, 0x00FF00, 0xFF0000);
//				final ImageData data = new ImageData(
//						bufferedImage.getWidth(),
//						bufferedImage.getHeight(),
//						colorModel.getPixelSize(),
//						palette);
//
//				//This is valid because we are using a 3-byte Data model with no transparent pixels
//				data.transparentPixel = -1;
//
//				final WritableRaster raster = bufferedImage.getRaster();
//
////				final int[] pixelArray = new int[3];
//				final int[] pixelArray = colorModel.getComponentSize();
//
//				for (int y = 0; y < data.height; y++) {
//					for (int x = 0; x < data.width; x++) {
//						raster.getPixel(x, y, pixelArray);
//						final int pixel = palette.getPixel(new RGB(pixelArray[0], pixelArray[1], pixelArray[2]));
//						data.setPixel(x, y, pixel);
//					}
//				}
//				return data;
//			}
//
//		} catch (final Exception e) {
//
//			System.out.println(NLS.bind(//
//					UI.timeStamp() + "Cannot convert AWT image into SWT image: {0}",
//					imagePathName));
//		}
//
//		return null;
//	}
}
