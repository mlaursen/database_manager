/**
 * 
 */
package com.github.mlaursen.database.utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author mlaursen
 * 
 */
public class FileUtil {
	
	/**
	 * http://stackoverflow.com/questions/326390/how-to-create-a-java-string-from-the-contents-of-a-file
	 * 
	 * @param path
	 *            The file path
	 * @param encoding
	 *            The encoding to use
	 * @return A string of the file
	 * @throws IOException
	 *             if the file does not exist
	 */
	public static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return encoding.decode(ByteBuffer.wrap(encoded)).toString();
	}
	
	/**
	 * http://stackoverflow.com/questions/326390/how-to-create-a-java-string-from-the-contents-of-a-file {@link #readFile(String, Charset)}
	 * The UTF_8 charset is used be default.
	 * 
	 * @param path
	 *            The file path
	 * @return A string of the file
	 * @throws IOException
	 *             If the file does not exist
	 */
	public static String readFile(String path) throws IOException {
		return readFile(path, StandardCharsets.UTF_8);
	}
}
