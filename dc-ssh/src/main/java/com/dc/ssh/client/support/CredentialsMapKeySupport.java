/*
 * Copyright (C) 2014 Divine Cloud Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
