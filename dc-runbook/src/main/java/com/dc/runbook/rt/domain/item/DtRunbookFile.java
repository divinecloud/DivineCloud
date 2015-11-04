package com.dc.runbook.rt.domain.item;

import com.dc.runbook.dt.domain.item.DownloadPrependType;
import com.dc.runbook.dt.domain.item.TransferType;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("File")
public class DtRunbookFile extends DtRunbookItem {
	private String	     source;
	private String	     destination;
	private TransferType	transferType;
    private DownloadPrependType prependType;

	public DtRunbookFile() {
		this.type = DtRunbookItemType.File;
	}

	public DtRunbookFile(int runbookId, int itemId, String source, String destination, TransferType transferType, DownloadPrependType prependType) {
		super(runbookId, itemId, null, false);
		this.source = source;
		this.destination = destination;
		this.type = DtRunbookItemType.File;
		this.transferType = transferType;
        this.prependType = prependType;
	}

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
}
