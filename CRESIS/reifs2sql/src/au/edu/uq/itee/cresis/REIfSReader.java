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

package au.edu.uq.itee.cresis;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLProperty;

public class REIfSReader {

	private OutputStreamWriter out;

	public REIfSReader(String uri) throws OntologyLoadException {
		try {
			out = new OutputStreamWriter(new FileOutputStream("REIfS.sql"), "UTF-8");
			OWLModel owlModel = ProtegeOWL.createJenaOWLModelFromURI(uri);

			tools(owlModel);
			characteristics(owlModel);
			unit(owlModel);
			actors(owlModel);
			programs(owlModel);
			qualifier(owlModel);
			targets(owlModel);
		} catch (IOException e) {
			System.out.println("Unable to open REIfS.sql for writing.");
		} finally {
			try {
				out.close();
			} catch (IOException e) {

			}
		}

	}

	private void targets(OWLModel owlModel) throws IOException {
		OWLNamedClass targetClass = owlModel
				.getOWLNamedClass("http://maenad.itee.uq.edu.au/metadata/Physical#Physical");
		OWLDatatypeProperty descProp = owlModel
				.getOWLDatatypeProperty("http://maenad.itee.uq.edu.au/metadata/Abstract#has_description");
		for (OWLIndividual targetInd : (Collection<OWLIndividual>) targetClass
				.getInstances()) {

			// ignore tools
			if (!targetInd
					.hasRDFType(
							owlModel
									.getOWLNamedClass("http://maenad.itee.uq.edu.au/metadata/Physical#Tool"),
							true)) {
				Map<String, String> values = getPropertyValues(targetInd,
						new OWLProperty[] { descProp });
				values.put("name", "'" + stripInstace(targetInd.getLocalName())
						+ "'");
				out.write(buildInsertStatement("target", values));
			}
		}
	}

	private void qualifier(OWLModel owlModel) throws IOException {
		OWLNamedClass qualifierClass = owlModel
				.getOWLNamedClass("http://maenad.itee.uq.edu.au/metadata/Abstract#Qualifier");
		for (OWLIndividual qualifierInd : (Collection<OWLIndividual>) qualifierClass
				.getInstances()) {
			Map<String, String> values = new HashMap<String, String>();
			values.put("qualifier", "'"
					+ stripInstace(qualifierInd.getLocalName()) + "'");
			out.write(buildInsertStatement("data_qualifier", values));
		}
	}

	private void unit(OWLModel owlModel) throws IOException {
		OWLNamedClass unitClass = owlModel
				.getOWLNamedClass("http://maenad.itee.uq.edu.au/metadata/Abstract#Unit");
		OWLDatatypeProperty descProp = owlModel
				.getOWLDatatypeProperty("http://maenad.itee.uq.edu.au/metadata/Abstract#has_description");
		for (OWLIndividual unitInd : (Collection<OWLIndividual>) unitClass
				.getInstances()) {
			Map<String, String> values = getPropertyValues(unitInd,
					new OWLProperty[] { descProp });
			values
					.put("name", "'" + stripInstace(unitInd.getLocalName())
							+ "'");
			out.write(buildInsertStatement("unit", values));
		}

	}

	private void actors(OWLModel owlModel) throws IOException {
		//now handle the actors
		OWLNamedClass actorTypeClass = owlModel
				.getOWLNamedClass("http://maenad.itee.uq.edu.au/metadata/Actor#Actor");
		for (OWLNamedClass actorTypeChildClass : (Collection<OWLNamedClass>) actorTypeClass
				.getSubclasses()) {
			out.write(buildInsertStatement("actor_type",
					new String[] { "type" }, new String[] { "'"
							+ actorTypeChildClass.getLocalName() + "'" }));
			// Actor instances
			OWLDatatypeProperty nameProp = owlModel
					.getOWLDatatypeProperty("http://maenad.itee.uq.edu.au/metadata/Actor#has_name");
			OWLDatatypeProperty emailProp = owlModel
					.getOWLDatatypeProperty("http://maenad.itee.uq.edu.au/metadata/Actor#has_email");
			for (OWLIndividual actorInd : (Collection<OWLIndividual>) actorTypeChildClass
					.getInstances()) {
				Map<String, String> values = getPropertyValues(actorInd,
						new OWLProperty[] { nameProp, emailProp });
				values.put("type", "(SELECT id FROM actor_type WHERE type='"
						+ actorTypeChildClass.getLocalName() + "')");
				out.write(buildInsertStatement("actor", values));
			}
		}
		
		//now handle the sensor & software actors
		String[] otherActors = new String[] {"Sensor", "Software"}; 
		for (String actors : otherActors) {
			//get a bind on the actor class we are looking at
			actorTypeClass = owlModel.getOWLNamedClass("http://maenad.itee.uq.edu.au/metadata/Physical#" + actors);
			//get the name property associated with the sensor and software actors.
			OWLDatatypeProperty nameProp = owlModel.getOWLDatatypeProperty("http://maenad.itee.uq.edu.au/metadata/Physical#has_name");
			//create sql inserts for all instances of this type of actor
			for (OWLIndividual actorInd : (Collection<OWLIndividual>) actorTypeClass.getInstances()) {
				Map<String, String> values = getPropertyValues(actorInd,new OWLProperty[] { nameProp });
					values.put("type", "(SELECT id FROM actor_type WHERE type='Machine')");
					out.write(buildInsertStatement("actor", values));
			}
		}
	}

	private void programs(OWLModel owlModel) throws IOException {
		OWLNamedClass programTypeClass = owlModel
				.getOWLNamedClass("http://maenad.itee.uq.edu.au/metadata/Abstract#Program");
		for (OWLNamedClass programTypeChildClass : (Collection<OWLNamedClass>) programTypeClass
				.getSubclasses()) {
			OWLDatatypeProperty methodProp = owlModel
					.getOWLDatatypeProperty("http://maenad.itee.uq.edu.au/metadata/Abstract#has_methodology");
			OWLDatatypeProperty urlProp = owlModel
					.getOWLDatatypeProperty("http://maenad.itee.uq.edu.au/metadata/Abstract#has_url");
			OWLDatatypeProperty disclaimProp = owlModel
					.getOWLDatatypeProperty("http://maenad.itee.uq.edu.au/metadata/Abstract#has_disclaimer");
			OWLObjectProperty actorsProp = owlModel
					.getOWLObjectProperty("http://maenad.itee.uq.edu.au/metadata/Abstract#has_actor");
			for (OWLIndividual programInd : (Collection<OWLIndividual>) programTypeChildClass
					.getInstances()) {
				Map<String, String> values = getPropertyValues(programInd,
						new OWLProperty[] { methodProp, urlProp });
				values.put("name", "'" + programInd.getLocalName() + "'");
				out.write(buildInsertStatement("program", values));
				
				// actor <-> programs
				for (Object obj : programInd.getPropertyValues(actorsProp)){
					if (obj != null) {
						String actorName = ((OWLIndividual)obj).getLocalName().replace("_", " ");
						Map<String, String> actorProgramValues = new HashMap<String, String>(2);
						actorProgramValues.put("programID",	"(SELECT id from program where name = '"+programInd.getLocalName()+"')");
						actorProgramValues.put("actorID",	"(SELECT id from actor where name = '"+actorName+"')");
						out.write(buildInsertStatement("program_actor", actorProgramValues));
					}
				}
			}
		}
	}

	private void characteristics(OWLModel owlModel) throws IOException {
		OWLNamedClass characteristicClass = owlModel
				.getOWLNamedClass("http://maenad.itee.uq.edu.au/metadata/Abstract#Characteristic");
		for (OWLNamedClass characteristicChildClass : (Collection<OWLNamedClass>) characteristicClass
				.getSubclasses()) {
			OWLDatatypeProperty descProp = owlModel
					.getOWLDatatypeProperty("http://maenad.itee.uq.edu.au/metadata/Abstract#has_description");

			// get the name of the class we are working on
			String className = characteristicChildClass.getLocalName();
			// Skip these classes
			// Base_Characterisitc & Derived_Characteristic &
			// Complex_Derived_Characterisitc
			if (!className.endsWith("_Characterisitc")) {
				out.write(buildInsertStatement("characteristic", new String[] {
						"name", "description" }, new String[] {
						"'" + className + "'",
						"'" + className.replace('_', ' ') + "'" }));
			}
		}
	}

	private void tools(OWLModel owlModel) throws IOException {
		OWLNamedClass toolTypeClass = owlModel
				.getOWLNamedClass("http://maenad.itee.uq.edu.au/metadata/Physical#Tool");
		for (OWLNamedClass toolTypeChildClass : (Collection<OWLNamedClass>) toolTypeClass
				.getSubclasses()) {
			out.write("INSERT INTO tool_type (type) values ('"
					+ toolTypeChildClass.getLocalName() + "');");
			// Tool instances
			OWLDatatypeProperty nameProp = owlModel
					.getOWLDatatypeProperty("http://maenad.itee.uq.edu.au/metadata/Physical#has_name");
			OWLDatatypeProperty descProp = owlModel
					.getOWLDatatypeProperty("http://maenad.itee.uq.edu.au/metadata/Physical#has_description");
			OWLDatatypeProperty manufProp = owlModel
					.getOWLDatatypeProperty("http://maenad.itee.uq.edu.au/metadata/Physical#has_manufacturer");
			OWLDatatypeProperty modelProp = owlModel
					.getOWLDatatypeProperty("http://maenad.itee.uq.edu.au/metadata/Physical#has_model_number");
			Set<String> insertStr = new HashSet<String>(); // used to avoid dups
			for (OWLIndividual toolInd : (Collection<OWLIndividual>) toolTypeChildClass
					.getInstances()) {
				Map<String, String> values = getPropertyValues(toolInd,
						new OWLProperty[] { nameProp, descProp, manufProp,
								modelProp });
				values.put("type", "(SELECT id FROM tool_type WHERE type='"
						+ toolTypeChildClass.getLocalName() + "')");
				insertStr.add(buildInsertStatement("tool", values));
			}
			for (String stmt : insertStr) {
				out.write(stmt);
			}
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// Helper methods
	// /////////////////////////////////////////////////////////////////////////

	private Map<String, String> getPropertyValues(OWLIndividual ind,
			OWLProperty[] props) {
		HashMap<String, String> values = new HashMap<String, String>(
				props.length);
		for (OWLProperty prop : props) {
			Object value = ind.getPropertyValue(prop);
			if (value != null) {
				values.put(prop.getLocalName(), "'" + value.toString() + "'");
			}
		}
		return values;
	}

	private String buildInsertStatement(String table, Map<String, String> values) {
		return buildInsertStatement(table, values.keySet().toArray(
				new String[values.size()]), values.values().toArray(
				new String[values.size()]));
	}

	private String buildInsertStatement(String table, String[] columns,
			String[] values) {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO ").append(table).append(" (");

		for (String col : columns) {
			sb.append(col.startsWith("has_") ? col.substring(4) : col).append(
					",");
		}
		sb.delete(sb.length() - 1, sb.length()); // remove trailing comma
		sb.append(") VALUES (");

		for (String val : values) {
			sb.append(val).append(",");
		}
		sb.delete(sb.length() - 1, sb.length()); // remove trailing comma
		sb.append(");\n");

		return sb.toString();
	}

	private static String stripInstace(String str) {
		return str.endsWith("_Instance") ? str.substring(0, str.length()
				- "_Instance".length()) : str;
	}

	public static void main(String[] args) throws OntologyLoadException {
		new REIfSReader(args[0]);
	}
}
