package com.dc.runbook.rt.domain;

public class NodeDto {
	private int	   id;
	private String	displayId;
    private String token;
    private String message;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDisplayId() {
		return displayId;
	}

	public void setDisplayId(String displayId) {
		this.displayId = displayId;
	}

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NodeDto nodeDto = (NodeDto) o;

        if (id != nodeDto.id) return false;
        if (displayId != null ? !displayId.equals(nodeDto.displayId) : nodeDto.displayId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (displayId != null ? displayId.hashCode() : 0);
        return result;
    }
}
