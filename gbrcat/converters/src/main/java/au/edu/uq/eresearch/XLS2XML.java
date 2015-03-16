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


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class XLS2XML {
	public void process(InputStream inputstream) throws InvalidFormatException, IOException {
        Workbook workbook = WorkbookFactory.create(inputstream);
        DataFormatter formatter = new DataFormatter();
        ArrayList<String> header = new ArrayList<String>();
        Sheet sheet = workbook.getSheetAt(0);
        
        if(sheet.getPhysicalNumberOfRows() > 0) {
         
            Row row = sheet.getRow(0);
            Cell cell;
            
            for (int j = 0; j <= row.getLastCellNum(); j++) {
            	cell = row.getCell(j);
            	String val = formatter.formatCellValue(cell);
            	val = val.trim();
            	val = val.replaceAll(" +", "_");
            	val = val.replaceAll("[()]", "");
            	
            	header.add(val);
            }
            
            for(int i = 1; i <= sheet.getLastRowNum(); i++) {
            	row = sheet.getRow(i);

                if(row != null) {
                    System.out.print("<record>");
                	for(int j = 0; j <= row.getLastCellNum(); j++) {
                        cell = row.getCell(j);
                        if(cell != null) {
                            if(cell.getCellType() != Cell.CELL_TYPE_FORMULA) {
                                System.out.print("<" + header.get(j) + ">");
                            	System.out.print(formatter.formatCellValue(cell));
                            	System.out.print("</" + header.get(j) + ">");
                            }
                        }
                	}
                	System.out.print("</record>");
                }
            }
        }
	}
}