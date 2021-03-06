/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package test.java.sign;

import static org.junit.Assert.assertTrue;

import java.security.KeyPair;

import org.junit.Test;

public class LibraryTest {
	@Test
    public void signAndVerify() throws Exception {
    	Library lib = new Library();
    	KeyPair keyPair = lib.generateKeyPair();
    	byte[] sign = lib.sign("TestMessage", keyPair.getPrivate());
    	assertTrue(lib.verify("TestMessage", keyPair.getPublic(), sign));
	}
}
