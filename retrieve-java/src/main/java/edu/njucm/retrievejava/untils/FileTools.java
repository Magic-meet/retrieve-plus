package edu.njucm.retrievejava.untils;

import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class FileTools {

    /***
     * 获取文件后缀名
     * @param fileName
     * @return
     */
    public static String getFileExtName(String fileName) {
        int index = fileName.lastIndexOf(".");
        if (index != -1) {
            return fileName.substring(index + 1);
        } else {
            return null;
        }
    }
}
