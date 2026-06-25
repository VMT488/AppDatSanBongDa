package com.example.datsanbong.services;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordService {

    /**
     * Hash password bằng MD5
     */
    public static String hashMD5(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] messageDigest = md.digest(password.getBytes());

            BigInteger no = new BigInteger(1, messageDigest);

            String hashText = no.toString(16);

            while (hashText.length() < 32) {
                hashText = "0" + hashText;
            }

            return hashText;

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Lỗi hash MD5", e);
        }
    }

    /**
     * Kiểm tra password nhập vào có khớp hash hay không
     */
    public static boolean verifyMD5(String rawPassword, String hashedPassword) {
        return hashMD5(rawPassword).equals(hashedPassword);
    }
}