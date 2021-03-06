<%@ page import="org.apache.commons.fileupload.servlet.ServletFileUpload" %>
<%@ page import="org.apache.commons.fileupload.disk.DiskFileItemFactory" %>
<%@ page import="org.apache.commons.fileupload.*" %>
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
</head>
<body>
<jsp:include page="/Body.jsp"/>
<%
	String Username="";
	String Password="";

	boolean isMultipart = ServletFileUpload.isMultipartContent(request);
	if (isMultipart) {
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		List items = null;
		try {
			items = upload.parseRequest(request);
		} catch (FileUploadException e) {
			e.printStackTrace();
		}
		Iterator itr = items.iterator();
		while (itr.hasNext()) {
			FileItem item = (FileItem) itr.next();
			if (item.isFormField()){
				String name = item.getFieldName();
				String value = item.getString();
				
				if(name.equals("j_username")){
					Username = value;
				} 	
				if(name.equals("j_password")){
					Password = value;
				} 		  
		   	}
		}
	}
	
	if(Username.equalsIgnoreCase("Guest") && Password.equalsIgnoreCase("GBRCAT135")){
		session.setAttribute("gbrcatLogin","true");
		response.sendRedirect("/gbrcat/Overview.jsp");
	} else {
		response.sendRedirect("/gbrcat/ProperLogin.jsp");
	}
%>
<jsp:include page="Footer.jsp"/> 