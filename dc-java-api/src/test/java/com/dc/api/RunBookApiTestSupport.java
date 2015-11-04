package com.dc.api;


import com.dc.ssh.client.exec.vo.Credential;
import com.dc.ssh.client.exec.vo.NodeCredentialKeys;
import com.dc.ssh.client.exec.vo.NodeCredentials;
import com.dc.ssh.client.support.CredentialKeys;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class RunBookApiTestSupport {

    public static void createNodeCredFile(List<List<NodeCredentials>> list, String destinationFolder, String fileName) throws IOException {
        int step = 0;
        File folder = new File(destinationFolder);
        folder.mkdirs();
        File credFile = new File(folder, fileName);
        FileWriter fileWriter = new FileWriter(credFile);
        for(List<NodeCredentials> nodeCredList : list) {
            step++;
            for(NodeCredentials nodeCredentials : nodeCredList) {
                StringBuilder builder = new StringBuilder();
                builder.append(NodeCredentialKeys.STEP.name()).append(":").append(step).append(",")
                        .append(NodeCredentialKeys.ID.name()).append(":").append(nodeCredentials.getId()).append(",")
                        .append(NodeCredentialKeys.HOST.name()).append(":").append(nodeCredentials.getHost()).append(",")
                        .append(NodeCredentialKeys.USERNAME.name()).append(":").append(nodeCredentials.getUsername()).append(",")
                        .append(NodeCredentialKeys.PASSWORD.name()).append(":").append(nodeCredentials.getPassword())
                        .append('\n');
                fileWriter.write(builder.toString());
            }
        }
        fileWriter.flush();
        fileWriter.close();
    }

    public static void deleteFile(String folderPath, String fileName) {
        File file = new File(folderPath, fileName);
        file.delete();
        File folder = new File(folderPath);
        folder.delete();
    }


    public static void createCredFile(List<Credential> list, String destinationFolder, String fileName) throws IOException {
        File folder = new File(destinationFolder);
        folder.mkdirs();
        File credFile = new File(folder, fileName);
        FileWriter fileWriter = new FileWriter(credFile);
        for(Credential credential : list) {
            StringBuilder builder = new StringBuilder();
            builder.append(CredentialKeys.NAME.name()).append(":").append(credential.getName()).append(",")
                    .append(CredentialKeys.USERNAME.name()).append(":").append(credential.getUserName()).append(",")
                    .append(CredentialKeys.PASSWORD.name()).append(":").append(credential.getPassword())
                    .append('\n');
            fileWriter.write(builder.toString());
        }
        fileWriter.flush();
        fileWriter.close();

    }
}
