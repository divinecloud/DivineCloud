package com.dc.ssh.client.support;

import com.dc.DcException;
import com.dc.ssh.client.exec.vo.Credential;

import java.util.HashMap;
import java.util.Map;

public class CredentialsFileParser {

    /**
     * Parses the credentials text. The text is expected in the following format:
     *
     * NAME:<CREDENTIAL_NAME>, USERNAME:<OS_USER_NAME>, PASSWORD:<PASSWORD>, PRIVATE_KEY:<>PRIVATE_KEY>, PASS_PHRASE:<PASS_PHRASE>, JUMP_HOST:<JUMP_HOST>
     *
     * @param credentialsText - credentials file text
     * @return list of node details object
     */
    public static Map<String, Credential> parse(String credentialsText) throws DcException {
        Map<String, Credential> result = new HashMap<>();
        if(credentialsText != null) {
            String[] lines = credentialsText.split("\n");
            for (String line : lines) {
                if (!"".equals(line.trim())) {
                    String[] fields = line.split(",");

                    if (fields.length > 0) {
                        Map<String, String> columnsMap = new HashMap<>();

                        for (String field : fields) {
                            int index = field.indexOf(":");
                            if (index > 0) {
                                String columnKey = field.substring(0, index);
                                String columnValue = field.substring(index + 1);
                                columnsMap.put(columnKey.trim(), columnValue.trim());
                            }
                        }

                        Credential nodeCredentials = convert(columnsMap);
                        result.put(nodeCredentials.getName(), nodeCredentials);
                    }
                }
            }
        }

        if(result.size() == 0) {
            throw new DcException("Invalid Node Details provided in Transient Nodes import file");
        }
        return result;
    }


    private static Credential convert(Map<String, String> columnsMap) {
        String name = columnsMap.get(CredentialKeys.NAME.name());
        String username = columnsMap.get(CredentialKeys.USERNAME.name());
        String password = columnsMap.get(CredentialKeys.PASSWORD.name());
        String privateKey = columnsMap.get(CredentialKeys.PRIVATE_KEY.name());
        String passPhrase = columnsMap.get(CredentialKeys.PASS_PHRASE.name());
        String jumpHost = columnsMap.get(CredentialKeys.JUMP_HOST.name());


        if(name == null || username == null) {
            throw new DcException("Invalid  Credentials record provided in the file : " + name + " " + username);
        }

        Credential credential = new Credential();
        credential.setName(name);
        credential.setUserName(username);
        credential.setJumpHost(jumpHost);
        credential.setPassword(password);
        credential.setPrivateKey(privateKey);
        credential.setPassPhrase(passPhrase);
        return credential;
    }
}
