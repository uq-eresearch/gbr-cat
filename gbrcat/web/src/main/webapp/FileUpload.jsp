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
<%	
	boolean isMultipart = ServletFileUpload.isMultipartContent(request);
	List<Short> columns = new ArrayList<Short>();
	File savedFile = null;
	
	String actorID = "";
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
					actorID = value;
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
   	
 	EntityManagerFactory emf2 = Persistence.createEntityManagerFactory("gisdb");
	EntityManager em2 = emf2.createEntityManager();
 	
	Query query = em2.createQuery("SELECT a FROM Person a WHERE actorid = " + actorID);
   	List<Person> people = (List<Person>) query.getResultList();
   	Person person = people.get(0);
	actorName = person.toString();
	
	em2.close();
	emf2.close();
	
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("cresis");
	EntityManager em = emf.createEntityManager();
   	
	int actor_type = -1;
	query = em.createQuery("SELECT a FROM Actor_type a WHERE type='Person'");
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

%>
<SCRIPT language="javascript" type="text/javascript">

	function uploadData() {		
		var i=0;
		for (i=0;i<=<%= data.size() %>;i++){
			if (window.XMLHttpRequest){// code for IE7+, Firefox, Chrome, Opera, Safari
			  	xmlhttp=new XMLHttpRequest();
			} else {// code for IE6, IE5
			  	xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
			}
			var URL = "AJAXFileUpload.jsp?na=<%= savedFile.getName() %>";
			URL += "&nu=" + i;
			URL += "&ti=" + <%= target_id %>;
			URL += "&ai=" + <%= actor_id %>;
			URL += "&ui=" + <%= unit_id %>;
			xmlhttp.open("POST",URL,false);
			xmlhttp.send(null);
			document.getElementById('mylabel').innerHTML = parseFloat(document.getElementById('mylabel').innerHTML) + 1;
			// pausecomp(50);
		}
		document.getElementById('results').innerHTML = "Upload Complete for <%= actorName %>";
	}
	
	function pausecomp(millis)
	{
		var date = new Date();
		var curDate = null;
	
		do { curDate = new Date(); }
		while(curDate-date < millis);
	} 
		
</SCRIPT>
</head>
<body onLoad="uploadData();">
<jsp:include page="/Body.jsp"/>
<br />
<div id="results"> 
<label id="mylabel">0</label> of <%= data.size() %> entries added.
</div>
<%
	em.close();
	emf.close();
%>
<jsp:include page="Footer.jsp"/> 