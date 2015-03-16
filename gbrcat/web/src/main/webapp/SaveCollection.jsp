<%@page import="au.edu.uq.eresearch.Collection_anzsrcfield" %>
<%@ page import="au.edu.uq.eresearch.Collection_subject" %>
<%@ page import="au.edu.uq.eresearch.Subject" %>
<%@ page import="org.apache.commons.fileupload.FileItem" %>
<%@ page import="org.apache.commons.fileupload.FileItemFactory" %>
<%@ page import="org.apache.commons.fileupload.FileUploadException" %>
<%@ page import="org.apache.commons.fileupload.disk.DiskFileItemFactory" %>
<%@ page import="org.apache.commons.fileupload.servlet.ServletFileUpload" %>
<%@ page import="javax.persistence.EntityManager" %>
<%@ page import="javax.persistence.EntityManagerFactory" %>
<%@ page import="javax.persistence.Persistence" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.ArrayList" %>
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
			
			au.edu.uq.eresearch.Collection col = new au.edu.uq.eresearch.Collection();    	    
    	    int id = 0;
    	   	String[] subjects = {};
			ArrayList<Integer> anzsrcFields = new ArrayList<Integer>();
			
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

						if(name.equals("ownerId")){
							col.setOwner_actorid(Integer.parseInt(value));
						}     
						if(name.equals("projectId")){
							col.setProject_projectid(Integer.parseInt(value));
						}  
						if(name.equals("collection_title")){
							col.setTitle(value);
						}
						if(name.equals("collectionType")){
							col.setCollectiontype(value);
						}
						if(name.equals("pid")){
							col.setPid(value);
						}
						if(name.equals("description")){
							col.setDescription(value);
						}
						if(name.equals("related_information")){
							col.setRelatedinformation(value);
						}
						if(name.equals("anzsrc")){
							anzsrcFields.add(Integer.parseInt(value));
						}
						if(name.equals("subjects")){
							subjects = value.split(",");
						}
						if(name.equals("spatial")){
							col.setSpatialFromString(value);
						}
						if(name.equals("begindate")){
							try{
								SimpleDateFormat ts= new SimpleDateFormat("dd/MM/yyyy");
								java.util.Date utilDate = ts.parse(value);
								col.setBegindate(new java.sql.Date((utilDate).getTime()));
							} catch (Exception e){
								
							}
						}
						if(name.equals("enddate")){
							try {	
								SimpleDateFormat ts= new SimpleDateFormat("dd/MM/yyyy");
								java.util.Date utilDate = ts.parse(value);
								col.setEnddate(new java.sql.Date((utilDate).getTime()));
							} catch (Exception e){
							
							}
						}
						if(name.equals("contributors")){
							col.setContributor(value);
						}
						if(name.equals("access_rights")){
							col.setAccessrights(value);
						}
		   		   }
		   		}
		   	}

    	    em.getTransaction().begin();
    	    em.persist(col);
    	    em.getTransaction().commit(); 
    	    
    	    id = col.getCollectionid();
    	    
    	    for(int i = 0; i < subjects.length; i++){
    	    	Subject s = new Subject();
    	    	s.setValue(subjects[i]);
    	    	
        	    em.getTransaction().begin();
        	    em.persist(s);
        	    em.getTransaction().commit(); 
    	    	
        	    Collection_subject cs = new Collection_subject();
        	    cs.setCollection_collectionid(id);
        	    cs.setSubjects_id(s.getId());
        	    
        	    em.getTransaction().begin();
        	    em.persist(cs);
        	    em.getTransaction().commit(); 	
    	    }
    	    
    	    for(int i = 0; i < anzsrcFields.size(); i++){
    	    	Collection_anzsrcfield caf = new Collection_anzsrcfield();
    	    	caf.setAnzsrcfields_code(anzsrcFields.get(i));
    	    	caf.setCollection_collectionid(id);
    	    	
        	    em.getTransaction().begin();
        	    em.persist(caf);
        	    em.getTransaction().commit(); 
    	    }
    	    
    	    em.close();
    	    emf.close();
    	    
    	    response.sendRedirect("/gbrcat/ViewCollection.jsp?id=" + id);
		%>
<jsp:include page="/Footer.jsp"/> 