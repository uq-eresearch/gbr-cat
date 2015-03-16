/*
 * Copyright (c) 2011, The University of Queensland
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of The University of Queensland nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE UNIVERSITY OF QUEENSLAND BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 */

package au.edu.uq.eresearch.server.datastore;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author	Campbell Allen
 * @date	18/7/2008
 *
 * Utility class to parse a SPARQL queries file and create a model of each query for use by the UI Grid component.
 * @deprecated XML Query Loader replaced this manual parser.
 */
public class QueryModel {

	//default store for the query model
	private String[][] queryStore;

	//counter for the object array store
	private int queryCounter;

	//context string for the XML query file
	private String context;

	//input reader
//	private BufferedReader inFile;

	//used for logging
	private Date date;
	private SimpleDateFormat sdf;

	//HashMap for the query variables
	private HashMap<String, String> queryNodeVars;
//	private HashMap<Integer, String> querNodeSortOrder;

	/**
	 * Default constructor
	 */
	public QueryModel(String context) {

		//setup my logging
		this.sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
		this.date = new Date();

		//setup the hash map of query node vars.
		this.queryNodeVars = setupQueryNodeMap();

		//setup the queryStore...which will house the queries.
		this.queryStore = new String[][] {};
		this.queryCounter = 0;
		this.context = context;
		this.date.setTime(System.currentTimeMillis());
		System.out.println(sdf.format(date) + " " + this.toString() + " :: Constructed the QueryModel!");
	}

	/**
	 * Method to parse the XML Query file to extract the query variables.
	 */
	protected void parseXML() throws Exception {

		//only setup the query store if it's empty
		if (this.queryCounter == 0) {

			//read the XML file. -> Queries.xml!
			try {

		        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		        Document doc = docBuilder.parse(new File(this.context + "cresis/Resources/Configuration_Files/Queries.xml"));

		        // normalize text representation
		        doc.getDocumentElement().normalize();

		        //get a list of nodes
		        NodeList queryNodeList = doc.getElementsByTagName("query");
		        int totalQueries = queryNodeList.getLength();
//		        System.out.println("The total number of queries in the XML file : " + totalQueries);

		        //loop over each of the nodes
		        for(int i=0; i < totalQueries ; i++){

		        	//get each node and the values associated
		            Node queryNode = queryNodeList.item(i);
		            //only process if i am a node
		            if(queryNode.getNodeType() == Node.ELEMENT_NODE){

		            	//process each element
		                Element queryElement = (Element)queryNode;

		                //Generic pattern to load each variable into a hashmap with a key

		                //loop over all my keys in the node HashMap and get the associated values
		                //TODO: can clean this up to include multivalues for each node...this would allow generic length Query Union's
		                //For a rainy day.
		                for (String key : this.queryNodeVars.keySet()){

			                //get the node list of these values
			                NodeList nodeList = queryElement.getElementsByTagName(key);
			                //only 1 of each variable for each query so index with 0 here.
			                Element element = (Element)nodeList.item(0);
			                //now we have a nodeList of all north tags
			                NodeList nodeValueList = element.getChildNodes();
			                this.queryNodeVars.put(key,((Node)nodeValueList.item(0)).getNodeValue().trim());

		                }

		                //resize the array to make sure i don't head out of bounds!
						this.queryStore = (String[][])resizeArray(this.queryStore,this.queryCounter+1);

		            }
		        }

			}catch (SAXParseException err) {
				this.date.setTime(System.currentTimeMillis());
				System.out.println(sdf.format(date) + " " + this.toString() + " :: QueryModel->ParseXML() : Parsing error" + ", line " + err.getLineNumber () + ", uri " + err.getSystemId () + " " + err.getMessage());
			    throw err;
		    }catch (SAXException e) {
			    Exception x = e.getException ();
			    ((x == null) ? e : x).printStackTrace ();
			    throw e;
		    }catch (Throwable t) {
		    	t.printStackTrace();
		    	//throw t;
		    }

		}
	}

	/*
	 * Method to setup all the nodes we want to capture in the query XML file
	 */
	private HashMap<String,String> setupQueryNodeMap() {

		//create the node map
		HashMap<String,String> nodeMap = new HashMap<String,String>();

		//setup all the nodes we know about in the query XML file
		nodeMap.put("north", "");
		nodeMap.put("south", "");
		nodeMap.put("east", "");
		nodeMap.put("west", "");
		nodeMap.put("lat", "");
		nodeMap.put("lon", "");
		nodeMap.put("begin_date", "");
		nodeMap.put("end_date", "");
		nodeMap.put("target", "");
		nodeMap.put("characteristic", "");
		nodeMap.put("location", "");
		nodeMap.put("actor", "");
		nodeMap.put("measurement_value", "");
		nodeMap.put("unit", "");
		nodeMap.put("research_program", "");
		nodeMap.put("ecological_process", "");
		nodeMap.put("process_output", "");
		nodeMap.put("genus", "");
		nodeMap.put("species", "");
		nodeMap.put("morphology", "");
		nodeMap.put("sensor", "");
		nodeMap.put("description", "");
		nodeMap.put("north1", "");
		nodeMap.put("south1", "");
		nodeMap.put("east1", "");
		nodeMap.put("west1", "");
		nodeMap.put("lat1", "");
		nodeMap.put("lon1", "");
		nodeMap.put("begin_date1", "");
		nodeMap.put("end_date1", "");
		nodeMap.put("target1", "");
		nodeMap.put("characteristic1", "");
		nodeMap.put("location1", "");
		nodeMap.put("actor1", "");
		nodeMap.put("measurement_value1", "");
		nodeMap.put("unit1", "");
		nodeMap.put("research_program1", "");
		nodeMap.put("ecological_process1", "");
		nodeMap.put("process_output1", "");
		nodeMap.put("genus1", "");
		nodeMap.put("species1", "");
		nodeMap.put("morphology1", "");
		nodeMap.put("sensor1", "");
		nodeMap.put("query_string", "");

		//now setup the sorting order of these as it is important that these are returned in order for the grid panel at the client

		//return the setup map.
		return nodeMap;
	}

//	/**
//	 * Method to parse the contents of the SPARQL queries file and separate out individual queries and associated attributes
//	 * Query Delimiters:
//	 * 		BEGIN QUERY		=	<-- Title :: Description -->
//	 * 		QUERY CONTENTS	=	 everything in between
//	 * 		END QUERY		=	<-- END -->
//	 */
//	protected void parseContents() throws Exception {
//
//		//only setup the query store if it's empty
//		if (queryCounter == 0) {
//
//			//string builder to store the read file contents
//			StringBuilder queryString = new StringBuilder();
//
//			//used to read each line
//			String line = "";
//			String name = "";
//			String description = "";
//			String type = "";
//
//			//setup regular expression for the start of the SPARQL query expression
//			Pattern queryStart = Pattern.compile("<-- (.+) : (.+) : (.+) -->");
//			//setup regular expression for the end of the SPARQL query expression
//			Pattern queryEnd = Pattern.compile("<-- END -->");
//			//match test for the reg. expressions
//			Matcher matcher;
//
//			try {
//
//				//read in each line till EOF
//				while ((line = inFile.readLine()) != null) {
//
//					//setup the reg. expressions matcher
//					matcher = queryStart.matcher(line);
//
//					//test the reg. expression for the beginning of a query
//					if (matcher.find()) {
//						//i have got the match so extract the name and description fields
//						name = matcher.group(1);
//						//System.out.println(name);
//						description = matcher.group(2);
//						//System.out.println(description);
//						type = matcher.group(3);
//						//System.out.println(type);
//						//skip to next line
//						continue;
//					}
//
//					//we made it past the start of query marker, now look for the end of query
//					matcher = queryEnd.matcher(line);
//					if (!matcher.find()) {
//						//if we haven't found the end of query marker
//						//then we have a query string line here.
//						queryString.append(line+"\n");
//						//skip to the next line
//						continue;
//					}
//
//					//resize the array to make sure i don't head out of bounds!
//					queryStore = (String[][])resizeArray(queryStore,queryCounter+1);
//
//					//if i get here then i have found the end of query marker
//					//so store the captured query data in the object array
//					queryStore[queryCounter++] = new String[]{name, description, type, queryString.toString()};
//
//					//reset the storage strings
//					name = "";
//					description = "";
//					//clear my string builder
//					queryString.delete(0, queryString.length());
//				}
//
//				//close the reader
//				inFile.close();
//
//			} catch (IOException e) {
//				this.date.setTime(System.currentTimeMillis());
//				System.out.println(sdf.format(date)  + " " + this.toString() + " :: error parsing SPARQL file -> " + e.getLocalizedMessage());
//				queryCounter = 0;
//				throw e;
//			}
//		}
//
//		this.date.setTime(System.currentTimeMillis());
//		System.out.println(sdf.format(date) + " " + this.toString() + " :: Parsed the querymodel...queryCounter: " + String.valueOf(queryCounter));
//		//completed successfully
//	}

	/**
	 * Getter for the queryModel
	 * @return Object[][] of query model = an array of strings [name, description, queryString].
	 */
	//Get the query model
	public String[][] getQueryModel() throws Exception{
		if (queryCounter == 0) {
			//return an empty set that won't load anything.
			return new String[][] { new String[]{""} };
		}
		return queryStore;
	}

	/**
	 * Setter for the queryModel
	 * @param objArray Array which represents the query model.
	 */
	//Set the query model
	public void SetQueryModel(String[][] objArray) {
		queryStore = objArray;
	}

	/**
	 * Return the number of queries in the object array
	 * @return number of queries
	 */
	protected int GetQueryCount() {
		return queryCounter;
	}

	/**
	* NOTE: Code below is not the authors, please see reference: http://www.source-code.biz/snippets/java/3.htm
	*
	* Reallocates an array with a new size, and copies the contents
	* of the old array to the new array.
	* @param oldArray  the old array, to be reallocated.
	* @param newSize   the new array size.
	* @return          A new array with the same contents.
	*/
	private static Object resizeArray (Object oldArray, int newSize) {
	   int oldSize = java.lang.reflect.Array.getLength(oldArray);
	   Class<?> elementType = oldArray.getClass().getComponentType();
	   Object newArray = java.lang.reflect.Array.newInstance(elementType,newSize);
	   int preserveLength = Math.min(oldSize,newSize);
	   if (preserveLength > 0)
	      System.arraycopy (oldArray,0,newArray,0,preserveLength);
	   return newArray;
	}

}
