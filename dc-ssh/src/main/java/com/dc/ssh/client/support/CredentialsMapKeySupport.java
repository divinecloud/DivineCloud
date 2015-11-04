package com.dc.ssh.client.support;


public class CredentialsMapKeySupport {

    public static String generateUserCredentialId(int accountId, int divisionId, int userId, int credentialId) {
        return "" + accountId + divisionId + userId + "_" + credentialId;
    }

    public static String generatePassphraseCredentialId(String userCredentialId) {
        return userCredentialId + "-PASSPHRASE";
    }

    public static String generatePasscodeCredentialId(String userCredentialId, String displayId) {
        return userCredentialId + "_" + displayId;
    }
}
