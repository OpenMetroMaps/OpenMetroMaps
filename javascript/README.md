# OpenMetroMaps JavaScript project

This is the native JavaScript implementation of a viewer for the map files.
Up to now, this implementation is merely a stub with basic setup of a HTML
Canvas element, JQuery XML request to retrieve a data file, but not much
more: No actual parsing, no actual rendering of stations and lines.
The focus is currently on the [GWT-based
approach](https://github.com/OpenMetroMaps/OpenMetroMaps/tree/master/java/maps-gwt),
which provides an in-browser component, too and allows code reuse with
the main Java modules.
Also, we tried to get a [JSweet-based
approach](https://github.com/OpenMetroMaps/OpenMetroMaps/tree/master/jsweet)
to work.

## Hacking

In order to work on this sub-module you'll need to serve the contents of this
directory with a HTTP server. Below is one way to achieve this.

To run the application, start a server:

    http-server ./ -p 9010

And navigate here with your browser:

    http://127.0.0.1:9010/test.html

You might need to install the `http-server` package using npm:

    npm install -g http-server
