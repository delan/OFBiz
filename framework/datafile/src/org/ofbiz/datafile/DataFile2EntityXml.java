/*
 * DataFile2EntityXml.java
 *
 * Created on 15 febbraio 2005, 9.45
 */

package org.ofbiz.datafile;

import java.util.*;
import java.net.*;
import java.io.*;
import org.ofbiz.base.util.*;
import org.ofbiz.datafile.*;

/**
 *
 * @author  jacopo
 */
public class DataFile2EntityXml {
    
    /** Creates a new instance of DataFile2EntityXml */
    public DataFile2EntityXml() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void writeToEntityXml(String fileName, DataFile dataFile) throws DataFileException {
        File file = new File(fileName);
        BufferedWriter outFile = null;

        try {
            
            //outFile = new BufferedWriter(new FileWriter(file));
            outFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
        } catch (Exception e) {
            throw new DataFileException("Could not open file " + fileName, e);
        }
        //----------------------------------------------------
        try {
            outFile.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            outFile.newLine();
            outFile.write("<entity-engine-xml>");
            outFile.newLine();
            List records = dataFile.getRecords();
            for(int r=0; r<records.size(); r++) {
                Record record = (Record)records.get(r);
                ModelRecord modelRecord = record.getModelRecord();
                outFile.write("<" + modelRecord.name + " ");
                for (int f=0;f<modelRecord.fields.size(); f++) {
                    ModelField modelField = (ModelField)modelRecord.fields.get(f);
                    if (modelField.ignored) continue;
                    Object value = record.get(modelField.name);
                    if (value == null) {
                        value = modelField.defaultValue;
                    }
                    if (value instanceof String) {
                        value = ((String)value).trim();
                        if (((String)value).length() == 0) {
                            value = modelField.defaultValue;
                        }
                    }
                    if (value != null) {
                        outFile.write(modelField.name + "=\"" + value + "\" ");
                    }
                }
                outFile.write("/>");
                outFile.newLine();
            }
            outFile.write("</entity-engine-xml>");
            outFile.close();
        } catch (IOException e) {
            throw new DataFileException("Error writing to file " + fileName, e);
        }
        
    }
    
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        String dataFileLoc = args[0];
        String definitionLoc = args[1];
        String definitionName = args[2];
        
        BufferedWriter outFile = new BufferedWriter(new FileWriter(dataFileLoc + ".xml"));
        
        URL dataFileUrl = null;
        //try {
            dataFileUrl = UtilURL.fromFilename(dataFileLoc);
        //} catch (java.net.MalformedURLException e) {
            //messages.add(e.getMessage());
        //}
        URL definitionUrl = null;
        //try {
            definitionUrl = UtilURL.fromFilename(definitionLoc);
        //} catch (java.net.MalformedURLException e) {
            //messages.add(e.getMessage());
        //}

        DataFile dataFile = null;
        if (dataFileUrl != null && definitionUrl != null && definitionName != null && definitionName.length() > 0) {
            try {
                dataFile = DataFile.readFile(dataFileUrl, definitionUrl, definitionName);
            } catch (Exception e) {
                //messages.add(e.toString());
                //Debug.log(e);
            }
        }

        // -----------------------------------------
        List records = dataFile.getRecords();
        for(int r=0; r<records.size(); r++) {
            Record record = (Record)records.get(r);
            ModelRecord modelRecord = record.getModelRecord();
            outFile.write("<" + modelRecord.name + " ");
            for (int f=0;f<modelRecord.fields.size(); f++) {
                ModelField modelField = (ModelField)modelRecord.fields.get(f);
                Object value = record.get(modelField.name);
                if (value instanceof String) {
                    value = ((String)value).trim();
                }
                outFile.write(modelField.name + "=\"" + value + "\" ");
            }
            outFile.write("/>");
            outFile.newLine();
        }
        outFile.close();

    }
    
}
