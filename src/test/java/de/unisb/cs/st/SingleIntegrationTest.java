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
