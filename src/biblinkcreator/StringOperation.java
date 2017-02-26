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

import biblinkcreator.Identifier.*;
import biblinkcreator.DataStructure.*;
import java.util.*;
import java.net.*;

/**
 * A class providing basic string operations for the identifier, title and year.
 * <br><br>
 * Author: David Nazarian
 * @author David Nazarian
 */
public class StringOperation {
    /**
     * An enumeration for the type of string replacement.
     */
    public enum StringReplacementType {
        Anywhere,
        BeforeEnd,
        AfterIndex,
        BeforeIndex,
        Regex
    }
    // An array of symbols to be removed from strings.
    private final String[] symbols =
        {"’", "[", "]", "(", ")", "{", "}", "‒", "–", "—",
         "―", "‐", "-", "‹", "›", "«", "»", "`", "‘", "’",
         "“", "”", "/", ",", ".", "~", "©", "℗", "®", "℠",
         "™", "_", ":", "|", ";", "¿", "?", "€", "$", 
         "\'", "\\", "\""};
    // A set of prefixes for the LCCN identifiers.
    // For more information please visit the following website:
    // https://www.loc.gov/marc/lccn_structure.html
    private final Set<String> lccnPrefixes = new HashSet<>(Arrays.asList( 
        "a", "ac", "af", "afl", "agr", "bi", "br", "bs", "c",
        "ca", "cad", "cd", "clc", "cs", "cx", "cy", "d", "do",
        "e", "es", "f", "fi", "fia", "fie", "g", "gm", "gs",
        "h", "ha", "he", "hew", "hex", "it", "int", "j", "ja",
        "jx", "k", "kx", "l", "llh", "ltf", "m", "ma", "map",
        "med", "mic", "mid", "mie", "mif", "mm", "mp", "mpa",
        "ms", "mus", "ncn", "ne", "nex", "no", "ntc", "nuc",
        "or", "pa", "pho", "php", "phq", "po", "pp", "r", "ra", 
        "rc", "re", "ru", "s", "sa", "sax", "sc", "sd", "sf",
        "sg", "sn", "ss", "su", "tb", "tmp", "um", "unk", "w",
        "war", "x", "z", "n", "nb", "nr", "no", "sh", "sj", "sp",
        "cf", "agr", "xca"));
    // A set of prefixes for the arXiv identifiers.
    // For more information please visit the following website:
    // https://arxiv.org/help/api/user-manual
    private final Set<String> arxivIDPrefixes = new HashSet<>(Arrays.asList(
        "stat", "q-bio", "cs", "nlin", "math", "astro-ph", "cond-mat",
        "gr-qc", "hep-ex", "hep-lat", "hep-ph", "hep-th", "math-ph",
        "nucl-ex", "nucl-th", "physics", "quant-ph", "alg-geom",
        "q-alg", "chao-dyn", "adap-org", "patt-sol", "funct-an",
        "dg-da", "comp-gas", "solv-int"));
    
    /**
     * Replaces the control characters of the input string with a specified
     * string.
     * @param str The input string.
     * @param replacementStr The string to replace the control characters.
     * @return A string with replaced control characters.
     */
    public String replaceControlChars(String str, String replacementStr) {
        // Replaces the control character ranges 00-1F and 80-9F.
        str = str.replaceAll("[\\x00-\\x1F\\x80-\\x9F]", replacementStr);
        
        return str;
    }
    
    /**
     * Replaces strings from the input string.
     * @param str The input string.
     * @param replacementData The list of the replacements.
     * @return A replaced string.
     */
    public String replaceUserDefinedStrings(String str,
            List<StringReplacementData> replacementData) {
        String replacedStr = str;
        StringReplacementData data;
        String searchString, replacementString;
        int replaceIndex;
        
        // An iteration for each replacement item.
        for (int i = 0; i < replacementData.size(); i++) {
            // The replacement parameters.
            data = replacementData.get(i);
            // The string to be searched or a regular expression.
            searchString = data.getSearchString();
            // The string to be replaced.
            replacementString = data.getReplacementString();
            // The index from where the search and replace will occur.
            replaceIndex = data.getReplacementIndex();
            String strPartA, strPartB;
            // The replace index can't be negative.
            if (replaceIndex < 0)
                replaceIndex = 0;
            // The replace index can't be greater than the length of the input
            // string.
            if (replaceIndex > str.length())
                replaceIndex = str.length();
            
            // The type of the search and replace that will occur.
            switch (data.getStringReplacementType()) {
                // A search and replace anywhere in the input string.
                case Anywhere:
                    // Creates the replaced string.
                    replacedStr = replacedStr.replace(searchString,
                           replacementString);
                    
                    break;
                // A search and replace before the end of the input string.
                case BeforeEnd:
                    // The replacedStr must have a length greater than that of
                    // searchString.
                    if (replacedStr.length() > searchString.length()) {
                        // The part of the replacedStr before the end with a
                        // length equal to that of search string.
                        strPartB = replacedStr.substring(replacedStr.length() -
                                searchString.length());
                        // If strPartB equals to the searchString, then a
                        // replacement will occur.
                        if (strPartB.equals(searchString)) {
                            // The part of the replacedStr from the start until
                            // the searchString.
                            strPartA = replacedStr.substring(
                                    0, replacedStr.length() -
                                    searchString.length());
                            // Creates the replaced string.
                            replacedStr = strPartA + replacementString;
                        }
                    }
                    
                    break;
                // A search and replace after an index of the input string.
                case AfterIndex:
                    // The replaceIndex must be less than the replacedStr
                    // length.
                    if (replaceIndex < replacedStr.length()) {
                        // The part of replacedStr before the replaceIndex.
                        strPartA = replacedStr.substring(0, replaceIndex);
                        // The part of replacedStr starting from replaceIndex.
                        strPartB = replacedStr.substring(replaceIndex);
                        // Replaces occurrences in strPartB.
                        strPartB = strPartB.replace(searchString,
                                replacementString);
                        // Creates the replaced string.
                        replacedStr = strPartA + strPartB;
                    }
                    
                    break;
                // A search and replace before an index of the input string.
                case BeforeIndex:
                    // The replaceIndex must be greater than 0.
                    if (replaceIndex > 0) {
                        // The part of the replacedStr before the replaceIndex.
                        strPartA = replacedStr.substring(0, replaceIndex);
                        // The part of replacedStr starting from replaceIndex.
                        strPartB = replacedStr.substring(replaceIndex);
                        // Replaces occurances in strPartA.
                        strPartA = strPartA.replace(searchString,
                                replacementString);
                        // Creates the replaced string.
                        replacedStr = strPartA + strPartB;
                    }
                    
                    break;
                // A search and replace anywhere in the input string based on a
                // based on a regular expression.
                case Regex:
                    // In case of provided invalid regular expression.
                    try {
                        // Creates the replaced string.
                        replacedStr = replacedStr.replaceAll(searchString,
                               replacementString);
                    }
                    catch (Exception ex) {
                    }
                    
                    break;
            }
        }
        // Replaces continuous spaces with single ones and trims the result.
        replacedStr = replacedStr.replaceAll(" +", " ").trim();
        
        return replacedStr;
    }
    
    /**
     * Replaces the symbols contained in the symbols array from the input
     * string, with a single space.
     * @param str The input string.
     * @return A string with replaced symbols.
     */
    public String replaceSymbols(String str) {
        // Iteration for each symbol.
        for (String symbol : symbols)
            str = str.replace(symbol, " ");
        
        // Replaces continuous spaces with single ones and trims the result.
        str = str.replaceAll(" +", " ").trim();
        
        return str;
    }
    
    /**
     * Validates and formats a provided arXiv identifier and returns it
     * accompanied by a value indicating its validity. For information about the
     * structure of the identifier please visit the following address: <br>
     * https://arxiv.org/help/arxiv_identifier
     * @param arxivID The arXiv identifier to be validated and formatted.
     * @param replacementData (Optional) The string replacements before
     * validation and formatting.
     * @return A string format result containing the validated and formatted
     * identifier and a value indicating its validity.
     */
    public StringFormatResult formatArXivID(String arxivID,
            List<StringReplacementData> replacementData) {
        StringFormatResult formatResult = new StringFormatResult();
        
        if (arxivID.length() == 0)
            return formatResult;
        
        // Replaces the control characters from the identifier.
        arxivID = replaceControlChars(arxivID, "");
        // String replacements will occur if replacement data have been
        // provided.
        if (replacementData != null)
            arxivID = replaceUserDefinedStrings(arxivID, replacementData);
        
        // Removes the identifier prefix (if exists).
        arxivID = arxivID.replace("arXiv:", "");
        // Conversion to the format which can be used by the download API.
        arxivID = arxivID.replace("quantph", "quant-ph");
        arxivID = arxivID.replace("astroph", "astro-ph");
        arxivID = arxivID.replace("hepth", "hep-th");        
        // Identifier parts for those assigned before April 1st 2007.
        String[] arxivIDPartsA = arxivID.split("/");
        String[] arxivIDPartsB, arxivIDPartsC;
        
        switch (arxivIDPartsA.length) {
            // Identifiers assigned before April 1st 2007.
            case 2:
                // Separates the version (if exists).
                arxivIDPartsB = arxivIDPartsA[1].split("v");
                // The provided identifier is invalid if the separated parts are
                // greater than 2.
                if (arxivIDPartsB.length > 2)
                    return formatResult;
                // Length of year = 2, length of month = 2, length of number = 3
                // The total length can't be different than 2 + 2 + 3 = 7.
                if (arxivIDPartsB[0].length() != 7)
                    return formatResult;
                
                // Separates the archive and the subject class.
                String[] subjectCategoryParts = arxivIDPartsA[0].split("\\.");
                // If arxivIDPrefixes set doesn't contain the archive prefix,
                // then the identifier is invalid.
                if (!arxivIDPrefixes.contains(subjectCategoryParts[0]))
                    return formatResult;
                else
                    // Creates the identifier that can be used by the download
                    // API.
                    arxivID = subjectCategoryParts[0] + "/" + arxivIDPartsA[1];
                
                try {
                    //The year, month, number part must be numeric.
                    Integer.parseInt(arxivIDPartsB[0]);
                    // Separates the year.
                    int year =
                            Integer.parseInt(arxivIDPartsB[0].substring(0, 2));
                    // Separates the month.
                    int month =
                            Integer.parseInt(arxivIDPartsB[0].substring(2, 4));
                    
                    // Validates the month and year.
                    if ((month == 0 || month > 12) || 
                            (year == 7 && month > 3) ||
                            (year > 7 && year < 91))
                        return formatResult;
                    
                    // The version part (is exists) must be numeric.
                    if (arxivIDPartsB.length == 2)
                        Integer.parseInt(arxivIDPartsB[1]);
                }
                catch (Exception ex) {
                    return formatResult;
                }
                
                break;
            // Identifiers assigned after April 1st 2007.
            case 1:
                // Separates the year and month part from the number and version
                // (if exists).
                arxivIDPartsB = arxivIDPartsA[0].split("\\.");
                // The provided identifier is invalid if the separated parts are
                // different than 2 or the year and month part length is
                // different than 4.
                if (arxivIDPartsB.length != 2 || arxivIDPartsB[0].length() != 4)
                    return formatResult;
                
                // Separates the version (if exists).
                arxivIDPartsC = arxivIDPartsB[1].split("v");
                // The provided identifier is invalid if the separated parts are
                // greater than 2.
                if (arxivIDPartsC.length > 2)
                    return formatResult;
                
                try {
                    //The year and month must be numeric.
                    Integer.parseInt(arxivIDPartsB[0]);
                    // Separates the year.
                    int year =
                            Integer.parseInt(arxivIDPartsB[0].substring(0, 2));
                    // Separates the month.
                    int month =
                            Integer.parseInt(arxivIDPartsB[0].substring(2, 4));
                    // Validates the month and year.
                    if ((year < 7 || month == 0 || month > 12) || 
                            (year == 7 && month <= 3))
                        return formatResult;
                    
                    // Validates the number part length based on the year and
                    // the month. From 0704 through 1412 it is 4-digits, after
                    // 1412 its 5-digits.
                    if ((year <= 14 && arxivIDPartsC[0].length() != 4) ||
                         (year > 14 && arxivIDPartsC[0].length() != 5))
                        return formatResult;
                    
                    // The number part must be numeric.
                    Integer.parseInt(arxivIDPartsC[0]);
                    // The version part (is exists) must be numeric.
                    if (arxivIDPartsC.length == 2)
                        Integer.parseInt(arxivIDPartsC[1]);
                }
                catch (Exception ex) {
                    return formatResult;
                }
                
                break;
            default:
                return formatResult;
        }
        // Sets the identifier value to be returned.
        formatResult.setValue(arxivID);
        // Sets the validity value of the identifier.
        formatResult.setIsValid(true);
        
        return formatResult;
    }    
    
    /**
     * Validates and formats a provided DOI and returns it accompanied by a
     * value indicating its validity. For information about the structure of the
     * identifier please visit the following address: <br>
     * https://www.doi.org/doi_handbook/2_Numbering.html
     * @param doi The Digital Object Identifier (DOI) to be validated and
     * formatted.
     * @param replacementData (Optional) The string replacements before
     * validation and formatting.
     * @return A string format result containing the validated and formatted
     * identifier and a value indicating its validity.
     */
    public StringFormatResult formatDOI(String doi,
            List<StringReplacementData> replacementData) {
        StringFormatResult formatResult = new StringFormatResult();
        
        if (doi.length() == 0)
            return formatResult;
        
        // Replaces the control characters from the identifier.
        doi = replaceControlChars(doi, "");
        // String replacements will occur if replacement data have been
        // provided.
        if (replacementData != null)
            doi = replaceUserDefinedStrings(doi, replacementData);
        
        String formattedDOI = "";
        int codePoint;
        // ASCII characters are case insensitive in DOIs. A conversion of these
        // characters to uppercase will provide uniformity for comparisons.
        for (int i = 0; i < doi.length(); i++) {
            // The character at the index.
            codePoint = doi.codePointAt(i);
            // Checks if the character is in the range a-z.
            if (codePoint >= 0x61 && codePoint <= 0x7A)
                // Converts the character to uppercase.
                formattedDOI += new String(Character.toChars(codePoint - 32));
            else 
                // Other characters will not be converted.
                formattedDOI += new String(Character.toChars(codePoint));
        }
        
        try {
            // URL decoding for the identifier.
            formattedDOI = URLDecoder.decode(formattedDOI, "UTF-8");
        }
        catch (Exception ex) {
            return formatResult;
        }
        // The index of the directory code.
        int dirIndex = formattedDOI.indexOf("10.");
        // The index of the prefix-suffix separator.
        int sepIndex = formattedDOI.indexOf("/");
        
        if (dirIndex > -1 && sepIndex > dirIndex)
            // DOI identifier to be returned.
            formattedDOI =
                    formattedDOI.substring(dirIndex, formattedDOI.length());
        else
            return formatResult;
        
        // Sets the identifier value to be returned.
        formatResult.setValue(formattedDOI);
        // Sets the validity value of the identifier.
        formatResult.setIsValid(true);
        
        return formatResult;
    }
    
    /**
     * Validates and formats a provided ISBN (including a conversion to an
     * ISBN13 if it represents an ISBN10) and returns it accompanied by a value
     * indicating its validity. For information about the structure of the
     * identifier please visit the following address: <br>
     * https://en.wikipedia.org/wiki/International_Standard_Book_Number
     * @param isbn The International Standard Book Number (ISBN) to be validated
     * and formatted.
     * @param replacementData (Optional) The string replacements before
     * validation and formatting.
     * @return A string format result containing the validated and formatted
     * identifier and a value indicating its validity.
     */
    public StringFormatResult formatISBN(String isbn,
            List<StringReplacementData> replacementData) {
        StringFormatResult formatResult = new StringFormatResult();
        UIDFunctionStore uid = new UIDFunctionStore();
        
        if (isbn.length() == 0)
            return formatResult;
        
        // Replaces the control characters from the identifier.
        isbn = replaceControlChars(isbn, "");
        // String replacements will occur if replacement data have been
        // provided.
        if (replacementData != null)
            isbn = replaceUserDefinedStrings(isbn, replacementData);
        
        // Removes from it characters other than 0 to 9, x or X.
        isbn = isbn.replaceAll("[^0-9xX]", "");
        // Validity check for the ISBN.
        if (uid.isISBNValid(isbn)) {
            // A length of 10 is an indicator of ISBN10.
            if (isbn.length() == 10)
                // Converts the ISBN10 to an equivalent ISBN13.
                isbn = uid.getISBN13(isbn);
            
            // Sets the identifier value to be returned.
            formatResult.setValue(isbn);
            // Sets the validity value of the identifier.
            formatResult.setIsValid(true);
        }
        
        return formatResult;
    }
    
    /**
     * Validates and formats a provided ISSN and returns it accompanied by a
     * value indicating its validity. For information about the structure of the
     * identifier please visit the following address: <br>
     * https://en.wikipedia.org/wiki/International_Standard_Serial_Number
     * @param issn The International Standard Serial Number (ISSN) to be
     * validated and formatted.
     * @param replacementData (Optional) The string replacements before
     * validation and formatting.
     * @return A string format result containing the validated and formatted
     * identifier and a value indicating its validity.
     */
    public StringFormatResult formatISSN(String issn,
            List<StringReplacementData> replacementData) {
        StringFormatResult formatResult = new StringFormatResult();
        UIDFunctionStore uid = new UIDFunctionStore();
        
        if (issn.length() == 0)
            return formatResult;
        
        // Replaces the control characters from the identifier.
        issn = replaceControlChars(issn, "");
        // String replacements will occur if replacement data have been
        // provided.
        if (replacementData != null)
            issn = replaceUserDefinedStrings(issn, replacementData);
        
        // Removes from it characters other than 0 to 9, x or X.
        issn = issn.replaceAll("[^0-9xX]", "");
        // Validity check for the ISBN.
        if (uid.isISSNValid(issn)) {
            // Sets the identifier value to be returned.
            formatResult.setValue(issn);
            // Sets the validity value of the identifier.
            formatResult.setIsValid(true);
        }
        
        return formatResult;
    }
    
    /**
     * Validates and formats a provided LCCN (including its normalization) and
     * returns it accompanied by a value indicating its validity. For
     * information about the structure of the identifier please visit the
     * following addresses: <br>
     * https://www.loc.gov/marc/lccn_structure.html<br>
     * https://www.loc.gov/marc/lccn-namespace.html
     * @param lccn The Library of Congress Control Number (LCCN) to be validated
     * and formatted.
     * @param replacementData (Optional) The string replacements before
     * validation and formatting.
     * @return A string format result containing the validated and formatted
     * identifier and a value indicating its validity.
     */
    public StringFormatResult formatLCCN(String lccn,
            List<StringReplacementData> replacementData) {
        StringFormatResult formatResult = new StringFormatResult();
        
        if (lccn.length() == 0)
            return formatResult;
        
        // Replaces the control characters from the identifier.
        lccn = replaceControlChars(lccn, "");
        // String replacements will occur if replacement data have been
        // provided.
        if (replacementData != null)
            lccn = replaceUserDefinedStrings(lccn, replacementData);
        
        // Removes one or more whitespaces from the identifier.
        lccn = lccn.replaceAll("\\s+","");
        // Separates the suffix/alphabetic identifier and revision date (if
        // exists) from the control number. They don't affect the uniqueness of
        // the control number.
        String[] lccnParts = lccn.split("/");
        // The control number is invalid if it has no length.
        if (lccnParts[0].length() == 0)
            return formatResult;
        
        String s;
        String prefix = "";
        int i;
        int prefixLength = 0;
        // An iteration for all the characters of the control number.
        for (i = 0; i < lccnParts[0].length() - 1; i++) {
            // A string containing a character.
            s = lccnParts[0].substring(i, i + 1);
            // If the string is not numeric, then it is a part of the prefix.
            if (s.matches("[^0-9]"))
                prefixLength++;
            else
                break;
        }
        // In case a prefix exists.
        if (prefixLength > 0) {
            // Separates the prefix.
            prefix = lccnParts[0].substring(0, prefixLength).trim();
            // Sometimes an "lc" exists in front of the control numbers which 
            // must be removed.
            if (prefix.equals("lc"))
                prefix = "";
            // If the LCCN prefixes set doesn't contain the prefix, then the
            // control number is invalid.
            else if (!lccnPrefixes.contains(prefix))
                return formatResult;
        }
        // Separation of the part after the prefix.
        String afterPrefix = lccnParts[0].substring(prefixLength);
        // If it has no length, then the control number is invalid.
        if (afterPrefix.length() == 0)
            return formatResult;
        
        // Separation of the parts after the prefix.
        String[] afterPrefixParts = afterPrefix.split("-");
        // The control number is invalid if parts greater than 2 is the result
        // of the separation.
        if (afterPrefixParts.length > 2)
            return formatResult;
        else if (afterPrefixParts.length > 1) {
            // Separates the year.
            String year = afterPrefixParts[0].replaceAll("[^0-9]", "");
            // Separates the serial number.
            String serial = afterPrefixParts[1].replaceAll("[^0-9]", "");
            // The year and the serial number must have a length greater than 0.
            if (year.length() == 0 || serial.length() == 0)
                return formatResult;
            // If the serial number length is less than 6, then it must be left
            // padded with zeros.
            if (serial.length() < 6) {
                // The number of zeros needed for padding.
                int zerosCount = 6 - serial.length();
                // A string consisted of zeros.
                String zerosString =
                        new String(new char[zerosCount]).replace("\0", "0");
                // Creation of the left padded serial number.
                serial = zerosString + serial;
            }
            // Creation of the part after the prefix.
            afterPrefix = year + serial;
        }
        // Creation of the control number.
        lccn = prefix + afterPrefix;
        // Sets the control number value to be returned.
        formatResult.setValue(lccn.trim());
        // Sets the validity value of the control number.
        formatResult.setIsValid(true);
        
        return formatResult;
    }     
    
    /**
     * Validates and formats a provided OCLC identifier and returns it
     * accompanied by a value indicating its validity. For more information
     * about the structure of the number visit the following address: <br>
     * https://www.oclc.org/batchload/controlnumber.en.html
     * @param oclc The Online Computer Library Center (OCLC) identifier to be
     * validated and formatted.
     * @param replacementData (Optional) The string replacements before
     * validation and formatting.
     * @return A string format result containing the validated and formatted
     * identifier and a value indicating its validity.
     */
    public StringFormatResult formatOCLC(String oclc,
            List<StringReplacementData> replacementData) {
        StringFormatResult formatResult = new StringFormatResult();
        
        if (oclc.length() == 0)
            return formatResult;
        
        // Replaces the control characters from the identifier.
        oclc = replaceControlChars(oclc, "");
        // String replacements will occur if replacement data have been
        // provided.
        if (replacementData != null)
            oclc = replaceUserDefinedStrings(oclc, replacementData);
        
        // Removes from it characters other than 0 to 9.
        oclc = oclc.replaceAll("[^0-9]", "");
        // Validity check for the OCLC.
        if (oclc.length() > 0) {
            // Sets the identifier value to be returned.
            formatResult.setValue(oclc);
            // Sets the validity value of the identifier.
            formatResult.setIsValid(true);
        }
        
        return formatResult;
    }
    
    /**
     * Validates and formats a provided PMID and returns it accompanied by a
     * value indicating its validity. For more information about the structure
     * of the identifier visit the following address: <br>
     * https://www.oclc.org/batchload/controlnumber.en.html
     * @param pmid The PubMed identifier (PMID) to be validated and formatted.
     * @param replacementData (Optional) The string replacements before
     * validation and formatting.
     * @return A string format result containing the validated and formatted
     * identifier and a value indicating its validity.
     */
    public StringFormatResult formatPMID(String pmid,
            List<StringReplacementData> replacementData) {
        StringFormatResult formatResult = new StringFormatResult();
        
        if (pmid.length() == 0)
            return formatResult;
        
        // Replaces the control characters from the identifier.
        pmid = replaceControlChars(pmid, "");
        // String replacements will occur if replacement data have been
        // provided.
        if (replacementData != null)
            pmid = replaceUserDefinedStrings(pmid, replacementData);
        
        // Removes from it characters other than 0 to 9.
        pmid = pmid.replaceAll("[^0-9]", "");
        // Validity check for the PMID.
        if (pmid.length() > 0) {
            // Sets the identifier value to be returned.
            formatResult.setValue(pmid);
            // Sets the validity value of the identifier.
            formatResult.setIsValid(true);
        }
        
        return formatResult;
    }
    
    /**
     * Validates and formats a provided title and returns it accompanied by a
     * value indicating its validity.
     * @param title The title to be validated and formatted.
     * @param replacementData (Optional) The string replacements before
     * validation and formatting.
     * @return A string format result containing the validated and formatted
     * title and a value indicating its validity.
     */
    public StringFormatResult formatTitle(String title,
            List<StringReplacementData> replacementData) {
        StringFormatResult formatResult = new StringFormatResult();
        
        if (title.length() == 0)
            return formatResult;
        
        // Replaces the control characters from the title with a space.
        title = replaceControlChars(title, " ");
        // String replacements will occur if replacement data have been
        // provided.
        if (replacementData != null)
            title = replaceUserDefinedStrings(title, replacementData);
        
        // Replaces the symbols from the title.
        title = replaceSymbols(title);
        // Validity check for the title.
        if (title.length() > 0) {
            // Sets the title value to be returned.
            formatResult.setValue(title.toLowerCase());
            // Sets the validity value of the title.
            formatResult.setIsValid(true);
        }

        return formatResult;
    }
    
    /**
     * Validates and formats a provided year string and returns it accompanied
     * by a value indicating its validity.
     * @param year The year string to be validated and formatted.
     * @param replacementData (Optional) The string replacements before
     * validation and formatting.
     * @return A string format result containing the validated and formatted
     * year and a value indicating its validity.
     */
    public StringFormatResult formatYear(String year,
            List<StringReplacementData> replacementData) {
        StringFormatResult formatResult = new StringFormatResult();
        
        // Replaces the control characters from the year string.
        year = replaceControlChars(year, "");
        // String replacements will occur if replacement data have been
        // provided.
        if (replacementData != null)
            year = replaceUserDefinedStrings(year, replacementData);
        
        // If a date representation has been provided, then it will be
        // splitted in order to retrieve the year.
        String[] yearParts = year.split("-");
        // Retrieval of the year part.
        year = yearParts[0];
        // Removes from it characters other than 0 to 9.
        year = year.replaceAll("[^0-9]", "");
        // Validity check for the year.
        if (year.length() == 4) {
            // Sets the year value to be returned.
            formatResult.setValue(year);
            // Sets the validity value of the year.
            formatResult.setIsValid(true);
        }
        
        return formatResult;
    }
    
    /**
     * Validates and formats a provided identifier string.
     * @param identifierString The identifier string to be validates and
     * formatted.
     * @param identifierType The type of the input string identifier.
     * @param stringFormatData The string format data for the identifier.
     * @return A string format result containing the validated and formatted
     * identifier and a value indicating its validity.
     */
    public StringFormatResult formatIdentifier(String identifierString,
            IdentifierType identifierType, StringFormatData stringFormatData) {
        StringFormatResult formatResult = new StringFormatResult();
        StringOperation stringOperation = new StringOperation();
        
        switch (identifierType) {
            case arXivID:
                if (stringFormatData != null)
                    formatResult =
                            stringOperation.formatArXivID(identifierString,
                            stringFormatData.getArxivIDReplacementDataList());
                else
                    formatResult =
                            stringOperation.formatArXivID(identifierString,
                            null);
                
                break;
            case DOI:
                if (stringFormatData != null)
                    formatResult = stringOperation.formatDOI(identifierString,
                            stringFormatData.getDOIReplacementDataList());
                else
                    formatResult = stringOperation.formatDOI(identifierString,
                            null);
                
                break;
            case ISBN:
                if (stringFormatData != null)
                    formatResult = stringOperation.formatISBN(identifierString,
                            stringFormatData.getISBNReplacementDataList());                    
                else
                    formatResult = stringOperation.formatISBN(identifierString,
                            null);
                
                break;
            case LCCN:
                if (stringFormatData != null)
                    formatResult = stringOperation.formatLCCN(identifierString,
                            stringFormatData.getLCCNReplacementDataList());
                else
                    formatResult = stringOperation.formatLCCN(identifierString,
                            null);
                
                break;
            case OCLC:
                if (stringFormatData != null)
                    formatResult = stringOperation.formatOCLC(identifierString,
                            stringFormatData.getOCLCReplacementDataList());
                else
                    formatResult = stringOperation.formatOCLC(identifierString,
                            null);
                    
                break;
            case PMID:
                if (stringFormatData != null)
                    formatResult = stringOperation.formatPMID(identifierString,
                            stringFormatData.getPMIDReplacementDataList());
                else
                    formatResult = stringOperation.formatPMID(identifierString,
                            null);
                
                break;
            case ISSN:
                if (stringFormatData != null)
                    formatResult = stringOperation.formatISSN(identifierString,
                            stringFormatData.getISSNReplacementDataList());
                else
                    formatResult = stringOperation.formatISSN(identifierString,
                            null);
                
                break;
            case JournalTitle:
                if (stringFormatData != null)
                    formatResult = stringOperation.formatTitle(identifierString,
                            stringFormatData.getTitleReplacementDataList());
                else
                    formatResult = stringOperation.formatTitle(identifierString,
                            null);
                
                break;  
        }
        
        return formatResult;
    }
}