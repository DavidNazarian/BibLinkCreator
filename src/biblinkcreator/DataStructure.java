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

import biblinkcreator.StringOperation.*;
import biblinkcreator.StringSimilarity.*;
import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;
import org.apache.commons.io.FileUtils;
import org.openrdf.model.URI;

/**
 * A class containing the static classes of the project.
 * <br><br>
 * Author: David Nazarian
 * @author David Nazarian
 */
public class DataStructure {
    /**
     * A static class which can be used to provide parameters for a repository
     * connection.
     */
    public static class RepositoryInfo {       
        private String serverURLString = "";
        private String repositoryID = "";
        private String repositoryName = "";
        private String schemaURLString = "";
        private AuthenticationData authenticationData;
        
        /**
         * @param serverURLString The URL of the server containing the
         * repository.
         * @param repositoryID The ID of the repository set during its creation.
         * @param repositoryName The name (alias) of the repository.
         * @param schemaURLString The base URL of the schema.
         * @param authenticationData The authentication data for the repository
         * connection.
         */
        public RepositoryInfo(String serverURLString, String repositoryID,
                String repositoryName, String schemaURLString,
                AuthenticationData authenticationData) {
            
            if (serverURLString != null)
                this.serverURLString = serverURLString;
            
            if (repositoryID != null)
                this.repositoryID = repositoryID;
            
            if (repositoryName != null)
                this.repositoryName = repositoryName;
            
            if (schemaURLString != null)
                this.schemaURLString = schemaURLString;
            
            this.authenticationData = authenticationData;
        }
        
        /**
         * @return The URL of the server containing the repository.
         */
        public String getServerURLString() {
            return serverURLString;
        }
        
        /**
         * @param serverURLString The URL of the server containing the
         * repository.
         */
        public void setServerURLString(String serverURLString) {
            if (serverURLString != null)
                this.serverURLString = serverURLString;
        }
        
        /**
         * @return The ID of the repository set during its creation.
         */
        public String getRepositoryID() {
            return repositoryID;
        }
        
        /**
         * @param repositoryID The ID of the repository set during its creation.
         */
        public void setRepositoryID(String repositoryID) {
            if (repositoryID != null)
                this.repositoryID = repositoryID;
        }
        
        /**
         * @return The name (alias) of the repository.
         */
        public String getRepositoryName() {
            return repositoryName;
        }
        
        /**
         * @param repositoryName The name (alias) of the repository.
         */
        public void setRepositoryName(String repositoryName) {
            if (repositoryName != null)
                this.repositoryName = repositoryName;
        }
        
        /**
         * @return The base URL of the schema.
         */
        public String getSchemaURLString() {
            return schemaURLString;
        }
        
        /**
         * @param schemaURLString The base URL of the schema.
         */
        public void setSchemaURLString(String schemaURLString) {
            if (schemaURLString != null)
                this.schemaURLString = schemaURLString;
        }
        
        /**
         * @return The authentication data for the repository connection.
         */
        public AuthenticationData getAuthenticationData() {
            return authenticationData;
        }
        
        /**
         * @param authenticationData The authentication data for the repository
         * connection.
         */
        public void setAuthenticationData(AuthenticationData
                authenticationData) {
            this.authenticationData = authenticationData;
        }
    }
    
    /**
     * A static class which can be used for the result of a string formatting.
     */
    public static class StringFormatResult {
        private boolean isValid = false;
        private String value = "";
        
        /**
         * @return A value indicating the validity of the string format result.
         */
        public boolean getIsValid() {
            return isValid;
        }
        
        /**
         * @param isValid A value indicating the validity of the string format
         * result.
         */
        public void setIsValid(boolean isValid) {
            this.isValid = isValid;
        }
        
        /**
         * @return The string format result value.
         */
        public String getValue() {
            return value;
        }
        
        /**
         * @param value The string format result value.
         */
        public void setValue(String value) {
            this.value = value;
        }
    }
    
    /**
     * A static class which can be used for extracted data from a repository.
     */
    public static class ExtractedData {
        private String subject = "";
        private String identifier = "";
        private String title = "";
        private String year = "";
        
        /**
         * @param subject The extracted subject.
         * @param identifier The extracted identifier.
         * @param title The extracted title.
         * @param year The extracted year.
         */
        public ExtractedData(String subject, String identifier, String title,
                String year) {
            this.subject = subject;
            this.identifier = identifier;
            this.title = title;
            this.year = year;
        }
        
        /**
         * @return The extracted subject.
         */
        public String getSubject() {
            return subject;
        }
        
        /**
         * @param subject The extracted subject.
         */
        public void setSubject(String subject) {
            this.subject = subject;
        }
        
        /**
         * @return The extracted identifier.
         */
        public String getIdentifier() {
            return identifier;
        }
        
        /**
         * @param identifier The extracted identifier.
         */
        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }
        
        /**
         * @return The extracted title.
         */
        public String getTitle() {
            return title;
        }
        
        /**
         * @param title The extracted title.
         */
        public void setTitle(String title) {
            this.title = title;
        }
        
        /**
         * @return The extracted year.
         */
        public String getYear() {
            return year;
        }
        
        /**
         * @param year The extracted year.
         */
        public void setYear(String year) {
            this.year = year;
        }
        
        /**
         * Calculates the hash code of the class.
         * @return The hash code of the class.
         */
        @Override
        public int hashCode() {
            String combinedString = subject + "_" + identifier + "_" +
                    title + "_" + year;
            return combinedString.hashCode();
        }
        
        /**
         * Object comparison mechanism.
         * @return A value indicating the equality of objects.
         */
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ExtractedData))
                return false;
            if (obj == this)
                return true;
            
            ExtractedData IdentifierData = (ExtractedData) obj;
            
            return IdentifierData.subject.equals(subject) &&
                   IdentifierData.identifier.equals(identifier) &&
                   IdentifierData.title.equals(title) &&
                   IdentifierData.year.equals(year);
        }
    }
    
    /**
     * A static class which can be used for a repository extraction result.
     */
    public static class ExtractionResult {
        private Set<String> identifierSet;
        private int invalidIdentifierCount;
        
        /**
         * @param identifierSet The set of extracted identifiers.
         * @param invalidIdentifierCount The number of invalid identifiers
         * encountered during the extraction.
         */
        public ExtractionResult(Set<String> identifierSet,
                int invalidIdentifierCount) {
            this.identifierSet = identifierSet;
            this.invalidIdentifierCount = invalidIdentifierCount;
        }
        
        /**
         * @return The set of extracted identifiers.
         */
        public Set<String> getIdentifierSet() {
            return identifierSet;
        }
        
        /**
         * @param identifierSet The set of extracted identifiers.
         */
        public void setIdentifierSet(Set<String> identifierSet) {
            this.identifierSet = identifierSet;
        }
        
        /**
         * @return The number of invalid identifiers encountered during the
         * extraction.
         */
        public int getInvalidIdentifierCount() {
            return invalidIdentifierCount;
        }
        
        /**
         * @param invalidIdentifierCount The number of invalid identifiers
         * encountered during the extraction.
         */
        public void setInvalidIdentifierCount(int invalidIdentifierCount) {
            this.invalidIdentifierCount = invalidIdentifierCount;
        }
    }
    
    /**
     * A static class which can be used for created links.
     */
    public static class LinkData {
        private URI subjectURI = null;
        private URI objectURI = null;
        
        /**
         * @param subjectURI The subject URI of a link.
         * @param objectURI The object URI of a link.
         */
        public LinkData(URI subjectURI, URI objectURI) {
            this.subjectURI = subjectURI;
            this.objectURI = objectURI;
        }
        
        /**
         * @return The subject URI of a link.
         */
        public URI getSubjectURI() {
            return subjectURI;
        }
        
        /**
         * @param subjectURI The subject URI of a link.
         */
        public void setSubjectURI(URI subjectURI) {
            this.subjectURI = subjectURI;
        }
        
        /**
         * @return The object URI of a link.
         */
        public URI getObjectURI() {
            return objectURI;
        }
        
        /**
         * @param objectURI The object URI of a link.
         */
        public void setObjectURI(URI objectURI) {
            this.objectURI = objectURI;
        }
        
        /**
         * Calculates the hash code of the class.
         * @return The hash code of the class.
         */
        @Override
        public int hashCode() {
            String combinedString = subjectURI.toString() + "_" +
                    objectURI.toString();
            return combinedString.hashCode();
        }
        
        /**
         * Object comparison mechanism.
         * @return A value indicating the equality of objects.
         */
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof LinkData))
                return false;
            if (obj == this)
                return true;
            
            LinkData linkData = (LinkData) obj;
            
            return linkData.subjectURI.toString().equals(subjectURI.toString())
                && linkData.objectURI.toString().equals(objectURI.toString());
        }
    }

    /**
     * A static class which can be used to select a string similarity measure
     * and provide its parameters.
     */
    public static class StringSimilaritySelector {
        private SimilarityType similarityType;
        private ShingleType shingleType;
        private int shingleSize;
        
        /**
         * @param similarityType The string similarity measure.
         * @param shingleType The type of the shingle (character or word).
         * @param shingleSize The size (k) of each shingle.
         */
        public StringSimilaritySelector(SimilarityType similarityType,
                ShingleType shingleType, int shingleSize) {
            this.similarityType = similarityType;
            this.shingleType = shingleType;
            
            if (shingleSize > 0)
                this.shingleSize = shingleSize;
            else
                this.shingleSize = 1;
        }
        
        /**
         * @return The string similarity measure.
         */
        public SimilarityType getSimilarityType() {
            return similarityType;
        }
        
        /**
         * @param similarityType The string similarity measure.
         */
        public void setSimilarityType(SimilarityType similarityType) {
            this.similarityType = similarityType;
        }
        
        /**
         * @return The type of the shingle (character or word).
         */
        public ShingleType getShingleType() {
            return shingleType;
        }
        
        /**
         * @param shingleType The type of the shingle (character or word).
         */
        public void setShingleType(ShingleType shingleType) {
            this.shingleType = shingleType;
        }
        
        /**
         * @return The size (k) of each shingle.
         */
        public int getShingleSize() {
            return shingleSize;
        }
        
        /**
         * @param shingleSize The size (k) of each shingle.
         */
        public void setShingleSize(int shingleSize) {
            if (shingleSize > 0)
                this.shingleSize = shingleSize;
            else
                this.shingleSize = 1;
        }
    }
    
    /**
     * A static class which can be used for the identifier chain data of the 
     * download APIs.
     */
    public static class IdentifierChainData {
        private String identifierChain;
        private int identifierCount;
        
        /**
         * @param identifierChain The string containing multiple identifiers 
         * separated by a delimiter.
         * @param identifierCount The number of identifiers contained in the
         * identifier chain.
         */
        public IdentifierChainData(String identifierChain,
                int identifierCount) {
            this.identifierChain = identifierChain;
            this.identifierCount = identifierCount;
        }
        
        /**
         * @return The string containing multiple identifiers separated by a
         * delimiter.
         */
        public String getIdentifierChain() {
            return identifierChain;
        }
        
        /**
         * @param identifierChain The string containing multiple identifiers
         * separated by a delimiter.
         */
        public void setIdentifierChain(String identifierChain) {
            this.identifierChain = identifierChain;
        }
        
        /**
         * @return The number of identifiers contained in the identifier chain.
         */
        public int getIdentifierCount() {
            return identifierCount;
        }
        
        /**
         * @param identifierCount The number of identifiers contained in the 
         * identifier chain.
         */
        public void setIdentifierCount(int identifierCount) {
            this.identifierCount = identifierCount;
        }
    }
    
    /**
     * A static class which can be used to provide the string similarity
     * thresholds of titles and the maximum absolute difference of the years.
     */
    public static class ToleranceData {
	private double titleThreshold = 1;
        private double titleThresholdA = 1;
        private double titleThresholdB = 1;
        private int yearMaxDifference = 0;
        
        /**
         * @param titleThreshold The title comparison threshold for category-B
         * identified data.
         * @param titleThresholdA The title comparison threshold for category-A
         * identified data when year data have been retrieved.
         * @param titleThresholdB The title comparison threshold for category-A
         * identified data when year data haven't been retrieved.
         * @param yearMaxDifference The maximum difference for compared years.
         */
        public ToleranceData(double titleThreshold, double titleThresholdA,
                double titleThresholdB, int yearMaxDifference) {
            if (titleThreshold >= 0 && titleThreshold <= 1)
                this.titleThreshold = titleThreshold;
            
            if (titleThresholdA >= 0 && titleThresholdA <= 1)
                this.titleThresholdA = titleThresholdA;
            
            if (titleThresholdB >= 0 && titleThresholdB <= 1)
                this.titleThresholdB = titleThresholdB;
            
            if (yearMaxDifference >= 0)
                this.yearMaxDifference = yearMaxDifference;
            else
                this.yearMaxDifference = -1 * yearMaxDifference;
        }
        
        /**
         * @return The title comparison threshold for category-B identified
         * data.
         */
        public double getTitleThreshold() {
            return titleThreshold;
        }
        
        /**
         * @param titleThreshold The title comparison threshold for category-B
         * identified data.
         */
        public void setTitleThreshold(double titleThreshold) {
            if (titleThreshold >= 0 && titleThreshold <= 1)
                this.titleThreshold = titleThreshold;
        }
        
        /**
         * @return The title comparison threshold for category-A identified data
         * when year data have been retrieved.
         */
        public double getTitleThresholdA() {
            return titleThresholdA;
        }
        
        /**
         * @param titleThresholdA The title comparison threshold for category-A
         * identified data when year have been retrieved.
         */
        public void setTitleThresholdA(double titleThresholdA) {
            if (titleThresholdA >= 0 && titleThresholdA <= 1)
                this.titleThresholdA = titleThresholdA;
        }
        
        /**
         * @return The title comparison threshold for category-A identified data
         * when year data haven't been retrieved.
         */
        public double getTitleThresholdB() {
            return titleThresholdB;
        }
        
        /**
         * @param titleThresholdB The title comparison threshold for category-A
         * identified data when year data haven't been retrieved.
         */
        public void setTitleThresholdB(double titleThresholdB) {
            if (titleThresholdB >= 0 && titleThresholdB <= 1)
                this.titleThresholdB = titleThresholdB;
        }
        
        /**
         * @return The maximum difference for compared years.
         */
        public int getYearMaxDifference() {
            return yearMaxDifference;
        }
        
        /**
         * @param yearMaxDifference The maximum difference for compared years.
         */
        public void setYearMaxDifference(int yearMaxDifference) {
            if (yearMaxDifference >= 0)
                this.yearMaxDifference = yearMaxDifference;
            else
                this.yearMaxDifference = -1 * yearMaxDifference;
        }
    }
    
    /**
     * A static class which can be used to provide the string similarity
     * thresholds of titles and the maximum absolute difference of the years,
     * for the category-A identified data.
     */
    public static class CategoryAToleranceData {
        private double titleThresholdA = 1;
        private double titleThresholdB = 1;
        private int yearMaxDifference = 0;
        
        /**
         * @param titleThresholdA The title comparison threshold when year data
         * have been retrieved.
         * @param titleThresholdB The title comparison threshold when year data
         * haven't been retrieved.
         * @param yearMaxDifference The maximum difference for compared years.
         */
        public CategoryAToleranceData(double titleThresholdA,
                double titleThresholdB, int yearMaxDifference) {
            if (titleThresholdA >= 0 && titleThresholdA <= 1)
                this.titleThresholdA = titleThresholdA;
            
            if (titleThresholdB >= 0 && titleThresholdB <= 1)
                this.titleThresholdB = titleThresholdB;
            
            if (yearMaxDifference >= 0)
                this.yearMaxDifference = yearMaxDifference;
            else
                this.yearMaxDifference = -1 * yearMaxDifference;
        }
        
        /**
         * @return The title comparison threshold when year data have been
         * retrieved.
         */
        public double getTitleThresholdA() {
            return titleThresholdA;
        }
        
        /**
         * @param titleThresholdA The title comparison threshold when year data
         * have been retrieved.
         */
        public void setTitleThresholdA(double titleThresholdA) {
            if (titleThresholdA >= 0 && titleThresholdA <= 1)
                this.titleThresholdA = titleThresholdA;
        }
        
        /**
         * @return The title comparison threshold when year data haven't been
         * retrieved.
         */
        public double getTitleThresholdB() {
            return titleThresholdB;
        }
        
        /**
         * @param titleThresholdB The title comparison threshold when year data
         * haven't been retrieved.
         */
        public void setTitleThresholdB(double titleThresholdB) {
            if (titleThresholdB >= 0 && titleThresholdB <= 1)
                this.titleThresholdB = titleThresholdB;
        }
        
        /**
         * @return The maximum difference for compared years.
         */
        public int getYearMaxDifference() {
            return yearMaxDifference;
        }
        
        /**
         * @param yearMaxDifference The maximum difference for compared years.
         */
        public void setYearMaxDifference(int yearMaxDifference) {
            if (yearMaxDifference >= 0)
                this.yearMaxDifference = yearMaxDifference;
            else
                this.yearMaxDifference = -1 * yearMaxDifference;
        }
    }
    
    /**
     * A static class which can be used to provide the string similarity
     * threshold for the category-B identified data.
     */
    public static class CategoryBToleranceData {
        private double titleThreshold = 1;
        
        /**
         * @param titleThreshold The title comparison threshold.
         */
        public CategoryBToleranceData(double titleThreshold) {
            if (titleThreshold >= 0 && titleThreshold <= 1)
                this.titleThreshold = titleThreshold;
        }
        
        /**
         * @return The title comparison threshold.
         */
        public double getTitleThreshold() {
            return titleThreshold;
        }
        
        /**
         * @param titleThreshold The title comparison threshold.
         */
        public void setTitleThreshold(double titleThreshold) {
            if (titleThreshold >= 0 && titleThreshold <= 1)
                this.titleThreshold = titleThreshold;
        }
    }

    /**
     * A static class which can be used to for the user-defined string
     * replacements.
     */
    public static class StringReplacementData {
        private String searchString = "";
        private String replacementString = "";
        private StringReplacementType stringReplacementType;
        private int replacementIndex = 0;
        
        /**
         * @param searchString The string to be searched or a regular expression
         * (if the value of the stringReplacementType is Regex).
         * @param replacementString The string to be replaced.
         * @param stringReplacementType The type of the search and replace.
         * @param replacementIndex The index from where the search and replace
         * will occur (used only for BeforeIndex and AfterIndex
         * stringReplacementType).
         */
        public StringReplacementData(String searchString, String
                replacementString, StringReplacementType stringReplacementType,
                int replacementIndex) {
            if (searchString != null)
                this.searchString = searchString;
            
            if (replacementString != null)
                this.replacementString = replacementString;
            
            this.stringReplacementType = stringReplacementType;
            this.replacementIndex = replacementIndex;
        }
        
        /**
         * @return The string be be searched or a regular expression (if the
         * value of the stringReplacementType is Regex).
         */
        public String getSearchString() {
            return searchString;
        }
        
        /**
         * @param searchString The string be be searched or a regular expression
         * (if the value of the stringReplacementType is Regex).
         */
        public void setSearchString(String searchString) {
            if (searchString != null)
                this.searchString = searchString;
        }
        
        /**
         * @return The string to be replaced.
         */
        public String getReplacementString() {
            return replacementString;
        }
        
        /**
         * @param replacementString The string to be replaced.
         */
        public void setReplacementString(String replacementString) {
            if (replacementString != null)
                this.replacementString = replacementString;
        }
        
        /**
         * @return The type of the search and replace.
         */
        public StringReplacementType getStringReplacementType() {
            return stringReplacementType;
        }
        
        /**
         * @param stringReplacementType The type of the search and replace.
         */
        public void setStringReplacementType(StringReplacementType
                stringReplacementType) {
            this.stringReplacementType = stringReplacementType;
        }
        
        /**
         * @return The index from where the search and replace will occur (used
         * only for BeforeIndex and AfterIndex stringReplacementType).
         */
        public int getReplacementIndex() {
            return replacementIndex;
        }
        
        /**
         * @param replacementIndex The index from where the search and replace
         * will occur (used only for BeforeIndex and AfterIndex
         * stringReplacementType).
         */
        public void setReplacementIndex(int replacementIndex) {
            this.replacementIndex = replacementIndex;
        }
    }
    
    /**
     * A static class which can be used to provide the user-defined string
     * replacement lists for the identifiers, titles and years.
     */
    public static class StringFormatData {
        private List<StringReplacementData> arxivIDReplacementDataList = null;
        private List<StringReplacementData> doiReplacementDataList = null;
        private List<StringReplacementData> isbnReplacementDataList = null;
        private List<StringReplacementData> issnReplacementDataList = null;
        private List<StringReplacementData> journaltitleReplacementDataList = 
                null;
        private List<StringReplacementData> lccnReplacementDataList = null;
        private List<StringReplacementData> oclcReplacementDataList = null;
        private List<StringReplacementData> pmidReplacementDataList = null;
        private List<StringReplacementData> titleReplacementDataList = null;
        private List<StringReplacementData> yearReplacementDataList = null;
        
        /**
         * @return A string replacement data list for arXiv IDs.
         */
        public List<StringReplacementData> getArxivIDReplacementDataList() {
            return arxivIDReplacementDataList;
        }
        
        /**
         * @param arxivIDReplacementDataList A string replacement data list for
         * arXiv IDs.
         */
        public void setArxivIDReplacementDataList(List<StringReplacementData>
                arxivIDReplacementDataList) {
            this.arxivIDReplacementDataList = arxivIDReplacementDataList;
        }
        
        /**
         * @return A string replacement data list for DOIs.
         */
        public List<StringReplacementData> getDOIReplacementDataList() {
            return doiReplacementDataList;
        }
        
        /**
         * @param doiReplacementDataList A string replacement data list for
         * DOIs.
         */
        public void setDOIReplacementDataList(List<StringReplacementData>
                doiReplacementDataList) {
            this.doiReplacementDataList = doiReplacementDataList;
        }
        
        /**
         * @return A string replacement data list for ISBNs.
         */
        public List<StringReplacementData> getISBNReplacementDataList() {
            return isbnReplacementDataList;
        }
        
        /**
         * @param isbnReplacementDataList A string replacement data list for 
         * ISBNs.
         */
        public void setISBNReplacementDataList(List<StringReplacementData>
                isbnReplacementDataList) {
            this.isbnReplacementDataList = isbnReplacementDataList;
        }
        
        /**
         * @return A string replacement data list for ISSNs.
         */
        public List<StringReplacementData> getISSNReplacementDataList() {
            return issnReplacementDataList;
        }
        
        /**
         * @param issnReplacementDataList A string replacement data list for 
         * ISSNs.
         */
        public void setISSNReplacementDataList(List<StringReplacementData>
                issnReplacementDataList) {
            this.issnReplacementDataList = issnReplacementDataList;
        }
        
        /**
         * @return A string replacement data list for journalTitles.
         */
        public List<StringReplacementData>
            getJournalTitleReplacementDataList() {
            return journaltitleReplacementDataList;
        }
        
        /**
         * @param journaltitleReplacementDataList A string replacement data list
         * for journalTitles.
         */
        public void setJournalTitleReplacementDataList(
                List<StringReplacementData> journaltitleReplacementDataList) {
            this.journaltitleReplacementDataList =
                    journaltitleReplacementDataList;
        }
        
        /**
         * @return A string replacement data list for LCCNs.
         */
        public List<StringReplacementData> getLCCNReplacementDataList() {
            return lccnReplacementDataList;
        }
        
        /**
         * @param lccnReplacementDataList A string replacement data list for 
         * LCCNs.
         */
        public void setLCCNReplacementDataList(List<StringReplacementData>
                lccnReplacementDataList) {
            this.lccnReplacementDataList = lccnReplacementDataList;
        }
        
        /**
         * @return A string replacement data list for OCLCs.
         */
        public List<StringReplacementData> getOCLCReplacementDataList() {
            return oclcReplacementDataList;
        }
        
        /**
         * @param oclcReplacementDataList A string replacement data list for 
         * OCLCs.
         */
        public void setOCLCReplacementDataList(List<StringReplacementData>
                oclcReplacementDataList) {
            this.oclcReplacementDataList = oclcReplacementDataList;
        }
        
        /**
         * @return A string replacement data list for PMIDs.
         */
        public List<StringReplacementData> getPMIDReplacementDataList() {
            return pmidReplacementDataList;
        }
        
        /**
         * @param pmidReplacementDataList A string replacement data list for 
         * PMIDs.
         */
        public void setPMIDReplacementDataList(List<StringReplacementData>
                pmidReplacementDataList) {
            this.pmidReplacementDataList = pmidReplacementDataList;
        }
        
        /**
         * @return A string replacement data list for titles.
         */
        public List<StringReplacementData> getTitleReplacementDataList() {
            return titleReplacementDataList;
        }
        
        /**
         * @param titleReplacementDataList A string replacement data list for 
         * titles.
         */
        public void setTitleReplacementDataList(List<StringReplacementData>
                titleReplacementDataList) {
            this.titleReplacementDataList = titleReplacementDataList;
        }
        
        /**
         * @return A string replacement data list for years.
         */
        public List<StringReplacementData> getYearReplacementDataList() {
            return yearReplacementDataList;
        }
        
        /**
         * @param yearReplacementDataList A string replacement data list for 
         * years.
         */
        public void setYearReplacementDataList(List<StringReplacementData>
                yearReplacementDataList) {
            this.yearReplacementDataList = yearReplacementDataList;
        }
    }
    
    /**
     * A static class which can be used to provide the authentication data of
     * a repository connection.
     */
    public static class AuthenticationData {
        private String username = "";
        private String password = "";
        
        /**
         * @param username The username for a repository connection.
         * @param password The password for a repository connection.
         */
        public AuthenticationData(String username, String password) {
            if (username != null)
                this.username = username;
            
            if (password != null)
                this.password = password;
        }
        
        /**
         * @return The username for a repository connection.
         */
        public String getUsername() {
            return username;
        }
        
        /**
         * @param username The username for a repository connection.
         */
        public void setUsername(String username) {
            if (username != null)
                this.username = username;
        }
        
        /**
         * @return The password for a repository connection.
         */
        public String getPassword() {
            return password;
        }
        
        /**
         * @param password The password for a repository connection.
         */
        public void setPassword(String password) {
            if (password != null)
                this.password = password;
        }
    }
    
    /**
     * A static class which can be used to provide the general download
     * parameters of APIs.
     */
    public static class DownloadParameterData {
        private int connectTimeout;
        private int readTimeout;
        private int maxAllowedConsecutiveErrors;
        
        /**
         * @param connectTimeout The connection timeout in milliseconds.
         * @param readTimeout The read timeout in milliseconds.
         * @param maxAllowedConsecutiveErrors The number of consecutive errors
         * allowed during the downloads.
         */
        public DownloadParameterData(int connectTimeout, int readTimeout,
                int maxAllowedConsecutiveErrors) {
            this.connectTimeout = connectTimeout;
            this.readTimeout = readTimeout;
            this.maxAllowedConsecutiveErrors = maxAllowedConsecutiveErrors;
        }
        
        /**
         * @return The connection timeout in milliseconds.
         */
        public int getConnectTimeout() {
            return connectTimeout;
        }
        
        /**
         * @param connectTimeout The connection timeout in milliseconds.
         */
        public void setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
        }
        
        /**
         * @return The read timeout in milliseconds.
         */
        public int getReadTimeout() {
            return readTimeout;
        }
        
        /**
         * @param readTimeout The read timeout in milliseconds.
         */
        public void setReadTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
        }
        
        /**
         * @return The number of consecutive errors allowed during the
         * downloads.
         */
        public int getMaxAllowedConsecutiveErrors() {
            return maxAllowedConsecutiveErrors;
        }
        
        /**
         * @param maxAllowedConsecutiveErrors The number of consecutive errors
         * allowed during the downloads.
         */
        public void setMaxAllowedConsecutiveErrors(int
                maxAllowedConsecutiveErrors) {
            this.maxAllowedConsecutiveErrors = maxAllowedConsecutiveErrors;
        }
    }
    
    /**
     * A static class which can be used to provide the download parameters of
     * APIs.
     */
    public static class APIDownloadParameterData
            extends DownloadParameterData {
        private int downloadBatchSize;
        private int downloadDelay;
        private int insertBatchSize;
        
        /**
         * @param connectTimeout The connection timeout in milliseconds.
         * @param readTimeout The read timeout in milliseconds.
         * @param insertBatchSize The number of records to be batch inserted
         * after downloads.
         * @param downloadBatchSize The number of identifiers to be queried and
         * downloaded in a single batch.
         * @param downloadDelay The delay in milliseconds between download
         * batches.
         * @param maxAllowedConsecutiveErrors The number of consecutive errors
         * allowed during the downloads.
         */
        public APIDownloadParameterData(int connectTimeout, int readTimeout,
                int insertBatchSize, int downloadBatchSize, int downloadDelay,
                int maxAllowedConsecutiveErrors) {
            super(connectTimeout, readTimeout,
                    maxAllowedConsecutiveErrors);
            
            this.downloadBatchSize = downloadBatchSize;
            this.downloadDelay = downloadDelay;
            this.insertBatchSize = insertBatchSize;
        }
        
        /**
         * @return The number of records to be batch inserted after downloads.
         */
        public int getInsertBatchSize() {
            return insertBatchSize;
        }
        
        /**
         * @param insertBatchSize The number of records to be batch inserted
         * after downloads.
         */
        public void setInsertBatchSize(int insertBatchSize) {
            this.insertBatchSize = insertBatchSize;
        }
        
        /**
         * @return The number of identifiers to be queried and downloaded in a
         * single batch.
         */
        public int getDownloadBatchSize() {
            return downloadBatchSize;
        }
        
        /**
         * @param downloadBatchSize The number of identifiers to be queried and
         * downloaded in a single batch.
         */
        public void setDownloadBatchSize(int downloadBatchSize) {
            this.downloadBatchSize = downloadBatchSize;
        }
        
        /**
         * @return The delay in milliseconds between download batches.
         */
        public int getDownloadDelay() {
            return downloadDelay;
        }
        
        /**
         * @param downloadDelay The delay in milliseconds between download
         * batches.
         */
        public void setDownloadDelay(int downloadDelay) {
            this.downloadDelay = downloadDelay;
        }
    }
    
    /**
     * A static class which can be used to provide the download parameters of
     * PubMed API.
     */
    public static class PubMedDownloadParameterData extends
            APIDownloadParameterData {
        private String apiTool = "";
        private String apiEmail = "";
        
        /**
         * @param connectTimeout The connection timeout in milliseconds.
         * @param readTimeout The read timeout in milliseconds.
         * @param insertBatchSize The number of records to be batch inserted
         * after downloads.
         * @param downloadBatchSize The number of identifiers to be queried and
         * downloaded in a single batch.
         * @param downloadDelay The delay in milliseconds between download
         * batches.
         * @param maxAllowedConsecutiveErrors The number of consecutive errors
         * allowed during the downloads.
         * @param apiTool The tool name for the PubMed E-utilities.
         * @param apiEmail The email for the PubMed E-utilities.
         */
        public PubMedDownloadParameterData(int connectTimeout,
                int readTimeout, int insertBatchSize, int downloadBatchSize,
                int downloadDelay, int maxAllowedConsecutiveErrors,
                String apiTool, String apiEmail) {
            super(connectTimeout, readTimeout, insertBatchSize, 
                    downloadBatchSize, downloadDelay,
                    maxAllowedConsecutiveErrors);
            
            if (apiTool != null)
                this.apiTool = apiTool;
            
            if (apiEmail != null)
                this.apiEmail = apiEmail;
        }
        
        /**
         * @return The tool name for the PubMed E-utilities.
         */
        public String getAPITool() {
            return apiTool;
        }
        
        /**
         * @param apiTool The tool name for the PubMed E-utilities.
         */
        public void setAPITool(String apiTool) {
            if (apiTool != null)
                this.apiTool = apiTool;
        }
        
        /**
         * @return The email for the PubMed E-utilities.
         */
        public String getAPIEmail() {
            return apiEmail;
        }
        
        /**
         * @param apiEmail The email for the PubMed E-utilities.
         */
        public void setAPIEmail(String apiEmail) {
            if (apiEmail != null)
                this.apiEmail = apiEmail;
        }
    }
    
    /**
     * A static class which can be used to provide the download parameters of
     * WorldCat API.
     */
    public static class WorldCatDownloadParameterData extends
            APIDownloadParameterData {
        private int attemptCount;
        private int delayBetweenAttempts;
        
        /**
         * @param connectTimeout The connection timeout in milliseconds.
         * @param readTimeout The read timeout in milliseconds.
         * @param insertBatchSize The number of records to be batch inserted
         * after downloads.
         * @param downloadBatchSize The number of identifiers to be queried and
         * downloaded in a single batch.
         * @param downloadDelay The delay in milliseconds between download
         * batches.
         * @param maxAllowedConsecutiveErrors The number of consecutive errors
         * allowed during the downloads.
         * @param attemptCount The number of retries after a failed download.
         * @param delayBetweenAttempts The delay in milliseconds between retries
         * after a failed download.
         */
        public WorldCatDownloadParameterData(int connectTimeout,
                int readTimeout, int insertBatchSize, int downloadBatchSize,
                int downloadDelay, int maxAllowedConsecutiveErrors,
                int attemptCount, int delayBetweenAttempts) {
            super(connectTimeout, readTimeout, insertBatchSize,
                    downloadBatchSize, downloadDelay,
                    maxAllowedConsecutiveErrors);
            
            this.attemptCount = attemptCount;
            this.delayBetweenAttempts = delayBetweenAttempts;
        }
        
        /**
         * @return The number of retries after a failed download.
         */
        public int getAttemptCount() {
            return attemptCount;
        }

        /**
         * @param attemptCount The number of retries after a failed download.
         */
        public void setAttemptCount(int attemptCount) {
            this.attemptCount = attemptCount;
        }
        
        /**
         * @return The delay in milliseconds between retries after a failed
         * download.
         */
        public int getDelayBetweenAttemps() {
            return delayBetweenAttempts;
        }
        
        /**
         * @param delayBetweenAttempts The delay in milliseconds between retries
         * after a failed download.
         */
        public void setDelayBetweenAttemps(int delayBetweenAttempts) {
            this.delayBetweenAttempts = delayBetweenAttempts;
        }
    }
    
    /**
     * A static class which can be used for parallel downloads from WorldCat.
     */
    public static class WorldCatCallable implements Callable {
        private final URL fileURL;
        private final File file;
        private final int connectTimeout;
        private final int readTimeout;
        private final int attemptCount;
        private final int delayBetweenAttempts;
        
        /**
         * @param fileURL The URL of the file to be downloaded.
         * @param file The file for the downloaded content.
         * @param connectTimeout The connection timeout in milliseconds.
         * @param readTimeout The read timeout in milliseconds.
         * @param attemptCount The number of retries after a failed download.
         * @param delayBetweenAttempts The delay in milliseconds between retries
         * after a failed download.
         */
        public WorldCatCallable(URL fileURL, File file, int connectTimeout
                ,int readTimeout, int attemptCount, int delayBetweenAttempts) {
            this.fileURL = fileURL;
            this.file = file;
            this.connectTimeout = connectTimeout;
            this.readTimeout = readTimeout;
            this.attemptCount = attemptCount;
            this.delayBetweenAttempts = delayBetweenAttempts;
        }
        
        /**
         * A callable method for parallel downloads.
         * @return The number of consecutive errors during the download.
         */
        @Override
        public Integer call() {
            int consecutiveErrors = 0;
            // Retries after a failed download.
            for (int i = 1; i <= attemptCount; i++) {
                try {
                    // File download.
                    FileUtils.copyURLToFile(fileURL, file, connectTimeout,
                            readTimeout);
                    consecutiveErrors = 0;
                    // In case of a successful download the loop will end.
                    break;
                }
                catch (Exception ex1) {
                    try {
                        // A delay between retries.
                        Thread.sleep(delayBetweenAttempts);
                    }
                    catch (Exception ex2) {
                    }
                    
                    if (i == attemptCount)
                        consecutiveErrors++;  
                }
            }
            
            return consecutiveErrors;
        } 
    }
}