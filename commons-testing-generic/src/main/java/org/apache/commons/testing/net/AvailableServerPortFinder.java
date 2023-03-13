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

package org.apache.commons.testing.net;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.testing.Closer;

/**
 * Finds currently available server ports.
 */
public final class AvailableServerPortFinder {

    /**
     * The minimum server currentMinPort number for IPv4. Set at 1100 to avoid returning privileged currentMinPort
     * numbers.
     */
    public static final int MIN_PORT_NUMBER = 1100;

    /**
     * Incremented to the next lowest available port when getNextAvailable() is called.
     */
    private static final AtomicInteger CURRENT_MIN_PORT = new AtomicInteger(MIN_PORT_NUMBER);

    /**
     * We'll hold open the lowest port in this process so parallel processes won't use the same block of ports. They'll
     * go up to the next block.
     */
    private static final ServerSocket LOCK;

    /**
     * The maximum server currentMinPort number for IPv4.
     */
    public static final int MAX_PORT_NUMBER = 65535;

    static {
        int port = MIN_PORT_NUMBER;
        ServerSocket ss = null;

        while (ss == null) {
            try {
                ss = new ServerSocket(port);
            } catch (final Exception e) {
                port += 200;
            }
        }
        LOCK = ss;
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                close();
            }
        });
        CURRENT_MIN_PORT.set(port + 1);
    }

    /**
     * Checks to see if a specific port is available.
     *
     * @param port
     *            the port number to check for availability
     * @return {@code true} if the port is available, or {@code false} if not
     * @throws IllegalArgumentException
     *             is thrown if the port number is out of range
     */
    public static synchronized boolean available(final int port) throws IllegalArgumentException {
        if (port < CURRENT_MIN_PORT.get() || port > MAX_PORT_NUMBER) {
            throw new IllegalArgumentException("Invalid start currentMinPort: " + port);
        }

        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (final IOException e) {
            // Do nothing
        } finally {
            Closer.closeSilently(ds);
            Closer.closeSilently(ss);
        }

        return false;
    }

    public static void close() {
        Closer.closeSilently(LOCK);
    }

    /**
     * Gets the next available port starting at the lowest number. This is the preferred method to use. The port return
     * is immediately marked in use and doesn't rely on the caller actually opening the port.
     *
     * @throws IllegalArgumentException
     *             is thrown if the port number is out of range
     * @throws NoSuchElementException
     *             if there are no ports available
     * @return the available port
     */
    public static synchronized int getNextAvailable() {
        final int next = getNextAvailable(CURRENT_MIN_PORT.get());
        CURRENT_MIN_PORT.set(next + 1);
        return next;
    }

    /**
     * Gets the next available port starting at a given from port.
     *
     * @param fromPort
     *            the from port to scan for availability
     * @throws IllegalArgumentException
     *             is thrown if the port number is out of range
     * @throws NoSuchElementException
     *             if there are no ports available
     * @return the available port
     */
    public static synchronized int getNextAvailable(final int fromPort) {
        if (fromPort < CURRENT_MIN_PORT.get() || fromPort > MAX_PORT_NUMBER) {
            throw new IllegalArgumentException("From port number not in valid range: " + fromPort);
        }

        for (int i = fromPort; i <= MAX_PORT_NUMBER; i++) {
            if (available(i)) {
                return i;
            }
        }

        throw new NoSuchElementException("Could not find an available port above " + fromPort);
    }

    /**
     * Creates a new instance.
     */
    private AvailableServerPortFinder() {
        // Do nothing
    }

}
