<%@ page import="javax.persistence.*" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.net.*" %>
<%@ page import="org.apache.commons.fileupload.servlet.ServletFileUpload" %>
<%@ page import="org.apache.commons.fileupload.disk.DiskFileItemFactory" %>
<%@ page import="org.apache.commons.fileupload.*" %>
<%@ page import="org.apache.poi.poifs.filesystem.POIFSFileSystem"%>
<%@ page import="org.apache.poi.hssf.usermodel.*"%>
<%@ page import="au.edu.uq.eresearch.*"%>
<%@ page import="au.edu.uq.eresearch.cresis.*"%>
<%@ page import="java.text.*"%>
<%@ page import="com.vividsolutions.jts.geom.*"%>
<%@ page import="com.vividsolutions.jts.io.WKTReader"%>
<%--
  ~ Copyright (c) 2011, The University of Queensland
  ~ All rights reserved.
  ~
  ~ Redistribution and use in source and binary forms, with or without
  ~ modification, are permitted provided that the following conditions are met:
  ~     * Redistributions of source code must retain the above copyright
  ~       notice, this list of conditions and the following disclaimer.
  ~     * Redistributions in binary form must reproduce the above copyright
  ~       notice, this list of conditions and the following disclaimer in the
  ~       documentation and/or other materials provided with the distribution.
  ~     * Neither the name of The University of Queensland nor the
  ~       names of its contributors may be used to endorse or promote products
  ~       derived from this software without specific prior written permission.
  ~
  ~ THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
  ~ ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
  ~ WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
  ~ DISCLAIMED. IN NO EVENT SHALL THE UNIVERSITY OF QUEENSLAND BE LIABLE FOR ANY
  ~ DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
  ~ (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
  ~ LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
  ~ ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
  ~ (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
  --%>

<html>
<head>
<%
	String usernamestring = "";
	try{
		usernamestring = (String) session.getAttribute("gbrcatLogin"); 
		if(usernamestring == null || "".equals(usernamestring) || usernamestring.equals(null)){
			response.sendRedirect("/gbrcat/ProperLogin.jsp");
		}
	} catch(Exception e){
		response.sendRedirect("/gbrcat/ProperLogin.jsp");
	}
%>
</head>
<body>
<%	
	File savedFile = new File("/opt/apache-tomcat-6.0.26/webapps/gbrcat/" + request.getParameter("na"));

	int number = Integer.parseInt(request.getParameter("nu"));
	int actor_id = Integer.parseInt(request.getParameter("ai"));
	int unit_id = Integer.parseInt(request.getParameter("ui"));
	int target_id = Integer.parseInt(request.getParameter("ti"));
   	
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("cresis");
	EntityManager em = emf.createEntityManager();
   	
	ArrayList<String> header = null;
	ArrayList<String> row = null;
	if(savedFile.getName().endsWith(".xls") || savedFile.getName().endsWith(".xlsx")){
		ArrayList<ArrayList<String>> data = SpreadSheetReader.getDataFromXLS(savedFile); 
		header = data.get(0);
		row = data.get(number + 1);
	} else {
		header = SpreadSheetReader.readLineFromCSV(savedFile,0); 
		row = SpreadSheetReader.readLineFromCSV(savedFile,number+1); 
	}
	
	String Latitude =  "";
	String Longitude = "";
	try{
		Latitude = row.get(header.indexOf("Latitude")).trim();
		Longitude = row.get(header.indexOf("Longitude")).trim();
	} catch (Exception err){
		
	}
	if(!Latitude.equals(null) && !Latitude.equals("") &&
			!Longitude.equals(null) && !Longitude.equals("")){
		java.util.Date startDate = null;
		java.util.Date endDate = null;
		
		boolean Passed = false;
		try{
			DateFormat format = new SimpleDateFormat("MMMM-yy");
			startDate = format.parse(row.get(header.indexOf("Date")).trim());
			Passed = true;
		} catch(Exception e) {
			
		}
		
		if(!Passed){
			try{
				DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
				startDate = format.parse(row.get(header.indexOf("Date")).trim());
				Passed = true;
			} catch(Exception e) {
				
			}
		}
		
		if(!Passed){
			try{
				String[] fields = row.get(header.indexOf("Date")).split(" ");
				DateFormat format = new SimpleDateFormat("MMMM yyyy");
				startDate = format.parse(fields[0].trim() + " " + fields[2].trim());
				endDate = format.parse(fields[1].trim() + " " + fields[2].trim());
				Passed = true;
			} catch(Exception e) {
				
			}
		}
		
		if(!Passed){
			try{
				String[] fields = row.get(header.indexOf("Date")).split("-");
				if(fields[0].split(" ")[1].length() > 3){
					DateFormat format = new SimpleDateFormat("MMMM yyyy");
					startDate = format.parse(fields[0].trim());
					endDate = format.parse(fields[0].split(" ")[0].trim() + " " + fields[1].trim());
					Passed = true;
				}
			} catch(Exception e) {
				
			}
		}
		
		if(!Passed){
			try{
				String[] fields = row.get(header.indexOf("Date")).split("-");
				DateFormat format = new SimpleDateFormat("MMMM dd yyyy");
				startDate = format.parse(fields[0].trim() + " " + row.get(header.indexOf("Year")).trim());
				endDate = format.parse(fields[0].split(" ")[0].trim() + " " + fields[1].trim() + " " + row.get(header.indexOf("Year")).trim());
				Passed = true;
			} catch(Exception e) {
				
			}
		}
		
		if(!Passed){
			try{
				String[] fields = row.get(header.indexOf("Date")).split("-");
				DateFormat format = new SimpleDateFormat("yyyy");
				startDate = format.parse(fields[0].trim());
				endDate = format.parse(fields[1].trim());
				Passed = true;
			} catch(Exception e) {
				
			}
		}

		if(!Passed){
			try{
				String[] fields = row.get(header.indexOf("Date")).split("-");
				DateFormat format = new SimpleDateFormat("MMMM yyyy");
				startDate = format.parse(fields[0].trim());
				endDate = format.parse(fields[1].trim());
				Passed = true;
			} catch(Exception e) {
				
			}
		}
		
		if(!Passed){
			try{
				DateFormat format = new SimpleDateFormat("MMM yyyy");
				startDate = format.parse(row.get(header.indexOf("Date")).trim() + " " + row.get(header.indexOf("Year")).trim());
				Passed = true;
			} catch(Exception e) {
				
			}
		}
		
		String site = row.get(header.indexOf("New site name")).trim();
		if(site.contains("(")){
			site = site.substring(site.indexOf("("));
		}
		if(site.contains(",")){
			site = site.substring(site.indexOf(","));
		}
		
		
		String site_id = site = site.replace(" ","_");
		Query query = em.createQuery("SELECT s from Site s where id = '" + site_id + "'");
    	List<Site> site_Data = (List<Site>) query.getResultList();
    	if(site_Data.size() == 0){
    		try{
	    		Site sit = new Site();
	    		sit.setName(site);
				sit.setId(site_id);
				sit.setSite_type("point");
				try{
					sit.setDepth(Double.parseDouble(row.get(header.indexOf("Depth (m)"))));
				} catch (Exception e){
					
				}
				try{
					sit.setDepth_desc(row.get(header.indexOf("Shelf position")));
	    		} catch (Exception e){
					
				}
	    		Geometry geo = (Geometry) new WKTReader().read("POINT( -" 
	    				+ row.get(header.indexOf("Latitude")) + " " + row.get(header.indexOf("Longitude")) + ")");
	    		geo.setSRID(4326);
	    		sit.setPoint_geometry(geo);
	    		
	    		au.edu.uq.eresearch.cresis.Controller.addObject(sit);
    		} catch (Exception e){
    			throw(e);
    		}
    	} 
		
		int datetime_id = -1;
		if(startDate == null){
			query = em.createQuery("SELECT dt FROM Datetime dt WHERE startdate = null AND enddate = null");
		} else if (endDate == null){
			query = em.createQuery("SELECT dt FROM Datetime dt WHERE startdate = '" + startDate + "' AND enddate = null");
		} else {
			query = em.createQuery("SELECT dt FROM Datetime dt WHERE startdate = '" + startDate + "' AND enddate = '" + endDate + "'");
		}
    	List<Datetime> datetime_Data = (List<Datetime>) query.getResultList();
    	if(datetime_Data.size() == 0){
    		Datetime dt = new Datetime();
    		try{
    			dt.setStartdate(new java.sql.Date((startDate).getTime()));
    		} catch (Exception e){
    			
    		}
    		try{
    			dt.setEnddate(new java.sql.Date((endDate).getTime()));
    		} catch (Exception e){
    			
    		}
    		au.edu.uq.eresearch.cresis.Controller.addObject(dt);
    		
    		datetime_id = dt.getId();
    	} else {
        	Datetime dt = datetime_Data.get(0);
    		datetime_id = dt.getId();
    	}
		
		int context_id = -1;
		query = em.createQuery("select c from Context c where datetime = '" + datetime_id + "' and site = '" + site_id + "'");
    	List<Context> context_Data = (List<Context>) query.getResultList();
    	if(context_Data.size() == 0){
    		Context dt = new Context();
    		dt.setDatetime(datetime_id);
    		dt.setSite(site_id);		    		
    		
    		au.edu.uq.eresearch.cresis.Controller.addObject(dt);
    		
    		context_id = dt.getId();
    	} else {
    		Context dt = context_Data.get(0);
    		context_id = dt.getId();
    	}
		
		String species = row.get(header.indexOf("Species"));
		String genus = row.get(header.indexOf("Genus"));
		
		int obs_id = -1;
		query = em.createQuery("select o from Observation o where target = '" + target_id + "' and context = '" 
				+ context_id + "' and species = '" + species + "' and genus = '" + genus + "'");
    	List<Observation> observation_Data = (List<Observation>) query.getResultList();
    	if(observation_Data.size() == 0){
    		Observation dt = new Observation();
    		dt.setTarget(target_id);
    		dt.setContext(context_id);
    		dt.setSpecies(species);
    		dt.setGenus(genus);
    		
    		dt.setIs_part_of_eco_process(1);
    		dt.setTransect_id(1);
    		dt.setTransect_type(1);
    		
    		au.edu.uq.eresearch.cresis.Controller.addObject(dt);
    		
    		obs_id = dt.getId();
    	} else {
    		Observation dt = observation_Data.get(0);
    		obs_id = dt.getId();
    	}
		
		query = em.createQuery("select oa from Observation_actor oa where observationid = '" + obs_id + "' and actorid = '" + actor_id + "'");
    	if(query.getResultList().size() == 0){
    		Controller.insertObservationActor(obs_id,actor_id);
    	}
		
		ArrayList<String> characteristics = new ArrayList<String>();
		characteristics.add("Clade 1");
		characteristics.add("Clade 2");
		characteristics.add("ITS1 subclade 1");
		characteristics.add("ITS1 subclade 2");
		characteristics.add("ITS1 subclade 3");
		characteristics.add("ITS2 subclade 1");
		characteristics.add("ITS2 subclade 2");
		characteristics.add("ITS2 subclade 3");
		characteristics.add("ITS1 type");
		characteristics.add("ITS2 type");
		
		for(String chara:characteristics){
			if(header.indexOf(chara) != -1){
				String record = row.get(header.indexOf(chara)).trim();
				if(record != null || !record.equalsIgnoreCase("")){
					String chara_name = chara.replace(" ","_");
				
					int chara_id = -1;
					query = em.createQuery("select c from Characteristic c where name = '" + chara + "'");
			    	List<Characteristic> chara_Data = (List<Characteristic>) query.getResultList();
			    	if(chara_Data.size() == 0){
			    		Characteristic dt = new Characteristic();
			    		dt.setName(chara);
			    		dt.setDescription("");
			    		
			    		au.edu.uq.eresearch.cresis.Controller.addObject(dt);
			    		
			    		chara_id = dt.getId();
			    	} else {
			    		Characteristic dt = chara_Data.get(0);
			    		chara_id = dt.getId();
			    	}
					
					int measurement_id = -1;
					Measurement m = new Measurement();
					m.setOther_value(record);
					m.setValue_type("numeric");
					m.setCharacteristic(chara_id);
					m.setUnit(unit_id);
					
					au.edu.uq.eresearch.cresis.Controller.addObject(m);

		    		measurement_id = m.getId();
					
		    		Controller.insertObservationMeasurement(obs_id,measurement_id);
				}
			}
		}
	}
	
	em.close();
	emf.close();
	%>
</body>
</html>