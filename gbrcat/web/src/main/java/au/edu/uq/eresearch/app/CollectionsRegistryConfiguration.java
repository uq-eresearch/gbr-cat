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

package au.edu.uq.eresearch.app;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Configuration for the project, read from properties file.
 */
public class CollectionsRegistryConfiguration {

    public static final String PROPERTIES_FILE = "collections-registry.properties";

    private static Logger log = Logger
            .getLogger(CollectionsRegistryConfiguration.class.getName());

    static {
        loadProperties();
    }

    private static int httpPort;

    private static String applicationURIPrefix;

    private static String rssTitle;
    private static String rssLink;
    private static String rssDesc;

    private static String rifCsOrganisation;
    private static String rssServiceEndPoint;
    private static String rifCsLink;
    private static String rifCsServiceEndPoint;

    private static String googleMapsAPIKey;

    private static void loadProperties() {
        Properties properties = new Properties();
        InputStream is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(PROPERTIES_FILE);
        try {
            properties.load(is);
            log.log(Level.INFO, "Loaded configuration values from "
                    + PROPERTIES_FILE);
        } catch (Exception e) {
            log
                    .log(
                            Level.SEVERE,
                            "Configuration file not found, please ensure "
                                    + "there is a 'health-e-reef.properties' on the classpath"
                                    + e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // nothing to do
                }
            }
        }

        httpPort = Integer
                .parseInt(properties.getProperty("http_port", "8080"));

        applicationURIPrefix = properties.getProperty("applicatin.uri.prefix", "");

        rssTitle = properties.getProperty("rss.feed.title", "");
        rssLink = properties.getProperty("rss.feed.link", "");
        rssDesc = properties.getProperty("rss.feed.description", "");
        rssServiceEndPoint = properties.getProperty(
                "rss.feed.service_end_point", "");

        rifCsOrganisation = properties.getProperty("rif-cs.organisation", "");
        rifCsLink = properties.getProperty("rif-cs.link", "");
        rifCsServiceEndPoint = properties.getProperty(
                "rif-cs.service_end_point", "");

        googleMapsAPIKey = properties.getProperty("googlemaps.api.key");
    }

    public static String getRssTitle() {
        return rssTitle;
    }

    public static String getRssLink() {
        return rssLink;
    }

    public static String getRssDesc() {
        return rssDesc;
    }

    public static String getRssServiceEndPoint() {
        return rssServiceEndPoint;
    }

    public static String getRifCsOrganisation() {
        return rifCsOrganisation;
    }

    public static String getRifCsLink() {
        return rifCsLink;
    }

    public static String getRifCsServiceEndPoint() {
        return rifCsServiceEndPoint;
    }

    public static int getHttpPort() {
        return httpPort;
    }

    public static String getGoogleMapsAPIKey() {
        return googleMapsAPIKey;
    }

    public static String getApplicationURIPrefix() {
        return applicationURIPrefix;
    }

    public static void setApplicationURIPrefix(String applicationURIPrefix) {
        CollectionsRegistryConfiguration.applicationURIPrefix = applicationURIPrefix;
    }
}
