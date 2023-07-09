package com.zyh.choutuan_take_out.controller;


import com.zyh.choutuan_take_out.common.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${choutuan.pic-path}")
    private String picPath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        String originalFilename = file.getOriginalFilename();
        String fileName = UUID.randomUUID().toString();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        File dir = new File(picPath);
        if(!dir.exists()){
            dir.mkdirs();
        }
        try {
            file.transferTo(new File(picPath + fileName + suffix));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName + suffix);
    }

    @GetMapping("download")
    public void download(String name, HttpServletResponse response){

        FileInputStream fileInputStream = null;
        ServletOutputStream servletOutputStream = null;
        try {
            fileInputStream = new FileInputStream(picPath + name);

            servletOutputStream = response.getOutputStream();

            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];
            while((len = fileInputStream.read(bytes)) != -1){
                servletOutputStream.write(bytes, 0, len);
                servletOutputStream.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fileInputStream != null){
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(servletOutputStream != null){
                try {
                    servletOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

}
