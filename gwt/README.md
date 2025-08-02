# Building and running the propject

## Preparations

To get things going, you currently need to do the following:

    ./prepare.sh

In the long run, we want to get rid of this script by implementing a proper
Gradle task that will
* perform the relevant source transformations for us
* create the necessary *.gwt.xml file automatically

## Build

To build the application, run:

    ./update.sh

## Run

To run the application, start a server:

    http-server test -p 9011

And navigate here with your browser:

    http://127.0.0.1:9011/index.html

You might need to install the `http-server` package using npm:

    npm install -g http-server

# Development

When working with Eclipse, make sure to use GWT SDK version 2.8.1, otherwise
there will be compile errors.

It is also possible to run super dev mode using Gradle:

    ./gradlew gwtDevMode

Then visit `http://localhost:8888`.

## Logging

To enable logging, add this to any of the `*.gwt.xml` module descriptors:

    <set-property name="gwt.logging.enabled" value="TRUE" />
    <set-property name="gwt.logging.consoleHandler" value="ENABLED" />
