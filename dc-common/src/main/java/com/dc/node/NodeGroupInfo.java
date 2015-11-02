package com.dc.node;

public class NodeGroupInfo {
	private String	id;
	private String	displayName;

	public NodeGroupInfo() {

	}

	public NodeGroupInfo(String id, String displayName) {
		this.id = id;
		this.displayName = displayName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(displayName);
		if (!displayName.equals(id)) {
			sb.append(" (").append(id).append(") ");
		}
		return sb.toString();
	}

}
