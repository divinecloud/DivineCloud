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

package com.dc.util.pds.executor.support;

/**
 * Partitioned Message containing the partition ID and the message.
 */
public class PartitionMessage<M> {
    private String partitionID;
    private M message;

    public PartitionMessage(String partitionID, M message) {
        this.partitionID = partitionID;
        this.message = message;
    }

    public String getPartitionID() {
        return partitionID;
    }

    public M getMessage() {
        return message;
    }
}
