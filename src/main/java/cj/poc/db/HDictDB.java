package cj.poc.db;

import cj.grpc.DictRes;
import cj.grpc.Row;
import org.slf4j.Logger;

import javax.persistence.Query;
import javax.persistence.Tuple;
import java.util.List;



public class HDictDB {
    //    private static final String PERSISTENCE_UNIT_NAME = "people";
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(HDictDB.class);
    //    private static EntityManagerFactory factory;
    private static int times = 10000;

    public DictRes getRes(cj.grpc.Query query) {
        var em = DBManagerHelper.getEntityManager();
        Query q = em.createNativeQuery(query.getQstr(), Tuple.class);
        var dictRes = DictRes.newBuilder();//.addStr(str).build());
        try {
            List<Tuple> results = q.getResultList();
            for (var item : results) {
                int len = item.toArray().length;
                var row = Row.newBuilder();
                for (int cnt = 0; cnt < len; cnt++) {

                    row.addStr(item.get(cnt).toString());
                }
                dictRes.addRow(row.build());

            }
            dictRes.setSize(results.size());
        } catch (Exception e) {
            log.error("query exception", e);
        }
        return dictRes.build();
    }


}
