package com.dc.runbook.dt.domain.item;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(  
    use = JsonTypeInfo.Id.NAME,  
    include = JsonTypeInfo.As.PROPERTY,  
    property = "type")  
@JsonSubTypes({  
    @Type(value = CommandItem.class, name = "Command"),  
    @Type(value = MultiCommandItem.class, name = "MultiCommand"),  
    @Type(value = ScriptItem.class, name = "Script"),
    @Type(value = FileScriptItem.class, name = "FileScript"),
    @Type(value = FileItem.class, name = "File"),
    @Type(value = RunBookReferenceItem.class, name = "RunBook"),
    @Type(value = MultiOsCommandItem.class, name = "MultiOsCommand"),  
    @Type(value = MultiScriptCommandItem.class, name = "MultiScriptCommand"),
    @Type(value = TextSaveItem.class, name = "TextSave"),  
    @Type(value = TextReplaceItem.class, name = "TextReplace"),  
    @Type(value = PropertiesTransferItem.class, name = "PropertiesTransfer")
 }) 
public abstract class RunBookItem {
    protected String description;
    protected String preRequisite;
    protected List<String> answers;
    protected boolean reboot;
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPreRequisite() {
        return preRequisite;
    }

    public void setPreRequisite(String preRequisite) {
        this.preRequisite = preRequisite;
    }

	public List<String> getAnswers() {
		return answers;
	}

	public void setAnswers(List<String> answers) {
		this.answers = answers;
	}

	public boolean isReboot() {
		return reboot;
	}

	public void setReboot(boolean reboot) {
		this.reboot = reboot;
	}

}
