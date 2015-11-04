package com.dc.ssh.client.support;

import com.dc.DcException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileSupport {

    public static void writeFile(File file, byte[] bytes) throws DcException {
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(file));
            bos.write(bytes);
            bos.flush();
        } catch (FileNotFoundException e) {
            throw new DcException("File : " + file.getAbsolutePath() + " not found.", e);
        } catch (IOException e) {
            throw new DcException("Error while writing to File : " + file.getAbsolutePath(), e);
        }
        finally {
            if(bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    throw new DcException("Error while closing File : " + file.getAbsolutePath(), e);
                }
            }
        }

    }

    public static byte[] readFile(File file) throws DcException {
        List<Byte> bytesList = new ArrayList<>();
        byte[] result = null;
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            byte[] buffer = new byte[1024];
            int readCount;
            while( (readCount = bis.read(buffer)) > 0) {
                copy(bytesList, buffer, readCount);
            }
            result = convert(bytesList);
        } catch (IOException e) {
            throw new DcException("IOException occurred while reading file : " + file.getAbsolutePath(), e);
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    throw new DcException("IOException occurred while closing input stream for file : " + file.getAbsolutePath(), e);
                }
            }
        }
        return result;

    }

    private static void copy(List<Byte> bytesList, byte[] buffer, int readCount) {
        for(int i=0; i<readCount; i++) {
            bytesList.add(buffer[i]);
        }
    }

    private static byte[] convert(List<Byte> bytesList) {
        byte[] result = new byte[bytesList.size()];
        int currentIndex = 0;
        for(byte b : bytesList) {
            result[currentIndex++] = b;
        }
        return result;
    }

}
