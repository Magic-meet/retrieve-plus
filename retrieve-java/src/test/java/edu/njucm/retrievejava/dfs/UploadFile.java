package edu.njucm.retrievejava.dfs;

import edu.njucm.retrievejava.service.FileService;
import edu.njucm.retrievejava.untils.FileTools;
import edu.njucm.retrievejava.untils.TestFileTools;
import org.csource.common.MyException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

@SpringBootTest
public class UploadFile {

    @Autowired
    FileService fileService;
    @Test
    void Test1() throws IOException, MyException {
        String FilePath = "/home/zj/Downloads/1706.03762.pdf";
        MockMultipartFile multipartFile = TestFileTools.convert(FilePath);
        String[] strings= fileService.uploadFileToDFS(multipartFile);
    }
}
