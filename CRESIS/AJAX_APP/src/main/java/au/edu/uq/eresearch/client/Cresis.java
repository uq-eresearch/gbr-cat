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

package au.edu.uq.eresearch.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.maps.client.InfoWindow;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapType;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.MapTypeControl;
import com.google.gwt.maps.client.control.SmallMapControl;
import com.google.gwt.maps.client.event.MarkerClickHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.geom.Size;
import com.google.gwt.maps.client.overlay.GroundOverlay;
import com.google.gwt.maps.client.overlay.Icon;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.InvocationException;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Margins;
import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.core.SortDir;
import com.gwtext.client.data.DateFieldDef;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.FloatFieldDef;
import com.gwtext.client.data.GroupingStore;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.SortState;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.data.XmlReader;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Component;
import com.gwtext.client.widgets.Container;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.Viewport;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.event.ContainerListenerAdapter;
import com.gwtext.client.widgets.event.TabPanelListenerAdapter;
import com.gwtext.client.widgets.form.Checkbox;
import com.gwtext.client.widgets.form.ComboBox;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.MultiFieldPanel;
import com.gwtext.client.widgets.form.TextArea;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.grid.BaseColumnConfig;
import com.gwtext.client.widgets.grid.CellMetadata;
import com.gwtext.client.widgets.grid.CheckboxColumnConfig;
import com.gwtext.client.widgets.grid.CheckboxSelectionModel;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.GridView;
import com.gwtext.client.widgets.grid.GroupingView;
import com.gwtext.client.widgets.grid.Renderer;
import com.gwtext.client.widgets.grid.RowSelectionModel;
import com.gwtext.client.widgets.grid.event.GridListenerAdapter;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;
import com.gwtext.client.widgets.layout.FitLayout;
import com.gwtext.client.widgets.menu.CheckItem;
import com.gwtext.client.widgets.menu.Menu;


 /**
 * @author Campbell Allen
 * @date   23/5/2008
 * Client side UI GWT class with GWT-EXT widgets.
 * Executes SPAQL queries against the server implementation with mapping and data display interfaces.
*/
public class Cresis implements EntryPoint {

    //App context
    private String appContext;
    //Map widgets
    private MapWidget map;
//    private GroundOverlay groundOverlay;
    private GroundOverlay[] overlays;
    private int overlayCounter;
    //overlay extents
    private Double S;
	private Double W;
	private Double N;
	private Double E;
	//static URL's for images to overlay
	private String[] images;

//    private com.eg.gwt.openLayers.client.MapWidget olMapWidget;

    //timer class used in the server polling mechanism
    private Timer elapsedTimer;
    private Timer imageLoopTimer;
    private long queryStartTime;

    //id's of the last executed query
    private String queryID;
    private Boolean cacheID;
    private long queryQueueIndex;

    //id's of the clicked marker
    private String markerLat;
    private String markerLon;
    private String markerName;

    //data Grid tab counter
    private int gridCounter;

    //query limit size
    private String queryLimit;

    //query JOIN variables
    private String locationJoinFilter;
    private String datetimeJoinFilter;
    private Boolean locationJoin;
    private Boolean datetimeJoin;

    //Panel
    private Panel borderPanel;
    private Panel eastPanel;
    private Panel mapPanel;
    private Panel queryPanel;
    private TabPanel southPanel;
    private Panel northPanel;
    private TabPanel centrePanel;
//    private Panel chartPanel;
    private GridPanel queryGrid;
    //private GridPanel dataGrid;
//    private ToolbarButton queryTypeButton;
    private Boolean queryType;
    private BorderLayoutData northData;
//    private PopupPanel joinQueryPopup;
    private ComboBox joinDatetimeQualifier;
    private Window joinWindow;
    private FormPanel formPanel;
    private Button joinStart;
    private MultiFieldPanel joinLocationFields;
    private Checkbox joinLocation;
    private TextField joinLocationQualifier;
    private MultiFieldPanel joinDatetimeFields;
    private Checkbox joinDatetime;
    private Menu menu;
    private CheckItem union;
    private CheckItem join;

    //Query UIPanel Array placeholder
    private QueryPanel[] queryPanels;
    private int uiPanelCount;

    //XML overlay
//    private GeoXmlOverlay geoXml = null;

    //Shared UI components
    private TextArea queryField;
    private RowSelectionModel rowSelectionModel;
//    private GroupingStore store;

    //RPC CallBack services
    private CresisServiceAsync reifsServer;
    private ServiceDefTarget endpoint;
    private AsyncCallback<String[][]> queryCallBack;
    private AsyncCallback<Long> threadedQueryCallBack;
    private AsyncCallback<Boolean> queryCompletedCallBack;
    private AsyncCallback<String> initCallBack;
//    private AsyncCallback<Object> queryModelCallBack;
    private AsyncCallback<String> dataModelCallBack;
    private AsyncCallback<ArrayList<String>> dataModelVariablesCallBack;

    //Example QueryGrid UI Model -> supplied by async server call
//    private String[][] queryExampleGridModel;
    //the Path of the specific location results file on the server
    private String dataGridModelFile;

    //map of query result markers information
    private HashMap<String,String> uniqueMarkers;
    private LinkedHashMap<String,String[]>columnMap;
    private HashMap<Integer,String> columnSortIndex;
//    private HashMap<String,String> nodeMap;
    
    //Enums of the query fields used in a string switch statement
    public enum QueryFields {
        NORTH, SOUTH, EAST, WEST, LAT, LON, REGION_AS_RANGE, START_DATE, END_DATE, TARGET, CHARACTERISTIC,
        LOCATION, ACTOR, MEASUREMENT_VALUE, UNIT, RESEARCH_PROGRAM, ECOLOGICAL_PROCESS,
        PROCESS_OUTPUT, GENUS, SPECIES, MORPHOLOGY, ORGANISM_VALUES, DEPTH, SENSOR, LOCATION_FILTER, DATETIME_FILTER,
        JOIN_QUERY, JOIN_QUERY_DATETIME, JOIN_QUERY_LOCATION, DESCRIPTION, QUERY_STRING;
    }

    //used for logging
    private Date date;
    private DateTimeFormat dtf;

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {

    	//setup my logging
        this.dtf = DateTimeFormat.getFormat("MMM dd, yyyy HH:mm:ss");
        this.date = new Date();

        //counter for the tabs of data grids
        this.gridCounter = 0;

        //setup a list of ground overlays
        this.overlays = new GroundOverlay[5];
        this.images = new String[] {"http://maenad.itee.uq.edu.au/metadata/images/M-L3-GBR-1M-1M-CHLOR-D-SGBR-1M-2007001-2007031_monthly.png",
//									"http://maenad.itee.uq.edu.au/metadata/images/M-L3-GBR-1M-1M-CHLOR-D-SGBR-1M-2008336-2008366_monthly_anomaly.png",
        							"http://maenad.itee.uq.edu.au/metadata/images/M-L3-GBR-1M-1M-SST-D+N-SGBR-1M-2007001-2007031_monthly.png",
//        							"http://maenad.itee.uq.edu.au/metadata/images/M-L3-GBR-1M-1M-SST-D+N-SGBR-1M-2008336-2008366_monthly_anomaly.png",
        							"http://maenad.itee.uq.edu.au/metadata/images/M-L3-GBR-1D-1D-ZEUL-D-SGBR-1M-2007001-2007031_monthly.png"};
        this.overlayCounter = 0;
        this.S = -24.5;
        this.W = 149.5;
        this.N = -21.5;
        this.E = 153.0;

        //static assignment of images to the array

        //setup my query limit
        this.queryLimit = "200";

        //setup my join vars
        this.locationJoin = false;
        this.locationJoinFilter = "";
        this.datetimeJoin = false;
        this.datetimeJoinFilter = "";

        //setup my hashmaps
        this.uniqueMarkers = new HashMap<String,String>();
        this.columnMap = new LinkedHashMap<String,String[]>();
        this.columnSortIndex = new HashMap<Integer,String>();
        
        //setup the server callbacks
        setupAsyncCallBacks();

        //now tell the server side to setup
        //note: I use this method to set the servlet context
        this.reifsServer.setup(initCallBack);

        //now setup the query model for use in the query grid UI
//        reifsServer.setupQueryModel(queryModelCallBack);

        //NOTE: Async calls need to finish before my UI can setup properly
        // however i can save time here and try to render the UI again after i have finished
        setupComponents();

        //setup the timer class to poll the server about completed queries.
        this.elapsedTimer = new Timer () {
          public void run() {
              //now i need to call the server...
              reifsServer.isQueryFinished(queryQueueIndex, queryCompletedCallBack);
          }
        };

        //setup the timer class to poll the server about completed queries.
        this.imageLoopTimer = new Timer () {
          public void run() {
        	  //take the modulus of it.
        	  int off_index = (overlayCounter-1) % images.length;
        	  int on_index = overlayCounter % images.length;

        	  //need to set a loop over the images overlays and set the current to visible
        	  overlays[off_index].setVisible(false);
        	  overlays[on_index].setVisible(true);

        	  //inc my overlayCounter
        	  overlayCounter++;

          }
        };

        //workaround!: now hide the second rendered query panel UI component..
//        this.queryPanels[1].columnPanel.hide();
        
        //load any HTTP params
        loadDefaultHTTPParameters();

    }
    
    private void loadDefaultHTTPParameters() {
    	
    	try {
    		//now i check if we have any URL params that need to be dealt with
	        Map<String,List<String>> params = com.google.gwt.user.client.Window.Location.getParameterMap();
	        
	        //should we execute the query by default
	        boolean executeQuery = false;
	        
	        //do we have any to load?
	        if (params.size() > 0) {
	        	//loop over the set of params
	        	for (String param : params.keySet()) {
	        		
	        		//check if we have an execute variable and if the set value is true
	        		if (param.equalsIgnoreCase("execute_query") && Boolean.parseBoolean(params.get(param).get(0))) {
	        			executeQuery = true;
	        			//skip to the next param
	        			continue;
	        		}
	        		//check if the field exists
	        		QueryFields[] fields = QueryFields.values();
	        		for (int i=0; i < fields.length; i++) {
	        			//only process fields we know about
	        			if (fields[i].toString().equalsIgnoreCase(param)) {
	        				//set the query specific query UI field 
	        				setUIQueryField(0, param, params.get(param).get(0));
	        			} 
	        		}
	        	}
	        
	        	//check if there was an execute http param
	        	if (executeQuery) {
	        		//execute the query
	        		executeQuery(true, false, true);
		        	//confirm that we should execute the query?
//		        	MessageBox.confirm("Execute Query", "Would you like to execute the automatically loaded query?", new MessageBox.ConfirmCallback() {
//	                    //Note Async behaviour -> i need to execute the query on return of the call back...which may be a long time after the code executes.
//	                    public void execute(String btnID) {
//	                        if (btnID.equals("yes")) {
//	                        	executeQuery(true, false, true);
//	                        }
//	                    }
//	                });
	        	}

	        }
    	} 
    	catch (Exception e) {
    		GWT.log(e.getLocalizedMessage(),null);
    		e.printStackTrace();
    	}
    }


    /**
     * Setup the RPC mechanism for client server interaction
     */
    private void setupAsyncCallBacks() {

        //Create an instance of the client-side stub for our server-side service.
        reifsServer = (CresisServiceAsync) GWT.create(CresisService.class);
        endpoint = (ServiceDefTarget) reifsServer;

        //The implementation of our service is an instance
        //of RemoteServiceServlet, so provide the server path
        //to the servlet; this path appears in web.xml
        endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "server");

        //app base context -> for use loading UI components
        appContext = GWT.getModuleBaseURL();

        //setup a callback for the query response from the server.
        queryCallBack = new AsyncCallback<String[][]>() {
            public void onSuccess(String[][] result) {

                //unmask the query edit panel
                northPanel.getEl().unmask();
                //map.checkResize();

                //setup the column maps for each query
                setupColumnMap();

                //clear up the overlays from the previous query
                //map.clearOverlays();

                //cast the result set into the String[][]
                final String[][] markers = (String[][])result;

                //setup a marker event handler
                MarkerClickHandler markerHandler = new MarkerClickHandler() {
                    public void onClick(MarkerClickEvent e) {
                        //setup a marker based on the click source
                        Marker myMarker = (Marker)e.getSource();
                        LatLng point = myMarker.getLatLng();
                        InfoWindow infoWindow = map.getInfoWindow();
                        //extract the marker location name and number of sightings from the hash map
                        String[] markerInfo = uniqueMarkers.get(String.valueOf(point.getLatitude()) + ":" +String.valueOf(point.getLongitude())).split(":");
                        InfoWindowContent content = new InfoWindowContent("<h1>" + markerInfo[0] + "</h1><i>Lat:" + point.getLatitude() + " ; Lon:" + point.getLongitude() + "</p>" + markerInfo[1]+ "</i> observations");
                        infoWindow.open(myMarker, content);

                        //create a hash map from the marker variables and the query variables here.
                        HashMap<String,String> gridVars = new HashMap<String,String>();
                        gridVars.put("latitude", String.valueOf(point.getLatitude()));
                        gridVars.put("longitude", String.valueOf(point.getLongitude()));
                        //get the cach_code from the result set -> used to remember which file our results came from.
                        gridVars.put("cacheCode", markers[0][4]);
                        gridVars.put("noCache", markers[0][5]);

                        //keep the details of the marker we clicked for use in the Data grid panel name
                        markerLat = String.valueOf(point.getLatitude());
                        markerLon = String.valueOf(point.getLongitude());
                        markerName = markerInfo[0];

                        //call the async callback to get the data model for the location
                        reifsServer.getLocationDataGrid(gridVars, dataModelCallBack);

                    }
                };
                
                try {

	                //make sure we only have the current results in the marker description map
	                //uniqueMarkers.clear();
	
	                // Loop through the different locations
	                for (int row=0; row < markers.length; row++) {
	
	                    //handle the case where we failed to get a result -> values in string array = "null"
	                    if (!markers[row][0].equals("null")) {
	
	                        //log the unique marker values in the hash map for later use in the info window.
	                        //use the lat / lon as the key for the marker map and add the name and number of sightings.
	                        //the marker description array is this -> location name, long, lat, numSightings
	                        //TODO: This may be overwritten accidently with diff sightings...maybe should add more data for the key in?
	                        uniqueMarkers.put(markers[row][2] + ":" + markers[row][1], markers[row][0] + ":" + markers[row][3]);
	
	                        int numSightings = Integer.valueOf(markers[row][3]);
	
	                        //create the marker data here - array looks like [name, long, lat, numSigthings]
	                        MarkerOptions markerOptions = MarkerOptions.newInstance();
	
	                        //Setup a base icon that we will override
	                        Icon icon = Icon.newInstance("http://www.google.com/intl/en_us/mapfiles/ms/micons/red-dot.png");
	                        //Icon icon = new Icon(Icon.DEFAULT_ICON);
	                        //Icon icon = new Icon();
	                        icon.setShadowURL("http://www.google.com/mapfiles/shadow50.png");
	                        icon.setIconSize(Size.newInstance(30, 30));
	                        //icon = 20,34; shadow = 37, 34
	                        icon.setShadowSize(Size.newInstance(54, 30));
	                        icon.setIconAnchor(Point.newInstance(14, 30));
	                        icon.setInfoWindowAnchor(Point.newInstance(14, 2));
	
	                        //change the icon colour according to the number of sightings
	                        if (numSightings < 100) {
	                            //set the marker for the correct range
	                            icon.setImageURL("http://www.google.com/intl/en_us/mapfiles/ms/micons/purple-dot.png");
	                        } else if (numSightings < 200) {
	                            //set the marker for the correct range
	                            icon.setImageURL("http://www.google.com/intl/en_us/mapfiles/ms/micons/green-dot.png");
	                        } else if (numSightings < 500) {
	                            //set the marker for the correct range
	                            icon.setImageURL("http://www.google.com/intl/en_us/mapfiles/ms/micons/blue-dot.png");
	                        } else if (numSightings < 1000) {
	                            //set the marker for the correct range
	                            icon.setImageURL("http://www.google.com/intl/en_us/mapfiles/ms/micons/pink-dot.png");
	                        } else if (numSightings < 2000) {
	                            //set the marker for the correct range
	                            icon.setImageURL("http://www.google.com/intl/en_us/mapfiles/ms/micons/yellow-dot.png");
	                        } else if (numSightings < 5000) {
	                            //set the marker for the correct range
	                            icon.setImageURL("http://www.google.com/intl/en_us/mapfiles/ms/micons/orange-dot.png");
	                        } else if (numSightings > 5000) {
	                            //set the marker for the correct range
	                            icon.setImageURL("http://www.google.com/intl/en_us/mapfiles/ms/micons/red-dot.png");
	                        }
	
	                        //now i can set the icon
	                        markerOptions.setIcon(icon);
	                        markerOptions.setTitle(markers[row][0] + " ; " + numSightings);
	
	                        //check for number format exceptions..
	                        try {
	                            //create the markers
	                            Marker marker = new Marker(LatLng.newInstance(Double.valueOf(markers[row][2]), Double.valueOf(markers[row][1])), markerOptions);
	                            marker.addMarkerClickHandler(markerHandler);
	                            map.addOverlay(marker);
	                        } catch (NumberFormatException ne) {
	                            date.setTime(System.currentTimeMillis());
	                            GWT.log(dtf.format(date) + " " + this.toString() + " :: " + ne.getLocalizedMessage(), null);
	                        }
	
	                        //pan the map if we have looped over all the rows
	                        if (row == markers.length-1){
	                            //set the map view to the last found marker
	                            map.panTo(LatLng.newInstance(Double.valueOf(markers[row][2]), Double.valueOf(markers[row][1])));
	                        }
	
	                        //TODO: Make a screenOverlay for the key / icon overlay!!
	                    }
	                    else {
	                        //this will skip the inner loop and as the outer loop is only 1 long we will only see this once.
	//                        MessageBox.alert("Query Error -> Failed to get a result set for the query. Please check the query.");
	                        MessageBox.alert("Failed to get a result set for the query, check the query. (Note: there may be no corresponding data)");
	                    }
	                }
                } catch (Exception e) {
                	GWT.log("Failed to create the result markers", null);
                	e.printStackTrace();
                	MessageBox.alert("ERROR: Failed to create the result set markers.<br>Contact the system administrator.");
                }

            }
            public void onFailure(Throwable caught) {

                //unmask the query edit panel
                northPanel.getEl().unmask();

                //clear up the overlays from the previous query
                //map.clearOverlays();

                //throw the exception and handle the different types
                try {
                  throw caught;
                } catch (IncompatibleRemoteServiceException e) {
                    // this client is not compatible with the server; cleanup and refresh the
                    // browser, the call didn't complete cleanly on the server
                    MessageBox.alert("Client is out of date and not compatable with the server anymore : " + caught.getMessage());
                    GWT.log("Client is out of date and not compatable with the server anymore : " + caught.getMessage(), null);
                }
//                catch (StatusCodeException e) {
//                  // HTTP error
//                	MessageBox.alert("HTTP ERROR : " + e.getStatusCode() + " : " + e.getMessage());
//                	GWT.log("HTTP ERROR : " + e.getStatusCode() + " : " + e.getMessage(), null);
//                }
                catch (InvocationException e) {
                    // the call didn't complete cleanly on the server
                    MessageBox.alert(e.getStackTrace().toString());
                    MessageBox.alert("Server error: Either the server isn't up OR RPC timeout error, check the server log for details:" + e.getMessage());
//                    GWT.log("Server invocation error: looks like the server isn't up, check the server log for details:" + e.getMessage(), null);
                }
                catch (Exception e) {
                    // exceptions that we are looking for
                    MessageBox.alert(e.getStackTrace().toString());
                    MessageBox.alert(e.getClass() + " Exception : " + e.getMessage());
//                    GWT.log(e.getClass() + " Exception : " + e.getMessage(), null);
                }
                catch (Throwable e) {
                    MessageBox.alert(e.getStackTrace().toString());
                    // last resort -- a very unexpected exception - need to drill down to the error here
                    MessageBox.alert("Unknown exception : " + e.getMessage());
//                    GWT.log("Unknown exception : " + e.getMessage(), null);
                }

                //alert the user to the error
//                MessageBox.alert("Error: Query did not execute properly. Please see the log for details.");

            }
        };

        //setup a callback for the threaded query response from the server.
        threadedQueryCallBack = new AsyncCallback<Long>() {
            public void onSuccess(Long result) {

                try {
                    //cast the object to a long
                    Long objLong = (Long)result;
                    //get the id of the query thread we kicked off.
                    queryQueueIndex = objLong.longValue();
                } catch (Exception e) {
                     // exceptions that we are looking for
                    MessageBox.alert(e.getStackTrace().toString());
                    MessageBox.alert(e.getClass() + " Exception : " + e.getMessage());
                }

                // Schedule the timer for every second (in milliseconds)
                elapsedTimer.scheduleRepeating(1000);

            }
            public void onFailure(Throwable caught) {

                //unmask the query edit panel
                northPanel.getEl().unmask();

                //throw the exception and handle the different types
                try {
                  throw caught;
                } catch (IncompatibleRemoteServiceException e) {
                    // this client is not compatible with the server; cleanup and refresh the
                    // browser, the call didn't complete cleanly on the server
                    MessageBox.alert("Client is out of date and not compatable with the server anymore : " + caught.getMessage());
                    GWT.log("Client is out of date and not compatable with the server anymore : " + caught.getMessage(), null);
                }
//                catch (StatusCodeException e) {
//                  // HTTP error
//                	MessageBox.alert("HTTP ERROR : " + e.getStatusCode() + " : " + e.getMessage());
//                	GWT.log("HTTP ERROR : " + e.getStatusCode() + " : " + e.getMessage(), null);
//                }
                catch (InvocationException e) {
                    // the call didn't complete cleanly on the server
                    MessageBox.alert(e.getStackTrace().toString());
                    MessageBox.alert("Server error: Either the server isn't up OR RPC timeout error, check the server log for details:" + e.getMessage());
//                    GWT.log("Server invocation error: looks like the server isn't up, check the server log for details:" + e.getMessage(), null);
                }
                catch (RuntimeException e) {
                    // one of the remote server methods failed to finish properly
                	
                	GWT.log(e.getMessage(), null);
                	//check if this is my sql error
                	if (e.getMessage().startsWith("Database Error")) {
                		MessageBox.alert("The query text is invalid for the data store : " + e.getMessage().replaceAll("Database Error:", ""));
                	}
                } 
                catch (Exception e) {
                    // exceptions that we are looking for
                    MessageBox.alert(e.getStackTrace().toString());
                    MessageBox.alert(e.getClass() + " Exception : " + e.getMessage());
//                    GWT.log(e.getClass() + " Exception : " + e.getMessage(), null);
                }
                catch (Throwable e) {
                    MessageBox.alert(e.getStackTrace().toString());
                    // last resort -- a very unexpected exception - need to drill down to the error here
                    MessageBox.alert("Unknown exception : " + e.getMessage());
//                    GWT.log("Unknown exception : " + e.getMessage(), null);
                }

                //alert the user to the error
//                MessageBox.alert("Error: Query did not execute properly. Please see the log for details.");

            }
        };

        //setup a callback for the poll to test if the query is still running from the server.
        queryCompletedCallBack = new AsyncCallback<Boolean>() {
            public void onSuccess(Boolean result) {

                //get the time and formater to update the query timer UI label
                double elapsedTime = (System.currentTimeMillis() - queryStartTime) / 1000.0;
                NumberFormat nf = NumberFormat.getFormat("#,##0");

                try {
                    //cast my result from the server - this indicates if the server has finished it's work.
                    if ((Boolean)result){

                        //cancel the timer till the next one.
                        elapsedTimer.cancel();

                        //update the UI
                        northPanel.getEl().mask("Query completed in " + nf.format(elapsedTime) + " seconds");

                        //now let's call the server to return the results.
                        reifsServer.getQueryResults(queryID, cacheID, queryCallBack);

                    } else {
                        //query still running -> update the UI to tell the client we are watching it.
                        //now update the UI with the timer...
                        northPanel.getEl().mask("Querying the server, please wait..." + nf.format(elapsedTime) + " seconds elapsed.");
                    }
                } catch(Exception e) {
                    // exceptions that we are looking for
                    MessageBox.alert(e.getStackTrace().toString());
                    MessageBox.alert(e.getClass() + " Exception : " + e.getMessage());
                }

            }
            public void onFailure(Throwable caught) {

                //unmask the query edit panel
                northPanel.getEl().unmask();

                //throw the exception and handle the different types
                try {
                  throw caught;
                } 
                catch (IncompatibleRemoteServiceException e) {
                    // this client is not compatible with the server; cleanup and refresh the
                    // browser, the call didn't complete cleanly on the server
                    MessageBox.alert("Client is out of date and not compatable with the server anymore : " + caught.getMessage());
                    GWT.log("Client is out of date and not compatable with the server anymore : " + caught.getMessage(), null);
                }
//                catch (StatusCodeException e) {
//                  // HTTP error
//                	MessageBox.alert("HTTP ERROR : " + e.getStatusCode() + " : " + e.getMessage());
//                	GWT.log("HTTP ERROR : " + e.getStatusCode() + " : " + e.getMessage(), null);
//                }
                catch (InvocationException e) {
                    // the call didn't complete cleanly on the server
                    MessageBox.alert(e.getStackTrace().toString());
                    MessageBox.alert("Server error: Either the server isn't up OR RPC timeout error, check the server log for details:" + e.getMessage());
//                    GWT.log("Server invocation error: looks like the server isn't up, check the server log for details:" + e.getMessage(), null);
                }
                catch (Exception e) {
                    // exceptions that we are looking for
                    MessageBox.alert(e.getStackTrace().toString());
                    MessageBox.alert(e.getClass() + " Exception : " + e.getMessage());
//                    GWT.log(e.getClass() + " Exception : " + e.getMessage(), null);
                }
                catch (Throwable e) {
                    MessageBox.alert(e.getStackTrace().toString());
                    // last resort -- a very unexpected exception - need to drill down to the error here
                    MessageBox.alert("Unknown exception : " + e.getMessage());
//                    GWT.log("Unknown exception : " + e.getMessage(), null);
                }

                //alert the user to the error
//                MessageBox.alert("Error: Query did not execute properly. Please see the log for details.");

            }
        };

        //setup a callback for creating the server side objects
        initCallBack = new AsyncCallback<String>() {
            public void onSuccess(String result) {
                date.setTime(System.currentTimeMillis());
                GWT.log(dtf.format(date) + " " + result, null);
            }
            public void onFailure(Throwable caught) {
            	date.setTime(System.currentTimeMillis());
            	caught.printStackTrace();
                GWT.log(dtf.format(date) + " " + this.toString() + " ERROR -> Problem setting up the server, see tomcat logs for details", null);
            }

        };

        //Unused -> created an XML file to load into the example grid.
//        //setup a callback for creating the query example grid panel entries
//        queryModelCallBack = new AsyncCallback<Object>() {
//            public void onSuccess(Object result) {
//                date.setTime(System.currentTimeMillis());
//                GWT.log(dtf.format(date) + " " + this.toString() + " :: Query Model returned ok.", null);
//                String[][] queryExampleGridModel = (String[][])result;
//
//                //if the query file failed to parse it will return an empty string as the grid.
//                if (!queryExampleGridModel[0][0].equalsIgnoreCase("")) {
//                    //setup the grid
//                    //due to async callbacks...now my Server has setup i can complete the UI setup!
//                    setupQueryExampleGridPanel();
//                    //now unmask the eastpanel
//                    eastPanel.getEl().unmask();
//                    //now i can set the first query string in the query edit panel
//                    borderPanel.doLayout();
//                    //now select the first row of the selection model.
//                    rowSelectionModel.selectFirstRow();
//                    queryPanel.setTitle(rowSelectionModel.getSelected().getAsString("description"));
//                    queryField.setValue(rowSelectionModel.getSelected().getAsString("query_string"));
//                } else {
//                    //mask the grid and notify user it failed...
//                    eastPanel.getEl().mask("Example Queries Failed to Load.");
//                }
//            }
//            public void onFailure(Throwable caught) {
//                //now unmask the eastpanel
//                eastPanel.getEl().mask();
//                //log the error
//                date.setTime(System.currentTimeMillis());
//                GWT.log(dtf.format(date) + " " + this.toString() + " ERROR -> Query model setup and return failed, see logs for details : " + caught.getLocalizedMessage(), null);
//            }
//        };

        //setup a callback for creating the data grid panel entries
        dataModelCallBack = new AsyncCallback<String>() {
            public void onSuccess(String result) {
                //log the event that we returned ok
                date.setTime(System.currentTimeMillis());
                GWT.log(dtf.format(date) + " " + this.toString() + " :: Data grid returned ok.", null);

                //get the URL of the Specific locations data file
                dataGridModelFile = (String)result;

                //now we get the columns...from the server.
                reifsServer.getQueryResultVariables(dataModelVariablesCallBack);

            }
            public void onFailure(Throwable caught) {
                date.setTime(System.currentTimeMillis());
                GWT.log(dtf.format(date) + " " + this.toString() + " ERROR -> Data Model failed to setup properly, see logs for details : " + caught.getLocalizedMessage(), null);
            }
        };

        //setup a callback to get the individual query variables
        dataModelVariablesCallBack = new AsyncCallback<ArrayList<String>>() {
            public void onSuccess(ArrayList<String> result) {

                date.setTime(System.currentTimeMillis());
                GWT.log(dtf.format(date) + " " + this.toString() + " :: DataModelVariables returned ok.", null);
                
                //retrieve the resulting arraylist of query variables
                ArrayList<String> queryVariables = (ArrayList<String>)result;
                
                //debug
//                GWT.log("Query Variables: ", null);
//                for (String var : queryVariables) {
//                	GWT.log(var, null);
//                }

                //log the result
                date.setTime(System.currentTimeMillis());
                GWT.log(dtf.format(date) + " " + this.toString() + " :: About to setup the Data Grid Panel...", null);

                //test if we got any variables back form the result set.
                if (queryVariables.size() > 0) {
                    //now i have to create the data grid array from the XML file
                    //read the XML file and create a memory proxy from it! -> see setupDataPanel() below
                    //setup the data panel
                    setupDataPanel(dataGridModelFile, queryVariables);

                    //shrink the QueryUI panel
                    northPanel.collapse();
                    eastPanel.collapse();
                    //grow the data panel
                    southPanel.expand();
                    //redraw the whole panel
                    borderPanel.doLayout();
                } else {
                    MessageBox.alert("Error: No Variables returned from the result set, please contact the system administrator.");
                }
            }
            public void onFailure(Throwable caught) {
                date.setTime(System.currentTimeMillis());
                GWT.log(dtf.format(date) + " " + this.toString() + " ERROR -> Data Grid failed to setup properly, see logs for details : " + caught.getLocalizedMessage(), null);
            }
        };

    }

    /**
     * Private method to setup the GWT-EXT widgets and populate with the mapping and query widgets.
     */
    private void setupComponents() {

        //main panel
        borderPanel = new Panel();
        borderPanel.setId("border-panel");
        borderPanel.setLayout(new BorderLayout());

        //setup the various border panels
        setupNorthPanel();
        setupSouthPanel();
        setupWestPanel();
        setupCentrePanel();
        setupEastPanel();

        //set the viewport
        new Viewport(borderPanel);

        //now unmask the eastpanel
        eastPanel.getEl().mask("Loading Example Queries...");

        //load the Example Query Data into the grid
        setupQueryExampleGridPanel();

        //now i can set the first query string in the query edit panel
        borderPanel.doLayout();

        //unmask the panel
        eastPanel.getEl().unmask();

        //hack to make sure the Google maps widgets render to the correct size
        // note: Have to do this after the viewport has been rendered
        // TODO:  I think this is due to the lazy rendering...look at using GWT-EXT googlemap class.
        // Could be zoom levels and the mapping system.
        map.checkResize();        

    }


    /**
     * Implements the windowListener resize method for this class.
     * Used to make the map render properly.
     * TODO: still problems with rendering the map...look at the MAPS GWT-EXT classes!
     * TODO: This listener is never receiving events....need to check why.
     */
    public void onWindowResized(int width, int height) {
        //this makes the components not resize at all....i like this!
        //RootPanel.get().setSize(String.valueOf(width), String.valueOf(height));
        //centrePanel.doLayout();
        //GWT.log("resize event received", null);
        map.checkResize();
    };

    /**
     * Setup the north border panel components
     */
    private void setupNorthPanel() {

        //setup the queryUI Array -> max of 2 panels for themoment.
        this.queryPanels = new QueryPanel[1];
        //initalise the counter
        this.uiPanelCount = 0;

        //add north panel
        this.northPanel = new Panel();
        this.northPanel.setBorder(false);
        this.northPanel.setHeight(255);
        this.northPanel.setCollapsible(true);
        this.northPanel.setTitle("QUERY CREATOR");
        this.northPanel.setAutoScroll(true);

//        //get a QueryUI Panel
//        this.queryPanels[this.uiPanelCount] = new QueryPanel(this.appContext);
//        //now add the columnPanel to the main panel
//        northPanel.add(this.queryPanels[this.uiPanelCount].columnPanel);
//        //inc the counter
//        this.uiPanelCount++;
//
//        //get a QueryUI Panel
//        this.queryPanels[this.uiPanelCount] = new QueryPanel(this.appContext);
//        //now add the columnPanel to the main panel
//        northPanel.add(this.queryPanels[this.uiPanelCount].columnPanel);
//        this.queryPanels[this.uiPanelCount].columnPanel.hide();
////        inc the counter
////        this.uiPanelCount++;

        //get a QueryUI Panel
        this.queryPanels[0] = new QueryPanel(this.appContext);
        //now add the columnPanel to the main panel
        this.northPanel.add(this.queryPanels[0].columnPanel);
        //inc the counter
        this.uiPanelCount++;

//        //get a QueryUI Panel
//        this.queryPanels[1] = new QueryPanel(this.appContext);
//        //now add the columnPanel to the main panel
//        northPanel.add(this.queryPanels[1].columnPanel);
//        //Note: Dont' inc the counter.

        //setup the query Type intitially for use when building queries
        this.queryType = false;

        //Toolbar for the query - Note: right Aligned
        Toolbar queryToolbar = new Toolbar();
        queryToolbar.addFill();
        queryToolbar.addSpacer();

        //ignore cache toggle button
        final ToolbarButton noCache = new ToolbarButton("No Cache");
        noCache.setEnableToggle(true);
        noCache.setTooltip("Ignore Cached Query Results");
        queryToolbar.addButton(noCache);
        queryToolbar.addSeparator();

        //manual query toggle button
        final ToolbarButton manualOverride = new ToolbarButton("Manual Override", new ButtonListenerAdapter() {
            public void onClick(Button button, EventObject e) {
            	
            	//test if i am on or off
            	if (button.isPressed()) {
            		//set my title for the Query Field here
                    queryPanel.setTitle("Query contructed manually");
                    centrePanel.setActiveTab("queryEditTab");
            	} else {
            		queryPanel.setTitle("Query constructed automatically from the query panel");
                    centrePanel.setActiveTab("queryEditTab");
            		//turn the query panel off.
//            		centrePanel.setActiveTab("mapTab");
            	}
            }
        });
        manualOverride.setEnableToggle(true);
        manualOverride.setTooltip("Input query directly into query tab.");
        queryToolbar.addButton(manualOverride);
        queryToolbar.addSeparator();

        //add the clear query results button
        queryToolbar.addButton(new ToolbarButton("Clear Map", new ButtonListenerAdapter() {
            public void onClick(Button button, EventObject e) {
                //clear the overlays from the map.
                map.clearOverlays();
                centrePanel.setActiveTab("mapTab");
            }
        }));
        queryToolbar.addSeparator();

        //add the clear query UI button
        queryToolbar.addButton(new ToolbarButton("Clear Query UI", new ButtonListenerAdapter() {
            public void onClick(Button button, EventObject e) {
                //clear the query UI
                clearQueryUI();
            }
        }));
        queryToolbar.addSeparator();

        //add the edit button
        queryToolbar.addButton(new ToolbarButton("Build Query", new ButtonListenerAdapter() {
            public void onClick(Button button, EventObject e) {

                //now execute the query or notify user
                if (validateQueryPanels()){
                    centrePanel.setActiveTab("queryEditTab");
//                    //use the set class variable to decide which query type to build.
//                    if (queryType && queryPanels[1].columnPanel.isVisible()) {
//                    	//use the JOIN query builer and set the query Field
//        	            queryField.setValue(buildJoinSQLQuery(false));
//                	} else {
//                		//set the query field to be the result of the call to the build query method
//        	            queryField.setValue(buildUnionSQLQuery(false));
//                	}
                    
                    //just use the single query panel SQL query build method
                    queryField.setValue(buildSQLQuery());

                } else {
                    MessageBox.alert("Invalid Query Fields, please check them.");
                }
            }
        }));
        queryToolbar.addSeparator();

        //add the execute button
        queryToolbar.addButton(new ToolbarButton("Execute Query", new ButtonListenerAdapter() {
            public void onClick(Button button, EventObject e) {

                //see if we just want to put a query straight into the query text area
                if (manualOverride.isPressed()) {
            		executeQuery(noCache.isPressed(), true, false);
                }
                else {
                    //check that our forms fields are valid
                    if (validateQueryPanels()) {
                    	
                    	//only execute a query if the UI is changed.
                        if (isQueryUIDirty()) {
                        	executeQuery(noCache.isPressed(), false, false);
                        }
                        else {
                            MessageBox.confirm("Execute Query", "Do you really want to run the default query - this will find all results and may take a while?", new MessageBox.ConfirmCallback() {
                                //Note Async behaviour -> i need to execute the query on return of the call back...which may be a long time after the code executes.
                                public void execute(String btnID) {
                                    if (btnID.equals("yes")) {
		                            	executeQuery(noCache.isPressed(), false, true);
                                    }
                                }
                            });
                        }
                    }
	            	else {
	            		MessageBox.alert("Invalid Query Fields, please check them.");
	            	}
                }
            }
        }));
        queryToolbar.addSeparator();

//        //setup the toobar button
//        this.queryTypeButton = new ToolbarButton("Add Extra Query Panel");
//
//        //create a menu button to allow us to use both panels as either a JOIN or a UNION query.
//        menu = new Menu();
//        menu.setShadow(true);
//        menu.setMinWidth(10);
//
//        //setup my check Items
//        union = new CheckItem();
//        join = new CheckItem();
//
//        //setup my listener
//        final CheckItemListenerAdapter listener = new CheckItemListenerAdapter() {
//            public void onCheckChange(CheckItem item, boolean checked) {
//                //set the flag to use the UNION structure of the query
//                if (item.getId().equalsIgnoreCase("union")) {
//                    queryType = false;
//                    //set the union check to be active
//                    join.setChecked(false);
//                    union.setChecked(false);
//
//                    //make the second panel visibile
//                    queryPanels[1].columnPanel.show();
//                    //extend the north panel to show the second UI panel.
//                    extendQueryPanelHeight();
//
//                    //now disable myself till i remove the joined panel
//                    queryTypeButton.setDisabled(true);
//
//                } else {
//                    queryType = true;
//                    //set the join check to be active
//            		join.setChecked(false);
//                    union.setChecked(false);
//
//                    //now disable myself till i remove the joined panel
//                    queryTypeButton.setDisabled(true);
//
//                    //only try and make the window if it's been made before.
//                    if (joinWindow == null) {
//
//                    	//setup my window
//
//                    	//only make the window if it haven't been made before
//                    	joinWindow = new Window();
//	                    joinWindow.setTitle("Join Query Setup");
//	                    joinWindow.setResizable(true);
//	                    joinWindow.setLayout(new FitLayout());
//	                    joinWindow.setWidth(450);
//	                    //make the window not take over the rest of the app
//	                    joinWindow.setModal(false);
//	                    joinWindow.setClosable(false);
//
////	                    //setup an close overide..just hide it
////	                    joinWindow.addListener(new WindowListenerAdapter() {
////	                    	//override the onclose
////	                    	public void onClose(Panel panel) {
////	                    		joinWindow.hide();
////	                    		GWT.log("Window is now hidden and shouldn't be shut..", null);
////	                    	}
////	                    });
//
//	                    //formpanel
//	                    formPanel = new FormPanel();
//	                    formPanel.setFrame(true);
//	                    formPanel.setBorder(true);
//	//                    formPanel.setTitle("Multiple Fields on Row");
//	                    formPanel.setWidth(450);
//	                    formPanel.setAutoHeight(true);
//	                    formPanel.setLabelWidth(100);
//	//                    formPanel.setUrl("save-form.php");
//
//	                    //add a button to start the join
//	                    joinStart = new Button("Continue", new ButtonListenerAdapter() {
//	                    	public void onClick(Button button, EventObject e) {
//
//	                    		//only progress if the form is valid
//	                    		if (formPanel.getForm().isValid()) {
//
//	                    			//start the join process...
//		                    		//1. Align the variables -> that we will join on
//		                    		locationJoin = joinLocation.getValue();
//		                    		datetimeJoin = joinDatetime.getValue();
//		                    		locationJoinFilter = joinLocationQualifier.getValueAsString();
//		                    		datetimeJoinFilter = joinDatetimeQualifier.getValueAsString();
//
//	                    			//make the second panel visibile
//		                            queryPanels[1].columnPanel.show();
//		                            //extend the north panel to show the second UI panel.
//		                            extendQueryPanelHeight();
//
//		                            //hide the window
//		                            joinWindow.hide();
//
//		                            if (locationJoinFilter.equalsIgnoreCase("") && datetimeJoinFilter.equalsIgnoreCase("")) {
//		                            	//notify the user that the join they have selected will cascade the
//			                            //values from the second panel into the first panel
//			                            MessageBox.alert("Leaving the join qualifiers empty means that the join values will equal<br><br>i.e. The Location / Datetime JOIN result of the first panel query will equal the second panel query results.");
//		                            }
////		                            else if (datetimeJoinFilter.equalsIgnoreCase("")) {
////		                            	MessageBox.alert("Leaving the join qualifiers empty means that the join values will equal<br><br>i.e. The Location / Datetime JOIN result of the first panel query will equal the second panel query results.");
////		                            }
//		                            else {
//		                            	//notify the user that the join they have selected will cascade the
//			                            //values from the second panel into the first panel
//			                            MessageBox.alert("The Location / Datetime JOIN result of the first panel query will join against the second panel query results.");
//		                            }
//
//
//	                    		} else {
//	                    			MessageBox.alert("Please verify the filters you have entered");
//	                    		}
//	                    	}
//	                    });
//	                    //disable the button till i set up the join
//	                    joinStart.setDisabled(true);
//
//	                    //add a button to start the join
//	                    final Button joinCancel = new Button("Cancel", new ButtonListenerAdapter() {
//	                    	public void onClick(Button button, EventObject e) {
//	                    		//hide the window..
//	                    		joinWindow.hide();
//
//	                    		//now disable myself till i remove the joined panel
//	                            queryTypeButton.setDisabled(false);
//
//	                    	}
//	                    });
//
//	                    //multi field panel to add the joins
//	                    joinLocationFields = new MultiFieldPanel();
//	                    joinLocationFields.setBorder(false);
//	                    joinLocationFields.setPaddings(5);
//
//	                    //setup the north field
//	                    joinLocation = new Checkbox("Join on Lat / Lon fields", "joinLocation");
//	                    joinLocation.setValue(false);
//	                    joinLocation.setHideLabel(true);
//
//	                    //setup the text field to hold the qualifier on the location
//	                    joinLocationQualifier = new TextField("Location filter", "joinLocationQualifier", 100);
//	                    joinLocationQualifier.setRegex("^(\\d*|\\d*.\\d*)$");
//	                    joinLocationQualifier.setRegexText("Specify a range to measure the lat / lon difference each way :<br>e.g. 0.01");
//	                    joinLocationQualifier.setFieldMsgTarget("qtip");
//	                    joinLocationQualifier.setAllowBlank(true);
//	                    joinLocationQualifier.setDisabled(true);
//	//                    joinLocationQualifier.setHideLabel(true);
//
//	                    //setup a panel to hold the button
//	                    HorizontalPanel buttonPanel = new HorizontalPanel();
//
//	                    //add a listener for the checkbox to turn on the qualifier field
//	                    joinLocation.addListener(new CheckboxListenerAdapter() {
//	                    	public void onCheck(Checkbox field, boolean checked) {
//	                    		//test if we have checked the box
//	                    		if (checked) {
//	                        		//set the text box to be visible
//	                    			joinLocationQualifier.setDisabled(false);
//	                    			//set the button to start the join process
//	                    			joinStart.setDisabled(false);
//	                    		} else {
//	                    			joinLocationQualifier.setDisabled(true);
//	                    			//set the button to start the join process
//	                    			joinStart.setDisabled(true);
//	                    		}
//	                    	}
//	                    });
//
//	                    //add the checkbox & the text box
//	                    joinLocationFields.addToRow(joinLocationQualifier, 220);
//	                    joinLocationFields.addToRow(joinLocation, new ColumnLayoutData(1));
//
//	                    joinDatetimeFields = new MultiFieldPanel();
//	                    joinDatetimeFields.setBorder(false);
//	                    joinDatetimeFields.setPaddings(5);
//
//	                    joinDatetime = new Checkbox("Join on the datetime fields", "joinDatetime");
//	                    joinDatetime.setHideLabel(true);
//
//	                    //create a Store using local array data
//	                    final Store cbStore = new SimpleStore(new String[]{"join_type", "name"}, new String[][]{
//	                    		new String[]{"equals", "Equals Exactly"},
//	                    		new String[]{"bounded_by", "Within Range"} });
//	                    cbStore.load();
//
//	                    //setup a comboBox to select the datetime join
//	                    joinDatetimeQualifier = new ComboBox();
//	                    joinDatetimeQualifier.setForceSelection(false);
//	                    joinDatetimeQualifier.setMinChars(1);
//	                    joinDatetimeQualifier.setFieldLabel("Datetime Join");
//	                    joinDatetimeQualifier.setStore(cbStore);
//	                    joinDatetimeQualifier.setDisplayField("name");
//	                    joinDatetimeQualifier.setMode(ComboBox.LOCAL);
//	                    joinDatetimeQualifier.setTriggerAction(ComboBox.ALL);
////	                    joinDatetimeQualifier.setEmptyText("Enter state");
////	                    joinDatetimeQualifier.setLoadingText("Searching...");
//	                    joinDatetimeQualifier.setTypeAhead(true);
//	                    joinDatetimeQualifier.setSelectOnFocus(true);
//	                    joinDatetimeQualifier.setWidth(100);
//	                    joinDatetimeQualifier.setHideTrigger(false);
//	                    joinDatetimeQualifier.setDisabled(true);
//
//	                    //TODO: Extend this to allow YEAR / MONTH / DAY selections to include ADD THIS TO THE DATETIME PANEL
//	                    //setup the text field to hold the qualifier on the location
////	                    joinDatetimeQualifier = new TextField("Datetime filter", "joinLocationQualifier", 100);
////	                    joinDatetimeQualifier.setRegex("^\\d{4}-\\d{2}-\\d{2} \\d{4}-\\d{2}-\\d{2}$");
////	                    joinDatetimeQualifier.setRegexText("Specify a qualifier for the datetime filter : <br>e.g. All February Months :<br>200\\d+?-02-\\d{2}?\\ 200\\d+?-02-\\d{2}?");
////	                    joinDatetimeQualifier.setFieldMsgTarget("qtip");
////	                    joinDatetimeQualifier.setAllowBlank(true);
////	                    joinDatetimeQualifier.setDisabled(true);
//
//	                    //add a listener for the checkbox to turn on the qualifier field
//	                    joinDatetime.addListener(new CheckboxListenerAdapter() {
//	                    	public void onCheck(Checkbox field, boolean checked) {
//	                    		//test if we have checked the box
//	                    		if (checked) {
//	                        		//set the text box to be visible
//	                    			joinDatetimeQualifier.setDisabled(false);
//	                    			//set the button to start the join process
//	                    			joinStart.setDisabled(false);
//	                    		} else {
//	                    			joinDatetimeQualifier.setDisabled(true);
//	                    			joinStart.setDisabled(true);
//	                    		}
//	                    	}
//	                    });
//
//	                    //add the checkbox & the text box
//	                    joinDatetimeFields.addToRow(joinDatetimeQualifier, 220);
//	                    joinDatetimeFields.addToRow(joinDatetime, new ColumnLayoutData(1));
//
//	                    //add the multipanel to the form panel
//	                    formPanel.add(joinLocationFields);
//	                    formPanel.add(joinDatetimeFields);
//
//	                    //add the buttons to the panel
//	                    buttonPanel.add(joinStart);
//	                    buttonPanel.add(joinCancel);
//
//	                    //add the button
//	                    formPanel.add(buttonPanel);
//
//	                    //add the fields to the window
//	                    joinWindow.add(formPanel);
//                    }
//                    else { //reset the join window
//                    	//reset the text fields
//                    	joinLocationQualifier.reset();
////                    	joinDatetimeQualifier.reset();
//                    	//uncheck the boxes
//                    	joinLocation.reset();
//                    	joinDatetime.reset();
//                    	//reset the button
//                    	joinStart.setDisabled(true);
//                    }
//                    //show the popup window
//                    joinWindow.show();
//                }
//            }
//        };
//
//        //union
//        union.setId("union");
//        union.setText("As a Union Query");
//        union.setChecked(false);
//        union.addListener(listener);
//        menu.addItem(union);
//        //now the join
//        join.setId("join");
//        join.setText("As a Join Query");
//        join.setChecked(false);
//        join.addListener(listener);
//        menu.addItem(join);
//
//        this.queryTypeButton.setMenu(menu);
////        this.queryTypeButton.setIconCls("bmenu");
//        queryToolbar.addButton(this.queryTypeButton);
//        queryToolbar.addSeparator();

//        //add the join query button
//        queryToolbar.addButton(new ToolbarButton("Add Extra Query Panel", new ButtonListenerAdapter() {
//            public void onClick(Button button, EventObject e) {
//
////            	date.setTime(System.currentTimeMillis());
////    			GWT.log(dtf.format(date) + " About to setup the extra query field...", null);
////    			GWT.log(dtf.format(date) + " : " + String.valueOf(uiPanelCount), null);
//
//            	//max out at 2 panels at the moment && test if i have setup the other panel before
////            	if (uiPanelCount < 2 && queryPanels[uiPanelCount+1] == null) {
////    			if (uiPanelCount < 2) {
//    			//TODO: make this dynamic -> can add as many as we want.
//            		//show the second query field
//            		queryPanels[1].columnPanel.show();
////            	}
//            }
//        }));

//      //add the join query button
//        queryToolbar.addButton(new ToolbarButton("Remove Extra Query Panel", new ButtonListenerAdapter() {
//            public void onClick(Button button, EventObject e) {
//
//            	try {
//
//            		//hide the panel if it's visible
//            		if (queryPanels[1].columnPanel.isVisible()){
//
//		                //max out at 2 panels at the moment.
//		//            	if (uiPanelCount != 0) {
//		                    //hide the query panel
//		            	//clear the query panel we are about to hide
//		            	clearQueryUI(1);
//		                queryPanels[1].columnPanel.hide();
//		                //renable my query add type menu button
//		                queryTypeButton.setDisabled(false);
//		                queryType = false;
//		                //reset the query tyope
//		                //clear the last type of query
//	//	                join.setChecked(false);
//	//	                union.setChecked(false);
//		//            	}
//            		}
//            	} catch (Exception ex) {
//            		ex.printStackTrace();
//            	}
//            }
//        }));
//        queryToolbar.addSeparator();

        //now set the toolbar
        northPanel.setTopToolbar(queryToolbar);

        //setup a layout for the north panel
        northData = new BorderLayoutData(RegionPosition.NORTH);
        northData.setMinSize(250);
        northData.setMaxSize(500);
        northData.setMargins(new Margins(1, 1, 1, 1));
        northData.setSplit(true);
        borderPanel.add(northPanel, northData);
        //end north panel
    }
    
    /**
     * JSNI method to export transform a data grid into and XLS file and push to the client.
     * @param GridPanel to transform to Excel file.
     */
    private native void export(GridPanel x) /*-{
		try {
			//transform the grid to Excel file and push to client.
			//TODO: Only export the selected rows.
	    	//document.location='data:application/vnd.ms-excel;base64,' + $wnd.Base64.encode(x.@com.gwtext.client.widgets.grid.GridPanel::exportExcelXml()());
//			window.open('data:application/vnd.ms-excel;base64,' + $wnd.Base64.encode(x.@com.gwtext.client.widgets.grid.GridPanel::exportExcelXml()()));
		} catch (err) {
			window.alert("Error trying to export out the grid panel: " + err.name + " : " + err.message + " : " + err.description);
		}
	}-*/;
   

    /**
     * Setup the south border panel components
     */
    private void setupSouthPanel() {
//        //add south panel
//        southPanel = new Panel();
//        southPanel.setBorder(false);
//        southPanel.setHeight(250);
//        southPanel.setCollapsible(true);
//        southPanel.setTitle("DATA VIEW");
//        //southPanel.setAutoScroll(true);
//        southPanel.setLayout(new FitLayout());

        southPanel = new TabPanel();
//        southPanel.setTitle("DATA VIEW");
        southPanel.setHeight(450);
        southPanel.setCollapsible(true);

        Toolbar bottomToolbar = new Toolbar();
        bottomToolbar.addFill();
        bottomToolbar.addSpacer();

        //add the export button. 
        bottomToolbar.addButton(new ToolbarButton("Export to Excel", new
    		ButtonListenerAdapter() {
		            public void onClick(Button button, EventObject e)
		            {
		            	//export the currently active tab
		                try {
		                	export((GridPanel)southPanel.getActiveTab().getComponent(0));
		                } catch (Exception er) {
		                	GWT.log("Got an error",null);
//		                	er.printStackTrace();
		                }
		            }
        }));

//        //add the edit button
//        bottomToolbar.addButton(new ToolbarButton("Chart Data", new ButtonListenerAdapter() {
//            public void onClick(Button button, EventObject e) {
//                //show the chart panel
//            	centrePanel.setActiveTab("chartTab");
//            	//MessageBox.alert("I should see the chart showing up here??");
//            	//create the chart
////                LineChart chart = chartData();
//                //add the chart to the panel
////                chartPanel.add(chart);
//            }
//        }));

//      //add the edit button
//        bottomToolbar.addButton(new ToolbarButton("Store Size", new ButtonListenerAdapter() {
//            public void onClick(Button button, EventObject e) {
//                //show me this size of the active tabs store array.
//                String[] id_parts = southPanel.getActiveTab().getId().split("_");
//                MessageBox.alert("The size of the " + southPanel.getActiveTab().getId() + "store is : " + stores[Integer.parseInt(id_parts[1])].getCount());
//            }
//        }));

//        //add the edit button
//        bottomToolbar.addButton(new ToolbarButton("Show Sat Images", new ButtonListenerAdapter() {
//            public void onClick(Button button, EventObject e) {
////             try{
////            	 //show me this size of the active tabs store array.
////                String[] id_parts = southPanel.getActiveTab().getId().split("_");
////                //setup my columns..
////                String[] columns = new String[] {};
////                //find out if the grid id has sat images..
////                //get all the columns...
////                try {
////                	//get the fields of this store..
//////                	columns = stores[Integer.parseInt(id_parts[1])].getFields();
////                	columns = StoreMgr.lookup(id_parts[1]).getFields();
////                }
////                catch (NumberFormatException nfe) {
////                	nfe.printStackTrace();
////                }
////
////              //loop over the columns looking for the target filed..
////                for (String field : columns) {
////                	//test if the characterisitic column is set and get these ones...
//////                	stores[Integer.parseInt(id_parts[1])].find(propname, value, startIndex, anymatch, casesensitive)
//////                	stores[Integer.parseInt(id_parts[1])].
//////                StoreTraversalCallback stb = new StoreTraversalCallback();
////                	GWT.log("Found the store and this is the data...:" + field, null);
////                }
////
////                //what i want is to query the store looking for the
////                Record[] matches = StoreMgr.lookup(id_parts[1]).query("characteristic", "[Sea Surface Temperature|Chlor_a|Zeu]");
////
////                //loop over all the matched records
////                for (Record match : matches) {
////                	//what have i got..in terms of characteristics
////                	GWT.log("Record characteristic here is: " + match.getAsString("characteristic"), null);
////                	GWT.log("Record measurement here is: " + match.getAsString("measurment"), null);
////                }
////            } catch (Exception ee) {
////            	ee.printStackTrace();
////            }
//                //I will have to execute a bunch of queries to return all the sat images associated with a grid..
//
//                //create a server to take a list of measuremnt ID's and execute multiple sat image queries against the data store.
////                each time after execution -> it will record the URL of the store and return a list of these upon completion.
//
//
//                //if this returns a bunch of records which have the type whcih i know to be sat imgaes....
//                //then now i need to extract the relevant URL for each image.
//                //
//                //TO Extract my Sat URL data
////                ----
////                1. get my measurment instance
////                2. get all workflow associated with the measure instance and extract the has_input slot -> get the associated input, which should be unique).
////                3. from there query every workflow that has input from 2 and output of rdf:type record:Satellite_Image).
//                //get the associated measurment instance..Should be included with every query..
//                //setup a query to get the SAT workflows with all has_input slots...
//
////TODO: How to query on the measurement instance?
////                Query WILL BE :
//                //1. select DISTINCT ?satimage {
////                		?satimage rdf:type record:Satellite_Image
////                		?workflow event:has_output_product ?satimage
////                		?workflow event:has_input ?inputfile
////                		?workflow1 event:has_input ?inputfile
////                		?workflow1 event:has_output_product http://maenad.itee.uq.edu.au/metadata/Event#(MeaseruID from above).
////                		?workflow1 rdf:type event:Data_Workflow .
////            		}
//
//                //IS there a hack i can do to make it appear that i have the data?
//                //can i just overlay a known set of data...as a show of what it can do.
//
//                //I.e. for the query about low chlorphyll..get the results..and overlay all known images for that one.in a loop
//                //http://maenad.itee.uq.edu.au/metadata/M-L3-GBR-1M-1M-CHLOR-D-SGBR-1M-2002182-2002212_monthly.png
//                //Create a ground overlay
//
//            	//so i need to find out all the images that would correllate to the query
//            	//create them in a loop and then setup my callback to switch them every 5 secs or so...
//
//            	//if the first overlay hasn't been set then set them up.
//                if (overlays[0] == null) {
//
//                	//setup a boundary
//                	LatLngBounds imageBounds = LatLngBounds.newInstance(LatLng.newInstance(S,W), LatLng.newInstance(N,E));
//
//                	for (int i=0; i < images.length; i++) {
////                		String imageURL = "http://maenad.itee.uq.edu.au/metadata/images/M-L3-GBR-1M-1M-CHLOR-D-SGBR-1M-2008336-2008366_monthly.png";
////                		String imageURL = "http://maenad.itee.uq.edu.au/metadata/images/M-L3-GBR-1M-1M-SST-N-SGBR-1M-2009091-2009120_monthly.png";
//                		//SW, NE -> ROI 149.5 -> 153 ; 21.5 -> 24.5
//                		// == -24.5,149.5
//
//                		//TODO move away from static lookup of the image URL
//                		//GWT.log("Setup overlay : " + i + " : " + images[i], null);
//
//                		//add it to the array of overlays
//                		overlays[i] = new GroundOverlay(images[i], imageBounds);
//                		//hide all of my overlays by default
//                		overlays[i].setVisible(false);
//
//                		//add the first overlay to the map.
//                    	map.addOverlay(overlays[i]);
//
////                		groundOverlay.addGroundOverlayVisibilityChangedHandler(new GroundOverlayVisibilityChangedHandler() {
////                      public void onVisibilityChanged(
////                          GroundOverlayVisibilityChangedEvent event) {
//////                        if (event.isVisible()) {
//////                          hideButton.setText("Hide Overlay");
//////                        } else {
//////                          hideButton.setText("Show Overlay");
//////                        }
////                      }
////                    });
//                	}
//
//                	//make the first one visible
//                	overlays[0].setVisible(true);
//
//                	//kick off my overlaycounter
//                	overlayCounter++;
//
//                	//set the timer loop the images -=> will set the one by one to be visible.
//                	imageLoopTimer.scheduleRepeating(5000);
//
//                } else {
//                	Boolean visible = false;
//                	//are they all hidden
//                	for (int i=0; i < images.length; i++) {
//                		//are they all hidden?
//                		if (overlays[i].isVisible()) {
//                			visible = true;
//                		}
//                	}
//                	if (visible) {
//                		//hide them all -> this needs to change
//	                	for (int i=0; i < images.length; i++) {
//	                		//hide them all
//	                    	overlays[i].setVisible(false);
//	                    	//cancel my timer
//	                    	imageLoopTimer.cancel();
//	                	}
//                	} else {
//                		//make the first one visible
//                    	overlays[0].setVisible(true);
//                    	//reset my overlay counter.
//                		overlayCounter = 1;
//                		//start the loop again
//                    	imageLoopTimer.scheduleRepeating(5000);
//                	}
//
//                	//switch between hide and show
////                	groundOverlay.setVisible(!groundOverlay.isVisible());
//                }
//            }
//        }));

        //add the toolbar
        southPanel.setBottomToolbar(bottomToolbar);
        
        southPanel.setId("SouthPanel");

        BorderLayoutData southData = new BorderLayoutData(RegionPosition.SOUTH);
        southData.setMinSize(100);
        southData.setMaxSize(600);
        southData.setMargins(new Margins(0, 0, 5, 0));
        southData.setSplit(true);
        borderPanel.add(southPanel, southData);
        
        //add the remove tab listener
		//note: tabs don't close they are just removed -> See PanelListerner.class :: OnClose()
		this.southPanel.addListener(new ContainerListenerAdapter() {
			public void onRemove(Container self, Component component) {

				if (gridCounter > 0) {
        			//decrease the grid counter
					gridCounter--;
        		}
				
				//if on my last tab
				if (gridCounter == 0) {
					//collapse the data grid panel back up
					southPanel.collapse();
					//now expand my query grids
					northPanel.expand();
					eastPanel.expand();
        		}
		    }

//			public void onAdd(Container self, Component component, int index) {
//				//set the newly added component to be the active tab
////				southPanel.activate(component.getId());
//				GWT.log("The container!: " + self.getId(), null);
//				GWT.log("The container index!: " + index, null);
//				southPanel.setActiveTab(index);
//			}
		});

        //hide this panel till it is ready for use with query data.
        southPanel.collapse();
        //end south panel
    }
    
    /**
     * Setup the west border panel components
     */
    private void setupWestPanel() {
        //add west panel
    }

    /**
     * Setup the centre border panel components
     */
    private void setupCentrePanel() {
        //add centre panel
        //final TabPanel centrePanel = new TabPanel();
        centrePanel = new TabPanel();
        centrePanel.setDeferredRender(false);
          centrePanel.setTitle("Center Panel");
//        centrePanel.setCollapsible(true);
        //add our own listener on tab change to resize the map components
        centrePanel.addListener
        (
          new TabPanelListenerAdapter()
          {
            public void onTabChange( TabPanel source, Panel tab )
            {
                //make sure the maps size correctly before we show them
                map.checkResize();
            }
          }
        );

        // try a GWT panel and then look at GWT CSS problems
        mapPanel = new Panel();
        mapPanel.setLayout(new FitLayout());
        mapPanel.setId("mapTab");
        //now make the map
        //map = new MapWidget(LatLng.newInstance(-22.5,154.0), 6);
        map = new MapWidget(LatLng.newInstance(-18.5,146.0), 7);
        //146.5, -17.5
        //set it to be an Earth map
        map.setCurrentMapType(MapType.getSatelliteMap());
        //map.setCurrentMapType(MapType.getEarthMap());
        //add the types of map buttons -not used
        map.addMapType(MapType.getEarthMap());
        map.addMapType(MapType.getPhysicalMap());
        //add controls
        //map.addControl(new LargeMapControl());
        map.addControl(new SmallMapControl());
        map.addControl(new MapTypeControl());
        map.setScrollWheelZoomEnabled(true);
        //map.addControl(new OverviewMapControl());

        //OVERLAY FOR CLICK HANDLER AND DRAWING A BOUNDING BOX.
//http://groups.google.gg/group/Google-Maps-API/browse_thread/thread/41aa4cd10e355531/0235915d81502692?lnk=gst&q=draw+rectangle+#0235915d81502692
//	     // boundsBox()
//	     // Returns a GPolygon rectangle
//	     // The first parameter is a GLatLngBounds object that defines the
//	     rectangle
//	     // Parameters 2...6 are standard GPolygon style parameters)
//
//	     function boundsBox(source, liColor, liWidth, liOpa, fillColor,
//	     fillOpa)
//	     {
//	     var northEast = source.getNorthEast();
//	     var southWest = source.getSouthWest();
//	     var topLat = northEast.lat();
//	     var rightLng = northEast.lng();
//	     var botLat = southWest.lat();
//	     var leftLng = southWest.lng();
//	     var boxPoints = [];
//	     boxPoints.push(southWest);
//	     boxPoints.push(new GLatLng(topLat,leftLng));
//	     boxPoints.push(northEast);
//	     boxPoints.push(new GLatLng(botLat,rightLng));
//	     boxPoints.push(southWest);
//	     fillColor = fillColor||liColor||"#0055ff";
//	     liWidth = liWidth||2;
//	     var boxPoly = new
//	     GPolygon(boxPoints,liColor,liWidth,liOpa,fillColor,fillOpa);
//	     return boxPoly;
//
//	     }


//        map.addMapDragStartHandler(new MapDragStartHandler(){
//        	public void onDragStart(MapDragStartEvent dragStart){
//
//        	}
//        });

//        final LatLng pointNE = LatLng.newInstance(-21.0, 152.0);
//        final LatLng pointNW = LatLng.newInstance(-21.0, 150.0);
//        final LatLng pointSW = LatLng.newInstance(-22.0, 150.0);
//        final LatLng pointSE = LatLng.newInstance(-22.0, 152.0);
//
//        GroundOverlay groundOverlay = new GroundOverlay("http://bbs.keyhole.com/ubb/z0302a1700/etna.jpg", LatLngBounds.newInstance(pointSW, pointNE));
//
//        map.addMapRightClickHandler(new MapRightClickHandler() {
//        	public void onRightClick(MapRightClickEvent e){
//
//
////        		//draw a rectangle where i right click
////        		Polygon rectangle = new Polygon(new LatLng[]{pointNE, pointNW, pointSW, pointSE, pointNE}, "#888888", 5, 0.8, "#888888", 0.2);
////                //rectangle.setEditingEnabled(true);
////                rectangle.addPolygonClickHandler(new PolygonClickHandler(){
////                	public void onClick(PolygonClickEvent e){
////                		MessageBox.alert("Clicked on the polygon");
////                	}
////
////                });
////                map.addOverlay(rectangle);
//        	}
//        });
//        map.addOverlay(groundOverlay);

        //attach to the flow panel
        mapPanel.add(map);
        mapPanel.setTitle("Google Maps");

        //now a panel to edit sparql queries
        final Panel sparqlPanel = new Panel();
        sparqlPanel.setLayout(new FitLayout());
        sparqlPanel.setTitle("Query Viewer");
        sparqlPanel.setId("queryEditTab");
        //sparqlPanel.setPaddings(1);
        sparqlPanel.setClosable(false);
        sparqlPanel.setFrame(false);

        //add another panel to hold the query data
        queryPanel = new Panel();
        queryPanel.setLayout(new FitLayout());
        queryPanel.setTitle("intial setup");
        queryPanel.setId("querywrapperpanel");
        queryPanel.setFrame(false);
        queryPanel.setClosable(false);

        queryField = new TextArea();
        queryPanel.add(queryField);
        sparqlPanel.add(queryPanel);

//        //a panel for displaying charts
//        chartPanel = new Panel();
//        chartPanel.setLayout(new VerticalLayout(15));
//        chartPanel.setTitle("Charts");
//        chartPanel.setId("chartTab");
//        chartPanel.setPaddings(10);
//        chartPanel.setClosable(false);
//        chartPanel.setFrame(false);
//        chartPanel.setVisible(false);

//// -------------------
//         MemoryProxy proxy = new MemoryProxy(getData());
//         RecordDef recordDef = new RecordDef(
//                 new FieldDef[]{
//                         new StringFieldDef("month"),
//                         new FloatFieldDef("rent"),
//                         new FloatFieldDef("utilities")
//                 }
//         );
//
//         ArrayReader reader = new ArrayReader(recordDef);
//         final Store store = new Store(proxy, reader);
//         store.load();
//
//         SeriesDefY[] seriesDef = new SeriesDefY[]{
//
//                 new SeriesDefY("Rent", "rent"),
//                 new SeriesDefY("Utilities", "utilities")
//
//         };
//
//         NumericAxis currencyAxis = new NumericAxis();
//         currencyAxis.setMinimum(800);
//         currencyAxis.setLabelFunction("formatCurrencyAxisLabel");
//         final LineChart chart = new LineChart();
//         chart.setTitle("Monthly Expenses");
//         chart.setWMode("transparent");
//         chart.setStore(store);
//         chart.setSeries(seriesDef);
//         chart.setXField("month");
//         chart.setYAxis(currencyAxis);
//         chart.setDataTipFunction("getDataTipText");
//         chart.setExpressInstall("js/yui/assets/expressinstall.swf");
//         chart.setWidth(500);
//         chart.setHeight(400);
//
//        chartPanel.add(chart);
//
//// -------------------

        //attach to the tab panel
        centrePanel.add(mapPanel);
        centrePanel.add(sparqlPanel);
//        centrePanel.add(chartPanel);

        BorderLayoutData centerData = new BorderLayoutData(RegionPosition.CENTER);
        centerData.setSplit(true);
        centerData.setMinSize(250);
        centerData.setMaxSize(800);
        centerData.setMargins(new Margins(0, 2, 2, 0));

        borderPanel.add(centrePanel, centerData);
        //end centre panel
    }

    /**
     * Setup the east border panel components
     */
    private void setupEastPanel() {
        //add east panel
        this.eastPanel = new Panel();
        this.eastPanel.setTitle("Pre-Defined Queries");
        this.eastPanel.setBorder(false);
        //this.eastPanel.setId("eastPanel");
        this.eastPanel.setCollapsible(true);
        this.eastPanel.setWidth(325);
        this.eastPanel.setAutoScroll(false);
        this.eastPanel.setLayout(new FitLayout());

        //Toolbar for the grid - used to load the relevant queries.
        Toolbar exampleToolbar = new Toolbar();
        exampleToolbar.addFill();
        exampleToolbar.addSpacer();

        //add the clear query results button
        exampleToolbar.addButton(new ToolbarButton("Load Example Query", new ButtonListenerAdapter() {
            public void onClick(Button button, EventObject e) {
                loadExampleQuery();
            }
        }));

        //add the clear query UI button
        exampleToolbar.addButton(new ToolbarButton("Clear Loaded Query", new ButtonListenerAdapter() {
            public void onClick(Button button, EventObject e) {
                //clear the query UI
                clearQueryUI();
            }
        }));

        //add the toolbar
        this.eastPanel.setBottomToolbar(exampleToolbar);

        BorderLayoutData eastData = new BorderLayoutData(RegionPosition.EAST);
//        eastData.setSplit(true);
//        eastData.setMinSize(225);
//        eastData.setMaxSize(225);
        eastData.setMargins(new Margins(0, 5, 5, 0));
        this.borderPanel.add(this.eastPanel, eastData);
        //end east panel
    }

    /**
     * Used to populate the Query Grid Panel
     * QueryGrid is populated with a result set from a server object.
     */
    private void setupQueryExampleGridPanel() {

        //setup the grid for data viewing
        this.rowSelectionModel = new RowSelectionModel();
        
        //setup what fields i have in my XML file.
        RecordDef queryRecordDef = new RecordDef(
                new FieldDef[]{
                        new StringFieldDef("north"),
                        new StringFieldDef("south"),
                        new StringFieldDef("east"),
                        new StringFieldDef("west"),
                        new StringFieldDef("lat"),
                        new StringFieldDef("lon"),
                        new StringFieldDef("start_date"),
                        new StringFieldDef("end_date"),
                        new StringFieldDef("target"),
                        new StringFieldDef("characteristic"),
                        new StringFieldDef("location"),
                        new StringFieldDef("actor"),
                        new StringFieldDef("measurement_value"),
                        new StringFieldDef("unit"),
                        new StringFieldDef("research_program"),
                        new StringFieldDef("ecological_process"),
                        new StringFieldDef("process_output"),
                        new StringFieldDef("genus"),
                        new StringFieldDef("species"),
                        new StringFieldDef("morphology"),
                        new StringFieldDef("depth"),
                        new StringFieldDef("sensor"),
                        new StringFieldDef("description"),
                        new StringFieldDef("north1"),
                        new StringFieldDef("south1"),
                        new StringFieldDef("east1"),
                        new StringFieldDef("west1"),
                        new StringFieldDef("lat1"),
                        new StringFieldDef("lon1"),
                        new StringFieldDef("start_date1"),
                        new StringFieldDef("end_date1"),
                        new StringFieldDef("target1"),
                        new StringFieldDef("characteristic1"),
                        new StringFieldDef("location1"),
                        new StringFieldDef("actor1"),
                        new StringFieldDef("measurement_value1"),
                        new StringFieldDef("unit1"),
                        new StringFieldDef("research_program1"),
                        new StringFieldDef("ecological_process1"),
                        new StringFieldDef("process_output1"),
                        new StringFieldDef("genus1"),
                        new StringFieldDef("species1"),
                        new StringFieldDef("morphology1"),
                        new StringFieldDef("depth1"),
                        new StringFieldDef("sensor1"),
                        new StringFieldDef("location_filter"),
                        new StringFieldDef("datetime_filter"),
                    	new StringFieldDef("join_query"),
                    	new StringFieldDef("join_query_datetime"),
                    	new StringFieldDef("join_query_location"),
                        new StringFieldDef("query_string")
                }
        );

        //setup the XML reader for my store
        XmlReader reader = new XmlReader("query", queryRecordDef);

        //setup the store
        Store queryStore = new Store(reader);
        //load the XML data
        queryStore.loadXmlDataFromUrl(this.appContext + "Resources/Configuration_Files/SQL_Queries.xml", false);
//        queryStore.loadXmlDataFromUrl(this.appContext + "cresis/Resources/Configuration_Files/Queries.xml", false);

        //setup my tootip renderer
        Renderer tooltipRenderer = new Renderer() {
            public String render(Object value, CellMetadata cellMetadata, Record record, int rowIndex, int colNum, Store store) {
//              cellMetadata.setHtmlAttribute("ext:qtip=\"" + value +  "\" ext:qtitle=\"Title\"");
            	cellMetadata.setHtmlAttribute("ext:qtip=\"" + value +  "\" ext:qtitle=\"\"");
              return (String) value;
            }
        };

        //setup the columns i want to display
        ColumnConfig[] queryColumns = new ColumnConfig[]{
            //only want to show the actual description field but have the other fields on hand in the grid.
            new ColumnConfig("Description", "description", 350, true, tooltipRenderer, "description"),
//        		new ColumnConfig("Description", "description", 350, true, null, "description"),
        };

        //setup the column and selection models
        ColumnModel queryColumnModel = new ColumnModel(queryColumns);

        //setup the view for the grid
        GridView queryGridView = new GridView();
        queryGridView.setScrollOffset(1);
//        queryGridView.setAutoFill(true);
//        queryGridView.setForceFit(true);

        //now setup the grid panel
        this.queryGrid = new GridPanel();
        this.queryGrid.setStore(queryStore);
        this.queryGrid.setColumnModel(queryColumnModel);
        this.queryGrid.setSelectionModel(rowSelectionModel);
        this.queryGrid.setStripeRows(true);
        this.queryGrid.setAutoExpandColumn("description");
        this.queryGrid.setWidth("500");
        this.queryGrid.setAutoScroll(true);
        this.queryGrid.setView(queryGridView);
        //queryGrid.setHideColumnHeader(true);
        this.queryGrid.setEnableHdMenu(false);
        
        //add the dbl click listener
        this.queryGrid.addGridListener(new GridListenerAdapter() {
            public void onDblClick(EventObject e) {
                //load the query we have selected.
                loadExampleQuery();
            }
        });

        //add the grid to the panel
        this.eastPanel.add(this.queryGrid);
      
    }

    /**
     * Method to create the data grid model based on the executed query result set variables.
     * @param String XML Result set file path
     * @param ArrayList<String> the query variables corresponding to the grid columns.
     */
    private void setupDataPanel(String xmlFilePath, ArrayList<String> queryVars) {

    	try {
    		
	        //setup a panel to hold the new grid we want.
	        Panel dataGridPanel = new Panel();
	        dataGridPanel.setId("ResultSet_" + this.gridCounter);
	        dataGridPanel.setTitle("ResultSet_" + this.gridCounter);
	        dataGridPanel.setClosable(true);
	        dataGridPanel.setAutoScroll(true);
//	        dataGridPanel.addListener(new PanelListenerAdapter() {
//	        	public void onDeactivate(Panel panel) {
//	        		if (gridCounter != 0) {
//	        			//decrease the grid counter
//		        		String[] grid_id = panel.getId().split("_");
//		        		GWT.log("I removed a tab! counter : " + gridCounter, null);
//		        		GWT.log("Component ID " + panel.getId(), null);
//		        		southPanel.remove(panel, true);
//	        		}
//	        	}
//	        	
//	        });
	       
	        //TODO: Add a toolbar which has the sum button..
	        //TODO: If i can do this i can do the average..
	        //ie. for each grid we will take the grid id and choose a column to sum on
	        //Listener will get grid id -> ask for a field (maybe a cell click)? might just use a drop down..based on fields we have here.
	        //and then sum these fields..
	        //this will take care of the abundance query...perhaps.

	        //tester -> see if the copy function is the problem..
	        ArrayList<String> queryVariables = queryVars;

	        this.date.setTime(System.currentTimeMillis());
	        GWT.log(this.dtf.format(date) + " " + this.toString() + " :: Setting up the data Grid XML Panel -> " + this.appContext+xmlFilePath, null);

	        //setup the grid for data viewing
	        final CheckboxSelectionModel cbSelectionModel = new CheckboxSelectionModel();
	        
	        //setup my field definition array - with the correct size
	        FieldDef[] fields = new FieldDef[queryVariables.size()];

	        //create the FieldDef with the releavant query variables for reading the XML file...
	        //since i make these off the result set these should correlate 100%
	        for (int i=0; i < queryVariables.size(); i++){

	        	//get a lower case version for comparison
	            String field = queryVariables.get(i).toLowerCase();
//	            GWT.log(field, null);

	            //since we can't use switch...we must use a bunch of compares
	            if (field.equalsIgnoreCase("lat") || field.equalsIgnoreCase("lon") || field.equalsIgnoreCase("lat_end") || field.equalsIgnoreCase("lon_end") || field.equalsIgnoreCase("value")) {
	                //use the float field type
	                fields[i] = new FloatFieldDef(field);
	            }
	        	if (field.equalsIgnoreCase("startdate") || field.equalsIgnoreCase("enddate")) {
	        		//make the date fields with the format
	            	//expected format -- 2006-08-15 09:00:00
	        		fields[i] = new DateFieldDef(field, "Y-m-d H:i:s");
//	        		GWT.log("Field i am converting : " + field ,null);
	        		//TODO get the grid to accept my date formats
//	        		//When i change my XSLT to do the SAX parser (Woodstax) -> whenever i come across one of these -> change the format.. so my date can be accepted.
	        	}
	            else {
	                //default to a string field...
	                fields[i] = new StringFieldDef(field);
	            }
	        }

	        //setup the XML reader for my store
	        XmlReader reader = new XmlReader("record", new RecordDef(fields));

	        //setup a grouping sotre
	//        this.store = new GroupingStore(dataProxy, reader);
	        GroupingStore store = new GroupingStore(reader);
	        //set my store id for later lookups
	        store.setStoreId("Store_" + this.gridCounter);
	        //if we have lat use this as the default sort order
	        if (queryVariables.contains("lat")){
	            store.setSortInfo(new SortState("lat", SortDir.ASC));
	            //and set the default grouping field
	            store.setGroupField("lat");
	        } else {
	            //otherwise run of the first column for sorting and grouping
	            store.setSortInfo(new SortState(queryVariables.get(0), SortDir.ASC));
	            store.setGroupField(queryVariables.get(0));
	        }

	        //TODO: Move to the server based paging store...as well.
	        //note: there is an append option to the store!
	        store.loadXmlDataFromUrl(this.appContext+xmlFilePath, false);
	             
	        //reset my counter -> skip the first column which is the checkbox
	        int columnCounter = 1;

	        //setup the column configs -> Don't forget to add the checkbox column
	        BaseColumnConfig[] columns = new BaseColumnConfig[queryVariables.size()+1];

	        //add the checkbox column to be the first colum
	        columns[0] = new CheckboxColumnConfig(cbSelectionModel);
	        //update the dataIndex to fit with the EXTJS ExportToExcel.js code.
//	        cbColumnConfig.setDataIndex("checker");
//	        columns[0] = cbColumnConfig;
	        	        
	        //loop over my ordered list of the known query variables
	        Set<Entry<String, String[]>> sortedColumns = this.columnMap.entrySet();
	        
	        //obtain an Iterator for my LinkedHash
	        Iterator<Entry<String,String[]>> itr = sortedColumns.iterator();
	       
	        //iterate through LinkedHashMap values iterator
	        //using the sort order of the hash to add columns to data grid in order.
	        while(itr.hasNext()) {
	        	//get each map etnry
	        	Entry<String, String[]> tempColumn = itr.next();
	        	if (queryVariables.contains(tempColumn.getKey())) {
	        		//get the known config for this column...and assign to the columnConfig.
	        		String[] config = tempColumn.getValue();
	        		columns[columnCounter++] = new ColumnConfig(config[0], config[1], Integer.valueOf(config[2]), Boolean.parseBoolean(config[3]), null, config[5]);
                } 
	        }
	        
	        //now convert the queyvars to a set
	        List<String> queryVarSet = queryVariables.subList(0, queryVariables.size());
	        //get the set difference between the query vars and the column keys
	        queryVarSet.removeAll(this.columnMap.keySet());
	        
//	        //obtain an Iterator for Collection
//	        Iterator<String> varItr = queryVarSet.iterator();
//	     
//	        //iterate through LinkedHashMap values iterator
//	        while(varItr.hasNext()) {
//	        	String temp = varItr.next();
//	        	System.out.println(temp);
//	        }	        
	        
	        //only process unknown vars if we have them.
	        if (queryVarSet.size() > 0) {
	        	
//	        	//debug
//	        	GWT.log("Found some unknown vars: " + queryVarSet.size() + "....working on them now.", null);
	        	
	        	//now loop over my difference set
		        for (int i=1; i <= queryVarSet.size(); i++) {
		        	//get my variable name
		        	String tempVarName = queryVarSet.get(i-1);
	        		//now insert the new columnConfig at the correct point, where the null(s) should be. 
	        		columns[columns.length-i] = new ColumnConfig(tempVarName, tempVarName, 40, true, null, tempVarName);
	        	}	        	
	        }
        	
	        //now create the column model
	        ColumnModel columnModel = new ColumnModel(columns);

	        //setup the grouping view
	        GroupingView gridView = new GroupingView();
	        gridView.setGroupTextTpl("{text} ({[values.rs.length]} {[values.rs.length > 1 ? \"Items\" : \"Item\"]})");
	        gridView.setScrollOffset(0);
	        gridView.setAutoFill(true);
	        gridView.setForceFit(true);

	        //make the final grid panel
	        GridPanel grid = new GridPanel();
	        grid.setId("Grid_" + this.gridCounter);
	        grid.setLayout(new FitLayout());
	        grid.setStore(store);
	        grid.setColumnModel(columnModel);
	        grid.setSelectionModel(cbSelectionModel);
	        grid.setStripeRows(true);
	        //test if we have the lat set..
	        if (queryVariables.contains("lat")){
	            grid.setAutoExpandColumn("lat");
	        } else {
	            grid.setAutoExpandColumn(0);
	        }
	        grid.setAutoScroll(true);
	        grid.setView(gridView);

	        //set the title of the data grid based on the location name...
	        grid.setTitle(markerName + " " + markerLat + " " + markerLon);
	//        if (queryVariables.contains("locname") && queryVariables.contains("lat") && queryVariables.contains("lon")){
	//        	grid.setTitle("Data from : " + markerName + ", Latitude: " + markerLat + " ; Longitude: " + markerLon);
	//        } else {
	//        	grid.setTitle("All data at a certain location....Please update");
	//        }

	//        //setup the paging toolbar.
	//        final PagingToolbar pagingToolbar = new PagingToolbar(this.store);
	//        pagingToolbar.setPageSize(20);
	//        pagingToolbar.setDisplayInfo(true);
	////        pagingToolbar.setDisplayMsg("Displaying records {0} - {1} of {2}");
	//        pagingToolbar.setEmptyMsg("No records to display");
	//
	//        //setup a field to change the page size automatically
	//        NumberField pageSizeField = new NumberField();
	//        pageSizeField.setWidth(40);
	//        pageSizeField.setSelectOnFocus(true);
	//        pageSizeField.addListener(new FieldListenerAdapter() {
	//            public void onSpecialKey(Field field, EventObject e) {
	//                if (e.getKey() == EventObject.ENTER) {
	//                    int pageSize = Integer.parseInt(field.getValueAsString());
	//                    pagingToolbar.setPageSize(pageSize);
	//                }
	//            }
	//        });
	//        //make a tool tip for the page size field
	//        ToolTip toolTip = new ToolTip("Enter page size");
	//        toolTip.applyTo(pageSizeField);
	//        //add these field size widget to the toolbar...
	//        pagingToolbar.addField(pageSizeField);
	//        grid.setBottomToolbar(pagingToolbar);
	//
	//        this.date.setTime(System.currentTimeMillis());
	//		GWT.log(this.dtf.format(date) + " :: Done, now loading the first 50 records and displaying the data grid.", null);
	//
	////		//set the grid to load it's data on render..
	////		grid.addListener(new PanelListenerAdapter() {
	////			public void onRender(Component component) {
	////				store.load(0, 10);
	////			}
	////		});
	//
	//		//load the initial page...
	//		store.load(0, 50);

	        //BUG:: More than 1 grid is rendered incorrectly.
	        //FIX: set the height to be auto.
	        grid.setAutoHeight(true);

	        //add the grid to the new panel
	        dataGridPanel.add(grid);
	        dataGridPanel.setLayout(new FitLayout());
	        dataGridPanel.doLayout();

	        //put the data onto the grid
	        this.southPanel.add(dataGridPanel);
	        
	        this.southPanel.doLayout();
	        
	        //set the tab to be active and don't forget to inc the counter.
	        this.southPanel.setActiveTab(gridCounter++);

//	        this.date.setTime(System.currentTimeMillis());
//	        GWT.log(this.dtf.format(date) + " " + this.toString() + " :: Count of items in the store after load and erender....-> "  + store.getCount() + " : "+ store.getTotalCount(), null);
    	} catch (Exception e) {

    		MessageBox.alert(e.getMessage());

    		//TODO: Do something with the error
    		GWT.log(e.getMessage(), null);
    		e.printStackTrace();
    	}

    }

    /*
     * Method to Execute the Query against the data store
     */
    private void executeQuery(boolean cache, boolean override, boolean limit) {
    	
    	//build the query from the UI if we don't want to override
        if (!override) {
        	//am i doing a JOIN query - should have been set by the query add panel interface
        	//OR by the load query example
        	//TODO: What if i load a join QUERY AND MODIFY THE FIELDS>>>I will still build a join query but may not actually want to
        	//TODO: FIX UP HOW THIS WILL WORK.
//        	if (this.queryType && this.queryPanels[1].columnPanel.isVisible()) {
//        		//use the JOIN query builer and set the query Field
//	            queryField.setValue(buildJoinSQLQuery());
//        	} else {
//        		//set the query field to be the result of the call to the build query method
//	            queryField.setValue(buildUnionSQLQuery());
//        	}
        	
        	//just use the single query panel SQL query build method
        	queryField.setValue(buildSQLQuery());
        }

        //if i have the limit keyword set
        //get the text from the query tab
        String queryText = queryField.getText();

        //check if we have a semblance of a query
        if (queryText.length() >1) {
        	
        	//now test if the query contains only a select? //make sure there are no deleted / updates / etc.
        	//Expand this to make sure the DB is read only via the connection.
        	if (queryText.toUpperCase().contains("DELETE") || queryText.toUpperCase().contains("UPDATE") || queryText.toUpperCase().contains("INSERT") || queryText.toUpperCase().contains("CREATE")) {
        		MessageBox.alert("Invalid query type inserted.\nThis query contains malicious clauses, details of the executing client have been noted.");
        	} else {
        	
	            //Call the server query method
	//            reifsServer.executeLocationQuery(queryText, cache, queryCallBack);
	            reifsServer.executeThreadedLocationQuery(queryText, cache, threadedQueryCallBack);
	            
	            //let the user see we are using rpc
	            //centrePanel.getActiveTab().getEl().mask("Querying the server...");
	            northPanel.getEl().mask("Querying the server, please wait...");
	
	            //hold onto my queryString and my cache for use in the RPC
	            this.queryID = queryText;
	            this.cacheID = cache;
	
	            //setup the query execution timer
	            queryStartTime = System.currentTimeMillis();
        	}

            //make the map the active tab for viewing
            centrePanel.setActiveTab("mapTab");
        }
        else {
            centrePanel.setActiveTab("queryEditTab");
            MessageBox.alert("Please enter a query into the query editing area.");
        }
    }
    
    /**
     * Method to build the JOIN type query from the 2 Query UI's
     * Special case where the second UI panel complements the first with variables which are subqueries for the first.
     * @result String the UI fields of the panels converted into an SQL query.
     * TODO: Recreate this based on 2 query UI panels
     */
    private String buildJoinSQLQuery(){
    	
    	//TODO build SQL join queries using sub queries based on a join param (Currently date / region).
        //now send the string back
        return "-- Not currently supported";
    }
    
    /**
     * Method to build the SQL query from the 1st Query UI Panel
     * Simple case where only 1 panel is allowed and the query is limited to strict data retrieval.
     * @result String the UI fields of the panels converted into a SPARQL query.
     */
    private String buildSQLQuery(){
    	
    	//setup my date formatter
        DateTimeFormat date_fmt = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");

    	//capture all the query variables we have set...
    	//as well they key (if containing spaces) is used for and extra table names we may need. 
        LinkedHashMap<String,String> setVariables = new LinkedHashMap<String,String>();

        //String Builder to make the query correctly
        StringBuilder queryText = new StringBuilder();

        //set my title for the Query Field here
        queryPanel.setTitle("Manually constructed SQL Query");

        //here is my template
        String queryTemplate = "SELECT ";

        String fromClause = "  target tgt, observation obs, unit un, context ctx, site st, datetime dt, " +
        		"observation_measurement obs_msr, characteristic ch, actor ac, observation_actor obs_ac, " +
        		"\nmeasurement msr LEFT JOIN tool tl on msr.tool = tl.id\n" +
        		"			  LEFT JOIN data_qualifier ql on msr.qualifier = ql.id ";
        
        String whereClause = "\nWHERE\n" +
        		"  msr.id = obs_msr.measurementID and\n" +
        		"  obs_msr.observationid = obs.id and\n" +
        		"  obs.target = tgt.id and\n" +
        		"  msr.characteristic = ch.id and\n" +
        		"  msr.unit = un.id and\n" +
        		"  obs.id = obs_ac.observationid and\n" +
        		"  obs_ac.actorid = ac.id and\n" +
        		"  obs.context = ctx.id and\n" +
        		"  ctx.site = st.id and\n" +
        		"  ctx.datetime = dt.id";

        String endQueryTemplate =	";\n";
//        String endQueryTemplate =	"\n";

        //setup the query text field with the start of the query string
        queryField.setValue(queryTemplate);

        //loop over each of the query panels i have setup here and create the SPARQL statements

        this.date.setTime(System.currentTimeMillis());
//            GWT.log(this.dtf.format(date) + " " + this.toString() + " :: " + this.queryPanels.length, null);
//            GWT.log(this.dtf.format(date) + " " + this.toString() + " :: " + this.queryPanels[i].toString(), null);
            
        //add this variable into the array of set vars
        setVariables.put("startdate", "dt.startdate");
            
        //now add the filters if we have set the fields
        if (this.queryPanels[0].start_date.isDirty()) {
        	
        	//need to format the expected date time string into the valid format for the query
//                String[] start_dates = date_fmt.format(queryPanels[i].begin_date.getValue()).split(" ");
            //test if i want the range of specific values
            if (this.queryPanels[0].datesAsRange.getValue()){
                //set the date as using the date formatter
                queryText.append("  dt.startdate = '" + date_fmt.format(queryPanels[0].start_date.getValue()) + "' and\n");
            } else {
            	//set the date as a range using the start date
                queryText.append("  dt.startdate >= '" + date_fmt.format(queryPanels[0].start_date.getValue()) + "' and\n");
            }
        }

        //add this variable into the array of set vars
        setVariables.put("enddate","dt.enddate");
        
        //test if we have set values
        if (this.queryPanels[0].end_date.isDirty()) {
            
        	//test if i want the range of specific values
            if (this.queryPanels[0].datesAsRange.getValue()){
                //set the date as a specific value
                queryText.append("  dt.enddate = '" + date_fmt.format(queryPanels[0].end_date.getValue()) + "' and\n");
            } else {
            	//set the date as a range using the start date
                queryText.append("  (dt.enddate is null or dt.enddate <= '" + date_fmt.format(queryPanels[0].end_date.getValue()) + "') and\n");
            }
        }

        //variables for my coords
        String lon = "";
        String lat = "";
        String lon_end = "";
        String lat_end = "";

        //am i using point or region selectors?
        if (this.queryPanels[0].regionPoint.getActiveItem().getText().equalsIgnoreCase("Point")){
        	
        	//add this variable into the array of set vars
            setVariables.put("location","ST_AsText(point_geometry) as location");
            
            //i am using the point selector
            lon = this.queryPanels[0].longitude.getValueAsString();
            lat = this.queryPanels[0].latitude.getValueAsString();

            //make sure the fields are set
            if (lon.length() != 0 && lat.length() !=0) {
                //Select all values with the lat / lon values
                queryText.append("  ST_GeomFromText('POINT(" + lat + lon + ")',4326) = st.point_geometry and\n");
            }
        } else {
        	
        	//add this variable into the array of set vars
            setVariables.put("location","COALESCE(ST_AsText(st.region_geometry), ST_AsText(point_geometry), '(none)') as location");
            
            //get the values from the region fields
            lon = this.queryPanels[0].west.getValueAsString();
            lat = this.queryPanels[0].north.getValueAsString();
            lon_end = this.queryPanels[0].east.getValueAsString();
            lat_end = this.queryPanels[0].south.getValueAsString();

            //set the bounding box values for the query
            if (lat.length() != 0 &&	 lon.length() !=0 && lon_end.length() != 0 && lat_end.length() != 0) {
            	
            	//check if we are using within or intersecting sql queries
            	if ((!this.queryPanels[0].region_as_range.isDisabled()) && this.queryPanels[0].region_as_range.getValue()) {
            		//add the postgis spatial query using the bounding saptial variables
                	queryText.append("(ST_Contains(ST_GeomFromText('POLYGON((" + lat + " " + lon + ", " + lat_end + " " + lon + ", " + lat_end + " " + lon_end + ", " + lat + " " + lon_end +", " + lat + " " + lon + "))',4326), st.point_geometry) or\n" +
                			"ST_Contains(ST_GeomFromText('POLYGON((" + lat + " " + lon + ", " + lat_end + " " + lon + ", " + lat_end + " " + lon_end + ", " + lat + " " + lon_end +", " + lat + " " + lon + "))',4326), st.region_geometry)) and\n");
            	} else {
            		//add the postgis spatial query using the bounding saptial variables
                	queryText.append("(ST_Intersects(ST_GeomFromText('POLYGON((" + lat + " " + lon + ", " + lat_end + " " + lon + ", " + lat_end + " " + lon_end + ", " + lat + " " + lon_end +", " + lat + " " + lon + "))',4326), st.point_geometry) or\n" +
                			"ST_Intersects(ST_GeomFromText('POLYGON((" + lat + " " + lon + ", " + lat_end + " " + lon + ", " + lat_end + " " + lon_end + ", " + lat + " " + lon_end +", " + lat + " " + lon + "))',4326), st.region_geometry)) and\n");
            	}
            }
        }

        //add this variable into the array of set vars
        setVariables.put("locname","st.name as locname");
        //set the location name
        if (this.queryPanels[0].location.isDirty()) {
            //need to offer the user a list of known location names
            queryText.append("  st.name ilike '%" + this.queryPanels[0].location.getValueAsString() + "%' and\n");
        }

        //if the depth values are set
        if (this.queryPanels[0].depth.isDirty()) {
        	//add this variable into the array of set vars
            setVariables.put("depth","st.depth");
            //get the depth variable
            queryText.append("  st.depth "+ this.queryPanels[0].depth.getValueAsString() + " and\n");
        }

        //add this variable into the array of set vars
        setVariables.put("unit","un.description as unit");
        //the units we are working on
        //add this variable into the array of set vars
        setVariables.put("precision","msr.precision as precision");
        if (this.queryPanels[0].unit_value.isDirty()) {
            //set the filter with a measurement value
            queryText.append("  un.name ilike '" + this.queryPanels[0].unit_value.getValueAsString() + "' and\n");
        }

        //add this variable into the array of set vars
        setVariables.put("value","COALESCE (CAST(msr.numeric_value as text), msr.other_value) as value");
        setVariables.put("precision","ql.qualifier");
        //actual measurement values
        if (this.queryPanels[0].measurement_value.isDirty()) {
        	//get the operator of the measurement value [>=<]
        	String operator = this.queryPanels[0].measurement_value.getValueAsString().substring(0, 1);
        	//get and trim the value after the operator [Alpha/Numeric]
        	String value = this.queryPanels[0].measurement_value.getValueAsString().substring(1).trim();
        	//test if the value contains only numbers
        	if (value.matches("\\d+")) {
        		//set the filter with a measurement value 
//        		System.out.println("Test of the Measurement value field regex non-alpha.... " + value);
        		//set the filter with a numeric measurement value
        		queryText.append("  msr.numeric_value " + operator + " " + value + " and\n");
        	} 
        	else { //i have non-numeric chars
//        		System.out.println("Test of the Measurement value field regex Alpha .... " + value);
        		queryText.append("  msr.other_value = '" + value + "' and\n");
        	}
        }

        //add this variable into the array of set vars
        setVariables.put("characteristic","ch.description as characteristic");
        if (this.queryPanels[0].characteristic.isDirty()) {
            //set the characteristic type from the UI
            queryText.append("  ch.description ilike '%" + this.queryPanels[0].characteristic.getValueAsString() + "%' and\n");
        }

        //add this variable into the array of set vars
        setVariables.put("actor","ac.name as actor");
        if (this.queryPanels[0].actor.isDirty()) {
        	//the actor name we are looking for
            queryText.append("  ac.name ilike '%" + this.queryPanels[0].actor.getValueAsString() + "%' and\n");            
        }

        //if the actor is a sensor && the sensor check is on
        //Note: should this also be a dirty field check?
        if (this.queryPanels[0].sensor.getValue()) {
        	setVariables.put("tool", "tl.name as tool"); 
            //restrict the actor to only be the machine types.
            queryText.append("  ac.type = 3 and\n");
        }

        //test if the ecological process is dirty
        if (this.queryPanels[0].ecological_process.isDirty()) {
        	setVariables.put("ecological_process ep","ep.name as ecoProcess");
            //get the ecological process
        	queryText.append("  ep.id = obs.is_part_of_eco_process and\n");
        	queryText.append("  ep.name ilike '%" + this.queryPanels[0].ecological_process.getValueAsString() + "%' and\n");
        }

        //test if the research program is dirty
        if (this.queryPanels[0].research_program.isDirty()) {
            //add this variable into the array of set vars
            setVariables.put("program pg","pg.name as reserchProgram");
            //get the reaserch program
            queryText.append("  pg.id = obs.program and\n");
            queryText.append("  pg.name ilike '%" + this.queryPanels[0].research_program.getValueAsString() + " and\n");

        }

        //only use these if they have been set.
        if (this.queryPanels[0].genus.isDirty()){
            //add this variable into the array of set vars
            setVariables.put("genus","obs.genus as genus");
            //add the target string to the field string
            queryText.append("  obs.genus ilike '%" + this.queryPanels[0].genus.getValueAsString() + "%' and\n");
        }

        if (this.queryPanels[0].species.isDirty()){
            //add this variable into the array of set vars
            setVariables.put("species","obs.species as species");
            //add the target string to the field string
            queryText.append("  obs.species ilike '%" + this.queryPanels[0].species.getValueAsString() + "%' and\n");
        }

        if (this.queryPanels[0].morphology.isDirty()){
            //add this variable into the array of set vars
            setVariables.put("morphology","obs.morphology as morphology");
            //add the target string to the field string
            queryText.append("  obs.morphology ilike '%" + this.queryPanels[0].morphology.getValueAsString() + "%' and\n");
        } 
        
        //check if we want to return the organism values in the query result
        if (this.queryPanels[0].organism_values.getValue()) {
        	setVariables.put("genus","obs.genus as genus");
        	setVariables.put("species","obs.species as species");
        	setVariables.put("morphology","obs.morphology as morphology");
        }
        
        //add this variable into the array of set vars
        setVariables.put("target","tgt.description as target");
        //set the target of the query
        if (this.queryPanels[0].target.isDirty()){
            //add the target string to the field string
            queryText.append("  tgt.description = '" + this.queryPanels[0].target.getValueAsString() +"' and\n");
        }
        
        //String builder to construct the list of variables
        StringBuilder variables = new StringBuilder();
        
        //String builder to construct the extra tables we need based on the query
        StringBuilder tables = new StringBuilder();

        //make a list of the variables that i am interested in to return from the query.
        int keyCounter = 0;
        //now i need to construct the string which selects only certain variables of the query.
        for (String key : setVariables.keySet()) {
        	
        	//test if my key contains spaces -> used to capture the extra tables
        	if (key.matches(".+\\s.+")) {
        		//note these extra tables will always be in front of standard clause.
        		tables.append(key + ", ");
        	}
        	
        	//reached the end of the variables
            if (keyCounter == setVariables.keySet().size()-1) {
            	variables.append(setVariables.get(key));
            }else {
            	variables.append(setVariables.get(key) + ", ");
            }
            //inc my keyCounter...
            keyCounter++;
        }
        
        //if i have extra query text then we must join the two clauses with an 'and'
        if (!queryText.toString().equalsIgnoreCase("")) {
        	//ad the joining and
        	queryText.insert(0, " and\n");
        }
        
        //now remove the last and from the query text - remnamt of the way we add clauses.
        queryText = new StringBuilder(queryText.toString().replaceAll("and\n$", ""));
        
        //now set the queryField text comprising all the built strings
        this.queryField.setValue(this.queryField.getValueAsString() + variables + "\nFROM\n" + tables + fromClause +  whereClause + queryText + endQueryTemplate);

        
        //now send the string back
        return this.queryField.getValueAsString();
    }

    /**
     * Method to build the JOIN type query (non SPARQL) from the 2 Query UI's
     * Special case where the second UI panel complements the first with variables which are subqueries for the first.
     * Similar to SQL JOIN but without subqueries.
     * @param Boolean specifying we want to limit the result set -> Defaults to 200
     * @result String the UI fields of the panels converted into a SPARQL query.
     */
    private String buildJoinSPARQLQuery(boolean limit){
    	try {


    	//TODO: Setup a date filter for the Date fields -> will create the regular expressions from years - months - days

        //TODO: use the queryType flag to find out if we are building a union OR a join query..
        //this will be used to extend the types of queries we can do....
        //TODO: get the queryies we want to run and relate to the interface and build them.
    	//I.E. make the UI build the right queries...

    	//Note: In a JOIN query: The first panel contains all the variables we want to return.
    	//					   : The second panel contains all the variables we want to constrain the first panel variables with.
    	//
    	//TODO: Use a datetime FILTER with the values of {Constrained <> OR ==]
		//to select if they coincide ie...how do monthly date ranges compare to instance data?
		//or equal -> the default -> As per the location filter.

        //setup my date formatter
        DateTimeFormat date_fmt = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");

        //capture all the variables we have set...
        HashMap<String,String> setVariables = new HashMap<String,String>();

        //String Builder to make the query correctly
        StringBuilder queryText = new StringBuilder();

        //set my title for the Query Field here
        queryPanel.setTitle("Manually constructed JOIN query");

        //here is my template
        String queryTemplate = "PREFIX event: <http://maenad.itee.uq.edu.au/metadata/Event#>\n" +
                                    "PREFIX spatial: <http://maenad.itee.uq.edu.au/metadata/Spatial#>\n" +
                                    "PREFIX context: <http://maenad.itee.uq.edu.au/metadata/Context#>\n" +
                                    "PREFIX temporal: <http://maenad.itee.uq.edu.au/metadata/Temporal#>\n" +
                                    "PREFIX physical: <http://maenad.itee.uq.edu.au/metadata/Physical#>\n" +
                                    "PREFIX actor: <http://maenad.itee.uq.edu.au/metadata/Actor#>\n" +
                                    "PREFIX abstract: <http://maenad.itee.uq.edu.au/metadata/Abstract#>\n" +
                                    "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                                    "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                                    "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>\n\n" +
                                    "SELECT DISTINCT ";

        String endHeader = 		"\nWHERE {\n";

        String endEachQueryTemplate = "  ?instance event:has_context ?context .\n" +
                                        "  ?instance event:has_measurement ?measurement .\n" +
                                        "  ?instance rdf:type ?observation .\n" +
                                        "  ?observation rdfs:subClassOf event:Observation .\n";

        String endQueryTemplate =	"}\n";

        String limitTemplate =	"LIMIT " + this.queryLimit + "\n";

        //setup the query text field with the start of the query string
        queryField.setValue(queryTemplate);

        //only build the appropriate query...build the first panel and finish it off if second panel empty.
        //if not then add the second panel vars as ?var1 in the query text...
        //the hard part is knowing how to join fields..
        //so if second panel is set then we use these fields in the first part of the query..

        //test if the second panel is empty -> if so warn the user that no join has been specified.
        if (!isQueryUIDirty(1)) {
        	MessageBox.alert("Please complete the JOIN query fields in the JOIN panel.");
        }
        else { //carry on and make the join query

        	//only set variable filters if this field is dirty
	        if (this.queryPanels[1].start_date.isDirty()) {

	        	//need to format the expected date time string into the valid format for the query
	            String[] start_dates = date_fmt.format(queryPanels[1].start_date.getValue()).split(" ");

	        	//set variable names to the normal values if i join on this field
	        	if (this.datetimeJoin) {

	        		//Note: i am using the vars form the second panel query to join against in the top panel query.
	        		//add this variable into the array of set vars
	        		setVariables.put("?begindate", "?begindate");

	        		//if the top panel has nothing set..then these values are used for the whole query
//	        		if (this.queryPanels[0].begin_date.getValueAsString().equalsIgnoreCase("")) {

	        		//join on the same date variable
	        		if (this.joinDatetimeQualifier.getValueAsString().equalsIgnoreCase("") || this.joinDatetimeQualifier.getValueAsString().equalsIgnoreCase("Equals Exactly")) {
	        			//set the start date time field
		                queryText.append("  ?datetime1 temporal:has_datetime ?begindate .\n");
		                //set the date as a range
		                queryText.append("  FILTER (?begindate >= \"" + start_dates[0] + "T" + start_dates[1] + "\"^^xsd:dateTime) .\n");
	        		} else {
	        			//join on the range..
	        			//set the start date time field
		                queryText.append("  ?datetime1 temporal:has_datetime ?begindate1 .\n");
		                //set the date as a range
		                queryText.append("  FILTER (?begindate1 >= \"" + start_dates[0] + "T" + start_dates[1] + "\"^^xsd:dateTime) .\n");
	        		}
//	        		} else {
	        			//then use secondary variables
	        			//set the start date time field
//		                queryText.append("  ?datetime1 temporal:has_datetime ?begindate1 .\n");
		                //set the date as a range
//		                queryText.append("  FILTER (?begindate1 >= " + start_dates[1] + "T" + start_dates[1] + "\"^^xsd:dateTime) .\n");
//	        		}

	        	} else {

	        		//set the start date time field
	                queryText.append("  ?datetime1 temporal:has_datetime ?begindate1 .\n");
	                //set the date as a range
	                queryText.append("  FILTER (?begindate1 >= \"" + start_dates[0] + "T" + start_dates[1] + "\"^^xsd:dateTime) .\n");
	        	}
	        } else {

	        	//set variable names to the normal values if i join on this field
	        	if (this.datetimeJoin) {

	        		//join on the exactly date time
	        		if (this.joinDatetimeQualifier.getValueAsString().equalsIgnoreCase("") || this.joinDatetimeQualifier.getValueAsString().equalsIgnoreCase("Equals Exactly")) {
		        		//no filters but i am joining on the datetime
		        		queryText.append("  ?datetime1 temporal:has_datetime ?begindate .\n");
	        		} else {
	        			queryText.append("  ?datetime1 temporal:has_datetime ?begindate1 .\n");
	        		}
	        	}
//	        	else {
//	        		//just the normal info then...
//	        		//actually - if not joining on datetime then i don't need this..
//	        		//I will only be interested in the top result set datetime
//	        	}

	        }

            //test if we have set values
	        if (this.queryPanels[1].end_date.isDirty()) {

	        	//get the date value
	            String[] end_dates = date_fmt.format(this.queryPanels[1].end_date.getValue()).split(" ");

	        	//set variable names to the normal values if i join on this field
	        	if (this.datetimeJoin) {

		            //test if we have values set in the top panel
//		            if (this.queryPanels[0].end_date.getValueAsString().equalsIgnoreCase("")) {
			            //add this variable into the array of set vars
				        setVariables.put("?enddate","?enddate");

			        //join on the same date variable
	        		if (this.joinDatetimeQualifier.getValueAsString().equalsIgnoreCase("") || this.joinDatetimeQualifier.getValueAsString().equalsIgnoreCase("Equals Exactly")) {

				        //set the start date time field
		                queryText.append("  OPTIONAL { ?datetime1 temporal:has_end_datetime ?enddate .\n");
		                //set the date as a range
		                queryText.append("  FILTER (?enddate <= \"" + end_dates[0] + "T" + end_dates[1] + "\"^^xsd:dateTime) } .\n");
	        		} else {
	        			 //set the start date time field
		                queryText.append("  OPTIONAL { ?datetime1 temporal:has_end_datetime ?enddate1 .\n");
		                //set the date as a range
		                queryText.append("  FILTER (?enddate1 <= \"" + end_dates[0] + "T" + end_dates[1] + "\"^^xsd:dateTime) } .\n");
	        		}
//		            } else {
		            	//set the start date time field
//		                queryText.append("  OPTIONAL { ?datetime1 temporal:has_end_datetime ?enddate1 .\n");
		                //set the date as a range
//		                queryText.append("  FILTER (?enddate1 <= " + end_dates[1] + "T" + end_dates[1] + "\"^^xsd:dateTime) } .\n");
//		            }
	        	} else {
	        		//set the start date time field
	                queryText.append("  OPTIONAL { ?datetime1 temporal:has_end_datetime ?enddate1 .\n");
	                //set the date as a range
	                queryText.append("  FILTER (?enddate1 <= \"" + end_dates[0] + "T" + end_dates[1] + "\"^^xsd:dateTime) } .\n");
	        	}
	        } else {
	        	//set variable names to the normal values if i join on this field
	        	if (this.datetimeJoin) {

	        		//join on the same date variable
	        		if (this.joinDatetimeQualifier.getValueAsString().equalsIgnoreCase("") || this.joinDatetimeQualifier.getValueAsString().equalsIgnoreCase("Equals Exactly")) {
		        		//no filters but i am joining on the datetime
		        		queryText.append("  OPTIONAL { ?datetime1 temporal:has_end_datetime ?enddate } .\n");
	        		} else {
	        			//no filters but i am joining on the datetime
		        		queryText.append("  OPTIONAL { ?datetime1 temporal:has_end_datetime ?enddate1 } .\n");
	        		}
	        	}
	        }

	        //set variable names to the normal values if i join on this field
        	if (this.datetimeJoin && this.locationJoin) {
        		//get the datetime variable used to get the begin and end dates
        		queryText.append("  ?context context:has_datetime ?datetime .\n");
        	}
//        	else if (datetimeJoin) {
//        		//get the datetime variable used to get the begin and end dates
//        		queryText.append("  ?context1 context:has_datetime ?datetime1 .\n");
//        	}
        	else {
        		//get the datetime variable used to get the begin and end dates
        		queryText.append("  ?context1 context:has_datetime ?datetime1 .\n");
        	}

	        //setup variables for my coords -> avoids warnings.
	        String lon = "";
	        String lat = "";
	        String lon_end = "";
	        String lat_end = "";

	        //set variable names to the normal values if i join on this field
//        	if (this.locationJoin) {

        		//test if i have set variables in the top panel --> Note Depends on the validate code picking up that both lat / lon must be set.
//        		if (!this.queryPanels[0].latitude.getValueAsString().equalsIgnoreCase("")) {
        			//set this var to be a comparison variable.

	        		//set the longitude & latitude vars
		        	queryText.append("  ?location1 spatial:has_longitude ?lon1 .\n");
			        queryText.append("  ?location1 spatial:has_latitude ?lat1 .\n");
//        		} else {
        			//Feed this variables straight into the top query panel then

        			//add this variable into the array of set vars
//        		    setVariables.put("?lat","?lat");
	    	        //add this variable into the array of set vars
//	    	        setVariables.put("?lon","?lon");

	        		//set the longitude & latitude vars
//		        	queryText.append("  ?location1 spatial:has_longitude ?lon .\n");
//			        queryText.append("  ?location1 spatial:has_latitude ?lat .\n");
//        		}
//        	} else {
//        		//set the longitude & latitude vars to subname
//	        	queryText.append("  ?location1 spatial:has_longitude ?lon1 .\n");
//		        queryText.append("  ?location1 spatial:has_latitude ?lat1 .\n");
//        	}

	        //am i using point or region selectors?
	        if (this.queryPanels[1].regionPoint.getActiveItem().getText().equalsIgnoreCase("Point")){

	            //i am using the point selector
	            lon = this.queryPanels[1].longitude.getValueAsString();
	            lat = this.queryPanels[1].latitude.getValueAsString();

	            //make sure the fields are set
	            if (lon.length() != 0 && lat.length() !=0) {

	            	//set variable names to the normal values if i join on this field
//	            	if (this.locationJoin) {
	            		//Select all values with the lat / lon values
//		                queryText.append("  FILTER (?lon = " + lon + " && ?lat = " + lat + ") .\n");
//	            	} else {
	            		queryText.append("  FILTER (?lon1 = " + lon + " && ?lat1 = " + lat + ") .\n");
//	            	}
	            }
	        } else {

	            //get the values from the region fields
	            lon = this.queryPanels[1].east.getValueAsString();
	            lat = this.queryPanels[1].north.getValueAsString();
	            lon_end = this.queryPanels[1].west.getValueAsString();
	            lat_end = this.queryPanels[1].south.getValueAsString();

	            //set the bounding box values for the query
	            if (lat.length() != 0 && lon.length() !=0 && lon_end.length() != 0 && lat_end.length() != 0) {

	            	//set variable names to the normal values if i join on this field
//	            	if (this.locationJoin) {
	            		//Note : Set the lat / lon vars above
	            		//add this variable into the array of set vars
//	    	            setVariables.put("?lat_end","?lat_end");
	    	            //add this variable into the array of set vars
//	    	            setVariables.put("?lon_end","?lon_end");

	    	            //Select all values with the lat / lon values
//		            	queryText.append("  FILTER (?lon <= " + lon + " && ?lat <= " + lat + ") .\n");
//	            	} else {
	            		//Select all values with the lat / lon values
		            	queryText.append("  FILTER (?lon1 <= " + lon + " && ?lat1 <= " + lat + ") .\n");
//	            	}

//	                if (this.queryPanels[1].region_as_range.getValue() && (!this.queryPanels[1].region_as_range.isDisabled())){
	                	//set variable names to the normal values if i join on this field
//		            	if (this.locationJoin) {
		            		//setup the optional tags for the lat / lon ends
//		            		queryText.append("  OPTIONAL { ?location1 spatial:has_end_longitude "+ lon_end + "^^xsd:float .\n");
//		            		queryText.append("                   ?location1 spatial:has_end_latitude " + lat_end + "^^xsd:float } .\n");
//		            	} else {
		            		//setup the optional tags for the lat / lon ends
//		            		queryText.append("  OPTIONAL { ?location1 spatial:has_end_longitude "+ lon_end + "^^xsd:float .\n");
//		            		queryText.append("                   ?location1 spatial:has_end_latitude " + lat_end + "^^xsd:float } .\n");
//		            	}
//	                } else {
	                	//set variable names to the normal values if i join on this field
//		            	if (this.locationJoin) {
//		                    queryText.append("  OPTIONAL { ?location1 spatial:has_end_longitude ?lon_end .\n");
//		                    queryText.append("                   ?location1 spatial:has_end_latitude ?lat_end . \n" +
//		                                     "  FILTER (?lon_end >= " + lon_end + " && ?lat_end >= " + lat_end + ") } .\n");
//		            	} else {
		            		 queryText.append("  OPTIONAL { ?location1 spatial:has_end_longitude ?lon_end1 .\n");
			                 queryText.append("                   ?location1 spatial:has_end_latitude ?lat_end1 . \n" +
			                                  "  FILTER (?lon_end1 >= " + lon_end + " && ?lat_end1 >= " + lat_end + ") } .\n");
//		            	}
//	                }

	            } else {
	            	//set variable names to the normal values if i join on this field
//	            	if (this.locationJoin) {
//		                queryText.append("  OPTIONAL { ?location1 spatial:has_end_longitude ?lon_end .\n");
//		                queryText.append("                   ?location1 spatial:has_end_latitude ?lat_end } .\n");
//	            	} else {
	            		queryText.append("  OPTIONAL { ?location1 spatial:has_end_longitude ?lon_end1 .\n");
		                queryText.append("                   ?location1 spatial:has_end_latitude ?lat_end1 } .\n");
//	            	}
	            }

	        }

	        //capture the location field if i have set it in the second panel
	        if (this.queryPanels[1].location.isDirty()) {
	        	//set variable names to the normal values if i join on this field
//            	if (this.locationJoin) {
            		//add this variable into the array of set vars
//        	        setVariables.put("?locname","?locname");
        	        //set the location name
//        	        queryText.append("  ?location1 spatial:has_name ?locname .\n");
        	        //need to offer the user a list of known location names
//    	            queryText.append("  FILTER regex(?locname, \"" + this.queryPanels[1].location.getValueAsString() + "\", \"i\") .\n");
//            	} else {
            		//add this variable into the array of set vars
//        	        setVariables.put("?locname1","?locname1");
        	        //set the location name
        	        queryText.append("  ?location1 spatial:has_name ?locname1 .\n");
        	        //need to offer the user a list of known location names
    	            queryText.append("  FILTER regex(?locname1, \"" + this.queryPanels[1].location.getValueAsString() + "\", \"i\") .\n");
//            	}

	        }

	        //if the depth values are set
	        if (this.queryPanels[1].depth.isDirty()) {
	        	//set variable names to the normal values if i join on this field
        		//get the depth variable
	            queryText.append("  ?location1 spatial:has_depth ?depth1 .\n");
	            //need to offer the user a list of known location names
	            queryText.append("  FILTER (?depth1 "+ this.queryPanels[1].depth.getValueAsString() + ") .\n");
	        }

	        //set variable names to the normal values if i join on this field
        	if (this.locationJoin && this.datetimeJoin) {
        		//test if we actually join on both vars..if we do then the context is shared.
        		//i.e. all the joined contexts set the query result set contexts
        		//need to get the location variables for matching
        		queryText.append("  ?context context:has_location ?location1 .\n");
        	}
//        	else if (this.locationJoin) {
//        		//need to get the location variables for matching
//        		queryText.append("  ?context1 context:has_location ?location1 .\n");
//        	}
        	else {
        		//need to get the location variables for matching
        		queryText.append("  ?context1 context:has_location ?location1 .\n");
        	}

        	//only get extract variables we have set in the join query
	        if (this.queryPanels[1].unit_value.isDirty()) {
//	        	//add this variable into the array of set vars
//		        setVariables.put("?unitsymbol1","?unitsymbol1");
		        //the units we are working on
		        queryText.append("  ?unit1 abstract:has_description ?unitsymbol1 .\n");
		        //get the unit var for use in query
		        queryText.append("  ?measurement1 event:has_unit ?unit1 .\n");
	            //set the filter with a measurement value
	            queryText.append("  FILTER regex(?unitsymbol1, \"" + this.queryPanels[1].unit_value.getValueAsString() + "\",\"i\") .\n");
	        }

	        //only get extract variables we have set in the join query
	        if (this.queryPanels[1].measurement_value.isDirty()) {
//	        	 //add this variable into the array of set vars
//		        setVariables.put("?value1","?value1");
		        //actual measurement values
		        queryText.append("  ?measurement1 event:has_value ?value1 .\n");
	            //set the filter with a measurement value
	            queryText.append("  FILTER (?value1 " + this.queryPanels[1].measurement_value.getValueAsString() + ") .\n");
	        }

	        //only get extract variables we have set in the join query
	        if (this.queryPanels[1].characteristic.isDirty()) {
//	        	//add this variable into the array of set vars
//		        setVariables.put("?characteristic1","?characteristic1");
		        //the characteristic class we are searching for
//		        queryText.append("  ?characteristic_type1 abstract:has_description ?characteristic1 .\n");
//		        queryText.append("  ?measurement1 event:has_characteristic ?characteristic_type1 .\n");
	            //set the characteristic type from the UI
	            queryText.append("  ?measurement1 event:has_characteristic " + this.queryPanels[1].characteristic.getValueAsString() + " .\n");

	        }

	        //only get extract variables we have set in the join query
	        if (this.queryPanels[1].actor.isDirty()) {
//	        	//add this variable into the array of set vars
//		        setVariables.put("?actorname","?actorname");
		        //the actor name we are looking for
		        queryText.append("  ?actor1 actor:has_name ?actorname1 .\n");
	            //set the actor name
	            queryText.append("  FILTER regex(?actorname1, \"" + this.queryPanels[1].actor.getValueAsString() + "\",\"i\") .\n");

	            //get the actor instance for query matching
		        queryText.append("  ?instance1 event:has_actor ?actor1 .\n");
	        }

	        //add the sensor class type here...
	        if (this.queryPanels[1].sensor.isDirty()) {
	            //the sensor type we are looking for
	            queryText.append("  ?actor1 rdf:type physical:Sensor .\n");
	        }

	        //test if the ecological process is dirty
	        if (this.queryPanels[1].ecological_process.isDirty()) {
	            //get the ecological process
	            queryText.append("  ?instance1 event:is_part_of_ecological_process ?eco_process1 .\n");
	            queryText.append("  ?eco_process1 rdf:type " + this.queryPanels[1].ecological_process.getValueAsString() + " .\n");
	        }

	        //test if the research program is dirty
	        if (this.queryPanels[1].research_program.isDirty()) {
	            //add this variable into the array of set vars
//	            setVariables.put("?group_name","?group_name");
	            //get the reaserch program
	            queryText.append("  ?research_program1 abstract:has_group ?group_name1 .\n");
	            queryText.append("  ?instance1 event:has_research_program ?research_program1 .\n");
	            queryText.append("  FILTER regex(?research_program1, \"" + this.queryPanels[1].research_program.getValueAsString() + "\",\"i\") .\n");

	        }

	        //only use these if they have been set.
	        if (this.queryPanels[1].genus.isDirty()){
	            //add this variable into the array of set vars
//	            setVariables.put("?genus","?genus");
	            //set the genus of the query
	            queryText.append("  ?instance1 event:has_genus ?genus1 .\n");
	            //add the target string to the field string
	            queryText.append("  FILTER regex(?genus1, \"" + this.queryPanels[1].genus.getValueAsString() + "\",\"i\") .\n");
	        }

	        if (this.queryPanels[1].species.isDirty()){
	            //add this variable into the array of set vars
//	            setVariables.put("?species","?species");
	            //set the species of the query
	            queryText.append("  ?instance1 event:has_species ?species1 .\n");
	            //add the target string to the field string
	            queryText.append("  FILTER regex(?species1, \"" + this.queryPanels[1].species.getValueAsString() + "\",\"i\") .\n");
	        }

	        if (this.queryPanels[1].morphology.isDirty()){
	            //add this variable into the array of set vars
//	            setVariables.put("?morphology","?morphology");
	            //set the morphology of the query
	            queryText.append("  ?instance1 event:has_morphology ?morphology1 .\n");
	            //add the target string to the field string
	            queryText.append("  FILTER regex(?morphology1, \"" + this.queryPanels[1].morphology.getValueAsString() + "\",\"i\") .\n");
	        }

	        if (this.queryPanels[1].target.isDirty()){
	        	//add this variable into the array of set vars
//		        setVariables.put("?target1","?target1");
		        //set the target of the query
		        queryText.append("  ?target_type1 abstract:has_description ?target1 .\n");
		        queryText.append("  ?instance1 event:of_target ?target_type1 .\n");
	            //add the target string to the field string
	            queryText.append("  ?instance1 event:of_target " + this.queryPanels[1].target.getValueAsString() +" .\n");
	        }

	        //setup the bottom of the join depending on what we are joining on...
	        StringBuilder endJoinBottom = new StringBuilder();

	        //change the context if we are using it to join
	        if (this.locationJoin && this.datetimeJoin) {
	        	endJoinBottom.append("  ?instance1 event:has_context ?context .\n");
	        } else if (this.datetimeJoin) {
	        	endJoinBottom.append("  ?instance1 event:has_context ?context1 .\n");
	        }

	        //now add the rest of the query
	        endJoinBottom.append(	"  ?instance1 event:has_measurement ?measurement1 .\n" +
            				    	"  ?instance1 rdf:type ?observation1 .\n" +
            						"  ?observation1 rdfs:subClassOf event:Observation .\n");

	        //add the normal end query footer
		    queryText.append(endJoinBottom.toString());

		    //NOW BUILD THE FIRST PANEL'S QUERY BASED ON THE JOIN INFORMATION

		    //add this variable into the array of set vars
        	setVariables.put("?begindate", "?begindate");

        	//only add these into the join query if they have been explicitly set..
        	if (this.queryPanels[0].start_date.isDirty()) {

        		//need to format the expected date time string into the valid format for the query
                String[] start_dates = date_fmt.format(queryPanels[0].start_date.getValue()).split(" ");

        		//test if i am joining on datetime
        		if (this.datetimeJoin) {
        			//if i join on datetime then the values in the top panel must filter the subrange from the bottom panel if they are set...
        			if (this.queryPanels[1].start_date.isDirty()) {
        				//then i have to compare the 2 with the top panel being contained by the bottom panel
        				//i.e. this panels date must be equal to or after the bottom panel date.
        				if (this.queryPanels[1].start_date.getValue().compareTo(this.queryPanels[0].start_date.getValue()) > 0) {
        					//Alert the user that this is incorret
        					MessageBox.alert("Setting the first panel Begin Date filter to be after the second panel begin date filter makes no sense for a join query!" +
							"<br><br>If setting constraints on the top and bottom date filters, all top panel filters must be contained by (a subset of) the bottom panel filters.");
        					//stop here...makes no sense to keep going.
        					return "";
        				}
        			}
        			//now i have to setup the join...
        			if (this.datetimeJoinFilter.equalsIgnoreCase("") || this.datetimeJoinFilter.equalsIgnoreCase("Equals Exactly")) {
        				//join on the exact datetimes
        				//if we haven't set a filter then...I need to set this datetime on the earlier query value
                        queryText.append("  ?datetime temporal:has_datetime ?begindate .\n");
                        queryText.append("  FILTER (?begindate >= " + start_dates[0] + "T" + start_dates[0] + "\"^^xsd:dateTime) .\n");
        			} else {
        				//join on the ranges
        				//if we haven't set a filter then...I need to set this datetime on the earlier query value
                        queryText.append("  ?datetime temporal:has_datetime ?begindate .\n");
                        queryText.append("  FILTER (?begindate >= " + start_dates[0] + "T" + start_dates[0] + "\"^^xsd:dateTime) .\n");
                        queryText.append("  FILTER (?begindate >= ?begindate1) .\n");
        			}
    			}
            } else {

            	//test if i am joining on datetime
        		if (this.datetimeJoin) {
	            	//now i have to setup the join...
	    			if (this.datetimeJoinFilter.equalsIgnoreCase("") || this.datetimeJoinFilter.equalsIgnoreCase("Equals Exactly")) {
	    				//join on the exact datetimes
	    				//if we haven't set a filter then...I need to set this datetime on the earlier query value
	                    queryText.append("  ?datetime temporal:has_datetime ?begindate .\n");

	    			} else {
	    				//join on the ranges
	    				//if we haven't set a filter then...I need to set this datetime on the earlier query value
	                    queryText.append("  ?datetime temporal:has_datetime ?begindate .\n");
	                    queryText.append("  FILTER (?begindate >= ?begindate1) .\n");
	    			}
        		} else {
        			//set the start date time field
        			queryText.append("  ?datetime temporal:has_datetime ?begindate .\n");
        		}
            }

        	//add this variable into the array of set vars
        	setVariables.put("?enddate", "?enddate");

        	//only add these into the join query if they have been explicitly set..
        	if (this.queryPanels[0].end_date.isDirty()) {

        		//need to format the expected date time string into the valid format for the query
                String[] end_dates = date_fmt.format(queryPanels[0].end_date.getValue()).split(" ");

                //test if i am joining on datetime
        		if (this.datetimeJoin) {
        			//if i join on datetime then the values in the top panel must filter the subrange from the bottom panel if they are set...
        			if (this.queryPanels[1].end_date.isDirty()) {
        				//then i have to compare the 2 with the top panel being contained by the bottom panel
        				//i.e. this panels date must be equal to or after the bottom panel date.
        				if (this.queryPanels[1].end_date.getValue().compareTo(this.queryPanels[0].end_date.getValue()) < 0) {
        					//Alert the user that this is incorret
        					MessageBox.alert("Setting the first panel End Date filter to be after the second panel begin date filter makes no sense for a join query!" +
        							"<br><br>If setting constraints on the top and bottom date filters, all top panel filters must be contained by (a subset of) the bottom panel filters.");
        					//stop here...makes no sense to keep going.
        					return "";
        				}
        			}

        			//now i have to setup the join...
        			if (this.datetimeJoinFilter.equalsIgnoreCase("") || this.datetimeJoinFilter.equalsIgnoreCase("Equals Exactly")) {
        				//if we haven't set a filter then...I need to set this datetime on the earlier query value
                        queryText.append("  OPTIONAL { ?datetime temporal:has_datetime ?enddate .\n");
                        queryText.append("  FILTER (?enddate >= " + end_dates[0] + "T" + end_dates[0] + "\"^^xsd:dateTime) } .\n");
        			} else {
        				//if we haven't set a filter then...I need to set this datetime on the earlier query value
                        queryText.append("  OPTIONAL { ?datetime temporal:has_datetime ?enddate .\n");
                        queryText.append("  FILTER (?enddate >= " + end_dates[0] + "T" + end_dates[0] + "\"^^xsd:dateTime) .\n");
                        queryText.append("  FILTER (?enddate <= ?enddate1) } .\n");
        			}

    			}

            } else {

            	//test if i am joining on datetime
        		if (this.datetimeJoin) {
	            	//now i have to setup the join...
	    			if (this.datetimeJoinFilter.equalsIgnoreCase("") || this.datetimeJoinFilter.equalsIgnoreCase("Equals Exactly")) {
	    				//if we haven't set a filter then...I need to set this datetime on the earlier query value
	                    queryText.append("  OPTIONAL { ?datetime temporal:has_datetime ?enddate } .\n");
	    			} else {
	    				//if we haven't set a filter then...I need to set this datetime on the earlier query value
	                    queryText.append("  OPTIONAL { ?datetime temporal:has_datetime ?enddate .\n");
	                    queryText.append("  FILTER (?enddate <= ?enddate1) }.\n");
	    			}
        		} else {
	            	//set the start date time field
	    			queryText.append("  OPTIONAL { ?datetime temporal:has_end_datetime ?enddate } .\n");
        		}
            }

            //setup variables for my coords -> reset from the first panel setting in case
            lon = "";
            lat = "";
            lon_end = "";
            lat_end = "";

            //set the longitude & latitude vars
            queryText.append("  ?location spatial:has_longitude ?lon .\n");
            queryText.append("  ?location spatial:has_latitude ?lat .\n");

            //add this variable into the array of set vars
            setVariables.put("?lat","?lat");
            //add this variable into the array of set vars
            setVariables.put("?lon","?lon");

            //am i using point or region selectors?
            if (this.queryPanels[0].regionPoint.getActiveItem().getText().equalsIgnoreCase("Point")){

                //i am using the point selector
                lon = this.queryPanels[0].longitude.getValueAsString();
                lat = this.queryPanels[0].latitude.getValueAsString();

                //test if i have a filter expression for the lat / lon
                if (lon.length() != 0 && lat.length() !=0) {

                	//TODO: Check if the second panel has a lat / lon box set and if so are our point bounds are contained within?
                	//TODO: Move the checking into the validateQueryUI panel for theranges and JOINs and points and so on.

                	//get the specified lat / lon vars
                	queryText.append("  FILTER (?lat=\"" + lat + "\"^^xsd:float && ?lon=\"" + lon + "\"^^xsd:float) .\n");
                }

                //test if i join on location and a filter expression
                if (this.locationJoin && (!this.locationJoinFilter.equalsIgnoreCase(""))) {

	                    //Filter the lat / lon diff on the filter qualifier we set up in the join
	                    queryText.append("  FILTER ((?lon1-?lon)<=" + this.locationJoinFilter + " && (?lat1-?lat)<= " + this.locationJoinFilter + ") .\n");
	                    queryText.append("  FILTER ((?lon-?lon1)<=" + this.locationJoinFilter + " && (?lat-?lat1)<= " + this.locationJoinFilter + ") .\n");

                } else if (this.locationJoin) {
                	//we haven't specified a filter to join on but we want to join on the values...
                	//therefore they must equal...here is a test for the UI values if set .. if not then apply the == test in the filter.
                	//make sure the fields are set

                	//no values explicitly set then we just filter on the results
                	//Filter the lat / lon diff on equality
                    queryText.append("  FILTER (?lon=?lon1 && ?lat=?lat1) .\n");

                }
            } else {

                //get the values from the region fields
                lon = this.queryPanels[0].east.getValueAsString();
                lat = this.queryPanels[0].north.getValueAsString();
                lon_end = this.queryPanels[0].west.getValueAsString();
                lat_end = this.queryPanels[0].south.getValueAsString();

                //add this variable into the array of set vars
                setVariables.put("?lat_end","?lat_end");
                //add this variable into the array of set vars
                setVariables.put("?lon_end","?lon_end");

                //test if i join on location
                if (this.locationJoin && (!this.locationJoinFilter.equalsIgnoreCase(""))) {

        	    	//test if the second panel has a range / point set...
                	//1. if point -> check that lat / lon is in range we have set.
                	//2. if range -> check that this range is a subrange -> warn and exit if not.
                	//3..

                	//set the bounding box values for the query
                    if (lat.length() != 0 && lon.length() !=0 && lon_end.length() != 0 && lat_end.length() != 0) {
                    	//the set the expression of the filter expression here
                    	//Select all values with the lat / lon values
                        queryText.append("  FILTER (?lon <= " + lon + " && ?lat <= " + lat + ") .\n");
                        queryText.append("  FILTER (?lon1-?lon <=" + this.locationJoinFilter + " && ?lat1-?lat<= " + this.locationJoinFilter + ") .\n");
                    	//setup the optional tags for the lat / lon ends
                    	queryText.append("  OPTIONAL { ?location spatial:has_end_longitude ?lon_end .\n");
                        queryText.append("                   ?location spatial:has_end_latitude ?lat_end . \n" +
                                         "  FILTER (?lon_end >= " + lon_end + " && ?lat_end >= " + lat_end + ") . \n" +
                                         "  FILTER (?lon1-?lon<=" + this.locationJoinFilter + " && ?lat1-?lat<= " + this.locationJoinFilter + ") .\n" +
                                         "  FILTER (?lon_end1-?lon_end<=" + this.locationJoinFilter + " && ?lat_end1-?lat_end<= " + this.locationJoinFilter + ") .\n" +
                                         "  FILTER (?lon_end-?lon_end1<=" + this.locationJoinFilter + " && ?lat_end-?lat_end1<= " + this.locationJoinFilter + ") } .\n");
                    } else {
                    	//just get the values
                    	queryText.append("  OPTIONAL { ?location spatial:has_end_longitude ?lon_end .\n");
                        queryText.append("                   ?location spatial:has_end_latitude ?lat_end . }\n" +
		                        		"  FILTER (?lon_end1-?lon_end<=" + this.locationJoinFilter + " && ?lat_end1-?lat_end<= " + this.locationJoinFilter + ") .\n" +
		                                "  FILTER (?lon_end-?lon_end1<=" + this.locationJoinFilter + " && ?lat_end-?lat_end1<= " + this.locationJoinFilter + ") } .\n");
                    }

                } else if (this.locationJoin) {
                	//no filter expression so that means that these have to match...
                	//set the bounding box values for the query
	                if (lat.length() != 0 && lon.length() !=0 && lon_end.length() != 0 && lat_end.length() != 0) {

	                    //Select all values with the lat / lon values
	                    queryText.append("  FILTER (?lon <= " + lon + " && ?lat <= " + lat + ") .\n");
	                    queryText.append("  FILTER (?lon1 = ?lon && ?lat1 = ?lat) .\n");

	                    //setup the optional tags for the lat / lon ends
                    	queryText.append("  OPTIONAL { ?location spatial:has_end_longitude ?lon_end .\n");
                        queryText.append("                   ?location spatial:has_end_latitude ?lat_end . \n" +
                        				 "  FILTER (?lon_end >= " + lon_end + " && ?lat_end >= " + lat_end + ")\n" +
                    					 "  FILTER (?lon_end1 = ?lon_end && ?lat_end1 = ?lat_end) } .\n");

	                } else {
	                	queryText.append("  FILTER (?lon1 = ?lon && ?lat1 = ?lat) .\n");
	                	//setup the optional tags for the lat / lon ends
	                	queryText.append("  OPTIONAL { ?location spatial:has_end_longitude ?lon_end .\n");
                        queryText.append("                   ?location spatial:has_end_latitude ?lat_end . \n" +
                    					 "  FILTER (?lon_end1 = ?lon_end && ?lat_end1 = ?lat_end) } .\n");
	                }
                } else {
                	//just get the lat / lon variables and apply the filter if i have them
                	//set the bounding box values for the query
	                if (lat.length() != 0 && lon.length() !=0 && lon_end.length() != 0 && lat_end.length() != 0) {

	                    //Select all values with the lat / lon values
	                    queryText.append("  FILTER (?lon <= " + lon + " && ?lat <= " + lat + ") .\n");

	                    //setup the optional tags for the lat / lon ends
                    	queryText.append("  OPTIONAL { ?location spatial:has_end_longitude ?lon_end .\n");
                        queryText.append("                   ?location spatial:has_end_latitude ?lat_end . \n" +
                        				"  FILTER (?lon_end >= " + lon_end + " && ?lat_end >= " + lat_end + ") } .\n");


	                } else {
	                	//just get the variables
	                	queryText.append("  OPTIONAL { ?location spatial:has_end_longitude ?lon_end .\n" +
	                					 "                   ?location spatial:has_end_latitude ?lat_end . } \n");
	                }
                }
            }

            //get the datetime form the context
        	queryText.append("  ?context context:has_datetime ?datetime .\n");

            //add this variable into the array of set vars
            setVariables.put("?locname","?locname");

            //test if we have somthing set
            if (this.queryPanels[0].location.isDirty()) {
//
//            		//test against the bottom panel if set
//	            	if (!this.queryPanels[1].location.getValueAsString().equalsIgnoreCase("")) {
//	            		//check if they differ
//	            		if (this.queryPanels[1].location.getValueAsString().compareToIgnoreCase(this.queryPanels[0].location.getValueAsString()) !=0) {
//	            			//alert the user that these values must be the same if set in both and joining on the location
//	            			MessageBox.alert("Error: If joining on location and location name is set to different values in both panels this is nonsensical." +
//	            					"<br>Please change to the same values or leave the top panel unset.");
//	            			//stop here.
//	            			return "";
//	            		}
//	            		//carry on and set the location value to be the same as the bottom panel.
//	            		//if it was set in bottom panel this will cascade -> otherwise we will capture the name here -> both have the same effect.
//	                    queryText.append("  ?location spatial:has_name ?locname .\n");
//	            	} else {
    			//set the location name with the filter as it wasn't set in the bottom panel..
            	queryText.append("  ?location spatial:has_name ?locname .\n");
                //need to offer the user a list of known location names
                queryText.append("  FILTER regex(?locname, \"" + this.queryPanels[0].location.getValueAsString() + "\", \"i\") .\n");
            } else {
	        	//set the location name
	            queryText.append("  ?location spatial:has_name ?locname .\n");
	    	}

            //if the depth values are set
            if (this.queryPanels[0].depth.isDirty()) {
                //get the depth variable
                queryText.append("  ?location spatial:has_depth ?depth .\n");
                //need to offer the user a list of known location names
                queryText.append("  FILTER (?depth "+ this.queryPanels[0].depth.getValueAsString() + ") .\n");
            }

            //need to get the location variables for matching
            queryText.append("  ?context context:has_location ?location .\n");

            //add this variable into the array of set vars
            setVariables.put("?unitsymbol","?unitsymbol");
            //the units we are working on
            queryText.append("  ?unit abstract:has_description ?unitsymbol .\n");
            setVariables.put("?measurement","?measurement");
            //get the unit var for use in query
            queryText.append("  ?measurement event:has_unit ?unit .\n");
            if (this.queryPanels[0].unit_value.isDirty()) {
                //set the filter with a measurement value
                queryText.append("  FILTER regex(?unitsymbol, \"" + this.queryPanels[0].unit_value.getValueAsString() + "\",\"i\") .\n");
            }

            //add this variable into the array of set vars
            setVariables.put("?value","?value");
            //actual measurement values
            queryText.append("  ?measurement event:has_value ?value .\n");
            if (this.queryPanels[0].measurement_value.isDirty()) {
                //set the filter with a measurement value
                queryText.append("  FILTER (?value " + this.queryPanels[0].measurement_value.getValueAsString() + ") .\n");
            }

            //add this variable into the array of set vars
            setVariables.put("?characteristic","?characteristic");
            //the characteristic class we are searching for
            queryText.append("  ?characteristic_type abstract:has_description ?characteristic .\n");
            queryText.append("  ?measurement event:has_characteristic ?characteristic_type .\n");
            if (this.queryPanels[0].characteristic.isDirty()) {
                //set the characteristic type from the UI
                queryText.append("  ?measurement event:has_characteristic " + this.queryPanels[0].characteristic.getValueAsString() + " .\n");

            }

            //add this variable into the array of set vars
            setVariables.put("?actorname","?actorname");
            //the actor name we are looking for
            queryText.append("  ?actor actor:has_name ?actorname .\n");
            if (this.queryPanels[0].actor.isDirty()) {
                //set the actor name
                queryText.append("  FILTER regex(?actorname, \"" + this.queryPanels[0].actor.getValueAsString() + "\",\"i\") .\n");
            }

            //get the actor instance for query matching
            queryText.append("  ?instance event:has_actor ?actor .\n");

            //add the sensor class type here...
            if (this.queryPanels[0].sensor.isDirty()) {
                //the sensor type we are looking for
                queryText.append("  ?actor rdf:type physical:Sensor .\n");
            }

            //test if the ecological process is dirty
            if (this.queryPanels[0].ecological_process.isDirty()) {
                //get the ecological process
                queryText.append("  ?instance event:is_part_of_ecological_process ?eco_process .\n");
                queryText.append("  ?eco_process rdf:type " + this.queryPanels[0].ecological_process.getValueAsString() + " .\n");
            }

            //test if the research program is dirty
            if (this.queryPanels[0].research_program.isDirty()) {
                //add this variable into the array of set vars
                setVariables.put("?group_name","?group_name");
                //get the reaserch program
                queryText.append("  ?research_program abstract:has_group ?group_name .\n");
                queryText.append("  ?instance event:has_research_program ?research_program .\n");
                queryText.append("  FILTER regex(?research_program, \"" + this.queryPanels[0].research_program.getValueAsString() + "\",\"i\") .\n");

            }

            //TODO: Extend the UI to hold both input and output fields for the workflow processing ->
            //make their own Field Set and arrange here accordingly.
//    		//test if the process output step is dirty
//    		if (process_output.isDirty()) {
//    			//get the data workflow instance
//    			queryText.append("  FILTER regex(?workflow_input, \"" + process_output.getValueAsString() + "\",\"i\") .\n");
//    			queryText.append("  ?workflow event:has_input ?workflow_input .\n");
//    			queryText.append("  ?instance event:output_of ?workflow .\n");
//    		}

            //only use these if they have been set.
            if (this.queryPanels[0].genus.isDirty()){
                //add this variable into the array of set vars
                setVariables.put("?genus","?genus");
                //set the genus of the query
                queryText.append("  ?instance event:has_genus ?genus .\n");
                //add the target string to the field string
                queryText.append("  FILTER regex(?genus, \"" + this.queryPanels[0].genus.getValueAsString() + "\",\"i\") .\n");
            }

            if (this.queryPanels[0].species.isDirty()){
                //add this variable into the array of set vars
                setVariables.put("?species","?species");
                //set the species of the query
                queryText.append("  ?instance event:has_species ?species .\n");
                //add the target string to the field string
                queryText.append("  FILTER regex(?species, \"" + this.queryPanels[0].species.getValueAsString() + "\",\"i\") .\n");
            }

            if (this.queryPanels[0].morphology.isDirty()){
                //add this variable into the array of set vars
                setVariables.put("?morphology","?morphology");
                //set the morphology of the query
                queryText.append("  ?instance event:has_morphology ?morphology .\n");
                //add the target string to the field string
                queryText.append("  FILTER regex(?morphology, \"" + this.queryPanels[0].morphology.getValueAsString() + "\",\"i\") .\n");
            }

            //add this variable into the array of set vars
            setVariables.put("?target","?target");
            //set the target of the query
            queryText.append("  ?target_type abstract:has_description ?target .\n");
            queryText.append("  ?instance event:of_target ?target_type .\n");
            if (this.queryPanels[0].target.isDirty()){
                //add the target string to the field string
                queryText.append("  ?instance event:of_target " + this.queryPanels[0].target.getValueAsString() +" .\n");
            }

	        //add the observation and instance sparql joins.
		    queryText.append(endEachQueryTemplate);

	        //String builder to construct the list of variables
	        StringBuilder variables = new StringBuilder();

	        int keyCounter = 0;
	        //now i need to construct the string which selects only certain variables of the query.
	        for (String key : setVariables.keySet()) {
	            //reached the end of the variables
	            if (keyCounter == setVariables.keySet().size()-1) {
	            	variables.append(setVariables.get(key));
	            }else {
	            	variables.append(setVariables.get(key) + " ");
	            }
	            //inc my keyCounter...
	            keyCounter++;
	        }

	        //now set the queryField text comprising all the built strings
	        this.queryField.setValue(this.queryField.getValueAsString() + variables + endHeader + queryText + endQueryTemplate);

	        //if i passed the limit var - used to restrict the size of the result set
	        //this will decrease time of queries on very large oness
	        if (limit) {
	        	//add the limit clause
	        	this.queryField.setValue(this.queryField.getValueAsString() + limitTemplate);

	        }
        }
    	} catch (Exception e) { e.printStackTrace(); }

        //now send the string back
        return this.queryField.getValueAsString();
    }

    /**
     * Method to build the UNION type SPARQL query from the 2 Query UI Panels
     * Simple case of each panel holds a query and a UNION clause creates the superset of both sub clauses.
     * @param Boolean specifying we want to limit the result set -> Defaults to 200
     * @result String the UI fields of the panels converted into a SPARQL query.
     */
    private String buildUnionSPARQLQuery(boolean limit){

    	//Union queries are easy to build -> each panel reflects and individual query with a UNOIN separator set.
        //setup my date formatter
        DateTimeFormat date_fmt = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");

        //capture all the variables we have set...
        HashMap<String,String> setVariables = new HashMap<String,String>();

        //String Builder to make the query correctly
        StringBuilder queryText = new StringBuilder();

        //set my title for the Query Field here
        queryPanel.setTitle("Manually constructed UNION Query");

        //here is my template
        String queryTemplate = "PREFIX event: <http://maenad.itee.uq.edu.au/metadata/Event#>\n" +
                                    "PREFIX spatial: <http://maenad.itee.uq.edu.au/metadata/Spatial#>\n" +
                                    "PREFIX context: <http://maenad.itee.uq.edu.au/metadata/Context#>\n" +
                                    "PREFIX temporal: <http://maenad.itee.uq.edu.au/metadata/Temporal#>\n" +
                                    "PREFIX physical: <http://maenad.itee.uq.edu.au/metadata/Physical#>\n" +
                                    "PREFIX actor: <http://maenad.itee.uq.edu.au/metadata/Actor#>\n" +
                                    "PREFIX abstract: <http://maenad.itee.uq.edu.au/metadata/Abstract#>\n" +
                                    "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                                    "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                                    "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>\n\n" +
                                    "SELECT DISTINCT ";

        String endHeader = 		"\nWHERE {\n";

        String endEachQueryTemplate = "  ?instance event:has_context ?context .\n" +
                                        "  ?instance event:has_measurement ?measurement .\n" +
                                        "  ?instance rdf:type ?observation .\n" +
                                        "  ?observation rdfs:subClassOf event:Observation .\n";

        String unionHeader = 		"}\nUNION\n{\n";

        String endQueryTemplate =	"}\n";

        String limitTemplate =	"LIMIT " + this.queryLimit + "\n";

        //setup the query text field with the start of the query string
        queryField.setValue(queryTemplate);

        //loop over each of the query panels i have setup here and create the SPARQL statements
        //Note: Limited to 2 at the moment...
        int visiblePanels = 1;
        if (this.queryPanels[1].columnPanel.isVisible()) {
            visiblePanels = 2;
        }
        for (int i=0; i < visiblePanels; i++) {

            this.date.setTime(System.currentTimeMillis());
//            GWT.log(this.dtf.format(date) + " " + this.toString() + " :: " + this.queryPanels.length, null);
//            GWT.log(this.dtf.format(date) + " " + this.toString() + " :: " + this.queryPanels[i].toString(), null);

            //add this variable into the array of set vars
            setVariables.put("?begindate", "?begindate");
            if (this.queryPanels[i].start_date.isDirty()) {
                //need to format the expected date time string into the valid format for the query
                String[] start_dates = date_fmt.format(queryPanels[i].start_date.getValue()).split(" ");
                //test if i want the range of specific values
                if (this.queryPanels[i].datesAsRange.getValue()){
                    //set the date as a specific value
                    queryText.append("  ?datetime temporal:has_datetime \"" + start_dates[0] + "T" + start_dates[1] + "\"^^xsd:dateTime .\n");
                } else {
                    //set the start date time field
                    queryText.append("  ?datetime temporal:has_datetime ?begindate .\n");
                    //set the date as a range
                    queryText.append("  FILTER (?begindate >= \"" + start_dates[0] + "T" + start_dates[1] + "\"^^xsd:dateTime) .\n");
                }
            } else {
                //set the start date time field
                queryText.append("  ?datetime temporal:has_datetime ?begindate .\n");
            }

            //add this variable into the array of set vars
            setVariables.put("?enddate","?enddate");
            //test if we have set values
            if (this.queryPanels[i].end_date.isDirty()) {
                //get the date value
                String[] end_dates = date_fmt.format(this.queryPanels[i].end_date.getValue()).split(" ");
                //test if i want the range of specific values
                if (this.queryPanels[i].datesAsRange.getValue()){
                    //set the date as a specific value
                    queryText.append("  ?datetime temporal:has_end_datetime \"" + end_dates[0] + "T" + end_dates[1] + "\"^^xsd:dateTime .\n");
                } else {
                    //set the start date time field
                    queryText.append("  OPTIONAL { ?datetime temporal:has_end_datetime ?enddate .\n");
                    //set the date as a range
                    queryText.append("  FILTER (?enddate <= \"" + end_dates[0] + "T" + end_dates[1] + "\"^^xsd:dateTime) } .\n");
                }
            } else {
                //set the start date time field
                queryText.append("  OPTIONAL { ?datetime temporal:has_end_datetime ?enddate } .\n");
            }

            //get the datetime variable used to get the begin and end dates
            queryText.append("  ?context context:has_datetime ?datetime .\n");

            //variables for my coords
            String lon = "";
            String lat = "";
            String lon_end = "";
            String lat_end = "";

            //set the longitude & latitude vars
            queryText.append("  ?location spatial:has_longitude ?lon .\n");
            queryText.append("  ?location spatial:has_latitude ?lat .\n");

            //add this variable into the array of set vars
            setVariables.put("?lat","?lat");
            //add this variable into the array of set vars
            setVariables.put("?lon","?lon");

            //am i using point or region selectors?
            if (this.queryPanels[i].regionPoint.getActiveItem().getText().equalsIgnoreCase("Point")){

                //i am using the point selector
                lon = this.queryPanels[i].longitude.getValueAsString();
                lat = this.queryPanels[i].latitude.getValueAsString();

                //make sure the fields are set
                if (lon.length() != 0 && lat.length() !=0) {
                    //Select all values with the lat / lon values
                    queryText.append("  FILTER (?lon = " + lon + " && ?lat = " + lat + ") .\n");
                }
            } else {

                //get the values from the region fields
                lon = this.queryPanels[i].east.getValueAsString();
                lat = this.queryPanels[i].north.getValueAsString();
                lon_end = this.queryPanels[i].west.getValueAsString();
                lat_end = this.queryPanels[i].south.getValueAsString();

                //add this variable into the array of set vars
                setVariables.put("?lat_end","?lat_end");
                //add this variable into the array of set vars
                setVariables.put("?lon_end","?lon_end");

                //set the bounding box values for the query
                if (lat.length() != 0 && lon.length() !=0 && lon_end.length() != 0 && lat_end.length() != 0) {

                    //Select all values with the lat / lon values
                    queryText.append("  FILTER (?lon <= " + lon + " && ?lat <= " + lat + ") .\n");

                    if (this.queryPanels[i].region_as_range.getValue() && (!this.queryPanels[i].region_as_range.isDisabled())){
                        //setup the optional tags for the lat / lon ends
                        queryText.append("  OPTIONAL { ?location spatial:has_end_longitude "+ lon_end + "^^xsd:float .\n");
                        queryText.append("                   ?location spatial:has_end_latitude " + lat_end + "^^xsd:float } .\n");
                    } else {
                        queryText.append("  OPTIONAL { ?location spatial:has_end_longitude ?lon_end .\n");
                        queryText.append("                   ?location spatial:has_end_latitude ?lat_end . \n" +
                                         "  FILTER (?lon_end >= " + lon_end + " && ?lat_end >= " + lat_end + ") } .\n");
                    }

                } else {
                    queryText.append("  OPTIONAL { ?location spatial:has_end_longitude ?lon_end .\n");
                    queryText.append("                   ?location spatial:has_end_latitude ?lat_end } .\n");
                }

            }

            //add this variable into the array of set vars
            setVariables.put("?locname","?locname");
            //set the location name
            queryText.append("  ?location spatial:has_name ?locname .\n");
            if (this.queryPanels[i].location.isDirty()) {
                //need to offer the user a list of known location names
                queryText.append("  FILTER regex(?locname, \"" + this.queryPanels[i].location.getValueAsString() + "\", \"i\") .\n");
            }

            //if the depth values are set
            if (this.queryPanels[i].depth.isDirty()) {
            	//add this variable into the array of set vars
                setVariables.put("?depth","?depth");
                //get the depth variable
                queryText.append("  OPTIONAL { ?location spatial:has_depth ?depth .\n");
                //need to offer the user a list of known location names
                queryText.append("  FILTER (?depth "+ this.queryPanels[i].depth.getValueAsString() + ") } .\n");
            }

            //need to get the location variables for matching
            queryText.append("  ?context context:has_location ?location .\n");

            //add this variable into the array of set vars
            setVariables.put("?unitsymbol","?unitsymbol");
            //the units we are working on
            queryText.append("  ?unit abstract:has_description ?unitsymbol .\n");
        	//get the unit var for use in query
            //add this variable into the array of set vars
            setVariables.put("?measurement","?measurement");
            queryText.append("  ?measurement event:has_unit ?unit .\n");
            if (this.queryPanels[i].unit_value.isDirty()) {
                //set the filter with a measurement value
                queryText.append("  FILTER regex(?unitsymbol, \"" + this.queryPanels[i].unit_value.getValueAsString() + "\",\"i\") .\n");
            }

            //add this variable into the array of set vars
            setVariables.put("?value","?value");
            //actual measurement values
            queryText.append("  ?measurement event:has_value ?value .\n");
            if (this.queryPanels[i].measurement_value.isDirty()) {
                //set the filter with a measurement value
                queryText.append("  FILTER (?value " + this.queryPanels[i].measurement_value.getValueAsString() + ") .\n");
            }

            //add this variable into the array of set vars
            setVariables.put("?characteristic","?characteristic");
            //the characteristic class we are searching for
            queryText.append("  ?characteristic_type abstract:has_description ?characteristic .\n");
            queryText.append("  ?measurement event:has_characteristic ?characteristic_type .\n");
            if (this.queryPanels[i].characteristic.isDirty()) {
                //set the characteristic type from the UI
                queryText.append("  ?measurement event:has_characteristic " + this.queryPanels[i].characteristic.getValueAsString() + " .\n");

            }

            //add this variable into the array of set vars
            setVariables.put("?actorname","?actorname");
            //the actor name we are looking for
            queryText.append("  ?actor actor:has_name ?actorname .\n");
            if (this.queryPanels[i].actor.isDirty()) {
                //set the actor name
                queryText.append("  FILTER regex(?actorname, \"" + this.queryPanels[i].actor.getValueAsString() + "\",\"i\") .\n");
            }

            //if the actor is a sensor && the sensor check is on
            if (this.queryPanels[i].sensor.getValue()) {
            	//add this variable into the array of set vars
                setVariables.put("?sensor","?sensor");
                //set the sensor type in the actor slot
                queryText.append("?actor rdf:type physical:Sensor .\n");
            }

            //get the actor instance for query matching
            queryText.append("  ?instance event:has_actor ?actor .\n");

            //add the sensor class type here...
            if (this.queryPanels[i].sensor.isDirty()) {
                //the sensor type we are looking for
                queryText.append("  ?actor rdf:type physical:Sensor .\n");
            }

            //test if the ecological process is dirty
            if (this.queryPanels[i].ecological_process.isDirty()) {
                //get the ecological process
                queryText.append("  ?instance event:is_part_of_ecological_process ?eco_process .\n");
                queryText.append("  ?eco_process rdf:type " + this.queryPanels[i].ecological_process.getValueAsString() + " .\n");
            }

            //test if the research program is dirty
            if (this.queryPanels[i].research_program.isDirty()) {
                //add this variable into the array of set vars
                setVariables.put("?group_name","?group_name");
                //get the reaserch program
                queryText.append("  ?research_program abstract:has_group ?group_name .\n");
                queryText.append("  ?instance event:has_research_program ?research_program .\n");
                queryText.append("  FILTER regex(?research_program, \"" + this.queryPanels[i].research_program.getValueAsString() + "\",\"i\") .\n");

            }

            //TODO: Extend the UI to hold both input and output fields for the workflow processing ->
            //make their own Field Set and arrange here accordingly.
    //		//test if the process output step is dirty
    //		if (process_output.isDirty()) {
    //			//get the data workflow instance
    //			queryText.append("  FILTER regex(?workflow_input, \"" + process_output.getValueAsString() + "\",\"i\") .\n");
    //			queryText.append("  ?workflow event:has_input ?workflow_input .\n");
    //			queryText.append("  ?instance event:output_of ?workflow .\n");
    //		}

	        //only use these if they have been set.
	        if (this.queryPanels[i].genus.isDirty()){
	            //add this variable into the array of set vars
	            setVariables.put("?genus","?genus");
	            //set the genus of the query
	            queryText.append("  ?instance event:has_genus ?genus .\n");
	            //add the target string to the field string
	            queryText.append("  FILTER regex(?genus, \"" + this.queryPanels[i].genus.getValueAsString() + "\",\"i\") .\n");
	        }

	        if (this.queryPanels[i].species.isDirty()){
	            //add this variable into the array of set vars
	            setVariables.put("?species","?species");
	            //set the species of the query
	            queryText.append("  ?instance event:has_species ?species .\n");
	            //add the target string to the field string
	            queryText.append("  FILTER regex(?species, \"" + this.queryPanels[i].species.getValueAsString() + "\",\"i\") .\n");
	        }

	        if (this.queryPanels[i].morphology.isDirty()){
	            //add this variable into the array of set vars
	            setVariables.put("?morphology","?morphology");
	            //set the morphology of the query
	            queryText.append("  ?instance event:has_morphology ?morphology .\n");
	            //add the target string to the field string
	            queryText.append("  FILTER regex(?morphology, \"" + this.queryPanels[i].morphology.getValueAsString() + "\",\"i\") .\n");
	        }

            //add this variable into the array of set vars
            setVariables.put("?target","?target");
            //set the target of the query
            queryText.append("  ?target_type abstract:has_description ?target .\n");
            queryText.append("  ?instance event:of_target ?target_type .\n");
            if (this.queryPanels[i].target.isDirty()){
                //add the target string to the field string
                queryText.append("  ?instance event:of_target " + this.queryPanels[i].target.getValueAsString() +" .\n");
            }
            //add the observation and instance sparql joins.
            queryText.append(endEachQueryTemplate);

            //test if we have another panel to set a query for
            if (i < visiblePanels-1) {
                //add the union clause and we loop over again.
                queryText.append(unionHeader);
            }
        }

        //String builder to construct the list of variables
        StringBuilder variables = new StringBuilder();

        int keyCounter = 0;
        //now i need to construct the string which selects only certain variables of the query.
        for (String key : setVariables.keySet()) {
            //reached the end of the variables
            if (keyCounter == setVariables.keySet().size()-1) {
            	variables.append(setVariables.get(key));
            }else {
            	variables.append(setVariables.get(key) + " ");

            }
            //inc my keyCounter...
            keyCounter++;
        }

        //now set the queryField text comprising all the built strings
        this.queryField.setValue(this.queryField.getValueAsString() + variables + endHeader + queryText + endQueryTemplate);

        //if i passed the limit var - used to restrict the size of the result set
        //this will decrease time of queries on very large oness
        if (limit) {
        	//add the limit clause
        	this.queryField.setValue(this.queryField.getValueAsString() + limitTemplate);

        }

        //now send the string back
        return this.queryField.getValueAsString();
    }

    /*
     * Load the UI fields from the example grid
     */
    private void loadExampleQuery() {

    	 //if the query fields are incorrectly setup in the XML file then it may not work properly here..
        try {
        	//make sure we have selected a row!
        	if (rowSelectionModel.getSelected() == null) {
        		//notify user to select a row
            	MessageBox.alert("Please select an example query to load.");
        	} else {
		        //setup centre panel
		        this.centrePanel.setActiveTab("queryEditTab");
		        //setup the queryPanel title
		        this.queryPanel.setTitle(rowSelectionModel.getSelected().getAsString("description"));
		        //set the query text
		        this.queryField.setValue(rowSelectionModel.getSelected().getAsString("query_string"));
		        //set the query type (Union / JOIN / NORMAL)
		        this.queryType = Boolean.parseBoolean(rowSelectionModel.getSelected().getAsString("join_query"));
		        //get query join fileds
		        //TODO: Extend the list of joins / make arbitrary.
		        this.locationJoin = Boolean.parseBoolean(rowSelectionModel.getSelected().getAsString("join_query_location"));
		        this.datetimeJoin = Boolean.parseBoolean(rowSelectionModel.getSelected().getAsString("join_query_datetime"));
	
		        //set the location filter join values
		        if (!rowSelectionModel.getSelected().getAsString("location_filter").equalsIgnoreCase("null")) {
		        	//then we copy over the values and setup the join for the query build of the UI
		        	this.locationJoin = true;
		        	this.locationJoinFilter = rowSelectionModel.getSelected().getAsString("location_filter");
		        } else {
		        	//set the values back to unset
		        	this.locationJoin = false;
		        	this.locationJoinFilter = "";
		        }
	
		        //set the datetime filter join values
		        if (!rowSelectionModel.getSelected().getAsString("datetime_filter").equalsIgnoreCase("null")) {
		        	//then we copy over the values and setup the join for the query build of the UI
		        	this.datetimeJoin = true;
		        	this.datetimeJoinFilter = rowSelectionModel.getSelected().getAsString("datetime_filter");
		        } else {
		        	//set the values back to unset
		        	this.datetimeJoin = false;
		        	this.datetimeJoinFilter = "";
	
		        	//TODO: Check why this isn't building properly...
		        }
		        
	        	//used to index into the panel array -> 0 is first panel..
	            int uiPanelIndex = 0;
	
		        //loop over each field and test if it's null...if not then set the correct UI field
		        for (String field : rowSelectionModel.getSelected().getFields()){
	
		            //set the value to be empty by default
		            String fieldValue = "";
	
		            //check that each value is not null and set the correct field
		            if (! rowSelectionModel.getSelected().getAsString(field).equalsIgnoreCase("null")){
		                //set a specific value if not null in the XML file
		                fieldValue = rowSelectionModel.getSelected().getAsString(field);
		            }
	    
		            //temp workaround - use only the first query panel
	                if (field.charAt(field.length()-1) == '1') {
	//                	System.out.println("Found my 1vars, skipping this loop. Also my uiPanelIndex is : " + uiPanelIndex);
	                    //skip the field
	                	break;                    
	                }
	
	//                //TODO make this more dynamic
	//                //get the panel id out of the variable name...all variable names with 1 on end are second panel.
	//                if (field.charAt(field.length()-1) == '1') {
	//                    //second panel
	//                    uiPanelIndex = 1;
	//                    //and clean up the field name -> remove the index from it
	//                    field = field.substring(0, field.length()-1);
	//                }
	
	//                //test if this query will trigger the UI to build a UNION or a join query if the second panel is visible and set.
	//                if (field.equalsIgnoreCase("query_string")){
	//	                //see if it's a union query
	//                	if (fieldValue.contains("UNION")) {
	//                		GWT.log("Found a UNION CLAUSE IN THE QUERY", null);
	//	                	this.queryType = false;
	//	                } else {
	//	                	this.queryType = false;
	//	                }
	//                }
	
	                //set the fields value -> set it empty if null : Reset the query form
		            setUIQueryField(uiPanelIndex, field, fieldValue);
	
	            //extend the XML file to have multiple queryFields ie. lat, lon, lat1 lon1 and so on.
	            //Only maxing out at 2 query fields so simple model should work.
	
	            //TODO: IF i set the bounding box i want a box to symolies it on screen - > i want to draw a box on the map representing it!
	            //TODO: Make this box resizable / modifiable so i can drive the map window
		        }
	
	//	        //second panel should only be visible if we set a value in it..
	//	        if (isQueryUIDirty(1)) {
	//	        	this.queryPanels[1].columnPanel.setVisible(true);
	//	        	//if i make this visible then i have to disable the button to add a new query panel
	//	        	this.queryTypeButton.setDisabled(true);
	//
	//	        	//can i resize the north panel to show both panels
	//	        	extendQueryPanelHeight();
	//	        }
	//	        else {
	//	        	this.queryPanels[1].columnPanel.setVisible(false);
	//	        	this.queryTypeButton.setDisabled(false);
	//
	//	        	//can i resize the north panel to show both panels
	//	        	shrinkQueryPanelHeight();
	//	        }
        	}
        }
        catch (Exception e) {
        	e.printStackTrace();
        	//Seems that we had an error on lookup for the field - Most likely cause is and incorrect Query XML file.
        	MessageBox.alert("Problem accessing the Example Query Properties, please contact your system administrator.");
        }
    }

    /*
     * Helper method to extend the North Panel to show only the first query UI panel
     */
    private void extendQueryPanelHeight() {
    	this.northPanel.setHeight(445);
    	this.borderPanel.doLayout();
    }

    /*
     * Helper method to shrink the North Panel to show only the first query UI panel
     */
    private void shrinkQueryPanelHeight() {
    	this.northPanel.setHeight(255);
    	this.borderPanel.doLayout();
    }

    /*
     * Helper method the clear the UI components.
     */
    private void clearQueryUI() {

    	//make sure that i have a handle on the first query in the examples list...
    	//This is a cheat to know what fields i have to set.

    	//only force the first row if i don't have any previously set.
    	if (this.rowSelectionModel.getSelected() == null) {
    		this.rowSelectionModel.selectFirstRow();
    	}

	    //use the selected example query as a cheat to get the field value name.
        for (String field : rowSelectionModel.getSelected().getFields()){

        	//get an index into which panel we are working on
        	int uiPanelIndex = 0;

        	//TODO make this more dynamic
            //get the panel id out of the variable name...all variable names with 1 on end are second panel.
            if (field.charAt(field.length()-1) == '1') {
//                //second panel
//                uiPanelIndex = 1;
//                //and clean up the field name -> remove the index from it
//                field = field.substring(0, field.length()-1);
            	//temp workaround
            	break;
            }

            //use the specific clearUI method
            clearQueryUI(uiPanelIndex, field);
        }
    }

    /**
     * Specific function to clear a field in a UI Panel.
     * @param int The panel index
     * @param String the field to be cleared.
     */
    private void clearQueryUI(int uiPanelIndex, String field) {

	    //set field to be empty...
        setUIQueryField(uiPanelIndex, field, "");
    }

    /**
     * Helper method the clear the field of a specific UI panel.
     */
    private void clearQueryUI(int uiPanelIndex) {

    	//make sure that i have a handle on the first query in the examples list...
    	//This is a cheat to know what fields i have to set.

    	//only force the first row if i don't have any previously set.
    	if (this.rowSelectionModel.getSelected() == null) {
    		this.rowSelectionModel.selectFirstRow();
    	}

	    //use the selected example query as a cheat to get the field value name.
        for (String field : rowSelectionModel.getSelected().getFields()){

        	//TODO make this more dynamic
            //get the panel id out of the variable name...all variable names with 1 on end are second panel.
            if (!(field.charAt(field.length()-1) == '1')) {
            	//only clear the set of fields for a the panel
                clearQueryUI(uiPanelIndex, field);
            }
        }
    }

    /*
     * Validate the query panels
     */
    private boolean validateQueryPanels() {
        //bool to test if any of the panels fail

//    	this.date.setTime(System.currentTimeMillis());
//		GWT.log(this.dtf.format(date) + " " + this.toString() + " :: Testing the Validity of the query panels", null);

        Boolean isValid = true;
        //Loop here over all the UI panels we have in the array
        for (int i=0; i < queryPanels.length; i++) {

            //check that our forms fields are valid
            if (!(queryPanels[i].queryFieldPanel.getForm().isValid() && queryPanels[i].organismQueryPanel.getForm().isValid()
                    && queryPanels[i].datePanel.getForm().isValid() && queryPanels[i].locationVarPanel.getForm().isValid())) {

//        		this.date.setTime(System.currentTimeMillis());
//        		GWT.log(this.dtf.format(date) + " " + this.toString() + " :: one of the form panels returned false", null);

                //set the flag if any panel is invalid
                return false;
            }

            //only test dates if they are set
            if (!(this.queryPanels[i].start_date.getValueAsString().equalsIgnoreCase("") || this.queryPanels[i].end_date.getValueAsString().equalsIgnoreCase(""))) {
                //test our date variables to make sure that begin is before end
                if (this.queryPanels[i].start_date.getValue().after(this.queryPanels[i].end_date.getValue())){
                    MessageBox.alert("End date cannot be before begin date.");
                    return false;
                }
            }

            //test our mapping variables to make sure that they are within our ROI and make sense regarding each other.
//         	ROI 149.5 -> 153 ; 21.5 -> 24.5 for the DATA SET
            if (this.queryPanels[i].regionPoint.getActiveItem().getText().equalsIgnoreCase("Point")) {

                //only test if the ranges are set
                if (!(queryPanels[i].latitude.getValueAsString().equalsIgnoreCase("") || queryPanels[i].longitude.getValueAsString().equalsIgnoreCase(""))) {
                    //setup variables
                    Float lat = new Float(0.0);
                    Float lon = new Float(0.0);
                    //try and parse the values into a float.
                    try {
                        lat = Float.parseFloat(queryPanels[i].latitude.getValueAsString());
                        lon = Float.parseFloat(queryPanels[i].longitude.getValueAsString());
                    } catch (Exception e) {
                        this.date.setTime(System.currentTimeMillis());
                        GWT.log(this.dtf.format(date) + " " + this.toString() + " :: error parsing the float" + e.getLocalizedMessage(), null);
                        GWT.log(this.dtf.format(date) + " " + this.toString() + " :: " + lat.toString() + " : " + lon.toString(), null);
                        e.printStackTrace();
                    }

                    //now let's test the mapping variables to see if they lie in the correct ranges.
                    if (lat >= -21.5 || lat <= -24.5) {
                        //set the falg
                        isValid = false;
//            			GWT.log(this.dtf.format(date) + " " + this.toString() + " :: Set the value to be false : " + lat.toString() + " : " + lon.toString(), null);

                    }
                    //now let's test the mapping variables to see if they lie in the correct ranges.
                    if (lon <= 149.5 || lon >= 153.0) {
                        //set the falg
                        isValid = false;
//    	        		GWT.log(this.dtf.format(date) + " " + this.toString() + " :: Set the value to be false : " + lat.toString() + " : " + lon.toString(), null);
                    }

//    	        	GWT.log(this.dtf.format(date) + " " + this.toString() + " :: Vlues : " + lat.toString() + " : " + lon.toString(), null);
                }
                //alert the user they must complete both fields
                else if ((!queryPanels[i].latitude.getValueAsString().equalsIgnoreCase("")) || (!queryPanels[i].longitude.getValueAsString().equalsIgnoreCase(""))) {
                    MessageBox.alert("Please enter both Latitude and Longitude values.");
                    return false;
                }
            }
            else {

                //only test if the ranges are set
                if (!(queryPanels[i].north.getValueAsString().equalsIgnoreCase("") || queryPanels[i].south.getValueAsString().equalsIgnoreCase("") || queryPanels[i].east.getValueAsString().equalsIgnoreCase("") || queryPanels[i].west.getValueAsString().equalsIgnoreCase(""))) {

                    //setup variables
                    Float north = new Float(0.0);
                    Float south = new Float(0.0);
                    Float east = new Float(0.0);
                    Float west = new Float(0.0);

                    try{
                        north = Float.parseFloat(queryPanels[i].north.getValueAsString());
                        south = Float.parseFloat(queryPanels[i].south.getValueAsString());
                        east = Float.parseFloat(queryPanels[i].east.getValueAsString());
                        west = Float.parseFloat(queryPanels[i].west.getValueAsString());

                    } catch (Exception e) {
                        this.date.setTime(System.currentTimeMillis());
                        GWT.log(this.dtf.format(date) + " " + this.toString() + " :: error parsing the float" + e.getLocalizedMessage(), null);
                        GWT.log(this.dtf.format(date) + " " + this.toString() + " :: " + north.toString() + " : " + south.toString() + east.toString() + " : " + west.toString(), null);
                        e.printStackTrace();
                    }
                    //check that we the coords make sense.
                    if (north <= south) {
                        MessageBox.alert("North cannot be below south.");
                        return false;
                    }
                    if (east <= west){
                        MessageBox.alert("East cannot more westerly than west.");
                        return false;
                    }
                    //now let's test the mapping variables to see if they lie in the correct ranges.
                    if (north >= -21.5 && south >= -21.5) {
                        //set the falg
                        isValid = false;
                    }
                    //now let's test the mapping variables to see if they lie in the correct ranges.
                    if (north <= -24.5 && south <= -24.5) {
                        //set the falg
                        isValid = false;
                    }
                    //now let's test the mapping variables to see if they lie in the correct ranges.
                    if (east <= 149.5 && west <= 149.5) {
                        //set the falg
                        isValid = false;
                    }
                    //now let's test the mapping variables to see if they lie in the correct ranges.
                    if (east >= 153.0 && west >= 153.0) {
                        //set the falg
                        isValid = false;
                    }
                }
                else if ((!queryPanels[i].north.getValueAsString().equalsIgnoreCase("")) || (!queryPanels[i].south.getValueAsString().equalsIgnoreCase("")) || (!queryPanels[i].east.getValueAsString().equalsIgnoreCase("")) || (!queryPanels[i].west.getValueAsString().equalsIgnoreCase(""))) {
                    MessageBox.alert("Please enter North, South, East and West values.");
                    return false;
                }
            }
        }

//    	this.date.setTime(System.currentTimeMillis());
//		GWT.log(this.dtf.format(date) + " " + this.toString() + " :: made it to the end of the mapping teest..." + isValid.toString(), null);

        //notify the user that the mapping bound failed
        if (!isValid) {
            MessageBox.alert("The coordinates are outside the Data Set Boundaries: [N:-21.5, S:-24.5, E: 149.5, W:153.0]");
        }
        return isValid;
    }

    private boolean isQueryUIDirty() {
    	
    	//look out for a non-selected panel item...
    	try {
    		
    		//see if we have a row selected 
    		Record selectedRow = this.rowSelectionModel.getSelected();
    		
    		//make sure the we have a record to loop over
    		//get the first one if none selected as we need this to validate the UI fields
    		if (selectedRow == null) {
    			selectedRow = this.queryGrid.getStore().getRecordAt(0);
    		}    		
    		
    		//use the selected example query as a cheat to get the field value name.
	        for (String field : selectedRow.getFields()){
	        	
	        	//used to index into the panel array -> 0 is first panel..
	            int uiPanelIndex = 0;
	
	            //TODO make this more dynamic
	            //get the panel id out of the variable name...all variable names with 1 on end are second panel.
	            if (field.charAt(field.length()-1) == '1') {
	                //second panel
	                uiPanelIndex = 1;
	                //and clean up the field name -> remove the index from it
	                field = field.substring(0, field.length()-1);
	            }
	
	            //call the overriden dirty function here..
	            if (isQueryUIDirty(uiPanelIndex)) {
	            	//if we are dirty the return
	            	return true;
	            }
        }
    	} catch (Exception e) {
//    		GWT.log("Seems to be a problem with the tab panel not having a selection -> check this.", null);
    		e.printStackTrace();
    	}
        //if we get here then nothing was dirty.
        return false;
    }

    private boolean isQueryUIDirty(int uiPanelIndex) {
    	
    	//see if we have a row selected 
		Record selectedRow = this.rowSelectionModel.getSelected();
		
		//make sure the we have a record to loop over
		//get the first one if none selected as we need this to validate the UI fields
		if (selectedRow == null) {
			selectedRow = this.queryGrid.getStore().getRecordAt(0);
		}    	

    	try {
	        //use the selected example query as a cheat to get the field value name.
	        for (String field : selectedRow.getFields()){
	
	        	//TODO make this more dynamic
	            //get the panel id out of the variable name...all variable names with 1 on end are second panel.
	            if (field.charAt(field.length()-1) == '1') {
	                //don't check the fields that don't exist...i.e the ones that indicate they are second panel.
	            	return false;
	            }
	
	            //loop over each field...
	            switch (QueryFields.valueOf(field.toUpperCase()))
	            {
	            case NORTH:
	                if (!this.queryPanels[uiPanelIndex].north.getValueAsString().equalsIgnoreCase("")) return true;
	            case SOUTH:
	                if (!this.queryPanels[uiPanelIndex].south.getValueAsString().equalsIgnoreCase("")) return true;
	            case EAST:
	                if (!this.queryPanels[uiPanelIndex].east.getValueAsString().equalsIgnoreCase("")) return true;
	            case WEST:
	                if (!this.queryPanels[uiPanelIndex].west.getValueAsString().equalsIgnoreCase("")) return true;
	            case LAT:
	                if (!this.queryPanels[uiPanelIndex].latitude.getValueAsString().equalsIgnoreCase("")) return true;
	            case LON:
	                if (!this.queryPanels[uiPanelIndex].longitude.getValueAsString().equalsIgnoreCase("")) return true;
	            case START_DATE:
	                if (!this.queryPanels[uiPanelIndex].start_date.getValueAsString().equalsIgnoreCase("")) return true;
	            case END_DATE:
	                if (!this.queryPanels[uiPanelIndex].end_date.getValueAsString().equalsIgnoreCase("")) return true;
	            case TARGET:
	                if (!this.queryPanels[uiPanelIndex].target.getValueAsString().equalsIgnoreCase("")) return true;
	            case CHARACTERISTIC:
	                if (!this.queryPanels[uiPanelIndex].characteristic.getValueAsString().equalsIgnoreCase("")) return true;
	            case LOCATION:
	                if (!this.queryPanels[uiPanelIndex].location.getValueAsString().equalsIgnoreCase("")) return true;
	            case ACTOR:
	                if (!this.queryPanels[uiPanelIndex].actor.getValueAsString().equalsIgnoreCase("")) return true;
	            case MEASUREMENT_VALUE:
	                if (!this.queryPanels[uiPanelIndex].measurement_value.getValueAsString().equalsIgnoreCase("")) return true;
	            case UNIT:
	                if (!this.queryPanels[uiPanelIndex].unit_value.getValueAsString().equalsIgnoreCase("")) return true;
	            case RESEARCH_PROGRAM:
	                if (!this.queryPanels[uiPanelIndex].research_program.getValueAsString().equalsIgnoreCase("")) return true;
	            case ECOLOGICAL_PROCESS:
	                if (!this.queryPanels[uiPanelIndex].ecological_process.getValueAsString().equalsIgnoreCase("")) return true;
	            case PROCESS_OUTPUT:
	                if (!this.queryPanels[uiPanelIndex].process_output.getValueAsString().equalsIgnoreCase("")) return true;
	            case GENUS:
	                if (!this.queryPanels[uiPanelIndex].genus.getValueAsString().equalsIgnoreCase("")) return true;
	            case DEPTH:
	                if (!this.queryPanels[uiPanelIndex].species.getValueAsString().equalsIgnoreCase("")) return true;
	            case SPECIES:
	                if (!this.queryPanels[uiPanelIndex].species.getValueAsString().equalsIgnoreCase("")) return true;
	            case MORPHOLOGY:
	                if (!this.queryPanels[uiPanelIndex].morphology.getValueAsString().equalsIgnoreCase("")) return true;
	            }

	        }
    	} catch (Exception e) {e.printStackTrace();}
        //if we get here then nothing was dirty.
        return false;
    }

    /*
     * Set each query field with the relevant value
     */
    private void setUIQueryField(int uiPanelIndex, String field, String value){

    	try {
	        //setup a composite of the panel index + field as per the Example XMl file
	        String uiField = field.toLowerCase();
	        //adjust if we are the second panel.
	        if (uiPanelIndex == 1) {
	        	uiField = uiField+"1";
	        }
	        
	        //Set each field in the correct UI field
	        switch (QueryFields.valueOf(field.toUpperCase()))
	        {
	            case NORTH:
	            	//chekc if the rowselection model has been setup or selected?
	            	if (this.rowSelectionModel.getSelected() != null) {
		            	//use the check button to update the UI if we selected a region query
		            	//TODO FIX THIS TO MAKE THE LOOKUP FOR THE VALUE PANEL DEPENDANT>>>i.e join the panel number..
		                if ((!this.rowSelectionModel.getSelected().getAsString(uiField).equalsIgnoreCase("null")) && this.queryPanels[uiPanelIndex].regionPoint.getActiveItem().getText().equalsIgnoreCase("Point")){
		            	    //set the check item of the cycle button and it should fire it's own event to udate the INNER HTML.
		                    this.queryPanels[uiPanelIndex].regionPoint.setActiveItem(this.queryPanels[uiPanelIndex].regionCheck);
		                }
	            	}
	                //test if we have HTML to set -> we set even if the value is empty.
	                if(this.queryPanels[uiPanelIndex].northSouth.isVisible()){
	                    //set the north variable
	                    this.queryPanels[uiPanelIndex].north.setValue(value);
	                }
	                break;
	            case SOUTH:
	                //only set the south variable if its visible.
	                if(this.queryPanels[uiPanelIndex].northSouth.isVisible()){
	                    this.queryPanels[uiPanelIndex].south.setValue(value);
	                }
	                break;
	            case EAST:
	                //only set the east variable if its visible.
	                if(this.queryPanels[uiPanelIndex].eastWest.isVisible()){
	                    this.queryPanels[uiPanelIndex].east.setValue(value);
	                }
	                break;
	            case WEST:
	                //only set the west variable if its visible.
	                if(this.queryPanels[uiPanelIndex].eastWest.isVisible()){
	                    this.queryPanels[uiPanelIndex].west.setValue(value);
	                }
	                break;
	            case LAT:
	            	//chekc if the rowselection model has been setup or selected?
	            	if (this.rowSelectionModel.getSelected() != null) {
	            		//use the check button to update the UI if we selected a point query
		            	if ((!this.rowSelectionModel.getSelected().getAsString(uiField).equalsIgnoreCase("null")) && this.queryPanels[uiPanelIndex].regionPoint.getActiveItem().getText().equalsIgnoreCase("Region")){
		                    //set the check item of the cycle button and it should fire it's own event to udate the INNER HTML.
		                    this.queryPanels[uiPanelIndex].regionPoint.setActiveItem(this.queryPanels[uiPanelIndex].pointCheck);
		                }
	            	}
	                //test if we have HTML to set -> we set even if the value is empty.
	                if(this.queryPanels[uiPanelIndex].latitude.isVisible()){
	                    //set the value
	                    this.queryPanels[uiPanelIndex].latitude.setValue(value);
	                }
	                break;
	            case LON:
	                //test if we have HTML to set -> we set even if the value is empty.
	                if(this.queryPanels[uiPanelIndex].longitude.isVisible()){
	                    this.queryPanels[uiPanelIndex].longitude.setValue(value);
	                }
	                break;
	            case START_DATE:
	                //only set the field if non empty
	                if (value.equalsIgnoreCase("")) {
	                    //clear the field
	                    this.queryPanels[uiPanelIndex].start_date.reset();
	                } else {
	                    //setup the correct formatting and set the field
	//                    String[] split_start_date = value.split("T");
	                    this.queryPanels[uiPanelIndex].start_date.setValue(value);
	                }
	                break;
	            case END_DATE:
	                //only set the field if non empty
	                if (value.equalsIgnoreCase("")) {
	                    //clear the field
	                    this.queryPanels[uiPanelIndex].end_date.reset();
	                } else {
	                    //setup the correct formatting and set the field
	//                    String[] split_end_date = value.split("T");
	                    this.queryPanels[uiPanelIndex].end_date.setValue(value);
	                }
	                break;
	            case TARGET:
	                this.queryPanels[uiPanelIndex].target.setValue(value);
	                break;
	            case CHARACTERISTIC:
	                this.queryPanels[uiPanelIndex].characteristic.setValue(value);
	                break;
	            case LOCATION:
	                this.queryPanels[uiPanelIndex].location.setValue(value);
	                break;
	            case ACTOR:
	                this.queryPanels[uiPanelIndex].actor.setValue(value);
	                break;
	            case MEASUREMENT_VALUE:
	                this.queryPanels[uiPanelIndex].measurement_value.setValue(value);
	                break;
	            case UNIT:
	                this.queryPanels[uiPanelIndex].unit_value.setValue(value);
	                break;
	            case RESEARCH_PROGRAM:
	                this.queryPanels[uiPanelIndex].research_program.setValue(value);
	                break;
	            case ECOLOGICAL_PROCESS:
	            	this.queryPanels[uiPanelIndex].ecological_process.setValue(value);
	                break;
	            case PROCESS_OUTPUT:
	                this.queryPanels[uiPanelIndex].process_output.setValue(value);
	                break;
	            case GENUS:
	            	this.queryPanels[uiPanelIndex].genus.setValue(value);
	            	this.queryPanels[uiPanelIndex].organismFieldSet.setVisible(true);
	                break;
	            case SPECIES:
	            	this.queryPanels[uiPanelIndex].species.setValue(value);
	            	this.queryPanels[uiPanelIndex].organismFieldSet.setVisible(true);
	                break;
	            case MORPHOLOGY:
	            	this.queryPanels[uiPanelIndex].morphology.setValue(value);
	            	this.queryPanels[uiPanelIndex].organismFieldSet.setVisible(true);
	                break;
	            case DEPTH:
	            	this.queryPanels[uiPanelIndex].depth.setValue(value);
	                break;
	            case SENSOR:
	            	this.queryPanels[uiPanelIndex].sensor.setValue(Boolean.parseBoolean(value));
	                break;
	//		    default:
	//		        // log that we had an error matching the enum type with this field
	//
	//				GWT.log(today.toString() + " " + this.toString() + " :: Enum Type Matching Error:  Field doenst exist in the enum! Field = " + field.toUpperCase(), null);
	//		    	break;
	        }
    	}
    	catch (Exception e) { e.printStackTrace(); }
    }


//    /**
//     * Private helper method to load an Overlay in Google Maps
//     * @param overlayURL
//     */
//    private void createKMLOverlay(String overlayURL) {
//
//        //NOT USED:  Need to wait till GWT API exposes the GeoXMLOverlay event handlers
//
//        //create the overlay from the KML result set url
//        GeoXmlOverlay.load(GWT.getModuleBaseURL() + overlayURL,
//        new GeoXmlLoadCallback() {
//
//          @Override
//          public void onFailure(String url, Throwable e) {
//            StringBuffer message = new StringBuffer("KML File " + url + " failed to load -- ");
//            if (e != null) {
//              message.append(e.toString());
//            }
//            MessageBox.alert(message.toString());
//          }
//
//          @Override
//          public void onSuccess(String url, GeoXmlOverlay overlay) {
//            geoXml = overlay;
//            geoXml.gotoDefaultViewport(map);
//            map.addOverlay(geoXml);
//          }
//        });
//    }

//    /**
//     * NOTE: Not the Authors code: see -> http://www.gwt-ext.com/forum/viewtopic.php?f=9&t=2192
//     *  Adds an information image and tooltip to the field of a form
//     * @param field The field we want to add the Information tooltip to.
//     * @param instructions the instruction to be displayed as info tooltip
//     */
//    public static void addInfoTooltip(final Field field, final String instructions) {
//        field.doOnRender( new Function () {
//           public void execute() {
//              ExtElement labelEl;
//              if (field instanceof Checkbox)
//                 labelEl = new ExtElement (field.getEl().up("div.x-form-item").child("label.x-form-cb-label"));
//              	//for checkboxes we need to be more specific as there are two labels...
//              else
//                 labelEl = new ExtElement (field.getEl().up("div.x-form-item").child("label")); // I haven't tested this bit, may have to be adapted slightly for some Field specialisations...
//
//              DomConfig dc = new DomConfig("img");
//              dc.addAttribute("src", "images/Web-Application-Icons-Set/PNG-24/Info.png");
//              dc.addAttribute("style", "margin-bottom: 0px; margin-left: 5px; padding: 0px;");
//              //dc.addAttribute("width", "10");
//              //dc.addAttribute("height", "11");
//              ExtElement img = labelEl.createChild(dc);
//              ToolTip t = new ToolTip (instructions);
//              t.applyTo(img.getParentNode());
//
//           }
//
//        });
//     }


    /**
     * Method to setup a hash map with standard query variable names and an associated String array
     * for use as the columns in the data grid panel
     */
    private void setupColumnMap() {

        //clear the previous column maps
//        columnMap.clear();
//        columnSortIndex.clear();
        //think i want to keep all my data in the grid here!

        //setup a hash map for the columns in the data grid - based on an organism sighting
        columnMap.put("startdate", new String[]{"Start Date", "startdate", "100", "true", "null", "start_date"} );
        columnMap.put("enddate", new String[]{"End Date", "enddate", "100", "true", "null", "end_date"} );
        columnMap.put("lat", new String[]{"Latitude", "lat", "50", "true", "null", "latitude"} );
        columnMap.put("lon", new String[]{"Longitude", "lon", "50", "true", "null", "longitude"} );
        columnMap.put("lat_end", new String[]{"Latitude End", "lat_end", "50", "true", "null", "latitude_end"} );
        columnMap.put("lon_end", new String[]{"Longitude End", "lon_end", "50", "true", "null", "longitude_end"} );
        columnMap.put("locname", new String[]{"Location", "locname", "80", "true", "null", "locname"} );
        columnMap.put("actor", new String[]{"Actor", "actor", "80", "true", "null", "actor"} );
        columnMap.put("target", new String[]{"Target", "target", "70", "true", "null", "target"} );
        columnMap.put("characteristic", new String[]{"Characteristic", "characteristic", "70", "true", "null", "characteristic"} );
//        columnMap.put("measurement", new String[]{"Measurement", "measurement", "50", "true", "null", "measurement"} );
        columnMap.put("tool", new String[]{"Tool", "tool", "40", "true", "null", "tool"} );
        columnMap.put("value", new String[]{"Value", "value", "50", "true", "null", "value"} );
        columnMap.put("precision", new String[]{"Precision", "precision", "40", "true", "null", "precision"} );
        columnMap.put("unit", new String[]{"Unit", "unit", "70", "true", "null", "unit"} );
        columnMap.put("species", new String[]{"Species", "species", "100", "true", "null", "species"} );
        columnMap.put("genus", new String[]{"Genus", "genus", "80", "true", "null", "genus"} );
        columnMap.put("morphology", new String[]{"Morphology", "morphology", "80", "true", "null", "morphology"} );
        columnMap.put("depth", new String[]{"Depth", "depth", "10", "true", "null", "depth"} );
        columnMap.put("group_name", new String[]{"Research Program", "group_name", "80", "true", "null", "group_name"} );
        columnMap.put("qualifier", new String[]{"Qualifier", "qualifier", "40", "true", "null", "qualifier"} );
//      columnMap.put("eco_process", new String[]{"Ecological Process", "eco_process", "80", "true", "null", "eco_process"} );
        
//        //do a test dump and sort of the map...
//        Set<Entry<String, String[]>> set =  columnMap.entrySet();
//        
//        //obtain an Iterator for Collection
//        Iterator<Entry<String,String[]>> itr = set.iterator();
//     
//        //iterate through LinkedHashMap values iterator
//        while(itr.hasNext()) {
//        	Entry<String, String[]> temp = itr.next();
//        	System.out.println(temp.getKey());
//        	System.out.println(temp.getValue()[0]);
//        }	        
    }
}
