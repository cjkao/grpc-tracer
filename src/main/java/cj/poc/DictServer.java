package cj.poc;

import cj.grpc.BulkRequest;
import cj.grpc.BulkResponse;
import cj.grpc.BulkServiceGrpc;
import cj.poc.db.DBManagerHelper;
import cj.poc.db.*;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import io.jaegertracing.Configuration;
import io.opentracing.Tracer;
import io.opentracing.contrib.grpc.ServerTracingInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Dictionary Service Server
 */
@Slf4j
public class DictServer {
    //    private static DataSrc poc = new DataSrc("/tmp/jdb",false);
    Server server;

    static final Tracer tracer = Configuration.fromEnv().getTracer();


    static public void main(String[] args) throws IOException, InterruptedException, ParseException {
        //Create a parser
        CommandLineParser parser = new DefaultParser();

        Options options = new Options();
        options.addOption("p", "prop", true, "use prop file");
        //parse the options passed as command line arguments
        CommandLine cmd = parser.parse(options, args);

        //***Interrogation Stage***
        //hasOptions checks if option is present or not
        if (cmd.hasOption("p")) {
            var str = cmd.getOptionValue("p");
            log.info("read :" + str);
            DBManagerHelper.loadProp(str);
            var dictServ = new DictServer();
            dictServ.start();
            dictServ.blockUntilShutdown();
        } else {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("DB Serv", options);
        }
    }

    public void start() throws IOException {
        var tracercingInterceptor = new ServerTracingInterceptor(tracer);
        server = ServerBuilder.forPort(9090)
                .addService(tracercingInterceptor.intercept(new GreetingServiceImpl())).build().start();

//        server = ServerBuilder.forPort(9090)
//                .addService(new GreetingServiceImpl()).build().start();

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
        /**
         */
        @Override
        public void query(cj.grpc.Query request,
                          io.grpc.stub.StreamObserver<cj.grpc.DictRes> responseObserver) {

           HDictDB db=new HDictDB();
           var ret=db.getRes(request);
           responseObserver.onNext(ret);
           responseObserver.onCompleted();

        }

//        /**
//         *
//         */
//        public StreamObserver<BulkRequest> greedyPutStream(
//                StreamObserver<BulkResponse> responseObserver) {
//
//            observers.add(responseObserver);
//            return new StreamObserver<BulkRequest>() {
//                private long sum = 0;
//                private long count = 0;
//                private BulkResponse.Builder builder = BulkResponse.newBuilder();
//
//                @Override
//                public void onNext(BulkRequest value) {
//                    log.info("value: " + count++);
////                    builder.(value.getName());
//                    var resp = BulkResponse.newBuilder().setGreeting("server reply " + value.getSubject() + " " + count).build();
//
//                    observers.stream().forEach(k -> k.onNext(resp));
//                }
//
//                @Override
//                public void onError(Throwable t) {
//                    responseObserver.onError(t);
//                    log.info("client err complete:__");
//                    observers.remove(responseObserver);
//                }
//
//                @Override
//                public void onCompleted() {
//                    log.info("client complete:__");
//                    builder
//                            .setGreeting("toatl msg:" + count);
////                    responseObserver.onNext(builder.build());
////                    responseObserver.onCompleted();
//                    observers.remove(responseObserver);
//                }
//            };
//        }
    }
}