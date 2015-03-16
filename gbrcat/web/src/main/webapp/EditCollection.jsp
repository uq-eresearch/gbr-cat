<%@ page import="javax.persistence.EntityManager" %>
<%@ page import="javax.persistence.EntityManagerFactory" %>
<%@ page import="javax.persistence.Persistence" %>
<%@ page import="javax.persistence.Query" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
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
	
	Query query = em.createQuery("SELECT c FROM Collection c WHERE collectionid = " + request.getParameter("id"));
    List<au.edu.uq.eresearch.Collection> collectionData = (List<au.edu.uq.eresearch.Collection>) query.getResultList();
    au.edu.uq.eresearch.Collection col = collectionData.get(0);
%>
<h4>Add new collection</h4>

<h3>Required fields</h3>

<form method="POST" action="/gbrcat/UpdateCollection.jsp?id=<%= col.getCollectionid() %>" enctype="multipart/form-data">
<table class="collection">
    <tr>
        <th>Owner:</th>
        <td>
            <select name="ownerId" style="width:95%">
                <option value=""></option>
                <%
                    query = em.createQuery("SELECT a FROM Person a ORDER BY actorid");
                    List<au.edu.uq.eresearch.Person> actorData = (List<au.edu.uq.eresearch.Person>) query.getResultList();

                    for (int i = 0; i < actorData.size(); i++) {
                        au.edu.uq.eresearch.Person a = actorData.get(i);
                        if (col.getOwner_actorid() == a.getActorid()) {
                %>
                <option value="<%= a.getActorid() %>" selected="selected"><%= a.toString() %>
                </option>
                <%
                } else {
                %>
                <option value="<%= a.getActorid() %>"><%= a.toString() %>
                </option>
                <%
                        }
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

                    for (int i = 0; i < projectData.size(); i++) {
                        au.edu.uq.eresearch.Project p = projectData.get(i);
                        if (col.getProject_projectid() == p.getProjectid()) {
                %>
                <option value="<%= p.getProjectid() %>" selected="selected"><%= p.getTitle() %>
                </option>
                <%
                } else {
                %>
                <option value="<%= p.getProjectid() %>"><%= p.getTitle() %>
                </option>
                <%
                        }
                    }
                %>
            </select>
    </tr>

    <tr>
        <th>Collection Title:</th>
        <td><input type="text" class="long" name="collection_title" style="width:95%" value="<%= col.getTitle() %>"/>
            <br>
            You should ensure that this name will be meaningful to researchers
            outside your discipline area.
        </td>
    </tr>
    <tr>
        <th>Collection type</th>
        <td>
            <%
                if (col.getCollectiontype().equals("Catalog")) {
            %><input type="radio" name="collectionType" value="Catalog" checked="checked"/>Catalog<%
        } else {
        %><input type="radio" name="collectionType" value="Catalog"/>Catalog<%
            }
            if (col.getCollectiontype().equals("Collection")) {
        %><input type="radio" name="collectionType" value="Collection" checked="checked"/>Collection<%
        } else {
        %><input type="radio" name="collectionType" value="Collection"/>Collection<%
            }
            if (col.getCollectiontype().equals("Registry")) {
        %><input type="radio" name="collectionType" value="Registry" checked="checked"/>Registry<%
        } else {
        %><input type="radio" name="collectionType" value="Registry"/>Registry<%
            }
            if (col.getCollectiontype().equals("Repository")) {
        %><input type="radio" name="collectionType" value="Repository" checked="checked"/>Repository<%
        } else {
        %><input type="radio" name="collectionType" value="Repository"/>Repository<%
            }
            if (col.getCollectiontype().equals("Dataset")) {
        %><input type="radio" name="collectionType" value="Dataset" checked="checked"/>Dataset<%
        } else {
        %><input type="radio" name="collectionType" value="Dataset"/>Dataset<%
            }
        %>

        </td>
    <tr>
        <th>Identifier:</th>
        <td><input type="text" class="long" name="pid" value="<%= col.getPid() %>" style="width:95%"/></td>
    </tr>
    <tr>
        <th>Brief Description:</th>
        <td>
            <textarea cols="55" rows="10" name="description" style="width:95%"><%= col.getDescription() %>
            </textarea>
            <br>
            Provide a short description of your collection. You should ensure that
            this description will be meaningful to researchers outside your
            discipline area. Try to include key information (who, what, when,
            where, how) that will help other researchers to discover your
            collection via text searching.
        </td>
    </tr>
    <tr>
        <th>Related Information:</th>
        <td><input type="text" class="long" name="related_information" value="<%= col.getRelatedinformation() %>"
                   style="width:95%"/></td>
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
                    query = em.createQuery("SELECT ca FROM Collection_anzsrcfield ca WHERE collection_collectionid = " + request.getParameter("id"));
                    List<au.edu.uq.eresearch.Collection_anzsrcfield> anzsrcCodes = (List<au.edu.uq.eresearch.Collection_anzsrcfield>) query.getResultList();

                    ArrayList<Integer> selectedcodes = new ArrayList<Integer>();

                    for (int i = 0; i < anzsrcCodes.size(); i++) {
                        au.edu.uq.eresearch.Collection_anzsrcfield caf = anzsrcCodes.get(i);

                        selectedcodes.add(caf.getAnzsrcfields_code());
                    }

                    query = em.createQuery("SELECT af FROM Anzsrcfield af ORDER BY division,grouping");
                    List<au.edu.uq.eresearch.Anzsrcfield> anzsrcFields = (List<au.edu.uq.eresearch.Anzsrcfield>) query.getResultList();

                    String division = "";
                    String grouping = "";

                    for (int i = 0; i < anzsrcFields.size(); i++) {
                        au.edu.uq.eresearch.Anzsrcfield a = anzsrcFields.get(i);
                        if (!(a.getDivision().equals(division))) {
                            division = a.getDivision();
                            grouping = a.getGrouping();
                %>
                <optgroup class="division" label="<%= division %>"></optgroup>
                <optgroup class="group" label="<%= grouping %>"></optgroup>
                <%
                    if (selectedcodes.contains(a.getCode())) {
                %>
                <option class="field" value="<%= a.getCode() %>" selected="selected"><%= a.getName() %>
                </option>
                <%
                } else {
                %>
                <option class="field" value="<%= a.getCode() %>"><%= a.getName() %>
                </option>
                <%
                    }
                } else if (!(a.getGrouping().equals(grouping))) {
                    grouping = a.getGrouping();
                %>
                <optgroup class="group" label="<%= grouping %>"></optgroup>
                <%
                    if (selectedcodes.contains(a.getCode())) {
                %>
                <option class="field" value="<%= a.getCode() %>" selected="selected"><%= a.getName() %>
                </option>
                <%
                } else {
                %>
                <option class="field" value="<%= a.getCode() %>"><%= a.getName() %>
                </option>
                <%
                    }
                } else {
                    if (selectedcodes.contains(a.getCode())) {
                %>
                <option class="field" value="<%= a.getCode() %>" selected="selected"><%= a.getName() %>
                </option>
                <%
                } else {
                %>
                <option class="field" value="<%= a.getCode() %>"><%= a.getName() %>
                </option>
                <%
                            }
                        }
                    }
                %>
            </select> <br>
            Choose a field of research from the vocabulary widget. You can find
            descriptive information about the ANZSRC tags on the ABS website.
            Click on the add button to add additional field of research tags for
            your collection.
        </td>
    </tr>

    <!-- Subject keywords -->
    <tr>
        <th>Subject Keywords:</th>
        <td><textarea cols="55" rows="10" name="subjects" style="width:95%"><%
            query = em.createQuery("SELECT cs FROM Collection_subject cs WHERE collection_collectionid = " + request.getParameter("id"));
            List<au.edu.uq.eresearch.Collection_subject> collectionSubjects = (List<au.edu.uq.eresearch.Collection_subject>) query.getResultList();

            for (int i = 0; i < collectionSubjects.size(); i++) {
                au.edu.uq.eresearch.Collection_subject cs = collectionSubjects.get(i);

                query = em.createQuery("SELECT s FROM Subject s WHERE id = " + cs.getSubjects_id());
                List<au.edu.uq.eresearch.Subject> subject = (List<au.edu.uq.eresearch.Subject>) query.getResultList();
                au.edu.uq.eresearch.Subject s = subject.get(0);

        %><%= s.getValue() %><%

            if (i < (collectionSubjects.size() - 1)) {
        %>,<%
                }
            }
        %></textarea>
            <br>
            <strong>Comma separated</strong> key subject terms relating to this
            collection?
        </td>
    </tr>

    <!-- Spatial -->
    <tr>
        <th>Spatial coverage:</th>
        <td>
            <textarea cols="55" rows="10" name="spatial" style="width:95%"><%= col.getSpatialString() %>
            </textarea>
            <br>
            The geographical coverage of the collection's data described as a set
            of KML long/lat coordinates defining a polygon as described by the <a
                href="http://code.google.com/apis/kml/documentation/kmlreference.html#coordinates"
                target="_blank">KML coordinates element</a>. For example '-9.3985
            143.4947,-9.3985 154.9387,-24.4123 155.3876,-30.5956 149.4398,-45.5735
            145.3856,-45.5232 130.4967,-34.4765 117.4523,-37.3957
            113.5287,-34.4645 112.4745,-21.3876 129.4475,-10.3825 143.4947,-9.3985
            143.4947' (without the quotes).
        </td>
    </tr>

    <!-- Temporal -->
    <tr>
        <th>Temporal coverage:</th>
        <td>
            Begin date: <input type="text" class="w8em format-d-m-y highlight-days-67 range-low-today" name="begindate"
                               id="sd"
                               value="<%= (new SimpleDateFormat("dd/MM/yyyy")).format(col.getBegindate()) %>"
                               maxlength="10"/>
            End date: <input type="text" class="w8em format-d-m-y highlight-days-67 range-low-today" name="enddate"
                             id="ed"
                             value="<%= (new SimpleDateFormat("dd/MM/yyyy")).format(col.getEnddate()) %>"
                             maxlength="10"/>
            <br>
            Enter date range to which your collection represents. <strong>DD/MM/YYYY</strong>.
        </td>
    </tr>

    <!-- Contributors -->
    <tr>
        <th>Contributors</th>
        <td><textarea cols="55" rows="10" name="contributors" style="width:95%"><%= col.getContributor() %>
        </textarea>
            <br>
            You should enter their name and role in the form: Dr John Smith,
            Research Assistant. You can include institutions or organisations in
            this section. e.g. Australian National University.
        </td>
    </tr>

    <!-- Access Rights -->
    <tr>
        <th>Access Rights</th>
        <td><textarea cols="55" rows="10" name="access_rights" style="width:95%"><%= col.getAccessrights() %>
        </textarea>
            <br>
            Is the data publicly available or are there security, privacy or
            ethics constraints in place?
        </td>
    </tr>
</table>
<br/>
<input class="button" type="submit" value="Save"/>
<input class="button" type="reset" value="Cancel" onclick="window.location='/gbrcat/Overview.jsp'"/>
<br/>
<br/>
</form>
<br/><br/>
    <%
	em.close();
	emf.close();
%>
<jsp:include page="/Footer.jsp"/> 