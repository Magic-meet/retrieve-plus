package edu.njucm.retrievejava.untils;

import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class TestFileTools {
    public static MockMultipartFile convert(String filePath) throws IOException {
        // 读取本地文件
        File file = new File(filePath);
        FileInputStream inputStream = new FileInputStream(file);
        byte[] bytes = new byte[(int) file.length()];
        inputStream.read(bytes);
        inputStream.close();
        // 创建 MockMultipartFile 对象
        return new MockMultipartFile(file.getName(), file.getName(), null, bytes);
    }
}
