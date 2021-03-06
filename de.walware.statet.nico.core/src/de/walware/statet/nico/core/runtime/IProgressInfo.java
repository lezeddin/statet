/*******************************************************************************
 * Copyright (c) 2006-2013 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.statet.nico.core.runtime;

import de.walware.ecommons.ts.IToolRunnable;


/**
 * Progress informations about the current task.
 * It comply a fixed state of a progress monitor.
 */
public interface IProgressInfo {
	
	
	/**
	 * Main label of current task.
	 */
	public String getLabel();
	
	/**
	 * Label of current subtask.
	 */
	public String getSubLabel();
	
	/**
	 * Worked part of IToolRunnable.TOTAL_WORK = 10000.
	 */
	public int getWorked();
	
	/**
	 * Current runnable
	 * @return the runnable, can be <code>null</code>
	 */
	public IToolRunnable getRunnable();
	
}
