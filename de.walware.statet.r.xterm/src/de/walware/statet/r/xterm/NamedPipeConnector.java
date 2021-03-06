/*******************************************************************************
 * Copyright (c) 2006-2013 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.statet.r.xterm;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import de.walware.statet.r.launching.IRCodeSubmitConnector;


public class NamedPipeConnector implements IRCodeSubmitConnector {
	
	
	private final String fLineSeparator;
	private final String fPipeName;
	
	
	public NamedPipeConnector() throws CoreException {
		
		fLineSeparator = System.getProperty("line.separator");
		fPipeName = System.getProperty("user.home") + "/.r-eclipse-pipe";
	}
	
	
	@Override
	public boolean submit(final List<String> lines, boolean gotoConsole) throws CoreException {
		// Initializing the pipe/file each time has the advantages:
		//  - No problem if pipe/file is deleted during one Eclipse session.
		//  - The pipe/file can be closed.
		
		RandomAccessFile pipe = null;
		try {
			pipe = new RandomAccessFile(fPipeName, "rwd");
			
			for (String line : lines) {
				pipe.writeChars(line);
				pipe.writeChars(fLineSeparator);
			}
			return true;
		}
		catch (IOException e) {
			
			throw new CoreException(new Status(
					IStatus.ERROR,
					RXtermPlugin.ID,
					-1,
					"Error occured when writing to the named pipe ('"+fPipeName+"').",
					e));
		}
		finally {
			if (pipe != null) {
				try {
					pipe.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	@Override
	public void gotoConsole() throws CoreException {
		
	}
	
}
