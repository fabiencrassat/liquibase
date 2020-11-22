package liquibase.change;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.*;

public class CheckSumTest {

    @Test
    public void parse() {
        String checksumString = "3:asdf";
        CheckSum checkSum = CheckSum.parse(checksumString);
        assertEquals(3, checkSum.getVersion());
        assertEquals(checksumString, checkSum.toString());
    }

    @Test
    public void parse_null() {
        assertNull(CheckSum.parse(null));
    }

    @Test
    public void parse_v1() {
        String checksumString = "asdf";
        CheckSum checkSum = CheckSum.parse(checksumString);
        assertEquals(1, checkSum.getVersion());
        assertEquals("1:asdf", checkSum.toString());
    }

    @Test
    public void getCurrentVersion() {
        assertEquals(8, CheckSum.getCurrentVersion());
    }

    @Test
    public void compute_String() {
        String valueToHash = "asdf";
        CheckSum checkSum = CheckSum.compute(valueToHash);
        assertEquals(CheckSum.getCurrentVersion(), checkSum.getVersion());
        assertNotEquals(checkSum.toString(), valueToHash);
    }

    @Test
    public void compute_String_shouldIgnoreUnknownUnicodeChar() {
        CheckSum checkSum1 = CheckSum.compute("asdfa");
        CheckSum checkSum2 = CheckSum.compute("as\uFFFDdf\uFFFDa");

        assertEquals(checkSum2, checkSum1);
    }

    @Test
    public void compute_Stream() {
        String valueToHash = "asdf";
        CheckSum checkSum = CheckSum.compute(new ByteArrayInputStream(valueToHash.getBytes()), false);
        assertEquals(CheckSum.getCurrentVersion(), checkSum.getVersion());
        assertNotEquals(checkSum.toString(), valueToHash);
        assertEquals(CheckSum.compute(valueToHash).toString(), checkSum.toString());
    }

    @Test
    public void toString_test() {
        assertTrue(CheckSum.parse("9:asdf").toString().startsWith("9:"));
    }

    @Test
    public void equals() {
        assertEquals(CheckSum.parse("9:asdf"), CheckSum.parse("9:asdf"));
        assertNotEquals(CheckSum.parse("9:asdf"), CheckSum.parse("8:asdf"));
        assertNotEquals(CheckSum.parse("9:asdf"), CheckSum.parse("9:qwert"));

        assertNotEquals(12, CheckSum.parse("9:asdf"));
        assertNotEquals(null, CheckSum.parse("9:asdf"));
    }

    @Test
    public void compute_lineEndingsDontMatter() {
        String checkSum = CheckSum.compute("a string\nwith\nlines").toString();
        assertEquals(checkSum, CheckSum.compute("a string\rwith\rlines").toString());
        assertEquals(checkSum, CheckSum.compute("a string\r\nwith\r\nlines").toString());
        assertEquals(checkSum, CheckSum.compute("a string\rwith\nlines").toString());

        assertNotEquals(checkSum, CheckSum.compute("a string\n\nwith\n\nlines").toString());

        assertEquals(checkSum, CheckSum.compute(new ByteArrayInputStream("a string\nwith\nlines".getBytes()), true).toString());
        assertEquals(checkSum, CheckSum.compute(new ByteArrayInputStream("a string\rwith\rlines".getBytes()), true).toString());
        assertEquals(checkSum, CheckSum.compute(new ByteArrayInputStream("a string\r\nwith\r\nlines".getBytes()), true).toString());
        assertEquals(checkSum, CheckSum.compute(new ByteArrayInputStream("a string\rwith\r\nlines".getBytes()), true).toString());
    }

    @Test
    public void compute_lineEndingsDontMatter_multiline() {
        String checkSum = CheckSum.compute("a string\n\nwith\n\nlines").toString();
        assertEquals(checkSum, CheckSum.compute("a string\r\rwith\r\rlines").toString());
        assertEquals(checkSum, CheckSum.compute("a string\r\n\r\nwith\r\n\r\nlines").toString());

        assertEquals(checkSum, CheckSum.compute(new ByteArrayInputStream("a string\n\nwith\n\nlines".getBytes()), true).toString());
        assertEquals(checkSum, CheckSum.compute(new ByteArrayInputStream("a string\r\rwith\r\rlines".getBytes()), true).toString());
        assertEquals(checkSum, CheckSum.compute(new ByteArrayInputStream("a string\r\n\r\nwith\r\n\r\nlines".getBytes()), true).toString());
    }

    /*
    * cd liquibase-core
    *
    * On Windows powershell:
    * > mvn -Dtest='liquibase.change.CheckSumTest' test
    *   - compute_inputStreamSpecialCharacters         fails with 8:0f2f39ab98028d13b51a6b686d3d7c89 md5
    *   - compute_inputStreamSpecialCharacterssWithISO fails with 8:5d489836209736e5f781feb17bc3a6ac md5
    * > mvn -D'file.encoding'=UTF-8 -Dtest='liquibase.change.CheckSumTest' test
    *   - compute_inputStreamSpecialCharacters         fails with 8:0f2f39ab98028d13b51a6b686d3d7c89 md5
    *   - compute_inputStreamSpecialCharacterssWithISO fails with 8:5d489836209736e5f781feb17bc3a6ac md5
    *
    * On Linux bash shell:
    * > mvn -Dtest=liquibase.change.CheckSumTest test
    *   - compute_inputStreamSpecialCharacterssWithISO fails with 8:5d489836209736e5f781feb17bc3a6ac md5
    * > mvn -Dfile.encoding=UTF-8 -Dtest=liquibase.change.CheckSumTest test
    *   - compute_inputStreamSpecialCharacterssWithISO fails with 8:5d489836209736e5f781feb17bc3a6ac md5
    *
    */
    private static final String TEST_STRING_SPECIAL_CHARACTERS = "² & é~ # '{ ([ -| è` _ ç^ à@ )°] =+} ^¨ $£¤ ù% *µ ,? ;. :/ !§ €êë îïì";
    private static final String TEST_STRING_SPECIAL_CHARACTERS_MD5_HASH = "8:6adc50b9de9cfc3b3e790257a7e1c08b";
    @Test
    public void compute_specialCharacters() {
        String checkSum = CheckSum.compute(TEST_STRING_SPECIAL_CHARACTERS).toString();
        assertEquals(TEST_STRING_SPECIAL_CHARACTERS_MD5_HASH, checkSum);
    }
    @Test
    public void compute_inputStreamSpecialCharacters() {
        String inputStreamCheckSum = CheckSum.compute(new ByteArrayInputStream(TEST_STRING_SPECIAL_CHARACTERS.getBytes()), true).toString();
        assertEquals(TEST_STRING_SPECIAL_CHARACTERS_MD5_HASH, inputStreamCheckSum);
    }
    @Test
    public void compute_inputStreamSpecialCharactersWithUTF8() throws UnsupportedEncodingException {
        String inputStreamCheckSum = CheckSum.compute(new ByteArrayInputStream(TEST_STRING_SPECIAL_CHARACTERS.getBytes("UTF-8")), true).toString();
        assertEquals(TEST_STRING_SPECIAL_CHARACTERS_MD5_HASH, inputStreamCheckSum);
    }
    @Test
    public void compute_inputStreamSpecialCharacterssWithISO() throws UnsupportedEncodingException {
        String inputStreamCheckSum = CheckSum.compute(new ByteArrayInputStream(TEST_STRING_SPECIAL_CHARACTERS.getBytes("ISO-8859-1")), true).toString();
        assertEquals(TEST_STRING_SPECIAL_CHARACTERS_MD5_HASH, inputStreamCheckSum);
    }
}
