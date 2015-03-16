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
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("gisdb");
		EntityManager em = emf.createEntityManager();
	
	
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		List<Short> columns = new ArrayList<Short>();
		File savedFile = null;
		
		String actorName = "";
		String actorEmail = "";
		String projectName = "";
		String projectDescription = "";
		String toolUsed = "Unkown";
		String unitUsed = "Unkown";
		
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
						String actorId = value;
						
						Query query = em.createQuery("SELECT a FROM Person a WHERE id = " + actorId);
					    List<au.edu.uq.eresearch.Person> actorData = (List<au.edu.uq.eresearch.Person>) query.getResultList();
						au.edu.uq.eresearch.Person act = actorData.get(0);
						
						actorName = act.getFirstname() + " " + act.getLastname();
						actorEmail = act.getEmail();
					} 
					if(name.equals("project")){
						
					} 
					if(name.equals("collection")){
						String collectionId = value;
						
						Query query = em.createQuery("SELECT c FROM Collection c WHERE collectionid = " + collectionId);
					    List<au.edu.uq.eresearch.Collection> collectionData = (List<au.edu.uq.eresearch.Collection>) query.getResultList();
						au.edu.uq.eresearch.Collection col = collectionData.get(0);
						
						projectName = col.getTitle();
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
	   	
	   	ArrayList<ArrayList<String>> variables = new ArrayList<ArrayList<String>>();
	   	variables.add(new ArrayList<String>());
	   	variables.add(new ArrayList<String>());
	   	
	   	variables.get(0).add("Name");
	   	variables.get(0).add("Email");
	   	variables.get(0).add("Project");
	   	variables.get(0).add("Description");
	   	variables.get(0).add("Tool");
	   	variables.get(0).add("Unit");

	   	variables.get(1).add(actorName);
	   	variables.get(1).add(actorEmail);
	   	variables.get(1).add(projectName);
	   	variables.get(1).add(projectDescription);
	   	variables.get(1).add(toolUsed);
	   	variables.get(1).add(unitUsed);
	   	
	   	File vars = CSVBuilder.makeCSV("/opt/apache-tomcat/webapps/gbrcat/variables.csv",variables);
	   	
		ArrayList<ArrayList<String>> data = null;
		if(savedFile.getName().endsWith(".xls") || savedFile.getName().endsWith(".xlsx")){
			data = SpreadSheetReader.getDataFromXLS(savedFile); 
		} else {
			data = SpreadSheetReader.getDataFromCSV(savedFile); 
		}
		ArrayList<String> header = data.get(0);
		boolean passed = true;
		for(int i = 1; i < data.size(); i = i + 1){
			ArrayList<ArrayList<String>> subData = new ArrayList<ArrayList<String>>();
			subData.add(header);
			for(int j = i; j <= i + 1 && j < data.size(); j++){
				subData.add(data.get(i));
			}
			File f = CSVBuilder.makeCSV("/opt/apache-tomcat/webapps/gbrcat/GBRCAT" + i + ".csv",subData);
			String cmd = "python /opt/apache-tomcat-6.0.26/webapps/import.py " + f.getPath() + " " + vars.getPath();
			try{
				String s = "";
	 			Process p = Runtime.getRuntime().exec(cmd);
	 			int j = p.waitFor();
			} catch (Exception e){
				passed = false;
				%><%= e.toString() %><%
			}
			f.delete();
		}
		savedFile.delete();
		vars.delete();
		if(passed){
			%>Upload Complete<%
		}
	%>
	<%
		em.close();
		emf.close();
	%>
	
<jsp:include page="Footer.jsp"/> 