package com.github.osinn.storage.manager;

import com.github.osinn.storage.dto.UpResultDTO;
import com.github.osinn.storage.utils.FileUtil;
import com.github.osinn.storage.exception.StorageException;
import com.github.osinn.storage.provider.ConfigProperties;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 本地存储
 *
 * @author wency_cai
 */
@Slf4j
public class LocalFileStorageManager extends AbstractManager implements FileStorageManager {

    private final ConfigProperties properties;

    /**
     * 保存文件路径名
     */
    private final String uploadDir;

    public LocalFileStorageManager(ConfigProperties properties) {
        super(properties.getCorePoolSize());
        this.properties = properties;
        this.uploadDir = FileUtil.delLastChar(properties.getLocal().getPathName(), FileUtil.UNIX_SEPARATOR, FileUtil.WINDOWS_SEPARATOR);
    }

    @Override
    public int delete(String relativePath) {
        //取得当前主机存放项目的绝对路径
        relativePath = FileUtil.delFirstChar(relativePath, FileUtil.UNIX_SEPARATOR, FileUtil.WINDOWS_SEPARATOR);
        //获得文件存放的绝对路径
        String fullFilePath = uploadDir + File.separator + relativePath;
        //删除文件
        File deleteFile = new File(fullFilePath);
        if (deleteFile.exists() && deleteFile.isFile() && deleteFile.delete()) {
            return 200;
        }
        return 500;
    }

    @Override
    public UpResultDTO upload(MultipartFile file) throws StorageException {
        return this.upload(file, null);
    }

    @Override
    public UpResultDTO upload(MultipartFile file, String model) throws StorageException {
        if (uploadDir == null || uploadDir.length() == 0) {
            throw new StorageException("文件保存路径为空");
        }
        try {
            validityFile(file, properties);
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String fileName = FileUtil.encodingFileName(file.getOriginalFilename());
            String extName = FileUtil.extName(file.getOriginalFilename());
            String modelName = getModelName(model);
            String fileRelativePath;
            if (StringUtils.isNotBlank(modelName)) {
                fileRelativePath = modelName + File.separator + datePath + File.separator + FileUtil.fileRelativePath(fileName);
            } else {
                fileRelativePath = datePath + File.separator + FileUtil.fileRelativePath(fileName);
            }
            File newFile = getAbsoluteFile(fileRelativePath);
            UpResultDTO upResult = new UpResultDTO();
            upResult.setFileSize(file.getSize());
            upResult.setFileName(fileName);
            upResult.setOlbFileName(file.getOriginalFilename());
            upResult.setExtFileName(extName);
            upResult.setRelativePath(fileRelativePath);
            upResult.setFullFilePath(uploadDir + File.separator + fileRelativePath);
            upResult.setDomain(properties.getLocal().getDomain());
            file.transferTo(newFile);
            return upResult;
        } catch (Exception e) {
            throw new StorageException(e.getMessage());
        }

    }

    @Override
    public void downloadFile(HttpServletResponse response, String path) {

        if (StringUtils.isNotEmpty(path)) {
            String fileName;
            File file;
            try {
                fileName = FileUtil.getName(path);
                //取得当前主机存放项目的绝对路径
                path = FileUtil.delFirstChar(path, FileUtil.UNIX_SEPARATOR, FileUtil.WINDOWS_SEPARATOR);
                //获得文件存放的绝对路径
                String fullFilePath = uploadDir + File.separator + path;
                //设置文件路径
                file = new File(fullFilePath);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                error404(response, properties.isOutErrorHtml());
                return;
            }
            if (file.exists()) {
                setServletStreamResponse(response, fileName, file.length());
                try (FileInputStream fis = new FileInputStream(file); BufferedInputStream bis = new BufferedInputStream(fis)) {
                    byte[] buffer = new byte[1024];
                    OutputStream os = response.getOutputStream();
                    int i = bis.read(buffer);
                    while (i != -1) {
                        os.write(buffer, 0, i);
                        i = bis.read(buffer);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            } else {
                error404(response, properties.isOutErrorHtml());
            }
        } else {
            error404(response, properties.isOutErrorHtml());
        }
    }

    @Override
    public String getToken() {
        throw new StorageException("本地存储不支持获取token");
    }

    @Override
    public String getDomain() {
        return properties.getLocal().getDomain();
    }

    /**
     * 创建文件
     *
     * @param filename
     * @return
     * @throws IOException
     */
    private File getAbsoluteFile(String filename) throws IOException {
        filename = FileUtil.delFirstChar(filename, FileUtil.UNIX_SEPARATOR, FileUtil.WINDOWS_SEPARATOR);
        File desc = new File(uploadDir + File.separator + filename);
        if (!desc.getParentFile().exists()) {
            desc.getParentFile().mkdirs();
        }
        if (!desc.exists()) {
            desc.createNewFile();
        }
        return desc;
    }

}
