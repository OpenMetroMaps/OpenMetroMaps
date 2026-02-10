# OpenMetroMaps Java project

**Table of Contents**
* [Requirements](#requirements)
* [Command line interface (CLI)](#command-line-interface-cli)
    * [Building the CLI module](#building-the-cli-module)
    * [CLI usage and tasks](#cli-usage-and-tasks)
    * [The osm-import task](#the-osm-import-task)
    * [The util task](#the-util-task)
    * [The export task](#the-export-task)
* [Map Editor](#map-editor)
* [To-Do](#to-do)
* [Hacking](#hacking)
    * [Gradle tasks](#gradle-tasks)
    * [Writing an optimization algorithm](#writing-an-optimization-algorithm)
* [Publishing artifacts](#publishing-artifacts)

## Requirements

In order to run the software from the development tree you need a Java
Development Kit (JDK), Version 8 or later. The project uses Gradle as a
build tool, but you should use the included Gradle Wrapper for building
the project.

On Debian-based systems such as Ubuntu or Mint, you can install the JDK
like this:

    sudo apt-get install openjdk-8-jdk

## Command line interface (CLI)

### Building the CLI module

First, make sure to change to the `java` directory, i.e. type

    cd java

Run the Gradle `createRuntime` task to build the CLI:

    ./gradlew clean createRuntime

### CLI usage and tasks

This project has a main executable that can be executed like this:

    ./scripts/openmetromaps-cli <task>

Alternatively, add the `scripts` directory to your `PATH` environment
variable in order to run `openmetromaps-cli` without specifying its location
each time. The following examples assume you have done that:

    export PATH=$PATH:$(readlink -f scripts)

Then invoke the main executable like this:

    openmetromaps-cli <task>

Where `<task>` can be any of the following:

    ui-selector
    osm-filter
    osm-extract
    osm-query
    osm-import
    osm-inspect
    map-editor
    map-viewer
    map-morpher
    simple-map-viewer
    gtfs-import
    graphml-import
    create-markdown-view
    util
    export

Each task accepts its own set of command line parameters. To run the Map Viewer
you would type:

    openmetromaps-cli map-viewer --input test-data/src/main/resources/berlin.xml

To run the Map Editor, type:

    openmetromaps-cli map-editor --input test-data/src/main/resources/berlin.xml

### The osm-import task

The `osm-import` task imports data from OpenStreetMap and offers more sub-tasks:

    openmetromaps-cli osm-import <sub-task>

where `<sub-task>` may be one of the following:

    file
    overpass

### The util task

The `util` task works on map model files and offers more sub-tasks:

    openmetromaps-cli util <sub-task>

where `<sub-task>` may be one of the following:

    info
    list-change-stations
    list-lines-with-change-stations
    purge-stations

### The export task

The `export` task works on map model files and offers more sub-tasks:

    openmetromaps-cli export <sub-task>

where `<sub-task>` may be one of the following:

    svg
    png

Examples:

    openmetromaps-cli export png --input test-data/src/main/resources/berlin.xml
                                 --output berlin.png --zoom 2

    openmetromaps-cli export svg --input test-data/src/main/resources/berlin.xml
                                 --output berlin.svg --zoom 3

## Map Editor

The Map Editor is the main interface for creating and manipulating maps.
There's a separate [manual](map-editor.md) that explains the features in
some detail.

## To-Do

Have a look at the [To-Do list](TODO.md).

## Hacking

To start hacking on the project, you should use an IDE. We're using Eclipse here
and Gradle provides the mechanisms to generate the Eclipse project files for all
submodules. Use `./gradlew cleanEclipse eclipse` to generate the required files.
Afterwards you can import the Git repository into your Eclipse workspace and
import the projects from there (Use the *Git Repositories* view for this.
If you don't have this view open in your Eclipse perspective, open it like this:
Window → Show View → Other..., Git → Git Repositories).

When importing the modules into Eclipse, it may show lots of build errors due
to circular dependencies between projects. Make the projects build by
configuring Eclipse to warn about circular dependencies instead of failing.
In the main menu go to Window → Preferences → Java → Compiler → Building
→ Circular Dependencies and select 'Warning' instead of 'Error'.

Once you've set up your working envrionment, you can start running the editor
from within the IDE. Navigate to the class `TestMapEditor` and run this class
as a Java application (Right click class → Run As → Java Application). To easily
find the class within the complex forrest of source trees, we recommend
Eclipse's *Open Type* feature (Navigate → Open Type, shortcut Ctrl+Shift+T).

### Gradle tasks

Here's a list of useful Gradle tasks:

    ./gradlew showInterModuleDependencies
    ./gradlew checkUploadArtifactList

The project is split into many modules. To get insights into the modules
and their dependencies to one another, use the `showInterModuleDependencies`
task.

To make sure that all required artifacts get uploaded, the
`checkUploadArtifactList` task has been implemented. Run it to make sure
that the list of artifacts is properly configured.

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

## Publishing artifacts

The process for publishing artifacts works by building the release artifacts,
copying them into a local repository, from which you need to deploy them to
a Maven server. We keep a separate repository for released artifacts that are
then served from a regular web server.
Run the following to build the release artifacts:

    ./gradlew clean
    ./gradlew checkUploadArtifactList
    ./gradlew -P topobyte publish

Where the last command requires you to have a special Gradle configuration
file in your Gradle user directory (`~/.gradle/topobyte.gradle`) with the
following content:

    apply plugin: Topobyte

    class Topobyte implements Plugin<Project> {
        void apply(Project project) {
            project.uploadArchives {
                repositories {
                    mavenDeployer {
                        repository(url: 'file://localhost/path/to/local/maven/repo')
                    }
                }
            }
        }
    }
