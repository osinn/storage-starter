package com.gitee.osinn.storage.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * @author wency_cai
 **/
public class StorageException extends RuntimeException {

    private Integer status = BAD_REQUEST.value();

    /**
     * 构造函数初始化异常对象
     *
     * @param message 异常信息
     */
    public StorageException(String message) {
        super(message);
    }

    /**
     * 构造函数初始化异常对象
     *
     * @param message 异常信息
     */
    public StorageException(Integer status, String message) {
        super(message);
        this.status = status;
    }

}
