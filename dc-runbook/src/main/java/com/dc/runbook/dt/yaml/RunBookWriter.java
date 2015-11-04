package com.dc.runbook.dt.yaml;

import com.dc.runbook.RunBookException;
import com.dc.runbook.dt.domain.Property;
import com.dc.runbook.dt.domain.RunBook;
import com.dc.runbook.dt.domain.RunBookStep;
import com.dc.runbook.dt.domain.item.*;
import com.dc.util.string.EnhancedStringBuilder;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.*;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.resolver.Resolver;

import java.io.File;
import java.io.FileWriter;

public class RunBookWriter {
    public static String COMMENT = "# This RunBook file generated using Divine Terminal. To download Free Edition of Divine Terminal go to www.divineterminal.com" + '\n';
    public static void write(RunBook runBook, File destination) throws RunBookException {
        setEmptyValuesToNull(runBook);
        String path = destination.getPath();
        path = path.replace("\\", "/");
        path = path.substring(0, path.lastIndexOf("/"));
        File folder = new File(path);
        try {
            folder.mkdirs();
            Representer representer = generateRepresenter();
            DumperOptions options = generateOptions();
            removeCarriageReturnChars(runBook);
            Yaml yaml = new Yaml(new Constructor(), representer, options, new CustomResolver());
            FileWriter fileWriter = new FileWriter(destination);
            fileWriter.write(COMMENT);
            yaml.dump(runBook, fileWriter);
        }
        catch(Throwable t) {
            throw new RunBookException("Error occurred while serializing the run-book : " + path, t);
        }
    }

	private static DumperOptions generateOptions() {
	    DumperOptions options = new DumperOptions();
	    options.setAllowUnicode(true);
	    options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
	    return options;
    }

	private static Representer generateRepresenter() {
	    Representer representer = new NullRepresenter();
	    representer.addClassTag(RunBook.class, new Tag("!RunBook"));
	    representer.addClassTag(RunBookStep.class, new Tag("!steps"));
	    representer.addClassTag(Property.class, new Tag("!properties"));
	    representer.addClassTag(CommandItem.class, new Tag("!CommandItem"));
	    representer.addClassTag(MultiCommandItem.class, new Tag("!MultiCommandItem"));
	    representer.addClassTag(ScriptItem.class, new Tag("!ScriptItem"));
	    representer.addClassTag(FileScriptItem.class, new Tag("!FileScriptItem"));
	    representer.addClassTag(PropertiesTransferItem.class, new Tag("!PropertiesTransferItem"));
	    representer.addClassTag(FileItem.class, new Tag("!FileItem"));
        representer.addClassTag(TextSaveItem.class, new Tag("!TextSaveItem"));
        representer.addClassTag(TextReplaceItem.class, new Tag("!TextReplaceItem"));
	    representer.addClassTag(RunBookReferenceItem.class, new Tag("!RunBookReferenceItem"));
	    representer.addClassTag(MultiOsCommandItem.class, new Tag("!MultiOsCommandItem"));
	    representer.getPropertyUtils().setSkipMissingProperties(true);
	    return representer;
    }

    public static String getYml(RunBook runBook) throws RunBookException {
    	StringBuilder sb = new StringBuilder();
    	sb.append(COMMENT);
        setEmptyValuesToNull(runBook);
        try {
            Representer representer = generateRepresenter();
            DumperOptions options = generateOptions();
            removeCarriageReturnChars(runBook);
            Yaml yaml = new Yaml(new Constructor(), representer, options, new CustomResolver());
            sb.append(yaml.dump(runBook));
        }
        catch(Throwable t) {
            throw new RunBookException("Error occurred while serializing the run-book to String.", t);
        }
        return sb.toString();
    }

    private static void removeCarriageReturnChars(RunBook runBook) {
        for(RunBookStep step : runBook.getSteps()) {
            if(step.getItemType() == ItemType.Script) {
                RunBookItem item = step.getItem();
                ScriptItem scriptItem = (ScriptItem)item;
                String script = scriptItem.getScript();
                script = removeCarriageReturnChars(script);
                scriptItem.setScript(script);
            }
        }
    }

    private static String removeCarriageReturnChars(String script) {
        EnhancedStringBuilder builder = new EnhancedStringBuilder(new StringBuilder(script));
        builder.replaceAll("\r\n", "\n");
        return builder.toString();
    }

    private static void setEmptyValuesToNull(RunBook runBook) {
        if(runBook.getAuthors() != null && runBook.getAuthors().trim().length() == 0) {
            runBook.setAuthors(null);
        }
        if(runBook.getDescription() != null && runBook.getDescription().trim().length() == 0) {
            runBook.setDescription(null);
        }
        if(runBook.getHardwareRequirements() != null && runBook.getHardwareRequirements().trim().length() == 0) {
            runBook.setHardwareRequirements(null);
        }
        if(runBook.getPreRequisite() != null && runBook.getPreRequisite().trim().length() == 0) {
            runBook.setPreRequisite(null);
        }
        if(runBook.getSuccessfulCompletionMessage() != null && runBook.getSuccessfulCompletionMessage().trim().length() == 0) {
            runBook.setSuccessfulCompletionMessage(null);
        }
        for(RunBookStep step : runBook.getSteps()) {
            if(step.getNodeSet() != null && step.getNodeSet().trim().length() == 0) {
                step.setNodeSet(null);
            }
            if(step.getItem() instanceof ScriptItem) {
                ScriptItem item = (ScriptItem)step.getItem();
                if(item.getFileName() != null && item.getFileName().trim().length() == 0) {
                    item.setFileName(null);
                }
                if(item.getInvokingProgram() != null && item.getInvokingProgram().trim().length() == 0) {
                    item.setInvokingProgram(null);
                }
            }
        }
    }

    public static class CustomResolver extends Resolver {

        protected void addImplicitResolvers() {
            addImplicitResolver(Tag.NULL, NULL, "~nN\0");
            addImplicitResolver(Tag.NULL, EMPTY, null);
            addImplicitResolver(Tag.BOOL, BOOL, "yYnNtTfFoO");
            addImplicitResolver(Tag.INT, INT, "-+0123456789");
            addImplicitResolver(Tag.FLOAT, FLOAT, "-+0123456789.");
        }
    }
    private static class NullRepresenter extends Representer {
        public NullRepresenter() {
            super();
            // null representer is exceptional and it is stored as an instance
            // variable.
            this.nullRepresenter = new RepresentNull();
        }
        @Override
        protected NodeTuple representJavaBeanProperty(Object javaBean, org.yaml.snakeyaml.introspector.Property property,
                                                      Object propertyValue, Tag customTag) {
            NodeTuple tuple = super.representJavaBeanProperty(javaBean, property, propertyValue,
                    customTag);
            Node valueNode = tuple.getValueNode();
            if (Tag.NULL.equals(valueNode.getTag())) {
                return null;// skip 'null' values
            }

            if (valueNode instanceof CollectionNode) {
                if (Tag.SEQ.equals(valueNode.getTag())) {
                    SequenceNode seq = (SequenceNode) valueNode;
                    if (seq.getValue().isEmpty()) {
                        return null;// skip empty lists
                    }
                }
                if (Tag.MAP.equals(valueNode.getTag())) {
                    MappingNode seq = (MappingNode) valueNode;
                    if (seq.getValue().isEmpty()) {
                        return null;// skip empty maps
                    }
                }
            }
            return tuple;
        }
        private class RepresentNull implements Represent {
            public Node representData(Object data) {
                // possible values are here http://yaml.org/type/null.html
                return representScalar(Tag.NULL, "");

            }
        }
    }
}
