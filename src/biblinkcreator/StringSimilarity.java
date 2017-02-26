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

import biblinkcreator.DataStructure.*;
import java.util.*;

/**
 * A class for the string similarity measures.
 * <br><br>
 * Author: David Nazarian
 * @author David Nazarian
 */
public class StringSimilarity {
    /**
     * An enumeration for the string similarity measures.
     */
    public enum SimilarityType {
        Cosine,
        Dice,
        Jaccard,
        Overlap
    }
    
    /**
     * An enumeration for string segmentation types.
     */
    public enum ShingleType {
        WORD,
        CHAR
    }
    
    /**
     * Gets a similarity coefficient ranging from 0 (no similarity) to 1
     * (absolute similarity) for the provided input strings based on a
     * selected string similarity measure and its parameters.
     * @param stringA The string to be compared.
     * @param stringB The string to be compared.
     * @param similaritySelector The string similarity measure and its
     * parameters.
     * @return The similarity coefficient of the input strings.
     */
    public double getCoefficient(String stringA, String stringB,
            StringSimilaritySelector similaritySelector) {
        double coefficient = 0.0;
        // A shingle set for stringA.
        Set<String> shingleSetA = getShingleSet(stringA,
                similaritySelector.getShingleSize(),
                similaritySelector.getShingleType());
        // A shingle set for stringB.
        Set<String> shingleSetB = getShingleSet(stringB,
                similaritySelector.getShingleSize(),
                similaritySelector.getShingleType());
        // A set for the union of the shinle sets.
        Set<String> stringUnionSet;
        // A set for the intersection of the shingle sets.
        Set<String> stringIntersectionSet = new HashSet<>(shingleSetA);
        stringIntersectionSet.retainAll(shingleSetB);
        
        switch (similaritySelector.getSimilarityType()) {
            case Cosine:
                // The calculation for the cosine coefficient.
                coefficient = (double) stringIntersectionSet.size() /
                        (Math.sqrt(shingleSetA.size() * shingleSetB.size()));
                break;
            case Dice:
                // The calculation for the Dice coefficient.
                coefficient = (double) 2 * stringIntersectionSet.size() /
                        (shingleSetA.size() + shingleSetB.size());
                break;
            case Jaccard:
                // Creates the union of the shingle sets.
                stringUnionSet = new HashSet<>(shingleSetA);
                stringUnionSet.addAll(shingleSetB);
                
                // The calculation for the Jaccard coefficient.
                coefficient = (double) stringIntersectionSet.size() /
                        stringUnionSet.size();
                // An alternative calculation for the Jaccard coefficient.
                //coefficient = (double) strIntersectionSet.size() /
                //      (strASet.size() + strBSet.size() -
                //      strIntersectionSet.size());
                break;
            case Overlap:
                // The calculation for the Overlap coefficient.
                coefficient = (double) stringIntersectionSet.size() /
                        Math.min(shingleSetA.size(), shingleSetB.size());
                break;
        }
        
        return coefficient;
    }
    
    /**
     * Creates a shingle set based on a provided string and parameters.
     * @param string The string for the shingle set creation.
     * @param shingleSize The number of words or characters to be used to create
     * each shingle.
     * @param shingleType The shingle type to be used (characters or words).
     * @return A shingle set.
     */
    private Set<String> getShingleSet(String string, int shingleSize,
            ShingleType shingleType) {
        Set<String> shingleSet = new HashSet<>();
        String shingle = "";
        int length;
        
        switch (shingleType) {
            // Word level shingles.
            case WORD:
                // Splits the words contained in the string.
                String[] words = string.split(" ");
                // The upper limit for the loop.
                length = words.length - shingleSize;
                
                for (int i = 0; i <= length; i++) {
                    // Consecutive words to be used for each shingle.
                    for (int j = 0; j < shingleSize; j++)
                        shingle += words[i + j] + " ";
                    
                    // Creates the shingle.
                    shingle = shingle.substring(0, shingle.length() - 1);
                    // Adds the created shingle into the result set.
                    shingleSet.add(shingle);
                    shingle = "";                    
                }
                
                break;
            // Character level shingles.
            case CHAR:
                // The upper limit for the loop.
                length = string.length() - shingleSize;
                
                for (int i = 0; i <= length; i++) {
                    // Creates the shingle.
                    shingle = string.substring(i, i + shingleSize);
                    // Adds the created shingle into the result set.
                    shingleSet.add(shingle);
                }
                
                break;
        }
        
        return shingleSet;
    }
}