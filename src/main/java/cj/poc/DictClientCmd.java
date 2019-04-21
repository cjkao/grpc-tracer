package cj.poc;

import cj.grpc.BulkRequest;
import cj.grpc.BulkResponse;
import cj.grpc.BulkServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
@Slf4j
public class DictClientCmd {
    public static void main(String[] args) throws InterruptedException, IOException {
        ManagedChannel channel = NettyChannelBuilder.forAddress("localhost", 8080)
//                .useTransportSecurity()
                .usePlaintext(true)
                .build();

        var begin = System.nanoTime();
        var stub =
                BulkServiceGrpc.newStub(channel);
        BulkServiceGrpc.newFutureStub(channel);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//        System.out.print("Enter String");
        StreamObserver<BulkResponse> responseObserver = new StreamObserver<BulkResponse>() {
            @Override
            public void onNext(BulkResponse result) {
               log.info("client stream--" + result.getGreeting());
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
               log.info("client stream--exception" );
            }

            @Override
            public void onCompleted() {
               log.info("channel stop" );

                //關閉channel
                channel.shutdown();
            }
        };
        var reqStub=stub.greedyPutStream(responseObserver);
        System.out.println("press quit to exit");
        while(true) {

            String s = br.readLine();
            if(s.equals("quit")) break;
            var  req=  BulkRequest.newBuilder()
                            .setSubject(s).build();

            reqStub.onNext(req);

        }
        reqStub.onCompleted();
        channel.shutdown();
        var end = System.nanoTime();
       log.info(" "+(end - begin) / 1000000);
        System.in.read();

    }
}



