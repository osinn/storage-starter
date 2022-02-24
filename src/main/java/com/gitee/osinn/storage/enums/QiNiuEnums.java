package com.gitee.osinn.storage.enums;

import com.qiniu.storage.Region;
import lombok.Getter;

/**
 * 七牛云枚举
 *
 * @author wency_cai
 **/
public class QiNiuEnums {

    /**
     * 七牛云存储机房
     */
    @Getter
    public enum RegionEnum {

        /**
         * 华东
         */
        region0(Region.region0()),

        /**
         * 华北
         */
        region1(Region.region1()),

        /**
         * 华南
         */
        region2(Region.region2()),

        /**
         * 北美
         */
        regionNa0(Region.regionNa0()),

        /**
         * 东南亚
         */
        regionAs0(Region.regionAs0());

        private final Region region;

        RegionEnum(Region region) {
            this.region = region;
        }
    }
}
