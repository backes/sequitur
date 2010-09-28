/** License information:
 *    Component: sequitur
 *    Package:   de.unisb.cs.st
 *    Class:     SingleIntegrationTest
 *    Filename:  sequitur/src/test/java/de/unisb/cs/st/SingleIntegrationTest.java
 *
 * This file is part of the JavaSlicer tool, developed by Clemens Hammacher at Saarland University.
 * See http://www.st.cs.uni-saarland.de/javaslicer/ for more information.
 *
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/3.0/ or send a
 * letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 */
package de.unisb.cs.st;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SingleIntegrationTest extends RandomIntegrationTest {

    private static final int length = 10;
    private static final long seed = 8398731863259715411l;

    public SingleIntegrationTest() {
        super(length, seed);
    }

}
