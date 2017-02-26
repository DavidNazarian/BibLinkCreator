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
 * A class for unique identifier conversion and validation.
 * <br><br>
 * Author: David Nazarian
 * @author David Nazarian
 */
public class UIDFunctionStore {
    /**
     * Determines if a provided ISBN string (containing only digits including X)
     * is valid.
     * @param isbn The ISBN string. 
     * @return A value indicating if the ISBN string is valid.
     */
    public boolean isISBNValid(String isbn) {
        int sum = 0;
        int weight;
        String isbnParts[];
        
        // Determines the type of the ISBN (10 or 13).
        switch (isbn.length()) {
            case 10:
                // Splits the digits of the ISBN10.
                isbnParts = isbn.split("");
                // Checks if the last digit is X.
                if (isbnParts[9].toUpperCase().equals("X"))
                    // Converts the last digit to 10.
                    isbnParts[9] = "10";
                
                for (int i = 0; i < 9; i++) {
                    // Each digit must be a positive integer.
                    if (isbnParts[i].matches("[0-9]") == false)
                        return false;
                    
                    // Weight calculation.
                    weight = 10 - i;
                    // Calculates the total sum.
                    sum += weight * Integer.parseInt(isbnParts[i]);
                }
                
                // The ISBN10 is valid if its last digit is equal to the
                // calculated value.
                return ((11 - (sum % 11)) % 11 ==
                        Integer.parseInt(isbnParts[9]));
            case 13:
                // Splits the digits of the ISBN13.
                isbnParts = isbn.split("");
                // Checks if the last digit is X (A valid ISBN13 is consisted
                // only of numbers).
                if (isbnParts[12].toUpperCase().equals("X"))
                    return false;
                
                for (int i = 0; i < 12; i++) {
                    // Each digit must be a positive integer.
                    if (isbnParts[i].matches("[0-9]") == false)
                        return false;
                    
                    // Weight calculation.
                    weight = (i % 2 == 0) ? 1 : 3;
                    // Calculates the total sum.
                    sum += weight * Integer.parseInt(isbnParts[i]);
                }
                
                // The ISBN13 is valid if its last digit is equal to the
                // calculated value
                return ((10 - (sum % 10)) % 10 ==
                        Integer.parseInt(isbnParts[12]));
            default:
                return false;
        }
    }
    
    /**
     * Gets the ISBN13 equivalent of a provided ISBN10.
     * @param isbn10 The ISBN10 string. 
     * @return The ISBN13 equivalent of the provided ISBN10.
     */
    public String getISBN13(String isbn10) {
        int sum = 0;
        int weight;
        String checkDigit;
        // The default prefix (978) of the ISBN13 and the digits of the ISBN10
        // except its last digit which is the check digit.
        String isbn13 = "978" + isbn10.substring(0, isbn10.length() - 1);
        // Splits the digits of the created string.
        String[] isbnParts = isbn13.split("");
        
        for (int i = 0; i < 12; i++) {
            // Weight calculation.
            weight = (i % 2 == 0) ? 1 : 3;
            // Calculates the total sum.
            sum += weight * Integer.parseInt(isbnParts[i]);
        }
        
        // Calculates the check digit for the ISBN13.
        checkDigit = Integer.toString((10 - (sum % 10)) % 10);
        // Creates the ISBN13.
        isbn13 += checkDigit;
        
        return isbn13;
    }
    
    /**
     * Determines if a provided ISSN string (containing only digits including X)
     * is valid.
     * @param issn The ISSN string. 
     * @return A value indicating if the ISSN string is valid.
     */
    public boolean isISSNValid(String issn) {
        int sum = 0;
        int weight;
        String issnParts[];
        // A valid ISSN is consisted of 8 digits.
        if (issn.length() != 8)
            return false;
        
        // Splits the digits of the ISSN.
        issnParts = issn.split("");
        // Checks if the last digit is X.
        if (issnParts[7].toUpperCase().equals("X"))
            // Converts the last digit to 10.
            issnParts[7] = "10";
        
        for (int i = 0; i < 7; i++) {
            // Each digit must be a positive integer.
            if (issnParts[i].matches("[0-9]") == false)
                return false;
            
            // Weight calculation.
            weight = 8 - i;
            // Calculates the total sum.
            sum += weight * Integer.parseInt(issnParts[i]);
        }
        
        // The ISSN is valid if its last digit is equal to the calculated value.
        return ((11 - (sum % 11)) % 11 == Integer.parseInt(issnParts[7]));
    }
    
}