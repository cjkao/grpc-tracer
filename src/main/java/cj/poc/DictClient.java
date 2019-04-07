package cj.poc;

import cj.grpc.*;
import io.grpc.ManagedChannel;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NegotiationType;
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
public class DictClient {

    private static SslContext buildSslContext(
                                              String clientCertChainFilePath,
                                              String clientPrivateKeyFilePath) throws SSLException {
        SslContextBuilder builder = GrpcSslContexts.forClient();
        if (clientCertChainFilePath != null && clientPrivateKeyFilePath != null) {
            builder.keyManager(new File(clientCertChainFilePath), new File(clientPrivateKeyFilePath));
        }
        return builder.build();
    }
    DictClient(){

    }
    public static void main(String[] args) throws InterruptedException, SSLException {
//        var sslcontext=buildSslContext("client.crt","client.pem");
        Tracer tracer= Configuration.fromEnv().getTracer();;
        ClientTracingInterceptor tracingInterceptor = new ClientTracingInterceptor(tracer);
        ManagedChannel channel = NettyChannelBuilder.forAddress("localhost", 9090)
//                .negotiationType(NegotiationType.TLS)
//                .sslContext(sslcontext)
                .usePlaintext(true)
                .build();
        var tracedChannel=tracingInterceptor.intercept(channel);
        BulkServiceGrpc.BulkServiceBlockingStub stub =
                BulkServiceGrpc.newBlockingStub(tracedChannel);
        stub.author(DictReq.newBuilder().setAcronym(true).build());
        var begin = System.nanoTime();
        for (int time = 1; time < 10; time++) {
            var oneList = new ArrayList<Eunit>();
            for (int i = 1; i < 50; i++) {
                Eunit eu = Eunit.newBuilder()
                        .setA1((float) Math.random())
                        .setA2((float) Math.random())
                        .setA3((float) Math.random())
                        .setA4((float) Math.random())
                        .setA5((float) Math.random())
                        .setA6((float) Math.random())
                        .setA7((float) Math.random())
                        .setA8((float) Math.random())
                        .setA8((float) Math.random())
                        .setA9((float) Math.random())
                        .setA10((float) Math.random())
                        .setTime( System.currentTimeMillis() +i * time)
                        .setName("table1")
                        .build();
                oneList.add(eu);
            }
            BulkResponse helloResponse = stub.greeting(
                    BulkRequest.newBuilder()
                            .setSentiment(Sentiment.HAPPY)
                            .addAllOneSec(oneList)
                            .build());

           log.info(helloResponse.toString());
        }

        var end = System.nanoTime();
       log.info(" going to shutdown"+(end - begin) / 1000000);
        tracer.close();
        channel.shutdown();
    }
}



