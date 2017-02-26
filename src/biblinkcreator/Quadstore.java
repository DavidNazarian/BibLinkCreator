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
import org.openrdf.http.client.*;
import org.openrdf.http.protocol.UnauthorizedException;
import org.openrdf.repository.http.HTTPRepository;

/**
 * A class for the quadstore methods.
 * <br><br>
 * Author: David Nazarian
 * @author David Nazarian
 */
public class Quadstore {
    /**
     * Gets a value indicating if the connection to the server is authenticated.
     * @param serverURLString The URL of the server containing the server. 
     * @param authenticationData The authentication data for the connection.
     * @param logger The logger for the messages.
     * @return A value indicating if the connection to the server is
     * authenticated.
     */
    public boolean isConnectionAuthorized(String serverURLString,
            AuthenticationData authenticationData, Logger logger) {
        boolean authorized = true;
        
        if (logger == null)
            logger = new Logger(Logger.PrintType.None, "", false);
        
        // A Sesame client.
        SesameClientImpl client = new SesameClientImpl();
        // A Sesame session.
        SesameSession session;
        
        try {
            // Creates a Sesame session based on the server URL.
            session = client.createSesameSession(serverURLString);
            String username, password;
            
            if (authenticationData != null) {
                // Gets the username.
                username = authenticationData.getUsername();
                // Gets the password.
                password = authenticationData.getPassword();
                // Sets the credentials.
                session.setUsernameAndPassword(username, password);
            }
            
            // Tries to read the server protocol.
            session.getServerProtocol();
        }
        catch (UnauthorizedException ex) {
            logger.logMessage("Authorization failed for the server " +
                    serverURLString, Logger.MessageCategory.Error);
            authorized = false;
        }
        catch (Exception ex) {
            logger.logMessage(ex.getMessage(), Logger.MessageCategory.Error);
            authorized = false;
        } 
        finally {
            try {
                client.shutDown();
            }
            catch (Exception ex){
            }
        }
        
        return authorized;
    }
    
    /**
     * Sets authentication data for an HTTP repository.
     * @param authenticationData The authentication data.
     * @param repository The HTTP repository. 
     */
    public void setAuthentication(AuthenticationData authenticationData,
            HTTPRepository repository) {
        
        if (authenticationData != null) {
            // Gets the username.
            String username = authenticationData.getUsername();
            // Gets the password.
            String password = authenticationData.getPassword();
            // Gets the credentials.
            repository.setUsernameAndPassword(username, password);
        }
    }
}