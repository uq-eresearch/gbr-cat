<%@page import="au.edu.uq.eresearch.*"%>
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
	
	String id = request.getParameter("id");
	
	Query query = em.createQuery("SELECT c FROM Collection_subject c WHERE collection_collectionid = " + request.getParameter("id"));
    List<au.edu.uq.eresearch.Collection_subject> collectionSubData = (List<au.edu.uq.eresearch.Collection_subject>) query.getResultList();
	
	for(Collection_subject col : collectionSubData){
		em.getTransaction().begin();
		em.remove(col);
		em.getTransaction().commit(); 
	}
	
	query = em.createQuery("SELECT c FROM Collection_anzsrcfield c WHERE collection_collectionid = " + request.getParameter("id"));
    List<au.edu.uq.eresearch.Collection_anzsrcfield> collectionAFData = (List<au.edu.uq.eresearch.Collection_anzsrcfield>) query.getResultList();
	
	for(Collection_anzsrcfield col : collectionAFData){
		em.getTransaction().begin();
		em.remove(col);
		em.getTransaction().commit(); 
	}
	
	query = em.createQuery("SELECT c FROM Collection c WHERE collectionid = " + request.getParameter("id"));
    List<au.edu.uq.eresearch.Collection> collectionData = (List<au.edu.uq.eresearch.Collection>) query.getResultList();
	au.edu.uq.eresearch.Collection col = collectionData.get(0);

	em.getTransaction().begin();
	em.remove(col);
	em.getTransaction().commit(); 
	    
	em.close();
	emf.close();
	    
	response.sendRedirect("/gbrcat/Overview.jsp");
%>
<jsp:include page="/Footer.jsp"/> 

collection actor 
collection subject
collection anzsrc 