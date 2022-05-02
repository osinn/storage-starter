package com.gitee.osinn.storage.manager;

import com.gitee.osinn.storage.provider.ConfigProperties;
import com.gitee.osinn.storage.utils.FileUtil;
import com.google.common.base.Charsets;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * @author wency_cai
 */
@Slf4j
public abstract class AbstractManager {


    protected ListeningExecutorService executorService;

    AbstractManager() {

    }

    AbstractManager(int corePoolSize) {
        executorService = MoreExecutors.listeningDecorator(new ScheduledThreadPoolExecutor(corePoolSize));
    }

    public String getModelName(String model) {
        return StringUtils.isNotBlank(model) ? model : FileUtil.EMPTY;
    }

    /**
     * 设置Servlet下载流基本信息
     *
     * @param response 响应对象
     * @param fileName 下载文件名称
     * @param fileSize 下载文件大小
     */
    public void setServletStreamResponse(HttpServletResponse response, String fileName, Long fileSize) {
        response.setContentType("application/octet-stream");
        response.setHeader("content-type", "application/octet-stream");
        if (fileSize != null) {
            response.setHeader("Content-Length", "" + fileSize);
        }
        response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes(Charsets.UTF_8), Charsets.ISO_8859_1));
    }

    /**
     * 效验上传文件
     *
     * @param file       上传的文件
     * @param properties 配置
     */
    public void validityFile(MultipartFile file, ConfigProperties properties) {
        String extensionFilename = FileUtil.extName(file.getOriginalFilename());
        if (properties.getDefaultAllowedExtension() != null && !properties.getDefaultAllowedExtension().contains(extensionFilename)) {
            throw new RuntimeException("只允许" + ArrayUtils.toString(properties.getDefaultAllowedExtension()) + "文件上传");
        }
        if (file.getSize() > properties.getDefaultMaxSize()) {
            throw new RuntimeException("文件过大");
        }
        if (file.getOriginalFilename().length() > properties.getDefaultFileNameLength()) {
            throw new RuntimeException("文件名过长");
        }
    }


    public void error404(HttpServletResponse response, boolean isOutErrorHtml) {
        if (isOutErrorHtml) {
            return;
        }
        try {
            response.setCharacterEncoding(Charsets.UTF_8.toString());
            PrintWriter pw = response.getWriter();
            String html = "<!DOCTYPE html>" +
                    "<html lang=\"zh-CN\"><title>未找到资源</title><body>" +
                    "<head><meta charset=\"utf-8\"/></head>" +
                    "<div style='color:red;text-align:center'>抱歉!未找到您要下载的资源……</div>" +
                    "</body></html>";
            pw.write(html);
            pw.flush();
            pw.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
