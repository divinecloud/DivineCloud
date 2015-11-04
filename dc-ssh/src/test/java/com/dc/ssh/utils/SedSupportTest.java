package com.dc.ssh.utils;

import com.dc.support.KeyValuePair;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SedSupportTest {

    @Test
    public void testCreateSedScript() {
        List<KeyValuePair<String, String>> list = new ArrayList<>();
        KeyValuePair<String, String> pair1 = new KeyValuePair<>("key1", "value1");
        list.add(pair1);
        KeyValuePair<String, String> pair2 = new KeyValuePair<>("<key2>", "<config>\nvalue1</config>");
        list.add(pair2);
        String scriptCode = SedSupport.createSedScript(list, "/sample/file/path");
        System.out.println(scriptCode);

        String expetcedString = "sed -i.bak ' {\n" +
                "s/key1/value1/\n" +
                "s/<key2>/<config>\\nvalue1<\\/config>/\n" +
                " } ' /sample/file/path";
        assertEquals(expetcedString, scriptCode);
    }
}
