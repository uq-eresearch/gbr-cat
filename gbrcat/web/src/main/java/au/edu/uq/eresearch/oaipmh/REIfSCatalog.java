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

import ORG.oclc.oai.server.catalog.AbstractCatalog;
import ORG.oclc.oai.server.catalog.RecordFactory;
import ORG.oclc.oai.server.verb.*;
import au.edu.uq.eresearch.Collection;
import au.edu.uq.eresearch.Person;
import au.edu.uq.eresearch.Project;
import au.edu.uq.eresearch.app.Constants;
import au.edu.uq.eresearch.dao.CollectionDAO;
import au.edu.uq.eresearch.dao.PersonDao;
import au.edu.uq.eresearch.dao.ProjectDao;
import org.hibernate.ObjectNotFoundException;

import java.util.*;

public class REIfSCatalog extends AbstractCatalog {

    public REIfSCatalog(Properties properties) {

    }

    @Override
    public void close() {
        // no action required
    }

    @Override
    public String getRecord(String identifier, String metadataPrefix)
            throws IdDoesNotExistException, CannotDisseminateFormatException,
            OAIInternalServerError {
        try {
            String schemaURL = getCrosswalks().getSchemaURL(metadataPrefix);
            String key = getEntityID(identifier);
            if (identifier.contains(Constants.VIEW_COLLECTION_PATH)) {
                Collection collection = CollectionDAO.getCollection(Integer.parseInt(key));
                return getRecordFactory().create(collection, schemaURL, metadataPrefix);
            } else if (identifier.contains(Constants.VIEW_PERSON_PATH)) {
                Person person = PersonDao.getPerson(Integer.parseInt(key));
                return getRecordFactory().create(person, schemaURL, metadataPrefix);
            } else if (identifier.contains(Constants.VIEW_PROJECT_PATH)) {
                Project project = ProjectDao.getProject(Integer.parseInt(key));
                return getRecordFactory().create(project, schemaURL, metadataPrefix);
            }
            return null;
        } catch (ObjectNotFoundException e) {
            throw new IdDoesNotExistException(identifier);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Vector getSchemaLocations(String identifier) throws IdDoesNotExistException, NoMetadataFormatsException,
            OAIInternalServerError {
        Collection col = CollectionDAO.getCollection(Integer.parseInt(identifier));
        return getRecordFactory().getSchemaLocations(col);
    }

    @Override
    public Map<String, Iterator<String>> listIdentifiers(String from, String until, String set, String metadataPrefix)
            throws BadArgumentException, CannotDisseminateFormatException,
            NoItemsMatchException, NoSetHierarchyException,
            OAIInternalServerError {

        Map<String, Iterator<String>> listIdentifiersMap = new HashMap<String, Iterator<String>>(2);
        List<Collection> CollectionList = CollectionDAO.getAllCollections();
        List<Person> allPersons = PersonDao.getPersonsWithCollections();
        List<Project> allProjects = ProjectDao.getProjectsWithCollection();

        int size = CollectionList.size() + allPersons.size() + allProjects.size();
        List<String> headers = new ArrayList<String>(size);
        List<String> identifiers = new ArrayList<String>(size);

        RecordFactory recordFactory = getRecordFactory();
        for (Collection col : CollectionList) {
            identifiers.add(recordFactory.getOAIIdentifier(col));
//            String oaiIdentifier = getRecordFactory().getOAIIdentifier(col);
            String[] header = recordFactory.createHeader(col);
            headers.add(header[0]);
        }

        for (Person person : allPersons) {
            identifiers.add(recordFactory.getOAIIdentifier(person));
            headers.add(recordFactory.createHeader(person)[0]);
        }

        for (Project project : allProjects) {
            identifiers.add(recordFactory.getOAIIdentifier(project));
            headers.add(recordFactory.createHeader(project)[0]);
        }

        listIdentifiersMap.put("identifiers", identifiers.iterator());
        listIdentifiersMap.put("headers", headers.iterator());
        return listIdentifiersMap;
    }

    @Override
    public Map<String, Iterator<String>> listIdentifiers(String resumptionToken)
            throws BadResumptionTokenException, OAIInternalServerError {
        // No resumption functionality implemented
        return null;
    }

    @Override
    public Map<String, Iterator<String>> listSets()
            throws NoSetHierarchyException, OAIInternalServerError {
        throw new NoSetHierarchyException();
    }

    @Override
    public Map<String, Iterator<String>> listSets(String resumptionToken)
            throws BadResumptionTokenException, OAIInternalServerError {
        // No sets functionality implemented
        return null;
    }

    private String getEntityID(String fullUrl) {
        if (fullUrl.contains("id=")) {
            return fullUrl = fullUrl.split("id=")[1];
        }
        return null;
    }
}
