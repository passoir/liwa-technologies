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
package org.archive.modules.canonicalize;

import java.io.Serializable;
import java.util.regex.Matcher;

import org.archive.spring.HasKeyedProperties;
import org.archive.spring.KeyedProperties;

/**
 * Base of all rules applied canonicalizing a URL that are configurable
 * via the Heritrix settings system.
 * 
 * This base class is abstact.  Subclasses must implement the
 * {@link CanonicalizationRule#canonicalize(String, Object)} method.
 * 
 * @author stack
 * @version $Date: 2009-11-10 21:03:27 +0000 (Tue, 10 Nov 2009) $, $Revision: 6637 $
 */
public abstract class BaseRule
implements CanonicalizationRule, Serializable, HasKeyedProperties {
    protected KeyedProperties kp = new KeyedProperties();
    public KeyedProperties getKeyedProperties() {
        return kp;
    }
    
    {
        setEnabled(true);
    }
    public boolean getEnabled() {
        return (Boolean) kp.get("enabled");
    }
    public void setEnabled(boolean enabled) {
        kp.put("enabled",enabled);
    }

    /**
     * Constructor.
     */
    public BaseRule() {
    }
    
    /**
     * Run a regex that strips elements of a string.
     * 
     * Assumes the regex has a form that wants to strip elements of the passed
     * string.  Assumes that if a match, appending group 1
     * and group 2 yields desired result.
     * @param url Url to search in.
     * @param matcher Matcher whose form yields a group 1 and group 2 if a
     * match (non-null.
     * @return Original <code>url</code> else concatenization of group 1
     * and group 2.
     */
    protected String doStripRegexMatch(String url, Matcher matcher) {
        return (matcher != null && matcher.matches())?
            checkForNull(matcher.group(1)) + checkForNull(matcher.group(2)):
            url;
    }

    /**
     * @param string String to check.
     * @return <code>string</code> if non-null, else empty string ("").
     */
    private String checkForNull(String string) {
        return (string != null)? string: "";
    }
    
}
