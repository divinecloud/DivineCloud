/*
 * Copyright (C) 2014 Divine Cloud Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dc.ssh.client.support;

import com.dc.ssh.client.exec.vo.Credential;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CredentialsFileParserTest {

    @Test
    public void testFileParse() {

        // Parses the credentials text. The text is expected in the following format:
        // NAME:<CREDENTIAL_NAME>, USERNAME:<OS_USER_NAME>, PASSWORD:<PASSWORD>, PRIVATE_KEY:<>PRIVATE_KEY>, PASS_PHRASE:<PASS_PHRASE>, JUMP_HOST:<JUMP_HOST>


        String record1 = "NAME:CRED_1, USERNAME:root, PASSWORD:welcome1";
        String record2 = "NAME:CRED_2, USERNAME:ec2-user, PRIVATE_KEY:/tmp/private-key.txt, PASS_PHRASE:samplePassPhrase";

        String nodeCredentialsText = record1 + '\n' + record2;

        Map<String, Credential> result = CredentialsFileParser.parse(nodeCredentialsText);

        assertNotNull(result);
        assertEquals(2, result.size());
        Credential cred1 = result.get("CRED_1");
        assertEquals("CRED_1", cred1.getName());
        assertEquals("root", cred1.getUserName());
        assertEquals("welcome1", cred1.getPassword());

        Credential cred2 = result.get("CRED_2");
        assertEquals("CRED_2", cred2.getName());
        assertEquals("ec2-user", cred2.getUserName());
        assertEquals("/tmp/private-key.txt", cred2.getPrivateKey());
        assertEquals("samplePassPhrase", cred2.getPassPhrase());

    }

}
