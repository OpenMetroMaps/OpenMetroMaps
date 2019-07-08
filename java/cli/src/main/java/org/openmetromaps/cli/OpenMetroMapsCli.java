// Copyright 2017 Sebastian Kuerten
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

package org.openmetromaps.cli;

import org.openmetromaps.cli.export.RunExportBatik;
import org.openmetromaps.cli.export.RunExportPng;
import org.openmetromaps.cli.graphml.RunGraphMLImport;
import org.openmetromaps.cli.gtfs.RunGtfsImport;
import org.openmetromaps.cli.maps.RunMapEditor;
import org.openmetromaps.cli.maps.RunMapMorpher;
import org.openmetromaps.cli.maps.RunMapViewer;
import org.openmetromaps.cli.maps.RunSimpleMapViewer;
import org.openmetromaps.cli.markdownview.RunCreateMarkdownView;
import org.openmetromaps.cli.newformat.RunCreateNewFormat;
import org.openmetromaps.cli.newformat.RunExportNewFormatToIpe;
import org.openmetromaps.cli.osm.RunFilterRegion;
import org.openmetromaps.cli.osm.RunFilterRelevantData;
import org.openmetromaps.cli.osm.RunModelInspector;
import org.openmetromaps.cli.osm.RunOsmDownloadOverpass;
import org.openmetromaps.cli.osm.RunOsmImportFile;
import org.openmetromaps.cli.osm.RunOsmImportOverpass;
import org.openmetromaps.cli.startup.RunUiSelector;
import org.openmetromaps.cli.util.RunFindCloseStations;
import org.openmetromaps.cli.util.RunListChangeStations;
import org.openmetromaps.cli.util.RunListLinesWithChangeStations;
import org.openmetromaps.cli.util.RunListStations;
import org.openmetromaps.cli.util.RunModelInfo;
import org.openmetromaps.cli.util.RunPurgeStations;

import de.topobyte.utilities.apache.commons.cli.commands.ArgumentParser;
import de.topobyte.utilities.apache.commons.cli.commands.ExeRunner;
import de.topobyte.utilities.apache.commons.cli.commands.ExecutionData;
import de.topobyte.utilities.apache.commons.cli.commands.RunnerException;
import de.topobyte.utilities.apache.commons.cli.commands.options.DelegateExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptionsFactory;

public class OpenMetroMapsCli
{

	public static ExeOptionsFactory OPTIONS_FACTORY = new ExeOptionsFactory() {

		@Override
		public ExeOptions createOptions()
		{
			DelegateExeOptions options = new DelegateExeOptions();
			options.addCommand("ui-selector", RunUiSelector.OPTIONS_FACTORY,
					RunUiSelector.class);
			options.addCommand("osm-filter",
					RunFilterRelevantData.OPTIONS_FACTORY,
					RunFilterRelevantData.class);
			options.addCommand("osm-extract", RunFilterRegion.OPTIONS_FACTORY,
					RunFilterRegion.class);
			options.addCommand("osm-query",
					RunOsmDownloadOverpass.OPTIONS_FACTORY,
					RunOsmDownloadOverpass.class);
			options.addCommand("osm-import", OPTIONS_FACTORY_OSM_IMPORT);
			options.addCommand("osm-inspect", RunModelInspector.OPTIONS_FACTORY,
					RunModelInspector.class);
			options.addCommand("map-editor", RunMapEditor.OPTIONS_FACTORY,
					RunMapEditor.class);
			options.addCommand("map-viewer", RunMapViewer.OPTIONS_FACTORY,
					RunMapViewer.class);
			options.addCommand("map-morpher", RunMapMorpher.OPTIONS_FACTORY,
					RunMapMorpher.class);
			options.addCommand("simple-map-viewer",
					RunSimpleMapViewer.OPTIONS_FACTORY,
					RunSimpleMapViewer.class);
			options.addCommand("gtfs-import", RunGtfsImport.OPTIONS_FACTORY,
					RunGtfsImport.class);
			options.addCommand("graphml-import",
					RunGraphMLImport.OPTIONS_FACTORY, RunGraphMLImport.class);
			options.addCommand("create-markdown-view",
					RunCreateMarkdownView.OPTIONS_FACTORY,
					RunCreateMarkdownView.class);
			options.addCommand("util", OPTIONS_FACTORY_UTIL);
			options.addCommand("export", OPTIONS_FACTORY_EXPORT);
			options.addCommand("new-format", OPTIONS_FACTORY_NEW_FORMAT);
			return options;
		}

	};

	public static ExeOptionsFactory OPTIONS_FACTORY_OSM_IMPORT = new ExeOptionsFactory() {

		@Override
		public ExeOptions createOptions()
		{
			DelegateExeOptions options = new DelegateExeOptions();
			options.addCommand("file", RunOsmImportFile.OPTIONS_FACTORY,
					RunOsmImportFile.class);
			options.addCommand("overpass", RunOsmImportOverpass.OPTIONS_FACTORY,
					RunOsmImportOverpass.class);
			return options;
		}

	};

	public static ExeOptionsFactory OPTIONS_FACTORY_UTIL = new ExeOptionsFactory() {

		@Override
		public ExeOptions createOptions()
		{
			DelegateExeOptions options = new DelegateExeOptions();
			options.addCommand("info", RunModelInfo.OPTIONS_FACTORY,
					RunModelInfo.class);
			options.addCommand("list-stations", RunListStations.OPTIONS_FACTORY,
					RunListStations.class);
			options.addCommand("list-change-stations",
					RunListChangeStations.OPTIONS_FACTORY,
					RunListChangeStations.class);
			options.addCommand("list-lines-with-change-stations",
					RunListLinesWithChangeStations.OPTIONS_FACTORY,
					RunListLinesWithChangeStations.class);
			options.addCommand("purge-stations",
					RunPurgeStations.OPTIONS_FACTORY, RunPurgeStations.class);
			options.addCommand("find-close-stations",
					RunFindCloseStations.OPTIONS_FACTORY,
					RunFindCloseStations.class);
			return options;
		}

	};

	public static ExeOptionsFactory OPTIONS_FACTORY_EXPORT = new ExeOptionsFactory() {

		@Override
		public ExeOptions createOptions()
		{
			DelegateExeOptions options = new DelegateExeOptions();
			options.addCommand("png", RunExportPng.OPTIONS_FACTORY,
					RunExportPng.class);
			options.addCommand("svg", RunExportBatik.OPTIONS_FACTORY,
					RunExportBatik.class);
			return options;
		}

	};

	public static ExeOptionsFactory OPTIONS_FACTORY_NEW_FORMAT = new ExeOptionsFactory() {

		@Override
		public ExeOptions createOptions()
		{
			DelegateExeOptions options = new DelegateExeOptions();
			options.addCommand("create", RunCreateNewFormat.OPTIONS_FACTORY,
					RunCreateNewFormat.class);
			options.addCommand("export-ipe",
					RunExportNewFormatToIpe.OPTIONS_FACTORY,
					RunExportNewFormatToIpe.class);
			return options;
		}

	};

	public static void main(String[] args) throws RunnerException
	{
		String name = "openmetromaps-cli";

		ExeOptions options = OPTIONS_FACTORY.createOptions();
		ArgumentParser parser = new ArgumentParser(name, options);

		if (args.length == 0) {
			System.out.println("OpenMetroMaps Command Line Interface");
			System.out.println();
			options.usage(name);
			System.exit(1);
		}

		ExecutionData data = parser.parse(args);
		if (data != null) {
			ExeRunner.run(data);
		}
	}

}
