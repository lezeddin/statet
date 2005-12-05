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

package de.walware.statet.r.internal.ui.preferences;

import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;

import de.walware.eclipsecommon.templates.TemplateVariableProcessor;
import de.walware.statet.ext.ui.editors.StatextSourceViewerConfiguration;
import de.walware.statet.ext.ui.preferences.ICodeGenerationTemplatesCategory;
import de.walware.statet.ext.ui.preferences.TemplateViewerConfigurationProvider;
import de.walware.statet.r.ui.RUiPlugin;
import de.walware.statet.r.ui.editors.RDocumentSetupParticipant;


/**
 * Integrates the R templates into the common StatET template
 * preference page. 
 * 
 * @author Stephan Wahlbrink
 */
public class RCodeTemplatesProvider implements ICodeGenerationTemplatesCategory {

	
	public RCodeTemplatesProvider() {
	}
	
	public TemplateStore getTemplateStore() {
		return RUiPlugin.getDefault().getRCodeGenerationTemplateStore();
	}

	public ContextTypeRegistry getContextTypeRegistry() {
		return RUiPlugin.getDefault().getRCodeGenerationTemplateContextRegistry();
	}

	public TemplateViewerConfigurationProvider getEditTemplateDialogConfiguation(final TemplateVariableProcessor processor) {

		StatextSourceViewerConfiguration configuration = new RTemplateSourceViewerConfiguration(processor);
		
		return new TemplateViewerConfigurationProvider(
				configuration,
				new RDocumentSetupParticipant(),
				RUiPlugin.getDefault().getPreferenceStore() );
	}

}
