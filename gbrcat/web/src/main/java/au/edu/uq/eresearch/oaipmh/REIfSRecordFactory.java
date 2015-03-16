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

import ORG.oclc.oai.server.catalog.RecordFactory;
import ORG.oclc.oai.server.verb.CannotDisseminateFormatException;
import au.edu.uq.eresearch.Collection;
import au.edu.uq.eresearch.Person;
import au.edu.uq.eresearch.Project;
import au.edu.uq.eresearch.app.CollectionsRegistryConfiguration;
import au.edu.uq.eresearch.app.Constants;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Properties;

public class REIfSRecordFactory extends RecordFactory {

    public REIfSRecordFactory(Properties properties) {
        super(properties);
    }

    @Override
    public String fromOAIIdentifier(String identifier) {
        return identifier;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator getAbouts(Object nativeItem) {
        return null;
    }

    @Override
    public String getDatestamp(Object nativeItem) {
//        return new SimpleDateFormat("yyyy-MM-dd").format(((Collection) nativeItem).getDateadded());
        if (nativeItem instanceof Collection) {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(((Collection) nativeItem).getDateadded());
        } else if (nativeItem instanceof Person) {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(((Person) nativeItem).getDateadded());
        } else if (nativeItem instanceof Project) {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(((Project) nativeItem).getDateadded());
        }
        return null;
    }

    @Override
    public String getOAIIdentifier(Object nativeItem) {
        if (nativeItem instanceof Collection) {
            return CollectionsRegistryConfiguration.getApplicationURIPrefix() + Constants.VIEW_COLLECTION_PATH + ((Collection) nativeItem).getCollectionid();
        } else if (nativeItem instanceof Person) {
            return CollectionsRegistryConfiguration.getApplicationURIPrefix() + Constants.VIEW_PERSON_PATH + ((Person) nativeItem).getActorid();
        } else if (nativeItem instanceof Project) {
            return CollectionsRegistryConfiguration.getApplicationURIPrefix() + Constants.VIEW_PROJECT_PATH + ((Project) nativeItem).getProjectid();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator getSetSpecs(Object nativeItem) throws IllegalArgumentException {
        // Not implemented
        return null;
    }

    @Override
    public boolean isDeleted(Object nativeItem) {
        return nativeItem == null;
    }

    @Override
    public String quickCreate(Object nativeItem, String schemaURL, String metadataPrefix)
            throws IllegalArgumentException, CannotDisseminateFormatException {
        return null;
    }

}
