/*
 * Copyright 2011 Peter Lawrey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hotwheel.jni.affinity;

import java.util.concurrent.ThreadFactory;

/**
 * This is a ThreadFactory which assigns threads based the strategies provided.
 * <p>
 * If no strategies are provided AffinityStrategies.ANY is used.
 *
 * @author peter.lawrey
 */
public class AffinityThreadFactory implements ThreadFactory {
    private final String name;
    private final boolean daemon;
    private final AffinityStrategy[] strategies;
    private AffinityLock lastAffinityLock = null;
    private int id = 1;

    public AffinityThreadFactory(String name, AffinityStrategy... strategies) {
        this(name, true, strategies);
    }

    public AffinityThreadFactory(String name, boolean daemon, AffinityStrategy... strategies) {
        this.name = name;
        this.daemon = daemon;
        this.strategies = strategies.length == 0 ? new AffinityStrategy[]{AffinityStrategies.ANY} : strategies;
    }

    @Override
    public synchronized Thread newThread(final Runnable r) {
        String name2 = id <= 1 ? name : (name + '-' + id);
        id++;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                AffinityLock al = lastAffinityLock == null ? AffinityLock.acquireLock() : lastAffinityLock.acquireLock(strategies);
                try {
                    if (al.cpuId() >= 0) {
                        lastAffinityLock = al;
                    }
                    r.run();
                } finally {
                    al.release();
                }
            }
        }, name2);
        t.setDaemon(daemon);
        return t;
    }
}
