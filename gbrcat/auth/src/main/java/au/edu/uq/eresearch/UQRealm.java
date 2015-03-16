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

import java.security.Principal;
import java.util.ArrayList;

import org.apache.catalina.Container;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.realm.JNDIRealm;

/**
 * A Tomcat Realm that authenticates against JNDI
 * and authorises against a JDBC database.
 * <p>
 * This allows password handling to be external,
 * but still have a list of defined users.
 * <p>
 * The implementation is very simple, it extends JNDIRealm, and has an internal JDBCRealm.
 * Calls are delegated to the JDBCRealm as required. All JNDIRealm configuration attributes
 * are supported, only the subset of JDBCRealm that relates to Roles are offered. There is
 * a clash on the {@link #setConnectionURL} attribute, which is resolved by offering {@link #setJndiURL} and 
 * {@link #jsetJdbcURL}.
 * <p>
 * HOWTO
 * <p>
 * Install the UQRealm WAR in $TOMCAT/common, as well as the JDBC driver.
 * <p>
 * Setup your database, it only needs one table, with users and roles
 * <p>
 * Copy the server.xml snippet into $TOMCAT/conf/server.xml and modify to suit
 * your application. If you replace the Realm element in $TOMCAT/conf/server.xml with
 * the provided snippet, the UQRealm will be the standard Realm for each context.
 * The JNDI parameters are setup to work with the University of 
 * Queenslands LDAP server, the JDBC settings are generic. 
 */
public class UQRealm extends JNDIRealm
{
	private DataSourceRoles database = new DataSourceRoles();
	
	/**
	 * Only succeed if there are associated roles. 
	 */
	@Override
	public Principal authenticate(String username, String credentials)
	{		
		Principal principal = super.authenticate(username, credentials);
						
		if (principal != null) {

			ArrayList<String> roles = database.getUserRoles(username);

			if (roles.isEmpty()) {
				principal = null;

			} else {
				principal = new GenericPrincipal(this, username, credentials, roles);
			}
		}
		
		return principal;
	}

	@Override
	public void start() throws LifecycleException
	{
		database.start();
		super.start();
	}

	@Override
	public void stop() throws LifecycleException
	{
		database.stop();
		super.stop();
	}


	/* I'm sure there's a fancy way of exposing the internal bean api. */

	@Override
	public void setContainer(Container container) {
		super.setContainer(container);
		database.setContainer(container);
	}
	
	public void setDriverName(String driver) {
		database.setDriverName(driver);
	}
	
	public void setJndiURL(String url) {
		this.setConnectionURL(url);
	}
	
	public void setJdbcURL(String url) {
		database.setConnectionURL(url);
	}
	
	public void setRoleNameCol( String roleNameCol ) {
		database.setRoleNameCol(roleNameCol);
	}

	public void setUserNameCol( String userNameCol ) {
		database.setUserNameCol(userNameCol);
	}

	public void setUserRoleTable( String userRoleTable ) {
		database.setUserRoleTable(userRoleTable);
	}
}

