// package com.xuecheng.media;
//
// import io.minio.ComposeObjectArgs;
// import io.minio.ComposeSource;
// import io.minio.MinioClient;
// import io.minio.UploadObjectArgs;
// import org.junit.jupiter.api.Test;
//
// import java.io.*;
// import java.nio.channels.FileChannel;
// import java.util.List;
// import java.util.stream.Collectors;
// import java.util.stream.Stream;
//
// public class BigFileTest {
//     @Test
//     public void chunk() throws IOException {
//         // 原文件
//         File originFile = new File("D:\\code\\JavaCode\\MiniProject\\xuecheng\\xuecheng-plus-project\\asset\\2.mp4");
//         // 分块大小，设置为5M
//         int chunkSize = 1024 * 1024 * 5;
//         // 分块个数
//         int chunkNum = (int) Math.ceil((originFile.length() * 1.0 / chunkSize));
//         // 分块文件存储目录
//         String chunkFileDir = "D:\\code\\JavaCode\\MiniProject\\xuecheng\\xuecheng-plus-project\\asset\\chunk\\";
//
//         // 原始文件输入通道
//         FileChannel from = new FileInputStream(originFile).getChannel();
//         // 记录原始文件的读取位置
//         long position = 0;
//         for(int i = 0; i < chunkNum; i++){
//             FileChannel to = new FileOutputStream(chunkFileDir + i).getChannel();
//             long transferred = from.transferTo(position, chunkSize, to);
//             position += transferred;
//             to.close();
//         }
//         from.close();
//     }
//
//     @Test
//     public void merge() throws IOException {
//         // 合并后文件
//         File originFile = new File("D:\\code\\JavaCode\\MiniProject\\xuecheng\\xuecheng-plus-project\\asset\\mergefile.mp4");
//         // 输出文件通道
//         FileChannel to = new FileOutputStream(originFile, true).getChannel();
//         // 分块大小，设置为1M
//         int chunkSize = 1024 * 1024;
//         // 分块个数
//         int chunkNum = 9;
//         // 分块文件存储目录
//         String chunkFileDir = "D:\\code\\JavaCode\\MiniProject\\xuecheng\\xuecheng-plus-project\\asset\\chunk\\";
//
//         for(int i = 0; i < chunkNum; i++){
//             FileChannel from = new FileInputStream(chunkFileDir + i).getChannel();
//             long l = from.transferTo(0, from.size(), to);
//             System.out.println(l);
//             from.close();
//         }
//         to.close();
//     }
//
//     // 连接minio
//     static MinioClient minioClient =
//             MinioClient.builder()
//                     .endpoint("http://172.19.186.192:9000")
//                     .credentials("minioadmin", "minioadmin")
//                     .build();
//
//     @Test
//     public void testUpload() throws  Exception{
//         // 准备文件参数
//         for(int i = 0 ; i < 38 ; i++){
//             // bucket()指定存储桶名称，object()指定minio上路径和文件名，filename()指定本地文件路径
//             UploadObjectArgs fileObjectArgs = UploadObjectArgs.builder()
//                     .bucket("testbucket")
//                     .object("/chunk/" + i)
//                     .filename("D:\\code\\JavaCode\\MiniProject\\xuecheng\\xuecheng-plus-project\\asset\\chunk\\" + i)
//                     .build();
//             // 上传文件
//             minioClient.uploadObject(fileObjectArgs);
//         }
//     }
//
//     @Test
//     public void testMerge() throws Exception{
//         List<ComposeSource> sources = Stream.iterate(0, i -> ++i)
//                 .limit(38)
//                 .map(i -> ComposeSource.builder()
//                         .bucket("testbucket")
//                         .object("/chunk/" + i)
//                         .build())
//                 .collect(Collectors.toList());
//
//         ComposeObjectArgs composeObjectArgs = ComposeObjectArgs.builder()
//                 .bucket("testbucket")
//                 .object("merge01.mp4")
//                 .sources(sources)
//                 .build();
//
//         // mino对于分块文件最小要求是5MB才能合并（最后一个不受此限制）
//         minioClient.composeObject(composeObjectArgs);
//     }
//
// }
