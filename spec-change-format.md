# OpenMetroMaps Change Format

This is a draft!

## Basic structure

The file should begin with this line:

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
```

All data is contained within a `changes` element:

```xml
<changes>
  ...
</changes>
```

The `changes` element contains a number of `change`, `exit`, and `batch`
elements.

## Changes

A `change` element defines a position on a station's platform.
The platform is specified via a triple of `line`, `towards` and
`at` attributes. Attributes `line` and `towards` specify a line with a
direction that it is going and `at` specified a stop on that line.

The `change-line` attribute then specifies a different line that can be
changed to and the `location` attribute can be used to define the location
on the platform that is optimal for a change to that line. See the
[section below](#location) for possible values of the `location` attribute.

## Exits

## Batches

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
