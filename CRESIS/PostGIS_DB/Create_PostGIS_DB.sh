 w#!/bin/sh
#Note: Must be run as the user who runs the DB service.
# !!IMPORTANT!!
#	ON MAC OS X this must be run under Terminal with the ENV VAR -> LANG=en_AU.UTF-8

#make the db
/usr/local/pgsql/bin/createdb -O postgres -U postgres reifs

#add the psql language
/usr/local/pgsql/bin/createlang plpgsql reifs

#add the GIS support
/usr/local/pgsql/bin/psql -d reifs -f /usr/local/pgsql/share/contrib/postgis.sql
/usr/local/pgsql/bin/psql -d reifs -f /usr/local/pgsql/share/contrib/spatial_ref_sys.sql

#now setup our DB schema
time /usr/local/pgsql/bin/psql -d reifs -f /usr/local/pgsql/share/contrib/REIFS/reifs_schema.sql

#add the main ontology instances
time /usr/local/pgsql/bin/psql -d reifs -f /usr/local/pgsql/share/contrib/REIFS/REIfS.sql

#add the default values
time /usr/local/pgsql/bin/psql -d reifs -f /usr/local/pgsql/share/contrib/REIFS/defaultValues.sql
