package com.dc.runbook.dt.support;

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

public class BasicGitClient {

    public static void clone(String from, String to) {
        try {
            Git.cloneRepository().setURI(from).setDirectory(new File(to)).call().close();
        } catch (GitAPIException e) {
            throw new GitClientException("Error occurred while cloning from : " + from + " to : " + to, e);
        }
    }

    public static void forceClone(String from, String to) {
        try {
            File file = new File(to);
            if(file.exists()) {
                boolean deleted = deleteFiles(file);
                if(!deleted) {
                    throw new GitClientException("Unable to delete folder : " + file.getAbsolutePath());
                }
            }

            Git.cloneRepository().setURI(from).setDirectory(new File(to)).call().close();
        } catch (GitAPIException e) {
            throw new GitClientException("Error occurred while cloning from : " + from + " to : " + to, e);
        }
    }

    private static boolean deleteFiles(File folder) {
        boolean deleted = true;
        File[] listOfFiles = folder.listFiles();
        if(listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    deleted = file.delete();
                } else if (file.isDirectory()) {
                    deleted = deleteFiles(file);
                }
            }
        }
        return folder.delete() && deleted;
    }


}

