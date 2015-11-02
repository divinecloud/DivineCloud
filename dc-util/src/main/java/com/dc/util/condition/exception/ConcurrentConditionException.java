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

package com.dc.util.condition.exception;

/**
 * Gets thrown when a thread tries to block on a condition id which is already used by other thread to block in
 * same condition blocker.
 */
public class ConcurrentConditionException extends ConditionException {

    private static final long serialVersionUID = 1L;

    public ConcurrentConditionException(String s) {
        super(s);
    }

    public ConcurrentConditionException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ConcurrentConditionException(Throwable throwable) {
        super(throwable);
    }
}
