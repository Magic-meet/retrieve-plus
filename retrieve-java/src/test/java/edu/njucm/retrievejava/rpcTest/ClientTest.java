package edu.njucm.retrievejava.rpcTest;

import edu.njucm.retrievejava.service.Impl.RPCServiceImpl;
import edu.njucm.retrievejava.service.RPCService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
public class ClientTest {
    @Autowired
    RPCService rpcService;

    @Test
    void test1(){
        String inputString = "Attention is all you need";
        System.out.println("输入："+inputString);
        double[] doubleArray = rpcService.processString(inputString);
        System.out.println("输出："+ Arrays.toString(doubleArray));

    }
}
