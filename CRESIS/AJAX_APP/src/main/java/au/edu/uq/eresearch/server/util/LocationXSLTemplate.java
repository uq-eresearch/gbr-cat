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
package au.edu.uq.eresearch.server.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author	Campbell Allen
 * @date	28/7/2008
 * Class to construct an XSL Template.
 *
 */
public class LocationXSLTemplate {

	//query data - hash map has keys: Location, Latitude, Longitude and Columns
		//Columns value is a string of column names separated by ":"
	private HashMap<String,String> queryVariables;

	//input reader
	private BufferedReader xsltTemplate;

	//output writer
	private BufferedWriter outXSLTFile;

	//used for logging
	private Date date;
	private SimpleDateFormat sdf;

	/**
	 * Default Constructor
	 */
	public LocationXSLTemplate(String context, HashMap<String,String> queryVars) {

		//setup my logging
		this.sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
		this.date = new Date();

		//set the query variables
		this.queryVariables = queryVars;

		//string for the XSLTemplate output path -> Note: simple use of hash code for caching
		String xsltPath = context + "cresis/temp/XSLT/extract_location_data_" + queryVariables.hashCode() + ".xsl";

		//test if we need to make the dir for the XSLT file
		File folderPath = new File (context + "cresis/temp/XSLT/");
		if (! folderPath.exists()){
			try {
				folderPath.mkdirs();
			} catch (Exception e) {
				this.date.setTime(System.currentTimeMillis());
				System.out.println(sdf.format(date) + " " + this.toString() + " : " + e.getMessage());
			}
		}

		//test if the file exists -> use the cached version (hashCode should be unique).
		File cachedXSLT = new File(xsltPath);
		Boolean noCache = Boolean.parseBoolean(this.queryVariables.get("noCache")); //get the bool if we should use the cache or not

		//see if the XSLT for the location has been setup before
		if (!cachedXSLT.exists() || noCache) {

			//get the the xslt location extraction file.
			// setup the file reader
			try {
				//setup the template input file
				this.xsltTemplate = new BufferedReader(new FileReader(context + "cresis/Resources/XSL_Templates/SQL_extract_location_data.xsl"));
				//setup my output file writer
				this.outXSLTFile = new BufferedWriter(new FileWriter(xsltPath));
				this.parseXSLTemplate();

			} catch (FileNotFoundException e) {
				//notify the user that i couldn't open the input file
				this.date.setTime(System.currentTimeMillis());
				System.out.println(sdf.format(date) + " " + this.toString() +  " :: Location Data Extraction XSL Template not found!" + e.getLocalizedMessage());
		    } catch (IOException e) {
		    	this.date.setTime(System.currentTimeMillis());
				System.out.println(sdf.format(date) + " " + this.toString() +  ":: XSL Template output file can't be created!" + e.getLocalizedMessage());
		    }
		}
//		else {
//		this.date.setTime(System.currentTimeMillis());
//		System.out.println(sdf.format(date) + " " + this.toString() +  " :: Seems the caching is working too well!");
//		}

	}

	/**
	 * Parse the contents of the XSL template file and insert the location and query column specific data at the correct marker.
	 */
	protected void parseXSLTemplate() {

		//used to read each line
		String line = "";

		//setup regular expression for the start of the SPARQL query expression
		Pattern latValue = Pattern.compile("--LATITUDE--");
		Pattern longValue = Pattern.compile("--LONGITUDE--");
		//match test for the reg. expressions
		Matcher matcher;

		try {

			//read in each line till EOF
			while ((line = xsltTemplate.readLine()) != null) {

				//test if I have the latitude marker
				matcher = latValue.matcher(line);
				if (matcher.find()) {
					//replace the place marker with the actual value
					//System.out.println("Found the Latitude marker in the XSLT file!!" + " : " + queryVariables.get("latitude"));
					line = matcher.replaceAll(queryVariables.get("latitude"));
					//System.out.println(line);
					outXSLTFile.write(line+"\n");
					//skip to next line
					continue;
				}

				//test if I have the longitude marker
				matcher = longValue.matcher(line);
				if (matcher.find()) {
					//replace the place marker with the actual value
					//System.out.println("Found the Longtitude marker in the XSLT file!!");
					line = matcher.replaceAll(queryVariables.get("longitude"));
					//System.out.println(line);
					outXSLTFile.write(line+"\n");
					//skip to next line
					continue;
				}

				//now output the line to my XSLT file
				outXSLTFile.write(line+"\n");
			}

			//close the reader
			xsltTemplate.close();
			//close the writer
			outXSLTFile.close();

		} catch (IOException e) {
			this.date.setTime(System.currentTimeMillis());
			System.out.println(sdf.format(date) + " " + this.toString() + " :: Error parsing the XSL Template file -> " + e.getLocalizedMessage());
		}
		//completed successfully
		this.date.setTime(System.currentTimeMillis());
		System.out.println(sdf.format(date) + " " + this.toString() + " :: Successfully created the XSL Template to extract the location data.");
//		}
//		else {
//			//problem occurred during the parsing of columns for use in the template
//			this.date.setTime(System.currentTimeMillis());
//			System.out.println(sdf.format(date) + " " + this.toString() + " :: COLUMN Data in the XSL Template is not intialised correctly. Column data hould be separated by ':'");
//		}
	}

	/**
	 * Getter for the query variables array
	 * @return String[] containing the query variables
	 */
	public HashMap<String,String> getQueryVariables() {
		return queryVariables;
	}

	/**
	 * Setter for the query variables hash map
	 * @param queryVars String[] containing query variables.
	 */
	public void setQueryVariables(HashMap<String,String> queryVars) {
		queryVariables = queryVars;
	}
}
