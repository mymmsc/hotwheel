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

/**
 * Allow you define a strategy for find the a cpu relative to another select cpu.
 *
 * @author peter.lawrey
 */
public interface AffinityStrategy {
    /**
     * @param cpuId  to cpuId to compare
     * @param cpuId2 with a second cpuId
     * @return true if it matches the criteria.
     */
    public boolean matches(int cpuId, int cpuId2);
}
