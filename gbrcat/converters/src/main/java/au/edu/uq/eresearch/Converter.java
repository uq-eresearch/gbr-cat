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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.naming.Context;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.fcrepo.client.messaging.JmsMessagingClient;
import org.fcrepo.client.messaging.MessagingClient;
import org.fcrepo.client.messaging.MessagingListener;
import org.fcrepo.server.errors.MessagingException;
import org.fcrepo.server.messaging.JMSManager;

public class Converter implements MessagingListener {
	public static void main(String [ ] args) {
    	try {
			Converter converter = new Converter();
			converter.start();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	MessagingClient messagingClient;
    public void start() throws MessagingException {
        Properties properties = new Properties();
        properties.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                               "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        properties.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
        properties.setProperty(JMSManager.CONNECTION_FACTORY_NAME, "ConnectionFactory");
        properties.setProperty("topic.fedora", "fedora.apim.*");
        messagingClient = new JmsMessagingClient("example1", this, properties, false);
        messagingClient.start();
    }
    public void stop() throws MessagingException {
        messagingClient.stop(false);
    }
    public void onMessage(String clientId, Message message) {
        String pid;
        String method;
    	String messageText = "";
        try {
            messageText = ((TextMessage)message).getText();
        } catch(JMSException e) {
            System.err.println("Error retrieving message text " + e.getMessage());
            return;
        }
        System.out.println("Message received: " + messageText + " from client " + clientId);

        try {
        	pid = message.getStringProperty("pid");
        	method = message.getStringProperty("methodName");
        	System.out.println("pid, method: " + pid + " " + method);
        } catch (JMSException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        	return;
        }        
        
        if (method.equals("ingest")) {
        	HttpGet httpget;
			try {
				httpget = new HttpGet("http://localhost:8080/fedora/objects/" + URLEncoder.encode(pid, "UTF-8") + "/datastreams/upload/content");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return;
			}
        	HttpClient httpclient = new DefaultHttpClient();
        	HttpResponse response;
			try {
				response = httpclient.execute(httpget);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
        	HttpEntity entity = response.getEntity();
        	
        	XLS2XML converter = new XLS2XML();
			try {
				converter.process(entity.getContent());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
        	
        	/* httpclient.post("http://localhost:8080/object/ingest")
        	header
        	xml
        	tail */
        }
    }
}
