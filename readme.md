### Modifications in this fork
Added null value support through a new table property: "nullString" (using RmnCsvSerde.java).  To accomplish this, copies/alterations of dependent files
from opencsv-2.3.jar have been made.  To determine a nullString match, the modified CSVParser will examine the raw delimited values.  This way, the "nullString" property can
be quoted and/or escaped, but it is not a requirement.<br>

Examples:<br>
"nullString" could be set to "NULL", where the unquoted string NULL will be treated as a null value
"nullString" could be set to "'NULL'", where the string 'NULL' will be treated as a null value

# Hive CSV Support

[![Build Status](https://drone.io/github.com/ogrodnek/csv-serde/status.png)](https://drone.io/github.com/ogrodnek/csv-serde/latest)

This SerDe adds *real* CSV input and ouput support to hive using the excellent [opencsv](http://opencsv.sourceforge.net/) library.

## Using


### Basic Use

```
add jar path/to/csv-serde.jar;

create table my_table(a string, b string, ...)
  row format serde 'com.bizo.hive.serde.csv.RmnCsvSerde'
  stored as textfile
;
```

### Custom formatting

The default separator, quote, and escape characters from the `opencsv` library are:

```
DEFAULT_ESCAPE_CHARACTER \
DEFAULT_QUOTE_CHARACTER  "
DEFAULT_SEPARATOR        ,
DEFAULT_NULL_STRING      NULL
```

You can also specify custom separator, quote, or escape characters.

```
add jar path/to/csv-serde.jar;

create table my_table(a string, b string, ...)
 row format serde 'com.bizo.hive.serde.csv.RmnCsvSerde'
 with serdeproperties (
   "separatorChar" = "\t",
   "quoteChar"     = "'",
   "escapeChar"    = "\\",
   "nullString"    = "'\\N'"
  )	  
 stored as textfile
;
```

## Files

The following include opencsv along with the serde, so only the single jar is needed.  Currently built against Hive 0.11.0, but should be compatible with other hive versions.

* [csv-serde-1.1.2-0.11.0-all.jar](https://drone.io/github.com/ogrodnek/csv-serde/files/target/csv-serde-1.1.2-0.11.0-all.jar)


## Building

Run `mvn package` to build.  Both a basic artifact as well as a "fat jar" (with opencsv) are produced.

### Eclipse support

Run `mvn eclipse:eclipse` to generate `.project` and `.classpath` files for eclipse.


## License

csv-serde is open source and licensed under the [Apache 2 License](http://www.apache.org/licenses/LICENSE-2.0.html).
