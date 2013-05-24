/** License information:
 *    Component: sequitur
 *    Package:   de.unisb.cs.st
 *    Class:     SingleIntegrationTest
 *    Filename:  sequitur/src/test/java/de/unisb/cs/st/SingleIntegrationTest.java
 *
 * This file is part of the Sequitur library developed by Clemens Hammacher
 * at Saarland University. It has been developed for use in the JavaSlicer
 * tool. See http://www.st.cs.uni-saarland.de/javaslicer/ for more information.
 *
 * Sequitur is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sequitur is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Sequitur. If not, see <http://www.gnu.org/licenses/>.
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
