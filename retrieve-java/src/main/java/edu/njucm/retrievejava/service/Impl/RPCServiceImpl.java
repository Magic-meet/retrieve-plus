package edu.njucm.retrievejava.service.Impl;

import edu.njucm.retrievejava.rpc.DoubleArrayResponse;
import edu.njucm.retrievejava.rpc.StringRequest;
import edu.njucm.retrievejava.rpc.Word2VectorServiceGrpc;
import edu.njucm.retrievejava.service.RPCService;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class RPCServiceImpl implements RPCService {
    @GrpcClient("word2VectorService")
    private Word2VectorServiceGrpc.Word2VectorServiceBlockingStub word2VectorServiceStub;

    public double[] processString(String inputString) {
        StringRequest request = StringRequest.newBuilder().setInputString(inputString).build();
        DoubleArrayResponse response = word2VectorServiceStub.processString(request);
        return response.getResultArrayList().stream()
                .mapToDouble(Double::doubleValue)
                .toArray();
    }




}
