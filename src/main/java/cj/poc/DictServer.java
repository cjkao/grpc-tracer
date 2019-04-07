package cj.poc;

import cj.grpc.BulkRequest;
import cj.grpc.BulkResponse;
import cj.grpc.BulkServiceGrpc;
import cj.grpc.DictRes;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import io.jaegertracing.Configuration;
import io.opentracing.Tracer;
import io.opentracing.contrib.grpc.ServerTracingInterceptor;
import lombok.extern.slf4j.Slf4j;


import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;


/**
 * Dictionary Service Server
 */
@Slf4j
public class DictServer {
//    private static DataSrc poc = new DataSrc("/tmp/jdb",false);
    Server server;
//    private final Tracer tracer;

    static final Tracer tracer = Configuration.fromEnv().getTracer();
    static public void main(String[] args) throws IOException, InterruptedException {
        var dictServ = new DictServer();
        dictServ.start();
        dictServ.blockUntilShutdown();

    }

    public void start() throws IOException {
        ServerTracingInterceptor tracingInterceptor = new ServerTracingInterceptor(tracer);

        server = ServerBuilder.forPort(9090)
                .addService(tracingInterceptor.intercept(new GreetingServiceImpl())).build().start();

        log.info("Starting server...");
        log.info("Server started!");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                DictServer.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    public void stop() {

        if (server != null) {
            System.err.println("*** shutting down db");
//            poc.close();
            System.err.println("*** shutting down db ok");
            server.shutdown();
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static class GreetingServiceImpl extends BulkServiceGrpc.BulkServiceImplBase {
        private static Set<StreamObserver<BulkResponse>> observers = ConcurrentHashMap.newKeySet();
        @Override
        public void author(cj.grpc.DictReq request,
                           io.grpc.stub.StreamObserver<cj.grpc.DictRes> responseObserver) {
            var res= DictRes.newBuilder().setAuthor("Johannes Brahms").setExplain("a man").setRef("wiki").build();
            if((Math.random()>0.5))
            responseObserver.onNext(res);
            responseObserver.onCompleted();
        }
        @Override
        public void greeting(BulkRequest request, StreamObserver<BulkResponse> responseObserver) {
//            poc.runDB(request);
            String greeting = "Hello there, " + request.getSubject() + " total:" + request.getOneSecCount();
            var response = BulkResponse.newBuilder().setGreeting(greeting).build();
            responseObserver.onNext(response);
            observers.stream().forEach(k-> k.onNext(response));
            responseObserver.onCompleted();
        }

        @Override
        public StreamObserver<BulkRequest> greedyPut(final StreamObserver<BulkResponse> responseObserver) {
            return new StreamObserver<BulkRequest>() {
                private long sum = 0;
                private long count = 0;
                private BulkResponse.Builder builder = BulkResponse.newBuilder();

                @Override
                public void onNext(BulkRequest value) {
                    responseObserver.onNext(builder.build());
                    log.info("value: " + count++);
//                    builder.(value.getName());
                    var resp=BulkResponse.newBuilder().setGreeting("from other"+value.getSubject()).build();

                }

                @Override
                public void onError(Throwable t) {
                    responseObserver.onError(t);
                }

                @Override
                public void onCompleted() {
                    builder
                            .setGreeting("toatl msg:" + count);
                    responseObserver.onCompleted();
                    observers.remove(responseObserver);
                }
            };
        }



        /**
         */
        public StreamObserver<BulkRequest> greedyPutStream(
                StreamObserver<BulkResponse> responseObserver) {

            observers.add(responseObserver);
            return new StreamObserver<BulkRequest>() {
                private long sum = 0;
                private long count = 0;
                private BulkResponse.Builder builder = BulkResponse.newBuilder();

                @Override
                public void onNext(BulkRequest value) {
                    log.info("value: " + count++);
//                    builder.(value.getName());
                    var resp=BulkResponse.newBuilder().setGreeting("server reply "+value.getSubject() + " "+ count).build();

                    observers.stream().forEach(k-> k.onNext(resp));
                }

                @Override
                public void onError(Throwable t) {
                    responseObserver.onError(t);
                    log.info("client err complete:__"  );
                    observers.remove(responseObserver);
                }

                @Override
                public void onCompleted() {
                    log.info("client complete:__"  );
                    builder
                            .setGreeting("toatl msg:" + count);
//                    responseObserver.onNext(builder.build());
//                    responseObserver.onCompleted();
                    observers.remove(responseObserver);
                }
            };
        }
    }
}