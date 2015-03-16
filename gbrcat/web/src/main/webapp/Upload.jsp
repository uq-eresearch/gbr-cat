<%@ page import="javax.persistence.*" %>
<%@ page import="java.util.*" %>
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
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("gisdb");
	EntityManager em = emf.createEntityManager();
				
	Query query = em.createQuery("SELECT p FROM Project p ORDER BY projectid");
    List<au.edu.uq.eresearch.Project> projectData = (List<au.edu.uq.eresearch.Project>) query.getResultList();
%>
<SCRIPT LANGUAGE="javascript">
	function fillCategory(){ 
		addOption("project", "", "");
		<%
		    for(int i = 0; i < projectData.size();i++){
		    	au.edu.uq.eresearch.Project p = projectData.get(i);
    	%>
    	addOption("project", "<%= p.getProjectid() %>", "<%= p.getTitle() %>");<%
		    }
		%>
	}

	function OnChange(){
		removeAllOptions();
		addOption("collection", "", "");

		<%
		    for(int i = 0; i < projectData.size();i++){
		    	au.edu.uq.eresearch.Project p = projectData.get(i);
		%>
		if(document.getElementById("project").value == '<%= p.getProjectid() %>'){<%
				query = em.createQuery("SELECT c FROM Collection c WHERE project_projectid = " + p.getProjectid() + "ORDER BY collectionid");
			    List<au.edu.uq.eresearch.Collection> collectionData = (List<au.edu.uq.eresearch.Collection>) query.getResultList();
				
			    for(int j = 0; j < collectionData.size();j++){
			    	au.edu.uq.eresearch.Collection c = collectionData.get(j);
			    	if(c.getProject_projectid() == p.getProjectid()){
    		%>
    			addOption("collection","<%= c.getCollectionid() %>", "<%= c.getTitle() %>");<%
			    	}
			    }
		    %>
		}
		<%
		    }
		%>
	}

	function removeAllOptions(){
		var selectbox = document.getElementById("collection");
		
		var i;
		for(i=selectbox.options.length-1;i>=0;i--)
		{
			selectbox.remove(i);
		}
	}


	function addOption(id, value, text )
	{
		var select = document.getElementById(id);
		
		var opt = document.createElement("option");
		opt.value = value;
		opt.text = text;
		select.options.add(opt);
	}
</SCRIPT>  
</head>
<body onload="fillCategory();">
<jsp:include page="/Body.jsp"/>
<form class="upload" method=post enctype="multipart/form-data" action="/gbrcat/FileUpload.jsp" >
<h4>Upload data</h4>
<table class="collection">
   	<tr>
		<th>Actor</th>
		<td>
			<select name="name" style="width:95%">
		        <option value=""></option>
		        <%
					query = em.createQuery("SELECT a FROM Person a ORDER BY actorid");
				    List<au.edu.uq.eresearch.Person> actorData = (List<au.edu.uq.eresearch.Person>) query.getResultList();
					
				    for(int i = 0; i < actorData.size();i++){
				    	au.edu.uq.eresearch.Person a = actorData.get(i);
				    	%><option value="<%= a.getActorid() %>"><%= a.toString() %></option><%
				    }
		        %>
			</select>
		</td>
    </tr>
   	<tr>
		<th>Project</th>
       <td>
       	<select id='project' name="project" style="width:95%" onchange='OnChange()'>
        </select>
    </tr>
    <tr>
		<th>Collection</th>
       <td>
       	<select id='collection' name="collection" style="width:95%">
        </select>
    </tr>
    <tr>
		<th>Data Description</th>
		<td>
			<input type="text" name= "description" style="width:95%">
		</td>
    </tr>
   	<tr>
		<th>Data file<br/>(.csv or .xlsx)</th>
		<td>
			<input type="file" name="datafile" style="width:95%" >
		</td>
    </tr>
</table>
<input type="submit" value="Upload">
<input class="button" type="reset" value="Cancel" onclick="window.location='/gbrcat/Home.jsp'" />
</form> 
<br />
<%
	em.close();
	emf.close();
%>
<br />
If you have not previously uploaded data to GBRCAT, please contact the <a href="mailto:o.pantos@uq.edu.au">administrator</a> to be registered.
<br />
<br />
Please note, large files may take a while to process.
<br />
<br />
<a href="static/example.csv" >Example spreadsheet</a>
<jsp:include page="Footer.jsp"/> 