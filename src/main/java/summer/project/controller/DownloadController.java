package summer.project.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import summer.project.common.lang.Result;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Api(tags = {"下载"})
@RestController
@Slf4j
public class DownloadController {
    @Value("${storage.pathname}")
    private String pathname;

    @GetMapping("/download")
    public Result download(@RequestParam("filename") String filename, HttpServletResponse response) {
        //设置文件路径
        File file = new File(pathname + filename);
        //File file = new File(realPath , fileName);
        if (file.exists()) {
            response.setContentType("application/force-download");
            // 设置强制下载不打开
            response.addHeader("Content-Disposition", "attachment;fileName=" + filename);
            // 设置文件名
            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
                return Result.succeed(201, null, null);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return Result.fail(null);
    }

    @RequestMapping("/getIcon")
    public Result getIcon(HttpServletResponse response) {
        //设置文件路径
        File file = new File(pathname + "icon.jpg");
        //File file = new File(realPath , fileName);
        if (file.exists()) {
//                response.setContentType("application/force-download");// 设置强制下载不打开
//                response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);// 设置文件名
            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
                return Result.succeed(201, null, null);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return Result.fail(null);
    }
}