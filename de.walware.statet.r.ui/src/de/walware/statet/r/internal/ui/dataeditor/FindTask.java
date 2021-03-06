/*******************************************************************************
 * Copyright (c) 2012-2013 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.statet.r.internal.ui.dataeditor;


public final class FindTask {
	
	
	public final String expression;
	
	public final int rowIdx;
	public final int columnIdx;
	public final boolean firstInRow;
	public final boolean forward;
	
	public final IFindFilter filter;
	
	
	public FindTask(final String expression,
			final int rowIdx, final int columnIdx,
			final boolean firstInRow, final boolean forward,
			final IFindFilter filter) {
		this.expression = expression;
		
		this.rowIdx = rowIdx;
		this.columnIdx = columnIdx;
		this.firstInRow = firstInRow;
		this.forward = forward;
		
		this.filter = filter;
	}
	
	
	@Override
	public int hashCode() {
		return expression.hashCode();
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof FindTask)) {
			return false;
		}
		final FindTask other = (FindTask) obj;
		return (expression.equals(other.expression)
				&& firstInRow == other.firstInRow);
	}
	
}
