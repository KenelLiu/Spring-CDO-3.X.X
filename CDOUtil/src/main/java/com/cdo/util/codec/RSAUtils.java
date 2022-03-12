package com.cdo.util.codec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayOutputStream;
import java.net.URLDecoder;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;


public class RSAUtils {

    public static final String CHARSET = "UTF-8";
    public static final String RSA_ALGORITHM = "RSA";


    public static Map<String, String> createKeys(int keySize){
        //为RSA算法创建一个KeyPairGenerator对象
        KeyPairGenerator kpg;
        try{
            kpg = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        }catch(NoSuchAlgorithmException e){
            throw new IllegalArgumentException("No such algorithm-->[" + RSA_ALGORITHM + "]");
        }

        //初始化KeyPairGenerator对象,密钥长度
        kpg.initialize(keySize);
        //生成密匙对
        KeyPair keyPair = kpg.generateKeyPair();
        //得到公钥
        Key publicKey = keyPair.getPublic();
        String publicKeyStr = Base64.encodeBase64URLSafeString(publicKey.getEncoded());
        //得到私钥
        Key privateKey = keyPair.getPrivate();
        String privateKeyStr = Base64.encodeBase64URLSafeString(privateKey.getEncoded());
        Map<String, String> keyPairMap = new HashMap<String, String>();
        keyPairMap.put("publicKey", publicKeyStr);
        keyPairMap.put("privateKey", privateKeyStr);

        return keyPairMap;
    }

    /**
     * 得到公钥
     * @param publicKey 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static RSAPublicKey getPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通过X509编码的Key指令获得公钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
        RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
        return key;
    }

    /**
     * 得到私钥
     * @param privateKey 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static RSAPrivateKey getPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通过PKCS#8编码的Key指令获得私钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
        RSAPrivateKey key = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
        return key;
    }

    /**
     * 公钥加密
     * @param data
     * @param publicKey
     * @return
     */
    public static String publicEncrypt(String data, RSAPublicKey publicKey){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), publicKey.getModulus().bitLength()));
        }catch(Exception e){
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 私钥解密
     * @param data
     * @param privateKey
     * @return
     */

    public static String privateDecrypt(String data, RSAPrivateKey privateKey){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data), privateKey.getModulus().bitLength()), CHARSET);
        }catch(Exception e){
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 私钥加密
     * @param data
     * @param privateKey
     * @return
     */

    public static String privateEncrypt(String data, RSAPrivateKey privateKey){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), privateKey.getModulus().bitLength()));
        }catch(Exception e){
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 公钥解密
     * @param data
     * @param publicKey
     * @return
     */

    public static String publicDecrypt(String data, RSAPublicKey publicKey){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data), publicKey.getModulus().bitLength()), CHARSET);
        }catch(Exception e){
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }

    private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize){
        int maxBlock = 0;
        if(opmode == Cipher.DECRYPT_MODE){
            maxBlock = keySize / 8;
        }else{
            maxBlock = keySize / 8 - 11;
        }
        ByteArrayOutputStream
                out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] buff;
        int i = 0;
        try{
            while(datas.length > offSet){
                if(datas.length-offSet > maxBlock){
                    buff = cipher.doFinal(datas, offSet, maxBlock);
                }else{
                    buff = cipher.doFinal(datas, offSet, datas.length-offSet);
                }
                out.write(buff, 0, buff.length);
                i++;
                offSet = i * maxBlock;
            }
        }catch(Exception e){
            throw new RuntimeException("加解密阀值为["+maxBlock+"]的数据时发生异常", e);
        }
        byte[] resultDatas = out.toByteArray();
        try{out.close();}catch(Exception ex){}
        return resultDatas;
    }

    public static String publicKey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDQYTUBmQ-UObC6S85xaBwhbC-mP-T0rTZVYAD5zBsHCs7mvF_ntZwkUo3q9NK8_9hX-O7lPkdqPlnmeFU5A6p-izzEBYAWYRoi5FNd6w2RwoMm0BOeklG8_UAbasnzvGzLG_NM5sAbv7HgE2flrDQkNQTW_8sHPqTfqL4bbRJC1QIDAQAB";
    public static String privateKey="MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBANBhNQGZD5Q5sLpLznFoHCFsL6Y_5PStNlVgAPnMGwcKzua8X-e1nCRSjer00rz_2Ff47uU-R2o-WeZ4VTkDqn6LPMQFgBZhGiLkU13rDZHCgybQE56SUbz9QBtqyfO8bMsb80zmwBu_seATZ-WsNCQ1BNb_ywc-pN-ovhttEkLVAgMBAAECgYBQ2aaLXCvjQw8C-W68Kbzgau2B_8CB_sLaFUnLohnKfbMRa316lchjf3R4-Uza8Bms6HUolSV4X4DaH05RtM5tX6vRMXE-bOyACx8_Tc_f5PAIMd76glKGkgzbJyF1zKxvc8cB9t-NbyclWQYP2tuP2EYIABHDCzt0EnERvhYc8QJBAOviyKC54TY2SG-ij-lQ7NwKBp5P7_opqzGAj4ecRhBMx3o-DVrDiRAODnvUKEIrs4wxBnqHWLJhf7XgmM7jAxMCQQDiJfvnjGu2W5u31C2ZM-iL8hZPFvUs8iNjabe-AKWEJ3aFfmwlzg6xNGKQftSkG-dg9z_35Z5jr4IejB8eKnd3AkAXKjpRrm9zh3ktnWVMoGniYgmsWmVSx-zL-7gctTgcOa4NTzK4iyNWtEqv1nHtraUAS4A7YDdRrUmBbwESsDCJAkB5_FE4IkeLszh43WvHQvfCXVBpUxHnL6mlG0Zwp_qgIYVOFtaRQegs7_vmw3NCHocdh-Eqf-KDFQ3UGnNCm1ebAkEA47PLaxlwQLf7eG5orNn0tLzSvPr5lTAGQpYqBMLluGiOymWNJAVHtXI7o_-XbnQGxrbBErOvs5zjk9SD6HDtlA";
    
    public static String areaPublicKey="MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAI1WK5OGdf2Ows0KdpjCwZjEA5vQ2iz6FZuSTozcc3FdYwQShs5P2hBUznuCvGjzSszKT9MIEiaRyCVkwL8tvAcCAwEAAQ";
    public static String areaPrivateKey="MIIBUwIBADANBgkqhkiG9w0BAQEFAASCAT0wggE5AgEAAkEAjVYrk4Z1_Y7CzQp2mMLBmMQDm9DaLPoVm5JOjNxzcV1jBBKGzk_aEFTOe4K8aPNKzMpP0wgSJpHIJWTAvy28BwIDAQABAkBDMEqo6AvhTOOYO7gjaLpF2DDq-r8tEHoDKetDlW0f348MJCP-owqMRYXmdv2jYhugPFT3X7z54ViTuTLrKQLxAiEA7Jyj8Z6Q2iwclmXsz89jvdNLVecGiMO949FJjCq-AdkCIQCY6vWpGzYpI13TqA_6d4-brh86-E0Qc6D3r-qa2ocg3wIgHiv3R-dFZ1y6pH8IzJMQj_MypxiXn7b-Lt_QNhx5MDkCIFktNKQwvU4SVOZY7TCIxxPn4EiYgwCkHew0B-HUrNcLAiAd29XPZ7XKT5Z_KYAfGaeTc0a4z9E9K1mBqk73YI31ww";
    
    public static void main (String[] args) throws Exception {
       /** Map<String, String> keyMap = RSAUtils.createKeys(512);
        String  publicKey = keyMap.get("publicKey");
        String  privateKey = keyMap.get("privateKey");
    	**/
    	publicKey=areaPublicKey;
    	privateKey=areaPrivateKey;
        System.out.println("公钥: \n\r" + publicKey);
        System.out.println("私钥： \n\r" + privateKey);

        String str="{\"id\":1,\"account\":\"sysadmin\",\"cnname\":\"系统管理员\",\"timestamp\":1617009497,\"expiretime\":1648742400}";       
        System.out.println("\r明文：\r\n" + str);
        
        String encodedData = RSAUtils.privateEncrypt(str, RSAUtils.getPrivateKey(privateKey));
        System.out.println("私钥加密密文：\r\n" + encodedData);

       // String urlDecode=URLDecoder.decode(encodedData, "UTF-8"); 
        //System.out.println("密文urlDecode:\r\n" + urlDecode);
        
        String decodeData = RSAUtils.publicDecrypt(encodedData, RSAUtils.getPublicKey(publicKey));
        System.out.println("公钥解密后文字: \r\n" + decodeData);
    }

}