/*
 * Copyright (c) 2008-2013, Hazelcast, Inc. All Rights Reserved.
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

package com.hazelcast.examples;

import com.hazelcast.map.AbstractEntryProcessor;

import java.util.Map;

public class Java8EntryProcessorAdapter<K, V>
        extends AbstractEntryProcessor<K, V> {

    private Java8EntryProcessor<K, V> entryProcessor;

    // Deserialization constructor
    public Java8EntryProcessorAdapter() {
    }

    public Java8EntryProcessorAdapter(Java8EntryProcessor<K, V> entryProcessor) {
        this.entryProcessor = entryProcessor;
    }

    @Override
    public Object process(Map.Entry<K, V> entry) {
        return entryProcessor.process(entry);
    }
}
