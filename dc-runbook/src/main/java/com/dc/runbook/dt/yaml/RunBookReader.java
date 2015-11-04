package com.dc.runbook.dt.yaml;

import com.dc.runbook.RunBookException;
import com.dc.runbook.dt.domain.Property;
import com.dc.runbook.dt.domain.RunBook;
import com.dc.runbook.dt.domain.RunBookStep;
import com.dc.runbook.dt.domain.item.*;
import com.dc.util.file.FileSupport;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.io.IOException;

public class RunBookReader {


    public static RunBook read(String runBookYaml) throws RunBookException {
        Constructor constructor = new Constructor();
        constructor.addTypeDescription(new TypeDescription(RunBook.class, new Tag("!RunBook")));
        constructor.addTypeDescription(new TypeDescription(RunBookStep.class, new Tag("!steps")));
        constructor.addTypeDescription(new TypeDescription(Property.class, new Tag("!properties")));
        constructor.addTypeDescription(new TypeDescription(CommandItem.class, new Tag("!CommandItem")));
        constructor.addTypeDescription(new TypeDescription(MultiCommandItem.class, new Tag("!MultiCommandItem")));
        constructor.addTypeDescription(new TypeDescription(ScriptItem.class, new Tag("!ScriptItem")));
        constructor.addTypeDescription(new TypeDescription(FileScriptItem.class, new Tag("!FileScriptItem")));
        constructor.addTypeDescription(new TypeDescription(PropertiesTransferItem.class, new Tag("!PropertiesTransferItem")));
        constructor.addTypeDescription(new TypeDescription(RunBookReferenceItem.class, new Tag("!RunBookReferenceItem")));
        constructor.addTypeDescription(new TypeDescription(FileItem.class, new Tag("!FileItem")));
        constructor.addTypeDescription(new TypeDescription(TextSaveItem.class, new Tag("!TextSaveItem")));
        constructor.addTypeDescription(new TypeDescription(TextReplaceItem.class, new Tag("!TextReplaceItem")));
        constructor.addTypeDescription(new TypeDescription(MultiOsCommandItem.class, new Tag("!MultiOsCommandItem")));
        Representer representer = new Representer();
        representer.getPropertyUtils().setSkipMissingProperties(true);
        Yaml yaml = new Yaml(constructor, representer);

        return (RunBook) yaml.load(runBookYaml);
    }

    public static RunBook read(File runBookFile) throws RunBookException {
        String runBookYaml;
        try {
            runBookYaml = new String(FileSupport.readFile(runBookFile.getAbsolutePath()));
        } catch (IOException e) {
            throw new RunBookException(e);
        }
        return read(runBookYaml);
    }
}
