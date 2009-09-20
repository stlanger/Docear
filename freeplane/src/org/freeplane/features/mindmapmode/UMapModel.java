/*
 *  Freeplane - mind map editor
 *  Copyright (C) 2009 Dimitry
 *
 *  This file author is Dimitry
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
package org.freeplane.features.mindmapmode;

import org.freeplane.core.modecontroller.ModeController;
import org.freeplane.core.model.MapModel;
import org.freeplane.core.model.NodeModel;
import org.freeplane.core.undo.IUndoHandler;
import org.freeplane.core.undo.UndoHandler;

/**
 * @author Dimitry Polivaev
 * 13.09.2009
 */
public class UMapModel extends MapModel{
	protected final IUndoHandler undoHandler;

	public UMapModel(final NodeModel root, final ModeController modeController){
		super(modeController, root);
		undoHandler = new UndoHandler();
	}

	public IUndoHandler getUndoHandler() {
    	return undoHandler;
    }
}