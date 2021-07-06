package org.spout.steamkit.util.crypto;

import org.junit.Assert;
import org.junit.Test;

import uk.co.thomasc.steamkit.util.crypto.CryptoHelper;

public class CryptoHelperTest {

	byte[] input = new byte[] { (byte) 0xf1, (byte) 0x57, (byte) 0xd3, (byte) 0x41, (byte) 0xcf, (byte) 0xee, (byte) 0x5c, (byte) 0x13, (byte) 0x81, (byte) 0xf7, (byte) 0x44, (byte) 0x1c, (byte) 0x05, (byte) 0xab, (byte) 0xc5, (byte) 0x10 };
	byte[] input2 = new byte[] { (byte) 0xe6, (byte) 0x61, (byte) 0xb3, (byte) 0xba, (byte) 0x4f, (byte) 0xcd, (byte) 0xad, (byte) 0x3c, (byte) 0x40, (byte) 0xe4, (byte) 0xc8, (byte) 0x6a, (byte) 0x92, (byte) 0xaf, (byte) 0x54, (byte) 0xad };

	byte[] input3 = new byte[] { (byte) 0x32, (byte) 0xBA, (byte) 0xD8, (byte) 0xF4, (byte) 0x3F, (byte) 0x96, (byte) 0x96, (byte) 0x7B, (byte) 0xD6, (byte) 0x8F, (byte) 0x02, (byte) 0xE8, (byte) 0x36, (byte) 0xD8, (byte) 0xB4, (byte) 0xE8, (byte) 0x45, (byte) 0x82, (byte) 0x9E, (byte) 0xF6, (byte) 0xC7, (byte) 0x34, (byte) 0x92, (byte) 0xFE, (byte) 0x17, (byte) 0xDB, (byte) 0x53, (byte) 0xB1, (byte) 0x5E, (byte) 0x84, (byte) 0xA0, (byte) 0x3C, (byte) 0x3C, (byte) 0x92, (byte) 0x1D, (byte) 0x42, (byte) 0x4D, (byte) 0x0E, (byte) 0x8E, (byte) 0x6C, (byte) 0x48, (byte) 0x26, (byte) 0x86, (byte) 0xF3, (byte) 0x93, (byte) 0xFF, (byte) 0x1F, (byte) 0x85, (byte) 0xBF, (byte) 0x47, (byte) 0x5F, (byte) 0xA4, (byte) 0x12, (byte) 0x07, (byte) 0x35, (byte) 0xF1, (byte) 0xAE, (byte) 0x19, (byte) 0xD1, (byte) 0x1E, (byte) 0xC3, (byte) 0x47, (byte) 0x2F, (byte) 0x33, (byte) 0x02, (byte) 0xA4, (byte) 0x76, (byte) 0xFB, (byte) 0xA5, (byte) 0x2C, (byte) 0x02, (byte) 0xCB, (byte) 0xF3, (byte) 0x8F,
			(byte) 0x57, (byte) 0x25, (byte) 0x4F, (byte) 0xC8, (byte) 0xBC, (byte) 0x06 };
	byte[] key = new byte[] { (byte) 0x30, (byte) 0x81, (byte) 0x9D, (byte) 0x30, (byte) 0x0D, (byte) 0x06, (byte) 0x09, (byte) 0x2A, (byte) 0x86, (byte) 0x48, (byte) 0x86, (byte) 0xF7, (byte) 0x0D, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x05, (byte) 0x00, (byte) 0x03, (byte) 0x81, (byte) 0x8B, (byte) 0x00, (byte) 0x30, (byte) 0x81, (byte) 0x87, (byte) 0x02, (byte) 0x81, (byte) 0x81, (byte) 0x00, (byte) 0xDF, (byte) 0xEC, (byte) 0x1A, (byte) 0xD6, (byte) 0x2C, (byte) 0x10, (byte) 0x66, (byte) 0x2C, (byte) 0x17, (byte) 0x35, (byte) 0x3A, (byte) 0x14, (byte) 0xB0, (byte) 0x7C, (byte) 0x59, (byte) 0x11, (byte) 0x7F, (byte) 0x9D, (byte) 0xD3, (byte) 0xD8, (byte) 0x2B, (byte) 0x7A, (byte) 0xE3, (byte) 0xE0, (byte) 0x15, (byte) 0xCD, (byte) 0x19, (byte) 0x1E, (byte) 0x46, (byte) 0xE8, (byte) 0x7B, (byte) 0x87, (byte) 0x74, (byte) 0xA2, (byte) 0x18, (byte) 0x46, (byte) 0x31, (byte) 0xA9, (byte) 0x03, (byte) 0x14, (byte) 0x79, (byte) 0x82, (byte) 0x8E, (byte) 0xE9, (byte) 0x45,
			(byte) 0xA2, (byte) 0x49, (byte) 0x12, (byte) 0xA9, (byte) 0x23, (byte) 0x68, (byte) 0x73, (byte) 0x89, (byte) 0xCF, (byte) 0x69, (byte) 0xA1, (byte) 0xB1, (byte) 0x61, (byte) 0x46, (byte) 0xBD, (byte) 0xC1, (byte) 0xBE, (byte) 0xBF, (byte) 0xD6, (byte) 0x01, (byte) 0x1B, (byte) 0xD8, (byte) 0x81, (byte) 0xD4, (byte) 0xDC, (byte) 0x90, (byte) 0xFB, (byte) 0xFE, (byte) 0x4F, (byte) 0x52, (byte) 0x73, (byte) 0x66, (byte) 0xCB, (byte) 0x95, (byte) 0x70, (byte) 0xD7, (byte) 0xC5, (byte) 0x8E, (byte) 0xBA, (byte) 0x1C, (byte) 0x7A, (byte) 0x33, (byte) 0x75, (byte) 0xA1, (byte) 0x62, (byte) 0x34, (byte) 0x46, (byte) 0xBB, (byte) 0x60, (byte) 0xB7, (byte) 0x80, (byte) 0x68, (byte) 0xFA, (byte) 0x13, (byte) 0xA7, (byte) 0x7A, (byte) 0x8A, (byte) 0x37, (byte) 0x4B, (byte) 0x9E, (byte) 0xC6, (byte) 0xF4, (byte) 0x5D, (byte) 0x5F, (byte) 0x3A, (byte) 0x99, (byte) 0xF9, (byte) 0x9E, (byte) 0xC4, (byte) 0x3A, (byte) 0xE9, (byte) 0x63, (byte) 0xA2, (byte) 0xBB, (byte) 0x88,
			(byte) 0x19, (byte) 0x28, (byte) 0xE0, (byte) 0xE7, (byte) 0x14, (byte) 0xC0, (byte) 0x42, (byte) 0x89, (byte) 0x02, (byte) 0x01, (byte) 0x11 };
	byte[] sessionKey = new byte[] { (byte) 0x6E, (byte) 0xE1, (byte) 0xB2, (byte) 0xB5, (byte) 0x90, (byte) 0xCD, (byte) 0xE5, (byte) 0x4D, (byte) 0x89, (byte) 0x4F, (byte) 0x1B, (byte) 0x7A, (byte) 0x63, (byte) 0x59, (byte) 0x4E, (byte) 0xE5, (byte) 0x19, (byte) 0xAE, (byte) 0xCC, (byte) 0xD6, (byte) 0xD0, (byte) 0xF8, (byte) 0xCB, (byte) 0xAB, (byte) 0x66, (byte) 0xCB, (byte) 0xDC, (byte) 0xCA, (byte) 0x5D, (byte) 0xA1, (byte) 0xC9, (byte) 0x37 };

	@Test
	public void testCRCHash() {
		byte[] result = CryptoHelper.CRCHash(input);
		Assert.assertArrayEquals(result, new byte[] { (byte) 0x0F, (byte) 0x33, (byte) 0x9C, (byte) 0x4B });

		result = CryptoHelper.CRCHash(input2);
		Assert.assertArrayEquals(result, new byte[] { (byte) 0xA0, (byte) 0xC0, (byte) 0x24, (byte) 0x17 });
	}

	@Test
	public void testJenkinsHash() {
		byte[] result = CryptoHelper.JenkinsHash(input);
		Assert.assertArrayEquals(result, new byte[] { (byte) 0xF6, (byte) 0x32, (byte) 0xED, (byte) 0xC6 });

		result = CryptoHelper.JenkinsHash(input2);
		Assert.assertArrayEquals(result, new byte[] { (byte) 0xA5, (byte) 0x63, (byte) 0x1E, (byte) 0x43 });
	}

	@Test
	public void testSHA1Hash() {
		byte[] result = CryptoHelper.SHAHash(input);
		Assert.assertArrayEquals(result, new byte[] { (byte) 0xE3, (byte) 0xCA, (byte) 0xC1, (byte) 0xB3, (byte) 0x7D, (byte) 0xBF, (byte) 0x48, (byte) 0xDE, (byte) 0x9B, (byte) 0x73, (byte) 0x8A, (byte) 0x41, (byte) 0x21, (byte) 0x7E, (byte) 0x5A, (byte) 0x08, (byte) 0xD8, (byte) 0xE3, (byte) 0xAA, (byte) 0x23 });

		result = CryptoHelper.SHAHash(input2);
		Assert.assertArrayEquals(result, new byte[] { (byte) 0x54, (byte) 0xD5, (byte) 0x58, (byte) 0x00, (byte) 0xD0, (byte) 0x51, (byte) 0xB4, (byte) 0x06, (byte) 0x47, (byte) 0x87, (byte) 0x8A, (byte) 0xAB, (byte) 0xAD, (byte) 0xF0, (byte) 0x69, (byte) 0x4B, (byte) 0xFF, (byte) 0x65, (byte) 0xD9, (byte) 0x8E });
	}

	@Test
	public void testSymetric() {
		byte[] result = CryptoHelper.SymmetricDecrypt(input3, sessionKey);
		Assert.assertArrayEquals(result, new byte[] { (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x12, (byte) 0x27, (byte) 0x23, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xEF, (byte) 0x02, (byte) 0x00, (byte) 0x80, (byte) 0x0D, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x09, (byte) 0x6B, (byte) 0xA3, (byte) 0xAA, (byte) 0x06, (byte) 0x01, (byte) 0x00, (byte) 0x10, (byte) 0x01, (byte) 0x10, (byte) 0xAD, (byte) 0xA8, (byte) 0x56, (byte) 0x08, (byte) 0x41, (byte) 0x50, (byte) 0x47, (byte) 0xA1, (byte) 0x01, (byte) 0x6B, (byte) 0xA3, (byte) 0xAA, (byte) 0x06, (byte) 0x01, (byte) 0x00, (byte) 0x10, (byte) 0x01 });

		final byte[] temp = CryptoHelper.SymmetricEncrypt(input3, sessionKey);
		result = CryptoHelper.SymmetricDecrypt(temp, sessionKey);

		Assert.assertArrayEquals(input3, result);
	}

}
