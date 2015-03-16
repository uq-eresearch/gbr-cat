<%@ page import="au.edu.uq.eresearch.Person" %>
<%@ page import="javax.persistence.EntityManager" %>
<%@ page import="javax.persistence.EntityManagerFactory" %>
<%@ page import="javax.persistence.Persistence" %>
<%@ page import="javax.persistence.Query" %>
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
</head>

<%
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("gisdb");
    EntityManager em = emf.createEntityManager();

    Query query = em.createQuery("SELECT a FROM Person a WHERE actorid = " + request.getParameter("id"));
    List<au.edu.uq.eresearch.Person> actorData = (List<au.edu.uq.eresearch.Person>) query.getResultList();
    if (actorData.isEmpty()) {
        response.setStatus(404);
        response.getWriter().write("<h1>Not Found</h1>");
    } else {
        Person actor = actorData.get(0);
%>
<body>
<jsp:include page="/Body.jsp"/>
<form method="POST" action="/gbrcat/EditActor.jsp?id=<%= actor.getActorid() %>">
    <h4>View actor</h4>

    <h3>Required fields</h3>
    <table class="collection">
        <tr>
            <th>Name:</th>
            <td>
                <%if (actor.getFirstname() != null) { %><%= actor.getFirstname() + " " %><% } %>
                <%if (actor.getLastname() != null) { %><%= actor.getLastname()%><% } %>
            </td>
        </tr>
        <tr>
            <th>Description:</th>
            <td>
                <%if (actor.getDescription() != null) { %><%= actor.getDescription()%><% } %>
            </td>
        </tr>
        <tr>
            <th>Organisation or Department:</th>
            <td>
                <%if (actor.getOrganisation() != null) { %><%= actor.getOrganisation() %><% } %>
            </td>
        </tr>
        <tr>
            <th>Email:</th>
            <td>
                <%if (actor.getEmail() != null) { %><%= actor.getEmail() %><% } %>
            </td>
        </tr>
        <tr>
            <th>Web address:</th>
            <td>
                <%if (actor.getWebaddress() != null) { %><%= actor.getWebaddress() %><% } %>
            </td>
        </tr>
    </table>
    <input class="button" type="submit" value="Edit"/>
    <input class="button" type="button" value="Delete"
           onClick="location.href='/gbrcat/DeleteActor.jsp?id=<%= actor.getActorid() %>'"/>
</form>
<br/><br/>
    <%
	em.close();
	emf.close();
%>
<jsp:include page="/Footer.jsp"/>
<%}%>