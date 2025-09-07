// package com.xuecheng;
//
// import com.xuecheng.content.model.po.CoursePublish;
// import com.xuecheng.learning.feignclient.ContentServiceClient;
// import org.junit.jupiter.api.Assertions;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.context.annotation.Import;
//
// /**
//  * @author Mr.M
//  * @version 1.0
//  * @description TODO
//  * @date 2023/2/22 20:14
//  */
// @Import(com.xuecheng.learning.Test1.class)
// @SpringBootTest
// public class FeignClientTest {
//
//     @Autowired
//     ContentServiceClient contentServiceClient;
//
//
//     @Test
//     public void testContentServiceClient() {
//         CoursePublish coursepublish = contentServiceClient.getCoursepublish(121L);
//         Assertions.assertNotNull(coursepublish);
//     }
// }
