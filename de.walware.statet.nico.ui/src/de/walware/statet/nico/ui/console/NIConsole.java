/*******************************************************************************
 * Copyright (c) 2005 StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.statet.nico.ui.console;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.part.IPageBookViewPage;

import de.walware.eclipsecommons.ui.util.UIAccess;

import de.walware.statet.base.StatetPlugin;
import de.walware.statet.nico.core.runtime.SubmitType;
import de.walware.statet.nico.core.runtime.ToolProcess;
import de.walware.statet.nico.core.runtime.ToolStreamMonitor;
import de.walware.statet.nico.ui.util.ToolInfoGroup;


/**
 * A console to interact with controller using command-line-based interface.
 */
public class NIConsole extends IOConsole {
	
	
	public static final String NICONSOLE_TYPE = "de.walware.statet.nico.console"; //$NON-NLS-1$
	

	public static List<IConsoleView> getConsoleViews(IWorkbenchPage page) {
		
		List<IConsoleView> consoleViews = new ArrayList<IConsoleView>();
		
		IViewReference[] allReferences = page.getViewReferences();
		for (IViewReference reference : allReferences) {
			if (reference.getId().equals(IConsoleConstants.ID_CONSOLE_VIEW)) {
				IViewPart view = reference.getView(false);
				if (view != null) {
					consoleViews.add((IConsoleView) view);
				}
			}
		}
		return consoleViews;
	}

	
	private Map<String, IOConsoleOutputStream> fStreams = new HashMap<String, IOConsoleOutputStream>();
	private boolean fStreamsClosed;
	
	private ToolProcess fProcess;
	private NIConsoleColorAdapter fAdapter;
	
	private IDebugEventSetListener fDebugListener;
	private IPropertyChangeListener fFontListener;
	
	
	/**
	 * Constructs a new console.
	 * 
	 * @param name console name
	 */
	public NIConsole(ToolProcess process, NIConsoleColorAdapter adapter) {
		
		super(process.getAttribute(IProcess.ATTR_PROCESS_LABEL),
				NICONSOLE_TYPE, null, null, true);
		
		fProcess = process;
		fAdapter = adapter;
		Charset.defaultCharset();
		setImageDescriptor(ToolInfoGroup.computeImageDescriptor(fProcess));
		
		fStreamsClosed = fProcess.isTerminated();
		fAdapter.connect(process, this);

		fDebugListener = new IDebugEventSetListener() {
			public void handleDebugEvents(DebugEvent[] events) {
				
				EVENTS: for (DebugEvent event : events) {
					if (event.getSource() == fProcess) {
						switch (event.getKind()) {
						case DebugEvent.CHANGE:
							Object obj = event.getData();
							if (obj != null && obj instanceof String[]) {
								String[] attrChange = (String[]) obj;
								if (attrChange.length == 3 && IProcess.ATTR_PROCESS_LABEL.equals(attrChange[0])) {
									runSetName(attrChange[2]);
								}
							}
							continue EVENTS;
						case DebugEvent.TERMINATE:
							disconnect();
							continue EVENTS;
						}
					}
				}
			}
			
			private void runSetName(final String name) {
				UIAccess.getDisplay().syncExec(new Runnable() {
	                public void run() {
	                	setName(name);
	//                	ConsolePlugin.getDefault().getConsoleManager().warnOfContentChange(NIConsole.this);
	                	ConsolePlugin.getDefault().getConsoleManager().refresh(NIConsole.this);
	                }
	            });
			}
		};
		DebugPlugin.getDefault().addDebugEventListener(fDebugListener);
	}
	
	@Override
	protected void init() {
		
		super.init();
		
		fFontListener = new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (JFaceResources.TEXT_FONT.equals(event.getProperty()) )
					setFont(null);
			};
		};
		JFaceResources.getFontRegistry().addListener(fFontListener);
	}
	
	
	@Override
	protected void dispose() {
		
		super.dispose();
		
		DebugPlugin debugPlugin = DebugPlugin.getDefault();
		if (debugPlugin != null) {
			debugPlugin.removeDebugEventListener(fDebugListener);
		}
		fDebugListener = null;

		disconnect();

		JFaceResources.getFontRegistry().removeListener(fFontListener);
	}
	
	@Override
    public IPageBookViewPage createPage(IConsoleView view) {
		
        return new NIConsolePage(this, view);
    }


	public void connect(ToolStreamMonitor streamMonitor, String streamId, EnumSet<SubmitType> filter) {
		
		synchronized (fStreams) {
			if (fStreamsClosed) {
				return;
			}
			
			IOConsoleOutputStream stream = fStreams.get(streamId);
			if (stream == null) {
				stream = newOutputStream();
				stream.setColor(fAdapter.getColor(streamId));
				fStreams.put(streamId, stream);
			}
			
			final IOConsoleOutputStream out = stream;
			streamMonitor.addListener(new IStreamListener() {
				public void streamAppended(String text, IStreamMonitor monitor) {
				    try {
						out.write(text);
					} catch (IOException e) {
						StatetPlugin.logUnexpectedError(e);
					}
				}
			}, filter);
		}
	}

    public IOConsoleOutputStream getStream(String streamId) {
		
		synchronized (fStreams) {
			return fStreams.get(streamId);
		}
	}

	private void disconnect() {
		
		synchronized (fStreams) {
			if (fStreamsClosed) {
				return;
			}
			
			for (IOConsoleOutputStream stream : fStreams.values()) {
				try {
					stream.close();
				} catch (IOException e) {
					StatetPlugin.logUnexpectedError(e);
				}
			}
			fStreamsClosed = true;

			fAdapter.disconnect();
			fAdapter = null;
		}
	}

	public ToolProcess getProcess() {
		
		return fProcess;
	}
}