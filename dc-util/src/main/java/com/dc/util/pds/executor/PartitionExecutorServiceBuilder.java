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

package com.dc.util.pds.executor;

import com.dc.util.pds.executor.nonordered.PartitionNonOrderedExecutorService;
import com.dc.util.pds.executor.ordered.PartitionOrderedExecutorService;

/**
 * Builds either the ordered or non-ordered partition executor service.
 */
public class PartitionExecutorServiceBuilder<M> {

    public PartitionExecutorService<M> build(PartitionMessageHandler<M> handler, Configuration configuration, boolean ordered) {
        PartitionExecutorService<M> service;
        if (ordered) {
            service = new PartitionOrderedExecutorService<M>(handler, configuration);
        } else {
            service = new PartitionNonOrderedExecutorService<M>(handler, configuration);
        }
        return service;
    }
}
