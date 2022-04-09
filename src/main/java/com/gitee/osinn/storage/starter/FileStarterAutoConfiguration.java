package com.gitee.osinn.storage.starter;

import com.gitee.osinn.storage.manager.FileStorageManager;
import com.gitee.osinn.storage.manager.LocalFileStorageManager;
import com.gitee.osinn.storage.provider.ConfigProperties;
import com.gitee.osinn.storage.manager.QiNiuFileStorageManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * 文件存储自动配置
 *
 * @author wency_cai
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(ConfigProperties.class)
public class FileStarterAutoConfiguration {

    @Bean(name = "localFileStorageManager")
    @ConditionalOnProperty(name = "enable", prefix = ConfigProperties.PREFIX + ".local", havingValue = "true")
    public FileStorageManager localFileStorageManager(ConfigProperties properties) {
        return new LocalFileStorageManager(properties, qiNiuFileStorageManager(properties));
    }

    @Bean(name = "qiNiuFileStorageManager")
    @ConditionalOnProperty(name = "enable", prefix = ConfigProperties.PREFIX + ".qi-niu", havingValue = "true")
    public FileStorageManager qiNiuFileStorageManager(ConfigProperties properties) {
        return new QiNiuFileStorageManager(properties);
    }

}
