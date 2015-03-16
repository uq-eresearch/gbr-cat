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

import au.edu.uq.eresearch.Person;
import au.edu.uq.eresearch.app.GbrcatApplication;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.util.List;

/**
 * Author: alabri
 * Date: 10/03/11
 * Time: 9:27 AM
 */
public class PersonDao {

    /**
     * Retrieve the person represented by an person Id.
     *
     * @param id person id
     * @return person object
     */
    public static Person getPerson(int id) {
        EntityManager em = GbrcatApplication.getConfiguration().getEntityManger();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        Query query = em.createQuery("SELECT p FROM Person p WHERE p.actorid = :id");
        query.setParameter("id", id);
        List<?> resultList = query.getResultList();
        transaction.commit();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Person) resultList.get(0);
    }


    /**
     * Return all persons.
     *
     * @return list of all person objects
     */
    @SuppressWarnings("unchecked")
    public static List<Person> getAllPersons() {
        EntityManager em = GbrcatApplication.getConfiguration().getEntityManger();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        Query query = em.createQuery("Select p From Person p");
        List<?> list = query.getResultList();
        transaction.commit();
        return (List<Person>) list;
    }

    public static List<Person> getPersonsWithCollections() {
        EntityManager em = GbrcatApplication.getConfiguration().getEntityManger();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        Query query = em.createQuery("SELECT p FROM Person p WHERE p.actorid in (select c.owner_actorid from Collection c where c.owner_actorid is not null)");
        List<?> list = query.getResultList();
        transaction.commit();
        return (List<Person>) list;
    }
}
