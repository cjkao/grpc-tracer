package cj.poc;

import cj.grpc.BulkRequest;
import cj.grpc.Query;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataSrc {
    boolean readOnly;

    {
    }

//    DBOptions options;
//    List<ColumnFamilyHandle> tables=new ArrayList<>();
//    List<ColumnFamilyDescriptor> cfDescs;
//    List<Integer> ttls;
//    private DB db;

    public static void main(String[] args) throws InvalidProtocolBufferException {
        var p = new DataSrc("/tmp/jdb", false);
//        RocksDB.open("/tmp/jdb");
//        p.read(Query.newBuilder().setBegin(1).setEnd(100).build());
    }

    private String dbName;

    public DataSrc(String dbName, boolean readonly) {

    }

    public void close() {
    }


    public void runDB(BulkRequest req) {
        open();

    }

   public void open() {
   }

    public BulkRequest read(Query query) {

           return null;

    }
}
