package org.openmetromaps.maps.editor.history;

public class Capture
{

	final String name;
	final MapEditorSnapshot before;

	Capture(String name, MapEditorSnapshot before)
	{
		this.name = name;
		this.before = before;
	}

}