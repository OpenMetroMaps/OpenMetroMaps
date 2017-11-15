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

package org.openmetromaps.maps.gwt.client;

import java.util.function.Consumer;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;

public class Util
{

	public static void load(String filename, Consumer<String> callback)
	{
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
				filename);

		try {
			builder.sendRequest(null, new RequestCallback() {

				@Override
				public void onResponseReceived(Request req, Response resp)
				{
					if (resp.getStatusCode() == Response.SC_OK) {
						String text = resp.getText();
						callback.accept(text);
					} else {
						Window.alert("request failed: " + resp.getStatusCode());
					}
				}

				@Override
				public void onError(Request res, Throwable throwable)
				{
					System.out.println("Error occurred while fetching data");
					Window.alert("request failed");
				}

			});
		} catch (RequestException e) {
			System.out.println("Error while fetching data");
			e.printStackTrace();
		}
	}

}
