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

package de.walware.statet.r.core.pkgmanager;

import java.util.List;

import de.walware.statet.r.core.renv.IRPkg;


public interface IRPkgCollection<T extends IRPkg> {
	
	
	boolean containsByName(String name);
	List<T> getByName(String name);
	T getFirstByName(String name);
	
	List<String> getSources();
	IRPkgList<T> getBySource(String source);
	List<? extends IRPkgList<T>> getAll();
	
}
