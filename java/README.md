# OpenMetroMaps Java project

## Requirements

In order to run the software from the development tree you need Java 8 and
Gradle.

## Command line interface (CLI)

This project has a main executable that can be executed like this:

    ./scripts/openmetromaps-cli <task>

Where task can be any of the following:

    osm-filter
    osm-extract
    build-model
    inspect-model
    map-editor
    map-viewer

Each task accepts its own set of command line parameters. To run the Map Viewer
you would type:

    ./scripts/openmetromaps-cli map-viewer --input test-data/src/main/resources/berlin.xml

To run the Map Editor, type:

    ./scripts/openmetromaps-cli map-editor --input test-data/src/main/resources/berlin.xml

## To-Do

Have a look at the [To-Do list](https://github.com/OpenMetroMaps/OpenMetroMaps/blob/master/java/TODO.md).

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
