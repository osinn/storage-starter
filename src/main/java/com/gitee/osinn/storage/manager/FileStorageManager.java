package com.gitee.osinn.storage.manager;

import com.gitee.osinn.storage.dto.UpResultDTO;
import com.gitee.osinn.storage.exception.StorageException;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;


/**
 * 文件存储管理
 *
 * @author wency_cai
 */
public interface FileStorageManager {

    /**
     * 删除文件
     *
     * @param relativePath 相对路径
     * @return 返回状态码 200成功，其余失败
     */
    int delete(String relativePath);

    /**
     * 上传文件
     *
     * @param file 上传文件
     * @return
     * @throws StorageException
     */
    UpResultDTO upload(MultipartFile file) throws StorageException;

    /**
     * 上传文件
     *
     * @param file  上传文件
     * @param model 保存模块文件夹名称
     * @return
     * @throws StorageException
     */
    UpResultDTO upload(MultipartFile file, String model) throws StorageException;

    /**
     * 本地文件推送到云存储
     *
     * @param filePath     文件本地绝对路径
     * @param relativePath 文件本地相对路径，空则自动生成云存储相对路径
     * @return
     * @throws StorageException
     */
    UpResultDTO localUploadCloud(String filePath, String relativePath, boolean asyncUpload) throws StorageException;

    /**
     * 下载文件
     *
     * @param response
     * @param key      如果是本地文件下载则是文件相对路径，七牛云则是完整的URL
     */
    void downloadFile(HttpServletResponse response, String key);

    /**
     * 获取云存储token
     *
     * @return 云存储返回token，本地存储返回null
     */
    String getToken();

    /**
     * 域名(包含http://或https://)
     *
     * @return 访问域名
     */
    String getDomain();

}
