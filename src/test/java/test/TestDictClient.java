package test;

import cj.grpc.BulkServiceGrpc;
import cj.grpc.Query;
import cj.poc.domain.AdpCustomer;
import cj.poc.domain.Invoice;
import cj.poc.util.Perf;
import io.grpc.ManagedChannel;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
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

    TestDictClient() {

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
        Perf.begin();
        BulkServiceGrpc.BulkServiceBlockingStub stub = BulkServiceGrpc.newBlockingStub(channel);
        int loopCnt=10;
        String query="select firstName, lastName,street,city from CUSTOMER";
        String queryInv="select * from invoice";
        for (int loop = 0; loop < loopCnt; loop++) {
            var retRaw = stub.query(Query.newBuilder().setQstr(query).build());
            var custList = new ArrayList<AdpCustomer>();
            for (var row : retRaw.getRowList()) {
                var customer = new AdpCustomer(row.getStrList().toArray());
                custList.add(customer);
            }
            log.info("total: " + custList.size());

            var invList = new ArrayList<Invoice>();
            retRaw = stub.query(Query.newBuilder().setQstr(queryInv).build());
            for (var row : retRaw.getRowList()) {
                var inv = new Invoice(row.getStrList().toArray());
                invList.add(inv);
            }
            log.info("total inv: " + invList.size());

        }

        Perf.perf10(" going to shutdown, time" ,loopCnt );
        channel.shutdown();
    }
}



