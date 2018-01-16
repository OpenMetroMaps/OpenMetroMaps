# OpenMetroMaps Map Format

This is a draft!

## Basic structure

The file should begin with this line:

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
```

All data is contained within an `omm-file` element:

```xml
<omm-file version="1.0.0">
  ...
</omm-file>
```

The `omm-file` element contains a `stations` element, a `lines` element and
optionally a number of `view` elements.

## Stations and Lines

Within the `stations` element, a list of `station` elements declares the stations
available on the map along with their geographic coordinates:

```xml
<stations>
  <station lat="52.521515" lon="13.412305" name="Alexanderplatz"/>
  <station lat="52.520387" lon="13.386885" name="Friedrichstraße"/>
  <station lat="52.522648" lon="13.402209" name="Hackescher Markt"/>
</stations>
```

Within the `lines` element, a list of `line` elements declares the lines
available on the map. A line has a `name`, a `color` and can be `circular`.
Each `line` element contains a number of `stop` references to stations that
define the schematic trajectory of the line:

```xml
<lines>
  <line circular="false" color="#006CB3" name="S3">
    <stop station="Alexanderplatz"/>
    <stop station="Hackescher Markt"/>
    <stop station="Friedrichstraße"/>
  </line>
</lines>
```

## Views

A map file can specify a number of views. Each view defines the
appearance of the map in terms of the positions of the stations on the
scene with Euclidean coordinates and other properties that effect the visual
appearance:

```xml
<view name="Berlin" scene-height="904.137943" scene-width="1000.000000"
      start-x="386.178629" start-y="478.710910">
  ...
</view>
```

The `view` element contains a number of `station` and `edges` elements.

Each station must be assigned an Euclidean coordinate:

```xml
<station name="Alexanderplatz" x="424.775181" y="461.793637"/>
<station name="Friedrichstraße" x="392.200080" y="459.965281"/>
<station name="Hackescher Markt" x="408.133553" y="459.451927"/>
```

We can define which edges of a line should be displayed on the map:

```xml
<edges line="S3"/>
<edges line="S45">
  <interval from="Adlershof" to="Flughafen Berlin-Schönefeld"/>
</edges>
```
