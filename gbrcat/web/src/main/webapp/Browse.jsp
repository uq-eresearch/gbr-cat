<%@ page import="com.vividsolutions.jts.geom.Coordinate" %>
<%@ page import="javax.persistence.EntityManager" %>

<%@ page import="javax.persistence.EntityManagerFactory" %>
<%@ page import="javax.persistence.Persistence" %>
<%@ page import="javax.persistence.Query" %>
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
                            <%
                                                EntityManagerFactory emf = Persistence.createEntityManagerFactory("gisdb");
                                                EntityManager em = emf.createEntityManager();

                                                Query query = em.createQuery("SELECT c FROM Collection c");
                                                List<au.edu.uq.eresearch.Collection> allCollections = (List<au.edu.uq.eresearch.Collection>) query.getResultList();

                                                ArrayList<au.edu.uq.eresearch.Collection> collections = new ArrayList<au.edu.uq.eresearch.Collection>();

                                                for(au.edu.uq.eresearch.Collection c : allCollections){
                                                    if(c.getBegindate() != null && c.getEnddate() != null){
                                                        collections.add(c);
                                                    }
                                                }

                                                for(int i = 0; i < collections.size(); i++){
                                                    au.edu.uq.eresearch.Collection collection = collections.get(i);
                                            %>
                            {
                                "start" : "<%=collection.getBegindate()%>",
                                "end" : "<%=collection.getEnddate()%>",
                                <%
                                               if(collection.getSpatial() != null){
                                                   Coordinate[] geo = collection.getSpatial().getCoordinates();
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
                                    }<% if(j < geo.length - 1){%>,
                                    <%}%>
                                    <%
                                                        }
                                                %>
                                ],
                                <%
                                               }
                                                   }
                                               %>
                                "title" : "<%= collection.getTitle() %>",
                                "options" : {
                                    "infoHtml": "<%=collection.getTitle()%><br/><a href='/gbrcat/ViewCollection.jsp?id=<%= collection.getCollectionid()%>'>More...</a>",
                                    "theme": "blue"
                                }
                            }
                            <% 		if(i < collections.size() - 1){
                                                      %>,
                            <%
                                                  }
                                              }
                                          %>
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
                },
            ]

        });
        tm.map.setCenter(new GLatLng(-23.397, 145.644), 5);
    }

</script>
<style>
    div#timelinecontainer {
        height: 300px;
    }

    div#mapcontainer {
        height: 800px;
    }
</style>
</head>
<body onLoad="loadTimeMap()">
<jsp:include page="/Body.jsp"/>
<h4>Browse</h4>

<p>
    The timeline below displays collections according to the dates they apply.
    You can click-and-drag the timeline to look at specific time periods.
    The map below shows collections according to there physical location(s).
    Clicking a collection on the timeline will highlight its location on the map.
</p>

<div id="timemap">
    <div id="timelinecontainer">
        <div id="timeline" class="timeline"></div>
    </div>
    <div id="mapcontainer">
        <div id="map"></div>
    </div>
</div>
    <%
	em.close();
	emf.close();
%>
<jsp:include page="/Footer.jsp"/> 