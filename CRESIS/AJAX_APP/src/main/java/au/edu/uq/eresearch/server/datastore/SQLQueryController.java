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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.postgresql.ds.PGPoolingDataSource;


/**
 * @author Campbell Allen
 * @date   3/2/2010
 * Prototype Controller class to interface between client and the PostgreSQL Database
 */
public class SQLQueryController implements QueryController {

	//setup the class variables
	private PGPoolingDataSource source;
	private HashMap<Long,Thread> queryList;

	//tomcat context
	private String appContext = "";

	//used for logging
	private Date date;
	private SimpleDateFormat sdf;

	/**
	 *
	 */
	public SQLQueryController(String context) {
		this.appContext = context;
		
		//try and get the existing data source
		this.source = PGPoolingDataSource.getDataSource("REIfS Data Source");
		
		//check if the data source is setup
		if (this.source == null) {
			//set up the connection pooling for use with the PGSQL Database
			this.source = new PGPoolingDataSource();
			this.source.setDataSourceName("REIfS Data Source");
			this.source.setServerName("localhost");
			this.source.setDatabaseName("cresis");
			this.source.setUser("gis");
			this.source.setPassword("p0stGr3s");
			this.source.setMaxConnections(20);
			
			//I want a read-only db here!
		} 
		
		//setup my query container
		this.queryList = new HashMap<Long,Thread>();

		//setup my logging
		this.sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
		this.date = new Date();

//		try {
//			connectRepository();
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	/**
	 * Connect to the predefined repository
	 * @return boolean indicating indicating if the connection failed.
	 */
	public void connectRepository() throws RuntimeException {
		
		//will this be a stub or can we use this here?
		//RDF Stores need to be initialised but the PG store doesn't
	}


	public long threadedLocationQuery(String queryString, Boolean noCache) throws SQLException {
		
		//test the query before execution - this way we can return an error.
		Connection conn = null;
		try {
		    conn = this.source.getConnection();
		    //make sure we set this as read only!
		    conn.setReadOnly(true);
		    
		    //get a statement to execute our query!
		    Statement st = null;
		    st = conn.createStatement();
		    //run the query but set LIMIT to 0 
		    //this shoudl cause the query to not execute? But this may not be correct.
	    	st.executeQuery(queryString.replaceAll(";$", " LIMIT 0;"));
	    	st.close();
	    	
		} catch (SQLException se) {
			System.out.println("Invalid SQL query!");
			se.printStackTrace();
			if (conn != null) {
				//NOTE: Make sure that the conn is closed or the pool will leak and lock everyone out!
				try { conn.close(); } catch (SQLException e) {}
		    }
			throw se;
		} finally {
			if (conn != null) {
		    	//NOTE: Make sure that the conn is closed or the pool will leak and lock everyone out!
				 try { conn.close(); } catch (SQLException e) {}
		    }
		}
		
		//get a new threaded Query object and start it...
		Thread query = (new Thread(new SQLThreadedQuery(this.source, queryString, noCache, this.appContext)));
//
		query.start();
		//add the thread into our array list...
		queryList.put(query.getId(), query);
		//need to test my queryList size and so on..

		this.date.setTime(System.currentTimeMillis());
		System.out.println(sdf.format(date) + " " + this.toString() + " : Query queue size: " + queryList.size());
		return query.getId();
	}

	public boolean isQueryFinished(long threadID) {

//		this.date.setTime(System.currentTimeMillis());
//		System.out.println(sdf.format(date) + " " + this.toString() + " : what is the thread states...mostl likely alive...but we will see: " + queryList.get(threadID).getState().toString());

		//TODO: So if we don't finsih properly we will have to find a way to test if we finished properly...or not...i guess checking the size of the output file -> but what about empty files?
		//not the best but by virtue of a XSLT processing legacy (it leaves blank lines in the file if it parses it). the file size is non-zero...could use this?

//		if (queryList.get(threadID) != null) {
			//need to test if the query is still alive...
			if (queryList.get(threadID).isAlive()) {
	//			System.out.println(date.toString() + " " + this.toString() + " : thread is still alive, " + threadID);
	//			System.out.println(date.toString() + " " + this.toString() + " : queryList contains: " + queryList.toString() + " :: "+queryList.keySet().toString());
	//			System.out.println(date.toString() + " " + this.toString() + " : " + queryList.size());
	//			//query still running...
				return false;
			} else {
	//			System.out.println(date.toString() + " " + this.toString() + " : removing thread from the list" + threadID);
	//			System.out.println(date.toString() + " " + this.toString() + " : queryList contains: " + queryList.toString() + " :: "+queryList.keySet().toString());
	//			System.out.println(date.toString() + " " + this.toString() + " : " + queryList.size());
				//thread has finished so remove it from the Hash
				queryList.remove(threadID);
				return true;
			}
//		}
		//return false as it seems the thread may have died before it could finish proerly...

	}


	/**
	 * Get the translated result set data from file and return it to the client
	 * @param  String queryString, used to ID the correct result set
	 * @param  Boolean noCache, used to build the correct data model of each location marker.
	 * @return String[][] containing the locations and the associated data
	 */
	public String[][] getQueryResult(String queryString, Boolean noCache) throws RuntimeException {

		//create a cache code
		String cacheCode = Integer.toString(queryString.hashCode());

		//get the corect location result set file -> Note: Caching is very simple, this may need to be extended!
		String resultFile = this.appContext + "cresis/temp/Results/Location_Results_" + cacheCode + ".txt";

		//declare the String[][] that will be used to hold the information
		String[][] locationData = new String[][] { new String[]{"null", "null", "null", "null", "null"} };

		//parse the converted XML file and return the location marker data
		BufferedReader inFile;

		//setup the file reader
		try {
			inFile = new BufferedReader(new FileReader(resultFile));

			String line = "";
			int lineNumber = 0;

			try {

				//read in each line till EOF
				while ((line = inFile.readLine()) != null) {

					//clean up any whitespace
					line = line.trim();

					//get rid of empty line markers
					if (line.length() == 0 || line == "") {
						//skip to the next line
						continue;
					}

					//split out the actual column values
					String[] lineData = line.split(",", 0);
					//each line should look like -> location name, long, lat, numSightings

					//test if we get some data back
					if (lineData.length > 0) {

						//resize the array to make sure i don't head out of bounds!
						locationData = (String[][])resizeArray(locationData,lineNumber+1);

						//so store the captured data in the array -> Adding the filename cache and if we should ignore caching
						locationData[lineNumber++] = new String[]{ lineData[0], lineData[1], lineData[2], lineData[3], cacheCode, noCache.toString() };

					}
				}

				//close the reader
				inFile.close();

			} catch (IOException e) {
				this.date.setTime(System.currentTimeMillis());
				System.out.println(sdf.format(date) + " " + this.toString() + "  :: QueryController->getQueryResult() Can't read from the result file.");
				throw new RuntimeException(e);
			}
		} catch (FileNotFoundException e) {
			//notify the user that i couldn't open the input file
			this.date.setTime(System.currentTimeMillis());
			System.out.println(sdf.format(date) + " " + this.toString() + " :: QueryController->getQueryResult() Can't open the result file to extract the location data " + e.getLocalizedMessage());
	    }

		//return the new location Data.
		return locationData;
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
