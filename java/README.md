# OpenMetroMaps Java project

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
