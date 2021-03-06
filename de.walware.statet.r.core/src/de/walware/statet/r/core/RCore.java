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

package de.walware.statet.r.core;

import de.walware.statet.r.core.model.IRModelManager;
import de.walware.statet.r.core.pkgmanager.IRPkgManager;
import de.walware.statet.r.core.renv.IREnv;
import de.walware.statet.r.core.renv.IREnvManager;
import de.walware.statet.r.core.rhelp.IRHelpManager;
import de.walware.statet.r.internal.core.RCorePlugin;


/**
 * Provides r core services.
 */
public class RCore {
	
	
	public static final String PLUGIN_ID = "de.walware.statet.r.core"; //$NON-NLS-1$
	
	
	/**
	 * Usually used, if no other context (e.g. project) specified.
	 */
	public static IRCoreAccess getWorkbenchAccess() {
		return RCorePlugin.getDefault().getWorkspaceRCoreAccess();
	}
	
	/**
	 * Usually only used in special cases like preference dialogs.
	 */
	public static IRCoreAccess getDefaultsAccess() {
		return RCorePlugin.getDefault().getDefaultsRCoreAccess();
	}
	
	/**
	 * @return the manager with with shared configurations of the R environments.
	 */
	public static IREnvManager getREnvManager() {
		return RCorePlugin.getDefault().getREnvManager();
	}
	
	/**
	 * @return the manager for the R model
	 */
	public static IRModelManager getRModelManager() {
		return RCorePlugin.getDefault().getRModelManager();
	}
	
	public static IRPkgManager getRPkgManager(IREnv env) {
		return RCorePlugin.getDefault().getREnvPkgManager().getManager(env);
	}
	
	public static IRHelpManager getRHelpManager() {
		return RCorePlugin.getDefault().getRHelpManager();
	}
	
}
