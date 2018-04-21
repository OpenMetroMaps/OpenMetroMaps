package org.openmetromaps.mobidig;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.xml.DesktopXmlModelReader;
import org.openmetromaps.maps.xml.XmlModel;
import org.openmetromaps.maps.xml.XmlModelConverter;
import org.openmetromaps.mobidig.demo.AufzugViewer;

import de.topobyte.xml.domabstraction.iface.ParsingException;

public class Util
{

	public static MapModel stuttgartSchematic() throws ParsingException
	{
		InputStream input = AufzugViewer.class.getClassLoader()
				.getResourceAsStream("sbahn-schematic.omm");
		XmlModel xmlModel = DesktopXmlModelReader.read(input);

		XmlModelConverter modelConverter = new XmlModelConverter();
		MapModel model = modelConverter.convert(xmlModel);
		return model;
	}

	public static List<String> lines(String resourceName) throws IOException
	{
		InputStream is = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(resourceName);
		String text = IOUtils.toString(is, StandardCharsets.UTF_8);
		String[] lines = text.split("\\r?\\n");
		return Arrays.asList(lines);
	}

}
