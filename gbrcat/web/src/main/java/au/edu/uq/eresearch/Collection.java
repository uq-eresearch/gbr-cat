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

package au.edu.uq.eresearch;


import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "collection",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"title"})}
)
public class Collection {

    @Id
    @GeneratedValue
    private int collectionid;

    @Column
    private String title;

    @Column
    private String accessrights;

    @Column
    private Date begindate;

    @Column
    private String contributor;

    @Column
    private Date dateadded;

    @Column(length = 5000)
    private String description;

    @Column
    private Date enddate;

    @Column
    private String pid;

    @Column
    private int owner_actorid;

    @Column
    private int project_projectid;

    @Column
    private String collectiontype;

    @Column
    private String relatedinformation;

    public Collection() {
        this.dateadded = new java.util.Date();
    }

    public int getCollectionid() {
        return collectionid;
    }

    public void setCollectionid(int collectionid) {
        this.collectionid = collectionid;
    }

    public String getAccessrights() {
        return accessrights;
    }

    public void setAccessrights(String accessrights) {
        this.accessrights = accessrights;
    }

    public Date getBegindate() {
        return begindate;
    }

    public void setBegindate(Date begindate) {
        this.begindate = begindate;
    }

    public String getContributor() {
        return contributor;
    }

    public void setContributor(String contributor) {
        this.contributor = contributor;
    }

    public Date getDateadded() {
        return dateadded;
    }

    public void setDateadded(Date dateadded) {
        this.dateadded = dateadded;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getEnddate() {
        return enddate;
    }

    public void setEnddate(Date enddate) {
        this.enddate = enddate;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getOwner_actorid() {
        return owner_actorid;
    }

    public void setOwner_actorid(int owner_actorid) {
        this.owner_actorid = owner_actorid;
    }

    public int getProject_projectid() {
        return project_projectid;
    }

    public void setProject_projectid(int project_projectid) {
        this.project_projectid = project_projectid;
    }

    public String getCollectiontype() {
        return collectiontype;
    }

    public void setCollectiontype(String collectiontype) {
        this.collectiontype = collectiontype;
    }

    public String getRelatedinformation() {
        return relatedinformation;
    }

    public void setRelatedinformation(String relatedinformation) {
        this.relatedinformation = relatedinformation;
    }

    @Column(name = "location")
    @Type(type = "org.hibernatespatial.GeometryUserType")
    private Geometry spatial;

    public Geometry getSpatial() {
        return spatial;
    }

    public void setSpatial(Geometry spatial) {
        this.spatial = spatial;
    }

    public double getXForCoordinate(Coordinate coord) {
        return coord.x;
    }

    public double getYForCoordinate(Coordinate coord) {
        return coord.y;
    }

    public String getSpatialString() {
        String spatial = "";
        if (this.spatial != null) {
            for (Coordinate cor : getSpatial().getCoordinates()) {
                spatial = spatial + cor.x + " " + cor.y + ",";
            }
            spatial = spatial.substring(0, spatial.length() - 1);
        }
        return spatial;
    }

    public void setSpatialFromString(String str) {
        str = str.trim();
        if (str != null && !"".equals(str)) {
            try {
                if (str.contains(",")) {
                    spatial = (Geometry) new WKTReader().read("POLYGON((" + str + "))");
                } else {
                    spatial = (Geometry) new WKTReader().read("POINT(" + str + ")");
                }
            } catch (Exception e) {
                System.err.println(e.toString());
            }
        } else {
            spatial = null;
        }
    }
}
