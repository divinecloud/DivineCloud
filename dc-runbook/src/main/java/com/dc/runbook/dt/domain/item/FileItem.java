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

package com.dc.runbook.dt.domain.item;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("File")
public class FileItem extends RunBookItem {
    private String source;
    private String destination;
    private TransferType transferType;
    private DownloadPrependType prependType;
    
    public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public TransferType getTransferType() {
		return transferType;
	}

	public void setTransferType(TransferType transferType) {
		this.transferType = transferType;
	}

	public DownloadPrependType getPrependType() {
		return prependType;
	}

	public void setPrependType(DownloadPrependType prependType) {
		this.prependType = prependType;
	}

	@Override
    public String toString() {
        return "FileItem{" +
                "transferType='" + transferType + '\'' + "\n" + 
                "prependType='" + prependType + '\'' + "\n" + 
                "source='" + source + '\'' + "\n" + 
                "destination='" + destination + '\'' +
                '}';
    }
}
