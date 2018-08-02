To get things going, you currently need to do the following:

    ./prepare.sh

In the long run, we want to get rid of this script by implementing a proper
Gradle task that will
* perform the relevant source transformations for us
* create the necessary *.gwt.xml file automatically

To build the application, run:

    ./update.sh

To run the application, start a server:

    http-server test -p 9011

And navigate here with your browser:

    http://127.0.0.1:9011/index.html

You might need to install the `http-server` package using npm:

    npm install -g http-server
