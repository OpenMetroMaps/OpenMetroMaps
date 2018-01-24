# OpenMetroMaps Station Format

This is a draft!

## Basic structure

The file should begin with this line:

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
```

All data is contained within a `omm-stations` element:

```xml
<omm-stations version="1.0.0">
  ...
</omm-stations>
```

The `omm-stations` element contains a number of `change`, `exit`, and `batch`
elements.

## Changes

A `change` element defines a position on a station's platform that is
optimal for changing to a different line.

The platform is specified via a triple of `line`, `towards` and
`at` attributes. Attributes `line` and `towards` specify a line with a
direction that it is going and `at` specified a stop on that line.

The `change-line` attribute then specifies a different line that can be
changed to and the `location` attribute can be used to define the location
on the platform that is optimal for a change to that line. See the
[section below](#location) for possible values of the `location` attribute.

Example:

```xml
<change line="U3" towards="Nollendorfplatz" at="Fehrbelliner Platz"
        change-line="U7" location="back"/>
<change line="U3" towards="Krumme Lanke"    at="Fehrbelliner Platz"
        change-line="U7" location="front"/>
```

When a position is valid for a number of change lines and you don't want
to specify each one separately, repeating the location and other attributes,
you can use the `change-line-regex` attribute instead of `change-line`.
This allows you for example to match all trains 'S41', 'S42', 'S45',
'S46' and 'S47' with the expression `change-line-regex="S.*"`:

```xml
<change line="U3" towards="Nollendorfplatz" at="Heidelberger Platz"
        change-line-regex="S.*" location="back"/>
```

Sometimes, it is necessary to specify the direction of the destination
line. This can be done with the `change-towards` attribute:

```xml
<change at="Bundesplatz" change-line="U9" change-towards="Rathaus Steglitz"
        location="front" derive-reverse-from="true"/>
<change at="Bundesplatz" change-line="U9" change-towards="Osloer Straße"
        location="middle" derive-reverse-from="true"/>
```

The boolean `derive-reverse-from` attribute allows you to define changes
for both directions of a line at a station in one entry if the situation
is symmetric:

```xml
<change line="S46" towards="Westend" at="Neukölln"
        change-line="U7" location="front" derive-reverse-from="true"/>
```

is equivalent to defining these two entries:

```xml
<change line="S46" towards="Westend" at="Neukölln"
        change-line="U7" location="front"/>
<change line="S46" towards="Königs Wusterhausen" at="Neukölln"
        change-line="U7" location="back"/>
```

## Exits

Similar to the `change` element, an `exit` element defines an exit from
a platform, most commonly to some position on the street, but sometimes
into buildings or elsewhere.

As with change-positions, thhe platform is specified with the triple
of `line`, `towards` and also `at` attributes.

An exit should have a `name` that shortly identifies it among all other
exits of the same stations, such as a cardinal direction, the suburb it
leads to, or simply one or more of the streets you can reach from there.

As with change-positions, a `location` attribute specified the location
on the platform. Also, the element can specify a boolean `derive-reverse`
attribute that works similar to the change's `derive-reverse-from` for
symmetric situations.

Example:

```xml
<exit line="S41" towards="Wedding" at="Hermannstraße" name="west" location="front"
      description="Escalator" derive-reverse="true"/>
<exit line="S41" towards="Wedding" at="Hermannstraße" name="east" location="back"
      description="Hermann-Quartier, Elevator" derive-reverse="true"/>
```

## Batches

The `batch` element can be used to specify multiple change and exit
records without repeating information that all of them share.
This is useful for specifying a number of records for the same line where
`line` and `towards` attribute don't have to be repeated over and over,
instead they can be specified once on the `batch` element and then apply
to all `change` and `exit` child elements of the batch.

Example:

```xml
<batch line="U7" towards="Rathaus Spandau">
  <change at="Neukölln" change-line-regex="S.*" location="front"/>
  <exit   at="Neukölln" name="north" location="front"
          description="Emser Straße, Saalestraße" derive-reverse="true"/>
  <exit   at="Neukölln" name="south" location="back"
          description="Lahnstraße, Silbersteinstraße" derive-reverse="true"/>
  <change at="Hermannplatz" change-line="U8" location="middle/middle back"/>
  <change at="Berliner Straße" change-line="U9" location="middle"/>
  <change at="Fehrbelliner Platz" change-line="U3" location="almost back"/>
</batch>
```

## Attribute values

### Location

The `location` attribute can have the following values:

* `front` (1.0)
* `almost front` (0.833)
* `middle/middle front` (0.667)
* `middle` (0.5)
* `middle/middle back` (0.333)
* `almost back` (0.167)
* `back` (0.0)
