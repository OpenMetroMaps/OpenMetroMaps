// Copyright 2018 Sebastian Kuerten
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

package org.openmetromaps.osm;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.model.osm.DraftModel;
import org.openmetromaps.model.osm.DraftModelConverter;
import org.openmetromaps.model.osm.Fix;
import org.openmetromaps.model.osm.ModelBuilder;
import org.openmetromaps.model.osm.filter.RouteFilter;

import de.topobyte.osm4j.core.access.OsmIterator;
import de.topobyte.osm4j.core.dataset.InMemoryMapDataSet;
import de.topobyte.osm4j.core.dataset.MapDataSetLoader;
import de.topobyte.osm4j.xml.dynsax.OsmXmlIterator;

public class OverpassApiImporter
{

	public ModelData execute(String q, RouteFilter routeFilter,
			List<String> prefixes, List<String> suffixes, List<Fix> fixes)
			throws MalformedURLException, IOException
	{
		HttpPost post = new HttpPost(
				"http://www.overpass-api.de/api/interpreter");

		post.setEntity(new StringEntity(q));

		CloseableHttpClient httpclient = HttpClients.createDefault();

		ResponseHandler<InMemoryMapDataSet> handler = new ResponseHandler<InMemoryMapDataSet>() {

			@Override
			public InMemoryMapDataSet handleResponse(HttpResponse response)
					throws ClientProtocolException, IOException
			{

				int status = response.getStatusLine().getStatusCode();
				if (status == 200) {
					HttpEntity entity = response.getEntity();
					InputStream input = entity.getContent();

					OsmIterator iterator = new OsmXmlIterator(input, true);
					InMemoryMapDataSet data = MapDataSetLoader.read(iterator,
							true, true, true);

					return data;
				} else {
					throw new ClientProtocolException(
							"Unexpected response status: " + status);
				}
			}

		};

		InMemoryMapDataSet data = httpclient.execute(post, handler);

		System.out.println(String.format("%d, %d, %d", data.getNodes().size(),
				data.getWays().size(), data.getRelations().size()));

		ModelBuilder modelBuilder = new ModelBuilder(data, routeFilter,
				prefixes, suffixes, fixes);
		modelBuilder.run(true, true);

		DraftModel draftModel = modelBuilder.getModel();
		ModelData model = new DraftModelConverter().convert(draftModel);

		return model;
	}

}
