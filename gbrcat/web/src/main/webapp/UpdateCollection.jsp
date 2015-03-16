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
<%@ page import="javax.persistence.Query" %>
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
			
			String id = request.getParameter("id");
			
			Query query = em.createQuery("SELECT c FROM Collection c WHERE collectionid = " + id);
		    List<au.edu.uq.eresearch.Collection> collectionData = (List<au.edu.uq.eresearch.Collection>) query.getResultList();
			au.edu.uq.eresearch.Collection col = collectionData.get(0);
			
    	   	String[] subjects = {};
			ArrayList<Integer> anzsrcFields = new ArrayList<Integer>();
			
			query = em.createQuery("SELECT ca FROM Collection_anzsrcfield ca WHERE collection_collectionid = " + request.getParameter("id"));
    		List<au.edu.uq.eresearch.Collection_anzsrcfield> oldAnzsrcFields = (List<au.edu.uq.eresearch.Collection_anzsrcfield>) query.getResultList();
    		
    	    for(Collection_anzsrcfield ca: oldAnzsrcFields){
        	    em.getTransaction().begin();
        	    em.remove(ca);
        	    em.getTransaction().commit(); 
    	    }
    		
    		query = em.createQuery("SELECT cs FROM Collection_subject cs WHERE collection_collectionid = " + request.getParameter("id"));
    		List<au.edu.uq.eresearch.Collection_subject> oldCollectionSubjects = (List<au.edu.uq.eresearch.Collection_subject>) query.getResultList();
    		
    	    for(Collection_subject cs: oldCollectionSubjects){
    	    	query = em.createQuery("SELECT s FROM Subject s WHERE id = " + cs.getSubjects_id());
		    	List<au.edu.uq.eresearch.Subject> subject = (List<au.edu.uq.eresearch.Subject>) query.getResultList();
		    	au.edu.uq.eresearch.Subject s = subject.get(0);

        	    em.getTransaction().begin();
        	    em.remove(cs);
        	    em.remove(s);
        	    em.getTransaction().commit(); 
    	    }
    		
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
    	    
    	    for(int i = 0; i < subjects.length; i++){
    	    	Subject s = new Subject();
    	    	s.setValue(subjects[i]);
    	    	
        	    em.getTransaction().begin();
        	    em.persist(s);
        	    em.getTransaction().commit(); 
    	    	
        	    Collection_subject cs = new Collection_subject();
        	    cs.setCollection_collectionid(Integer.parseInt(id));
        	    cs.setSubjects_id(s.getId());
        	    
        	    em.getTransaction().begin();
        	    em.persist(cs);
        	    em.getTransaction().commit(); 	
    	    }
    	    
    	    for(int i = 0; i < anzsrcFields.size(); i++){
    	    	Collection_anzsrcfield caf = new Collection_anzsrcfield();
    	    	caf.setAnzsrcfields_code(anzsrcFields.get(i));
    	    	caf.setCollection_collectionid(Integer.parseInt(id));
    	    	
        	    em.getTransaction().begin();
        	    em.persist(caf);
        	    em.getTransaction().commit(); 
    	    }
    	    
    	    em.close();
    	    emf.close();
    	    
    	    response.sendRedirect("/gbrcat/ViewCollection.jsp?id=" + id);
		%>
<jsp:include page="/Footer.jsp"/> 