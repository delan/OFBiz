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
    public List records = null;
    /** Contains the definition for the file */
    public ModelDataFile modelDataFile;

    /** Creates a DataFile object which will contain the parsed objects for the specified datafile, using the specified definition.
     * @param fileUrl The URL where the data file is located
     * @param definitionUrl The location of the data file definition XML file
     * @param dataFileName The data file model name, as specified in the definition XML file
     * @throws DataFileException Exception thown for various errors, generally has a nested exception
     * @return A new DataFile object with the specified file pre-loaded
     */
    public static DataFile readFile(URL fileUrl, URL definitionUrl, String dataFileName) throws DataFileException {
        ModelDataFileReader reader = ModelDataFileReader.getModelDataFileReader(definitionUrl);
        if (reader == null)
            throw new IllegalArgumentException("Could not load definition file located at \"" + definitionUrl + "\"");
        ModelDataFile modelDataFile = reader.getModelDataFile(dataFileName);
        if (modelDataFile == null)
            throw new IllegalArgumentException("Could not find file definition for data file named \"" + dataFileName + "\"");
        DataFile dataFile = new DataFile(modelDataFile);
        dataFile.readDataFile(fileUrl);
        return dataFile;
    }

    /** Construct a DataFile object setting the model, does not load it
     * @param modelDataFile The model of the DataFile to instantiate
     */
    public DataFile(ModelDataFile modelDataFile) {
        this.modelDataFile = modelDataFile;
    }

    /** Loads (or reloads) the data file at the pre-specified location.
     * @param fileUrl The URL that the file will be loaded from
     * @throws DataFileException Exception thown for various errors, generally has a nested exception
     */
    public void readDataFile(URL fileUrl) throws DataFileException {
        if (fileUrl == null)
            throw new IllegalStateException("File URL is null, cannot load file");

        InputStream urlStream = null;
        try {
            urlStream = fileUrl.openStream();
        } catch (IOException e) {
            throw new DataFileException("Error open URL: " + fileUrl.toString(), e);
        }
        try {
            readDataFile(urlStream, fileUrl.toString());
        }
        finally { try {
                      urlStream.close();
              } catch (IOException e) {
                      Debug.logWarning(e);
                  }
                } }

    /** Populates (or reloads) the data file with the text of the given content
     * @param content The text data to populate the DataFile with
     * @throws DataFileException Exception thown for various errors, generally has a nested exception
     */
    public void readDataFile(String content) throws DataFileException {
        if (content == null || content.length() <= 0)
            throw new IllegalStateException("Content is empty, can't read file");

        ByteArrayInputStream bis = new ByteArrayInputStream(content.getBytes());
        readDataFile(bis, null);
    }

    /** Loads (or reloads) the data file from the given stream
     * @param dataFileStream A stream containing the text data for the data file
     * @param locationInfo Text information about where the data came from for exception messages
     * @throws DataFileException Exception thown for various errors, generally has a nested exception
     */
    public void readDataFile(InputStream dataFileStream, String locationInfo) throws DataFileException {
        if (modelDataFile == null)
            throw new IllegalStateException("DataFile model is null, cannot load file");
        if (locationInfo == null)
            locationInfo = "unknown";

        records = new Vector();
        Stack parentStack = new Stack();

        if (ModelDataFile.SEP_FIXED_RECORD.equals(modelDataFile.separatorStyle) || ModelDataFile.SEP_FIXED_LENGTH.equals(modelDataFile.separatorStyle)) {
            BufferedReader br = new BufferedReader(new InputStreamReader(dataFileStream));
            boolean isFixedRecord = ModelDataFile.SEP_FIXED_RECORD.equals(modelDataFile.separatorStyle);
            Debug.logInfo("[DataFile.readDataFile] separatorStyle is " + modelDataFile.separatorStyle + ", isFixedRecord: " + isFixedRecord);

            int lineNum = 1;
            String line = null;
            if (isFixedRecord) {
                if (modelDataFile.recordLength <= 0)
                    throw new DataFileException("Cannot read a fixed record length file if no record length is specified");

                try {
                    char[] charData = new char[modelDataFile.recordLength + 1];
                    //Debug.logInfo("[DataFile.readDataFile] reading line " + lineNum + " from position " + (lineNum-1)*modelDataFile.recordLength + ", length is " + modelDataFile.recordLength);
                    if (br.read(charData, 0, modelDataFile.recordLength) == -1) {
                        line = null;
                        //Debug.logInfo("[DataFile.readDataFile] found end of file, got -1");
                    } else {
                        line = new String(charData);
                        //Debug.logInfo("[DataFile.readDataFile] read line " + lineNum + " line is: \"" + line + "\"");
                    }
                } catch (IOException e) {
                    throw new DataFileException("Error reading line #" + lineNum + " (index " + (lineNum - 1) * modelDataFile.recordLength + " length " +
                            modelDataFile.recordLength + ") from location: " + locationInfo, e);
                }
            } else {
                try {
                    line = br.readLine();
                } catch (IOException e) {
                    throw new DataFileException("Error reading line #" + lineNum + " from location: " + locationInfo, e);
                }
            }

            while (line != null) {
                //first check to see if the file type has a line size, and if so if this line complies
                if (!isFixedRecord && modelDataFile.recordLength > 0 && line.length() != modelDataFile.recordLength) {
                    throw new DataFileException("Line number " + lineNum + " was not the expected length; expected: " + modelDataFile.recordLength + ", got: " +
                            line.length());
                }

                //find out which type of record it is - will throw an exception if not found
                ModelRecord modelRecord = findModelForLine(line, lineNum, modelDataFile);

                Record record = Record.createRecord(line, lineNum, modelRecord);

                //if no parent pop all and put in dataFile records list
                if (modelRecord.parentRecord == null) {
                    parentStack.clear();
                    records.add(record);
                }
                //if parent equals top parent on stack, add to that parents child list, otherwise pop off parent and try again
                else {
                    Record parentRecord = null;
                    while (parentStack.size() > 0) {
                        parentRecord = (Record) parentStack.peek();
                        if (parentRecord.recordName.equals(modelRecord.parentName)) {
                            break;
                        } else {
                            parentStack.pop();
                            parentRecord = null;
                        }
                    }

                    if (parentRecord == null) {
                        throw new DataFileException("Expected Parent Record not found for line " + lineNum + "; record name of expected parent is " +
                                modelRecord.parentName);
                    }

                    parentRecord.addChildRecord(record);
                }

                //if this record has children, put it on the parentStack
                if (modelRecord.childRecords.size() > 0) {
                    parentStack.push(record);
                }

                lineNum++;
                if (isFixedRecord) {
                    try {
                        char[] charData = new char[modelDataFile.recordLength];
                        //Debug.logInfo("[DataFile.readDataFile] reading line " + lineNum + " from position " + (lineNum-1)*modelDataFile.recordLength + ", length is " + modelDataFile.recordLength);
                        if (br.read(charData, 0, modelDataFile.recordLength) == -1) {
                            line = null;
                            //Debug.logInfo("[DataFile.readDataFile] found end of file, got -1");
                        } else {
                            line = new String(charData);
                            //Debug.logInfo("[DataFile.readDataFile] read line " + lineNum + " line is: \"" + line + "\"");
                        }
                    } catch (IOException e) {
                        throw new DataFileException("Error reading line #" + lineNum + " (index " + (lineNum - 1) * modelDataFile.recordLength + " length " +
                                modelDataFile.recordLength + ") from location: " + locationInfo, e);
                    }
                } else {
                    try {
                        line = br.readLine();
                    } catch (IOException e) {
                        throw new DataFileException("Error reading line #" + lineNum + " from location: " + locationInfo, e);
                    }
                }
            }
        } else if (ModelDataFile.SEP_DELIMITED.equals(modelDataFile.separatorStyle)) {
            throw new DataFileException("Delimited files not yet supported");
        } else {
            throw new DataFileException("Separator style " + modelDataFile.separatorStyle + " not recognized.");
        }
    }

    /** Writes the records in this DataFile object to a text data file
     * @param filename The filename to put the data into
     * @throws DataFileException Exception thown for various errors, generally has a nested exception
     */
    public void writeDataFile(String filename) throws DataFileException {
        File outFile = new File(filename);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outFile);
        } catch (FileNotFoundException e) {
            throw new DataFileException("Could not open file " + filename, e);
        }

        try {
            writeDataFile(fos);
        }
        finally { 
            try {
                if (fos != null)
                    fos.close();
              } catch (IOException e) {
                    throw new DataFileException("Could not close file " + filename + ", may not have written correctly;", e);
            }
        }
    }

    /** Returns the records in this DataFile object as a plain text data file content
     * @throws DataFileException Exception thown for various errors, generally has a nested exception
     * @return A String containing what would go into a data file as plain text
     */
    public String writeDataFile() throws DataFileException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        writeDataFile(bos);
        String outString = bos.toString();
        try {
            if (bos != null)
                bos.close();
        } catch (IOException e) {
            Debug.logWarning(e);
        }
        return outString;
    }

    /** Writes the records in this DataFile object to the given OutputStream
     * @param outStream The Stream to put the data into
     * @throws DataFileException Exception thown for various errors, generally has a nested exception
     */
    public void writeDataFile(OutputStream outStream) throws DataFileException {
        writeRecords(outStream, this.records);
    }

    protected void writeRecords(OutputStream outStream, List records) throws DataFileException {
        for (int r = 0; r < records.size(); r++) {
            Record record = (Record) records.get(r);
            String line = record.writeLineString(modelDataFile);
            try {
                outStream.write(line.getBytes());
            } catch (IOException e) {
                throw new DataFileException("Could not write to stream;", e);
            }

            if (record.getChildRecords() != null && record.getChildRecords().size() > 0) {
                writeRecords(outStream, record.getChildRecords());
            }
        }
    }

    /** Searches through the record models to find one with a matching type-code, if no type-code exists that model will always be used if it gets to it
     * @param line
     * @param lineNum
     * @param modelDataFile
     * @throws DataFileException Exception thown for various errors, generally has a nested exception
     * @return
     */
    protected ModelRecord findModelForLine(String line, int lineNum, ModelDataFile modelDataFile) throws DataFileException {
        //Debug.logInfo("[DataFile.findModelForLine] line: " + line);
        ModelRecord modelRecord = null;
        for (int i = 0; i < modelDataFile.records.size(); i++) {
            ModelRecord curModelRecord = (ModelRecord) modelDataFile.records.get(i);
            if (curModelRecord.tcPosition < 0) {
                modelRecord = curModelRecord;
                break;
            }

            String typeCode = line.substring(curModelRecord.tcPosition, curModelRecord.tcPosition + curModelRecord.tcLength);
            //try to match with a single typecode
            if (curModelRecord.typeCode.length() > 0) {
                if (typeCode != null && typeCode.equals(curModelRecord.typeCode)) {
                    modelRecord = curModelRecord;
                    break;
                }
            }
            //try to match a ranged typecode (tcMin <= typeCode <= tcMax)
            else if (curModelRecord.tcMin.length() > 0 || curModelRecord.tcMax.length() > 0) {
                if (curModelRecord.tcIsNum) {
                    long typeCodeNum = Long.parseLong(curModelRecord.typeCode);
                    if ((curModelRecord.tcMinNum < 0 || typeCodeNum >= curModelRecord.tcMinNum) &&
                        (curModelRecord.tcMaxNum < 0 || typeCodeNum <= curModelRecord.tcMaxNum)) {
                        modelRecord = curModelRecord;
                        break;
                    }
                } else {
                    if ((typeCode.compareTo(curModelRecord.tcMin) >= 0) &&
                        (typeCode.compareTo(curModelRecord.tcMax) <= 0)) {
                        modelRecord = curModelRecord;
                        break;
                    }
                }
            }
        }
        if (modelRecord == null)
            throw new DataFileException("Could not find record definition for line " + lineNum + "; first bytes: " +
                    line.substring(0, (line.length() > 5) ? 5 : line.length()));
        return modelRecord;
    }
}

