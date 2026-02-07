// Copyright 2026 Sebastian Kuerten
//
// This file is part of OpenMetroMaps.
//
// OpenMetroMaps is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// OpenMetroMaps is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with OpenMetroMaps. If not, see <http://www.gnu.org/licenses/>.

package org.openmetromaps.swing.config;

import java.nio.file.Path;

public class VolatileConfiguration
{

	private Path lastUsedDirectory;

	public VolatileConfiguration()
	{
		// no-op
	}

	public VolatileConfiguration(VolatileConfiguration other)
	{
		this.lastUsedDirectory = other.lastUsedDirectory;
	}

	public static VolatileConfiguration createDefaultConfiguration()
	{
		return new VolatileConfiguration();
	}

	public Path getLastUsedDirectory()
	{
		return lastUsedDirectory;
	}

	public void setLastUsedDirectory(Path lastUsedDirectory)
	{
		this.lastUsedDirectory = lastUsedDirectory;
	}

}
