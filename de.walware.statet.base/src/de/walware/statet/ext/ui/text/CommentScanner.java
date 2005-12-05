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

package de.walware.statet.ext.ui.text;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.WordRule;

import de.walware.eclipsecommon.ui.util.ColorManager;
import de.walware.statet.base.StatetPreferenceConstants;


/**
 * AbstractJavaCommentScanner.java
 */
public class CommentScanner extends StatextTextScanner {

	private static class TaskTagDetector implements IWordDetector {

		public boolean isWordStart(char c) {
			return Character.isLetterOrDigit(c);
		}

		public boolean isWordPart(char c) {
			return Character.isLetterOrDigit(c);
		}
	}

	private class TaskTagRule extends WordRule {

		private IToken fToken;

		public TaskTagRule(IToken token, IToken defaultToken) {
			super(new TaskTagDetector(), defaultToken);
			fToken = token;
		}
	
		public void clearTaskTags() {
			fWords.clear();
		}
	
		public void addTaskTags(String value) {
			String[] tasks = value.split(","); //$NON-NLS-1$
			for (int i= 0; i < tasks.length; i++) {
				if (tasks[i].length() > 0) {
					addWord(tasks[i], fToken);
				}
			}
		}
	}
	
	
	private TaskTagRule fTaskTagRule;
	
	private String fCommentTokenKey;
	private String fTaskTokenKey;
	
	public CommentScanner(ColorManager colorManager, IPreferenceStore preferenceStore, 
			String commentTokenKey, String taskTokenKey) {
		
		super(colorManager, preferenceStore);

		fCommentTokenKey = commentTokenKey;
		fTaskTokenKey = taskTokenKey;
		
		initialize();
	}
	
	protected List<IRule> createRules() {
		
		List<IRule> list = new ArrayList<IRule>();
		
		IToken defaultToken = getToken(fCommentTokenKey);
		IToken taskToken = getToken(fTaskTokenKey);
		
		// Add rule for Task Tags.
		fTaskTagRule = new TaskTagRule(taskToken, defaultToken);
		list.add(fTaskTagRule);
		loadTaskTags();

		setDefaultReturnToken(defaultToken);

		return list;
	}

	public void loadTaskTags() {
		
		fTaskTagRule.clearTaskTags();
		String tasks = fPreferenceStore.getString(StatetPreferenceConstants.TASK_TAGS);
		if (tasks != null) {
			fTaskTagRule.addTaskTags(tasks);
		}
	}
}
