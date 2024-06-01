package com.bit.springboot.controller;

import cn.hutool.core.util.StrUtil;
import com.bit.springboot.common.BaseResponse;
import com.bit.springboot.common.ResultUtils;
import com.bit.springboot.constant.FileConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author LeLe
 * @date 2024/5/23 17:25
 * @Description:
 */

//todo 完善文件上传，和修改图片
@RestController
@RequestMapping("/upload")
@Slf4j
public class FileUploadController {

    @PostMapping("/file")
    public BaseResponse<String> upload(@RequestParam("file")MultipartFile file){
        try {
            // 获取原始文件名称
            String originalFilename = file.getOriginalFilename();
            // 生成新文件名
            List<String> newFileList = createNewFileName(originalFilename);
            String fileName = newFileList.get(1);
            // 保存文件
            file.transferTo(new File(newFileList.get(0), fileName));
            // 返回结果
            log.debug("文件上传成功，{}", newFileList.get(2)+fileName);
            return ResultUtils.success(newFileList.get(2)+File.separator+fileName);
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败", e);
        }
    }

    private List<String> createNewFileName(String originalFilename) {
        List<String> list = new ArrayList<>();
        // 获取后缀
        String suffix = StrUtil.subAfter(originalFilename, ".", true);
        // 生成目录
        String name = UUID.randomUUID().toString();
        String fileName = name+'.'+suffix;
        String folderName = LocalDate.now().toString();
        // 判断目录是否存在
        File dir = new File(FileConstant.UPLOAD_FILE+folderName);
//        System.out.println(dir.toString());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // 生成文件名
        list.add(FileConstant.UPLOAD_FILE + folderName + File.separator);
        list.add(fileName);
        list.add(folderName);
        return list;
    }
}
