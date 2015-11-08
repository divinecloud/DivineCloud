
/*
 *
 *  * Copyright (C) 2014 Divine Cloud Inc.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.dc.util.logscanner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class MessageBuffer {
    private List<Message> messages;

    public MessageBuffer() {
        messages = new ArrayList<Message>();
    }
    
    public void addMessage(Message message) {
        messages.add(message);
    }

    public Message getLastMessage(){
        if ( messages.isEmpty()){
            return null;
        } else{
            return messages.get(messages.size()-1);
        }
    }

    public void removeMessage(Message message) {
        messages.remove(message);
    }

    public List<Message> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    public static class Message{
        private Stack<String> messageEntry;

        public Message() {
            messageEntry = new Stack<String>();
        }

        public void addLine(String line){
            messageEntry.push(line);
        }

        public String removeLastLine(){
            return messageEntry.pop();
        }

        public boolean isEmpty(){
            return messageEntry.isEmpty();
        }

        public String getEntry(int i) {
            return messageEntry.get(i);
        }
        
        public int getEntryCount(){
            return messageEntry.size();
        }

        public String[] getEntries() {
            return messageEntry.toArray(new String[0]);
        }
    }
}
