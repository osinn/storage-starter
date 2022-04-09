package com.gitee.osinn.storage.manager;

import com.gitee.osinn.storage.dto.UpResultDTO;
import com.gitee.osinn.storage.exception.StorageException;
import com.gitee.osinn.storage.provider.ConfigProperties;
import com.gitee.osinn.storage.utils.FileUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 七牛云存储
 *
 * @author wency_cai
 */
@Slf4j
public class QiNiuFileStorageManager extends AbstractManager implements FileStorageManager {

    private final ConfigProperties properties;
    private final UploadManager uploadManager;
    private final BucketManager bucketManager;
    private final Auth auth;

    public QiNiuFileStorageManager(ConfigProperties properties) {
        super(properties.getCorePoolSize());
        this.properties = properties;
        this.auth = Auth.create(properties.getQiNiu().getAccessKey(), properties.getQiNiu().getSecretKey());
        Configuration cfg = new Configuration(properties.getQiNiu().getRegionEnum().getRegion());
        this.uploadManager = new UploadManager(cfg);
        this.bucketManager = new BucketManager(auth, cfg);
    }

    @Override
    public int delete(String key) {
        String domain = properties.getQiNiu().getDomain();
        key = key.replace("http://" + domain + "/", "");
        key = key.replace("https://" + domain + "/", "");
        Response delete = null;
        try {
            delete = bucketManager.delete(properties.getQiNiu().getBucket(), key);
            return delete.statusCode;
        } catch (QiniuException e) {
            Response r = e.response;
            log.error(r.toString(), e);
            return r.statusCode;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return 500;
    }

    @Override
    public UpResultDTO upload(MultipartFile file) throws StorageException {
        return this.upload(file, null);
    }

    @Override
    public UpResultDTO upload(MultipartFile file, String key) throws StorageException {
        validityFile(file, properties);
        String fileName = FileUtil.encodingFileName(file.getOriginalFilename());
        String fileRelativePath = getModelName(key) + FileUtil.fileRelativePath(fileName);
        UpResultDTO upResult = new UpResultDTO();
        try {
            if (properties.isAsyncUpload()) {
                executorService.submit(() -> {
                    try {
                        this.byteToQiNiuCloud(file.getBytes(), fileRelativePath, upResult);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                });
            } else {
                this.byteToQiNiuCloud(file.getBytes(), fileRelativePath, upResult);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new StorageException(e.getMessage());
        }

        String extName = FileUtil.extName(file.getOriginalFilename());
        upResult.setFileSize(file.getSize());
        upResult.setFileName(fileName);
        upResult.setOlbFileName(file.getOriginalFilename());
        upResult.setExtFileName(extName);
        upResult.setRelativePath(fileRelativePath);
        upResult.setFullFilePath(properties.getQiNiu().getDomain() + FileUtil.UNIX_SEPARATOR + fileRelativePath);
        upResult.setDomain(properties.getQiNiu().getDomain());
        return upResult;
    }

    @Override
    public void downloadFile(HttpServletResponse response, String url) {
        OkHttpClient client = new OkHttpClient();
        System.out.println(url);
        Request req = new Request.Builder().url(url).build();
        okhttp3.Response resp = null;
        InputStream inputStream = null;
        try {
            resp = client.newCall(req).execute();
            if (resp.isSuccessful()) {
                String fileName = FileUtil.getName(url);
                ResponseBody body = resp.body();
                if (body != null) {
                    inputStream = body.byteStream();
                    setServletStreamResponse(response, fileName, body.contentLength());
                    OutputStream os = response.getOutputStream();
                    byte[] buffer = new byte[1024];
                    int i = inputStream.read(buffer);
                    while (i != -1) {
                        os.write(buffer, 0, i);
                        i = inputStream.read(buffer);
                    }
                }
            } else {
                error404(response, properties.isOutErrorHtml());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            error404(response, properties.isOutErrorHtml());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public UpResultDTO localUploadCloud(String filePath, String relativePath, boolean asyncUpload) throws StorageException {
        File file = new File(filePath);
        if (!file.isFile()) {
            throw new StorageException("推送云存储本地文件不存在");
        }
        UpResultDTO upResult = new UpResultDTO();
        try {
            if (StringUtils.isEmpty(relativePath)) {
                relativePath = FileUtil.fileRelativePath(file.getName());
            }
            if (asyncUpload) {
                String finalRelativePath = relativePath;
                executorService.submit(() -> {
                    Response response = null;
                    try {
                        response = uploadManager.put(filePath, finalRelativePath, this.getToken());
                        if (response.statusCode != 200) {
                            log.error("本地上传七牛云错误，状态码：{},错误信息：{}", response.statusCode, response.error);
                        }
                    } catch (QiniuException e) {
                        response = e.response;
                        log.error("本地上传七牛云错误，状态码：{},错误信息：{}", response.statusCode, response.error);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                });
            } else {
                Response response = uploadManager.put(filePath, relativePath, this.getToken());
                upResult.setReturnBody(response.bodyString());
                if (response.statusCode != 200) {
                    throw new StorageException(response.statusCode, response.error);
                }
            }
            String extName = FileUtil.extName(file.getName());
            upResult.setFileSize(file.length());
            upResult.setFileName(file.getName());
            upResult.setOlbFileName(file.getName());
            upResult.setExtFileName(extName);
            upResult.setRelativePath(relativePath);
            upResult.setFullFilePath(properties.getQiNiu().getDomain() + FileUtil.UNIX_SEPARATOR + relativePath);
            upResult.setDomain(properties.getQiNiu().getDomain());
            return upResult;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new StorageException(e.getMessage());
        }

    }

    @Override
    public String getToken() {
        return auth.uploadToken(properties.getQiNiu().getBucket(),
                null,
                properties.getQiNiu().getExpireSeconds(),
                new StringMap().put("returnBody", properties.getQiNiu().getReturnBody()));
    }

    @Override
    public String getDomain() {
        return properties.getQiNiu().getDomain();
    }

    private void byteToQiNiuCloud(byte[] uploadBytes, String fileName, UpResultDTO upResult) throws Exception {
        Response response = uploadManager.put(uploadBytes, fileName, this.getToken());
        upResult.setReturnBody(response.bodyString());
        //解析上传成功的结果
//        UpQiNiuResultDTO putRet = Json.decode(response.bodyString(), UpQiNiuResultDTO.class);
//        log.info("上传成功响应数据：【{}】", response.bodyString());
        if (response.statusCode != 200) {
            throw new StorageException(response.statusCode, response.error);
        }
    }

}
