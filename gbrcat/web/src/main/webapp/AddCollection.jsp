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
</head>
<body>
<jsp:include page="/Body.jsp"/>
<% 	
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("gisdb");
	EntityManager em = emf.createEntityManager();
%>
<h4>Add new collection</h4>
<h3>Required fields</h3>
<form method="POST" action="/gbrcat/SaveCollection.jsp" enctype="multipart/form-data">
<table class="collection">
	<tr>
		<th>Owner:</th>
		<td>
			<select name="ownerId" style="width:95%">
		        <option value=""></option>
		        <%
					Query query = em.createQuery("SELECT a FROM Person a ORDER BY actorid");
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
		<th>Project:</th>
        <td>
        	<select name="projectId" style="width:95%">
            	<option value=""></option>
            	<%
					query = em.createQuery("SELECT p FROM Project p ORDER BY projectid");
				    List<au.edu.uq.eresearch.Project> projectData = (List<au.edu.uq.eresearch.Project>) query.getResultList();
					
				    for(int i = 0; i < projectData.size();i++){
				    	au.edu.uq.eresearch.Project p = projectData.get(i);
				    	%><option value="<%= p.getProjectid() %>"><%= p.getTitle() %></option><%
				    }
		        %>
                </select>
        </tr>

	<tr>
		<th>Collection Title:</th>
		<td><input type="text" class="long" name="collection_title" value="" style="width:95%"/> <br>
		You should ensure that this name will be meaningful to researchers
		outside your discipline area.</td>
	</tr>
	<tr>
		<th>Collection type</th>
		<td>            
            <input type="radio" name="collectionType"   value="Catalog" />Catalog

		    <input type="radio" name="collectionType"   value="Collection" />Collection

		    <input type="radio" name="collectionType"   value="Registry" />Registry

		    <input type="radio" name="collectionType"  value="Repository" />Repository

		    <input type="radio" name="collectionType"  value="Dataset"/>Dataset
		</td>
	<tr>
		<th>Identifier:</th>
		<td><input type="text" class="long" name="pid" value="" style="width:95%"/></td>
	</tr>
	<tr>
		<th>Brief Description:</th>
		<td><textarea cols="55" rows="10" name="description" style="width:95%"></textarea>
		<br>
		Provide a short description of your collection. You should ensure that
		this description will be meaningful to researchers outside your
		discipline area. Try to include key information (who, what, when,
		where, how) that will help other researchers to discover your
		collection via text searching.</td>
	</tr>
	<tr>
		<th>Related Information:</th>
		<td><input type="text" class="long" name="related_information" value="" style="width:95%"/></td>
	</tr>
</table>

<h3>Optional fields</h3>
<table class="collection">

	<!-- ANZSRC Fields of research -->
	<tr>
		<th>ANZSRC Fields of Research:</th>
		<td>
			<select name="anzsrc" multiple style="width:95%">
		<%
			query = em.createQuery("SELECT af FROM Anzsrcfield af ORDER BY division,grouping");
			List<au.edu.uq.eresearch.Anzsrcfield> anzsrcFields = (List<au.edu.uq.eresearch.Anzsrcfield>) query.getResultList();
			
		  	String division = "";
		  	String grouping = "";
			
			for(int i = 0; i < anzsrcFields.size();i++){
			  	au.edu.uq.eresearch.Anzsrcfield a = anzsrcFields.get(i);
				if(!(a.getDivision().equals(division))){
					division = a.getDivision();
					grouping = a.getGrouping();
					%><optgroup class="division" label="<%= division %>"></optgroup>
					<optgroup class="group" label="<%= grouping %>"></optgroup>
					<option class="field" value="<%= a.getCode() %>"><%= a.getName() %></option><%
				} else if(!(a.getGrouping().equals(grouping))){
					grouping = a.getGrouping();
					%><optgroup class="group" label="<%= grouping %>"></optgroup>
					<option class="field" value="<%= a.getCode() %>"><%= a.getName() %></option><%
				} else {
					%><option class="field" value="<%= a.getCode() %>"><%= a.getName() %></option><%
				}
			}
			em.close();
			emf.close();
	    %>
		</select> <br>
		Choose a field of research from the vocabulary widget. You can find
		descriptive information about the ANZSRC tags on the ABS website.
		Click on the add button to add additional field of research tags for
		your collection.</td>
	</tr>
	
	<!-- Subject keywords -->
	<tr>
		<th>Subject Keywords:</th>
		<td><textarea cols="55" rows="10" name="subjects" style="width:95%"></textarea>
		<br>
		<strong>Comma separated</strong> key subject terms relating to this
		collection?</td>
	</tr>
	
	<!-- Spatial -->
	<tr>
		<th>Spatial coverage:</th>
		<td>
			<textarea cols="55" rows="10" name="spatial" style="width:95%"></textarea>
		<br>
		The geographical coverage of the collection's data described as a set
		of KML long/lat coordinates defining a polygon as described by the <a
			href="http://code.google.com/apis/kml/documentation/kmlreference.html#coordinates"
			target="_blank">KML coordinates element</a>. For example '-9.3985
		143.4947,-9.3985 154.9387,-24.4123 155.3876,-30.5956 149.4398,-45.5735
		145.3856,-45.5232 130.4967,-34.4765 117.4523,-37.3957
		113.5287,-34.4645 112.4745,-21.3876 129.4475,-10.3825 143.4947,-9.3985
		143.4947' (without the quotes).</td>
	</tr>
	
	<!-- Temporal -->
	<tr>
		<th>Temporal coverage:</th>
		<td>
			Begin date: <input type="text" class="w8em format-d-m-y highlight-days-67 range-low-today" name="begindate" id="sd" value="" maxlength="10" /> 
			End date: <input type="text" class="w8em format-d-m-y highlight-days-67 range-low-today" name="enddate" id="ed" value="" maxlength="10" />
			<br>
			Enter date range to which your collection represents. <strong>DD/MM/YYYY</strong>.
		</td>
	</tr>
	
	<!-- Contributors -->
	<tr>
		<th>Contributors</th>
		<td><textarea cols="55" rows="10" name="contributors" style="width:95%"></textarea>
		<br>
		You should enter their name and role in the form: Dr John Smith,
		Research Assistant. You can include institutions or organisations in
		this section. e.g. Australian National University.</td>
	</tr>
	
	<!-- Access Rights -->
	<tr>
		<th>Access Rights</th>
		<td><textarea cols="55" rows="10" name="access_rights" style="width:95%"></textarea>
		<br>
		Is the data publicly available or are there security, privacy or
		ethics constraints in place?</td>
	</tr>
</table>
<br />
<input class="button" type="submit" value="Save" />
<input class="button" type="reset" value="Cancel" onclick="window.location='/gbrcat/Overview.jsp'" />
<br />
<br />
</form>
<jsp:include page="/Footer.jsp"/> 