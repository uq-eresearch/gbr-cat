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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.xml.sax.SAXException;

import com.megginson.sax.DataWriter;

/**
 * @author Campbell Allen 
 * @date 4-2-2010
 * Class to convert an SQL result set into an XML document
 * Relies on the OpenSource XML Writer project http://sourceforge.net/projects/xml-writer/files/
 * 
 */
public class XMLFileWriter {
	
	//declare the XML DATAWriter used in this class
	private DataWriter xmlWriter;
	
	public XMLFileWriter(String outFilePath) {
		
//		System.out.println("Setting up the XML writer");
		
		try {
			//need to wrap the out file with a writer
			this.xmlWriter = new DataWriter(new BufferedWriter(new FileWriter(outFilePath)));
			
		} catch (IOException ioe) {
			//log the message
			System.out.println(ioe.getMessage());
			
		} 		
	}
	
	/**
	 * Make a new XML file
	 * @param outFilePath new file path
	 */
	public void newOutputFile(String outFilePath) {
		
		try {
			//need to wrap the out file with a writer
			this.xmlWriter = new DataWriter(new BufferedWriter(new FileWriter(outFilePath)));
			
		} catch (IOException ioe) {
			//log the message
			System.out.println(ioe.getMessage());
			
		} 	
	}
	
	public void startOutput() throws SAXException {
		
//		System.out.println("Starting the output of the writer");
		
		//setup the output document.
		try {
			this.xmlWriter.setIndentStep(2);
			this.xmlWriter.startDocument();
			
		} catch (SAXException sae) {
			System.out.println(sae.getMessage());
			throw sae;
		}
		
	}
	
	public void finishOutput() throws SAXException {
		
//		System.out.println("Finishing the output of the writer");
		
		//close the document
		try {
			this.xmlWriter.endDocument();
			
		} catch (SAXException sae) {
			System.out.println(sae.getMessage());
			throw sae;
		}
		
	}
	
	public void startElement(String elementName) throws SAXException {
		
//		System.out.println("print the element : " + elementName + " to the XML doc.");
		
		//start the element
		try {
			this.xmlWriter.startElement(elementName);
			
		} catch (SAXException sae) {
			System.out.println(sae.getMessage());
			throw sae;
		} 		
	}
	
	public void writeDataElement(String dataElement, String dataValue) throws SAXException {
		
//		System.out.println("Writing the dataElement: " + dataElement + " : as well as the dataValue : " + dataValue);

		//output the data values for the element
		try {
			this.xmlWriter.dataElement(dataElement, dataValue);
			
		} catch (SAXException sae) {
			System.out.println(sae.getMessage());
			throw sae;
		} 	
	}
	
	public void endElement(String elementName) throws SAXException {
		
//		System.out.println("writing the end element " + elementName + " to the XML doc.");
		
		//end the element
		try {
			this.xmlWriter.endElement(elementName);
			
		} catch (SAXException sae) {
			System.out.println(sae.getMessage());
			throw sae;
		} 
		
	}
}