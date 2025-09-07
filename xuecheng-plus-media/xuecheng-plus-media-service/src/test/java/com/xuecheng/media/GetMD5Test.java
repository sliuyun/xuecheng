// package com.xuecheng.media;
//
// import org.apache.commons.codec.digest.DigestUtils;
// import org.junit.jupiter.api.Test;
//
// import java.io.*;
// import java.security.MessageDigest;
// import java.security.NoSuchAlgorithmException;
//
// import static org.springframework.util.FileCopyUtils.BUFFER_SIZE;
//
// public class GetMD5Test {
//
//     /**
//      * 计算输入流的MD5值，不会关闭输入流
//      * @param inputStream 要计算MD5的输入流
//      * @return 输入流内容的MD5哈希值（32位小写）
//      * @throws IOException 如果流操作出错
//      * @throws NoSuchAlgorithmException 如果JVM不支持MD5算法
//      */
//     public static String calculateStreamMD5(InputStream inputStream) throws IOException, NoSuchAlgorithmException {
//         MessageDigest md = MessageDigest.getInstance("MD5");
//         try (BufferedInputStream bis = new BufferedInputStream(inputStream, BUFFER_SIZE)) {
//             byte[] buffer = new byte[BUFFER_SIZE];
//             int bytesRead;
//             // 每次读取缓冲区大小的数据并更新消息摘要
//             while ((bytesRead = bis.read(buffer)) != -1) {
//                 md.update(buffer, 0, bytesRead);
//             }
//         }
//         // 将摘要转换为32位小写十六进制字符串
//         return bytesToHex(md.digest());
//     }
//
//     /**
//      * 将字节数组转换为十六进制字符串
//      * @param bytes 要转换的字节数组
//      * @return 十六进制字符串
//      */
//     private static String bytesToHex(byte[] bytes) {
//         final int BUFFER_SIZE = 8192; // 8KB缓冲区，平衡内存占用和效率
//         final char[] HEX_CHARS = "0123456789abcdef".toCharArray();
//
//         char[] hexChars = new char[bytes.length * 2];
//         for (int i = 0; i < bytes.length; i++) {
//             int value = bytes[i] & 0xFF; // 确保值为非负
//             hexChars[i * 2] = HEX_CHARS[value >>> 4]; // 高4位
//             hexChars[i * 2 + 1] = HEX_CHARS[value & 0x0F]; // 低4位
//         }
//         return new String(hexChars);
//     }
//
//     public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
//         File file = new File("D:\\OS\\CentOS-7-x86_64-DVD-2009.iso");
//         FileInputStream inputStream = new FileInputStream(file);
//         long start = System.currentTimeMillis();
//         // String md5 = DigestUtils.md5Hex(inputStream);
//         String md5 = calculateStreamMD5(inputStream);
//
//         System.out.println(md5);
//         long end = System.currentTimeMillis();
//         System.out.println(end - start);
//
//         inputStream.close();
//     }
// }
