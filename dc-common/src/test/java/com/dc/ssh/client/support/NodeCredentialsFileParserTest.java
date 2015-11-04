package com.dc.ssh.client.support;

import com.dc.ssh.client.exec.vo.NodeCredentials;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class NodeCredentialsFileParserTest {

    @Test
    public void testFileParse() {

         // Parses the node details text. The text is expected in the following format:
         // STEP:<STEP>, ID:<NODE_DISPLAY_ID>, HOST:<HOST_NAME_OR_IP>, PORT:<PORT_NUMBER>, USERNAME:<USER_NAME>, PASSWORD:<PASSWORD>, PRIVATE_KEY:<PRIVATE_KEY>, PASS_PHRASE:<PASS_PHRASE>, JUMP_HOST:<JUMP_HOST>, AUTH_MODE:M, PASSCODE:<PASSCODE>


        String record1 = "STEP:3, ID:NODE_ID_3, HOST:SAMPLE.HOST.3, USERNAME:root, PASSWORD:welcome3";
        String record2 = "STEP:1, ID:NODE_ID_1, HOST:SAMPLE.HOST.1, PORT:356, USERNAME:ec2-user, PASSWORD:welcome1";
        String record3 = "STEP:2, ID:NODE_ID_2, HOST:SAMPLE.HOST.2, PORT:444, USERNAME:ubuntu, PASSWORD:welcome2";

        String nodeCredentialsText = record1 + '\n' + record2 + '\n' + record3;

        List<List<NodeCredentials>> result = NodeCredentialsFileParser.parse(nodeCredentialsText);

        assertNotNull(result);
        assertEquals(3, result.size());
        List<NodeCredentials> list1 = result.get(0);
        NodeCredentials nodeCred1 = list1.get(0);
        assertEquals("NODE_ID_1", nodeCred1.getId());
        assertEquals("SAMPLE.HOST.1", nodeCred1.getHost());
        assertEquals("ec2-user", nodeCred1.getUsername());
        assertEquals("welcome1", nodeCred1.getPassword());

        List<NodeCredentials> list2 = result.get(1);
        NodeCredentials nodeCred2 = list2.get(0);
        assertEquals("NODE_ID_2", nodeCred2.getId());
        assertEquals("SAMPLE.HOST.2", nodeCred2.getHost());
        assertEquals("ubuntu", nodeCred2.getUsername());
        assertEquals("welcome2", nodeCred2.getPassword());

        List<NodeCredentials> list3 = result.get(2);
        NodeCredentials nodeCred3 = list3.get(0);
        assertEquals("NODE_ID_3", nodeCred3.getId());
        assertEquals("SAMPLE.HOST.3", nodeCred3.getHost());
        assertEquals("root", nodeCred3.getUsername());
        assertEquals("welcome3", nodeCred3.getPassword());

    }
}
