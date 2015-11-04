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

import com.dc.api.runbook.RunBookApi;
import com.dc.runbook.dt.domain.Property;
import com.dc.runbook.dt.domain.RunBook;
import com.dc.runbook.rt.domain.DtProperty;
import com.dc.ssh.client.exec.vo.NodeCredentials;
import com.dc.util.condition.ConditionalBarrier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class RunBookApiTest {

    @Test
    public void testRunbookExecute() {

        RunBookApi api = ApiBuilder.buildRunBookApi(5);
        NodeCredentials nodeCredentials1 = NodeCredentialsGenerator.generateServer1Credentials();
        NodeCredentials nodeCredentials2 = NodeCredentialsGenerator.generateServer2Credentials();

        RunBook runBook = SampleRunBookGenerator.createRunBook();

        List<NodeCredentials> step1List = new ArrayList<>();
        List<NodeCredentials> step2List = new ArrayList<>();

        step1List.add(nodeCredentials1);
        step2List.add(nodeCredentials2);
        List<List<NodeCredentials>> nodesPerStep = new ArrayList<>();
        nodesPerStep.add(step1List);
        nodesPerStep.add(step2List);
        String blockingId = "" + System.nanoTime();
        ConditionalBarrier<String> barrier = new ConditionalBarrier<>();
        SampleRunBookCallBack callback = new SampleRunBookCallBack(barrier, blockingId);
        api.execute(nodesPerStep, runBook, callback, null);
        barrier.block(blockingId);
        assertTrue(callback.getOutputData().contains("Sample Script"));
    }


    @Test
    public void testRunbookExecuteWithProperties() {

        RunBookApi api = ApiBuilder.buildRunBookApi(5);
        NodeCredentials nodeCredentials1 = NodeCredentialsGenerator.generateServer1Credentials();
        NodeCredentials nodeCredentials2 = NodeCredentialsGenerator.generateServer2Credentials();

        RunBook runBook = SampleRunBookGenerator.createRunBookWithProperties();

        List<NodeCredentials> step1List = new ArrayList<>();
        List<NodeCredentials> step2List = new ArrayList<>();

        step1List.add(nodeCredentials1);
        step2List.add(nodeCredentials2);
        List<List<NodeCredentials>> nodesPerStep = new ArrayList<>();
        nodesPerStep.add(step1List);
        nodesPerStep.add(step2List);
        String blockingId = "" + System.nanoTime();
        ConditionalBarrier<String> barrier = new ConditionalBarrier<>();
        SampleRunBookCallBack callback = new SampleRunBookCallBack(barrier, blockingId);
        List<DtProperty> propertyList = new ArrayList<>();
        DtProperty dtProperty1 = new DtProperty();
        Property property1 = new Property();
        property1.setName("$PROP1");
        dtProperty1.setStepProperty(property1);
        String property1Val = "propertyOne";
        dtProperty1.setValue(property1Val);
        propertyList.add(dtProperty1);

        DtProperty dtProperty2 = new DtProperty();
        Property property2 = new Property();
        property2.setName("$PROP2");
        dtProperty2.setStepProperty(property2);
        String property2Val = "propertyTwo";
        dtProperty2.setValue(property2Val);
        propertyList.add(dtProperty2);

        String execId = api.execute(nodesPerStep, runBook, callback, propertyList);
        System.out.println(execId);
        barrier.block(blockingId);

        assertTrue(callback.getOutputData().contains("Sample Script"));
        assertTrue(callback.getOutputData().contains(property1Val));
        assertTrue(callback.getOutputData().contains(property2Val));

    }
}