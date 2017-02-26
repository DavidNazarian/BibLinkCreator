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

/**
 * A class for the structure (vocabulary) of the saved data or links.
 * <br><br>
 * Author: David Nazarian
 * @author David Nazarian
 */
public class Schema {
    private String namespace;
    private final String propertyPath;
    private final String graphPath;
    private final String titlePath;
    private final String yearPath;
    
    /**
     * @param namespace The base URL of the data structure.
     */
    public Schema(String namespace) {
        this.namespace = namespace;
        
        if (namespace != null && namespace.length() > 0 && 
                !namespace.substring(namespace.length() - 1).equals("/"))
            this.namespace += "/";
        
        this.graphPath = namespace + "/graph/";
        this.propertyPath = namespace + "/property/";
        this.titlePath = namespace + "/property/title";
        this.yearPath = namespace + "/property/year";
    }
    
    /**
     * @return The base URL of the data structure.
     */
    public String getNamespace() {
        return namespace;
    }
    
    /**
     * @return The URL of the property.
     */
    public String getPropertyPath() {
        return propertyPath;
    }
    
    /**
     * @return The URL of the graph.
     */
    public String getGraphPath() {
        return graphPath;
    }
    
    /**
     * @return The URL of the title.
     */
    public String getTitlePath() {
        return titlePath;
    }
    
    /**
     * @return The URL of the year.
     */
    public String getYearPath() {
        return yearPath;
    }
    
    /**
     * Gets the URI string of the named graph containing the extracted,
     * processed and saved data of a repository.
     * @param repositoryName The name (alias) of the repository.
     * @return The URI string of the named graph containing the extracted,
     * processed and saved data of a repository.
     */
    public String getDataGraphURIString(String repositoryName) {
        String graphURIString = graphPath +
                repositoryName.toLowerCase().replace(" ", "_");
        return graphURIString;
    }

    /**
     * Gets a string that can be used to filter the link triples from a named
     * graph by specifying the link source repository name (alias).
     * @param repositoryAName The name (alias) of the link source repository.
     * @return The string that can be used to filter the link triples.
     */
    public String getLinksGraphURIString(String repositoryAName) {
        String graphURIString = graphPath +
                repositoryAName.toLowerCase().replace(" ", "_") + "_to_";
        return graphURIString;
    }
    
    /**
     * Gets a string that can be used to filter the link triples from a named
     * graph by specifying the link source and link destination repository names
     * (aliases).
     * @param repositoryAName The name (alias) of the link source repository.
     * @param repositoryBName The name (alias) of the link destination
     * repository.
     * @return The string that can be used to filter the link triples.
     */
    public String getLinksGraphURIString(String repositoryAName,
            String repositoryBName) {
        String graphURIString = graphPath +
                repositoryAName.toLowerCase().replace(" ", "_") +
                "_to_" + repositoryBName.toLowerCase().replace(" ", "_");
        return graphURIString;
    }
    
    /**
     * Gets a string that can be used to filter the link triples from a named
     * graph by specifying the link source, the link destination repository
     * names (aliases) and the identifier used to create the links.
     * @param repositoryAName The name (alias) of the link source repository.
     * @param repositoryBName The name (alias) of the link destination
     * repository.
     * @param identifierType The identifier used to create the links.
     * @return The string that can be used to filter the link triples.
     */
    public String getLinksGraphURIString(String repositoryAName,
            String repositoryBName, IdentifierType identifierType) {
        Identifier identifier = new Identifier();
        String identifierVariableName = identifier.getIdentifierVariableName(
                identifierType).toLowerCase();
        
        String graphURIString = graphPath +
                repositoryAName.toLowerCase().replace(" ", "_") +
                "_to_" + repositoryBName.toLowerCase().replace(" ", "_") +
                "_" + identifierVariableName + "_links";
        return graphURIString;
    }
}