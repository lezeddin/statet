/*******************************************************************************
 * Copyright (c) 2008-2013 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.statet.r.internal.ui.editors;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import de.walware.ecommons.ltk.IElementName;
import de.walware.ecommons.ltk.IModelElement;
import de.walware.ecommons.ltk.LTK;
import de.walware.ecommons.ltk.ui.IElementLabelProvider;
import de.walware.ecommons.ltk.ui.sourceediting.assist.AssistInvocationContext;
import de.walware.ecommons.ltk.ui.sourceediting.assist.ElementNameCompletionProposal;
import de.walware.ecommons.text.ui.BracketLevel.InBracketPosition;

import de.walware.statet.nico.ui.console.InputSourceViewer;

import de.walware.statet.r.core.IRCoreAccess;
import de.walware.statet.r.core.RCodeStyleSettings;
import de.walware.statet.r.core.RCore;
import de.walware.statet.r.core.RSymbolComparator;
import de.walware.statet.r.core.model.ArgsDefinition;
import de.walware.statet.r.core.model.IRElement;
import de.walware.statet.r.core.model.IRMethod;
import de.walware.statet.r.core.model.RElementName;
import de.walware.statet.r.core.rsource.RHeuristicTokenScanner;
import de.walware.statet.r.ui.RUI;
import de.walware.statet.r.ui.sourceediting.RBracketLevel;


public class RElementCompletionProposal extends ElementNameCompletionProposal {
	
	
	public static class ArgumentProposal extends RElementCompletionProposal {
		
		
		public ArgumentProposal(final AssistInvocationContext context, 
				final IElementName replacementName, final int replacementOffset,
				final int relevance, final IElementLabelProvider labelProvider) {
			super(context, replacementName, replacementOffset, null, relevance+100, labelProvider);
		}
		
		
		@Override
		public Image getImage() {
			return RUI.getImage(RUI.IMG_OBJ_ARGUMENT_ASSIGN);
		}
		
		@Override
		public String getDisplayString() {
			return fReplacementName.getDisplayName();
		}
		
		@Override
		public StyledString getStyledDisplayString() {
			return new StyledString(fReplacementName.getDisplayName());
		}
		
		@Override
		protected boolean isArgumentName() {
			return true;
		}
		
	}
	
	public static class ContextInformationProposal extends RElementCompletionProposal {
		
		
		public ContextInformationProposal(final AssistInvocationContext context,
				final IElementName elementName, final int replacementOffset,
				final IModelElement element, final int relevance,
				final IElementLabelProvider labelProvider) {
			super(context, elementName, replacementOffset, element, relevance, labelProvider);
		}
		
		
		@Override
		public boolean validate(final IDocument document, final int offset, final DocumentEvent event) {
			return (offset == fContext.getInvocationOffset());
		}
		
		@Override
		public boolean isAutoInsertable() {
			return true;
		}
		
		@Override
		protected void doApply(final char trigger, final int stateMask, final int caretOffset, final int replacementOffset, final int replacementLength) throws BadLocationException {
			final ApplyData data = getApplyData();
			setCursorPosition(-1);
			data.setContextInformation(new RArgumentListContextInformation(getReplacementOffset(), (IRMethod) fElement));
		}
		
	}
	
	
	static final class ApplyData {
		
		private final AssistInvocationContext fContext;
		private final SourceViewer fViewer;
		private final IDocument fDocument;
		
		private RHeuristicTokenScanner fScanner;
		
		private IContextInformation fContextInformation;
		
		ApplyData(final AssistInvocationContext context) {
			fContext = context;
			fViewer = context.getSourceViewer();
			fDocument = fViewer.getDocument();
		}
		
		public SourceViewer getViewer() {
			return fViewer;
		}
		
		public IDocument getDocument() {
			return fDocument;
		}
		
		public RHeuristicTokenScanner getScanner() {
			if (fScanner == null) {
				fScanner = (RHeuristicTokenScanner) LTK.getModelAdapter(
						fContext.getEditor().getModelTypeId(), RHeuristicTokenScanner.class );
			}
			return fScanner;
		}
		
		public void setContextInformation(final IContextInformation info) {
			fContextInformation = info;
		}
		
		public IContextInformation getContextInformation() {
			return fContextInformation;
		}
		
	}
	
	
	private static final boolean isFollowedByOpeningBracket(final ApplyData util, final int forwardOffset) {
		final RHeuristicTokenScanner scanner = util.getScanner();
		scanner.configure(util.getDocument());
		final int idx = scanner.findAnyNonBlankForward(forwardOffset, RHeuristicTokenScanner.UNBOUND, false);
		return (idx >= 0
				&&  scanner.getChar() == '(' );
	}
	
	private static final boolean isClosedBracket(final ApplyData data, final int backwardOffset, final int forwardOffset) {
		final int searchType = RHeuristicTokenScanner.ROUND_BRACKET_TYPE;
		int[] balance = new int[3];
		balance[searchType]++;
		final RHeuristicTokenScanner scanner = data.getScanner();
		scanner.configureDefaultParitions(data.getDocument());
		balance = scanner.computeBracketBalance(backwardOffset, forwardOffset, balance, searchType);
		return (balance[searchType] <= 0);
	}
	
	private static final boolean isFollowedByEqualAssign(final ApplyData data, final int forwardOffset) {
		final RHeuristicTokenScanner scanner = data.getScanner();
		scanner.configure(data.getDocument());
		final int idx = scanner.findAnyNonBlankForward(forwardOffset, RHeuristicTokenScanner.UNBOUND, false);
		return (idx >= 0
				&&  scanner.getChar() == '=' );
	}
	
	private static final boolean isFollowedByAssign(final ApplyData util, final int forwardOffset) {
		final RHeuristicTokenScanner scanner = util.getScanner();
		scanner.configure(util.getDocument());
		final int idx = scanner.findAnyNonBlankForward(forwardOffset, RHeuristicTokenScanner.UNBOUND, false);
		return (idx >= 0
				&& (scanner.getChar() == '=' || scanner.getChar() == '<') );
	}
	
	
	private ApplyData fApplyData;
	
	
	public RElementCompletionProposal(final AssistInvocationContext context, final IElementName elementName, 
			final int replacementOffset, final IModelElement element,
			final int relevance, final IElementLabelProvider labelProvider) {
		super(context, elementName, replacementOffset, element, 80+relevance, labelProvider);
	}
	
	
	@Override
	protected String getPluginId() {
		return RUI.PLUGIN_ID;
	}
	
	protected final ApplyData getApplyData() {
		if (fApplyData == null) {
			fApplyData = new ApplyData(fContext);
		}
		return fApplyData;
	}
	
	
	@Override
	protected int computeReplacementLength(final int replacementOffset, final Point selection, final int caretOffset, final boolean overwrite) {
		// keep in synch with RSimpleCompletionProposal
		final int end = Math.max(caretOffset, selection.x + selection.y);
		if (overwrite) {
			final ApplyData data = getApplyData();
			final RHeuristicTokenScanner scanner = data.getScanner();
			scanner.configure(data.getDocument());
			final IRegion word = scanner.findRWord(end, false, true);
			if (word != null) {
				return (word.getOffset() + word.getLength() - replacementOffset);
			}
		}
		return (end - replacementOffset);
	}
	
	@Override
	public boolean validate(final IDocument document, final int offset, final DocumentEvent event) {
		// keep in synch with RSimpleCompletionProposal
		try {
			int start = getReplacementOffset();
			int length = offset - getReplacementOffset();
			if (length > 0 && document.getChar(start) == '`') {
				start++;
				length--;
			}
			if (length > 0 && document.getChar(start+length-1) == '`') {
				length--;
			}
			final String prefix = document.get(start, length);
			final String replacement = fReplacementName.getSegmentName();
			if (new RSymbolComparator.PrefixPattern(prefix).matches(replacement)) {
				return true;
			}
		}
		catch (final BadLocationException e) {
			// ignore concurrently modified document
		}
		return false;
	}
	
	@Override
	protected void doApply(final char trigger, final int stateMask, final int caretOffset, final int replacementOffset, int replacementLength) throws BadLocationException {
		final ApplyData data = getApplyData();
		final IDocument document = data.getDocument();
		
		final boolean assignmentFunction = isFunction()
				&& fReplacementName.getNextSegment() == null && fReplacementName.getSegmentName().endsWith("<-");
		final IElementName elementName;
		if (assignmentFunction) {
			elementName = RElementName.create(RElementName.MAIN_DEFAULT, fReplacementName.getSegmentName().substring(0, fReplacementName.getSegmentName().length()-2));
		}
		else {
			elementName = fReplacementName;
		}
		final StringBuilder replacement = new StringBuilder(elementName.getDisplayName());
		int cursor = replacement.length();
		if (replacementLength > 0 && document.getChar(replacementOffset) == '`' && replacement.charAt(0) != '`') {
			if (replacement.length() == elementName.getSegmentName().length() 
					&& replacementOffset+replacementLength < document.getLength()
					&& document.getChar(replacementOffset+replacementLength) == '`') {
				replacementLength++;
			}
			replacement.insert(elementName.getSegmentName().length(), '`');
			replacement.insert(0, '`');
			cursor += 2;
		}
		
		int mode = 0;
		if (isArgumentName()) {
			if (!isFollowedByEqualAssign(data, replacementOffset+replacementLength)) {
				final RCodeStyleSettings codeStyle = getCodeStyleSettings();
				final String argAssign = codeStyle.getArgAssignString();
				replacement.append(argAssign);
				cursor += argAssign.length();
			}
		}
		else if (isFunction()) {
			mode = 1;
			final IRMethod rMethod = (IRMethod) fElement;
			
			if (replacementOffset+replacementLength < document.getLength()-1
					&& document.getChar(replacementOffset+replacementLength) == '(') {
				cursor ++;
				mode = 10;
			}
			else if (!isFollowedByOpeningBracket(data, replacementOffset+replacementLength)) {
				replacement.append('(');
				cursor ++;
				mode = 11;
			}
			if (mode >= 10) {
				if (mode == 11
						&& !isClosedBracket(data, replacementOffset, replacementOffset+replacementLength)) {
					replacement.append(')');
					mode = 101;
					
					if (assignmentFunction && !isFollowedByAssign(data, replacementOffset+replacementLength)) {
						replacement.append(" <- ");
						mode += 4;
					}
				}
				
				final ArgsDefinition argsDef = rMethod.getArgsDefinition();
				if (argsDef == null || argsDef.size() > 0 || mode == 11) {
					data.setContextInformation(new RArgumentListContextInformation(replacementOffset + cursor, rMethod));
				}
				else {
					cursor ++;
					mode = 200;
				}
			}
			
		}
		
		document.replace(replacementOffset, replacementLength, replacement.toString());
		setCursorPosition(replacementOffset + cursor);
		if (mode > 100 && mode < 200) {
			createLinkedMode(data, replacementOffset + cursor - 1, (mode-100)).enter();
		}
	}
	
	private LinkedModeUI createLinkedMode(final ApplyData util, final int offset, final int exitAddition) throws BadLocationException {
		final LinkedModeModel model = new LinkedModeModel();
		int pos = 0;
		
		final LinkedPositionGroup group = new LinkedPositionGroup();
		final InBracketPosition position = RBracketLevel.createPosition('(', util.getDocument(),
				offset + 1, 0, pos++);
		group.addPosition(position);
		model.addGroup(group);
		
		model.forceInstall();
		
		final RBracketLevel level = new RBracketLevel(util.getDocument(),
				fContext.getEditor().getPartitioning().getPartitioning(),
				position, (util.getViewer() instanceof InputSourceViewer), true);
		
		/* create UI */
		final LinkedModeUI ui = new LinkedModeUI(model, util.getViewer());
		ui.setCyclingMode(LinkedModeUI.CYCLE_NEVER);
		ui.setExitPosition(util.getViewer(), offset+exitAddition, 0, pos);
		ui.setSimpleMode(true);
		ui.setExitPolicy(level);
		return ui;
	}
	
	protected boolean isFunction() {
		return (fElement != null
				&& (fElement.getElementType() & IRElement.MASK_C1) == IRElement.C1_METHOD);
	}
	
	protected boolean isArgumentName() {
		return false;
	}
	
	protected RCodeStyleSettings getCodeStyleSettings() {
		final IRCoreAccess access = (IRCoreAccess) fContext.getEditor().getAdapter(IRCoreAccess.class);
		if (access != null) {
			return access.getRCodeStyle();
		}
		return RCore.getWorkbenchAccess().getRCodeStyle();
	}
	
	@Override
	public IContextInformation getContextInformation() {
		return getApplyData().getContextInformation();
	}
	
}
