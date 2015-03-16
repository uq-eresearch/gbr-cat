INSERT INTO transect(width, length, quadrat_size) VALUES (1, 15, 1);
INSERT INTO transect(width, length, quadrat_size) VALUES (1, 10, 1);
INSERT INTO transect(width, length, quadrat_size) VALUES (4, 4, 1);

INSERT INTO site (id, name, site_type, region_geometry) values 
	('GBR_ROI', 'GBR ROI', 'polygon', ST_GeomFromText('POLYGON((-20 148.5, -25 148.5, -25 154, -20 154, -20 148.5))',4326));

INSERT INTO site (id, name, site_type, region_geometry) values 
	('Sat_Data_ROI', 'Sat Data ROI', 'polygon', ST_GeomFromText('POLYGON((-21.5 149.5, -25.5 149.5, -25.5 154, -21.5 154, -21.5 149.5))',4326));	
	
INSERT INTO datetime (startdate, enddate) VALUES ('2001-11-01T00:00:00.000', '2002-03-31T23:59:59.999');
INSERT INTO datetime (startdate, enddate) VALUES ('1997-11-01T00:00:00.000', '1998-03-31T23:59:59.999');

INSERT INTO context (datetime, site) VALUES ((SELECT id FROM datetime where startdate = '2001-11-01T00:00:00.000' and enddate = '2002-03-31T23:59:59.999'), 'GBR_ROI');
INSERT INTO context (datetime, site) VALUES ((SELECT id FROM datetime where startdate = '1997-11-01T00:00:00.000' and enddate = '1998-03-31T23:59:59.999'), 'GBR_ROI');

INSERT INTO ecological_process (name, context, severity) 
	VALUES ('Coral Bleaching 1998', (SELECT id from context WHERE datetime = (SELECT id FROM datetime where startdate = '2001-11-01T00:00:00.000' and enddate = '2002-03-31T23:59:59.999') and site = 'GBR_ROI'), 'Severe');
INSERT INTO ecological_process (name, context, severity) 
	VALUES ('Coral Bleaching 2002', (SELECT id from context WHERE datetime = (SELECT id FROM datetime where startdate = '1997-11-01T00:00:00.000' and enddate = '1998-03-31T23:59:59.999') and site = 'GBR_ROI'), 'Severe');

INSERT INTO tool_type (type) VALUES ('Other');

INSERT INTO tool (name, type) VALUES ('Colour Reference Card', (SELECT id FROM tool_type where type = 'Other'));

INSERT INTO actor (name, type) VALUES ('Unkown Actor', 2);

