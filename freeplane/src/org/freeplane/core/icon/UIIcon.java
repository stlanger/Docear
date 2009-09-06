/*
 *  Freeplane - mind map editor
 *  Copyright (C) 2009 Tamas Eppel
 *
 *  This file author is Tamas Eppel
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
package org.freeplane.core.icon;

import java.io.File;
import java.net.URISyntaxException;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import org.freeplane.core.icon.factory.ImageIconFactory;
import org.freeplane.core.resources.ResourceController;
import org.freeplane.core.util.LogTool;

/**
 * Base class for all icons used in FreePlane.
 *
 * @author Tamas Eppel
 *
 */
public class UIIcon implements IIconInformation, Comparable<UIIcon> {

	public static final UIIcon ICON_NOT_FOUND = new UIIcon("icon_not_found", "IconNotFound.png", "icon not found", "");

	private static final String DEFAULT_IMAGE_PATH = "/images";

	protected static final String SEPARATOR = "/";

	protected static final String THEME_FOLDER_KEY = "icon.theme.folder";

	protected static final ResourceController RESOURCE_CONTROLLER = ResourceController.getResourceController();

	protected final String name;
	protected final String fileName;
	protected final String description;
	protected final String shortcutKey;

	public UIIcon(final String name, final String fileName) {
		this.name = name;
		this.fileName = fileName;
		this.description = "";
		this.shortcutKey = "?";
	}

	public UIIcon(final String name, final String fileName, final String description) {
		this.name = name;
		this.fileName = fileName;
		this.description = description;
		this.shortcutKey = "?";
	}

	public UIIcon(final String name, final String fileName,
			final String description, final String shortcutKey) {
		this.name = name;
		this.fileName = fileName;
		this.description = description;
		this.shortcutKey = shortcutKey;
	}

	public String getFileName() {
		return this.fileName;
	}

	public String getShortcutKey() {
		return this.shortcutKey;
	}

	public String getDescription() {
		return this.description;
	}

	public String getName() {
		return this.name;
	}

	public ImageIcon getIcon() {
		return ImageIconFactory.getInstance().getImageIcon(this);
	}

	public KeyStroke getKeyStroke() {
		return null; // as in MindIcon TODO ask?
	}

	public String getDefaultImagePath() {
		return DEFAULT_IMAGE_PATH;
	}

	public String getPath() {
		final String themeFolder = RESOURCE_CONTROLLER.getProperty(THEME_FOLDER_KEY);
		final String userDir     = RESOURCE_CONTROLLER.getFreeplaneUserDirectory();

		StringBuilder builder = new StringBuilder();
		builder.append(userDir);
		builder.append(SEPARATOR);
		builder.append(themeFolder);
		builder.append(SEPARATOR);
		builder.append(this.fileName);

		final String themePath   = builder.toString();

		builder = new StringBuilder();
		builder.append(this.getDefaultImagePath());
		builder.append(SEPARATOR);
		builder.append(this.fileName);

		final String defaultPath = builder.toString();

		String path = null;
		final File iconFile = new File(themePath);
		if(themeFolder != null && iconFile.exists()) {
			path = iconFile.getPath();
		}
		else {
			try {
				path = new File(RESOURCE_CONTROLLER.getResource(defaultPath).toURI()).getPath();
			} catch (final URISyntaxException e) {
				LogTool.warn(String.format("could not open file [%s]", defaultPath));
			}
		}

		return path;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.description == null) ? 0 : this.description.hashCode());
		result = prime * result
				+ ((this.fileName == null) ? 0 : this.fileName.hashCode());
		result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
		result = prime * result
				+ ((this.shortcutKey == null) ? 0 : this.shortcutKey.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		final UIIcon other = (UIIcon) obj;
		if (this.description == null) {
			if (other.description != null)
				return false;
		} else if (!this.description.equals(other.description))
			return false;
		if (this.fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!this.fileName.equals(other.fileName))
			return false;
		if (this.name == null) {
			if (other.name != null)
				return false;
		} else if (!this.name.equals(other.name))
			return false;
		if (this.shortcutKey == null) {
			if (other.shortcutKey != null)
				return false;
		} else if (!this.shortcutKey.equals(other.shortcutKey))
			return false;
		return true;
	}

	public int compareTo(final UIIcon uiIcon) {
		return this.getPath().compareTo(uiIcon.getPath());
	}
}