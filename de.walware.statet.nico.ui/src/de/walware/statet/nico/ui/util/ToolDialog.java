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

package de.walware.statet.nico.ui.util;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import de.walware.ecommons.ui.util.LayoutUtil;

import de.walware.statet.nico.core.runtime.ToolProcess;


/**
 * A dialog that has a title area for displaying a title and an image as well as a common area for
 * displaying a description, a message, or an error message (top) and a tool information area
 * (bottom).
 */
public abstract class ToolDialog extends TitleAreaDialog {
	
	
	private final String fDialogTitle;
	private final Image fDialogImage;
	
	private final ToolProcess fTool;
	private final int fToolInfoFlags;
	
	
	public ToolDialog(final ToolProcess tool, final Shell parentShell,
			final Image dialogImage, final String dialogTitle) {
		this(tool, parentShell, dialogImage, dialogTitle, 0);
	}
	
	public ToolDialog(final ToolProcess tool, final Shell parentShell,
			final Image dialogImage, final String dialogTitle, int toolInfoFlags) {
		super(parentShell);
		
		fDialogImage = dialogImage;
		fDialogTitle = dialogTitle;
		
		fTool = tool;
		fToolInfoFlags = toolInfoFlags;
	}
	
	
	@Override
	protected boolean isResizable() {
		return true;
	}
	
	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		if (fDialogImage != null) {
			shell.setImage(fDialogImage);
		}
		if (fDialogTitle != null) {
			shell.setText(fDialogTitle);
		}
	}
	
	protected ToolProcess getTool() {
		return fTool;
	}
	
	@Override
	protected IDialogSettings getDialogBoundsSettings() {
		return getDialogSettings();
	}
	
	
	protected IDialogSettings getDialogSettings() {
		return null;
	}
	
	@Override
	protected Control createDialogArea(final Composite parent) {
		final Composite dialogArea = (Composite) super.createDialogArea(parent);
		
		final Composite composite = new Composite(dialogArea, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLayout(LayoutUtil.applyDialogDefaults(new GridLayout(), 1));
		
		final Control content = createDialogContent(composite);
		content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		addToolInfo(composite);
		
		return dialogArea;
	}
	
	protected abstract Control createDialogContent(Composite parent);
	
	protected void addToolInfo(final Composite parent) {
		LayoutUtil.addSmallFiller(parent, false);
		
		final ToolInfoGroup info = new ToolInfoGroup(parent, fToolInfoFlags, fTool);
		info.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
	}
	
}
