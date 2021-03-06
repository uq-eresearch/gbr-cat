<%@ page import="au.edu.uq.eresearch.Person" %>
<%@ page import="org.apache.commons.fileupload.FileItem" %>
<%@ page import="org.apache.commons.fileupload.FileItemFactory" %>
<%@ page import="org.apache.commons.fileupload.FileUploadException" %>
<%@ page import="org.apache.commons.fileupload.disk.DiskFileItemFactory" %>
<%@ page import="org.apache.commons.fileupload.servlet.ServletFileUpload" %>
<%@ page import="javax.persistence.EntityManager" %>
<%@ page import="javax.persistence.EntityManagerFactory" %>
<%@ page import="javax.persistence.Persistence" %>
<%@ page import="javax.persistence.Query" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.List" %>
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
    try {
        usernamestring = (String) session.getAttribute("gbrcatLogin");
        if (usernamestring == null || "".equals(usernamestring) || usernamestring.equals(null)) {
            response.sendRedirect("/gbrcat/ProperLogin.jsp");
        }
    } catch (Exception e) {
        response.sendRedirect("/gbrcat/ProperLogin.jsp");
    }
%>
</head>
<body>
<jsp:include page="/Body.jsp"/>
    <%
			EntityManagerFactory emf = Persistence.createEntityManagerFactory("gisdb");
			EntityManager em = emf.createEntityManager();
			
			String id = request.getParameter("id");
			
			Query query = em.createQuery("SELECT a FROM Person a WHERE actorid = " + request.getParameter("id"));
		    List<au.edu.uq.eresearch.Person> actorData = (List<au.edu.uq.eresearch.Person>) query.getResultList();
			Person person = actorData.get(0);
			
			String personEmail = person.getEmail();
			
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
							person.setFirstname(value);
						} 
						if(name.equals("lastname")){
							person.setLastname(value);
						} 	
						if(name.equals("description")){
							person.setDescription(value);
						} 		
						if(name.equals("organisation")){
							person.setOrganisation(value);
						} 		
						if(name.equals("email")){
							person.setEmail(value);
						} 		
						if(name.equals("webaddress")){
							person.setWebaddress(value);
						} 			    
		   		   }
		   		}
		   	}
		 	
    	    em.getTransaction().begin();
    	    em.persist(person);
    	    em.getTransaction().commit(); 
    	    
    	    em.close();
    	    emf.close();
    	    
			EntityManagerFactory emf2 = Persistence.createEntityManagerFactory("cresis");
			EntityManager em2 = emf2.createEntityManager();
			
			query = em2.createQuery("SELECT a FROM Actor a WHERE email = '" + personEmail + "'");
		    List<au.edu.uq.eresearch.cresis.Actor> aData = (List<au.edu.uq.eresearch.cresis.Actor>) query.getResultList();
    	    if (!actorData.isEmpty()){
		        au.edu.uq.eresearch.cresis.Actor actor = aData.get(0);
                actor.setEmail(person.getEmail());
                actor.setType(2);
                actor.setName(person.toString());

                em2.getTransaction().begin();
                em2.persist(actor);
                em2.getTransaction().commit();

                em2.close();
                emf2.close();
    	    }
    	    response.sendRedirect("/gbrcat/ViewActor.jsp?id=" + id);
		%>
<jsp:include page="/Footer.jsp"/> 