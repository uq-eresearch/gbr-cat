RIEfs Ontology to PL/SQL
========================

This project reads a REIfS OWL Ontology and generates PL/SQL.

Dependencies
------------
1. Protege 3.4.1 (may work with later versions)
2. Apache Ant

Installation
------------
1. Copy the top-level 'reids2sql' folder into your Protege base directory.
	e.g. you will now have /usr/local/Protege_3.4.1/refis2sql
	
2. Change to the refis2sql and run 'ant'

Running
-------
In the reids2sql run:
	ant run -Dowl-file=<url-to-owl-file>
e.g.:
	ant run -Dowl-file=file:///home/frodo/projects/CREDVAT/Ontology/REIfS.owl
	
The program will output PL/SQL to a file called REIfS.sql