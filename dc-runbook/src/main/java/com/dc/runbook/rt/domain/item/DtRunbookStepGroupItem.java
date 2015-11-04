package com.dc.runbook.rt.domain.item;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("StepGroup")
public class DtRunbookStepGroupItem extends DtRunbookItem {
    private List<DtRunbookItem> itemsList;

    public DtRunbookStepGroupItem() {
        this.type = DtRunbookItemType.StepGroup;
    }

    public DtRunbookStepGroupItem(int runbookId, int itemId, List<DtRunbookItem> itemsList) {
        super(runbookId, itemId, null, false);
        this.itemsList = itemsList;
        this.type = DtRunbookItemType.StepGroup;
    }

    public List<DtRunbookItem> getItemsList() {
        return itemsList;
    }

    public void setItemsList(List<DtRunbookItem> itemsList) {
        this.itemsList = itemsList;
    }
}
