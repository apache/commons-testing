/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */
package org.apache.commons.testing;

import org.junit.Assert;
import org.junit.Test;

public class CloserTest {

    static class MarkAutoCloseable implements AutoCloseable {

        private boolean closed;

        @Override
        public void close() throws Exception {
            closed = true;
        }

        boolean isClosed() {
            return closed;
        }

    }

    @Test
    public void testCloseAutoCloseable() throws Exception {
        @SuppressWarnings("resource")
        final MarkAutoCloseable closeable = new MarkAutoCloseable();
        Assert.assertFalse(closeable.isClosed());
        Closer.close(closeable);
        Assert.assertTrue(closeable.isClosed());
    }

    @Test
    public void testCloseSlientlyAutoCloseable() {
        @SuppressWarnings("resource")
        final MarkAutoCloseable closeable = new MarkAutoCloseable();
        Assert.assertFalse(closeable.isClosed());
        Closer.closeSilently(closeable);
        Assert.assertTrue(closeable.isClosed());
    }

}
