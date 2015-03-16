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

package au.edu.uq.eresearch.server;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.ServletContext;

import au.edu.uq.eresearch.client.CresisService;
import au.edu.uq.eresearch.server.datamodel.LocationDataModel;
import au.edu.uq.eresearch.server.datamodel.SQLLocationDataModel;
import au.edu.uq.eresearch.server.datastore.QueryController;
import au.edu.uq.eresearch.server.datastore.SQLQueryController;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author Campbell Allen
 * @date   23/5/2008
 * Server side functionality implementation.
 * Note: Uses GWT RPC mechanism -> http://code.google.com/webtoolkit/documentation/com.google.gwt.doc.DeveloperGuide.RemoteProcedureCalls.html
 *
 */
public class ServiceImpl extends RemoteServiceServlet implements CresisService {

		final static long serialVersionUID = 12345678;
		private QueryController controller;
		private String appPath;

		//class to create a data model for use in the data grid UI component
		private LocationDataModel dataModel;

		//used for logging
		private Date date;
		private SimpleDateFormat sdf;

		/**
		 * Default constructor
		 */
		public ServiceImpl(){
			this.appPath = "";
			//setup my logging
			this.sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
			this.date = new Date();
		}

		/**
		 * Set the context of the servlet and pass onto server side objects
		 * @throws Generic Exception
		 */
		public String setup() throws RuntimeException {
			
			//Check if the server has already setup!
			if (this.appPath == null || this.appPath.equalsIgnoreCase("")) {
				//setup my tomcat app context
				ServletContext context = getServletContext();
				this.appPath = context.getRealPath("/");
//				System.out.println("Server apppath was not setup, apppath :: " + this.appPath);
			}
			
			if (this.controller == null) {
				//create a new controller instance with the correct context
				this.controller = new SQLQueryController(appPath);

//				System.out.println("Server Controller was not setup, controller :: " + this.controller.toString());
//				//used by RDF store only at present
//				try {
//					//connect the repository object
//					controller.connectRepository();
//				} catch (Exception e) {
//					throw new RuntimeException(e);
//				}
				return "Server side setup completed ok.";
			}
			return "The server was running and ready to go!";
		}

		/**
		 * Tests if the query is still executing.
		 * @return Boolean indicating if the query execution has finished.
		 * @throws Generic Exception
		 */
		public Boolean isQueryFinished(long threadID) throws RuntimeException {

			return this.controller.isQueryFinished(threadID);
		}

		/**
		 * Execute the given SPARQL query against the RDF data store.
		 * @param SPARQL query to execute against the data store
		 * @return String array of Strings containing the location marker information
		 * @throws Generic Exception
		 */
		public long executeThreadedLocationQuery(String queryString, Boolean noCache) throws RuntimeException {

			//check the if query exists
			if(queryString.length() < 1) {
				throw new RuntimeException("Please enter a query.");
			}

			long threadID;

			//Query the RDF store
			try {
				//get the result from the controller.
				threadID = controller.threadedLocationQuery(queryString, noCache);
			} catch (SQLException e){
				throw new RuntimeException("Database Error: " + e.getMessage());
			}

			return threadID;
		}

		/**
		 * Parse the Location Result file and return this info to the UI
		 * @param SPARQL query to execute against the data store
		 * @return String array of Strings containing the location marker information
		 * @throws Generic Exception
		 */
		public String[][] getQueryResults(String queryString, Boolean cache) throws RuntimeException {

			//ByteArrayOutputStream stream = new ByteArrayOutputStream();
			String[][] locationData = new String[][] { new String[]{"executeLocationQuery Method setup", "executeLocationQuery Method setup", "executeLocationQuery Method setup"} };

			//check the if query exists
			if(queryString.length() < 1) {
				throw new RuntimeException("Please enter a query.");
			}

			//Query the RDF store
			try {
				//get the result from the controller.
				locationData = this.controller.getQueryResult(queryString, cache);
			} catch (RuntimeException e){
				throw e;
			}

			return locationData;
		}

		/**
		 * Parse the Query SQL Variables file to find the query variables used.
		 * These are then passed back to the UI to setup the Grid view
		 * @param SPARQL query to execute against the data store
		 * @return String array of the query variables
		 * @throws Generic Exception
		 */
		public ArrayList<String> getQueryResultVariables() throws RuntimeException {

			//use the data model to get the query variables.
			return this.dataModel.getQueryVariables();
		}

		/**
		 * Translate the location specific data from the result set file and return the path to the translated file
		 * @return String Path of the location data XML file
		 * @throws Generic Exception
		 */
		public String getLocationDataGrid(HashMap<String,String> queryVariables) throws RuntimeException {

			//setup my context -> have to do this here again due to async behaviour
			ServletContext context = getServletContext();

			//log the event
			this.date.setTime(System.currentTimeMillis());
			System.out.println(sdf.format(date) + " " + this.toString() + " :: ServiceImpl->getLocationDataGird() trying to construct the LocationDataModel.");

			//setup the queryModel helper class
			this.dataModel = new SQLLocationDataModel(context.getRealPath("//"), queryVariables);

			//log the event
			this.date.setTime(System.currentTimeMillis());
			System.out.println(sdf.format(date) + " " + this.toString() + " :: ServiceImpl->getLocationDataGird() constructed the Location Data Model.");

			try {
				//try and parse the contents of the file
				this.dataModel.parseLocationData();

			} catch (Exception e) {

				//log the event
				this.date.setTime(System.currentTimeMillis());
				System.out.println(sdf.format(date) + " " + this.toString() + " :: ServiceImpl->getLocationDataGird() ERROR: " + e.getLocalizedMessage());
				throw new RuntimeException(e);
			}

			//return the path of the location data xml file
			this.date.setTime(System.currentTimeMillis());
			System.out.println(sdf.format(date) + " " + this.toString() + " :: Returning this file:" + this.dataModel.getLocationDataFile());
			return this.dataModel.getLocationDataFile();
		}
}