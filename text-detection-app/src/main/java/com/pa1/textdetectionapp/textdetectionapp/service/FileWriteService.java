package com.pa1.textdetectionapp.textdetectionapp.service;

import lombok.extern.slf4j.Slf4j;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

@Slf4j
/**
 * Service class to handle file writing operations.
 */
public class FileWriteService {

    /**
     * Writes the contents of the provided map to a file.
     * Each entry is written as a key-value pair in the format: "key:value\n".
     *
     * @param mp The map containing the data to be written to the file.
     */
    public void fileWrite(Map<String, String> mp) {
        try {
            // Create a FileWriter for the file "ImageText.txt"
            FileWriter writer = new FileWriter("ImageText.txt");

            // Iterate through the map and write each key-value pair to the file
            Iterator<Map.Entry<String, String>> it = mp.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> pair = it.next();
                writer.write(pair.getKey() + ":" + pair.getValue() + "\n");
                it.remove(); // Remove the current entry from the map
            }
            writer.close(); // Close the FileWriter

            // Log the successful write operation
            log.info("Write operation complete, new file created ImageText.txt");
        } catch (IOException e) {
            // Log any exceptions that occur during the write operation
            log.error("Error occurred while writing file", e);
        }
    }
}
