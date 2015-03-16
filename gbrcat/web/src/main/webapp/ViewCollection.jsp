<%@ page import="au.edu.uq.eresearch.Collection" %>
<%@ page import="au.edu.uq.eresearch.app.CollectionsRegistryConfiguration" %>
<%@ page import="com.vividsolutions.jts.geom.Coordinate" %>

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
<%
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("gisdb");
    EntityManager em = emf.createEntityManager();

    Query query = em.createQuery("SELECT c FROM Collection c WHERE collectionid = " + request.getParameter("id"));
    List<au.edu.uq.eresearch.Collection> collectionData = (List<au.edu.uq.eresearch.Collection>) query.getResultList();
    if (collectionData.isEmpty()) {
        response.setStatus(404);
        response.getWriter().write("<h1>Not Found</h1>");
    } else {
        Collection col = collectionData.get(0);

%>
<script src="http://maps.google.com/maps?file=api&amp;v=2&amp;sensor=true&amp;key=ABQIAAAAIKvD4QcEJbBg5VRcnDv7ExSuTrH6Z2pVHS3veiumQZWew7D9sRRvhLoSh3mP_bQP29nvfOVzFy1d5w"
        type="text/javascript"></script>
<script src="http://static.simile.mit.edu/timeline/api-2.3.0/timeline-api.js" type="text/javascript"></script>
<script src="static/timemap_full.pack.js" type="text/javascript"></script>
<script type="text/javascript">
    var tm;
    function loadTimeMap() {

        tm = TimeMap.init({
            mapId: "map",               // Id of map div element (required)
            timelineId: "timeline",     // Id of timeline div element (required)
            options: {
                eventIconPath: "http://gbrcat.metadata.net/ers-colreg/images/timemap-themes/",
                // Intial map zoom level
                mapZoom: 4,
                hidePastFuture: false,
                centerMapOnItems: true
            },
            datasets: [
                {
                    id: "collections",
                    title: "Collections",
                    theme: "orange",
                    // note that the lines below are now the preferred syntax
                    type: "basic",
                    options: {
                        items: [

                            {
                                <%
                                if(col.getBegindate() != null && col.getEnddate() != null){
                                %>
                                "start" : "<%=col.getBegindate()%>",
                                "end" : "<%=col.getEnddate()%>",
                                <%
                                }
                                if(col.getSpatial() != null){
                                    Coordinate[] geo = col.getSpatial().getCoordinates();
                                    if(geo.length == 1){
                                %>
                                "point" : {
                                    "lat" : <%= geo[0].x %>,
                                    "lon" : <%= geo[0].y %>
                                },
                                <%
                                    } else if(geo.length > 1){
                                %>
                                "polygon" : [
                                    <%
                                    for(int j = 0; j < geo.length;j++){
                                        Coordinate co = geo[j];
                                    %>
                                    {
                                        "lat": <%= co.x %>,
                                        "lon": <%= co.y %>
                                    }
                                    <% if(j < geo.length - 1){%>,
                                    <%}%>
                                    <%
                                    }
                                    %>
                                ],
                                <%
                                    }
                                }
                                %>
                                "title" : "<%= col.getTitle() %>",
                                "options" : {
                                    "infoHtml": "<%=col.getTitle()%><br/><a href='/gbrcat/ViewCollection.jsp?id=<%= col.getCollectionid()%>'>More...</a>",
                                    "theme": "blue"
                                }
                            }

                        ]
                    }
                }
            ],
            bandInfo: [
                {
                    width:          "70%",
                    intervalUnit:   Timeline.DateTime.MONTH,
                    intervalPixels: 35
                },
                {
                    width:          "30%",
                    intervalUnit:   Timeline.DateTime.YEAR,
                    intervalPixels: 60
                }
            ]

        });
    }

</script>
<style>
    div#timelinecontainer {
        height: 200px;
    }

    div#mapcontainer {
        height: 300px;
    }
</style>
</head>
<body onLoad="loadTimeMap()">
<jsp:include page="/Body.jsp"/>
<form method="post" action="/gbrcat/EditCollection.jsp?id=<%= col.getCollectionid() %>">
    <h4>View Project</h4>

    <h3>Required fields</h3>
    <table class="collection">
        <tr>
            <th>Collection title:</th>
            <td><%= col.getTitle() %>
            </td>
        </tr>
        <tr>
            <th>Collection type:</th>
            <td><%= col.getCollectiontype() %>
            </td>
        </tr>
        <tr>
            <th>Identifer:</th>
            <%
                String collectionId = CollectionsRegistryConfiguration.getApplicationURIPrefix() + au.edu.uq.eresearch.app.Constants.VIEW_COLLECTION_PATH + col.getCollectionid();
            %>
            <td><a href="<%= collectionId %>"><%= collectionId %>
            </a></td>
        </tr>
        <tr>
            <th>Description:</th>
            <td><%= col.getDescription() %>
            </td>
        </tr>
        <tr>
            <th>Related Information:</th>
            <td><%= col.getRelatedinformation() %>
            </td>
        </tr>
        <tr>
            <th>Created by:</th>
            <td><a href="/gbrcat/ViewActor.jsp?id=<%= col.getOwner_actorid() %>">
                <%
                    query = em.createQuery("SELECT a FROM Person a WHERE actorid = " + col.getOwner_actorid());
                    List<au.edu.uq.eresearch.Person> actorData = (List<au.edu.uq.eresearch.Person>) query.getResultList();
                    au.edu.uq.eresearch.Person act = actorData.get(0);
                %>
                <%= act.toString() %>
            </a></td>
        </tr>
        <tr>
            <th>Part of:</th>

            <td><a href="/gbrcat/ViewProject.jsp?id=<%= col.getProject_projectid() %>">
                <%
                    query = em.createQuery("SELECT p FROM Project p WHERE projectid = " + col.getProject_projectid());
                    List<au.edu.uq.eresearch.Project> projectData = (List<au.edu.uq.eresearch.Project>) query.getResultList();
                    au.edu.uq.eresearch.Project pro = projectData.get(0);
                %>
                <%= pro.getTitle() %>
            </a></td>
        </tr>
        <tr>
            <th>ANZSRC Fields of Research:</th>
            <td>
                <%
                    query = em.createQuery("SELECT ca FROM Collection_anzsrcfield ca WHERE collection_collectionid = " + request.getParameter("id"));
                    List<au.edu.uq.eresearch.Collection_anzsrcfield> anzsrcFields = (List<au.edu.uq.eresearch.Collection_anzsrcfield>) query.getResultList();

                    for (int i = 0; i < anzsrcFields.size(); i++) {
                        au.edu.uq.eresearch.Collection_anzsrcfield caf = anzsrcFields.get(i);

                        query = em.createQuery("SELECT a FROM Anzsrcfield a WHERE code = " + caf.getAnzsrcfields_code());
                        List<au.edu.uq.eresearch.Anzsrcfield> anzsrc = (List<au.edu.uq.eresearch.Anzsrcfield>) query.getResultList();
                        au.edu.uq.eresearch.Anzsrcfield af = anzsrc.get(0);

                %><%=af.getName() %><%

                if (i < (anzsrcFields.size() - 1)) {
            %><br/><%
                    }
                }
            %>
            </td>
        </tr>
        <tr>
            <th>Subjects:</th>
            <td>
                <%
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
            %>
            </td>
        </tr>
        <%--<%--%>
        <%--if (col.getSpatial() != null) {--%>
        <%--%>--%>
        <tr>
            <th>Spatial Coverage</th>
            <td><%= col.getSpatialString() %><br/>

                <div id="mapcontainer">
                    <div id="map"></div>
                </div>
            </td>
        </tr>
        <%
            //        }
            if (col.getBegindate() != null && col.getEnddate() != null) {
        %>
        <tr>
            <th>Temporal coverage:</th>
            <td>
                <div id="timelinecontainer">
                    <div id="timeline" class="timeline"></div>
                </div>
                <br/>From: <%= col.getBegindate().toString() %> To: <%= col.getEnddate().toString() %>
            </td>
        </tr>
        <%--if (col.getSpatial() != null) {--%>
        <%--%>--%>
        <%--<tr>--%>
        <%--<th>Spatial Coverage</th>--%>
        <%--<td><%= col.getSpatialString() %>--%>
        <%--</td>--%>
        <%--</tr>--%>
        <%--<%--%>
        <%--}--%>
        <%--if (col.getBegindate() != null && col.getEnddate() != null) {--%>
        <%--%>--%>
        <%--<tr>--%>
        <%--<th>Temporal coverage:</th>--%>
        <%--<td>--%>
        <%--<div id="timelinecontainer">--%>
        <%--<div id="timeline" class="timeline"></div>--%>
        <%--</div>--%>
        <%--From: <%= col.getBegindate().toString() %> To: <%= col.getEnddate().toString() %>--%>
        <%--</td>--%>
        <%--</tr>--%>
        <%--<%--%>
        <%--}--%>
        <%
            }
            if (col.getContributor() != null) {
        %>
        <tr>
            <th>Contributor():</th>
            <td><%= col.getContributor() %>
            </td>
        </tr>
        <%
            }
            if (col.getAccessrights() != null) {
        %>
        <tr>
            <th>Access Rights:</th>
            <td><%= col.getAccessrights() %>
            </td>
        </tr>
        <%
            }
        %>

    </table>
    <input class="button" type="submit" value="Edit"/>
    <input class="button" type="button" value="Delete"
           onClick="location.href='/gbrcat/DeleteCollection.jsp?id=<%= col.getCollectionid() %>'"/>
</form>
<br/><br/>
    <%
	em.close();
	emf.close();
%>
<jsp:include page="/Footer.jsp"/>
<%}%>