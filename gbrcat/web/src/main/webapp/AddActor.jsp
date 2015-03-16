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
<form method="POST" action="/gbrcat/SaveActor.jsp" enctype="multipart/form-data">
<h4>Add new actor</h4>
<h3>Required fields</h3>
<table class="collection">
	<tr>
		<th>Your Name:</th>
		<td>
			First: 
			<input type="text" name="firstname" style="width:40%"/> 
			Last: 
			<input type="text" name="lastname" style="width:40%"/>
		</td>
	</tr>
	<tr>
		<th>Description:</th>
		<td>
			<textarea cols="55" rows="10" name="description" style="width:95%"></textarea>
		</td>
	</tr>
	<tr>
		<th>Organisation or Department:</th>
		<td>
			<input type="text" class="long" name="organisation" style="width:95%" />
		</td>
	</tr>
	<tr>
		<th>Email:</th>
		<td>
			<input type="text" class="long" name="email" style="width:95%"/>
		</td>
	</tr>
	<tr>
		<th>Web address:</th>
		<td>
			<input type="text" class="long" name="webaddress" style="width:95%"/>
		</td>
	</tr>


</table>
<p>
	<input class="button" type="submit" value="Save" />
	<input class="button" type="reset" value="Cancel" onclick="window.location='/gbrcat/Overview.jsp'" /></p>
</form>
<jsp:include page="/Footer.jsp"/> 