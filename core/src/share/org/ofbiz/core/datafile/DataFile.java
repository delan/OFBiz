package org.ofbiz.core.datafile;

import java.util.*;
import java.net.*;
import java.io.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> 
 * <p><b>Description:</b> None
 * <p>Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 * <p>Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *@author <a href='mailto:jonesde@ofbiz.org'>David E. Jones</a>
 *@created Nov 14, 2001
 *@version 1.0
 */

public class DataFile {
  /** List of record in the file, contains Record objects */
  public List records = new Vector();
  /** Contains the definition for the file */
  public ModelDataFile modelDataFile;
  /** Contains the original location of the file */
  public URL fileUrl;
  
  public static DataFile readFile(URL fileUrl, URL definitionUrl, String dataFileName) throws DataFileException {
    ModelDataFileReader reader = ModelDataFileReader.getModelDataFileReader(definitionUrl);
    ModelDataFile modelDataFile = reader.getModelDataFile(dataFileName);
    if(modelDataFile == null) throw new IllegalArgumentException("Could not find file definition for data file named \"" + dataFileName + "\"");
    DataFile dataFile = new DataFile(modelDataFile, fileUrl);
    dataFile.loadDataFile();
    return dataFile;
  }
  
  protected DataFile(ModelDataFile modelDataFile, URL fileUrl) {
    this.modelDataFile = modelDataFile;
    this.fileUrl = fileUrl;
  }
  
  public void loadDataFile() throws DataFileException {
    if(modelDataFile == null) throw new IllegalStateException("DataFile model is null, cannot load file");
    if(fileUrl == null) throw new IllegalStateException("File URL is null, cannot load file");
    
    InputStream urlStream = null;
    try { urlStream = fileUrl.openStream(); }
    catch(IOException e) { throw new DataFileException("Error open URL: " + fileUrl.toString(), e); }
    Stack parentStack = new Stack();

    try {
      if(ModelDataFile.SEP_FIXED_RECORD.equals(modelDataFile.separatorStyle) || ModelDataFile.SEP_FIXED_LENGTH.equals(modelDataFile.separatorStyle)) {
        BufferedReader br = new BufferedReader(new InputStreamReader(urlStream));
        boolean isFixedRecord = ModelDataFile.SEP_FIXED_RECORD.equals(modelDataFile.separatorStyle);
        Debug.logInfo("[DataFile.loadDataFile] separatorStyle is " + modelDataFile.separatorStyle + ", isFixedRecord: " + isFixedRecord);
        
        int lineNum = 1;
        String line = null;
        if(isFixedRecord) {
          if(modelDataFile.recordLength <= 0) throw new DataFileException("Cannot read a fixed record length file if no record length is specified");

          try {
            char[] charData = new char[modelDataFile.recordLength + 1];
            //Debug.logInfo("[DataFile.loadDataFile] reading line " + lineNum + " from position " + (lineNum-1)*modelDataFile.recordLength + ", length is " + modelDataFile.recordLength);
            if(br.read(charData, 0, modelDataFile.recordLength) == -1) {
              line = null;
              //Debug.logInfo("[DataFile.loadDataFile] found end of file, got -1");
            }
            else {
              line = new String(charData);
              //Debug.logInfo("[DataFile.loadDataFile] read line " + lineNum + " line is: \"" + line + "\"");
            }
          }
          catch(IOException e) { throw new DataFileException("Error reading line #" + lineNum + " (index " + (lineNum-1)*modelDataFile.recordLength + " length " + modelDataFile.recordLength + ") from URL: " + fileUrl.toString(), e); }
        }
        else {
          try { line = br.readLine(); }
          catch(IOException e) { throw new DataFileException("Error reading line #" + lineNum + " from URL: " + fileUrl.toString(), e); }
        }

        while(line != null) {
          //first check to see if the file type has a line size, and if so if this line complies
          if(!isFixedRecord && modelDataFile.recordLength > 0 && line.length() != modelDataFile.recordLength) {
            throw new DataFileException("Line number " + lineNum + " was not the expected length; expected: " + modelDataFile.recordLength + ", got: " + line.length());
          }

          //find out which type of record it is - will throw an exception if not found
          ModelRecord modelRecord = findModelForLine(line, lineNum, modelDataFile);

          Record record = createRecord(line, lineNum, modelRecord);

          //if no parent pop all and put in dataFile records list
          if(modelRecord.parentRecord == null) {
            parentStack.clear();
            records.add(record);
          }
          //if parent equals top parent on stack, add to that parents child list, otherwise pop off parent and try again
          else {
            Record parentRecord = null;
            while(parentStack.size() > 0) {
              parentRecord = (Record)parentStack.peek();
              if(parentRecord.recordName.equals(modelRecord.parentName)) {
                break;
              }
              else {
                parentStack.pop();
                parentRecord = null;
              }
            }

            if(parentRecord == null) {
              throw new DataFileException("Expected Parent Record not found for line " + lineNum + "; record name of expected parent is " + modelRecord.parentName);
            }

            parentRecord.addChildRecord(record);
          }

          //if this record has children, put it on the parentStack
          if(modelRecord.childRecords.size() > 0) {
            parentStack.push(record);
          }

          lineNum++;
          if(isFixedRecord) {
            try {
              char[] charData = new char[modelDataFile.recordLength];
              //Debug.logInfo("[DataFile.loadDataFile] reading line " + lineNum + " from position " + (lineNum-1)*modelDataFile.recordLength + ", length is " + modelDataFile.recordLength);
              if(br.read(charData, 0, modelDataFile.recordLength) == -1) {
                line = null;
                //Debug.logInfo("[DataFile.loadDataFile] found end of file, got -1");
              }
              else {
                line = new String(charData);
                //Debug.logInfo("[DataFile.loadDataFile] read line " + lineNum + " line is: \"" + line + "\"");
              }
            }
            catch(IOException e) { throw new DataFileException("Error reading line #" + lineNum + " (index " + (lineNum-1)*modelDataFile.recordLength + " length " + modelDataFile.recordLength + ") from URL: " + fileUrl.toString(), e); }
          }
          else {
            try { line = br.readLine(); }
            catch(IOException e) { throw new DataFileException("Error reading line #" + lineNum + " from URL: " + fileUrl.toString(), e); }
          }
        }
      }
      else if(ModelDataFile.SEP_DELIMITED.equals(modelDataFile.separatorStyle)) {
        throw new DataFileException("Delimited files not yet supported");
      }
      else {
        throw new DataFileException("Separator style " + modelDataFile.separatorStyle + " not recognized.");
      }
    }
    finally {
      try { urlStream.close(); }
      catch(IOException e) { Debug.logWarning(e); }
    }
  }
  
  protected ModelRecord findModelForLine(String line, int lineNum, ModelDataFile modelDataFile) throws DataFileException {
    //Debug.logInfo("[DataFile.findModelForLine] line: " + line);
    ModelRecord modelRecord = null;
    for(int i=0; i<modelDataFile.records.size(); i++) {
      ModelRecord curModelRecord = (ModelRecord)modelDataFile.records.get(i);
      String typeCode = line.substring(curModelRecord.tcPosition, curModelRecord.tcPosition + curModelRecord.tcLength);
      if(typeCode != null && typeCode.equals(curModelRecord.typeCode)) {
        modelRecord = curModelRecord;
      }
    }
    if(modelRecord == null) throw new DataFileException("Could not find record definition for line " + lineNum + "; first bytes: " + line.substring(0, (line.length()>5)?5:line.length()));
    return modelRecord;
  }
  
  protected Record createRecord(String line, int lineNum, ModelRecord modelRecord) throws DataFileException {
    Record record = new Record(modelRecord);
    for(int i=0; i<modelRecord.fields.size(); i++) {
      ModelField modelField = (ModelField)modelRecord.fields.get(i);
      String strVal = line.substring(modelField.position, modelField.position + modelField.length);
      try { record.setString(modelField.name, strVal); }
      catch(java.text.ParseException e) { throw new DataFileException("Could not parse field " + modelField.name + ", format string \"" + modelField.format + "\" with value " + strVal + " on line " + lineNum, e); }
    }
    return record;
  }
}
