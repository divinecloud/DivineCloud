package com.dc.node;

import java.util.ArrayList;
import java.util.List;

public class NodeGroupInfoList extends ArrayList<NodeGroupInfo> {

	private static final long	serialVersionUID	= 1L;

	// marker class for mybatis type handler.

	public NodeGroupInfoList() {

	}

	public NodeGroupInfoList(List<NodeGroupInfo> list) {
		this.addAll(list);
	}

}
