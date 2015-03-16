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

/**
 *
 */
package au.edu.uq.eresearch.server.datastore;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.postgresql.ds.PGPoolingDataSource;

import au.edu.uq.eresearch.server.util.Parser;
import au.edu.uq.eresearch.server.util.XMLFileWriter;

/**
 * @author	Campbell Allen
 * @date	24/5/09
 * Threaded Query Execution class
 * This class will create execute a query against and remote RDF store using SPARQL query
 * This design allows us to run a query in the background and not block at the server therefore if executing
 * a large query, we won't run into browser timeout issues.
 *
 */
public class SQLThreadedQuery implements Runnable {

	//class variables
//	private Date today;

	//setup a date and formatter
	private SimpleDateFormat sdf;
	private Date date;  // uses current system time to create a date by default

	private PGPoolingDataSource dbSource;
	private Parser parser;
	private String queryString;
	private Boolean noCache;
	private String appContext;
	
	private XMLFileWriter writer;

	/*
	 * Constructor,  pass in all the pieces we need to query and convert the result set.
	 */
	public SQLThreadedQuery(PGPoolingDataSource source, String queryString, Boolean noCache, String context) {
		//copy over my class setup
		this.dbSource = source;
		this.queryString = queryString;
		this.noCache = noCache;
		this.appContext = context;
		this.sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
		this.date = new Date();
	}

	/*
	 * Execute the query using the threaded run method.
	 *
	 */
	public void run() {

//		this.date.setTime(System.currentTimeMillis());
//		System.out.println(sdf.format(date) + " " + Thread.currentThread().getName() + " : Started my thread to query the server..." + Thread.currentThread().getId());

		//create a cache code
		String cacheCode = Integer.toString(queryString.hashCode());

		//get the current working directory -> Note: Caching is very simple, this may need to be extended!
		String xmlResultFile = appContext + "cresis/temp/Results/SQL_Results_" + cacheCode + ".xml";
		String resultFile = appContext + "cresis/temp/Results/Location_Results_" + cacheCode + ".txt";
		//a file to hold the unique variables for the SQL query
		String sqlVariablesFile = appContext + "cresis/temp/Results/SQL_Variables_" + cacheCode + ".xml";

		//declare the String[][] that will be used to hold the information
//		String[][] locationData = new String[][] { new String[]{"null", "null", "null", "null", "null"} };
//			String[][] locationData = new String[][] { new String[]{"", "", "", "", ""} };

		//test if we need to make the dir for the file
		File folderPath = new File (appContext + "cresis/temp/Results/");
		if (! folderPath.exists()){
			try {
				folderPath.mkdirs();
			} catch (Exception e) {
				this.date.setTime(System.currentTimeMillis());
				System.out.println(sdf.format(date) + " " + this.toString() + " : " + e.getMessage());
			}
		}
		//test if we need to make the dir for the file
		folderPath = new File (appContext + "cresis/temp/XSLT/");
		if (! folderPath.exists()){
			try {
				folderPath.mkdirs();
			} catch (Exception e) {
				this.date.setTime(System.currentTimeMillis());
				System.out.println(sdf.format(date) + " " + this.toString() + " : " + e.getMessage());
			}
		}

		//test if the file exists -> use the cached version (hashCode should be unique).
		File cachedResultFile = new File(xmlResultFile);
		File cachedLocationFile = new File(resultFile);

		try {

			//TODO: WHAT ABOUT IF WE LAUNCH 2 Threads at the same time for the same query..
			//if the result file is 0KB then both threads will launch...not good.
			//How to test if i have a thread running? hmm?  Cant use server as all connections have separate one...or do they?
			//Technically there is only 1 server so each request should go through the same server.

			//see if we should use the cached file
			if (!cachedResultFile.exists() || cachedResultFile.length() == 0 || noCache) {
				
				//use a connection from the pool.
				//Note that it is critical that the connections are eventually closed. Else the pool will “leak” connections and will eventually lock all the clients out.
				Connection conn = null;
				try {
				    conn = this.dbSource.getConnection();
				    
				    //make sure we set this as read only!
				    conn.setReadOnly(true);

				    //get a statement to execute our query!
				    Statement st = null;
				    try {
				      st = conn.createStatement();
				    } catch (SQLException se) {
				      System.out.println("We got an exception while creating a statement:" +
				                         "that probably means we're no longer connected.");
				      se.printStackTrace();
				    }
				    
				    ResultSet rs = null;
				    try {
				    	//run the query fromt the client
				    	rs = st.executeQuery(this.queryString);
				    	
				    } catch (SQLException se) {
				      System.out.println("We got an exception while executing our query:" +
				                         "that probably means our SQL is invalid");
				      se.printStackTrace();
				    }
				    
				    //get my XML writer
				    this.writer = new XMLFileWriter(xmlResultFile);
				    this.writer.startOutput();
				    
				    //output the results
		    		this.writer.startElement("results");
				    
				    //setup vars to hold my lat and lon of the observation
				    //need these for the to locate my observation in the XSLT transform
				    String lat = "";
				    String lon = "";
				    String lat_end = "";
				    String lon_end = "";
				    
				    //setup a ArrayList to hold my SQL variables.
				    Set<String> sqlVars = new LinkedHashSet<String>();
				    
				    //now i have to output he result set as an XML doc....
				    try {
				    	//loop over the rows of the result set!
				    	while (rs.next()) {
				    		
				    		//output each row as a result
				    		this.writer.startElement("result");
				    		
				    		//now i loop over each of the result sets and output the data -> yay!
				    		for (int i=1; i <= rs.getMetaData().getColumnCount(); i++) {
				    			
//			    				//Debug:
//					    		System.out.println("Name of the column in the result set: " + rs.getMetaData().getColumnName(i));
//					    		System.out.println("Value of the column in the result set: " + rs.getString(i));				    				
				    			
				    			String value = "";
				    			//check for null values in the result set 
				    			if (rs.getString(i) != null) {
				    				value = rs.getString(i);
				    			} else {
				    				//skip to the next record
				    				continue;
				    			}
				    			
				    			//check if we have a certain element (namely we need to handle the "location" geometry) 
				    			if (rs.getMetaData().getColumnName(i).equalsIgnoreCase("location")) {
				    				
				    				//reset the lat / lon vars in case they are carrying over
				    				lat = "";
				    				lon = "";
				    				lat_end = "";
				    				lon_end = "";
				    				
				    				//then we need to split the data!
				    				if (rs.getString(i).startsWith("POINT")) {
				    					
				    					//handle the point case...simple here just extract the lat / lon
				    					//"POINT(-23.4422 151.9106)"
				    					//setup regular expression for the start of the SPARQL query expression
				    					Pattern point = Pattern.compile("POINT\\((.+)\\s(.+)\\)");
				    					//match test for the reg. expressions
				    					Matcher pointMatcher;
				    					
				    					//setup the reg. expressions matchers with the input line
				    					pointMatcher = point.matcher(value);
				    					
				    					//test the regular expression for the beginning of the variable declarations
				    					if (pointMatcher.find()) {
				    						lat = pointMatcher.group(1);
				    						lon = pointMatcher.group(2);
				    					}
				    					
				    				} else {
				    					//this one is trickier -> polygon is a bounding box...starting at N/W and finishing at N/W!
//				    					E.G. "POLYGON((-21.5 149.5,-25.5 149.5,-25.5 154,-21.5 154,-21.5 149.5))"
				    					//setup regular expression for the start of the SPARQL query expression
				    					Pattern polygon = Pattern.compile("^POLYGON\\(\\((.+)\\s(.+),(.+)\\s.+,.+\\s(.+),.+,.+\\)\\)$");				    												   
				    					//match test for the reg. expressions
				    					Matcher polygonMatcher;
				    					//setup the reg. expressions matchers with the input line
				    					polygonMatcher = polygon.matcher(value);
				    					
				    					//test the regular expression for the beginning of the variable declarations
				    					if (polygonMatcher.find()) {
				    						lat = polygonMatcher.group(1);
				    						lon = polygonMatcher.group(2);
				    						lat_end = polygonMatcher.group(3);
				    						lon_end = polygonMatcher.group(4);
				    					}
				    				}
				    				
				    				//output my lat and lon instead of the location var
				    				this.writer.writeDataElement("lat", lat);
				    				this.writer.writeDataElement("lon", lon);
				    				//add my lat and lon to the sqlVars set
				    				sqlVars.add("lat");
				    				sqlVars.add("lon");
				    				
				    				//test if the lat/lon end values are set
				    				if (!lon_end.equalsIgnoreCase("")) {
				    					//ad them if they are non empty
				    					this.writer.writeDataElement("lat_end", lat_end);
					    				this.writer.writeDataElement("lon_end", lon_end);
					    				//add them to the var list as well.
					    				sqlVars.add("lat_end");
					    				sqlVars.add("lon_end");
				    				}				    				
				    			} 
				    			else {
				    				//now output a the string values of the columns!
					    			this.writer.writeDataElement(rs.getMetaData().getColumnName(i), value);
					    			sqlVars.add(rs.getMetaData().getColumnName(i));
				    			}				    			
				    		}
				    		
				    		//Make sure these match the startElement as above! 
				    		this.writer.endElement("result");	    	  
				    	  
				    	}
				    	
				    	//close the results 
			    		this.writer.endElement("results");
				    	
				    	//now finish our document;
				    	this.writer.finishOutput();
				    	
				    	//Use the existing XML writer to save the unique query variables for use in the data grid
				    	this.writer.newOutputFile(sqlVariablesFile);
					    this.writer.startOutput();
					    
					    //setup the variables tag
			    		this.writer.startElement("variables");
					    
					    //now output the variables into the XML file for save using
			    		for (String variable : sqlVars) {
			    			//output all the uniq variables that we used.
			    			this.writer.writeDataElement("variable", variable);
			    		}
			    		//close the tag
			    		this.writer.endElement("variables");
			    		
			    		//finish the doc.
			    		this.writer.finishOutput();
				    	
			    	} catch (SQLException se) {
				      System.out.println("We got an exception while navigating the result set : this " +
				                         "shouldn't happen: we've done something really bad.");
				      se.printStackTrace();
				    } finally {
				    	
				    	//clean up my result set and statements!
				    	rs.close();
				    	st.close();
				    }
				    
				} catch (SQLException e) {
				    // log error
					e.printStackTrace();
				} finally {
				    if (conn != null) {
				    	//NOTE: Make sure that the conn is closed or the pool will leak and lock everyone out!
				        try { conn.close(); } catch (SQLException e) {}
				    }
				}
			}

			//test if the query returned a file -> or it was cached..
			if (!cachedLocationFile.exists() || cachedLocationFile.length() == 0 || noCache) {
				
				//setup my parseras i will need this to process the query resutls
				this.parser = new Parser(this.appContext, "cresis/Resources/XSL_Templates/SQL_location_result_data.xsl");

				//now translate the SPARQL Result set to location data
				if (! this.parser.translate(xmlResultFile, resultFile)) {

					//notify the user we failed to parse the result set
					this.date.setTime(System.currentTimeMillis());
					System.out.println(sdf.format(date) + " " + this.toString() + " :: ThreadedQuery->run() Failed to Parse the SQL XML Result Set : check the XSLT or XML Result files.");

				}
			}
		}
		catch (Exception e){
			this.date.setTime(System.currentTimeMillis());
			System.out.println(sdf.format(date) + " " + this.toString() + " : " + e.getLocalizedMessage());
			System.out.println(sdf.format(date) + " " + Thread.currentThread().getName() + " : Thread execution finished with an error.");
		}
		finally {
			//if i got here then we finished normally
			this.date.setTime(System.currentTimeMillis());
			System.out.println(sdf.format(date) + " " + Thread.currentThread().getName() + " : Finished the thread execution...");
		}
    }
}
