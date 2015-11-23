package com.dc.support;

import com.dc.DcException;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ExecOutputFileStore {
    private ExecutionOutput output;
    private File destinationFile;
    private ObjectMapper writeMapper;
    private FileWriter fileWriter;

    public ExecOutputFileStore(ExecutionOutput output, File destinationFile) {
        this.output = output;
        this.destinationFile = destinationFile;

        if(!destinationFile.exists()) {
            File parentFile = destinationFile.getParentFile();
            parentFile.mkdirs();
            try {
                destinationFile.createNewFile();
            } catch (IOException e) {
                throw new DcException("Cannot create the execution output file : " + destinationFile.getAbsolutePath(), e);
            }
        }
        try {
            fileWriter = new FileWriter(destinationFile);
        } catch (IOException e) {
            throw new DcException("Cannot open writer for the execution output file : " + destinationFile.getAbsolutePath(), e);
        }

        writeMapper = new ObjectMapper();

    }

    public void store() {
        try {
            writeToFile();
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            throw new DcException("Error occurred while closing the execution outfile : " + destinationFile.getAbsolutePath(), e);
        }

    }

    private void writeToFile() {
        writeMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
        String text = serialize(output);
        try {
            fileWriter.write(text);
        } catch (IOException e) {
            throw new DcException("Unable to write to file " + destinationFile.getAbsolutePath(), e);
        }
    }

    private String serialize(Object object) throws DcException {
        StringBuilder builder = new StringBuilder();
        try {
            builder.append(writeMapper.writeValueAsString(object));

        }
        catch (JsonGenerationException e) {
            throw new DcException("JsonGenerationException occurred while serializing object : " + object, e);
        }
        catch (JsonMappingException e) {
            throw new DcException("JsonMappingException occurred while serializing object : " + object, e);
        }
        catch (IOException e) {
            throw new DcException("IOException occurred while serializing object: " + object, e);
        }
        return builder.toString();

    }
}
