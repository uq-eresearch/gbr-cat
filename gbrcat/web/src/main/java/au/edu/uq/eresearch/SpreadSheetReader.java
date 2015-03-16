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

package au.edu.uq.eresearch;

import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.util.*;

public class SpreadSheetReader {

	public static ArrayList<ArrayList<String>> getDataFromCSV(File file){
		ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
		
		try{
			au.com.bytecode.opencsv.CSVReader CR = new au.com.bytecode.opencsv.CSVReader(new java.io.FileReader(file));
		    List<String[]> dataLine = CR.readAll();
		    for(String[] lr: dataLine){
		    	ArrayList<String> row = new ArrayList<String>();
		    	for(String str: lr){
		    		row.add(str);
		    	}
		    	data.add(row);
		    }
		} catch (Exception e){
			System.err.println(e);
		}
	   	return data;
	};
	
	public static ArrayList<String> readLineFromCSV(File file,int rowNum){
		ArrayList<String> data = new ArrayList<String>();
		
		try{
			au.com.bytecode.opencsv.CSVReader CR = new au.com.bytecode.opencsv.CSVReader(new java.io.FileReader(file));
		    ArrayList<String[]> dataLine = (ArrayList<String[]>) CR.readAll();
		    
	    	for(String str: dataLine.get(rowNum)){
	    		data.add(str);
	    	}
		} catch (Exception e){
			System.err.println(e);
		}
	   	return data;
	}

	
	public static ArrayList<ArrayList<String>> getDataFromXLS(File file){
		ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
		
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            Workbook workbook = WorkbookFactory.create(fis);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator rows = sheet.rowIterator();
            while (rows.hasNext()) {
                Row row = (Row) rows.next();
                Iterator cells = row.cellIterator();

                ArrayList<String> newrow = new ArrayList<String>();
                while (cells.hasNext()) {
                	Cell cell = (Cell) cells.next();
                    newrow.add(cell.toString());
                }
                data.add(newrow);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
            	try{
            		fis.close();
            	} catch (Exception e){
            		
            	}
            }
        }
        
	   	return data;
	};
	
}
