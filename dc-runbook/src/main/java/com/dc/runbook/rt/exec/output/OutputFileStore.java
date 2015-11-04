package com.dc.runbook.rt.exec.output;

import com.dc.DcException;
import com.dc.runbook.rt.exec.RunbookItemStatus;
import com.dc.runbook.rt.exec.RunbookStatus;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class OutputFileStore implements OutputStore {

    private RunBookOutput	runBookOutput;
    private File destinationFile;
    private FileWriter fileWriter;
    private ObjectMapper writeMapper;

    public OutputFileStore(File destinationFile) {
        this.destinationFile = destinationFile;
        if(!destinationFile.exists()) {
            File parentFile = destinationFile.getParentFile();
            parentFile.mkdirs();
            try {
                destinationFile.createNewFile();
            } catch (IOException e) {
                throw new DcException("Cannot create the runbook output file : " + destinationFile.getAbsolutePath(), e);
            }
        }
        try {
            fileWriter = new FileWriter(destinationFile);
        } catch (IOException e) {
            throw new DcException("Cannot open writer for the runbook output file : " + destinationFile.getAbsolutePath(), e);
        }

        writeMapper = new ObjectMapper();
        runBookOutput = new RunBookOutput();
    }

    @Override
    public void create(RunbookStatus status) {
        runBookOutput.setExecutionId(status.getExecutionId());
        runBookOutput.setStartTime(status.getStartTime());
        runBookOutput.setNodesPerStep(status.getNodesMap());
        applyForUpdate(status);
        //writeToFile();
    }

    private void applyForUpdate(RunbookStatus execStatus) {
        runBookOutput.setComplete(execStatus.isComplete());
        runBookOutput.setStatus(execStatus.getState());
        runBookOutput.setEndTime(execStatus.getEndTime());
        runBookOutput.setStepExecutionStatusMap((convert(execStatus.getStatusMap())));
        runBookOutput.setOutputMap(execStatus.getOutputMap());
    }

    private Map<String, StepExecutionStatus> convert(Map<String, RunbookItemStatus> statusMap) {
        Map<String, StepExecutionStatus> stepExecutionStatusMap = new HashMap<>();
        if(statusMap != null) {
            Set<String> keysSet = statusMap.keySet();
            Iterator<String> iterator = keysSet.iterator();

            while (iterator.hasNext()) {
                String key = iterator.next();
                stepExecutionStatusMap.put(key, (StepExecutionStatus) statusMap.get(key));
            }
        }
        return stepExecutionStatusMap;
    }


    private void writeToFile() {
        writeMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
        String text = serialize(runBookOutput);
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

    @Override
    public void create(RunbookItemStatus itemStatus) {
        // TODO: to implement partial update later.
    }

    @Override
    public void update(RunbookItemStatus itemStatus) {
        // TODO: to implement partial update in repo.
    }

    @Override
    public void update(RunbookStatus execStatus) {
        applyForUpdate(execStatus);
        //writeToFile();
    }

    @Override
    public void done() {
        try {
            writeToFile();
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            throw new DcException("Error occurred while closing the runbook outfile : " + destinationFile.getAbsolutePath(), e);
        }

    }
}