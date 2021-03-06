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

package de.walware.ecommons.emf.internal.forms;

import java.util.List;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.edit.command.CopyToClipboardCommand;
import org.eclipse.jface.viewers.IStructuredSelection;

import de.walware.ecommons.collections.ConstList;
import de.walware.ecommons.emf.core.util.IEMFEditPropertyContext;


public class CopyEObjectHandler extends EditEObjectHandler {
	
	
	public CopyEObjectHandler() {
	}
	
	
	@Override
	protected boolean isValidSelection(final IStructuredSelection selection) {
		return (super.isValidSelection(selection) && !selection.isEmpty());
	}
	
	@Override
	protected Command createCommand(final IStructuredSelection selection,
			final IEMFEditPropertyContext context) {
		final List<Object> collection = new ConstList<Object>(selection.toList());
		return CopyToClipboardCommand.create(context.getEditingDomain(), collection);
	}
	
}
