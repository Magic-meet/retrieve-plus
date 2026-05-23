package edu.njucm.retrievejava.searchTest;

import edu.njucm.retrievejava.service.DocumentService;
import edu.njucm.retrievejava.vo.ChunkVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

@SpringBootTest
public class GetParagraphs {
    @Autowired
    DocumentService documentService;
    @Test
    void Test1() throws IOException {
        Long Id = 2l;

        List<ChunkVO> chunkVOList = documentService.getDocumentChunks(Id);
        for (ChunkVO chunkVO : chunkVOList){
            System.out.println(chunkVO);
            System.out.println("00000000000000000000000000000000000");
        }
        System.out.println("-------------------------------");
    }
}
