# GTFS Importer

This module relies on our GTFS access library called
[gtfs4j](https://github.com/OpenMetroMaps/gtfs4j).

The importer does not support filtering of routes itself currently. To reduce
the number of routes imported and reduce processing time, filter the dataset
in advance using the gtfs4j CLI. For example, to extract the light rail
routes from the Berlin GTFS file from VBB, use this:

    gtfs4j-cli filter-routes
        --input /tmp/gtfs/vbb.zip --output /tmp/gtfs/vbb-lightrail.zip
        --agencies 1,796 --pattern "U[0-9]+" --pattern "S[0-9]+"

Afterwards, use the `gtfs-import` task of the main CLI to run the import
utility:

    openmetromaps-cli gtfs-import
        --input /tmp/gtfs/vbb-lightrail.zip --output /tmp/gtfs/berlin.xml
