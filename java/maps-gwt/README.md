To get things going, you currently need to do the following:

    ./prepare.sh

In the long run, we want to get rid of this script by implementing a proper
Gradle task that will
* perform the relevant source transformations for us
* create the necessary *.gwt.xml file automatically
