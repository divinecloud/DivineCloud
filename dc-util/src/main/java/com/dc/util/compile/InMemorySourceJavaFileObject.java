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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.tools.SimpleJavaFileObject;

public class InMemorySourceJavaFileObject extends SimpleJavaFileObject {
    private String code;

    public InMemorySourceJavaFileObject(String fullClassName, final String code) throws CodeCompilationException {
        super(createUri(fullClassName), Kind.SOURCE);

        if (code == null || code.length() == 0) {
            throw new CodeCompilationException("Invalid arguments passed: code = " + code);
        }

        this.code = code;
    }

    private static URI createUri(String fullClassName) throws CodeCompilationException {
        if (fullClassName == null || fullClassName.length() == 0) {
            throw new CodeCompilationException("Invalid arguments passed: fullClassName = " + fullClassName);
        }

        try {
            return (new URI(fullClassName));
        } catch (final URISyntaxException e) {
            throw new CodeCompilationException("Invalid class name: " + fullClassName, e);
        }
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        return (code);
    }
}
