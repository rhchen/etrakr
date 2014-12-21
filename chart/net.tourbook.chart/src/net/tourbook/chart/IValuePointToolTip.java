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
package net.tourbook.chart;

import net.tourbook.common.PointLong;

import org.eclipse.swt.widgets.Shell;

/**
 * This tooltip is displayed when the mouse is hovered over a value point in a line graph and
 * displays value point information.
 */
public interface IValuePointToolTip {

	/**
	 * @return Returns the tool tip shell or <code>null</code> when the tool tip is not visible
	 */
	Shell getToolTipShell();

	/**
	 * Hide the tooltip.
	 */
	void hide();

	/**
	 * Set chart margins, this is used to position the value point tool tip correctly at the chart
	 * border.
	 * 
	 * @param marginTop
	 * @param marginBottom
	 */
	void setChartMargins(int marginTop, int marginBottom);

	/**
	 * Mouse has been moved to a new or still the old value index.
	 * 
	 * @param valueIndex
	 * @param devXMouseMove
	 * @param devYMouseMove
	 * @param valueDevPosition
	 *            Position where the value is painted in the chart
	 * @param chartZoomFactor
	 */
	void setValueIndex(	int valueIndex,
						int devXMouseMove,
						int devYMouseMove,
						PointLong valueDevPosition,
						double chartZoomFactor);

}
