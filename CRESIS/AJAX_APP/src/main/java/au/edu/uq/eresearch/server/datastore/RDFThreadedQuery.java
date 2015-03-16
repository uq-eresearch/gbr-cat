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
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.resultio.sparqlxml.SPARQLResultsXMLWriter;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.http.HTTPRepository;

import au.edu.uq.eresearch.server.util.Parser;

/**
 * @author	Campbell Allen
 * @date	24/5/09
 * Threaded Query Execution class
 * This class will create execute a query against and remote RDF store using SPARQL query
 * This design allows us to run a query in the background and not block at the server therefore if executing
 * a large query, we won't run into browser timeout issues.
 *
 */
public class RDFThreadedQuery implements Runnable {

	//class variables
//	private Date today;

	//setup a date and formatter
	private SimpleDateFormat sdf;
	private Date date;  // uses current system time to create a date by default

	private HTTPRepository repository;
	private Parser parser;
	private String queryString;
	private Boolean noCache;
	private String appContext;

	/*
	 * Constructor,  pass in all the pieces we need to query and convert the result set.
	 */
	public RDFThreadedQuery(HTTPRepository httpRepository, String queryString, Boolean noCache, String context) {
		//copy over my class setup
		this.repository = httpRepository;
		this.queryString = queryString;
		this.noCache = noCache;
		this.appContext = context;
		this.parser = new Parser(this.appContext, "cresis/Resources/XSL_Templates/location_result_data.xsl");
//		this.parser = new Parser(this.appContext, "cresis/Resources/XSL_Templates/Test_location_result_data.xsl");
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
		String xmlResultFile = appContext + "cresis/temp/Results/SPARQL_Results_" + cacheCode + ".xml";
		String resultFile = appContext + "cresis/temp/Results/Location_Results_" + cacheCode + ".txt";

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
			//THINK ABOUT IT....!

			//see if we should use the cached file
			if (!cachedResultFile.exists() || cachedResultFile.length() == 0 || noCache) {

				try {
					FileOutputStream fout = new FileOutputStream(xmlResultFile);
					SPARQLResultsXMLWriter sparqlWriter = new SPARQLResultsXMLWriter(fout);

					RepositoryConnection con = repository.getConnection();
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
			if (!cachedLocationFile.exists() || cachedLocationFile.length() == 0 || noCache) {

				//now translate the SPARQL Result set to KML
				if (! parser.translate(xmlResultFile, resultFile)) {

					//notify the user we failed to parse the result set
					this.date.setTime(System.currentTimeMillis());
					System.out.println(sdf.format(date) + " " + this.toString() + " :: ThreadedQuery->run() Failed to Parse the SPARQL XML Result Set : check the XSLT or XML Result files.");

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
