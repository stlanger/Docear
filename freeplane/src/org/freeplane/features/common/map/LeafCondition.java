package org.freeplane.features.common.map;

import org.freeplane.core.util.TextUtils;
import org.freeplane.features.common.filter.condition.ASelectableCondition;
import org.freeplane.features.common.filter.condition.ISelectableCondition;
import org.freeplane.n3.nanoxml.XMLElement;

public class LeafCondition extends ASelectableCondition {
	public static final String NAME = "node_leaf_condition";
	@Override
	protected String getName() {
		return NAME;
	}

	public boolean checkNode(NodeModel node) {
		return node.isLeaf();
	}

	public static ISelectableCondition load(XMLElement element) {
	    return new LeafCondition();
    }

	@Override
    protected String createDesctiption() {
	    return TextUtils.getText(NodeLevelConditionController.FILTER_LEAF);
    }
}