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

import java.io.*;
import java.text.*;
import java.util.Date;

/**
 * A message logging mechanism.
 * <br><br>
 * Author: David Nazarian
 * @author David Nazarian
 */
public class Logger {
    /**
     * An enumeration for the destination of the printed messages.
     */
    public enum PrintType {
        Console,
        File,
        All,
        None,
    }
    
    /**
     * An enumeration for the category of the printed messages.
     */
    public enum MessageCategory {
        Info,
        Warning,
        Error,
        None
    }
    
    // The destination of the printed messages.
    private PrintType printType;
    // The category of the printed messages.
    private String pathname;
    
    /**
     * @param printType The destination of the printed messages.
     * @param pathname The path (including the filename) of the file where the
     * messages will be logged.
     * @param appendFile A value indicating if the file will be appended or 
     * created.
     */
    public Logger(PrintType printType, String pathname, boolean appendFile) {
        this.printType = printType;
        this.pathname = pathname;
        
        try {
            if (appendFile == false) {
                File file = new File(pathname);
                // If a log file already exists, then it will be deleted.
                if (file.exists() && !file.isDirectory())
                    file.delete();
            }
        }
        catch (Exception ex) {
        }
    }
    
    /**
     * Logs a provided message.
     * @param message The message to be logged.
     * @param messageCategory The category of the message to be logged.
     */
    public void logMessage(String message, MessageCategory messageCategory) {
        if (messageCategory == MessageCategory.None)
            return;
        
        // A variable for the message to be logged.
        String formattedMessage;
        // Gets the current date.
        Date date = new Date();
        // A format for the logged date.
        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // Formats the date.
        String dateString = formatter.format(date);
        // The message line to be logged.
        formattedMessage = "[" + dateString + "] " +
                "[" + messageCategory.name() + "]\t" + message;
        
        switch (printType) {
            // The message will be logged in consolse.
            case Console:
                System.out.println(formattedMessage);
                break;
            // The message will be logged in a file.
            case File:
                printToFile(formattedMessage, pathname);
                break;
            // The message will be logged in the console and the file.
            case All:
                System.out.println(formattedMessage);
                printToFile(formattedMessage, pathname);
                break;
        }
    }
    
    /**
     * Logs a provided message in a file.
     * @param message The message to be logged.
     * @param pathname The path (including the filename) of the file where the
     * messages will be logged.
     */
    private void printToFile(String message, String pathname) {
        // The file where the message will be logged.
        File file = new File(pathname);
        // A writer for the file.
        try (FileWriter fileWriter = new FileWriter(file, true)) {
            // Writes the message line in the file.
            fileWriter.write(message + "\r\n");
        }
        catch (Exception ex) {
        }
    }
}