package io.github.osinn.storage.starter;

import io.github.osinn.storage.manager.FileStorageManager;
import io.github.osinn.storage.manager.LocalFileStorageManager;
import io.github.osinn.storage.provider.ConfigProperties;
import io.github.osinn.storage.manager.QiNiuFileStorageManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
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

    @Bean
    @ConditionalOnProperty(name = "enable", prefix = ConfigProperties.PREFIX + ".local", havingValue = "true")
    public FileStorageManager localFileStorageManager(ConfigProperties properties) {
        return new LocalFileStorageManager(properties, qiNiuFileStorageManager(properties));
    }

    @Bean
    @ConditionalOnProperty(name = "enable", prefix = ConfigProperties.PREFIX + ".qi-niu", havingValue = "true")
    public FileStorageManager qiNiuFileStorageManager(ConfigProperties properties) {
        return new QiNiuFileStorageManager(properties);
    }

}
