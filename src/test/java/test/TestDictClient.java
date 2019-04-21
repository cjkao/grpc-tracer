package test;

import cj.grpc.*;
import io.grpc.ManagedChannel;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import io.jaegertracing.Configuration;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.opentracing.Tracer;
import io.opentracing.contrib.grpc.ClientTracingInterceptor;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLException;
import java.io.File;
import java.util.ArrayList;

@Slf4j
public class TestDictClient {

    private static SslContext buildSslContext(
                                              String clientCertChainFilePath,
                                              String clientPrivateKeyFilePath) throws SSLException {
        SslContextBuilder builder = GrpcSslContexts.forClient();
        if (clientCertChainFilePath != null && clientPrivateKeyFilePath != null) {
            builder.keyManager(new File(clientCertChainFilePath), new File(clientPrivateKeyFilePath));
        }
        return builder.build();
    }
    TestDictClient(){

    }
    public static void main(String[] args) throws InterruptedException, SSLException {
//        var sslcontext=buildSslContext("client.crt","client.pem");
//        Tracer tracer= Configuration.fromEnv().getTracer();;
//        ClientTracingInterceptor tracingInterceptor = new ClientTracingInterceptor(tracer);
        ManagedChannel channel = NettyChannelBuilder.forAddress("localhost", 9090)
//                .negotiationType(NegotiationType.TLS)
//                .sslContext(sslcontext)
                .usePlaintext(true)
                .build();
//        var tracedChannel=tracingInterceptor.intercept(channel);
//        BulkServiceGrpc.BulkServiceBlockingStub stub =
//                BulkServiceGrpc.newBlockingStub(tracedChannel);

        var begin = System.nanoTime();
        BulkServiceGrpc.BulkServiceBlockingStub stub =
                BulkServiceGrpc.newBlockingStub(channel);
        var ret =stub.query(Query.newBuilder().build());
       log.info("total: "+ ret.getRowList().size()) ;

       var end = System.nanoTime();
       log.info(" going to shutdown, time"+(end - begin) / 1000000);
       channel.shutdown();
    }
}



