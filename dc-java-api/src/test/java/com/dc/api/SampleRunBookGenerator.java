/*
 *
 *  * Copyright (C) 2014 Divine Cloud Inc.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.dc.api;


import com.dc.runbook.dt.domain.RunBook;
import com.dc.runbook.dt.domain.RunBookStep;
import com.dc.runbook.dt.domain.item.CommandItem;
import com.dc.runbook.dt.domain.item.ItemType;
import com.dc.runbook.dt.domain.item.ScriptItem;
import com.dc.ssh.client.exec.cmd.script.ScriptLanguage;

import java.util.ArrayList;
import java.util.List;

public class SampleRunBookGenerator {

    public static RunBook createRunBook() {
        CommandItem item1 = new CommandItem();
        item1.setCommand("whoami");
        item1.setDescription("Sample Command Item");
        RunBookStep step1 = new RunBookStep();
        step1.setItem(item1);
        step1.setItemType(ItemType.Command);

        ScriptItem item2 = new ScriptItem();
        item2.setScript("echo Sample Script;" + " echo $1; " + "echo $2");
        item2.setArguments("arg1 arg2");
        item2.setLanguage(ScriptLanguage.Shell);
        item2.setInvokingProgram("/bin/bash");
        RunBookStep step2 = new RunBookStep();
        step2.setItem(item2);
        step2.setItemType(ItemType.Script);

        List<RunBookStep> stepsList = new ArrayList<>();
        stepsList.add(step1);
        stepsList.add(step2);
        RunBook runBook = new RunBook();
        runBook.setSteps(stepsList);
        runBook.setName("Sample RunBook 1");
        runBook.setVersion("1.0");
        return runBook;
    }

    public static RunBook createRunBookWithGeneratedProperties() {

        CommandItem item1 = new CommandItem();
        item1.setCommand("whoami");
        item1.setDescription("Sample Command Item");
        RunBookStep step1 = new RunBookStep();
        step1.setItem(item1);
        step1.setItemType(ItemType.Command);

        ScriptItem item2 = new ScriptItem();
        item2.setScript("echo '$GENERATED_PROP=sample_generated_property' > /tmp/props_file");
        item2.setLanguage(ScriptLanguage.Shell);
        item2.setInvokingProgram("/bin/bash");
        RunBookStep step2 = new RunBookStep();
        step2.setItem(item2);
        step2.setItemType(ItemType.Script);
        step2.setGeneratedPropertiesFilePath("/tmp/props_file");


        ScriptItem item3 = new ScriptItem();
        item3.setScript("echo $GENERATED_PROP");
        item3.setLanguage(ScriptLanguage.Shell);
        item3.setInvokingProgram("/bin/bash");
        RunBookStep step3 = new RunBookStep();
        step3.setItem(item3);
        step3.setItemType(ItemType.Script);
        step3.setReplaceProperties(true);

        List<RunBookStep> stepsList = new ArrayList<>();
        stepsList.add(step1);
        stepsList.add(step2);
        stepsList.add(step3);
        RunBook runBook = new RunBook();
        runBook.setSteps(stepsList);
        runBook.setName("Sample RunBook 1");
        runBook.setVersion("1.0");
        return runBook;
    }

    public static RunBook createLongRunningRunBook() {
        CommandItem item1 = new CommandItem();
        item1.setCommand("whoami");
        item1.setDescription("Sample Command Item");
        RunBookStep step1 = new RunBookStep();
        step1.setItem(item1);
        step1.setItemType(ItemType.Command);

        ScriptItem item2 = new ScriptItem();
        item2.setScript("echo Sample Script;" + "sleep 60; echo $1; " + "echo $2");
        item2.setArguments("arg1 arg2");
        item2.setLanguage(ScriptLanguage.Shell);
        item2.setInvokingProgram("/bin/bash");
        RunBookStep step2 = new RunBookStep();
        step2.setItem(item2);
        step2.setItemType(ItemType.Script);

        List<RunBookStep> stepsList = new ArrayList<>();
        stepsList.add(step1);
        stepsList.add(step2);
        RunBook runBook = new RunBook();
        runBook.setSteps(stepsList);
        runBook.setName("Sample RunBook 1");
        runBook.setVersion("1.0");
        return runBook;
    }

    public static RunBook createRunBookWithProperties() {
        CommandItem item1 = new CommandItem();
        item1.setCommand("whoami");
        item1.setDescription("Sample Command Item");
        RunBookStep step1 = new RunBookStep();
        step1.setItem(item1);
        step1.setItemType(ItemType.Command);

        ScriptItem item2 = new ScriptItem();
        item2.setScript("echo Sample Script;" + " echo $1; " + "echo $2; " + "echo $PROP1 $PROP2");
        item2.setArguments("arg1 arg2");
        item2.setLanguage(ScriptLanguage.Shell);
        item2.setInvokingProgram("/bin/bash");
        RunBookStep step2 = new RunBookStep();
        step2.setItem(item2);
        step2.setItemType(ItemType.Script);
        step2.setReplaceProperties(true);
        List<RunBookStep> stepsList = new ArrayList<>();
        stepsList.add(step1);
        stepsList.add(step2);
        RunBook runBook = new RunBook();
        runBook.setSteps(stepsList);
        runBook.setName("Sample RunBook 1");
        runBook.setVersion("1.0");
        return runBook;
    }

    public static RunBook createRunBookWithTransientNodesSupport() {

        String transientHost = TestSupport.getProperty("transient.server1.host");
        String dynamicTag = "DTAG1";
        String nodesImportFilePath = "/tmp/nodes-import.txt";
        CommandItem item1 = new CommandItem();
        item1.setCommand("whoami");
        item1.setDescription("Sample Command Item");
        RunBookStep step1 = new RunBookStep();
        step1.setItem(item1);
        step1.setItemType(ItemType.Command);

        ScriptItem item2 = new ScriptItem();
        item2.setScript("echo \"HOST:" + transientHost + ",ID:" + transientHost + ",NAME:OnDemandInstance1,UNIQUE_ID:" + transientHost + ",TEMP:Y,CREDENTIALS_NAME:" + "transient_node_password" + ",DYNAMIC_TAGS:" + dynamicTag + "\" > " + nodesImportFilePath + "\n");
        item2.setArguments("arg1 arg2");
        item2.setLanguage(ScriptLanguage.Shell);
        item2.setInvokingProgram("/bin/bash");
        RunBookStep step2 = new RunBookStep();
        step2.setItem(item2);
        step2.setItemType(ItemType.Script);
        step2.setNodesImportFilePath(nodesImportFilePath);

        ScriptItem item3 = new ScriptItem();
        item3.setScript("hostname");
        item3.setLanguage(ScriptLanguage.Shell);
        item3.setInvokingProgram("/bin/bash");
        RunBookStep step3 = new RunBookStep();
        step3.setItem(item3);
        step3.setItemType(ItemType.Script);
        List<String> tagsList = new ArrayList<>();
        tagsList.add(dynamicTag);
        step3.setDynamicNodeTags(tagsList);

        List<RunBookStep> stepsList = new ArrayList<>();
        stepsList.add(step1);
        stepsList.add(step2);
        stepsList.add(step3);
        RunBook runBook = new RunBook();
        runBook.setSteps(stepsList);
        runBook.setName("Sample RunBook 1");
        runBook.setVersion("1.0");
        return runBook;
    }

}
