# spring-storage-starter
> 简易封装文件存储自动配置,可同时使用本地存储、云存储

# 存储类型
- [x] 本地存储
  - 上传文件
  - 下载文件
  - 删除文件
- [x] 七牛云
  - 上传文件
  - 下载文件
  - 删除文件
# 快速开始
- 引入以下依赖(未发布到maven中央仓库)
```
<dependency>
    <groupId>com.gitee.osinn</groupId>
    <artifactId>storage-starter</artifactId>
    <version>1.0</version>
</dependency>
```
# 注入bean
- 可同时注入本地存储bean和七牛云存储bean,注入bean的名称严格要求如下
```
// 本地存储
@Autowired
private FileStorageManager localFileStorageManager;

// 七牛云
@Autowired
private FileStorageManager qiNiuFileStorageManager;
```

# 接口
```
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
     * @param file
     * @return
     * @throws StorageException
     */
    UpResultDTO upload(MultipartFile file) throws StorageException;

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
```