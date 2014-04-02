package org.genericsystem.security;

import org.genericsystem.security.hachage.MD5Hashing;
import org.testng.annotations.Test;

@Test
public class HashTest extends AbstractTest {

	public void testHash() {
		String password = "middlewest";
		String passwordHash = MD5Hashing.hashPassword(password);
		assert passwordHash.equals("7661bfdb88890e9a28369c7d794a1670");
	}
}
