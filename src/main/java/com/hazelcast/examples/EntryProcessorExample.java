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

import com.hazelcast.config.Config;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.map.EntryProcessor;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class EntryProcessorExample {

    public static void main(String[] args)
            throws Exception {

        // Prepare Hazelcast cluster
        HazelcastInstance hazelcastInstance = buildCluster(3);

        try {

            IMap<String, Integer> counters = hazelcastInstance.getMap("counters");
            counters.put("foo", 1000);

            EntryProcessor<String, Integer> processor = wrap(EntryProcessorExample::increment);

            CountDownLatch latch = new CountDownLatch(1000);
            for (int i = 0; i < 1000; i++) {
                new Thread(() -> {

                    int newId = (Integer) counters.executeOnKey("foo", processor);
                    System.out.println("Generated Id: " + newId);
                    latch.countDown();

                }).start();
            }

            latch.await();

        } finally {
            // Shutdown cluster
            Hazelcast.shutdownAll();
        }
    }

    private static int increment(Map.Entry<String, Integer> entry) {
        int newValue = entry.getValue() + 1;
        entry.setValue(newValue);
        return newValue;
    }

    private static <K, V> EntryProcessor<K, V> wrap(Java8EntryProcessor<K, V> entryProcessor) {
        return new Java8EntryProcessorAdapter<>(entryProcessor);
    }

    private static HazelcastInstance buildCluster(int memberCount) {
        Config config = new Config();
        NetworkConfig networkConfig = config.getNetworkConfig();
        networkConfig.getJoin().getMulticastConfig().setEnabled(false);
        networkConfig.getJoin().getTcpIpConfig().setEnabled(true);
        networkConfig.getJoin().getTcpIpConfig().setMembers(Arrays.asList(new String[]{"127.0.0.1"}));

        HazelcastInstance[] hazelcastInstances = new HazelcastInstance[memberCount];
        for (int i = 0; i < memberCount; i++) {
            hazelcastInstances[i] = Hazelcast.newHazelcastInstance(config);
        }
        return hazelcastInstances[0];
    }
}
