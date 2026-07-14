package com.shaik.bulkimport.util;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.web.multipart.MultipartFile;

public class FileHashUtil {

    public static String generateHash(MultipartFile file) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    digest.digest(file.getBytes());

            StringBuilder builder = new StringBuilder();

            for (byte b : hash) {
                builder.append(
                        String.format("%02x", b));
            }

            return builder.toString();

        } catch (NoSuchAlgorithmException | IOException ex) {

            throw new RuntimeException(
                    "Unable to generate file hash.", ex);
        }
    }
}