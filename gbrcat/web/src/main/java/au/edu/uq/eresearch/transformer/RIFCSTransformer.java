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

import au.edu.uq.eresearch.Collection;
import au.edu.uq.eresearch.*;
import au.edu.uq.eresearch.Subject;
import au.edu.uq.eresearch.app.CollectionsRegistryConfiguration;
import au.edu.uq.eresearch.dao.CollectionDAO;
import au.edu.uq.eresearch.dao.PersonDao;
import com.vividsolutions.jts.geom.Envelope;
import org.ands.rifcs.base.*;
import org.xml.sax.SAXException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Produced a RIF-CS representation of the collection domain object.
 */
public class RIFCSTransformer implements DomainTransformer {

    /**
     * Generate a String of RIF-CS XML content from a single collection objects.
     *
     * @param collection collection object to generateCollection RIF-CS for
     * @return string of RIF-CS content
     */
    public String generateCollection(Collection collection) throws RIFCSException,
            MalformedURLException, SAXException, IOException,
            ParserConfigurationException {
        List<Collection> list = new ArrayList<Collection>(1);
        list.add(collection);
        return generateCollections(list);
    }

    @Override
    public String generatePerson(Person person) throws RIFCSException, MalformedURLException, SAXException,
            IOException, ParserConfigurationException {
        List<Person> list = new ArrayList<Person>(1);
        list.add(person);
        return generatePersons(list);
    }

    @Override
    public String generateProject(Project project) throws RIFCSException, MalformedURLException, SAXException,
            IOException, ParserConfigurationException {
        List<Project> list = new ArrayList<Project>(1);
        list.add(project);
        return generateProjects(list);
    }

    /**
     * Generate a String of RIF-CS XML content from a list of Collections
     * objects.
     *
     * @param colList the list of Collections to generateCollection RIF-CS for
     * @return string of RIF-CS content
     */
    public String generateCollections(List<Collection> colList) throws RIFCSException,
            MalformedURLException, SAXException, IOException,
            ParserConfigurationException {
        return generateCollectionsWrapper(colList).toString();
    }

    private String generatePersons(List<Person> list) throws RIFCSException, MalformedURLException, SAXException,
            IOException, ParserConfigurationException {
        return generatePersonsWrapper(list).toString();
    }

    private String generateProjects(List<Project> list) throws RIFCSException, MalformedURLException, SAXException,
            IOException, ParserConfigurationException {
        return generateProjectsWrapper(list).toString();
    }

    /**
     * Generate RIF-CS XML content from a list of Collections objects writing
     * the output to a OutputStream.
     *
     * @param collectionList the list of Collections to generateCollection RIF-CS for
     * @param os             the outputstream where content is written.
     * @return string of RIF-CS content
     */
    public void generate(List<Collection> collectionList, OutputStream os)
            throws MalformedURLException, RIFCSException, SAXException,
            IOException, ParserConfigurationException {
        generateCollectionsWrapper(collectionList).write(os);
    }

    private RIFCSWrapper generateCollectionsWrapper(List<Collection> colList)
            throws RIFCSException, MalformedURLException, SAXException,
            IOException, ParserConfigurationException {
        RIFCSWrapper rifcsWrapper = new RIFCSWrapper();
        RIFCS rifcs = rifcsWrapper.getRIFCSObject();
        for (Collection col : colList) {

            RegistryObject registryObject = rifcs.newRegistryObject();

            String collectionId = CollectionsRegistryConfiguration.getApplicationURIPrefix() + au.edu.uq.eresearch.app.Constants.VIEW_COLLECTION_PATH + col.getCollectionid();
            registryObject.setKey(collectionId);
            registryObject.setGroup(CollectionsRegistryConfiguration.getRifCsOrganisation());
            registryObject.setOriginatingSource(CollectionsRegistryConfiguration.getRifCsLink());
            registryObject.setOriginatingSourceType("authoritative");
            org.ands.rifcs.base.Collection collection = registryObject.newCollection();

            if (col.getCollectiontype() != null) {
                collection.setType(col.getCollectiontype().toLowerCase());
            }

            collection.addIdentifier(collectionId, "uri");

            Name name = collection.newName();
            name.setType("primary");
            // alternatively could use n.addNamePart("Sample Collection", null,
            // null);
            NamePart namePart = name.newNamePart();
            namePart.setValue(col.getTitle());
            name.addNamePart(namePart);
            collection.addName(name);

            Location addrLocation = collection.newLocation();
            Address address = addrLocation.newAddress();
            Electronic electronic = address.newElectronic();
            electronic.setValue(collectionId);
            electronic.setType("url");
            address.addElectronic(electronic);
            addrLocation.addAddress(address);
            collection.addLocation(addrLocation);

            if (col.getSpatial() != null) {
                Location spatialLoc = collection.newLocation();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

                if (col.getBegindate() != null) {
                    spatialLoc.setDateFrom(formatter.format(col.getBegindate()));
                }

                if (col.getEnddate() != null) {
                    spatialLoc.setDateTo(formatter.format(col.getEnddate()));
                }

                spatialLoc.setType("coverage");
                Spatial spatial = spatialLoc.newSpatial();
                Envelope envelope = col.getSpatial().getEnvelopeInternal();
                spatial.setType("iso19139dcmiBox");
                spatial.setValue("northlimit=" + envelope.getMinX() + ";" +
                        "southlimit=" + envelope.getMaxX() + ";" +
                        "westlimit=" + envelope.getMinY() + ";" +
                        "eastLimit=" + envelope.getMaxY() + "; projection=WGS84x");
                spatialLoc.addSpatial(spatial);
                collection.addLocation(spatialLoc);
            }


            /* Connections to party objects. */
            if (col.getOwner_actorid() > 0) {
                RelatedObject partyRelatedObject = collection.newRelatedObject();
                partyRelatedObject.setKey(CollectionsRegistryConfiguration.getApplicationURIPrefix() + au.edu.uq.eresearch.app.Constants.VIEW_PERSON_PATH + col.getOwner_actorid());
                Person person = PersonDao.getPerson(col.getOwner_actorid());
                if (person.getFirstname() != null) {
                    partyRelatedObject.addRelation("isOwnedBy", null, null, null);
                } else {
                    partyRelatedObject.addRelation("isManagedBy", null, null, null);
                }
                collection.addRelatedObject(partyRelatedObject);
            }

            /* Connections to project objects. */
            if (col.getProject_projectid() > 0) {
                RelatedObject projectRelatedObject = collection.newRelatedObject();
                projectRelatedObject.setKey(CollectionsRegistryConfiguration.getApplicationURIPrefix() + au.edu.uq.eresearch.app.Constants.VIEW_PROJECT_PATH + col.getProject_projectid());
                projectRelatedObject.addRelation("isOutputOf", null, null, null);
                collection.addRelatedObject(projectRelatedObject);
            }
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("gisdb");
            EntityManager em = emf.createEntityManager();

            Query query = em.createQuery("SELECT canz FROM Collection_anzsrcfield canz WHERE collection_collectionid = " + col.getCollectionid());
            List<Collection_anzsrcfield> collection_anzsrcfields = (List<Collection_anzsrcfield>) query.getResultList();
            for (Collection_anzsrcfield collection_anzsrcfield : collection_anzsrcfields) {
                String anzsrcFieldCode = Integer.toString(collection_anzsrcfield.getAnzsrcfields_code());
                for (int i = 0; i < (6 - anzsrcFieldCode.length()); i++) {
                    anzsrcFieldCode = "0" + anzsrcFieldCode;
                }
                collection.addSubject(anzsrcFieldCode, "anzsrc-for", null);
            }

            query = em.createQuery("SELECT cs FROM Collection_subject cs WHERE collection_collectionid = " + col.getCollectionid());
            List<au.edu.uq.eresearch.Collection_subject> collectionSubjects = (List<au.edu.uq.eresearch.Collection_subject>) query.getResultList();
            for (Collection_subject cs : collectionSubjects) {
                query = em.createQuery("SELECT s FROM Subject s WHERE id = " + cs.getSubjects_id());
                List<au.edu.uq.eresearch.Subject> subjects = (List<au.edu.uq.eresearch.Subject>) query.getResultList();
                for (Subject subject : subjects) {
                    collection.addSubject(subject.getValue(), "local", null);
                }
            }

            collection.addDescription(col.getDescription(), "brief", null);
            if (col.getAccessrights() != null) {
                collection.addDescription(col.getAccessrights(), "rights", null);
            }

            String relatedinformation = col.getRelatedinformation();
            if (relatedinformation != null) {
                RelatedInfo relatedInfo = collection.newRelatedInfo();
                relatedInfo.setValue(relatedinformation);
                collection.addRelatedInfo(relatedInfo);
            }
            registryObject.addCollection(collection);
            rifcs.addRegistryObject(registryObject);
        }

        return rifcsWrapper;
    }

    private RIFCSWrapper generatePersonsWrapper(List<Person> list) throws RIFCSException, MalformedURLException, SAXException,
            IOException, ParserConfigurationException {
        RIFCSWrapper rifcsWrapper = new RIFCSWrapper();
        RIFCS rifcs = rifcsWrapper.getRIFCSObject();

        for (Person person : list) {
            RegistryObject registryObject = rifcs.newRegistryObject();

            registryObject.setKey(CollectionsRegistryConfiguration.getApplicationURIPrefix() + au.edu.uq.eresearch.app.Constants.VIEW_PERSON_PATH + person.getActorid());
            registryObject.setGroup(CollectionsRegistryConfiguration.getRifCsOrganisation());
            registryObject.setOriginatingSource(CollectionsRegistryConfiguration.getRifCsLink());
            registryObject.setOriginatingSourceType("authoritative");

            Party party = registryObject.newParty();

            Name name = party.newName();
            name.setType("primary");

            if (person.getFirstname() != null) {
                party.setType("person");

                NamePart np = name.newNamePart();
                np.setType("given");
                np.setValue(person.getFirstname());
                name.addNamePart(np);

                np = name.newNamePart();
                np.setType("family");
                np.setValue(person.getLastname());
                name.addNamePart(np);
            } else {
                party.setType("group");
                NamePart np = name.newNamePart();
                np.setValue(person.getOrganisation());
                name.addNamePart(np);
            }
            party.addName(name);

            if (person.getEmail() != null) {
                Location location = party.newLocation();
                Address address = location.newAddress();
                Electronic electronic = address.newElectronic();

                electronic.setType("email");
                electronic.setValue(person.getEmail());

                address.addElectronic(electronic);
                location.addAddress(address);
                party.addLocation(location);
            }

            if (person.getWebaddress() != null) {
                Location location = party.newLocation();
                Address address = location.newAddress();

                Electronic electronic = address.newElectronic();

                electronic.setType("url");
                electronic.setValue(person.getWebaddress());

                address.addElectronic(electronic);
                location.addAddress(address);
                party.addLocation(location);
            }
            /* Connection to collection and activity records. */
            for (Collection collection : CollectionDAO.getAllCollections()) {
                if (collection.getOwner_actorid() == person.getActorid()) {
                    RelatedObject partyRelatedObject = party.newRelatedObject();
                    partyRelatedObject.setKey(CollectionsRegistryConfiguration.getApplicationURIPrefix() + au.edu.uq.eresearch.app.Constants.VIEW_COLLECTION_PATH + collection.getCollectionid());
                    if (person.getFirstname() != null) {
                        partyRelatedObject.addRelation("isOwnerOf", null, null, null);
                    } else {
                        partyRelatedObject.addRelation("isManagerOf", null, null, null);
                    }
                    party.addRelatedObject(partyRelatedObject);

                    if (collection.getProject_projectid() > 0) {
                        RelatedObject activityRelatedObject = party.newRelatedObject();
                        activityRelatedObject.setKey(CollectionsRegistryConfiguration.getApplicationURIPrefix() + au.edu.uq.eresearch.app.Constants.VIEW_PROJECT_PATH + collection.getProject_projectid());
                        activityRelatedObject.addRelation("isParticipantIn", null, null, null);
                        party.addRelatedObject(activityRelatedObject);
                    }
                }
            }

            if (person.getDescription() != null) {
                Description description = party.newDescription();
                description.setType("brief");
                description.setValue(person.getDescription());
                party.addDescription(description);
            }

            registryObject.addParty(party);

            rifcs.addRegistryObject(registryObject);
        }

        return rifcsWrapper;
    }

    private RIFCSWrapper generateProjectsWrapper(List<Project> list) throws RIFCSException, MalformedURLException, SAXException,
            IOException, ParserConfigurationException {

        RIFCSWrapper rifcsWrapper = new RIFCSWrapper();
        RIFCS rifcs = rifcsWrapper.getRIFCSObject();

        for (Project project : list) {
            RegistryObject registryObject = rifcs.newRegistryObject();

            registryObject.setKey(CollectionsRegistryConfiguration.getApplicationURIPrefix() + au.edu.uq.eresearch.app.Constants.VIEW_PROJECT_PATH + project.getProjectid());
            registryObject.setGroup(CollectionsRegistryConfiguration.getRifCsOrganisation());
            registryObject.setOriginatingSource(CollectionsRegistryConfiguration.getRifCsLink());
            registryObject.setOriginatingSourceType("authoritative");

            Activity activity = registryObject.newActivity();
            activity.setType("project");

            Name name = activity.newName();
            name.setType("primary");
            NamePart np = name.newNamePart();
            np.setValue(project.getTitle());
            name.addNamePart(np);
            activity.addName(name);

            for (Collection collection : CollectionDAO.getAllCollections()) {
                if (collection.getProject_projectid() == project.getProjectid()) {
                    RelatedObject partyRelatedObject = activity.newRelatedObject();
                    partyRelatedObject.setKey(CollectionsRegistryConfiguration.getApplicationURIPrefix() + au.edu.uq.eresearch.app.Constants.VIEW_COLLECTION_PATH + collection.getCollectionid());
                    partyRelatedObject.addRelation("hasOutput", null, null, null);
                    activity.addRelatedObject(partyRelatedObject);
                    if (collection.getOwner_actorid() > 0) {
                        partyRelatedObject = activity.newRelatedObject();
                        partyRelatedObject.setKey(CollectionsRegistryConfiguration.getApplicationURIPrefix() + au.edu.uq.eresearch.app.Constants.VIEW_PERSON_PATH + collection.getOwner_actorid());
                        partyRelatedObject.addRelation("isManagedBy", null, null, null);
                        activity.addRelatedObject(partyRelatedObject);
                    }
                }
            }

            Description desc = activity.newDescription();
            desc.setType("brief");
            desc.setValue(project.getDescription());
            activity.addDescription(desc);

            registryObject.addActivity(activity);
            rifcs.addRegistryObject(registryObject);
        }

        return rifcsWrapper;
    }
}
