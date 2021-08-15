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

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.apache.commons.testing.junit4.RuleChainFactory;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TemporaryFolder;

public class CopyFileTestRuleTest {

    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    public final CopyFileTestRule copyFileTestRule = CopyFileTestRule.create("src/test/resources/test.txt", temporaryFolder,
            "test-dest.txt");

    @Rule
    public RuleChain ruleChain = RuleChainFactory.create(temporaryFolder, copyFileTestRule);

    @Test
    public void test() throws IOException {
        Assert.assertEquals("test",
                FileUtils.readFileToString(copyFileTestRule.getTargetFile(), StandardCharsets.UTF_8).trim());

    }
}
