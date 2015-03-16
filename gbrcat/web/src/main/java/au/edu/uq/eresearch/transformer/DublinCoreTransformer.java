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

package au.edu.uq.eresearch.transformer;

import ORG.oclc.oai.util.OAIUtil;
import au.edu.uq.eresearch.Collection;
import au.edu.uq.eresearch.Person;
import au.edu.uq.eresearch.Project;

import java.util.ArrayList;
import java.util.List;

public class DublinCoreTransformer implements DomainTransformer {

    public static final String NS_PREFIX = "<oai_dc:dc xmlns:oai_dc=\"http://www.openarchives.org/OAI/2.0/oai_dc/\" "
            + "xmlns:dc=\"http://purl.org/dc/elements/1.1/\" "
            + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
            + "xsi:schemaLocation=\"";

    private String schemaLocation;

    public DublinCoreTransformer(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    public String generateCollection(Collection col) throws Exception {
        List<Collection> list = new ArrayList<Collection>(1);
        list.add(col);
        return generateCollections(list);

    }

    public String generateCollections(List<Collection> colList) throws Exception {
        StringBuffer sb = new StringBuffer();
        for (Collection col : colList) {
            sb.append(NS_PREFIX + this.schemaLocation + "\">\n");

            sb.append(getDCEntry("dc:title", col.getTitle()));
            sb.append(getDCEntry("dc:creator", col.getTitle()));
            sb.append(getDCEntry("dc:subject", col.getTitle()));
            sb.append(getDCEntry("dc:description", col.getTitle()));
            sb.append(getDCEntry("dc:publisher", col.getTitle()));
            sb.append(getDCEntry("dc:contributor", col.getTitle()));
            sb.append(getDCEntry("dc:date", col.getDateadded().toString()));
            sb.append(getDCEntry("dc:type", col.getTitle()));
            sb.append(getDCEntry("dc:format", col.getTitle()));
            sb.append(getDCEntry("dc:identifier", col.getTitle()));
            sb.append(getDCEntry("dc:source", col.getTitle()));
            sb.append(getDCEntry("dc:language", col.getTitle()));
            sb.append(getDCEntry("dc:relation", col.getTitle()));
            sb.append(getDCEntry("dc:coverage", col.getTitle()));
            sb.append(getDCEntry("dc:rights", col.getTitle()));

            sb.append("</oai_dc:dc>\n");

        }
        return sb.toString();
    }

    @Override
    public String generatePerson(Person person) {
        return null;
    }

    @Override
    public String generateProject(Project project) {
        return null;
    }

    private String getDCEntry(String dcElement, String value) {
        StringBuffer sb = new StringBuffer();
        sb.append("<").append(dcElement).append(">");
        sb.append(OAIUtil.xmlEncode(value));
        sb.append("</").append(dcElement).append(">\n");
        return sb.toString();
    }

}
