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

package au.edu.uq.eresearch.cresis;

import java.sql.*;
import java.util.List;

import javax.persistence.*;

public class Controller {

	public static void addObject(Object o){
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("cresis");
		EntityManager em = emf.createEntityManager();
		
   		em.getTransaction().begin();
		em.persist(o);
		em.getTransaction().commit();
		
		em.close();
		emf.close();
	}
	
	public static boolean hasObservationActor(int obs_id,int actor_id){
		java.sql.Connection conn; 
	
		try { 
			Class.forName("org.postgresql.Driver"); 
			String url = "jdbc:postgresql://localhost/cresis"; 
			conn = DriverManager.getConnection(url, "gis", "p0stGr3s"); 
	
			Statement s = conn.createStatement(); 
			ResultSet r = s.executeQuery("select oa from observation_actor oa where observationid = '" 
					+ obs_id + "' and actorid = '" + actor_id + "'"); 
			if(r.next()){
				return true;
			}
		} catch( Exception e ) { 
			e.printStackTrace(); 
		}
		return false;
	}
	
	public static void insertObservationActor(int obs_id, int actor_id) throws Exception{
		java.sql.Connection conn; 
		
		try { 
			Class.forName("org.postgresql.Driver"); 
			String url = "jdbc:postgresql://localhost/cresis"; 
			conn = DriverManager.getConnection(url, "gis", "p0stGr3s"); 
	
			Statement s = conn.createStatement(); 
			s.executeUpdate("insert into Observation_actor (observationid, actorid) values (" + obs_id + "," + actor_id + ")");
		} catch( Exception e ) { 
			throw(e); 
		}
	}
	
	public static void insertObservationMeasurement(int obs_id, int measurement_id) throws Exception{
		java.sql.Connection conn; 
		
		try { 
			Class.forName("org.postgresql.Driver"); 
			String url = "jdbc:postgresql://localhost/cresis"; 
			conn = DriverManager.getConnection(url, "gis", "p0stGr3s"); 
	
			Statement s = conn.createStatement(); 
			s.executeUpdate("INSERT INTO Observation_measurement (observationid, measurementid) VALUES (" + obs_id + "," + measurement_id + ")");
		} catch( Exception e ) { 
			throw(e); 
		}
	}
	
	public static String getName(int actorid){
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("gisdb");
		EntityManager em = emf.createEntityManager();
		
		Query query = em.createQuery("SELECT a FROM Person a WHERE actorid = " + actorid);
	    List<au.edu.uq.eresearch.Person> actorData = (List<au.edu.uq.eresearch.Person>) query.getResultList();
		
	    au.edu.uq.eresearch.Person a = actorData.get(0);
	    
    	if(a.getFirstname() != null && a.getLastname() != null){
	    	return  a.getFirstname() + " " + a.getLastname();
    	} else {
	    	return a.getOrganisation();
    	}
	}

	
}
