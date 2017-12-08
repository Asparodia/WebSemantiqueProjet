/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.jenasandbox;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.util.FileManager;

/**
 *
 * @author danielahmed
 */
public class InsertInFile extends Object {
    // some definitions
    static String schema    = "http://schema.org/";
    static String rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    static String wgs84 = "http://www.w3.org/2003/01/geo/wgs84_pos#";
    static String local = "http://localhost:3333/";
    
    static final String mt180  = "/Users/danielahmed/Desktop/WebSemantiqueProjet/Datasets/MT180_WithConst.ttl";
    static final String mt180_w  = "/Users/danielahmed/Desktop/WebSemantiqueProjet/Datasets/MT180_liee.ttl";
    // Données sur établissements publics et privés impliqués dans la recherche et dévelippement
    static final String data  = "/Users/danielahmed/Desktop/WebSemantiqueProjet/Datasets/etaPub&Prive.ttl";
    
    // Données sur le concours etoiles europe
    static final String dataConcoursEtoiles  = "/Users/danielahmed/Desktop/tarql-1.1/src/RDFGraph_Concours_Etoiles_Europes.rdf";
    
    static final String owl = "http://www.w3.org/2002/07/owl#";
    
      public static void main (String args[]) {
          
        
        
        // create an empty model
        Model model = ModelFactory.createDefaultModel();
        
        Property schema_longitue = model.createProperty( schema + "longitue" );
        Property schema_latitude = model.createProperty( schema + "latitude" );
        Property schema_location = model.createProperty(schema + "location");
        Property schema_geo = model.createProperty( schema + "geo" );
        Property owl_sameAs = model.createProperty(owl + "sameAs");
        Property schema_identifier = model.createProperty( schema + "identifier" );
        
        // create an empty model
        Model modelData = ModelFactory.createDefaultModel();
        Property wgs84_long = modelData.createProperty( wgs84 + "long" );
        Property wgs84_lat = modelData.createProperty( wgs84 + "lat" );
        Property local_SIREN = modelData.createProperty( local + "SIREN" );

        
     // create an empty model
        Model modelDataConcoursEtoiles = ModelFactory.createDefaultModel();
        
        

        InputStream in = FileManager.get().open( mt180 );
        
        if (in == null) {
            throw new IllegalArgumentException( "File: " + mt180 + " not found");
        }
        
        // read the RDF/XML file
        //model.read(in, "N3");
        //RDFDataMgr.read(model, mt180);
        model = RDFDataMgr.loadModel(mt180);
        
        InputStream inData = FileManager.get().open( data );
        if (inData == null) {
            throw new IllegalArgumentException( "File: " + data + " not found");
        }
        // read the RDF/XML file data
        //modelData.read(inData, "TURTLE");
        //RDFDataMgr.read(modelData, data);
        modelData = RDFDataMgr.loadModel(data);
        
        
        InputStream inDataConcoursEtoiles = FileManager.get().open( dataConcoursEtoiles );
        if (inDataConcoursEtoiles == null) {
            throw new IllegalArgumentException( "File: " + dataConcoursEtoiles + " not found");
        }
        // read the RDF/XML file dataConcoursEtoiles
        modelDataConcoursEtoiles.read(inDataConcoursEtoiles, "");
        
        //modelDataConcoursEtoiles.write(System.out);
        
        ResIterator iter = model.listSubjectsWithProperty(schema_location);
        
        ResIterator iter2 = model.listSubjectsWithProperty(schema_longitue);
        
        ResIterator iter3 = model.listSubjectsWithProperty(schema_identifier);
        
        ResIterator iterData;
        
        ResIterator iterDataConcoursEtoiles;
        
        String longitude;
        String latitude;
        Resource rIt;
        Resource rItData;
        Resource rItDataConcoursEtoiles;
        
        while (iter.hasNext()) {
            rIt = iter.nextResource();
            longitude = rIt
                    .getProperty(schema_location)
                    .getResource()
                    .getProperty(schema_geo)
                    .getResource()
                    .getProperty(schema_longitue)
                    .getLiteral().getString();
            latitude = rIt
                    .getProperty(schema_location)
                    .getResource()
                    .getProperty(schema_geo)
                    .getResource()
                    .getProperty(schema_latitude)
                    .getLiteral().getString();
            iterData = modelData.listSubjectsWithProperty(wgs84_long,longitude);
                   
            while(iterData.hasNext()){
                rItData = iterData.nextResource();
                if(rItData.hasLiteral(wgs84_lat,latitude)){
                		
                		//System.out.println();
                    rIt.addProperty(owl_sameAs,  model.createResource(rItData.getProperty(local_SIREN).getLiteral().toString()));
                }
            }

        }
        
        
        try (OutputStream outMt180 = new FileOutputStream(mt180_w)) {
	        	
	        	model.write(outMt180, "N3");
        } catch (IOException e) {
            e.printStackTrace();
        }

      }
}

