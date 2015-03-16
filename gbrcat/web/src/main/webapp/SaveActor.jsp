<%@ page import="au.edu.uq.eresearch.Person"%>
<%@ page import="au.edu.uq.eresearch.cresis.Actor"%>
<%@ page import="javax.persistence.*" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="org.apache.commons.fileupload.servlet.ServletFileUpload" %>
<%@ page import="org.apache.commons.fileupload.disk.DiskFileItemFactory" %>
<%@ page import="org.apache.commons.fileupload.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
			
			Person actor = new Person();
			int id = 0;
			
			boolean isMultipart = ServletFileUpload.isMultipartContent(request);
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
						
						if(name.equals("firstname")){
							actor.setFirstname(value);
						} 
						if(name.equals("lastname")){
							actor.setLastname(value);
						} 	
						if(name.equals("description")){
							actor.setDescription(value);
						} 		
						if(name.equals("organisation")){
							actor.setOrganisation(value);
						} 		
						if(name.equals("email")){
							actor.setEmail(value);
						} 		
						if(name.equals("webaddress")){
							actor.setWebaddress(value);
						} 			    
		   		   }
		   		}
		   	}
		 	
		 	String actorId = actor.toString();
		 	
    	    em.getTransaction().begin();
    	    em.persist(actor);
    	    em.getTransaction().commit(); 
    	    
    	    id = actor.getActorid();
    	    
    	    em.close();
    	    emf.close();
    	    
			EntityManagerFactory emf2 = Persistence.createEntityManagerFactory("cresis");
			EntityManager em2 = emf2.createEntityManager();
			
			Actor a = new Actor();
			
			a.setEmail(actor.getEmail());
			a.setType(2);
			a.setName(actor.toString());
			
    	    em2.getTransaction().begin();
    	    em2.persist(a);
    	    em2.getTransaction().commit(); 
    	    
    	    em2.close();
    	    emf2.close();
    	    
    	    response.sendRedirect("/gbrcat/ViewActor.jsp?id=" + id);
		%>
<jsp:include page="/Footer.jsp"/> 