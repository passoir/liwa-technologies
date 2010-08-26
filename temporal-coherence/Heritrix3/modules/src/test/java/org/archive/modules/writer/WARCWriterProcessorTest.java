/*
 *  This file is part of the Heritrix web crawler (crawler.archive.org).
 *
 *  Licensed to the Internet Archive (IA) by one or more individual 
 *  contributors. 
 *
 *  The IA licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.archive.modules.writer;

import java.io.File;

import org.archive.modules.CrawlMetadata;
import org.archive.modules.ProcessorTestBase;
import org.archive.modules.fetcher.DefaultServerCache;
import org.archive.modules.net.RobotsHonoringPolicy;
import org.archive.spring.ConfigPath;
import org.archive.util.TmpDirTestCase;

/**
 * Unit test for {@link WARCWriterProcessor}.
 *
 * @author pjack
 */
public class WARCWriterProcessorTest extends ProcessorTestBase {
 
    @Override
    protected Object makeModule() throws Exception {
        File tmp = TmpDirTestCase.tmpDir();
        tmp = new File(tmp, "ARCWriterProcessTest");
        tmp.mkdirs();

        WARCWriterProcessor result = new WARCWriterProcessor();
        result.setDirectory(new ConfigPath("test",tmp.getAbsolutePath()));
        result.setServerCache(new DefaultServerCache());
        CrawlMetadata metadata = new CrawlMetadata();
        metadata.setRobotsHonoringPolicy(new RobotsHonoringPolicy());
        result.setMetadataProvider(metadata);
        result.start();
        return result;
    }

    @Override
    protected void verifySerialization(Object first, byte[] firstBytes, 
            Object second, byte[] secondBytes) throws Exception {

    }
    
    // TODO TESTME!
    
}
