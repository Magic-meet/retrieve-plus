package edu.njucm.retrievejava.kafkaTest;

import edu.njucm.retrievejava.service.KafkaService;
import edu.njucm.retrievejava.untils.TestFileTools;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

@SpringBootTest
public class SendFile {
    @Autowired
    private KafkaService kafkaService;
    @Test
    void Test1() throws IOException {
        String FilePath = "/home/zj/Downloads/1706.03762.pdf";
        MockMultipartFile multipartFile = TestFileTools.convert(FilePath);
        kafkaService.SendPDFFile(multipartFile, 1L);
    }
}
