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

package com.dc.ssh.client.sftp.stream;

import com.dc.ssh.batch.sftp.SftpMode;
import com.dc.ssh.client.sftp.SftpClientException;

public interface SftpStreamer {

    /**
     * Gets the Node display ID.
     *
     * @return unique node display ID.
     */
    public String id();

    /**
     * Gets the file transfer mode.
     *
     * @return file transfer mode.
     */
    public SftpMode mode();

    /**
     * Writes the file content to the remote server.
     *
     * @param fileContent file content bytes
     * @throws com.dc.ssh.client.sftp.SftpClientException - Gets thrown for any SSH related issues.
     */
    public void write(byte[] fileContent) throws SftpClientException;

    /**
     * Reads the file content from the remote server.
     *
     * @param buffer - byte array for data be read into.
     * @return total number of bytes read
     * @throws SftpClientException - Gets thrown for any SSH related issues.
     */
    public int read(byte[] buffer) throws SftpClientException;

    public void close() throws SftpClientException;

}