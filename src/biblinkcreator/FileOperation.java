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
import java.nio.file.*;
import java.util.List;
import org.apache.commons.io.FileUtils;

/**
 * A class providing basic file operations.
 * <br><br>
 * Author: David Nazarian
 * @author David Nazarian
 */
public class FileOperation {
    /**
     * Gets the text content of a provided file.
     * @param file The file providing the content.
     * @return The string content of the provided file.
     * @throws java.io.IOException
     * @throws java.io.FileNotFoundException
     * @throws java.io.UnsupportedEncodingException
     */
    public String getTextContent(File file)
            throws IOException, FileNotFoundException,
            UnsupportedEncodingException  {
        String textContent = "";
        // An input stream for the file.
        FileInputStream inputStream = new FileInputStream(file);
        // The file will be read by using UTF-8 encoding.
        InputStreamReader streamReader =
                new InputStreamReader(inputStream, "UTF8");
        
        try (BufferedReader bufferedReader = new BufferedReader(streamReader)) {
            // Reads the first line of the file.
            String line = bufferedReader.readLine();
            
            while (line != null) {
                // Appends the read content.
                textContent += line + "\n";
                // Reads the next line.
                line = bufferedReader.readLine();
            }
        }
        
        return textContent;
    }
    
    public void deleteTempFiles(List<Path> filePathList) {
        for (int i = 0; i < filePathList.size(); i++) {
            try {
                Files.delete(filePathList.get(i));
            }
            catch (Exception ex) {}
        }

        filePathList.clear();
    }
    
    /**
     * Tries to delete a provided file.
     * @param file The file to be deleted.
     */
    public void deleteTempFile(File file) {
        try {
            file.delete();
        }
        catch (Exception ex) {
        }
    }
    
    /**
     * Tries to delete a folder.
     * @param folderPath The path of the folder.
     */
    public void deleteTempFolder(Path folderPath) {
        try {
            FileUtils.deleteDirectory(new File(folderPath.toString()));
        }
        catch (Exception ex) {
        }
    }
}