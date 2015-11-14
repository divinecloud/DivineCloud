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

package com.dc.util;

import java.util.Random;

public class ExponentialValueCreator {
    private int[] values = {2, 2, 5, 5, 5, 10, 10, 10, 15, 15, 15, 15, 15, 30, 30, 30, 30, 30, 60, 60, 60, 60, 60};
    
    private int valuePointer;
    
    private Random random;
    
    public ExponentialValueCreator() {
        random = new Random(System.currentTimeMillis());
    }

    public synchronized int nextValue() {
        valuePointer++;
        valuePointer = valuePointer % values.length;
        return values[valuePointer] + generateSalt();
    }

    private int generateSalt() {
        return random.nextInt(5);
    }
    
}
