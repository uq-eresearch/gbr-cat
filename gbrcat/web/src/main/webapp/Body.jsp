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

<div id="header">
		<table width="100%">
			<tr>
				<td><img src="static/DSCN0288.jpg" width=100 height=75 /></td>  
				<td><img src="static/DSCN0781.jpg" width=100 height=75 /></td>
				<td><img src="static/DSCN01385.jpg" width=100 height=75 /></td>
				<td style="text-align: center"><h1><font style="font-family:Tahoma" color="white">Great Barrier Reef - Climate Analysis Tools (GBR-CAT)</font></h1></td>
				<td><img src="static/afangblenny-thumb.jpg" width=100 height=75 /></td>  
				<td><img src="static/cots-thumb.jpg" width=100 height=75 /></td>
				<td><img src="static/gt-thumb.jpg" width=100 height=75 /></td>
			</tr>
		</table>
	</div>
	
	<div id="navcontainer">
		<div id="menu">
		<br />
		<ul>
			<li><h2><a href="/gbrcat/Overview.jsp"><font style="font-family:Tahoma" color="white">Overview</font></a></h2></li>
			<li id="li_with_sublist"><h2><a href="/gbrcat/SearchAndBrowse.jsp"><font style="font-family:Tahoma" color="white">Setup, Search &amp; Browse</font></a></h2>
		  	 	<ul class="sublist">
		  	 		<li><a href="/gbrcat/Search.jsp"><font style="font-family:Tahoma;font-size: 1.5em;font-weight: bolder" color="white">Search</font></a></li>
		  	 		<li><a href="/gbrcat/Browse.jsp"><font style="font-family:Tahoma;font-size: 1.5em;font-weight: bolder" color="white">Browse</font></a></li>
		  	 		<li><a href="/gbrcat/AddCollection.jsp"><font style="font-family:Tahoma;font-size: 1.5em;font-weight: bolder" color="white">Add Collection</font></a></li>
		  	 		<li><a href="/gbrcat/AddActor.jsp"><font style="font-family:Tahoma;font-size: 1.5em;font-weight: bolder" color="white">Add Actor</font></a></li>
		  	 		<li><a href="/gbrcat/AddProject.jsp"><font style="font-family:Tahoma;font-size: 1.5em;font-weight: bolder" color="white">Add Project</font></a></li>
		  	 	</ul>
		  	</li>
		  	<li><h2><a href="/gbrcat/Upload.jsp"><font style="font-family:Tahoma" color="white">Upload Data</font></a></h2></li>
			<li><h2><a href="/cresis"><font style="font-family:Tahoma" color="white">GIS Interface</font></a></h2></li>
			<li><h2><a href="/gbrcat/Help.jsp"><font style="font-family:Tahoma" color="white">Help</font></a></h2></li>
			<%
				try{
					if(!(session.getAttribute("gbrcatLogin") == null || 
						((String) session.getAttribute("gbrcatLogin")).equalsIgnoreCase(""))){
			%><li><h2><a href="/gbrcat/Logout.jsp"><font style="font-family:Tahoma" color="white">Logout</font></a></h2></li><%
					}
				} catch (Exception e){}
			%>	
		</ul>
		</div>
		<br/>
		<br/>
		<br/>
		<a href="http://gci.uq.edu.au/">
			<img style="width: 100%" src="static/gci.gif" alt="Global Change Institute" title="Global Change Institute" />
		</a>
		<br />
		<br />
		<a href="http://www.itee.uq.edu.au/~eresearch/">
			<img style="width: 100%" src="static/uq.gif" alt="eResearch, University of Queensland" title="eResearch, University of Queensland"  />
		</a>
	</div>
	
	<div id="content">
		<div id="viewable">
		