package liquibase.util;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;

public class MD5UtilTest {

	private static final String TEST_STRING = "foo";
	private static final String TEST_STRING_MD5_HASH = "acbd18db4cc2f85cedef654fccc4a4d8";

    private static final String TEST_STRING2 = "abc";
    private static final String TEST_STRING2_MD5_HASH = "900150983cd24fb0d6963f7d28e17f72";

    private static final String TEST_STRING3 = "bbb";
    private static final String TEST_STRING3_MD5_HASH = "08f8e0260c64418510cefb2b06eee5cd";

    private static final String TEST_STRING_SPECIAL_CHARACTERS = "² & é~ # '{ ([ -| è` _ ç^ à@ )°] =+} ^¨ $£¤ ù% *µ ,? ;. :/ !§ €êë îïì";
    private static final String TEST_STRING_SPECIAL_CHARACTERS_MD5_HASH = "6adc50b9de9cfc3b3e790257a7e1c08b";

	@Test
	public void testComputeMD5() throws Exception {
		String hash = MD5Util.computeMD5(TEST_STRING);
		assertEquals(TEST_STRING_MD5_HASH, hash);

        String hash2 = MD5Util.computeMD5(TEST_STRING2);
        assertEquals(TEST_STRING2_MD5_HASH, hash2);

        String hash3 = MD5Util.computeMD5(TEST_STRING3);
        assertEquals(TEST_STRING3_MD5_HASH, hash3);

        String hash_special_characters = MD5Util.computeMD5(TEST_STRING_SPECIAL_CHARACTERS);
        assertEquals(TEST_STRING_SPECIAL_CHARACTERS_MD5_HASH, hash_special_characters);
	}

	@Test
	public void testComputeMD5InputStream() {
		ByteArrayInputStream bais = new ByteArrayInputStream(TEST_STRING.getBytes());
		String hexString = MD5Util.computeMD5(bais);
		assertEquals(TEST_STRING_MD5_HASH, hexString);

    ByteArrayInputStream bais2 = new ByteArrayInputStream(TEST_STRING2.getBytes());
		String hexString2 = MD5Util.computeMD5(bais2);
		assertEquals(TEST_STRING2_MD5_HASH, hexString2);

    ByteArrayInputStream bais3 = new ByteArrayInputStream(TEST_STRING3.getBytes());
		String hexString3 = MD5Util.computeMD5(bais3);
		assertEquals(TEST_STRING3_MD5_HASH, hexString3);
	}

  /*
   * cd liquibase-core
   *
   * On Windows powershell:
   * > mvn -Dtest='liquibase.util.MD5UtilTest' test
   *   - testComputeMD5InputStreamWithSpecialCharacters        failed with 0f2f39ab98028d13b51a6b686d3d7c89 md5
   *   - testComputeMD5InputStreamWithSpecialCharactersWithISO failed with 5d489836209736e5f781feb17bc3a6ac md5
   * > mvn -D'file.encoding'=UTF-8 -Dtest='liquibase.util.MD5UtilTest' test
   *   - testComputeMD5InputStreamWithSpecialCharacters        failed with 0f2f39ab98028d13b51a6b686d3d7c89 md5
   *   - testComputeMD5InputStreamWithSpecialCharactersWithISO failed with 5d489836209736e5f781feb17bc3a6ac md5
   *
   * On Linux bash shell:
   * > mvn -Dtest=liquibase.util.MD5UtilTest test
   *   - testComputeMD5InputStreamWithSpecialCharactersWithISO failed with 5d489836209736e5f781feb17bc3a6ac md5
   * > mvn -Dfile.encoding=UTF-8 -Dtest=liquibase.util.MD5UtilTest test
   *   - testComputeMD5InputStreamWithSpecialCharactersWithISO failed with 5d489836209736e5f781feb17bc3a6ac md5
   *
   */
	@Test
	public void testComputeMD5InputStreamWithSpecialCharacters() {
		ByteArrayInputStream bais = new ByteArrayInputStream(TEST_STRING_SPECIAL_CHARACTERS.getBytes());
		String hexString = MD5Util.computeMD5(bais);
		assertEquals(TEST_STRING_SPECIAL_CHARACTERS_MD5_HASH, hexString);
	}
	@Test
	public void testComputeMD5InputStreamWithSpecialCharactersWithUTF8() throws UnsupportedEncodingException {
		ByteArrayInputStream bais = new ByteArrayInputStream(TEST_STRING_SPECIAL_CHARACTERS.getBytes("UTF-8"));
		String hexString = MD5Util.computeMD5(bais);
		assertEquals(TEST_STRING_SPECIAL_CHARACTERS_MD5_HASH, hexString);
	}
	@Test
	public void testComputeMD5InputStreamWithSpecialCharactersWithISO() throws UnsupportedEncodingException {
		ByteArrayInputStream bais = new ByteArrayInputStream(TEST_STRING_SPECIAL_CHARACTERS.getBytes("ISO-8859-1"));
		String hexString = MD5Util.computeMD5(bais);
		assertEquals(TEST_STRING_SPECIAL_CHARACTERS_MD5_HASH, hexString);
	}

}
