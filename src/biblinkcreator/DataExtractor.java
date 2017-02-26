/*
 * Copyright (C) 2017 David Nazarian
 *
 * This file is part of BibLinkCreator.
 *
 * BibLinkCreator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BibLinkCreator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BibLinkCreator.  If not, see <http://www.gnu.org/licenses/>.
 */
package biblinkcreator;

import biblinkcreator.Logger.*;
import biblinkcreator.Identifier.*;
import biblinkcreator.DataStructure.*;
import java.util.*;
import org.openrdf.model.*;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.query.*;
import org.openrdf.repository.*;
import org.openrdf.repository.http.HTTPRepository;

/**
 * A mechanism that extracts preprocesses and saves bibliographic data retrieved
 * from semantic repositories. <br><br>
 * Author: David Nazarian
 * @author David Nazarian
 */
public class DataExtractor {
    // Number of extracted records needed for a message to be logged during the
    // extraction.
    private int extractMessageRecords = 25000;
    // Number of inserted records needed for a message to be logged during the
    // insertion of the data into destination repository.
    private int insertMessageRecords = 5000;
    // The number of records to be batch inserted after extraction and
    // preprocessing.
    private int insertBatchSize = 1000;
    // Provides structure for the saved data.
    private Schema schema = null;
    private Logger logger = null;
    // Repository connection to the source repository providing the subjects of
    // the links.
    private RepositoryConnection sourceARepositoryConn = null;
    // Extra preprocessing information for the data extracted from the source
    // repository providing the subjects of the links.
    private StringFormatData sourceAStringFormatData = null;
    // Information about the source A repository.
    private RepositoryInfo sourceARepositoryInfo;
    // The ID of the source A repository set during its creation.
    private String sourceARepositoryID = "";
    // The name (alias) of the source A repository.
    private String sourceARepositoryName = "";
    // Repository connection to the destination repository where the extracted
    // and preprocessed data will be transferred to.
    private RepositoryConnection destRepositoryConn = null;
    // Information about the destination repository.
    private RepositoryInfo destRepositoryInfo;
    // The ID of the destination repository set during its creation.
    private String destRepositoryID = "";
    
    /**
     * @param sourceARepositoryInfo Information about the source repository
     * providing the subjects of the links.
     * @param destRepositoryInfo Information about the destination repository
     * where the extracted and preprocessed data will be transferred to.
     * @param sourceAStringFormatData Extra preprocessing information for the
     * identifiers extracted from the source repository providing the subject
     * of the links.
     * @param logger A logger that will log generated messages.
     */
    public DataExtractor(RepositoryInfo sourceARepositoryInfo,
            RepositoryInfo destRepositoryInfo,
            StringFormatData sourceAStringFormatData, Logger logger) {
        if (logger != null)
            this.logger = logger;
        else
            this.logger = new Logger(PrintType.None, "", false);
        // Logs the missing necessary repository parameters for the source A and
        // the destination.
        if (sourceRepositoryParameterMissing(
                sourceARepositoryInfo, "sourceARepositoryInfo") |
                destRepositoryParameterMissing(destRepositoryInfo))
            return;
        
        String sourceAServerURLString =
                sourceARepositoryInfo.getServerURLString();
        String destServerURLString = destRepositoryInfo.getServerURLString();
        Quadstore quadstore = new Quadstore();
        
        this.sourceAStringFormatData = sourceAStringFormatData;
        // The ID of the source A repository set during its creation.
        this.sourceARepositoryID = sourceARepositoryInfo.getRepositoryID();
        // The name (alias) of the source A repository.
        this.sourceARepositoryName = sourceARepositoryInfo.getRepositoryName();
        this.sourceARepositoryInfo = sourceARepositoryInfo;
        // The ID of the destination repository set during its creation.
        this.destRepositoryID = destRepositoryInfo.getRepositoryID();
        // The name (alias) of the destination repository.
        this.destRepositoryInfo = destRepositoryInfo;
        // Provides structure for the saved data.
        schema = new Schema(destRepositoryInfo.getSchemaURLString());
        // Checks for valid credentials for the source A repository server.
        if (quadstore.isConnectionAuthorized(sourceAServerURLString,
                sourceARepositoryInfo.getAuthenticationData(), logger) == false)
            return;
        // Checks for valid credentials for the destination repository server.
        if (quadstore.isConnectionAuthorized(destServerURLString,
                destRepositoryInfo.getAuthenticationData(), logger) == false)
            return;
        
        try {
            HTTPRepository sourceAHTTPRepository = new HTTPRepository(
                    sourceAServerURLString, sourceARepositoryID);
            quadstore.setAuthentication(
                    sourceARepositoryInfo.getAuthenticationData(),
                    sourceAHTTPRepository);
            sourceARepositoryConn = sourceAHTTPRepository.getConnection();
            
            HTTPRepository destHTTPRepository = new HTTPRepository(
                    destServerURLString, destRepositoryID);
            quadstore.setAuthentication(
                    destRepositoryInfo.getAuthenticationData(),
                    destHTTPRepository);
            destRepositoryConn = destHTTPRepository.getConnection();    
        }
        catch (RepositoryException ex) {
            this.logger.logMessage(ex.getMessage(), MessageCategory.Error);
        }
    }
    
    /**
     * @return The number of extracted records needed for a message to be logged
     * during the extraction.
     */
    public int getExtractMessageRecords() {
        return extractMessageRecords;
    }
    
    /**
     * @param extractMessageRecords The number of extracted records needed for
     * a message to be logged during the extraction.
     */
    public void setExtractMessageRecords(int extractMessageRecords) {
        this.extractMessageRecords = extractMessageRecords;
    }

    /**
     * @return The number of inserted records needed for a message to be logged
     * during the insertion of the data into the destination repository.
     */
    public int getInsertMessageRecords() {
        return insertMessageRecords;
    }
    
    /**
     * @param insertMessageRecords The number of inserted records needed for a
     * message to be logged during the insertion of the data into the 
     * destination repository.
     */
    public void setInsertMessageRecords(int insertMessageRecords) {
        this.insertMessageRecords = insertMessageRecords;
    }
    
    /**
     * @return The number of records to be batch inserted after extraction and
     * preprocessing.
     */
    public int getInsertBatchSize() {
        return insertBatchSize;
    }
    
    /**
     * @param insertBatchSize The number of records to be batch inserted after
     * extraction and preprocessing.
     */
    public void setInsertBatchSize(int insertBatchSize) {
        this.insertBatchSize = insertBatchSize;
    }
    
    /**
     * Closes the connection to the source repository providing the subjects
     * of the links and the destination repository where the extracted and
     * preprocessed data are saved.
     */
    public void close() {
        try {
            sourceARepositoryConn.close();
        }
        catch (RepositoryException ex) {
            logger.logMessage(ex.getMessage(), MessageCategory.Error);
        }
        
        try {
            destRepositoryConn.close();
        }
        catch (RepositoryException ex) {
            logger.logMessage(ex.getMessage(), MessageCategory.Error);
        }
    }
    
    /**
     * Extracts, preprocesses records from the two source repositories providing
     * data for the formation of the subjects and objects of the links,
     * and saves them into the destination repository.
     * @param sourceAExtractionQuery A SPARQL query which will extract the
     * records from the source repository providing the subjects of the links.
     * Its select clause must contain a variable for the subject, the
     * identifier, the title and optionally for the publication year (except
     * for the issn and the journalTitle identifiers for which it is mandatory).
     * The variable names are the following: <br>
     * Variable name for the subject: ?subject <br>
     * Variable name for the title: ?title <br>
     * Variable name for the publication year: ?year <br>
     * The variable names for the identifiers can be retrieved by using the 
     * getIdentifierVariableName method of the identifier class. <br>
     * <br>
     * An example of a select clause for record retrieval: <br>
     * <br>
     * SELECT<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;?subject<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;?isbn<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;?title<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;?year<br>
     * <br><br>
     * or a select everything can be used instead:<br>
     * <br>
     * SELECT *<br>
     * @param sourceBExtractionQuery A SPARQL query which will extract the
     * records from the source repository providing the objects of the links.
     * Its select clause is similar to the one described previously. The
     * variable subject will retrieve the URIs that will form the objects
     * of the links.
     * @param sourceBRepositoryInfo Information about the source repository
     * providing the objects of the links.
     * @param identifierType The type of the identifier to be used for the
     * extraction from the two sources.
     * @param saveNewDataOnly Excludes the identifiers, data for which have
     * been already saved once.
     * @param sourceBStringFormatData Extra preprocessing information for the
     * data extracted from the source repository providing the objects of the
     * links.
     * @return The number of saved records from the repository that providing
     * the objects of the links.
     */
    public int extractData(String sourceAExtractionQuery, 
            String sourceBExtractionQuery,
            RepositoryInfo sourceBRepositoryInfo,
            IdentifierType identifierType, boolean saveNewDataOnly,
            StringFormatData sourceBStringFormatData) {
        int recordCount = 0;
        // Logs the missing necessary repository parameters for the source A,
        // the source B and the destination.
        if (sourceRepositoryParameterMissing(sourceARepositoryInfo,
                "sourceARepositoryInfo") | sourceRepositoryParameterMissing(
                        sourceBRepositoryInfo, "sourceBRepositoryInfo") |
                destRepositoryParameterMissing(destRepositoryInfo))
            return recordCount;
        // Repository connection to the source repository providing the objects
        // of the links.
        RepositoryConnection sourceBRepositoryConn = null;
        
        try {
            String sourceAServerURLString =
                    sourceARepositoryInfo.getServerURLString();
            String destServerURLString =
                    destRepositoryInfo.getServerURLString();
            
            HTTPRepository sourceBHTTPRepository;
            String sourceBServerURLString =
                    sourceBRepositoryInfo.getServerURLString();
            // The ID of the source B repository set during its creation.
            String sourceBRepositoryID =
                    sourceBRepositoryInfo.getRepositoryID();
            // The name (alias) of the source B repository.
            String sourceBRepositoryName =
                    sourceBRepositoryInfo.getRepositoryName();
            Quadstore quadstore = new Quadstore();
            // Checks for valid credentials for the source A repository server.
            if (quadstore.isConnectionAuthorized(sourceAServerURLString,
                    sourceARepositoryInfo.getAuthenticationData(),
                    logger) == false)
                return recordCount;
            // Checks for valid credentials for the source B repository server.
            if (quadstore.isConnectionAuthorized(sourceBServerURLString,
                    sourceBRepositoryInfo.getAuthenticationData(),
                    logger) == false)
                return recordCount;
            // Checks for valid credentials for the destination repository
            // server.
            if (quadstore.isConnectionAuthorized(destServerURLString,
                    destRepositoryInfo.getAuthenticationData(),
                    logger) == false)
                return recordCount;
            
            try {
                sourceBHTTPRepository = new HTTPRepository(
                        sourceBServerURLString, sourceBRepositoryID);
                quadstore.setAuthentication(
                        sourceBRepositoryInfo.getAuthenticationData(),
                        sourceBHTTPRepository);
                sourceBRepositoryConn = sourceBHTTPRepository.getConnection();
            }
            catch (Exception ex) {
                logger.logMessage(ex.getMessage(), MessageCategory.Error);
                return recordCount;
            }
            
            Identifier identifier = new Identifier();
            // An identifier name for logging.
            String identifierName =
                    identifier.getIdentifierName(identifierType);
            String identifierVariableName =
                    identifier.getIdentifierVariableName(identifierType);
            // A set of identifiers data for which have been saved once.
            Set<String> existingIdentifierSet = null;
            
            if (saveNewDataOnly) {
                String graphURIString = schema.getDataGraphURIString(
                        sourceBRepositoryName);        
                String identifierRetrievalQuery =
                "PREFIX prop: <" + schema.getPropertyPath() + ">           \n" +
                "                                                          \n" +
                "SELECT                                                    \n" +
                "    *                                                     \n" +
                "WHERE                                                     \n" +
                "    {                                                     \n" +
                "        GRAPH <" + graphURIString + ">                    \n" +
                "            {?subject prop:" + identifierVariableName +
                              " ?" + identifierVariableName + " .}         \n" +
                "    }                                                     \n";

                logger.logMessage("Retrieving " + identifierName + " data of " +
                        sourceBRepositoryName + " from repository " +
                        destRepositoryID + "...", MessageCategory.Info);
                // Retrieves the identifier set data for which have been saved
                // once.
                existingIdentifierSet =
                        getIdentifierSet(identifierRetrievalQuery,
                        destRepositoryConn, identifierType, false, null);
                // The set will have a null value in case of an error in the 
                // getIdentifierSet method.
                if (existingIdentifierSet == null)
                    return recordCount;

                logger.logMessage("\tunique records found : " + String.valueOf(
                        existingIdentifierSet.size()), MessageCategory.Info);
            }
            
            logger.logMessage("Extracting " + identifierName +
                    " data from repository " + sourceARepositoryID + "...",
                    MessageCategory.Info);
            // Retrieves the preprocessed and validated identifier set (except
            // of those retrieved from the destination repository) from the
            // source A repository.
            ExtractionResult extractionResultA = getFormattedIdentifierSet(
                    sourceAExtractionQuery, sourceARepositoryConn, null,
                    existingIdentifierSet, identifierType,
                    sourceAStringFormatData);
            // The set will have a null value in case of an error in the 
            // getFormattedIdentifierSet method.
            if (extractionResultA == null ||
                    extractionResultA.getIdentifierSet().isEmpty())
                return recordCount;
            
            logger.logMessage("\ttotal unique records found : " +
                    String.valueOf(extractionResultA.getIdentifierSet().size()),
                    MessageCategory.Info);

            logger.logMessage("Extracting " + identifierName +
                    " data from repository " + sourceBRepositoryID + "...",
                    MessageCategory.Info);
            // Retrieves the preprocessed and validated identifier set (only
            // those that exist in source A) from the source B repository.
            ExtractionResult extractionResultB = getFormattedIdentifierSet(
                    sourceBExtractionQuery, sourceBRepositoryConn,
                    extractionResultA.getIdentifierSet(), null, identifierType,
                    sourceBStringFormatData);
            // The set will have a null value in case of an error in the
            // getFormattedIdentifierSet method.
            if (extractionResultB == null ||
                    extractionResultB.getIdentifierSet().isEmpty())
                return recordCount;

            logger.logMessage("\tmatching unique records found : " + String.
                    valueOf(extractionResultB.getIdentifierSet().size()),
                    MessageCategory.Info);
            Set<String> identifierSet = extractionResultB.getIdentifierSet();
            
            logger.logMessage("Saving records from repository " +
                    sourceARepositoryID + "...",MessageCategory.Info);
            // Extracts, preprocesses data retrieved from the source A
            // repository based on the provided identifier set, and saves them
            // into the destination repository.
            int recordCountA = insertExtractedData(identifierSet,
                    sourceAExtractionQuery, sourceARepositoryName,
                    sourceARepositoryConn, identifierType,
                    identifierVariableName, sourceAStringFormatData);
            logger.logMessage("\ttotal records saved : " + String.valueOf(
                    recordCountA), MessageCategory.Info);
            
            logger.logMessage("Saving records from repository " +
                    sourceBRepositoryID + "...", MessageCategory.Info);
            // Extracts, preprocesses data retrieved from the source B
            // repository based on the provided identifier set, and saves them
            // into the destination repository.
            int recordCountB = insertExtractedData(identifierSet,
                    sourceBExtractionQuery, sourceBRepositoryName,
                    sourceBRepositoryConn, identifierType,
                    identifierVariableName, sourceBStringFormatData);
            logger.logMessage("\ttotal records saved : " + String.valueOf(
                    recordCountB), MessageCategory.Info);
            // The number of successfully saved records from the source B
            // repository.
            recordCount = recordCountB;
        }
        finally {
            try {
                if (sourceBRepositoryConn != null)
                    sourceBRepositoryConn.close();
            }
            catch (Exception ex) {
            }
        }
        
        return recordCount;
    }
    
    /**
     * Extracts, preprocesses records from the source repository and inserts
     * them into the destination repository. A record will be omitted from the
     * insertion if its identifier is not found in a provided identifier set.
     * Each record is comprised of a subject, an identifier, a title and a year
     * of publication.
     * @param identifierSet The set of identifiers necessary for the insertion.
     * @param extractionQuery A SPARQL query which will extract the records
     * from the source repository. Its select clause must contain a variable for
     * the subject, the identifier, the title and optionally for the publication
     * year (except for the issn and the journalTitle identifiers for which it
     * is mandatory). The variable names are the following: <br>
     * Variable name for the subject: ?subject <br>
     * Variable name for the title: ?title <br>
     * Variable name for the publication year: ?year <br>
     * The variable names for the identifiers can be retrieved by using the 
     * getIdentifierVariableName method of the identifier class. <br>
     * <br>
     * An example of a select clause for record retrieval: <br>
     * <br>
     * SELECT<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;?subject<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;?isbn<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;?title<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;?year<br>
     * <br><br>
     * or a select everything can be used instead:<br>
     * <br>
     * SELECT *<br>
     * @param repositoryName The name (alias) of the source repository from 
     * where the records will be extracted, preprocessed and saved.
     * @param sourceRepositoryConn A repository connection to the source
     * repository from where the records will be extracted, preprocessed and
     * saved.
     * @param identifierType The type of the identifier to be used for the
     * extraction.
     * @param identifierVariableName The variable name for the identifier.
     * @param stringFormatData (Optional) Extra preprocessing information for
     * the variable of the identifier, the title and the publication year
     * contained in each record extracted from the source repository.
     * @return The count of the inserted records into the destination
     * repository.
     */
    private int insertExtractedData(Set<String> identifierSet,
            String extractionQuery, String repositoryName,
            RepositoryConnection sourceRepositoryConn,
            IdentifierType identifierType, String identifierVariableName,
            StringFormatData stringFormatData) {
        int insertCount = 0;
        StringOperation stringOperation = new StringOperation();
        BindingSet bindingSet;
        TupleQueryResult queryResult = null;
        int invalidTitleCount = 0;
        int invalidYearCount = 0;
        int logCount = 0;
        Identifier identifier = new Identifier();
        // An identifier name for logging.
        String identifierName = identifier.getIdentifierName(identifierType);
        String subjectString, identifierString, titleString, yearString;
        StringFormatResult identifierResult, titleResult, yearResult;
        Set<ExtractedData> extractedDataSet = new HashSet<>();
        
        try {
            // Retrieves data from the source repository.
            queryResult = sourceRepositoryConn.prepareTupleQuery(
                    QueryLanguage.SPARQL, extractionQuery).evaluate();
            
            while (queryResult.hasNext()) {
                bindingSet = queryResult.next();
                // Reads the identifier string.
                identifierString = bindingSet.getBinding(
                        identifierVariableName).getValue().stringValue();
                // Formats the identifier string.
                identifierResult = stringOperation.formatIdentifier(
                        identifierString, identifierType, stringFormatData);
                // If the retrieved identifier is invalid or not contained in
                // the identifier set, then its record will be omitted from 
                // the insertion.
                if (!identifierResult.getIsValid() ||
                        !identifierSet.contains(identifierResult.getValue()))
                    continue;
                // Reads the subject string.
                subjectString = bindingSet.getBinding("subject").getValue().
                        stringValue();
                // Reads the title string.
                titleString = bindingSet.getBinding("title").getValue().
                        stringValue();
                // Since the publication year variable is optional in some cases
                // it must be checked first for existance.
                if (bindingSet.getBinding("year") != null)
                    // Reads the publication year string.
                    yearString = bindingSet.getBinding("year").getValue().
                            stringValue();
                else
                    yearString = "";
                
                if (stringFormatData != null) {
                    // Extra preprocessing for the title.
                    titleResult = stringOperation.formatTitle(titleString,
                            stringFormatData.getTitleReplacementDataList());
                    // Extra preprocessing for the publication year.
                    yearResult = stringOperation.formatYear(yearString,
                            stringFormatData.getYearReplacementDataList());
                }
                else {
                    // Default preprocessing for the title.
                    titleResult =
                            stringOperation.formatTitle(titleString, null);
                    // Default preprocessing for the publication year.
                    yearResult = stringOperation.formatYear(yearString, null);
                }
                
                if (!yearResult.getIsValid())
                    invalidYearCount++;
                
                if (!titleResult.getIsValid())
                    invalidTitleCount++;
                else {
                    // Adds a record.
                    extractedDataSet.add(new ExtractedData(subjectString,
                        identifierResult.getValue(), titleResult.getValue(),
                        yearResult.getValue()));
                    // The collected records will be sent to the repository as a
                    // batch.
                    if (extractedDataSet.size() >= insertBatchSize) {
                        insertCount += executeTransaction(extractedDataSet,
                                identifierVariableName, repositoryName,
                                destRepositoryConn);
                        extractedDataSet.clear();
                        
                        logCount = logInsertMessage(logCount, insertCount,
                                identifierName);
                    }
                }
            }
            // The remaining records will be sent to the repository.
            if (extractedDataSet.size() > 0) {
                insertCount += executeTransaction(extractedDataSet, 
                        identifierVariableName, repositoryName,
                        destRepositoryConn);
                extractedDataSet.clear();
            }
            
            if (invalidTitleCount > 0)
                logger.logMessage("\tinvalid title count : " + String.valueOf(
                        invalidTitleCount), MessageCategory.Info);
            if (invalidYearCount > 0)
                logger.logMessage("\tinvalid year count : " + String.valueOf(
                        invalidYearCount), MessageCategory.Info);
        }
        catch (Exception ex) {
            logger.logMessage(ex.getMessage(), MessageCategory.Error);
        }
        finally {
            if (queryResult != null) {
                try {
                    queryResult.close();
                }
                catch (Exception ex) {
                }
            }
        }
        
        return insertCount;
    }
    
    /**
     * Sends the extracted and preprocessed data batch into the destination
     * repository.
     * @param extractedDataSet A set containing the extracted and preprocessed
     * data batch.
     * @param identifierVariableName The variable name for the identifier.
     * @param repositoryName The name (alias) of the source repository from 
     * where the data is being saved.
     * @param destRepositoryConn A connection to the destination repository
     * where the extracted and preprocessed data will be sent.
     * @param schema Provides structure for the saved data.
     * @return The count of the saved records.
     */
    private int executeTransaction(Set<ExtractedData> extractedDataSet,
            String identifierVariableName, String repositoryName,
            RepositoryConnection destRepositoryConn)
            throws RepositoryException {
        int recordCount = 0;
        
        try {
            // The named graph URI string where the data will be saved.
            String graphURIString =
                    schema.getDataGraphURIString(repositoryName);
            // A predicate URI string for the identifier.
            String identifierURIString =
                    schema.getPropertyPath() + identifierVariableName;
            String subjectString, identifierString, titleString, yearString;
            URI graphURI, subjectURI, predicateURI;
            Literal objectLiteral;
            ValueFactory valueFactory = destRepositoryConn.getValueFactory();
            // Creation of the named graph URI.
            graphURI = valueFactory.createURI(graphURIString);
            Iterator insertSetIterator = extractedDataSet.iterator();
            ExtractedData extractedData;
            // Begins a transation.
            destRepositoryConn.begin();
            
            while (insertSetIterator.hasNext()) {
                extractedData = (ExtractedData) insertSetIterator.next();
                // Reads the extracted information.
                subjectString = extractedData.getSubject();
                identifierString = extractedData.getIdentifier();
                titleString = extractedData.getTitle();
                yearString = extractedData.getYear();
                // Creates the subject URI.
                subjectURI = valueFactory.createURI(subjectString);
                
                predicateURI = valueFactory.createURI(identifierURIString);
                objectLiteral = valueFactory.createLiteral(identifierString,
                        XMLSchema.STRING);
                // Adds the quad for the identifier.
                destRepositoryConn.add(subjectURI, predicateURI, objectLiteral,
                        graphURI);
                
                predicateURI =
                        valueFactory.createURI(schema.getTitlePath());
                objectLiteral = valueFactory.createLiteral(titleString,
                        XMLSchema.STRING);
                // Adds the quad for the title.
                destRepositoryConn.add(subjectURI, predicateURI, objectLiteral,
                        graphURI);
                
                if (yearString.length() > 0) {
                    predicateURI =
                            valueFactory.createURI(schema.getYearPath());
                    objectLiteral = valueFactory.createLiteral(yearString,
                            XMLSchema.GYEAR);
                    // Adds the quad for the publication year.
                    destRepositoryConn.add(subjectURI, predicateURI,
                            objectLiteral, graphURI);
                }
            }
            
            recordCount = extractedDataSet.size();
        }
        finally {
            try {
                // Commits the transation.
                destRepositoryConn.commit();
            }
            catch (Exception ex) {
            }
        }
        
        return recordCount;
    }
    
    /**
     * Logs a message during the insertion of the records into the extraction
     * destination repository depending on the value of the insertMessageRecords
     * variable.
     * @param logCount The number of times a message has been logged.
     * @param insertCount The number of records that have been inserted.
     * @param identifierName The name of the identifier to be logged.
     * @return The number of times a message has been logged.
     */
    private int logInsertMessage(int logCount, int insertCount,
            String identifierName) {
        if (logCount != insertCount / insertMessageRecords) {
            logCount = insertCount / insertMessageRecords;
            int inserts = logCount * insertMessageRecords;
            // A suffix for the word "record".
            String recordSuffix = (inserts == 1) ? "" : "s";
            logger.logMessage("\tsaved " + String.valueOf(inserts) + " " +
                    identifierName + " record" + recordSuffix,
                    MessageCategory.Info);
        }
        
        return logCount;
    }
    
    /**
     * Gets an identifier set by extracting data from the source repository.
     * The identifiers of the set can either be in a raw form (as retrieved from
     * the repository) or formatted.
     * @param extractionQuery A SPARQL query which will extract the identifiers
     * from the source repository. Its select clause must contain a variable
     * with the name of the identifier, e.g. ?isbn. The variable names for the
     * identifiers can be retrieved by using the getIdentifierVariableName
     * method of the Identifier class.
     * @param sourceRepositoryConn A repository connection to the source
     * repository from where the identifiers will be extracted.
     * @param identifierType The type of the identifier to be extracted.
     * @param formatIdentifier A value indicating if the identifiers will be 
     * formatted after the retrieval from the repository or not.
     * @param stringFormatData (Optional) Extra preprocessing information for
     * the identifiers extracted from the source repository.
     * @return An identifier set.
     */ 
    public Set<String> getIdentifierSet(String extractionQuery,
            RepositoryConnection sourceRepositoryConn,
            IdentifierType identifierType, boolean formatIdentifier,
            StringFormatData stringFormatData) {
        Set<String> identifierSet = null;
        TupleQueryResult queryResult = null;
        BindingSet bindingSet;
        String identifierString;
        StringOperation stringOperation = new StringOperation();
        StringFormatResult identifierResult;
        int extractedCount = 0;    
        Identifier identifier = new Identifier();
        String identifierName = identifier.getIdentifierName(identifierType);
        String identifierVariableName = identifier.
                getIdentifierVariableName(identifierType);
        
        try {
            // Retrieves data from the source repository.
            queryResult = sourceRepositoryConn.prepareTupleQuery(
                    QueryLanguage.SPARQL, extractionQuery).evaluate();
            // Initializes the identifier set that will be returned. In case of
            // an error during the retrieval from the repository, a null value
            // must be returned.
            identifierSet = new HashSet<>();
            
            while (queryResult.hasNext()) {
                bindingSet = queryResult.next();
                // Reads the identifier string.
                identifierString = bindingSet.getBinding(
                        identifierVariableName).getValue().stringValue();
                // Checks if the identifier must be formatted.
                if (formatIdentifier) {
                    // Formats the identifier string.
                    identifierResult = stringOperation.formatIdentifier(
                            identifierString, identifierType, stringFormatData);
                    // Only valid identifiers can be used after formatting.
                    if (identifierResult.getIsValid())
                        identifierSet.add(identifierResult.getValue());
                }
                else
                    identifierSet.add(identifierString);
                
                extractedCount++;
                
                if (extractedCount % extractMessageRecords == 0)
                    logExtractMessage(extractedCount, identifierName);                
            }
            
            if (extractedCount > 0 &&
                    extractedCount % extractMessageRecords != 0)
                logExtractMessage(extractedCount, identifierName);
        }
        catch (Exception ex) {
            logger.logMessage(ex.getMessage(), MessageCategory.Error);
        }
        finally {
            if (queryResult != null) {
                try {
                    queryResult.close();
                }
                catch (Exception ex) {
                }
            }
        }
        
        return identifierSet;
    }
     
    /**
     * Gets an extraction result which contains a formatted identifier set and 
     * a count for the invalid identifiers encountered during the extraction
     * of data from the source repository.
     * The formatted identifier set can be narrowed by an exclusion set and be
     * controlled by an inclusion set regarding its content.
     * @param extractionQuery A SPARQL query which will extract the identifiers
     * from the source repository. Its select clause must contain a variable
     * with the name of the identifier, e.g. ?isbn. The variable names for the
     * identifiers can be retrieved by using the getIdentifierVariableName
     * method of the Identifier class.
     * @param sourceRepositoryConn A repository connection to the source
     * repository from where the identifiers will be extracted and preprocessed.
     * @param inclusionSet (Optional) A set of identifiers which will control
     * the content of the result set. Only identifiers retrieved by the
     * extractionQuery that exist in this set can be added to the result set.
     * @param exclusionSet (Optional) A set of identifiers which will narrow the
     * content of the result set. The identifiers retrieved by the
     * extractionQuery that exist in this set will be omitted from the result
     * set.
     * @param identifierType The type of the identifier to be extracted.
     * @param stringFormatData (Optional) Extra preprocessing information for
     * the identifiers extracted from the source repository.
     * @return An extraction result which contains a formatted identifier set
     * and a count for the invalid identifiers encountered.
     */ 
    public ExtractionResult getFormattedIdentifierSet(String extractionQuery,
            RepositoryConnection sourceRepositoryConn, Set<String> inclusionSet,
            Set<String> exclusionSet, IdentifierType identifierType,
            StringFormatData stringFormatData) {
        ExtractionResult extractionResult = null;
        Set<String> identifierSet = new HashSet<>();
        int invalidIdentifierCount = 0;
        BindingSet bindingSet;
        TupleQueryResult queryResult = null;
        String identifierString;
        StringOperation stringOperation = new StringOperation();
        StringFormatResult identifierResult;
        // Indicates if data have been retrieved from the source repository.
        boolean dataExist = false;
        int extractedCount = 0;
        Identifier identifier = new Identifier();
        String identifierName = identifier.getIdentifierName(identifierType);
        String identifierVariableName = identifier.getIdentifierVariableName(
                identifierType);
        
        try {
            // Retrieves data from the source repository.
            queryResult = sourceRepositoryConn.prepareTupleQuery(
                    QueryLanguage.SPARQL, extractionQuery).evaluate();
            
            while (queryResult.hasNext()) {
                bindingSet = queryResult.next();
                // Data have been retrieved from the source repository.
                dataExist = true;
                extractedCount++;
                
                if (extractedCount % extractMessageRecords == 0)
                    logExtractMessage(extractedCount, identifierName);
                // Reads the identifier string.
                identifierString = bindingSet.getBinding(
                        identifierVariableName).getValue().stringValue();
                // Formats the identifier string.
                identifierResult = stringOperation.formatIdentifier(
                        identifierString, identifierType, stringFormatData);
                // If an inclusion set is provided and it doesn't contain the
                // formatted identifier, then the formatted identifier will not
                // be included in the result set.
                if (inclusionSet != null && !inclusionSet.contains(
                        identifierResult.getValue()))
                    continue;
                // If an exclusion set is provided and it contains the formatted
                // identifier, then the formatted identifier will not be
                // included in the result set.
                if (exclusionSet != null && exclusionSet.contains(
                        identifierResult.getValue()))
                    continue;
                // Only valid identifiers can be used after formatting
                if (identifierResult.getIsValid()) {
                    identifierSet.add(identifierResult.getValue());
                    // If an inclusion set is provided and all of its
                    // identifiers exist in the result set, then no more data
                    // will be needed from the source repository.
                    if (inclusionSet != null && inclusionSet.size() > 0 &&
                            inclusionSet.size() == identifierSet.size())
                        break;
                }
                else
                    invalidIdentifierCount++;
            }
            // Creates the result to be returned.
            extractionResult = new ExtractionResult(identifierSet,
                    invalidIdentifierCount);
            
            if (extractedCount > 0 && 
                    extractedCount % extractMessageRecords != 0)
                logExtractMessage(extractedCount, identifierName);
            
            if (invalidIdentifierCount > 0)
                logger.logMessage("\tinvalid " + identifierName + " count : " +
                        String.valueOf(invalidIdentifierCount),
                        MessageCategory.Info);
            
            if (extractionResult.getIdentifierSet().isEmpty()) {
                if (dataExist)
                    logger.logMessage("\tno new " + identifierName +
                            " records found", MessageCategory.Info);
                else
                    logger.logMessage("\tno " + identifierName +
                            " records found", MessageCategory.Info);
            }
        }
        catch (Exception ex) {
            logger.logMessage(ex.getMessage(), MessageCategory.Error);
        }
        finally {
            if (queryResult != null) {
                try {
                    queryResult.close();
                }
                catch (Exception ex) {
                }
            }
        }
        
        return extractionResult;
    }
    
    /**
     * Logs the missing necessary source repository parameters.
     * @param sourceRepositoryInfo Information about the source repository
     * providing data to be used for the formation of either the subjects or
     * the objects of the links.
     * @param sourceRepositoryVariableName The name used for the variable
     * sourceRepositoryInfo.
     * @return A value indicating if a parameter is missing.
     */
    private boolean sourceRepositoryParameterMissing(
            RepositoryInfo sourceRepositoryInfo,
            String sourceRepositoryVariableName) {
        boolean parameterMissing = false;
        
        if (sourceRepositoryInfo == null) {
            this.logger.logMessage(sourceRepositoryVariableName + " is null",
                    MessageCategory.Warning);
            parameterMissing = true;
            return parameterMissing;
        }
        
        if (sourceRepositoryInfo.getRepositoryID().length() == 0) {
            this.logger.logMessage(sourceRepositoryVariableName +
                    " repositoryID not set", MessageCategory.Warning);
            parameterMissing = true;
        }
        
        if (sourceRepositoryInfo.getRepositoryName().length() == 0) {
            this.logger.logMessage(sourceRepositoryVariableName + 
                    "repositoryName not set", MessageCategory.Warning);
            parameterMissing = true;
        }
        
        if (sourceRepositoryInfo.getServerURLString().length() == 0) {
            this.logger.logMessage(sourceRepositoryVariableName +
                    " serverURLString not set", MessageCategory.Warning);
            parameterMissing = true;
        }
        
        if (sourceRepositoryInfo.getAuthenticationData() != null) {
            AuthenticationData authenticationData =
                    sourceRepositoryInfo.getAuthenticationData();
            
            if (authenticationData.getUsername().length() == 0) {
                this.logger.logMessage(sourceRepositoryVariableName +
                        " authenticationData username not set",
                        MessageCategory.Warning);
                parameterMissing = true;
            }
            
            if (authenticationData.getPassword().length() == 0) {
                this.logger.logMessage(sourceRepositoryVariableName +
                        " authenticationData password not set",
                        MessageCategory.Warning);
                parameterMissing = true;
            }
        }
        
        return parameterMissing;
    }
    
    /**
     * Logs the missing necessary destination repository parameters.
     * @return A value indicating if a parameter is missing.
     */
    private boolean destRepositoryParameterMissing(
            RepositoryInfo destRepositoryInfo) {
        boolean parameterMissing = false;
        
        if (destRepositoryInfo == null) {
            this.logger.logMessage("destRepositoryInfo is null",
                    MessageCategory.Warning);
            parameterMissing = true;
            return parameterMissing;
        }
        
        if (destRepositoryInfo.getRepositoryID().length() == 0) {
            this.logger.logMessage("destRepositoryInfo repositoryID not set",
                    MessageCategory.Warning);
            parameterMissing = true;
        }
        
        if (destRepositoryInfo.getServerURLString().length() == 0) {
            this.logger.logMessage("destRepositoryInfo serverURLString not set",
                    MessageCategory.Warning);
            parameterMissing = true;
        }
        
        if (destRepositoryInfo.getSchemaURLString().length() == 0) {
            this.logger.logMessage("destRepositoryInfo schemaURIString not set",
                    MessageCategory.Warning);
            parameterMissing = true;
        }
        
        if (destRepositoryInfo.getAuthenticationData() != null) {
            AuthenticationData authenticationData =
                    destRepositoryInfo.getAuthenticationData();
            
            if (authenticationData.getUsername().length() == 0) {
                this.logger.logMessage("destRepositoryInfo " + 
                        "authenticationData username not set",
                        MessageCategory.Warning);
                parameterMissing = true;
            }
            
            if (authenticationData.getPassword().length() == 0) {
                this.logger.logMessage("destRepositoryInfo " + 
                        "authenticationData password not set",
                        MessageCategory.Warning);
                parameterMissing = true;
            }
        }
        
        return parameterMissing;
    }
    
    /**
     * Logs a message during the extraction.
     * @param extractedCount The number of records that have been extracted.
     * @param identifierName The name of the identifier to be logged.
     */
    private void logExtractMessage(int extractedCount, String identifierName) {
        // A suffix for the word "record".
        String recordSuffix = (extractedCount == 1) ? "" : "s";
        logger.logMessage("\textracted " + String.valueOf(extractedCount) +
                " " + identifierName + " record" + recordSuffix,
                MessageCategory.Info);
    }
}