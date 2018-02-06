// Copyright 2017-2018 Rocket Software, Inc. All rights reserved.

package org.apache.commons.testing.junit4.mongodb;

import org.apache.commons.testing.junit4.RuleChainFactory;
import org.apache.commons.testing.junit4.net.AvailableServerPortSystemPropertyTestRule;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import com.mongodb.client.MongoIterable;

/**
 * Tests {@link MongoDbTestRule}.
 */
public class MongoDbTestRuleTest {

    public static final AvailableServerPortSystemPropertyTestRule mongoDbPortTestRule = AvailableServerPortSystemPropertyTestRule
            .create(MongoDbTestRuleTest.class.getName());

    public static final MongoDbTestRule mongoDbTestRule = new MongoDbTestRule(mongoDbPortTestRule.getName());

    @ClassRule
    public static RuleChain mongoDbChain = RuleChainFactory.create(mongoDbPortTestRule, mongoDbTestRule);

    @Test
    public void testAccess() {
        final MongoIterable<String> databaseNames = mongoDbTestRule.getMongoClient().listDatabaseNames();
        Assert.assertNotNull(databaseNames);
        Assert.assertNotNull(databaseNames.first());
    }

    @Test
    public void testMongoDbRule() {
        Assert.assertNotNull(MongoDbTestRule.getStarter());
        Assert.assertNotNull(mongoDbTestRule);
        Assert.assertNotNull(mongoDbTestRule.getMongoClient());
        Assert.assertNotNull(mongoDbTestRule.getMongodExecutable());
        Assert.assertNotNull(mongoDbTestRule.getMongodProcess());
    }
}
