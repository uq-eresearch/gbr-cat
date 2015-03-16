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
package au.edu.uq.eresearch.server.datamodel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.edu.uq.eresearch.server.util.LocationXSLTemplate;
import au.edu.uq.eresearch.server.util.Parser;


/**
 * @author Campbell Allen
 * @date   27/7/08
 * Utility class to parse the Sparql xml result set and create a model of the location data for use in the UI data component.
 */
public class SQLLocationDataModel implements LocationDataModel {

	//default query data extraction parser
	private Parser dataParser;

	//tomcat context
	private String appContext;

	//hash map of query variables
	private HashMap<String,String> queryVariables;

	//code used as a file suffix for caching
	private String resultFileCacheCode;
	private String locationXSLTFileCacheCode;
	private Boolean noCache;

	//used for logging
	private Date date;
	private SimpleDateFormat sdf;


	/**
	 * Constructor
	 * @param QueryVariables String containing all the query variables.
	 */
	public SQLLocationDataModel(String context, HashMap<String,String> specificLocationVars) {

		//setup my logging
		this.sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
		this.date = new Date();

		//copy over my queryVariables
		this.queryVariables = specificLocationVars;

		//setup the tomcat context
		this.appContext = context;

		//setup this cache code from the query Variables each lat / long should coincide with a unique XSLT
		//this.cacheCode = Integer.toString(queryVariables.hashCode());
		//get the cachecode from the query string
		this.resultFileCacheCode = queryVariables.get("cacheCode"); //get the cacheCode of the result set file i am working on!
		this.noCache = Boolean.parseBoolean(queryVariables.get("noCache")); //get the bool if we should use the cache or not
		//remove this cacheCode once i have it -> so that my XSLT file is unique based on lat / lon coords
		this.queryVariables.remove("cacheCode");
		//now save the location cache code
		this.locationXSLTFileCacheCode = Integer.toString(this.queryVariables.hashCode());

//		this.date.setTime(System.currentTimeMillis());
//		System.out.println(sdf.format(date) + " " + this.toString() + ":: " + appContext);
		//log the event
		this.date.setTime(System.currentTimeMillis());
		System.out.println(sdf.format(date) + " " + this.toString() + "::LocationDataModel->Construtor() constructing the LocationXSLTemplate && the dataParser.");

		//NOTE: These objects need to be created for each new query.
		//create the new XSL Template file based on my query variables
		new LocationXSLTemplate(this.appContext, queryVariables);
		//construct the xslt parser - using the new XSLT file created by LocationXSLTemplate
		this.dataParser = new Parser(this.appContext, "cresis/Temp/XSLT/extract_location_data_" + this.locationXSLTFileCacheCode + ".xsl");

	}

	/**
	 * Extract Location Data from the SPARQL result set for a specified location.
	 * @param HasMap<String,String> containing the lat & lon and query variables which act as columns for the grid
	 */
	public void parseLocationData() throws Exception {

		//get the correct SPARQL result set file
		String xmlResultFile = this.appContext + "cresis/temp/Results/SQL_Results_" + this.resultFileCacheCode + ".xml";
		String resultFile = this.appContext + "cresis/temp/Results/ResultFile_" + this.resultFileCacheCode + "_Location_" + this.locationXSLTFileCacheCode + ".xml";

		//test if the file exists -> use the cached version (hashCode should be unique).
		File cachedResults = new File(resultFile);

		//see if the XSLT for the location has been setup before
		if (!cachedResults.exists() || this.noCache) {

			//now translate the SPARQL Result set to XML
			this.dataParser.translate(xmlResultFile, resultFile);
		}
	}

	/**
	 * Getter for the location data XMLFile
	 * @return String path of the location Data XML File
	 */
	public String getLocationDataFile() {
		//TODO turn this into a class variable and return it! -> Seems each locationDataModel Class will be unique and point to a different xml file / location , hence each class will have a uniq path!
		//hosted mode.
//		return "cresis/temp/Results/ResultFile_" + this.resultFileCacheCode + "_Location_" + this.locationXSLTFileCacheCode + ".xml";
		//The server version
		return "temp/Results/ResultFile_" + this.resultFileCacheCode + "_Location_" + this.locationXSLTFileCacheCode + ".xml";
	}


	/**
	 * Getter for the location column variables array
	 * @return String[] of the query columns
	 */
	public ArrayList<String> getQueryVariables() {

		//setup the paths
		String sqlVariablesFile = this.appContext + "cresis/temp/Results/SQL_Variables_" + this.resultFileCacheCode + ".xml";

		//parse the converted XML file and return the location marker data
		BufferedReader inFile;

		//setup an array to hold the variable names
		ArrayList<String> variableNames = new ArrayList<String>();

		//setup a flag to end the parsing
		Boolean finishedFlag = true;

		//setup regular expression for the start of the SPARQL query expression
		Pattern queryVariable = Pattern.compile("<variable>(.+)</variable>");
		//setup regular expression for the end of the SPARQL query expression
		Pattern queryEnd = Pattern.compile("</variables>");
		//match test for the reg. expressions
		Matcher boundaryMatcher;
		Matcher nameMatcher;

		//setup the file reader
		try {
			//setup my Buffered File Reader
			inFile = new BufferedReader(new FileReader(sqlVariablesFile));

			//var to hold the lines of the file
			String line = "";

			//parse the file
			try {

				//read in each line till EOF or i find the end of the </head> section
				while ((line = inFile.readLine()) != null && finishedFlag) {

					//clean up any whitespace
					line = line.trim();

					//setup the reg. expressions matchers with the input line
					boundaryMatcher = queryEnd.matcher(line);
					nameMatcher = queryVariable.matcher(line);

					//test the regular expression for the beginning of the variable declarations
					if (boundaryMatcher.find()) {
						this.date.setTime(System.currentTimeMillis());
//						System.out.println(sdf.format(date) + " " + this.toString() + " :: Found the end </head> tag...exiting.");
						//found the end head tag...so should have picked up all my variables names...
						//this will cause us to exit the parsing.
						finishedFlag = false;
						//skip to next line
						continue;
					}

					//test the regular expression for the beginning of the variable declarations
					if (nameMatcher.find()) {

						this.date.setTime(System.currentTimeMillis());
//						System.out.println(sdf.format(date) + " " + this.toString() + " :: Found the vairable tag -> extracting the variable name....");

						//i have got the match so extract the name and description fields
						variableNames.add(nameMatcher.group(1));
						this.date.setTime(System.currentTimeMillis());
//						System.out.println(sdf.format(date)  + " The Variable we just found is : " + nameMatcher.group(1));
						//skip to next line
						continue;
					}
				}

				//close the reader
				inFile.close();

			} catch (IOException e) {
				this.date.setTime(System.currentTimeMillis());
				System.out.println(sdf.format(date) + " :: LocationDataModel->getQueryVariables() :: Can't read from the Results file.");
			}
		} catch (FileNotFoundException e) {
			//notify the user that i couldn't open the input file
			this.date.setTime(System.currentTimeMillis());
			System.out.println(sdf.format(date) + " :: LocationDataModel->getQueryVariables() Can't open the result file to extract query variables." + e.getLocalizedMessage());
	    }



		//return the resulting arrayList
		return variableNames;
	}

}
