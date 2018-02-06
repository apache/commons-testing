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

package org.apache.commons.testing.junit4.io;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.rules.ExternalResource;
import org.junit.rules.TemporaryFolder;

/**
 * Copies a file as a JUnit TestRule.
 * 
 * @since 1.0.0
 */
public class CopyFileTestRule extends ExternalResource {

    /**
     * Creates a test rule that will copy a file to a given temporary folder.
     * 
     * @param sourceFilePath
     *            the file to copy.
     * @param targetTemporaryFolder
     *            the destination folder.
     * @param targetFilePath
     * @return a new test rule.
     */
    public static CopyFileTestRule create(final String sourceFilePath, final TemporaryFolder targetTemporaryFolder,
            final String targetFilePath) {
        return new CopyFileTestRule(sourceFilePath, targetTemporaryFolder, targetFilePath);
    }

    private final TemporaryFolder targetTemporaryFolder;
    private final String targetFileName;
    private final String sourceFilePath;

    CopyFileTestRule(final String sourceFilePath, final TemporaryFolder targetTemporaryFolder,
            final String targetFilePath) {
        super();
        this.sourceFilePath = Objects.requireNonNull(sourceFilePath, "sourceFilePath");
        this.targetTemporaryFolder = Objects.requireNonNull(targetTemporaryFolder, "targetTemporaryFolder");
        this.targetFileName = Objects.requireNonNull(targetFilePath, "targetFileName");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.junit.rules.ExternalResource#before()
     */
    @Override
    protected void before() throws Throwable {
        copyFile();
    }

    private void copyFile() throws IOException {
        final File fromFile = getSourceFile();
        final File toFile = getTargetFile();
        FileUtils.copyFile(fromFile, toFile);
        Assert.assertTrue(toFile.exists());
    }

    public File getSourceFile() {
        return new File(sourceFilePath);
    }

    public File getTargetFile() {
        return new File(targetTemporaryFolder.getRoot(), targetFileName);
    }

}
