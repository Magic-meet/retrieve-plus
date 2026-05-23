package edu.njucm.retrievejava.rpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@io.grpc.stub.annotations.GrpcGenerated
public final class Word2VectorServiceGrpc {

  private Word2VectorServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "retrieve.Word2VectorService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<edu.njucm.retrievejava.rpc.StringRequest,
      edu.njucm.retrievejava.rpc.DoubleArrayResponse> getProcessStringMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "processString",
      requestType = edu.njucm.retrievejava.rpc.StringRequest.class,
      responseType = edu.njucm.retrievejava.rpc.DoubleArrayResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<edu.njucm.retrievejava.rpc.StringRequest,
      edu.njucm.retrievejava.rpc.DoubleArrayResponse> getProcessStringMethod() {
    io.grpc.MethodDescriptor<edu.njucm.retrievejava.rpc.StringRequest, edu.njucm.retrievejava.rpc.DoubleArrayResponse> getProcessStringMethod;
    if ((getProcessStringMethod = Word2VectorServiceGrpc.getProcessStringMethod) == null) {
      synchronized (Word2VectorServiceGrpc.class) {
        if ((getProcessStringMethod = Word2VectorServiceGrpc.getProcessStringMethod) == null) {
          Word2VectorServiceGrpc.getProcessStringMethod = getProcessStringMethod =
              io.grpc.MethodDescriptor.<edu.njucm.retrievejava.rpc.StringRequest, edu.njucm.retrievejava.rpc.DoubleArrayResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "processString"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  edu.njucm.retrievejava.rpc.StringRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  edu.njucm.retrievejava.rpc.DoubleArrayResponse.getDefaultInstance()))
              .setSchemaDescriptor(new Word2VectorServiceMethodDescriptorSupplier("processString"))
              .build();
        }
      }
    }
    return getProcessStringMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static Word2VectorServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<Word2VectorServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<Word2VectorServiceStub>() {
        @java.lang.Override
        public Word2VectorServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new Word2VectorServiceStub(channel, callOptions);
        }
      };
    return Word2VectorServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static Word2VectorServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<Word2VectorServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<Word2VectorServiceBlockingStub>() {
        @java.lang.Override
        public Word2VectorServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new Word2VectorServiceBlockingStub(channel, callOptions);
        }
      };
    return Word2VectorServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static Word2VectorServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<Word2VectorServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<Word2VectorServiceFutureStub>() {
        @java.lang.Override
        public Word2VectorServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new Word2VectorServiceFutureStub(channel, callOptions);
        }
      };
    return Word2VectorServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void processString(edu.njucm.retrievejava.rpc.StringRequest request,
        io.grpc.stub.StreamObserver<edu.njucm.retrievejava.rpc.DoubleArrayResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getProcessStringMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service Word2VectorService.
   */
  public static abstract class Word2VectorServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return Word2VectorServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service Word2VectorService.
   */
  public static final class Word2VectorServiceStub
      extends io.grpc.stub.AbstractAsyncStub<Word2VectorServiceStub> {
    private Word2VectorServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected Word2VectorServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new Word2VectorServiceStub(channel, callOptions);
    }

    /**
     */
    public void processString(edu.njucm.retrievejava.rpc.StringRequest request,
        io.grpc.stub.StreamObserver<edu.njucm.retrievejava.rpc.DoubleArrayResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getProcessStringMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service Word2VectorService.
   */
  public static final class Word2VectorServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<Word2VectorServiceBlockingStub> {
    private Word2VectorServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected Word2VectorServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new Word2VectorServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public edu.njucm.retrievejava.rpc.DoubleArrayResponse processString(edu.njucm.retrievejava.rpc.StringRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getProcessStringMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service Word2VectorService.
   */
  public static final class Word2VectorServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<Word2VectorServiceFutureStub> {
    private Word2VectorServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected Word2VectorServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new Word2VectorServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<edu.njucm.retrievejava.rpc.DoubleArrayResponse> processString(
        edu.njucm.retrievejava.rpc.StringRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getProcessStringMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_PROCESS_STRING = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_PROCESS_STRING:
          serviceImpl.processString((edu.njucm.retrievejava.rpc.StringRequest) request,
              (io.grpc.stub.StreamObserver<edu.njucm.retrievejava.rpc.DoubleArrayResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getProcessStringMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              edu.njucm.retrievejava.rpc.StringRequest,
              edu.njucm.retrievejava.rpc.DoubleArrayResponse>(
                service, METHODID_PROCESS_STRING)))
        .build();
  }

  private static abstract class Word2VectorServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    Word2VectorServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return edu.njucm.retrievejava.rpc.RetrieveProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Word2VectorService");
    }
  }

  private static final class Word2VectorServiceFileDescriptorSupplier
      extends Word2VectorServiceBaseDescriptorSupplier {
    Word2VectorServiceFileDescriptorSupplier() {}
  }

  private static final class Word2VectorServiceMethodDescriptorSupplier
      extends Word2VectorServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    Word2VectorServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (Word2VectorServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new Word2VectorServiceFileDescriptorSupplier())
              .addMethod(getProcessStringMethod())
              .build();
        }
      }
    }
    return result;
  }
}
