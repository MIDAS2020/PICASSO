/*
 * =========================================================== JFreeChart : a free chart library for
 * the Java(tm) platform ===========================================================
 *
 * (C) Copyright 2000-2008, by Object Refinery Limited and Contributors.
 *
 * Project Info: http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. in the United States and
 * other countries.]
 *
 * ------------------------- CombinedCategoryPlot.java ------------------------- (C) Copyright 2008,
 * by Richard West and Contributors.
 *
 * Original Author: Richard West, Advanced Micro Devices, Inc.; Contributor(s): David Gilbert (for
 * Object Refinery Limited);
 *
 * Changes ------- 02-Feb-2007 : Version 1, contributed by Richard West - see patch 1924543 (DG);
 */

package gblend.result;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedDomainCategoryPlot;
import org.jfree.data.Range;

import java.util.List;

/**
 * A specialised form of {@link CombinedDomainCategoryPlot} where the subplots share not only the
 * same x-axis, but also the same y-axis.
 */
class CombinedCategoryPlot extends CombinedDomainCategoryPlot {

  private static final long serialVersionUID = 1L;
  private static final double GAP = 10.0;

  /**
   * Creates a new instance with the specified axes.
   *
   * @param domainAxis the x-axis.
   */
  public CombinedCategoryPlot(CategoryAxis domainAxis) {
    super(domainAxis);
    super.setGap(GAP);
  }

  /**
   * Adds a new subplot with weight <code>1</code>.
   *
   * @param subplot the subplot.
   */
  @Override
  public void add(CategoryPlot subplot) {
    this.add(subplot, 1);
  }

  /**
   * Adds a new subplot with the specified weight.
   *
   * @param subplot the subplot.
   * @param weight  the weight for the subplot.
   */
  public void add(CategoryPlot subplot, int weight, ValueAxis lRange) {
    super.add(subplot, weight);
    subplot.setRangeAxis(0, lRange, false);
    super.setRangeAxis(lRange);
    if (null == lRange) {
      return;
    }

    lRange.configure();
  }

  /**
   * Returns the bounds of the data values that will be plotted against the specified axis.
   *
   * @param axis the axis.
   * @return The bounds.
   */
  @Override
  public Range getDataRange(ValueAxis axis) {
    Range lResult = null;
    @SuppressWarnings("unchecked")
    List<CategoryPlot> subplots = getSubplots();
    for (CategoryPlot subplot : subplots) {
      lResult = Range.combine(lResult, subplot.getDataRange(axis));
    }
    return lResult;
  }

  /**
   * Sets the range axis that is shared by all the subplots.
   *
   * @param axis the axis.
   */
  @Override
  public void setRangeAxis(ValueAxis axis) {
    @SuppressWarnings("unchecked")
    List<CategoryPlot> subplots = getSubplots();
    for (CategoryPlot subplot : subplots) {
      subplot.setRangeAxis(0, axis, false);
    }

    super.setRangeAxis(axis);
    if (null == axis) {
      return;
    }

    axis.configure();
  }

}
