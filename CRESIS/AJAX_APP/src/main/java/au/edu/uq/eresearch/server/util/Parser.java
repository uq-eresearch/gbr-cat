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

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Campbell Allen
 * @date   30/5/08
 * Class to represent the XML parsing utility class.
 * Will be used to translate XML documents between formats using XSLT style sheets.
 */
public class Parser {

	private static String xslt = "";
	private Templates cachedXSLT;
	private TransformerFactory transFact;
	private Source xsltSource;
	//used for logging
	private Date date;
	private SimpleDateFormat sdf;

	/**
	 * Constructor
	 * @param context of the tomcat servlet
	 * @param XST file path
	 */
	public Parser(String context, String xsltPath) {

		this.sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
		this.date = new Date();

		System.out.println(sdf.format(date) + " " + this.toString() +  " :: Trying to setup the XSLT parser....");

		// set the TransformFactory to use the Saxon TransformerFactoryImpl method
		System.setProperty("javax.xml.transform.TransformerFactory","net.sf.saxon.TransformerFactoryImpl");

		//setup the passed in style sheet path
		Parser.xslt = context + xsltPath;

		// the factory pattern supports different XSLT processors
		this.transFact = TransformerFactory.newInstance();
        File xsltFile = new File(xslt);
        this.xsltSource = new StreamSource(xsltFile);
        try{
        	this.cachedXSLT = transFact.newTemplates(xsltSource);

        	this.date.setTime(System.currentTimeMillis());
			System.out.println(sdf.format(date) + " " + this.toString() +  " :: XSLT Parser successfully setup.");
        }
        catch (TransformerConfigurationException e){
        	System.out.println(e.getMessage());
        }

	}

	/**
	 * Translate the input file using the xsl template
	 * @param inputFile
	 * @param outFile
	 * @return boolean indicating if the translation was successful
	 */
	public boolean translate(String inputFile, String outFile){

		File xmlFile = new File(inputFile);

		try {
			FileOutputStream fout = new FileOutputStream(outFile);

			this.date.setTime(System.currentTimeMillis());
			System.out.println(sdf.format(date) + " " + this.toString() + " : Started reading the file " + outFile);

			// JAXP reads data using the Source interface
	        Source xmlSource = new StreamSource(xmlFile);

	        this.date.setTime(System.currentTimeMillis());
			System.out.println(sdf.format(date) + " " + this.toString() + " : Finished reading the file " + outFile);

	        try{
	        	this.date.setTime(System.currentTimeMillis());
				System.out.println(sdf.format(date) + " " + this.toString() + " : Starting the transform " + outFile);

	        	Transformer trans = cachedXSLT.newTransformer();
	        	trans.transform(xmlSource, new StreamResult(fout));

	        	this.date.setTime(System.currentTimeMillis());
				System.out.println(sdf.format(date) + " " + this.toString() + " : Finsihed the transform " + outFile);

	        	return true;
	        }
	        catch (TransformerConfigurationException e){
	        	System.out.println(e.getMessage());
	        }
	        catch (TransformerException e){
	        	System.out.println(e.getMessage());
	        }
		}
		catch (FileNotFoundException e){
			System.out.println(e.getMessage());
		}
		return false;
	}

	/**
	 * Set the XSL template file
	 * @param xsltFile
	 */
	public void setXSLTemplate(String xsltFile) {
		xslt = xsltFile;
	}

	/**
	 * Getter for the XSL template file
	 * @param xsltFile
	 */
	public String getXSLTemplate() {
		return xslt;
	}


}
