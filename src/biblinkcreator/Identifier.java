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

/**
 * A class for the identifiers involved in the link creation.
 * <br><br>
 * Author: David Nazarian
 * @author David Nazarian
 */
public class Identifier {
    /**
     * An enumeration for all the identifiers.
     */
    public enum IdentifierType {
        arXivID,
        DOI,
        ISBN,
        ISSN,
        JournalTitle,
        LCCN,
        OCLC,
        PMID,
    }
    
    /**
     * An enumeration for the category-A identifiers.
     */
    public enum CategoryAIdentifierType {
        arXivID,
        DOI,
        ISBN,
        LCCN,
        OCLC,
        PMID
    }
    
    /**
     * An enumeration for the category-B identifiers.
     */
    public enum CategoryBIdentifierType {
        ISSN,
        JournalTitle
    }
    
    /**
     * Gets the equivalent of an IdentifierType provided a
     * categoryAIdentifierType.
     * @param categoryAIdentifierType The category-A identifier type.
     * @return An equivalent IdentifierType.
     */
    public IdentifierType getIdentifierType(
            CategoryAIdentifierType categoryAIdentifierType) {
        IdentifierType identifierType = null;
        
        switch (categoryAIdentifierType) {
            case arXivID:
                identifierType = IdentifierType.arXivID;
                break;
            case DOI:
                identifierType = IdentifierType.DOI;
                break;
            case ISBN:
                identifierType = IdentifierType.ISBN;
                break;
            case LCCN:
                identifierType = IdentifierType.LCCN;
                break;
            case OCLC:
                identifierType = IdentifierType.OCLC;
                break;
            case PMID:
                identifierType = IdentifierType.PMID;
                break;
        }
        
        return identifierType;
    }
    
    /**
     * Gets the equivalent of an IdentifierType provided a
     * categoryBIdentifierType.
     * @param categoryBIdentifierType The category-B identifier type.
     * @return An equivalent IdentifierType.
     */
    public IdentifierType getIdentifierType(
            CategoryBIdentifierType categoryBIdentifierType) {
        IdentifierType identifierType = null;
        
        switch (categoryBIdentifierType) {
            case ISSN:
                identifierType = IdentifierType.ISSN;
                break;
            case JournalTitle:
                identifierType = IdentifierType.JournalTitle;
                break;
        }
        
        return identifierType;
    }
    
    /**
     * Gets a variable name for the provided identifier type.
     * @param identifierType The identifier type.
     * @return A variable name for the provided identifier type.
     */
    public String getIdentifierVariableName(IdentifierType identifierType) {
        String variableName;
        
        switch (identifierType) {
            case arXivID:
                variableName = "arxivID";
                break;
            case JournalTitle:
                variableName = "journalTitle";
                break;
            default:
                variableName = identifierType.name().toLowerCase();
                break;
        }

        return variableName;
    }
    
    /**
     * Gets an identifier name for the provided identifier type.
     * @param identifierType The identifier type.
     * @return An identifier name for the provided identifier type.
     */
    public String getIdentifierName(IdentifierType identifierType) {
        String identifierName;
        
        switch (identifierType) {
            case arXivID:
                identifierName = "arXiv ID";
                break;
            case JournalTitle:
                identifierName = "journal title";
                break;
            default:
                identifierName = identifierType.name();
                break;
        }

        return identifierName;
    }
}