# OpenMetroMaps

This is the main repository of the
[OpenMetroMaps](https://www.openmetromaps.org) project.

Data projects for cities/regions will be stored within the
[OpenMetroMapsData](https://github.com/OpenMetroMapsData) organization to keep
the namespaces for data and code projects separated.

## Data projects

[Berlin](https://github.com/OpenMetroMapsData/berlin)

## Research

We're also gathering material about transit maps in general on the
[Research](https://github.com/OpenMetroMaps/OpenMetroMaps/blob/master/research/Research.md)
page.

## File format

A major goal of this project is to develop a file format for storing schematic
maps for public transport networks.
There's no formal specification of the file format yet and features of the
format are still under construction.
See an [example file](https://github.com/OpenMetroMaps/OpenMetroMaps/blob/master/java/test-data/src/main/resources/berlin.xml)
to get an idea of how it's going to look.

## Desktop Tools

We're developing a set of desktop tools for working with the map files.
Those tools are written in Java and user interfaces are based on Swing with
DockingFrames for dockable dialogs.

One core component is the Map Editor that allows you to create new maps based on
OpenStreetMap data or from scratch and lets you manipulate existing maps.

If you want to run the editor or start hacking on the desktop tools, please have
a look at the relevant
[REAMDE file](https://github.com/OpenMetroMaps/OpenMetroMaps/blob/master/java/README.md).

## Web Viewer

We would also like to build a Javascript-based Web Viewer for the file format to
allow for easy presentation of results in a browser without the need to install
any desktop software.
