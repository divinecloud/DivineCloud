/*
 * Copyright (C) 2014 Divine Cloud Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dc.util.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.io.IOUtils;

public class FileSupport {
	public static void writeTextFile(String folderPath, String fileName, String data, boolean isExecutable) throws IOException {
		File dir = new File(folderPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File file = new File(dir, fileName);
		FileWriter fileWriter = null;
		BufferedWriter bufferedWriter = null;
		try {
			fileWriter = new FileWriter(file);
			bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(data);
			bufferedWriter.flush();
			if (isExecutable) {
				file.setExecutable(true);
			}
		} finally {
			if (fileWriter != null) {
				fileWriter.close();
			}
			if (bufferedWriter != null) {
				bufferedWriter.close();
			}
		}
	}

	public static void appendToFile(File file, String data) throws IOException {
		FileWriter fileWriter = null;
		BufferedWriter bufferedWriter = null;
		try {
			fileWriter = new FileWriter(file, true);
			bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(data);
			bufferedWriter.flush();
		} finally {
			if (fileWriter != null) {
				fileWriter.close();
			}
			if (bufferedWriter != null) {
				bufferedWriter.close();
			}
		}
	}

	public static void writeBinaryFile(String folderPath, String fileName, byte[] data) throws IOException {
		File dir = new File(folderPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File file = new File(dir, fileName);
		writeBinaryFile(file, data);
	}

	public static void writeBinaryFile(File file, byte[] data) throws IOException {
		FileOutputStream fileOutputStream = null;
		BufferedOutputStream bufferedOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(file);
			bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
			bufferedOutputStream.write(data, 0, data.length);
			bufferedOutputStream.flush();
		} finally {
			if (bufferedOutputStream != null) {
				fileOutputStream.close();
			}
			if (bufferedOutputStream != null) {
				bufferedOutputStream.close();
			}
		}
	}

	public static void writeBinaryFile(File inputFile, File outputFile) throws IOException {
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(inputFile));
			bos = new BufferedOutputStream(new FileOutputStream(outputFile));
			copyStream(bis, bos);
		} finally {
			if (bis != null) {
				bis.close();
			}
			if (bos != null) {
				bos.close();
			}
		}

	}

	public static void writeBinaryFile(InputStream in, File targetFile) throws IOException {
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			bis = new BufferedInputStream(in);
			bos = new BufferedOutputStream(new FileOutputStream(targetFile));
			copyStream(bis, bos);
		} finally {
			if (bis != null) {
				bis.close();
			}
			if (bos != null) {
				bos.close();
			}
		}

	}

	public static void writeBinaryFile(InputStream in, File targetFile, boolean encrypted, String key) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			bis = new BufferedInputStream(in);
			bos = new BufferedOutputStream(new FileOutputStream(targetFile));
			if (encrypted) {
				copyEncryptedStream(bis, bos, key);
			} else {
				copyStream(bis, bos);
			}
		} finally {
			if (bis != null) {
				bis.close();
			}
			if (bos != null) {
				bos.close();
			}
		}

	}

	private static void copyEncryptedStream(InputStream input, OutputStream output, String key) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
		Cipher dcipher = Cipher.getInstance("AES");
		CipherInputStream cis = null;
		try {
			dcipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes(), "AES"));
			cis = new CipherInputStream(input, dcipher);
			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = cis.read(buffer)) != -1) {
				output.write(buffer, 0, bytesRead);
			}
			output.flush();
		} finally {
			if (cis != null) {
				cis.close();
			}
		}
	}

	private static void copyStream(InputStream input, OutputStream output) throws IOException {
		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = input.read(buffer)) != -1) {
			output.write(buffer, 0, bytesRead);
		}
	}

	public static void copyAllFiles(String sourcePath, String destPath) throws IOException {
		File s = new File(sourcePath);
		String[] files = s.list();
		for (String file : files) {
			String sourceFile = sourcePath + "/" + file;
			File destination = new File(destPath + "/" + file);
			InputStream is = new BufferedInputStream(new FileInputStream(sourceFile));
			copy(is, destination);
		}
	}

	public static void copy(InputStream is, File destinationFile) throws IOException {
		OutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(destinationFile));
			byte[] buf = new byte[1024];
			int len;
			while ((len = is.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	public static void copy(byte[] bytes, File destinationFile) throws IOException {
		OutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(destinationFile));
			out.write(bytes, 0, bytes.length);
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	public static byte[] readFile(String file) throws IOException {
		byte[] content = null;
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			content = IOUtils.toByteArray(reader);
		}
		return content;
	}

}
