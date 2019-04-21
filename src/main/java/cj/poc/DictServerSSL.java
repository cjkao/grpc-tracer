package cj.poc;

import cj.grpc.BulkRequest;
import cj.grpc.BulkResponse;
import cj.grpc.BulkServiceGrpc;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rayt on 5/16/16.
 */
@Slf4j
public class DictServerSSL {
    private static DataSrc poc = new DataSrc("/tmp/jdb", false);
    Server server;

    static public void main(String[] args) throws IOException, InterruptedException, ParseException {
        //***Parsing Stage***
        //Create a parser
        CommandLineParser parser = new DefaultParser();

        Options options = new Options();
        options.addOption("p", "prop", true, "use prop file");
        //parse the options passed as command line arguments
        CommandLine cmd = parser.parse(options, args);

        //***Interrogation Stage***
        //hasOptions checks if option is present or not
        if (cmd.hasOption("p")) {
           var str= cmd.getOptionValue("p");
           log.info("read :"+ str);
        } else {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("DB Serv", options);
        }

        var edc = new DictServerSSL();
        edc.start();
        edc.blockUntilShutdown();

    }

    public void start() throws IOException {
        File crtF = new File("server.crt");
        File pemF = new File("server.pem");
        server = ServerBuilder.forPort(9090)
                .useTransportSecurity(crtF, pemF)
                .addService(new GreetingServiceImpl())

                .build().start();

        log.info("Starting server...");
        log.info("Server started!");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                DictServerSSL.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    public void stop() {

        if (server != null) {
            System.err.println("*** shutting down db");
            poc.close();
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
        public void greeting(BulkRequest request, StreamObserver<BulkResponse> responseObserver) {
            poc.runDB(request);
            String greeting = "Hello there, " + request.getSubject() + " total:" + request.getOneSecCount();
            var response = BulkResponse.newBuilder().setGreeting(greeting).build();
            responseObserver.onNext(response);
            observers.stream().forEach(k -> k.onNext(response));
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
                    var resp = BulkResponse.newBuilder().setGreeting("from other" + value.getSubject()).build();

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
         *
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
                    var resp = BulkResponse.newBuilder().setGreeting("server reply " + value.getSubject() + " " + count).build();

                    observers.stream().forEach(k -> k.onNext(resp));
                }

                @Override
                public void onError(Throwable t) {
                    responseObserver.onError(t);
                    log.info("client err complete:__");
                    observers.remove(responseObserver);
                }

                @Override
                public void onCompleted() {
                    log.info("client complete:__");
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