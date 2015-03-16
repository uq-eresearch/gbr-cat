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
<body>
<jsp:include page="/Body.jsp"/>
<h4>Search and browse overview</h4>
	Allow collections of research data to be indexed, registered and
	searched. To begin, select from the following options:
	<ul>
		<li>To enter a new collection into the system select <a
			href="/gbrcat/AddCollection.jsp">Add Collection</a>.</li>
        <li>To enter a new actor into the system select <a
                        href="/gbrcat/AddActor.jsp">Add Actor</a>.</li>
        <li>To enter a new project into the system select <a
                        href="/gbrcat/AddProject.jsp">Add Project</a>.</li>		
		<li>Search for collection by keyword using the <a
			href="/gbrcat/Search.jsp">Search</a> section.</li>
		<li>Browse collections using a timeline and map interface in the <a
			href="/gbrcat/Browse.jsp">Browse</a> section.</li>
	</ul>
<jsp:include page="Footer.jsp"/> 