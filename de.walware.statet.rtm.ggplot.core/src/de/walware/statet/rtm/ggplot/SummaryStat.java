/**
 * Copyright (c) 2012-2013 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 */
package de.walware.statet.rtm.ggplot;

import de.walware.statet.rtm.rtdata.types.RTypedExpr;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Summary Stat</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link de.walware.statet.rtm.ggplot.SummaryStat#getYFun <em>YFun</em>}</li>
 * </ul>
 * </p>
 *
 * @see de.walware.statet.rtm.ggplot.GGPlotPackage#getSummaryStat()
 * @model
 * @generated
 */
public interface SummaryStat extends Stat {
	/**
	 * Returns the value of the '<em><b>YFun</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>YFun</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>YFun</em>' attribute.
	 * @see #setYFun(RTypedExpr)
	 * @see de.walware.statet.rtm.ggplot.GGPlotPackage#getSummaryStat_YFun()
	 * @model dataType="de.walware.statet.rtm.rtdata.RFunction"
	 * @generated
	 */
	RTypedExpr getYFun();

	/**
	 * Sets the value of the '{@link de.walware.statet.rtm.ggplot.SummaryStat#getYFun <em>YFun</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>YFun</em>' attribute.
	 * @see #getYFun()
	 * @generated
	 */
	void setYFun(RTypedExpr value);

} // SummaryStat
