/*
 *  Freeplane - mind map editor
 *  Copyright (C) 2008 Dimitry Polivaev
 *
 *  This file author is Dimitry Polivaev
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.freeplane.features.common.note;

import org.freeplane.core.filter.condition.ConditionFactory;
import org.freeplane.core.filter.condition.ICondition;
import org.freeplane.core.filter.condition.NodeCondition;
import org.freeplane.core.model.NodeModel;
import org.freeplane.core.resources.ResourceBundles;
import org.freeplane.core.util.HtmlTools;
import org.freeplane.n3.nanoxml.XMLElement;

class NoteContainsCondition extends NodeCondition {
	static final String NAME = "node_contains_condition";
	static final String VALUE = "VALUE";

	static ICondition load(final XMLElement element) {
		return new NoteContainsCondition(element.getAttribute(NoteContainsCondition.VALUE, null));
	}

	final private String value;

	NoteContainsCondition(final String value) {
		super();
		this.value = value;
	}

	public boolean checkNode(final NodeModel node) {
		final String text = getText(node);
		if(text == null){
			return false;
		}
		return checkText(text) || HtmlTools.isHtmlNode(text) && checkText(HtmlTools.htmlToPlain(text));
	}

	protected String getText(final NodeModel node) {
	    return NoteModel.getNoteText(node);
    }

	private boolean checkText(final String plainTextContent) {
		return plainTextContent.indexOf(value) > -1;
	}

	@Override
	protected String createDesctiption() {
		return createDesctiption(false);
	}

	protected String createDesctiption(final boolean ignoreCase) {
	    final String nodeCondition = ResourceBundles.getText(NoteConditionController.FILTER_NOTE);
		final String simpleCondition = ResourceBundles.getText(ConditionFactory.FILTER_CONTAINS);
		return ConditionFactory.createDescription(nodeCondition, simpleCondition, value, ignoreCase);
    }

	public void toXml(final XMLElement element) {
		toXml(element, NAME);
	}

	protected void toXml(final XMLElement element, final String name) {
	    final XMLElement child = new XMLElement();
		child.setName(name);
		super.attributesToXml(child);
		child.setAttribute(NoteContainsCondition.VALUE, value);
		element.addChild(child);
    }
}