/*******************************************************************************
 * Copyright (c) 2009-2013 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.statet.r.internal.rdata;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import de.walware.rj.data.RJIO;
import de.walware.rj.data.RList;
import de.walware.rj.data.RObjectFactory;
import de.walware.rj.data.RStore;
import de.walware.rj.data.defaultImpl.ExternalizableRObject;
import de.walware.rj.data.defaultImpl.RObjectFactoryImpl;

import de.walware.statet.r.core.model.IRLangElement;
import de.walware.statet.r.core.model.RElementName;


public final class ROtherVar extends CombinedElement
		implements ExternalizableRObject {
	
	
	private String fClassName;
	
	protected RList fAttributes;
	
	
	public ROtherVar(final String className) {
		if (className == null) {
			throw new NullPointerException();
		}
		fClassName = className;
	}
	
	public ROtherVar(final RJIO io, final RObjectFactory factory, final CombinedElement parent, final RElementName name) throws IOException {
		fParent = parent;
		fElementName = name;
		readExternal(io, factory);
	}
	
	@Override
	public void readExternal(final RJIO io, final RObjectFactory factory) throws IOException {
		final int options = io.readInt();
		fClassName = io.readString();
		if ((options & RObjectFactoryImpl.F_WITH_ATTR) != 0) {
			fAttributes = factory.readAttributeList(io);
		}
	}
	
	@Override
	public void writeExternal(final RJIO io, final RObjectFactory factory) throws IOException {
		final boolean withAttr = ((io.flags & RObjectFactoryImpl.F_WITH_ATTR) != 0) && (fAttributes != null);
		io.writeInt((withAttr) ? RObjectFactoryImpl.F_WITH_ATTR : 0);
		io.writeString(fClassName);
		if (withAttr) {
			factory.writeAttributeList(fAttributes, io);
		}
	}
	
	
	@Override
	public byte getRObjectType() {
		return TYPE_OTHER;
	}
	
	@Override
	public String getRClassName() {
		return fClassName;
	}
	
	@Override
	public int getLength() {
		return 0;
	}
	
	@Override
	public RStore getData() {
		return null;
	}
	
	
	@Override
	public int getElementType() {
		return R_GENERAL_VARIABLE;
	}
	
	@Override
	public boolean hasModelChildren(final Filter filter) {
		return false;
	}
	
	@Override
	public List<? extends IRLangElement> getModelChildren(final Filter filter) {
		return Collections.EMPTY_LIST;
	}
	
}
