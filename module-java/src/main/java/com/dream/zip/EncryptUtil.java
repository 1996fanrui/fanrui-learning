package com.dream.zip;


import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.util.UUID;

public class EncryptUtil {

    public static void main(String[] args) throws Exception {
        String message = "Hello, world!";
        byte[] data = message.getBytes("UTF-8");
        byte[] key = generateKey();
        byte[] iv = generateIV();

        byte[] encrypted = encrypt(key, iv, data);
        byte[] decrypted = decrypt(key, iv, encrypted);
        System.out.println("Decrypted: " + new String(decrypted, "UTF-8"));
    }

    private static byte[] generateKey() throws Exception {
        String uuid = UUID.randomUUID().toString();
        byte[] key = uuid.replace("-", "").getBytes("UTF-8");
        System.out.println(uuid);
        return key;
    }

    private static byte[] generateIV() {
        String uuid = UUID.randomUUID().toString();
        System.out.println(uuid);
        byte[] iv = new byte[16];
        System.arraycopy(uuid.replace("-", "").getBytes(), 0, iv, 0, 16);
        return iv;
    }

    public static byte[] encrypt(byte[] input) throws Exception {
        byte[] key = generateKey();
        byte[] iv = generateIV();
        return encrypt(key, iv, input);
    }

    public static byte[] encrypt(byte[] key, byte[] iv, byte[] input) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivps = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivps);
        return cipher.doFinal(input);
    }

    public static byte[] decrypt(byte[] key, byte[] iv, byte[] data) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivps = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivps);
        return cipher.doFinal(data);
    }
}
