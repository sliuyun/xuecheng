package com.xuecheng.media.api;

import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.service.MediaFileService;
import com.xuecheng.media.utils.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @author Mr.M
 * @version 1.0
 * @description 大文件上传接口
 * @date 2022/9/6 11:29
 */
@Api(value = "大文件上传接口", tags = "大文件上传接口")
@RestController
public class BigFilesController {

    private final MediaFileService mediaFileService;

    public BigFilesController(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

    @ApiOperation(value = "文件上传前检查文件")
    @PostMapping("/upload/checkfile")
    public RestResponse<Boolean> checkfile(
            @RequestParam("fileMd5") String fileMd5
    ) throws Exception {
        return mediaFileService.checkFile(fileMd5);
    }


    /**
     * 分块文件上传前的检测
     * 假如分块在上传过程被中断，重新上传分块时，会从第一块开始校验（前端没有存储从那块开始没有上传的字段）
     * （不完整的分块会一直保存在minio，直到它合并成文件后才会销毁），直到分块在minio上不存在，就会开始请求uploadchunk方法真正上传分块
     * @param fileMd5 完整文件的MD5值
     * @param chunk   分块序号
     * @return        响应
     */
    @ApiOperation(value = "分块文件上传前的检测")
    @PostMapping("/upload/checkchunk")
    public RestResponse<Boolean> checkchunk(@RequestParam("fileMd5") String fileMd5,
                                            @RequestParam("chunk") int chunk) throws Exception {
        return mediaFileService.checkChunk(fileMd5, chunk);
    }

    /**
     * 上传分块文件
     * 前端传递的file对象需要先写入磁盘，因为minio不接受file对象，它需要的是磁盘上的文件
     * @param file      分块文件，每一个分块文件都需要前端单独发起一次请求，后端将分块单独存入minio
     * @param fileMd5   完整文件的MD5
     * @param chunk     分块序号
     * @return          响应
     */
    @ApiOperation(value = "上传分块文件")
    @PostMapping("/upload/uploadchunk")
    public RestResponse uploadchunk(@RequestParam("file") MultipartFile file,
                                    @RequestParam("fileMd5") String fileMd5,
                                    @RequestParam("chunk") int chunk) throws Exception {
        // 创建临时文件
        File tempFile = File.createTempFile("minio", ".temp");
        file.transferTo(tempFile);
        // 临时文件路径
        String absolutePath = tempFile.getAbsolutePath();
        // 存储分块
        RestResponse restResponse = mediaFileService.uploadChunk(fileMd5, chunk, absolutePath);

        // 删除临时文件
        if(tempFile.exists()){
            tempFile.delete();
        }
        return restResponse;
    }

    /**
     * 合并文件
     * @param fileMd5       完整文件的MD5值
     * @param fileName      文件名
     * @param chunkTotal    待合并的分块数
     */
    @ApiOperation(value = "合并文件")
    @PostMapping("/upload/mergechunks")
    public RestResponse mergechunks(@RequestParam("fileMd5") String fileMd5,
                                    @RequestParam("fileName") String fileName,
                                    @RequestParam("chunkTotal") int chunkTotal) throws Exception {
        // Long companyId = 1232141425L;
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        Long companyId = Long.valueOf(user.getCompanyId());

        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        uploadFileParamsDto.setFileType("001002");
        uploadFileParamsDto.setTags("课程视频");
        uploadFileParamsDto.setRemark("");
        uploadFileParamsDto.setFilename(fileName);

        return mediaFileService.mergechunks(companyId,fileMd5,chunkTotal,uploadFileParamsDto);

    }


}
