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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.resultio.sparqlxml.SPARQLResultsXMLWriter;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;

import au.edu.uq.eresearch.server.util.Parser;

/**
 * @author Campbell Allen
 * @date   23/5/2008
 * Prototype Controller class to interface between client and the RDF store
 */
public class RDFQueryController implements QueryController {

	//setup the class variables
	//TODO: Should drive the repository of an XML config file!
	private static String sesameServer = "http://localhost:8080/openrdf-sesame/repositories/REIfS";
	private HTTPRepository myRepository;
	private HashMap<Long,Thread> queryList;

	//default query result xslt parser
	private Parser parser;

	//tomcat context
	private String appContext = "";

	//used for logging
	private Date date;
	private SimpleDateFormat sdf;

	/**
	 *
	 */
	public RDFQueryController(String context) {
		this.appContext = context;
		//get a bind on the repository
		this.myRepository = new HTTPRepository(sesameServer);
		//setup my XSLT parser -> use the default query location, observation aggregate XSLT file.
//		this.parser = new Parser(this.appContext, "cresis/Resources/XSL_Templates/location_result_data.xsl");

		//setup my query container
		this.queryList = new HashMap<Long,Thread>();

		//setup my logging
		this.sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
		this.date = new Date();
	}

	/**
	 * Connect to the predefined repository
	 * @return boolean indicating indicating if the connection failed.
	 */
	public void connectRepository() throws RuntimeException {

		try {
			this.myRepository.initialize();
		} catch (RepositoryException e) {
			this.date.setTime(System.currentTimeMillis());
			System.out.println(sdf.format(date) + " " + this.toString() + " : " + e.getLocalizedMessage());
			throw new RuntimeException(e);
		}
	}

	public long threadedLocationQuery(String queryString, Boolean noCache) {

		//get a new threaded Query object and start it...
		Thread query = (new Thread(new RDFThreadedQuery(this.myRepository, queryString, noCache, this.appContext)));
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
	 * Query the repository and parse the results using XSLT to group the location sightings
	 * @param queryString
	 * @param noCache
	 * @return String[][] containing the locations and the associated data
	 */
	public String[][] locationQuery(String queryString, Boolean noCache) throws Exception {

		//create a cache code
		String cacheCode = Integer.toString(queryString.hashCode());

		//get the current working directory -> Note: Caching is very simple, this may need to be extended!
		String xmlResultFile = this.appContext + "cresis/temp/Results/SPARQL_Results_" + cacheCode + ".xml";
		String resultFile = this.appContext + "cresis/temp/Results/Location_Results_" + cacheCode + ".txt";

		//declare the String[][] that will be used to hold the information
		String[][] locationData = new String[][] { new String[]{"null", "null", "null", "null", "null"} };
//		String[][] locationData = new String[][] { new String[]{"", "", "", "", ""} };

		//test if we need to make the dir for the file
		File folderPath = new File (this.appContext + "cresis/temp/Results/");
		if (! folderPath.exists()){
			try {
				folderPath.mkdirs();
			} catch (Exception e) {
				this.date.setTime(System.currentTimeMillis());
				System.out.println(sdf.format(date) + " " + this.toString() + " : " + e.getMessage());
			}
		}
		//test if we need to make the dir for the file
		folderPath = new File (this.appContext + "cresis/temp/XSLT/");
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

		//see if we should use the cached file
		if (!cachedResultFile.exists() || cachedResultFile.length() == 0 || noCache) {

			try {
				FileOutputStream fout = new FileOutputStream(xmlResultFile);
				SPARQLResultsXMLWriter sparqlWriter = new SPARQLResultsXMLWriter(fout);

				RepositoryConnection con = this.myRepository.getConnection();
				this.date.setTime(System.currentTimeMillis());
				System.out.println(sdf.format(date) + " " + this.toString() + " : Opened the Connection to the repo...");
				try {
					TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
					tupleQuery.evaluate(sparqlWriter);
				}
				catch (Exception e){
					this.date.setTime(System.currentTimeMillis());
					System.out.println(sdf.format(date) + " " + this.toString() + " : " + e.getMessage());
					throw new Exception(e.getMessage());
				}
				finally {
					con.close();
					this.date.setTime(System.currentTimeMillis());
					System.out.println(sdf.format(date) + " " + this.toString() + " : Closed the connection...");
				}
			}
			catch (Exception e){
				this.date.setTime(System.currentTimeMillis());
				System.out.println(sdf.format(date) + " " + this.toString() + " : " + e.getLocalizedMessage());
				throw new Exception(e.getMessage());
			}
		}

		//test if the query returned a file -> or it was cached..
		if ((!cachedLocationFile.exists() || cachedLocationFile.length() == 0) || noCache) {

			//now translate the SPARQL Result set to KML
			if (! this.parser.translate(xmlResultFile, resultFile)) {

				//notify the user we failed to parse the result set
				this.date.setTime(System.currentTimeMillis());
				System.out.println(sdf.format(date) + " " + this.toString() + " :: QueryController->locationQuery() Failed to Parse the SPARQL XML Result Set : check the XSLT or XML Result files.");

				//return an empty set -> failed to parse the XML file
				return locationData;
			}
		}

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
				System.out.println(sdf.format(date) + " " + this.toString() + "  :: QueryController->locationQuery() Can't read from the result file.");
				throw e;
			}
		} catch (FileNotFoundException e) {
			//notify the user that i couldn't open the input file
			this.date.setTime(System.currentTimeMillis());
			System.out.println(sdf.format(date) + " " + this.toString() + " :: QueryController->locationQuery() Can't open the result file to extract the location data " + e.getLocalizedMessage());
	    }

		//return the new location Data.
		return locationData;
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
