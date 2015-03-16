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

package au.edu.uq.eresearch.oaipmh;

import ORG.oclc.oai.server.crosswalk.Crosswalk;
import ORG.oclc.oai.server.verb.CannotDisseminateFormatException;
import au.edu.uq.eresearch.Collection;
import au.edu.uq.eresearch.Person;
import au.edu.uq.eresearch.Project;
import au.edu.uq.eresearch.transformer.DomainTransformerFactory;

import java.util.Properties;

public class RIFCSCrosswalk extends Crosswalk {

    public RIFCSCrosswalk(Properties props) {
        super(
                "http://services.ands.org.au/documentation/rifcs/schema/registryObjects.xsd");
    }

    @Override
    public String createMetadata(Object nativeItem)
            throws CannotDisseminateFormatException {
        try {
            String xmlContent = "";
            if (nativeItem instanceof Collection) {
                xmlContent = DomainTransformerFactory.getTransformer(DomainTransformerFactory.RIF_CS).generateCollection((Collection) nativeItem);
            } else if (nativeItem instanceof Person) {
                xmlContent = DomainTransformerFactory.getTransformer(DomainTransformerFactory.RIF_CS).generatePerson((Person) nativeItem);
            } else if (nativeItem instanceof Project) {
                xmlContent = DomainTransformerFactory.getTransformer(DomainTransformerFactory.RIF_CS).generateProject((Project) nativeItem);
            }

            // need to strip the xml declaration - <?xml version="1.0"
            // encoding="UTF-16"?>
            if (xmlContent.startsWith("<?xml ")) {
                xmlContent = xmlContent.substring(xmlContent.indexOf("?>") + 2);
            }
            return xmlContent;
        } catch (Exception e) {
            throw new CannotDisseminateFormatException(e.toString());
        }
    }

    @Override
    public boolean isAvailableFor(Object nativeItem) {
        return true;
    }

}
