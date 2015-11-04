package com.dc.ssh.utils;

import org.junit.Test;

public class TextSupportTest {

    @Test
    public void testCreateSaveTextScript() {
        String script = TextSupport.generateSaveTextScript("/some/file/path", "Sample Text\nMore text on 2nd line\neven more text", false);
        System.out.println(script);
    }

}
