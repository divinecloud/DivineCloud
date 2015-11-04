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
