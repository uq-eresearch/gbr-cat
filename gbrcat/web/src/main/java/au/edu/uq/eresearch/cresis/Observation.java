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

import javax.persistence.*;

@javax.persistence.SequenceGenerator(
    name="SEQ_GEN",
    sequenceName="observation_id_seq",
    allocationSize=20
)

@Entity
public class Observation {
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_GEN")
	private Integer id;
    
	@Column
    private Integer target;
	
	@Column
    private Integer context;
	
	@Column
    private String species;

	@Column
    private String genus;

	@Column
    private String morphology;

	@Column
    private String health;

	@Column
    private Integer individual_count;

	@Column
    private Integer transect_type;	
	
	@Column
    private Integer transect_id;
	
	@Column
    private Integer quadrat_id;
	
	@Column
    private Integer is_part_of_eco_process;
	
	@Column
    private Integer reference;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getTarget() {
		return target;
	}

	public void setTarget(Integer target) {
		this.target = target;
	}

	public Integer getContext() {
		return context;
	}

	public void setContext(Integer context) {
		this.context = context;
	}

	public String getSpecies() {
		return species;
	}

	public void setSpecies(String species) {
		this.species = species;
	}

	public String getGenus() {
		return genus;
	}

	public void setGenus(String genus) {
		this.genus = genus;
	}

	public String getMorphology() {
		return morphology;
	}

	public void setMorphology(String morphology) {
		this.morphology = morphology;
	}

	public String getHealth() {
		return health;
	}

	public void setHealth(String health) {
		this.health = health;
	}

	public Integer getIndividual_count() {
		return individual_count;
	}

	public void setIndividual_count(Integer individual_count) {
		this.individual_count = individual_count;
	}

	public Integer getTransect_type() {
		return transect_type;
	}

	public void setTransect_type(Integer transect_type) {
		this.transect_type = transect_type;
	}

	public Integer getTransect_id() {
		return transect_id;
	}

	public void setTransect_id(Integer transect_id) {
		this.transect_id = transect_id;
	}

	public Integer getQuadrat_id() {
		return quadrat_id;
	}

	public void setQuadrat_id(Integer quadrat_id) {
		this.quadrat_id = quadrat_id;
	}

	public Integer getIs_part_of_eco_process() {
		return is_part_of_eco_process;
	}

	public void setIs_part_of_eco_process(Integer is_part_of_eco_process) {
		this.is_part_of_eco_process = is_part_of_eco_process;
	}

	public Integer getReference() {
		return reference;
	}

	public void setReference(Integer reference) {
		this.reference = reference;
	}
}
