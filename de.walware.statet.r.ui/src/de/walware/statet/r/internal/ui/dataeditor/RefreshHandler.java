/*******************************************************************************
 * Copyright (c) 2010-2013 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.statet.r.internal.ui.dataeditor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import de.walware.statet.r.ui.dataeditor.RDataTableComposite;


public class RefreshHandler extends AbstractHandler {
	
	
	private final RDataTableComposite fTableComposite;
	
	
	public RefreshHandler(final RDataTableComposite tableProvider) {
		fTableComposite = tableProvider;
	}
	
	
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		fTableComposite.refresh();
		return null;
	}
	
}
