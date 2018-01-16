# OpenMetroMaps Java project

## Requirements

In order to run the software from the development tree you need Java 8 and
Gradle.

## Command line interface (CLI)

### Building the CLI module

Run the Gradle `createRuntime` task to build the CLI:

    ./gradlew clean createRuntime

### CLI usage and tasks

This project has a main executable that can be executed like this:

    ./scripts/openmetromaps-cli <task>

Alternatively, add the `scripts` directory to your `PATH` environment
variable in order to run `openmetromaps-cli` without specifying its location
each time. The following examples assume you have done that.
Then invoke the main executable like this:

    openmetromaps-cli <task>

Where task can be any of the following:

    osm-filter
    osm-extract
    build-model
    inspect-model
    map-editor
    map-viewer
    simple-map-viewer
    gtfs-import
    create-markdown-view
    util

Each task accepts its own set of command line parameters. To run the Map Viewer
you would type:

    openmetromaps-cli map-viewer --input test-data/src/main/resources/berlin.xml

To run the Map Editor, type:

    openmetromaps-cli map-editor --input test-data/src/main/resources/berlin.xml

### The util task

The `util` task works on map model files and offers more sub-tasks:

    openmetromaps-cli util <sub-task>

where sub-task may be one of the following:

    info
    list-change-stations
    list-lines-with-change-stations

## To-Do

Have a look at the [To-Do list](TODO.md).

## Hacking

To start hacking on the project, you should use an IDE. We're using Eclipse here
and Gradle provides the mechanisms to generate the Eclipse project files for all
submodules. Use `gradle cleanEclipse eclipse` to generate the required files.
Afterwards you can import the Git repository into your Eclipse workspace and
import the projects from there (Use the *Git Repositories* view for this.
If you don't have this view open in your Eclipse perspective, open it like this:
Window → Show View → Other..., Git → Git Repositories).

Once you've set up your working envrionment, you can start running the editor
from within the IDE. Navigate to the class `TestMapEditor` and run this class
as a Java application (Right click class → Run As → Java Application). To easily
find the class within the complex forrest of source trees, we recommend
Eclipse's *Open Type* feature (Navigate → Open Type, shortcut Ctrl+Shift+T).

### Writing an optimization algorithm

The Map Editor provides an infrastructure for implementing algorithms for
optimizing maps. When you run the editor, you can access the available
optimization algorithms via the menu (Edit → Algorithms). Currently there's only
two algorithms available:

* Dummy Optimization: This is a placeholder algorithm that does nothing and only
  exists in order to show how to add an algorithm to the menu. Have a look at
  class `DummyOptimizationAction`.
* StraightenAxisParallelLines: This is a very basic optimization algorithm that
  strives to detect subway lines with almost axis-parallel sections. Sections
  that are classified as quasi axis-parallel will be modified so that they are
  really axis-parallel afterwards. Have a look at `StraightenAxisParallelLinesAction`
  to see how the menu action can be set up and at
  `StraightenAxisParallelLinesOptimization` to see the actual optimization code.

To write your own optimization algorithm, we recommend to copy and rename
the classes `StraightenAxisParallelLinesAction` and
`StraightenAxisParallelLinesOptimization` and start modifying the existing code.

See [this list of papers](../research/research.md#optimization-algorithms)
for possible implementations that have been discussed in literature.
