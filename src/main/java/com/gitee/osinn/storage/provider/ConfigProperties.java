package com.gitee.osinn.storage.provider;

import com.gitee.osinn.storage.enums.QiNiuEnums;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 配置
 *
 * @author wency_cai
 **/
@Data
@ConfigurationProperties(prefix = ConfigProperties.PREFIX)
public class ConfigProperties {

    public static final String PREFIX = "file.storage";

    /**
     * 默认大小 10M
     */
    private long defaultMaxSize = 10 * 1024 * 1024;

    /**
     * 默认的文件名最大长度 100
     */
    private int defaultFileNameLength = Integer.MAX_VALUE;

    /**
     * 允许上传文件的后缀名-空则不限制
     */
    private List<String> defaultAllowedExtension;

    /**
     * 下载错误是否输出html提示语
     */
    private boolean outErrorHtml;

    /**
     * 是否异步上传，仅作用与推送云存储
     */
    private boolean asyncUpload;

    /**
     * 线程池核心数，仅作用于异步上传
     */
    private int corePoolSize = 5;

    /**
     * 本地存储配置
     */
    private Local local;

    /**
     * 七牛云存储配置
     */
    private QiNiuCloud qiNiu;

    /**
     * 本地存储配置类
     */
    @Data
    public static class Local {

        /**
         * 保存文件路径名
         */
        private String pathName;

        /**
         * 是否上传之七牛云
         */
        private boolean toQiNiu;

        /**
         * 域名(包含http://或https://)
         */
        private String domain;
    }

    /**
     * 七牛云配置类
     */
    @Data
    public static class QiNiuCloud {

        /**
         * 账号的access_key
         */
        private String accessKey;

        /**
         * 账号的secret_key
         */
        private String secretKey;

        /**
         * 存储空间
         */
        private String bucket;

        /**
         * token过期时间，单位秒
         */
        private int expireSeconds = 3600;

        /**
         * 自定义回复内容
         * 例如："{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"bucket\":\"$(bucket)\",\"fileSize\":$(fsize),\"fileName\":\"${fname}\",\"imageInfo\":${imageInfo},\"avinfo\":${avinfo}}"
         */
        private String returnBody = "{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"fileSize\":$(fsize),\"fileName\":\"${fname}\"}";

        /**
         * 七牛云存储机房默认华东
         */
        private QiNiuEnums.RegionEnum regionEnum = QiNiuEnums.RegionEnum.region0;

        /**
         * 域名(包含http://或https://)
         */
        private String domain;
    }
}
