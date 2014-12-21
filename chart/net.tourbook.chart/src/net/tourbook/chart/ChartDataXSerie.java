/*******************************************************************************
 * Copyright (C) 2005, 2012  Wolfgang Schramm and Contributors
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

/**
 * Author: Wolfgang Schramm Created: 21.06.2005
 */
package net.tourbook.chart;

import org.joda.time.DateTime;

/**
 * Contains data values for the x-axis
 */
public class ChartDataXSerie extends ChartDataSerie {

	double[][]				_lowValuesDouble;
	double[][]				_highValuesDouble;

	/**
	 * start value for the serie data, this is use to set the start point for time data to the
	 * starting time
	 */
	private double			_startValue				= 0;

	/**
	 * index in the x-data at which the graph is painted in the marker color, <code>-1</code>
	 * disables the synch marker
	 */
	private int				_synchMarkerStartIndex	= -1;

	/**
	 * index in the x-data at which the graph is stoped to painted in the marker color
	 */
	private int				_synchMarkerEndIndex	= -1;

	/**
	 * Range marker shows an area with a different color in the graph
	 */
	private int[]			_rangeMarkerStartIndex;
	private int[]			_rangeMarkerEndIndex;

	/**
	 * Segment contains information to show statistics for several years
	 */
	private ChartSegments	_chartSegments;

	/**
	 * Contains information how to draw the title for a history chart
	 */
	private HistoryTitle	_historyTitle;

	/**
	 * Scaling for the x-axis which is computed with {@link Math#pow(double, double)} when this
	 * value is <code>!= 1</code>. Extended scaling is used in the conconi view.
	 */
	private double			_scalingFactor			= 1;

	private double			_scalingMaxValue		= 1;
	/**
	 * Defines <code>true</code> or <code>false</code> if a line should be drawn for a value point,
	 * can be <code>null</code> to disable this feature.
	 */
	public boolean[]		_noLine;

	/**
	 * X-axis start Date/Time
	 */
	private DateTime		_startDateTime;

	private boolean			_timeSerieWithTimeZoneAdjustment;

	public ChartDataXSerie(final double[] values) {
		setMinMaxValues(new double[][] { values });
	}

	public ChartDataXSerie(final double[][] values) {
		setMinMaxValues(values);
	}

	public ChartSegments getChartSegments() {
		return _chartSegments;
	}

	/**
	 * @return returns the value array
	 */
	public double[][] getHighValuesDouble() {
		return _highValuesDouble;
	}

	public HistoryTitle getHistoryTitle() {
		return _historyTitle;
	}

	public boolean[] getNoLine() {
		return _noLine;
	}

	public int[] getRangeMarkerEndIndex() {
		return _rangeMarkerEndIndex;
	}

	public int[] getRangeMarkerStartIndex() {
		return _rangeMarkerStartIndex;
	}

	/**
	 * @return Returns scaling for the x-axis which is computed with Math.pow(double, double). This
	 *         scaling is disabled when <code>1</code> is returned.
	 */
	public double getScalingFactor() {
		return _scalingFactor;
	}

	public double getScalingMaxValue() {
		return _scalingMaxValue;
	}

	/**
	 * @return Returns x-axis start date/time or <code>null</code> when not available.
	 */
	public DateTime getStartDateTime() {
		return _startDateTime;
	}

	/**
	 * @return Returns the startValue.
	 */
	public double getStartValue() {
		return _startValue;
	}

	/**
	 * @return Returns the xMarkerEndIndex.
	 */
	public int getSynchMarkerEndIndex() {
		return _synchMarkerEndIndex;
	}

	/**
	 * @return Returns the xMarkerStartIndex or <code>-1</code> when the x-marker is not displayed
	 */
	public int getSynchMarkerStartIndex() {
		return _synchMarkerStartIndex;
	}

	public boolean isTimeSerieWithTimeZoneAdjustment() {
		return _timeSerieWithTimeZoneAdjustment;
	}

	public void setChartSegments(final ChartSegments chartSegments) {
		_chartSegments = chartSegments;
	}

	public void setHistoryTitle(final HistoryTitle historyTitle) {
		_historyTitle = historyTitle;
	}

	public void setIsTimeSerieWithTimeZoneAdjustment(final boolean timeSerieWithTimeZoneAdjustment) {
		_timeSerieWithTimeZoneAdjustment = timeSerieWithTimeZoneAdjustment;
	}

	private void setMinMaxValues(final double[][] valueSeries) {

		if (valueSeries == null || valueSeries.length == 0 || valueSeries[0] == null || valueSeries[0].length == 0) {

			_highValuesDouble = new double[1][2];
			_lowValuesDouble = new double[1][2];

			_visibleMaxValue = _visibleMinValue = 0;
			_originalMaxValue = _originalMinValue = 0;

		} else {

			_highValuesDouble = valueSeries;

			// set initial min/max value
			_visibleMaxValue = _visibleMinValue = valueSeries[0][0];

			// calculate min/max highValues
			for (final double[] valueSerie : valueSeries) {

				if (valueSerie == null) {
					continue;
				}

				for (final double value : valueSerie) {
					_visibleMaxValue = (_visibleMaxValue >= value) ? _visibleMaxValue : value;
					_visibleMinValue = (_visibleMinValue <= value) ? _visibleMinValue : value;
				}
			}

			/*
			 * force the min/max values to have not the same value this is necessary to display a
			 * visible line in the chart
			 */
			if (_visibleMinValue == _visibleMaxValue) {

				_visibleMaxValue++;

				if (_visibleMinValue > 0) {
					_visibleMinValue--;
				}
			}

			_originalMinValue = _visibleMinValue;
			_originalMaxValue = _visibleMaxValue;
		}
	}

	public void setNoLine(final boolean[] noLineSerie) {
		_noLine = noLineSerie;
	}

	/**
	 * Range markers are an area in the graph which will be displayed in a different color. This
	 * feature is use when tours are compared.
	 * 
	 * @param rangeMarkerStartIndex
	 * @param rangeMarkerEndIndex
	 */
	public void setRangeMarkers(final int[] rangeMarkerStartIndex, final int[] rangeMarkerEndIndex) {
		_rangeMarkerStartIndex = rangeMarkerStartIndex;
		_rangeMarkerEndIndex = rangeMarkerEndIndex;
	}

	public void setScalingFactors(final double scalingFactor, final double scalingMaxValue) {
		_scalingFactor = scalingFactor;
		_scalingMaxValue = scalingMaxValue;
	}

	/**
	 * @param dateTime
	 */
	public void setStartDateTime(final DateTime dateTime) {
		_startDateTime = dateTime;
	}

	/**
	 * @param startValue
	 *            The startValue to set.
	 */
	public void setStartValue(final double startValue) {
		_startValue = startValue;
	}

	/**
	 * set the start/end value index for the marker which is displayed in a different color, by
	 * default the synch marker is disabled
	 * 
	 * @param startIndex
	 * @param endIndex
	 */
	public void setSynchMarkerValueIndex(final int startIndex, final int endIndex) {
		_synchMarkerStartIndex = startIndex;
		_synchMarkerEndIndex = endIndex;
	}

	@Override
	public String toString() {
		return "[ChartDataXSerie]";//$NON-NLS-1$
	}

	public void xxxset_timeSerieWithCETAdjustment(final boolean _timeSerieWithCETAdjustment) {
		this._timeSerieWithTimeZoneAdjustment = _timeSerieWithCETAdjustment;
	}
}
