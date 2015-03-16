\set ON_ERROR_STOP 1

/*Create the characteristic table*/
DROP TABLE IF EXISTS characteristic CASCADE;
CREATE TABLE characteristic (
 id serial PRIMARY KEY,
 name varchar(50) UNIQUE NOT NULL,
 description text
);

DROP TABLE IF EXISTS data_qualifier CASCADE;
CREATE TABLE data_qualifier (
 id serial PRIMARY KEY,
 qualifier varchar(50) UNIQUE NOT NULL
);

DROP TABLE IF EXISTS unit CASCADE;
CREATE TABLE unit (
 id serial PRIMARY KEY,
 name varchar(50) UNIQUE NOT NULL,
 description varchar(50) UNIQUE NOT NULL
);

DROP TABLE IF EXISTS tool_type CASCADE;
CREATE TABLE tool_type (
 id serial PRIMARY KEY,
 type varchar(50) UNIQUE NOT NULL
);

DROP TABLE IF EXISTS tool CASCADE;
CREATE TABLE tool (
 id serial PRIMARY KEY,
 name varchar(255) UNIQUE NOT NULL,
 description text,
 type integer references tool_type(id) NOT NULL,
 model_number varchar(100),
 manufacturer varchar(100),
 platform varchar(100),
 callibration_settings varchar(100)
);

DROP TABLE IF EXISTS measurement CASCADE;
CREATE TABLE measurement (
 id serial PRIMARY KEY,
 numeric_value float,
 other_value varchar(50),
 value_type varchar(50) NOT NULL,
 CHECK(value_type IN ('numeric', 'other')),
 precision decimal(7,4),
 characteristic integer references characteristic(id) NOT NULL,
 unit integer references unit(id) NOT NULL,
 tool integer references tool(id),
 qualifier integer references data_qualifier(id),
 reference integer
);

DROP TABLE IF EXISTS datetime CASCADE;
CREATE TABLE datetime (
 id serial PRIMARY KEY,
 startdate timestamp,
 enddate timestamp
);

DROP TABLE IF EXISTS site CASCADE;
CREATE TABLE site (
 id varchar(255) PRIMARY KEY,
 name varchar(255) NOT NULL,
 site_type varchar(50) NOT NULL,
 depth float,
 depth_desc varchar(50),
 CHECK (site_type IN ('point', 'polygon'))
);
SELECT AddGeometryColumn('public', 'site','point_geometry',4326,'POINT',2);
SELECT AddGeometryColumn('public', 'site','region_geometry',4326,'POLYGON',2);

DROP TABLE IF EXISTS context CASCADE;
CREATE TABLE context (
 id serial PRIMARY KEY,
 datetime integer references datetime(id),
 site varchar(255) references site(id)
);

DROP TABLE IF EXISTS program CASCADE;
CREATE TABLE program (
 id serial PRIMARY KEY,
 name varchar(50) UNIQUE NOT NULL,
 methodology text,
 disclaimer text,
 url text
);

DROP TABLE IF EXISTS actor_type CASCADE;
CREATE TABLE actor_type (
 id serial PRIMARY KEY,
 type varchar(50) UNIQUE NOT NULL
);

DROP TABLE IF EXISTS actor CASCADE;
CREATE TABLE actor (
 id serial PRIMARY KEY,
 name varchar(255) UNIQUE NOT NULL,
 email varchar(100),
 type integer references actor_type(id)
);

DROP TABLE IF EXISTS target CASCADE;
CREATE TABLE target (
 id serial PRIMARY KEY,
 name varchar(50) UNIQUE NOT NULL,
 description varchar(50) UNIQUE NOT NULL
);

DROP TABLE IF EXISTS image CASCADE;
CREATE TABLE image (
 id serial PRIMARY KEY,
 url text UNIQUE NOT NULL,
 dimensions varchar(50),
 creation_date timestamp,
 context integer references context(id)
);

DROP TABLE IF EXISTS ecological_process CASCADE;
CREATE TABLE ecological_process (
 id serial PRIMARY KEY,
 name varchar(50) UNIQUE NOT NULL,
 context integer references context(id),
 severity varchar(50)
);

DROP TABLE IF EXISTS transect CASCADE;
CREATE TABLE transect (
 id serial PRIMARY KEY,
 width integer,
 length integer,
 quadrat_size integer
);

 /*Create the observation table*/
DROP TABLE IF EXISTS observation CASCADE;
CREATE TABLE observation (
 id serial PRIMARY KEY,
 target integer references target(id) NOT NULL,
 context integer references context(id) NOT NULL,
 program integer references program(id) NOT NULL,
 species varchar(50),
 genus varchar(50),
 morphology varchar(50),
 health varchar(50),
 individual_count integer,
 transect_type integer references transect(id),
 transect_id integer,
 quadrat_id integer,
 is_part_of_eco_process integer references ecological_process(id),
 reference integer
);

/* NOW FOR THE MAPPING TABLES WHICH LINK THE TABLES WITH CARDINALITY CONSTRAINTS */
DROP TABLE IF EXISTS observation_image CASCADE;
CREATE TABLE observation_image (
 observationID integer references observation(id),
 imageID integer references image(id),
 PRIMARY KEY (observationID, imageID)
);

DROP TABLE IF EXISTS observation_actor CASCADE;
CREATE TABLE observation_actor (
 observationID integer references observation(id),
 actorID integer references actor(id),
 PRIMARY KEY (observationID, actorID)
);

DROP TABLE IF EXISTS program_actor CASCADE;
CREATE TABLE program_actor (
 programID integer references program(id),
 actorID integer references actor(id),
 PRIMARY KEY (programID, actorID)
);

DROP TABLE IF EXISTS observation_measurement CASCADE;
CREATE TABLE observation_measurement (
 observationID integer references observation(id),
 measurementID integer references measurement(id),
 PRIMARY KEY (observationID, measurementID)
);
