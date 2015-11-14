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

package com.dc.util.compile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;

public class InMemoryJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {
    private InMemoryOutputJavaFileObject fileObject;

    public InMemoryJavaFileManager(final JavaFileManager fileManager) {
        super(fileManager);
    }

    @Override
    public JavaFileObject getJavaFileForOutput(final Location location, final String className, final Kind kind, final FileObject sibling) throws IOException {
        try {
            fileObject = new InMemoryOutputJavaFileObject(new URI(className), kind);
        } catch (final URISyntaxException e) {
            throw new IOException(e);
        }
        return fileObject;
    }

    @Override
    public JavaFileObject getJavaFileForInput(final Location location, final String className, final Kind kind) throws IOException {
        JavaFileObject result;
        result = fileObject;
        return (result);
    }

    public byte[] getBytes() throws IOException {
        InputStream classStream = fileObject.openInputStream();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int n;
        byte[] buf = new byte[4096];
        do {
            n = classStream.read(buf);
            if (n >= 0)
                bos.write(buf, 0, n);
        } while (n > 0);

        return (bos.toByteArray());
    }
}