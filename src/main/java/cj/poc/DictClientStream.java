package cj.poc;

import cj.grpc.*;
import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@Slf4j
public class DictClientStream {
    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = NettyChannelBuilder.forAddress("localhost", 8080)
                .usePlaintext(true)
                .build();

        var stub = //定義異步的stub
                BulkServiceGrpc.newStub(channel);

       log.info("---client stream rpc---");
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
                //關閉channel
                channel.shutdown();
            }
        };

        var streamObs=stub.greedyPut(responseObserver);

        var begin = System.nanoTime();
        for (int time = 0; time < 10; time++) {
            var oneList = new ArrayList<Eunit>();
            for (int i = 0; i < 5; i++) {
                Eunit eu = Eunit.newBuilder()
                        .setA1((float) Math.random())
                        .setA1((float) Math.random())
                        .setA3((float) Math.random())
                        .setA4((float) Math.random())
                        .setA5((float) Math.random())
                        .setA2((float) Math.random())
                        .setTime(time*i)
                        .build();
                oneList.add(eu);
            }
            streamObs.onNext(
                    BulkRequest.newBuilder()
                            .addAllOneSec(oneList)
                            .build()
            );
            Thread.sleep(10000);
        }
        streamObs.onCompleted();
        Thread.sleep(10000);
        var end = System.nanoTime();
       log.info("time: "+(end - begin) / 1000000);

    }
}



