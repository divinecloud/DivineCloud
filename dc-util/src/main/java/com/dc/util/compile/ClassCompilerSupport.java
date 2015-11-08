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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

public class ClassCompilerSupport {

    public static Class<?> compileAndLoadClass(final String name, final String code) throws CodeCompilationException {
        compile(name, code);
        return load(name, code);
    }

    public static Class<?> load(final String name, final String code) throws CodeCompilationException {
        ClassLoader loader;
        try {
            loader = new InMemoryBytesClassLoader(ClassCompilerSupport.class.getClassLoader(), compile(name, code));
        } catch (Exception e) {
            throw new CodeCompilationException("Class loader initialization Failed: ", e);
        }

        Class<?> c;
        String classDotName = name.replace('/', '.');
        try {
            c = Class.forName(classDotName, true, loader);
        } catch (ClassNotFoundException e) {
            throw new CodeCompilationException("Class " + classDotName + " not found", e);
        }
        return c;
    }

    public static byte[] compile(final String name, final String code) throws CodeCompilationException {

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        byte data[] = null;
        if (compiler == null) {
            throw new CodeCompilationException("Compiler instance is null. Possible reason: JDK tools.jar file not included in runtime classpath");
        }

        InMemoryJavaFileManager fileManager = new InMemoryJavaFileManager(compiler.getStandardFileManager(null, null, null));
        List<InMemorySourceJavaFileObject> compilationUnits = new ArrayList<InMemorySourceJavaFileObject>();
        compilationUnits.add(new InMemorySourceJavaFileObject(name + ".java", code));

        String classPath = ".";

        DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<JavaFileObject>();

        List<String> optionList = new ArrayList<String>();
        optionList.addAll(Arrays.asList("-classpath", classPath + File.pathSeparator + System.getProperty("java.class.path")));
        Boolean result = compiler.getTask(null, fileManager, diagnosticCollector, optionList, null, compilationUnits).call();

        if (!Boolean.TRUE.equals(result)) {
            throw new RuntimeException("Compilation Failed: " + diagnosticCollector.getDiagnostics());
        }
        try {
            data = fileManager.getBytes();
        } catch (IOException e) {
            throw new CodeCompilationException("Compilation Failed: " + diagnosticCollector.getDiagnostics(), e);
        }
        return data;
    }
}
