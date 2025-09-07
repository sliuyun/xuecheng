package com.xuecheng.media.api;

import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFileService;
import com.xuecheng.media.utils.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

/**
 * @author Mr.M
 * @version 1.0
 * @description 媒资文件管理接口
 * @date 2022/9/6 11:29
 */
@Api(value = "媒资文件管理接口", tags = "媒资文件管理接口")
@RestController
public class MediaFilesController {

    private final MediaFileService mediaFileService;

    public MediaFilesController(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

    @ApiOperation("媒资列表查询接口")
    @PostMapping("/files")
    public PageResult<MediaFiles> list(PageParams pageParams, @RequestBody(required = false) QueryMediaParamsDto queryMediaParamsDto) {
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        String companyId = user.getCompanyId();

        return mediaFileService.queryMediaFiels(Long.valueOf(companyId), pageParams, queryMediaParamsDto);
    }

    /**
     *
     * @param filedata   待上传文件，不能为空
     * @param objectName 对象名，可以为空，前端传递时为空，远程调用时不为空
     * @return
     * @throws IOException
     */
    @ApiOperation("上传图片或html")
    @RequestMapping(value = "/upload/coursefile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadFileResultDto upload(@RequestPart(value = "filedata", required = true) MultipartFile filedata, @RequestParam(value= "objectName",required=false) String objectName) throws IOException {

        // 准备上传文件的信息
        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        // 原始文件名称，这个名称包含扩展名
        uploadFileParamsDto.setFilename(filedata.getOriginalFilename());
        // 文件大小
        uploadFileParamsDto.setFileSize(filedata.getSize());
        // 文件类型
        uploadFileParamsDto.setFileType("001001");
        // 创建一个临时文件，如果不指定目录，会创建在C:\Users\zms\AppData\Local\Temp目录下
        // 文件名是 prefix + 随机数字 + suffix
        // 这些临时文件在程序关闭前如果不调用 delete() 方法，就不会被清除
        File tempFile = File.createTempFile("minio", ".temp");
        filedata.transferTo(tempFile);
        // 机构id
        Long companyId = 1232141425L;
        if(!StringUtils.isNotBlank(objectName)){
            SecurityUtil.XcUser user = SecurityUtil.getUser();
            companyId = Long.parseLong(user.getCompanyId());
        }



        // 文件路径
        String localFilePath = tempFile.getAbsolutePath();

        // 调用service上传图片
        UploadFileResultDto uploadFileResultDto = mediaFileService.uploadFile(companyId, uploadFileParamsDto, localFilePath, objectName);

        return uploadFileResultDto;
    }





    @ApiOperation("预览文件")
    @GetMapping("/preview/{mediaId}")
    public RestResponse<String> getPlayUrlByMediaId(@PathVariable String mediaId){

        MediaFiles mediaFiles = mediaFileService.getFileById(mediaId);
        if(mediaFiles == null || StringUtils.isEmpty(mediaFiles.getUrl())){
            XueChengPlusException.cast("视频还没有转码处理");
        }
        return RestResponse.success(mediaFiles.getUrl());
    }

    @ApiOperation("删除文件")
    @DeleteMapping("/{mediaId}")
    public RestResponse<String> deletePlayUrlByMediaId(@PathVariable String mediaId){
        return mediaFileService.deleteMediaFile(mediaId);
    }

}
