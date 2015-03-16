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

import org.hibernate.annotations.Type;

import com.vividsolutions.jts.geom.Geometry;

@Entity
public class Site {
	
	@Id
	private String id;
    
	@Column
    private String name;

	@Column
    private String site_type;
	
	@Column
    private Double depth;
	
	@Column
    private String depth_desc;
	
	@Column
	@Type(type = "org.hibernatespatial.GeometryUserType")
	private Geometry point_geometry;
	
	@Column
	@Type(type = "org.hibernatespatial.GeometryUserType")
	private Geometry region_geometry;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSite_type() {
		return site_type;
	}

	public void setSite_type(String site_type) {
		this.site_type = site_type;
	}

	public Double getDepth() {
		return depth;
	}

	public void setDepth(Double depth) {
		this.depth = depth;
	}

	public String getDepth_desc() {
		return depth_desc;
	}

	public void setDepth_desc(String depth_desc) {
		this.depth_desc = depth_desc;
	}

	public Geometry getPoint_geometry() {
		return point_geometry;
	}

	public void setPoint_geometry(Geometry point_geometry) {
		this.point_geometry = point_geometry;
	}

	public Geometry getRegion_geometry() {
		return region_geometry;
	}

	public void setRegion_geometry(Geometry region_geometry) {
		this.region_geometry = region_geometry;
	}
}
