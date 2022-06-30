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
- 引入以下依赖
```
<dependency>
    <groupId>io.github.osinn</groupId>
    <artifactId>storage-starter</artifactId>
    <version>1.0</version>
</dependency>
```
# `application.yml` 配置
```
file:
  storage:
    # 本地存储配置
    local:
      # 启用本地存储配置
      enable: true
      # 本地文件存储绝对路径
      path-name: /Users/dev/tmp/storage
      # 存到本地服务器后是否推送到七牛云
      to-qi-niu: true
      # 本地文件访问路径，例如nginx文件服务器访问地址
      domain: http://xxx.nginx.com
    # 七牛云存储配置
    qi-niu:
      # 启用七牛云存储配置
      enable: true
      access-key: 你的七牛云access-key
      secret-key: 你的七牛云secret-key
      bucket: 你的七牛云存储空间(桶)
      region-enum: region2 #(七牛云存储机房)
      # 你的七牛云存储空间文件访问域名
      domain: http://xxx.qiniu.com
      # 自定义上传七牛云回复内容
      return-body: "{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"bucket\":\"$(bucket)\",\"fileSize\":$(fsize),\"fileName\":\"${fname}\"}"
    # 是否开启异步推送到云存储
    async-upload: false
    core-pool-size: 5 # 异步推送线程池核心数，默认5
    default-max-size: 10 * 1024 * 1024 #默认大小 10M
    default-file-name-length: # 默认文件名称长度
    # 允许上传到文件格式
    default-allowed-extension:
      - png
      - jpg
    # 下载错误是否输出404页面内容
    out-error-html: false
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
# 上传接口返回内容
- 以本地存储为例

```
{
    //文件大小 
    "fileSize": 32533,
    //文件名称
    "fileName": "3520055c3ca20ad00137033f597d56ec.jpg",
    //原始文件名称
    "olbFileName": "761640910663_.pic.jpg",
    // 文件后缀
    "extFileName": "jpg",
    // 文件存储相对路径
    "relativePath": "2022-02-24/3520055c3ca20ad00137033f597d56ec.jpg",
    //本地存储绝对路径，如果是云存储则是完整的访问路径(URL)
    "fullFilePath": "/Users/dev/tmp/storage/2022-02-24/3520055c3ca20ad00137033f597d56ec.jpg",
    //域名
    "domain": "http://xxx.nginx.com",
    // 七牛云自定义回复内容
    "returnBody": null,
    // 如果本地存储同时推送云存储，带回云存储接口返回到数据
    "cloudUpResult": null
}
```