package com.gitee.osinn.storage.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

/**
 * @author wency_cai
 **/
@Slf4j
public class FileUtil {

    /**
     * 类Unix路径分隔符
     */
    public static final char UNIX_SEPARATOR = '/';
    /**
     * Windows路径分隔符
     */
    public static final char WINDOWS_SEPARATOR = '\\';
    public static final String EMPTY = "";
    public static final String DOT = ".";


    /**
     * 生成文件文件名称且相对路径
     */
    public static String encodingFileNameRelativePath(String fileName) {
        fileName = DateFormatUtils.format(new Date(), "yyyy-MM-dd") + UNIX_SEPARATOR + encodingFileName(fileName);
        return fileName;
    }


    /**
     * 根据文件名称生成文件相对路径
     */
    public static String fileRelativePath(String fileName) {
        fileName = DateFormatUtils.format(new Date(), "yyyy-MM-dd") + UNIX_SEPARATOR + fileName;
        return fileName;
    }

    /**
     * 生成新的文件名
     */
    public static String encodingFileName(String fileName) {
        fileName = fileName.replace("_", " ");
        fileName = DigestUtils.md5Hex(fileName + System.nanoTime() + RandomStringUtils.randomNumeric(6)) + DOT + extName(fileName);
        return fileName;
    }

    /**
     * 获得文件的扩展名（后缀名），扩展名不带“.”
     *
     * @param fileName 文件名
     * @return 扩展名
     */
    public static String extName(String fileName) {
        if (fileName == null) {
            return null;
        }
        int index = fileName.lastIndexOf(DOT);
        if (index == -1) {
            return "";
        } else {
            String ext = fileName.substring(index + 1);
            // 扩展名中不能包含路径相关的符号
            return containsAny(ext, UNIX_SEPARATOR, WINDOWS_SEPARATOR) ? EMPTY : ext;
        }
    }

    /**
     * 返回文件名<br>
     * <pre>
     * "d:/test/aaa" 返回 "aaa"
     * "/test/aaa.jpg" 返回 "aaa.jpg"
     * </pre>
     *
     * @param filePath 文件
     * @return 文件名
     * @since 4.1.13
     */
    public static String getName(String filePath) {
        if (null == filePath) {
            return null;
        }
        int len = filePath.length();
        if (0 == len) {
            return filePath;
        }
        char c = filePath.charAt(len - 1);
        if (UNIX_SEPARATOR == c || WINDOWS_SEPARATOR == c) {
            // 以分隔符结尾的去掉结尾分隔符
            len--;
        }

        int begin = 0;
        for (int i = len - 1; i > -1; i--) {
            c = filePath.charAt(i);
            if (UNIX_SEPARATOR == c || WINDOWS_SEPARATOR == c) {
                // 查找最后一个路径分隔符（/或者\）
                begin = i + 1;
                break;
            }
        }

        return filePath.substring(begin, len);
    }

    /**
     * 删除最后一个字符
     *
     * @param fileName 待删除的字符
     * @param lastChar 需要删除的字符
     * @return
     */
    public static String delLastChar(String fileName, char... lastChar) {
        if (StringUtils.isEmpty(fileName)) {
            return EMPTY;
        }

        for (char c : lastChar) {
            int index = fileName.lastIndexOf(c);
            if (index >= fileName.length() - 1) {
                return fileName.substring(0, index);
            }
        }
        return fileName;
    }

    /**
     * 删除第一个字符
     *
     * @param fileName 待删除的字符
     * @param lastChar 需要删除的字符
     * @return
     */
    public static String delFirstChar(String fileName, char... lastChar) {
        if (StringUtils.isEmpty(fileName)) {
            return EMPTY;
        }
        for (char c : lastChar) {
            if (fileName.indexOf(c) == 0) {
                return fileName.substring(1);
            }
        }
        return fileName;
    }

    /**
     * 查找指定字符串是否包含指定字符列表中的任意一个字符
     *
     * @param str   指定字符串
     * @param chars 需要检查的字符数组
     * @return 是否包含任意一个字符
     * @since 4.1.11
     */
    public static boolean containsAny(CharSequence str, char... chars) {
        if (StringUtils.isNotEmpty(str)) {
            int len = str.length();
            for (int i = 0; i < len; i++) {
                if (indexOf(chars, str.charAt(i)) > -1) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 返回数组中指定元素所在位置，未找到返回
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回
     * @since 3.0.7
     */
    public static int indexOf(char[] array, char value) {
        if (null != array) {
            for (int i = 0; i < array.length; i++) {
                if (value == array[i]) {
                    return i;
                }
            }
        }
        return -1;
    }
}
