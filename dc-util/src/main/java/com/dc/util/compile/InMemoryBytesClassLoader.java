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

public class InMemoryBytesClassLoader extends ClassLoader {
    private byte[] bytes;

    /**
     * Construct the class loader with the bytes for the class to be loaded.
     * 
     * @param parent
     *            - parent class loader
     * @param bytes
     *            - class bytes
     */
    public InMemoryBytesClassLoader(ClassLoader parent, byte[] bytes) {
        super(parent);
        this.bytes = bytes;
    }

    @Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException {
        if (bytes == null) {
            throw new ClassNotFoundException(name);
        }
        return defineClass(name, bytes, 0, bytes.length);
    }
}
