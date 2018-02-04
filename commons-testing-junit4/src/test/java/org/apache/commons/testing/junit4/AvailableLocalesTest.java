/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.testing.junit4;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class AvailableLocalesTest extends AbstractAvailableLocalesTest {

    private static Set<Locale> found;

    @AfterClass
    public static void afterClass() {
        final ConcurrentSkipListSet<Locale> expected = createSet();
        Collections.addAll(expected, Locale.getAvailableLocales());
        Assert.assertEquals(expected, found);
    }

    @BeforeClass
    public static void beforeClass() {
        found = createSet();
    }

    private static ConcurrentSkipListSet<Locale> createSet() {
        return new ConcurrentSkipListSet<>(new ObjectToStringComparator());
    }

    public AvailableLocalesTest(final Locale locale) {
        super(locale);
    }

    @Test
    public void test() {
        found.add(getLocale());
    }
}
