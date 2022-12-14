/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.dolphinscheduler.server.log;

import org.apache.commons.lang.StringUtils;
import org.apache.dolphinscheduler.common.Constants;
import org.apache.dolphinscheduler.common.utils.FileUtils;
import org.apache.dolphinscheduler.service.log.LogClientService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class LoggerServerTest {

    private LoggerServer loggerServer;

    private LogClientService logClientService;

    private String dsHome;

    @Before
    public void startServerAndClient() {
        this.loggerServer = new LoggerServer();
        this.loggerServer.start();
        this.logClientService = new LogClientService();

        // DOLPHINSCHEDULER_HOME is be set in start.sh. if we run test in IDE user.dir is DS Home.
        dsHome = System.getProperty("DOLPHINSCHEDULER_HOME");
        if (StringUtils.isBlank(dsHome)) {
            dsHome = System.getProperty("user.dir");
            System.setProperty("DOLPHINSCHEDULER_HOME", dsHome);
        }
    }

    @Test
    public void testRollViewLog()
            throws IOException {
        String expectedTmpDemoString = "testRolloViewLog";
        String testFile = dsHome + "/tmp/demo.log";
        org.apache.commons.io.FileUtils.writeStringToFile(new File(testFile), expectedTmpDemoString, Charset.defaultCharset());

        String resultTmpDemoString = this.logClientService.rollViewLog(
                "localhost", Constants.RPC_PORT, testFile, 0, 1000);

        Assert.assertEquals(expectedTmpDemoString, resultTmpDemoString.replaceAll("[\r|\n|\t]", StringUtils.EMPTY));

        FileUtils.deleteFile(testFile);
    }

    @Test
    public void testRemoveTaskLog()
            throws IOException {
        String expectedTmpRemoveString = "testRemoveTaskLog";
        String testFile = dsHome + "/tmp/remove.log";
        org.apache.commons.io.FileUtils.writeStringToFile(new File(testFile), expectedTmpRemoveString, Charset.defaultCharset());

        Boolean b = this.logClientService.removeTaskLog("localhost", Constants.RPC_PORT, testFile);

        Assert.assertTrue(b);

        String result = this.logClientService.viewLog("localhost", Constants.RPC_PORT, testFile);

        Assert.assertEquals(StringUtils.EMPTY, result);
    }

    @After
    public void stopServerAndClient() {
        this.loggerServer.stop();
        this.logClientService.close();
    }
}
