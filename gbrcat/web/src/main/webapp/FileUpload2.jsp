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

<jsp:include page="/Header.jsp"/>
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
<jsp:include page="/Body.jsp"/>
<%	
	boolean isMultipart = ServletFileUpload.isMultipartContent(request);
	List<Short> columns = new ArrayList<Short>();
	File savedFile = null;
	
	String actorName = "";
	String collectionName = "";
	String projectName = "";
	String projectDescription = "";
	
 	if (isMultipart) {
   		FileItemFactory factory = new DiskFileItemFactory();
   		ServletFileUpload upload = new ServletFileUpload(factory);
   		List items = null;
   		try {
   			items = upload.parseRequest(request);
   		} catch (FileUploadException e) {
   			e.printStackTrace();
   		}
   		Iterator itr = items.iterator();
   		while (itr.hasNext()) {
   			FileItem item = (FileItem) itr.next();
   			if (item.isFormField()){
				String name = item.getFieldName();
				String value = item.getString();
				if(name.equals("name")){
					actorName = value;
				} 
				if(name.equals("collection")){
					collectionName = value;
				} 
				if(name.equals("project")){
					projectName = value;
				} 
				if(name.equals("description")){
					projectDescription = value;
				} 
   			} else {
    			try {
					String itemName = item.getName();
   					savedFile = new File(config.getServletContext().getRealPath("/")+itemName);
   					item.write(savedFile); 
   					
   					FileInputStream fileIn = new FileInputStream(savedFile);
   					byte[] outputByte = new byte[(int)savedFile.length()];
   					fileIn.read(outputByte);
   					fileIn.close();
   				} catch (Exception e) {
   					e.printStackTrace();
   					%><%= e.toString() %><%
   				}	
   			}
   		}
   	}
   	%><BR/><%
   	
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("cresis");
	EntityManager em = emf.createEntityManager();
   	
	int actor_type = -1;
	Query query = em.createQuery("SELECT a FROM Actor_type a WHERE type='Person'");
   	List<Actor_type> actor_typeData = (List<Actor_type>) query.getResultList();
   	Actor_type actorType = actor_typeData.get(0);
	actor_type = actorType.getId();
	
	int actor_id = -1;
	query = em.createQuery("SELECT a FROM Actor a WHERE name = '" + actorName + "'");
   	List<au.edu.uq.eresearch.cresis.Actor> actor_Data = (List<au.edu.uq.eresearch.cresis.Actor>) query.getResultList();
   	if(actor_Data.size() == 0){
   		au.edu.uq.eresearch.cresis.Actor actor = new au.edu.uq.eresearch.cresis.Actor();
   		actor.setName(actorName);
   		actor.setType(actor_type);
   		actor.setEmail("");
   		
   		au.edu.uq.eresearch.cresis.Controller.addObject(actor);
   		
   		actor_id = actor.getId();
   	} else {
       	au.edu.uq.eresearch.cresis.Actor actor = actor_Data.get(0);
   		actor_id = actor.getId();
   	}
	
	int project_id = -1;
	query = em.createQuery("SELECT a FROM Program a WHERE name = '" + projectName + "'");
   	List<Program> program_Data = (List<Program>) query.getResultList();
   	if(program_Data.size() == 0){
   		Program program = new Program();
   		program.setName(projectName);
   		program.setMethodology(projectDescription);
   		
   		au.edu.uq.eresearch.cresis.Controller.addObject(program);
   		
   		project_id = program.getId();
   	} else {
       	Program program = program_Data.get(0);
   		project_id = program.getId();
   	}
	
	int tool_type = -1;
	query = em.createQuery("SELECT t FROM Tool_type t WHERE type = 'Other'");
   	List<Tool_type> tool_typeData = (List<Tool_type>) query.getResultList();
   	Tool_type tool_t = tool_typeData.get(0);
	tool_type = tool_t.getId();
	
	int tool_id = -1;
	query = em.createQuery("SELECT a FROM Tool a WHERE name = 'Unkown'");
   	List<Tool> tool_Data = (List<Tool>) query.getResultList();
   	if(tool_Data.size() == 0){
   		Tool tool = new Tool();
   		tool.setName("Unkown");
   		tool.setType(tool_type);
   		
   		au.edu.uq.eresearch.cresis.Controller.addObject(tool);

   		tool_id = tool.getId();
   	} else {
       	Tool tool = tool_Data.get(0);
   		tool_id = tool.getId();
   	}
	
	int unit_id = -1;
	query = em.createQuery("SELECT a FROM Unit a WHERE name = 'Unkown'");
   	List<Unit> unit_Data = (List<Unit>) query.getResultList();
   	if(unit_Data.size() == 0){
   		Unit unit = new Unit();
   		unit.setName("Unkown");
   		unit.setDescription("Unkown Unit");
   		
   		au.edu.uq.eresearch.cresis.Controller.addObject(unit);
   		
   		unit_id = unit.getId();
   	} else {
       	Unit unit = unit_Data.get(0);
   		unit_id = unit.getId();
   	}
	
	int target_id = -1;
	query = em.createQuery("SELECT t FROM Target t WHERE name='Coral'");
   	List<Target> targetData = (List<Target>) query.getResultList();
   	Target target = targetData.get(0);

	target_id = target.getId();

	ArrayList<ArrayList<String>> data = null;
	if(savedFile.getName().endsWith(".xls") || savedFile.getName().endsWith(".xlsx")){
		data = SpreadSheetReader.getDataFromXLS(savedFile); 
	} else {
		data = SpreadSheetReader.getDataFromCSV(savedFile); 
	}
	ArrayList<String> header = data.get(0);
	
	data.remove(0);

	for(ArrayList<String> row:data){
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
			query = em.createQuery("SELECT s from Site s where id = '" + site_id + "'");
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
				    		dt.setDescription(projectDescription);
				    		
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
	}
	%><Br />Upload Complete<%
		em.close();
		emf.close();
	%>
<jsp:include page="Footer.jsp"/> 