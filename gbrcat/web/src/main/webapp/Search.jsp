<%@ page import="org.apache.commons.fileupload.FileItem" %>
<%@ page import="org.apache.commons.fileupload.FileItemFactory" %>

<%@ page import="org.apache.commons.fileupload.FileUploadException" %>
<%@ page import="org.apache.commons.fileupload.disk.DiskFileItemFactory" %>
<%@ page import="org.apache.commons.fileupload.servlet.ServletFileUpload" %>
<%@ page import="javax.persistence.EntityManager" %>
<%@ page import="javax.persistence.EntityManagerFactory" %>
<%@ page import="javax.persistence.Persistence" %>
<%@ page import="javax.persistence.Query" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.List" %>

<jsp:include page="/Header.jsp"/>
</head>
<body>
<jsp:include page="/Body.jsp"/>
    <%
		String searchField = "";
		boolean foundString = false;
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
	 	if (isMultipart) {
	   		FileItemFactory factory = new DiskFileItemFactory();
	   		ServletFileUpload upload = new ServletFileUpload(factory);
	   		List items = null;
	   		try {
	   			items = upload.parseRequest(request);
	   		} catch (FileUploadException err) {
	   			err.printStackTrace();
	   		}
	   		Iterator itr = items.iterator();
	   		while (itr.hasNext()) {
	   			FileItem item = (FileItem) itr.next();
	   			if (item.isFormField()){
					String name = item.getFieldName();
					String value = item.getString();
					if(name.startsWith("searchField")){
						if(value != null && !"".equals(value)){
							searchField = value.trim();
							foundString = true;
						}
					}
	   		   }
	   		}
	   	}			 
	%>
<h4>Search collections</h4>

<form METHOD=POST ACTION="Search.jsp" enctype="multipart/form-data">
    Keyword: <input type="text" name="searchField" size="100" value="<%= searchField %>"/>
    <input class="button" type="submit"/>
</form>
<br/>
<br/>

<div id="res">
    <%
        if (foundString) {
            String term = searchField.toLowerCase();
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("gisdb");
            EntityManager em = emf.createEntityManager();
            ArrayList<Integer> collections = new ArrayList<Integer>();

            Query query = em.createQuery("SELECT cs FROM Collection_subject cs");
            List<au.edu.uq.eresearch.Collection_subject> collectionSubjects = (List<au.edu.uq.eresearch.Collection_subject>) query.getResultList();

            for (au.edu.uq.eresearch.Collection_subject cs : collectionSubjects) {
                query = em.createQuery("SELECT s FROM Subject s WHERE id = " + cs.getSubjects_id());
                List<au.edu.uq.eresearch.Subject> subject = (List<au.edu.uq.eresearch.Subject>) query.getResultList();

                for (au.edu.uq.eresearch.Subject s : subject) {
                    String searchedValue = s.getValue().toLowerCase();
                    if (searchedValue.contains(term) || term.contains(searchedValue)) {
                        if (!collections.contains(cs.getCollection_collectionid())) {
                            collections.add(cs.getCollection_collectionid());
                        }
                    }
                }
            }

            query = em.createQuery("SELECT c FROM Collection c");
            List<au.edu.uq.eresearch.Collection> allCollections = (List<au.edu.uq.eresearch.Collection>) query.getResultList();

            for (au.edu.uq.eresearch.Collection c : allCollections) {
                if (c.getTitle().toLowerCase().contains(term) || c.getDescription().toLowerCase().contains(term)) {
                    if (!collections.contains(c.getCollectionid())) {
                        collections.add(c.getCollectionid());
                    }
                }
            }

            for (int id : collections) {
                query = em.createQuery("SELECT c FROM Collection c WHERE collectionid = " + id);
                List<au.edu.uq.eresearch.Collection> collectionData = (List<au.edu.uq.eresearch.Collection>) query.getResultList();
                for (au.edu.uq.eresearch.Collection col : collectionData) {
    %>
    <div class="search-result">
						<span class="result-title">
		 						<a href="/gbrcat/ViewCollection.jsp?id=<%= col.getCollectionid() %>"><%= col.getTitle() %>
                                 </a>
		 					</span>
        <br>
        <%= col.getDescription() %>
        <br>

        <div class="search-detail">
            <span class="search-detail-url">URL</span>: <a
                href="/gbrcat/ViewCollection.jsp?id=<%= col.getCollectionid() %>">/gbrcat/ViewCollection.jsp?id=<%= col.getCollectionid() %>
        </a>
            <br>
            <span class="search-detail-creator">Created by</span><%
            query = em.createQuery("SELECT a FROM Person a WHERE actorid = " + col.getOwner_actorid());
            List<au.edu.uq.eresearch.Person> actorData = (List<au.edu.uq.eresearch.Person>) query.getResultList();
            au.edu.uq.eresearch.Person act = actorData.get(0);
        %><%= ": " + act.toString() %>
        </div>
    </div>
    <br/>
    <%
            }
        }
        em.close();
        emf.close();
    } else {
    %>Example Search: "coral", "Great Barrier Reef","Heron Island"<%
    }
%>
</div>

<jsp:include page="Footer.jsp"/> 