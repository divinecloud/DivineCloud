package com.dc.runbook.rt.cmd.exec;

public interface GroupTermCallback {

    public void complete(String nodeDisplayId, int statusCode);

    public void output(String displayId, String output);

    public void error(String displayId, String error);

    public void started();

    public void markCancelled();

    public void done();

    public void done(Exception e);

}
