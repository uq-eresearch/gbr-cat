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

package au.edu.uq.eresearch.dao;

import au.edu.uq.eresearch.Collection;
import au.edu.uq.eresearch.app.GbrcatApplication;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.util.List;

/**
 * Data Access Object for dealing with collections.
 * <p/>
 * Provides create, update, delete, retrieve and search functionality.
 * Transaction are not handled by this class, they should be handling by
 * the calling class. For example a Collection created using
 * CollectionDAO.create will not be commited / persisted within CollectionDAO.
 */
public class CollectionDAO {

    /**
     * Retrieve the collection represented by an collection Id.
     *
     * @param id collection id
     * @return collection object
     */
    public static Collection getCollection(int id) {
        EntityManager em = GbrcatApplication.getConfiguration().getEntityManger();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        Query query = em.createQuery("SELECT o FROM Collection o WHERE o.collectionid = :id");
        query.setParameter("id", id);
        List<?> resultList = query.getResultList();
        transaction.commit();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Collection) resultList.get(0);
    }


    /**
     * Return all collections.
     *
     * @return list of all collection objects
     */
    @SuppressWarnings("unchecked")
    public static List<Collection> getAllCollections() {
        EntityManager em = GbrcatApplication.getConfiguration().getEntityManger();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        Query query = em.createQuery("SELECT o FROM Collection o");
        List<?> list = query.getResultList();
        transaction.commit();
        return (List<Collection>) list;

    }

}
