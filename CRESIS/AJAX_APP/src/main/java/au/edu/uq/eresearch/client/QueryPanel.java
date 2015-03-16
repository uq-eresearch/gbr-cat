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
package au.edu.uq.eresearch.client;

import java.util.Date;
import java.util.HashMap;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.PopupPanel;
import com.gwtext.client.core.Connection;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.CycleButton;
import com.gwtext.client.widgets.DatePicker;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.ToolTip;
import com.gwtext.client.widgets.event.CycleButtonListenerAdapter;
import com.gwtext.client.widgets.event.DatePickerListenerAdapter;
import com.gwtext.client.widgets.form.Checkbox;
import com.gwtext.client.widgets.form.DateField;
import com.gwtext.client.widgets.form.Field;
import com.gwtext.client.widgets.form.FieldSet;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.MultiFieldPanel;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.form.event.TextFieldListenerAdapter;
import com.gwtext.client.widgets.layout.ColumnLayout;
import com.gwtext.client.widgets.layout.ColumnLayoutData;
import com.gwtext.client.widgets.menu.CheckItem;
import com.gwtext.client.widgets.tree.AsyncTreeNode;
import com.gwtext.client.widgets.tree.DefaultSelectionModel;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.TreePanel;
import com.gwtext.client.widgets.tree.XMLTreeLoader;
import com.gwtext.client.widgets.tree.event.TreePanelListenerAdapter;

/**
 * @author cam
 * Query Panel class to hold all the UI fields in a panel
 *
 */
public class QueryPanel {

	//TODO: Expose check boxes for each field to select if it should be included in the query variable output...

    public DateField start_date;
    public DateField end_date;
    public TextField target;
    public TextField characteristic;
    public TextField location;
    public TextField actor;
    public TextField measurement_value;
    public TextField unit_value;
    public TextField research_program;
    public TextField ecological_process;
    public TextField depth;
    public Checkbox sensor;
    public Checkbox organism_values;
    public TextField process_output;
    public TextField genus;
    public TextField species;
    public TextField morphology;
    public TextField north;
    public TextField south;
    public TextField east;
    public TextField west;
    public TextField latitude;
    public TextField longitude;
    public CycleButton regionPoint;
    public CheckItem regionCheck;
    public CheckItem pointCheck;
    public FieldSet organismFieldSet;
    public MultiFieldPanel northSouth;
    public MultiFieldPanel eastWest;
    public MultiFieldPanel latLon;
    public Checkbox datesAsRange;
    public Checkbox region_as_range;

     public HashMap<String,String> nodeMap;

     //setup the actual panels
     public Panel columnPanel;
     public FormPanel queryFieldPanel;
     public FormPanel organismQueryPanel;
     public FormPanel datePanel;
     public FormPanel locationVarPanel;
     public Panel pointRegionPanel;
     public Panel datesAsRangePanel;

     //UI fields
     public PopupPanel targetPopup;
     public FieldSet dateFieldSet;
     public FieldSet queryFieldSet;
     public PopupPanel characteristicPopup;
     public TreePanel targetTreePanel;
     public AsyncTreeNode targetRootNode;
     public MultiFieldPanel tandc;
     public MultiFieldPanel landa;
     public MultiFieldPanel mandu;
     public MultiFieldPanel rande;
     public MultiFieldPanel dands;
     public XMLTreeLoader targetLoader;
     public TreePanel characteristicTreePanel;
     public XMLTreeLoader characteristicLoader;
     public AsyncTreeNode characteristicRootNode;
     public PopupPanel actorPopup;
     public TreePanel actorTreePanel;
     public XMLTreeLoader actorLoader;
     public AsyncTreeNode actorRootNode;
     public PopupPanel ecoProcessPopup;
     public TreePanel ecoProcessTreePanel;
     public XMLTreeLoader ecoProcessLoader;
     public AsyncTreeNode ecoProcessRootNode;
     public FieldSet mapFieldSet;

      //used for logging
    private Date date;
    private DateTimeFormat dtf;

    public QueryPanel(String appContext) {

        //setup my logging
        this.dtf = DateTimeFormat.getFormat("MMM dd, yyyy HH:mm:ss");
        this.date = new Date();

        //setup a column panel
        this.columnPanel = new Panel();
        this.columnPanel.setLayout(new ColumnLayout());
        this.columnPanel.setPaddings(2);
        this.columnPanel.setBorder(false);
//        this.columnPanel.setAutoWidth(true);
        this.columnPanel.setWidth(1390	);
        this.columnPanel.setAutoHeight(true);
        this.columnPanel.setBorder(true);

        //panel to hold the actual query fields
        this.queryFieldPanel = new FormPanel();
        this.queryFieldPanel.setId("queryFieldPanel");
        this.queryFieldPanel.setBorder(false);
        this.queryFieldPanel.setPaddings(2);
        this.queryFieldPanel.setAutoWidth(true);
        this.queryFieldPanel.setAutoHeight(true);

        //Panel for the organism field set
        this.organismQueryPanel = new FormPanel();
        this.organismQueryPanel.setId("organismQueryPanel");
        this.organismQueryPanel.setBorder(false);
        this.organismQueryPanel.setPaddings(2);
        this.organismQueryPanel.setAutoWidth(true);
        this.organismQueryPanel.setAutoHeight(true);

        //Panel for the date fields
        this.datePanel = new FormPanel();
        this.datePanel.setId("datePanel");
        this.datePanel.setBorder(false);
        this.datePanel.setPaddings(2);
        this.datePanel.setAutoWidth(true);
        this.datePanel.setAutoHeight(true);

        //multi field panel for the dates.
//        MultiFieldPanel dateTimePanel = new MultiFieldPanel();
//        dateTimePanel.setBorder(false);

        //panel for the location variables
//        final Panel locationVarPanel = new Panel();
        this.locationVarPanel = new FormPanel();
        this.locationVarPanel.setId("locationVarPanel");
        this.locationVarPanel.setBorder(false);
        this.locationVarPanel.setPaddings(2);
        this.locationVarPanel.setAutoWidth(true);
        this.locationVarPanel.setAutoHeight(true);

        //Panel for the point / region selector
        this.pointRegionPanel = new Panel();
//        this.pointRegionPanel.setId("pointRegionPanel")
        this.pointRegionPanel.setBorder(false);
        this.pointRegionPanel.setPaddings(2,105,10,2);

    //TODO: Drive lat / long selection of the map selection tool.

        //field register set for the Lat / Lon variables
        this.mapFieldSet = new FieldSet("Location Variables");
        this.mapFieldSet.setWidth(280);

        //panel to hold the north / south
        MultiFieldPanel locName = new MultiFieldPanel();
        locName.setBorder(false);

        //panel to hold the location name
        //TODO make this a class var as per the rest.
//        Panel locationName = new Panel();
//        locationName.setBorder(false);
		//location field of the query
		this.location = new TextField("Location Name", "location", 145);
		this.location.setRegex("^\\S+.+$");
		this.location.setRegexText("Location name can't start with a space.");
		this.location.setFieldMsgTarget("qtip");
		this.location.setId("location");
		locName.addToRow(this.location, 250);

        //panel to hold the north / south
        this.northSouth = new MultiFieldPanel();
        this.northSouth.setBorder(false);

        //setup the north field
        this.north = new TextField("North / South", "north", 70);
        this.north.setRegex("^\\-+(\\d*|\\d*.\\d*)$");
        this.north.setRegexText("Negative Numbers only - all data in Southern Hemisphere");
        this.north.setFieldMsgTarget("qtip");
        this.north.setAllowBlank(true);
        this.north.setId("north");

        //setup the south field
        this.south = new TextField("South", "south", 70);
        this.south.setRegex("^\\-+(\\d*|\\d*.\\d*)$");
        this.south.setRegexText("Negative Numbers only - all data in Southern Hemisphere");
        this.south.setFieldMsgTarget("qtip");
        this.south.setAllowBlank(true);
        this.south.setHideLabel(true);
        this.south.setId("south");

        //add the panels
        this.northSouth.addToRow(north, 180);
        this.northSouth.addToRow(south, new ColumnLayoutData(1));

        //east / west panel
        this.eastWest = new MultiFieldPanel();
        this.eastWest.setBorder(false);

        //setup the west field
        this.west = new TextField("West / East", "west", 70);
        this.west.setRegex("^(\\d*|\\d*.\\d*)$");
        this.west.setRegexText("Numbers only");
        this.west.setFieldMsgTarget("qtip");
        this.west.setAllowBlank(true);
        this.west.setId("west");

        //setup the east field
        this.east = new TextField("East", "east", 70);
        this.east.setRegex("^(\\d*|\\d*.\\d*)$");
        this.east.setRegexText("Numbers only");
        this.east.setFieldMsgTarget("qtip");
        this.east.setAllowBlank(true);
        this.east.setHideLabel(true);
        this.east.setId("east");

        //add the panels
        this.eastWest.addToRow(this.west, 180);
        this.eastWest.addToRow(this.east, new ColumnLayoutData(1));

        //panel to hold the lat / lon
        this.latLon = new MultiFieldPanel();
        this.latLon.setBorder(false);

        //setup the lat field
        this.latitude = new TextField("Lat / Lon", "latitude", 70);
        this.latitude.setRegex("^\\-+(\\d*|\\d*.\\d*)$");
        this.latitude.setRegexText("Negative Numbers only - all data in Southern Hemisphere");
        this.latitude.setFieldMsgTarget("qtip");
        this.latitude.setAllowBlank(true);
        this.latitude.setId("latitude");
        //setup the long field
        this.longitude = new TextField("Longitude", "longitude", 70);
        this.longitude.setRegex("^(\\d*|\\d*.\\d*)$");
        this.longitude.setRegexText("Numbers only");
        this.longitude.setFieldMsgTarget("qtip");
        this.longitude.setAllowBlank(true);
        this.longitude.setHideLabel(true);
        this.longitude.setId("longitude");

        this.latLon.addToRow(this.latitude, 180);
        this.latLon.addToRow(this.longitude, new ColumnLayoutData(1));
        this.latLon.setVisible(false);

        //now add the n-s, e-w selectors to the field set!
        this.mapFieldSet.add(locName);
        this.mapFieldSet.add(this.northSouth);
        this.mapFieldSet.add(this.eastWest);
        this.mapFieldSet.add(this.latLon);

        //create a cycle button for the
        this.regionPoint = new CycleButton();
        this.regionPoint.setShowText(true);
        this.regionPoint.setPrependText("Coordinates as : ");
        this.regionCheck = new CheckItem("Region", true);
        this.pointCheck = new CheckItem("Point", false);
        this.regionPoint.addItem(this.regionCheck);
        this.regionPoint.addItem(this.pointCheck);
        this.regionPoint.setId("regionPoint");
        
        //make sure that we have set the active item
        this.regionPoint.setActiveItem(this.regionCheck);
        //Add the listener
        this.regionPoint.addListener(new CycleButtonListenerAdapter() {
            public void onChange(CycleButton self, CheckItem item) {
                //selected the region here!
                if (item.getText().equalsIgnoreCase("Region")){
                    //set the coords panel inner HTML block to the the region HTML code.
                    northSouth.setVisible(true);
                    eastWest.setVisible(true);
                    region_as_range.enable();
                    latLon.setVisible(false);
                } else {
                    //set the coords panel inner HTML block to the the point HTML code.
                    northSouth.setVisible(false);
                    eastWest.setVisible(false);
                    region_as_range.setValue(false);
                    region_as_range.disable();
                    latLon.setVisible(true);
                }
            }
        });
        
        //add the button the panel
        this.pointRegionPanel.add(this.regionPoint);
        //setup the map selector panel with all it's widgets
        this.mapFieldSet.add(this.pointRegionPanel);

        //add a checkbox to see if we want the dates as a range
        this.region_as_range = new Checkbox("Return all observations fully contained by the region.", "regionAsRange"); //--> Temp here as well
        this.region_as_range.setId("regionAsRange");
        this.region_as_range.setValue(false);
        this.region_as_range.setHideLabel(true);
        this.mapFieldSet.add(this.region_as_range);
        
        //make a tooltip for the check box
        ToolTip regionRange = new ToolTip();
        regionRange.setHtml("Select this to only return observations that are fully enclosed by the specified region." +
        					"</br>Don't select to find all observations that intersect the specified region.");
        //apply the tooltips
        regionRange.applyTo(this.region_as_range);

        //date field set
        this.dateFieldSet = new FieldSet("Date Variables");
        this.dateFieldSet.setAutoHeight(true);
        this.dateFieldSet.setAutoWidth(true);
//        this.dateFieldSet.setWidth(280);
        this.dateFieldSet.setLabelWidth(70);

        //startDate field of the query
        this.start_date = new DateField("Start Date", "Y-m-d H:i:s");
        this.start_date.setWidth(170);
        this.start_date.setRegex("^\\d{4}\\-\\d{2}\\-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}$");
        this.start_date.setRegexText("Date in the format: Y-M-D H:M:S only");
        this.start_date.setFieldMsgTarget("qtip");
        this.start_date.setId("start_date");
        this.dateFieldSet.add(this.start_date);

        //endDate field of the query
        this.end_date = new DateField("End Date", "Y-m-d H:i:s");
        this.end_date.setWidth(170);
        this.end_date.setRegex("^\\d{4}\\-\\d{2}\\-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}$");
        this.end_date.setRegexText("Date in the format: Y-M-D H:M:S only");
        this.end_date.setFieldMsgTarget("qtip");
        this.end_date.setId("end_date");
        this.end_date.addListener(new DatePickerListenerAdapter() {
        	public void onSelect(DatePicker datePicker, Date date) {
        		//make the time at the end of the day!
        		//note: 1ms*1000*60*60-1000 (1 day - 1 sec)
        		date.setTime(date.getTime()+86399000);
        		//now set the time of the date picker.
        		datePicker.setValue(date);
        	}
        });
        this.dateFieldSet.add(this.end_date);

        //make tooltips for the dates
        ToolTip begin_tip = new ToolTip();
        begin_tip.setHtml("Enter a date in the format: Y-M-D H:M:S");
        ToolTip end_tip = new ToolTip();
        end_tip.setHtml("Enter a date in the format: Y-M-D H:M:S");
        //apply the tooltips
        begin_tip.applyTo(this.start_date);
        end_tip.applyTo(this.end_date);

        //add a checkbox to see if we want the dates as a range
        this.datesAsRangePanel = new Panel();
        this.datesAsRangePanel.setBorder(false);
//        this.datesAsRange = new Checkbox("Do not treat dates as a range.", "datesAsRange");
        this.datesAsRange = new Checkbox("", "datesAsRange"); // Temp here as well-->>
        this.datesAsRange.setId("datesAsRange");
        this.datesAsRange.setValue(false);
        this.datesAsRangePanel.add(this.datesAsRange);
        this.datesAsRange.setVisible(false);  // -->> HIDE THIS FOR THE MOMENT, Treat all lat / lon ranges as a range.
        this.datesAsRange.setHideLabel(true);
        this.dateFieldSet.add(datesAsRangePanel);

        //make a tooltip for the check box
        ToolTip dateRange = new ToolTip();
        dateRange.setHtml("Select to treat the dates as specific values<br>Deselect to treat the dates as a range.");
        //apply the tooltips
        dateRange.applyTo(this.datesAsRange);

        /*
         * Common Query Field Set
         */
        //field register set
        this.queryFieldSet = new FieldSet("Common Query Variables");
        this.queryFieldSet.setAutoHeight(true);
        this.queryFieldSet.setWidth(500);
        //statically set the label width...used for calcs of col positions.
        this.queryFieldSet.setLabelWidth(100);

        //setup my multi field panel for the columns of this field set
        this.tandc = new MultiFieldPanel();
        this.tandc.setBorder(false);

        //target field of the query
        this.target = new TextField("Target / Metric", "target", 170);
        this.target.setRegex("^\\w+\\s*\\w*$");
        this.target.setRegexText("Format: \"Obseravtion Target\"");
        this.target.setFieldMsgTarget("qtip");
        this.target.setId("target");
//        queryFieldSet.add(target);
        this.tandc.addToRow(target, 280);

        //characteristic field of the query
        this.characteristic = new TextField("Characteristic", "characteristic", 195);
        this.characteristic.setHideLabel(true);
        this.characteristic.setRegex("^\\w+\\s*\\w*\\s*\\w*$");
        this.characteristic.setRegexText("Format: \"Measurement Characteristic\"");
        this.characteristic.setFieldMsgTarget("qtip");
        this.characteristic.setId("characteristic");
        this.tandc.addToRow(this.characteristic, new ColumnLayoutData(1));
        this.queryFieldSet.add(tandc);

//        //setup multifield panel for field set
//        this.landa = new MultiFieldPanel();
//        this.landa.setBorder(false);
//
//        //location field of the query
//        this.location = new TextField("Location Name / Actor", "location", 170);
//        this.location.setRegex("^\\S*.+$");
//        this.location.setRegexText("Location name can't start with a space.");
//        this.location.setFieldMsgTarget("qtip");
//        this.landa.addToRow(this.location, 280);
//
//        //actor field of the query
//        this.actor = new TextField("Actor", "actor", 195);
//        this.actor.setHideLabel(true);
//        this.actor.setRegex("^\\S*.+$");
//        this.actor.setRegexText("Actor name can't start with a space.");
//        this.actor.setFieldMsgTarget("qtip");
//        this.landa.addToRow(this.actor, new ColumnLayoutData(1));
//        this.queryFieldSet.add(this.landa);

        //setup multifield panel for field set
        this.mandu = new MultiFieldPanel();
        this.mandu.setBorder(false);

        //Measurment value field
        this.measurement_value = new TextField("Value / Unit", "measurement_value", 170);
        this.measurement_value.setRegex("^[=<>]\\s*\\w+$"); 
        this.measurement_value.setRegexText("Please enter a value clause - (E.G > 10 OR =5).");
        this.measurement_value.setFieldMsgTarget("qtip");
        this.measurement_value.setFieldMsgTarget("qtip");
        this.measurement_value.setId("measurement_value");
        this.mandu.addToRow(this.measurement_value, 280);

        //Output of value field
        this.unit_value = new TextField("Unit", "unit_value");
        this.unit_value.setHideLabel(true);
        this.unit_value.setRegex("^\\S+.+$");
        this.unit_value.setRegexText("Must correspond to the measured characteristic.");
        this.unit_value.setFieldMsgTarget("qtip");
        this.unit_value.setId("unit_value");
        this.mandu.addToRow(unit_value, new ColumnLayoutData(1));
        this.queryFieldSet.add(this.mandu);

        //setup multifield panel for field set
        this.rande = new MultiFieldPanel();
        this.rande.setBorder(false);

        //Research Program value field
        this.research_program = new TextField("Project / Eco-Process", "research_program", 170);
        this.research_program.setRegex("^\\S+.+$");
        this.research_program.setRegexText("Please enter a program.");
        this.research_program.setFieldMsgTarget("qtip");
        this.research_program.setId("research_program");
        this.rande.addToRow(this.research_program, 280);
        
        //Ecological Process value field
        this.ecological_process = new TextField("Ecological Process", "ecological_process");
        this.ecological_process.setHideLabel(true);
        this.ecological_process.setRegex("^\\S+.+$");
        this.ecological_process.setRegexText("Please enter an ecological process.");
        this.ecological_process.setFieldMsgTarget("qtip");
        this.ecological_process.setId("ecological_process");
        this.rande.addToRow(this.ecological_process, new ColumnLayoutData(1));
        this.queryFieldSet.add(this.rande);

        //setup multifield panel for field set
        this.dands = new MultiFieldPanel();
        this.dands.setBorder(false);

        //Depth value field
        this.depth = new TextField("Depth", "depth", 170);
        this.depth.setRegex("^[=|<>]=*\\s*\\d+$");
        this.depth.setRegexText("Please enter a depth clause - (E.G > 10 OR = 5).");
        this.depth.setFieldMsgTarget("qtip");
        this.depth.setId("depth");
        this.dands.addToRow(this.depth, 280);
        this.queryFieldSet.add(this.dands);

        //Sensor value field
//        this.sensor = new TextField("Sensor", "sensor");
//        this.sensor.setHideLabel(true);
//        this.sensor.setRegex("^\\S+.*$");
//        this.sensor.setRegexText("Please enter a Sensor type OR instance.");
        this.sensor = new Checkbox(" Only Sensor Measurements", "sensor");
        this.sensor.setHideLabel(true);
        this.sensor.setId("sensor");
        //now add the checkbox
        this.dands.addToRow(this.sensor, new ColumnLayoutData(1));
        this.queryFieldSet.add(this.dands);

		//setup multifield panel for field set
		this.landa = new MultiFieldPanel();
		this.landa.setBorder(false);

//      //location field of the query
//      this.location = new TextField("Location Name / Actor", "location", 170);
//      this.location.setRegex("^\\S*.+$");
//      this.location.setRegexText("Location name can't start with a space.");
//      this.location.setFieldMsgTarget("qtip");
//      this.landa.addToRow(this.location, 280);

//      //location field of the query
//      this.location = new TextField("Actor", "actor", 170);
//      this.location.setRegex("^\\S*.+$");
//      this.location.setRegexText("Location name can't start with a space.");
//      this.location.setFieldMsgTarget("qtip");
//      this.landa.addToRow(this.location, 280);

      //actor field of the query
      this.actor = new TextField("Actor", "actor", 170);
//      this.actor.setHideLabel(true);
      this.actor.setRegex("^\\S+.+$");
      this.actor.setRegexText("Actor name can't start with a space.");
      this.actor.setFieldMsgTarget("qtip");
      this.actor.setId("actor");
      this.landa.addToRow(this.actor, 280);
      this.queryFieldSet.add(this.landa);


      //make a tooltip for the sensor check box
      ToolTip sensorToolTip = new ToolTip();
      sensorToolTip.setHtml("Select to include only the sensor observations.");
      //apply the tooltips
      sensorToolTip.applyTo(this.sensor);


      //Output of Processing step value field
      this.process_output = new TextField("Processing Step Output", "process_output", 170);
      this.process_output.setRegex("^\\S+.+$");
      this.process_output.setRegexText("Please enter a processing output step.");
      this.process_output.setFieldMsgTarget("qtip");
      this.process_output.setId("process_output");
//        this.queryFieldSet.add(this.process_output);
        //TODO: Need to add an input field for the workflows
        //and add these to their own fieldset....

      /*
       * Organism Field Set
       */
      //Organism Query Field register set
      this.organismFieldSet = new FieldSet("Organism Query Variables");
      this.organismFieldSet.setId("organismFieldSet");
      this.organismFieldSet.setAutoHeight(true);
      this.organismFieldSet.setAutoWidth(true);
      //set to initial visibility false
//      this.organismFieldSet.setVisible(false);

      //Genus field of the query
      this.genus = new TextField("Genus", "genus");
      this.genus.setRegex("^\\S+.+$");
      this.genus.setRegexText("Please enter a genus to search on.");
      this.genus.setFieldMsgTarget("qtip");
      this.genus.setId("genus");
      this.organismFieldSet.add(this.genus);

      //Species field of the query
      this.species = new TextField("Species", "species");
      this.species.setRegex("^\\S+.+$");
      this.species.setRegexText("Please enter a species to search on.");
      this.species.setFieldMsgTarget("qtip");
      this.species.setId("species");
      this.organismFieldSet.add(this.species);

      //Morphology field of the query
      this.morphology = new TextField("Morphology", "morphology");
      this.morphology.setRegex("^\\S+.+$");
      this.morphology.setRegexText("Please enter a morphology to search on.");
      this.morphology.setFieldMsgTarget("qtip");
      this.morphology.setId("morphology");
      this.organismFieldSet.add(this.morphology);
        
	  //return all Organism values field
	  this.organism_values = new Checkbox(" Return unset organsim query values", "organism_values");
	  this.organism_values.setHideLabel(true);
	  this.organism_values.setId("organism_values");
	  //now add the checkbox
	  this.organismFieldSet.add(this.organism_values);
	    

      /*
       * Target Tree panel and listener for the target text area
       */
      //make popup to hold the target tree panel
      this.targetPopup = new PopupPanel(true);
      //targetPopup.setAnimationEnabled(true);

      //create a Tree Panel from an XML file
      this.targetTreePanel = new TreePanel();
      this.targetTreePanel.setTitle("Query Target Classes");
      this.targetTreePanel.setCollapsible(false);
      //set an icon for the panel - Might be useful later
      //targetTreePanel.setIconCls("world-icon");
      this.targetTreePanel.setAutoHeight(true);
      this.targetTreePanel.setAutoWidth(true);
      this.targetTreePanel.setSelectionModel(new DefaultSelectionModel());

      this.targetLoader = new XMLTreeLoader();
      this.targetLoader.setDataUrl(appContext + "Resources/Configuration_Files/query_targets.xml");
      this.targetLoader.setMethod(Connection.GET);
      this.targetLoader.setRootTag("target");
      this.targetLoader.setFolderIdMapping("@id");
      this.targetLoader.setLeafIdMapping("@id");
      this.targetLoader.setFolderTitleMapping("@title");
      this.targetLoader.setFolderTag("folder");
      this.targetLoader.setLeafTitleMapping("@title");
      this.targetLoader.setLeafTag("leaf");
      this.targetLoader.setQtipMapping("@qtip");
      this.targetLoader.setDisabledMapping("@disabled");
      this.targetLoader.setCheckedMapping("@checked");
      this.targetLoader.setIconMapping("@icon");
      this.targetLoader.setAttributeMappings(new String[]{"@id", "@type"});

      //make a root node for the treePanel
      this.targetRootNode = new AsyncTreeNode("Query Targets", this.targetLoader);
      //add the root node the the tree panel
      this.targetTreePanel.setRootNode(this.targetRootNode);
        
      //add the target treepanel to the popup
      this.targetPopup.add(this.targetTreePanel);

      //setup the target popup event listener
      this.target.addListener(new TextFieldListenerAdapter(){
          public void onFocus(Field field) {
              targetPopup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
                  public void setPosition(int offsetWidth, int offsetHeight) {
                      int left = (target.getAbsoluteLeft() + target.getOffsetWidth());
                      int top = (target.getAbsoluteTop());
                      targetPopup.setPopupPosition(left, top);
                  }
              });
              targetRootNode.expand();
          }
      });

      /*
       * Characteristic Tree panel and listener for the characteristic text area
       */

      //make popup to hold the target tree panel
      this.characteristicPopup = new PopupPanel(true);

        //create a Tree Panel from an XML file
        this.characteristicTreePanel = new TreePanel();
        this.characteristicTreePanel.setTitle("Query Characteristic Classes");
        this.characteristicTreePanel.setCollapsible(false);
        //set an icon for the panel - Might be useful later
        //this.xmlTreePanel.setIconCls("world-icon");
        this.characteristicTreePanel.setAutoHeight(true);
        this.characteristicTreePanel.setAutoWidth(true);
        this.characteristicTreePanel.setSelectionModel(new DefaultSelectionModel());

        this.characteristicLoader = new XMLTreeLoader();
        this.characteristicLoader.setDataUrl(appContext + "Resources/Configuration_Files/query_characteristics.xml");
        this.characteristicLoader.setMethod(Connection.GET);
        this.characteristicLoader.setRootTag("characteristic");
        this.characteristicLoader.setFolderIdMapping("@id");
        this.characteristicLoader.setLeafIdMapping("@id");
        this.characteristicLoader.setFolderTitleMapping("@title");
        this.characteristicLoader.setFolderTag("folder");
        this.characteristicLoader.setLeafTitleMapping("@title");
        this.characteristicLoader.setLeafTag("leaf");
        this.characteristicLoader.setQtipMapping("@qtip");
        this.characteristicLoader.setDisabledMapping("@disabled");
        this.characteristicLoader.setCheckedMapping("@checked");
        this.characteristicLoader.setIconMapping("@icon");
        this.characteristicLoader.setAttributeMappings(new String[]{"@id", "@type"});

        //make a root node for the treePanel
        this.characteristicRootNode = new AsyncTreeNode("Query Characteristics", this.characteristicLoader);
        //add the root node the the tree panel
       this.characteristicTreePanel.setRootNode(this.characteristicRootNode);

       //add the target treepanel to the popup
       this.characteristicPopup.add(this.characteristicTreePanel);

       //setup the target popup event listener
       this.characteristic.addListener(new TextFieldListenerAdapter(){
            public void onFocus(Field field) {
                characteristicPopup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
                    public void setPosition(int offsetWidth, int offsetHeight) {
                        int left = (characteristic.getAbsoluteLeft() + characteristic.getOffsetWidth());
                        int top = (characteristic.getAbsoluteTop());
                        characteristicPopup.setPopupPosition(left, top);
                    }
                });
                characteristicRootNode.expand();
            }
        });

        /*
         * Actor Tree panel and listener for the actor text area
         */
        //make popup to hold the actor tree panel
        this.actorPopup = new PopupPanel(true);

        //create a Tree Panel from an XML file
        this.actorTreePanel = new TreePanel();
        this.actorTreePanel.setTitle("Actors");
        this.actorTreePanel.setCollapsible(false);
        this.actorTreePanel.setAutoHeight(true);
        this.actorTreePanel.setAutoWidth(true);
        this.actorTreePanel.setSelectionModel(new DefaultSelectionModel());

        this.actorLoader = new XMLTreeLoader();
        this.actorLoader.setDataUrl(appContext + "Resources/Configuration_Files/Actors.xml");
        this.actorLoader.setMethod(Connection.GET);
        this.actorLoader.setRootTag("actors");
        this.actorLoader.setFolderIdMapping("@id");
        this.actorLoader.setLeafIdMapping("@id");
        this.actorLoader.setFolderTitleMapping("@title");
        this.actorLoader.setFolderTag("folder");
        this.actorLoader.setLeafTitleMapping("@title");
        this.actorLoader.setLeafTag("leaf");
        this.actorLoader.setQtipMapping("@qtip");
        this.actorLoader.setDisabledMapping("@disabled");
        this.actorLoader.setCheckedMapping("@checked");
        this.actorLoader.setIconMapping("@icon");
        this.actorLoader.setAttributeMappings(new String[]{"@id", "@type"});

        //make a root node for the treePanel
        this.actorRootNode = new AsyncTreeNode("Actors", actorLoader);
        //add the root node the the tree panel
        this.actorTreePanel.setRootNode(this.actorRootNode);

        //add the actor treepanel to the popup
        this.actorPopup.add(this.actorTreePanel);

        //setup the target popup event listener
        this.actor.addListener(new TextFieldListenerAdapter(){
            public void onFocus(Field field) {
                actorPopup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
                    public void setPosition(int offsetWidth, int offsetHeight) {
                        int left = (actor.getAbsoluteLeft() + actor.getOffsetWidth());
                        int top = (actor.getAbsoluteTop());
                        actorPopup.setPopupPosition(left, top);
                    }
                });
                actorRootNode.expand();
            }
        });

        /*
         * Ecological Process Tree panel and listener for the ecological Process text area
         */
        //make popup to hold the actor tree panel
        this.ecoProcessPopup = new PopupPanel(true);

        //create a Tree Panel from an XML file
        this.ecoProcessTreePanel = new TreePanel();
        this.ecoProcessTreePanel.setTitle("Ecological Process");
        this.ecoProcessTreePanel.setCollapsible(false);
        this.ecoProcessTreePanel.setAutoHeight(true);
        this.ecoProcessTreePanel.setAutoWidth(true);
        this.ecoProcessTreePanel.setSelectionModel(new DefaultSelectionModel());

        this.ecoProcessLoader = new XMLTreeLoader();
        this.ecoProcessLoader.setDataUrl(appContext + "Resources/Configuration_Files/Ecological_Process.xml");
        this.ecoProcessLoader.setMethod(Connection.GET);
        this.ecoProcessLoader.setRootTag("ecological_processes");
        this.ecoProcessLoader.setFolderIdMapping("@id");
        this.ecoProcessLoader.setLeafIdMapping("@id");
        this.ecoProcessLoader.setFolderTitleMapping("@title");
        this.ecoProcessLoader.setFolderTag("folder");
        this.ecoProcessLoader.setLeafTitleMapping("@title");
        this.ecoProcessLoader.setLeafTag("leaf");
        this.ecoProcessLoader.setQtipMapping("@qtip");
        this.ecoProcessLoader.setDisabledMapping("@disabled");
        this.ecoProcessLoader.setCheckedMapping("@checked");
        this.ecoProcessLoader.setIconMapping("@icon");
        this.ecoProcessLoader.setAttributeMappings(new String[]{"@id", "@type"});

        //make a root node for the treePanel
        this.ecoProcessRootNode = new AsyncTreeNode("Ecological Process", this.ecoProcessLoader);
        //add the root node the the tree panel
        this.ecoProcessTreePanel.setRootNode(this.ecoProcessRootNode);

        //add the actor treepanel to the popup
        this.ecoProcessPopup.add(this.ecoProcessTreePanel);

        //setup the target popup event listener
        this.ecological_process.addListener(new TextFieldListenerAdapter(){
            public void onFocus(Field field) {
                ecoProcessPopup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
                    public void setPosition(int offsetWidth, int offsetHeight) {
                        int left = (ecological_process.getAbsoluteLeft() + ecological_process.getOffsetWidth());
                        int top = (ecological_process.getAbsoluteTop());
                        ecoProcessPopup.setPopupPosition(left, top);
                    }
                });
                ecoProcessRootNode.expand();
            }
        });

        //Map to link the target selection to the ontology classes
//        this.SetupOntologyNodeMap();

        //add the target listener to the tree
        this.targetTreePanel.addListener(new TreePanelListenerAdapter(){
            public void onClick(TreeNode node, EventObject e){
            	target.setValue(node.getAttribute("id"));
                targetPopup.hide();

                //set the organism query panel to be visible or not
                if (node.getAttribute("type").equalsIgnoreCase("organism")){
                    organismFieldSet.setVisible(true);
                } else {
                    organismFieldSet.setVisible(false);
                }
            }
        });

        //add the characteristic listener to the tree
        this.characteristicTreePanel.addListener(new TreePanelListenerAdapter(){
            public void onClick(TreeNode node, EventObject eo){
                try {
                    characteristic.setValue(node.getAttribute("id"));
                } catch (Exception e){
                    date.setTime(System.currentTimeMillis());
                    System.out.println(dtf.format(date) + " " + this.toString() + " :: " + e.getLocalizedMessage());
                }
                characteristicPopup.hide();
            }
        });

        //add the actor listener to the tree
        this.actorTreePanel.addListener(new TreePanelListenerAdapter(){
            public void onClick(TreeNode node, EventObject eo){
                try {
                    actor.setValue(node.getAttribute("id"));
                } catch (Exception e){
                    date.setTime(System.currentTimeMillis());
                    System.out.println(dtf.format(date) + " " + this.toString() + " :: " + e.getLocalizedMessage());
                }
                actorPopup.hide();
            }
        });

        //add the ecological_process listener to the tree
        this.ecoProcessTreePanel.addListener(new TreePanelListenerAdapter(){
            public void onClick(TreeNode node, EventObject eo){
                try {
                    ecological_process.setValue(node.getAttribute("id"));
                } catch (Exception e){
                    date.setTime(System.currentTimeMillis());
                    System.out.println(dtf.format(date) + " " + this.toString() + " :: " + e.getLocalizedMessage());
                }
                ecoProcessPopup.hide();
            }
        });

        //add the query field set to the query panel
        this.queryFieldPanel.add(this.queryFieldSet);

        //add the organism query fields to the panel
        this.organismQueryPanel.add(this.organismFieldSet);

        //setup the date Panel to hold the date
        this.datePanel.add(this.dateFieldSet);

        //Now add all my field sets to their respective panels - starting with the map
        this.locationVarPanel.add(this.mapFieldSet);

        //add all the panels to the main panel with 25% of the width
//        ColumnLayoutData cld = new ColumnLayoutData(.25);
//        columnPanel.add(mappingPanel, new ColumnLayoutData(.25));
        this.columnPanel.add(this.queryFieldPanel, new ColumnLayoutData(.25));
        this.columnPanel.add(this.organismQueryPanel, new ColumnLayoutData(.25));
        this.columnPanel.add(this.datePanel, new ColumnLayoutData(.25));
        this.columnPanel.add(this.locationVarPanel, new ColumnLayoutData(.25));

    }
}
