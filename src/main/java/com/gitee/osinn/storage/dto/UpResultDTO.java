package com.gitee.osinn.storage.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 上传结果
 *
 * @author wency_cai
 **/
@Data
public class UpResultDTO implements Serializable {

    /**
     * 文件大小
     */
    public long fileSize;

    /**
     * 文件名称
     */
    public String fileName;

    /**
     * 原始文件名称
     */
    public String olbFileName;

    /**
     * 扩展名称
     */
    public String extFileName;

    /**
     * 文件存储的相对路径
     */
    public String relativePath;

    /**
     * 本地存储绝对路径，如果是云存储则是完整的访问路径
     */
    public String fullFilePath;

    /**
     * 域名(包含http://或https://)
     */
    private String domain;

    /**
     * 七牛云自定义回复内容JSON，如果是异步推送值为null
     */
    private String returnBody;

    /**
     * 本地存储推送的云存储，带回推送云存储接口返回的数据
     */
    private UpResultDTO cloudUpResult;

}
