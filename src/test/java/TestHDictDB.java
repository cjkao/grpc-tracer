import cj.grpc.DictRes;
import cj.grpc.Row;
import cj.poc.db.AdpCustomer;
//import cj.poc.db.BuilderCustomer;
import cj.poc.db.Customer;
import cj.poc.db.DBManagerHelper;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.ConstructorUtils;
import org.slf4j.Logger;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.Tuple;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Satya Choudhury
 */
@Slf4j
public class TestHDictDB {
    private static final String PERSISTENCE_UNIT_NAME = "people";
    private static EntityManagerFactory factory;
    private static int times=10000;

   /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
//        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        var em=DBManagerHelper.getEntityManager();
//        EntityManager em = factory.createEntityManager();
        Query q = em.createNativeQuery("select firstName, lastName,street,city from CUSTOMER", Tuple.class);
        try {
            List<Tuple> todoList = q.getResultList();

            em.close();
            System.gc();
            System.gc();
            System.gc();
            System.gc();
            var custList3 = new ArrayList<AdpCustomer>();
            long begin = System.nanoTime();
            for (var todo : todoList) {
                var obj = new AdpCustomer(todo.toArray());
                custList3.add(obj);
            }
            perf("manual len :", begin);
            custList3.get(0).getCity();

            List custList2 = new ArrayList();
            begin = System.nanoTime();
            for (var todo : todoList) {
                custList2.add(todo.toArray());
            }
            perf("no new:", begin);


            var custList = new ArrayList<Customer>();
            begin = System.nanoTime();
            for (var todo : todoList) {
                var obj = ConstructorUtils.invokeConstructor(Customer.class, todo.toArray());
                custList.add(obj);
            }
            perf("reflect new:", begin);


            begin = System.nanoTime();



            System.gc();
            begin = System.nanoTime();
            int len=0;
            for (int j = 0; j < times; j++) {
                var custList6 = new ArrayList<DictRes>(15000);
                var res = DictRes.newBuilder();
                for (var todo : todoList) {

                  var row=  Row.newBuilder().addStr(todo.get(1, String.class))
                            .addStr(todo.get(2, String.class))
                            .addStr(todo.get(3, String.class))
                            .addStr(todo.get(0, String.class))
                            .build();
                    res.addRow(row);
//                    custList6.add(res);
                }
                res.build();
            }
            perf10("str raw pb builder len:" + len + " cost:", begin);


            begin = System.nanoTime();
            StringBuilder sb = new StringBuilder();
            len = 0;
            for (int j = 0; j < times; j++) {
                var custList7 = new ArrayList<DictRes>();
                var dicRes=DictRes.newBuilder();
                for (var todo : todoList) {
                    sb.append(todo.get(0, String.class))
                            .append("|||"+j)
                            .append(todo.get(1, String.class))
                            .append("|||"+j)
                            .append(todo.get(2, String.class))
                            .append("|||"+j)
                            .append(todo.get(3, String.class));
                    var res =dicRes.setBb(ByteString.copyFrom(sb.toString().getBytes()));
                    sb.setLength(0);
                }
                dicRes.build();
            }
            perf10("strbu " + len + " com value:", begin);

            begin = System.nanoTime();
            for (int j = 0; j < times; j++) {
                var dicRes=DictRes.newBuilder();
                for (var todo : todoList) {
                    var str = todo.get(0, String.class) +
                            "|||" +j+
                            todo.get(1, String.class) +
                            "|||" +j+
                            todo.get(2, String.class) +
                            "|||" +j+
                            todo.get(3, String.class);
                    dicRes.setBb(ByteString.copyFromUtf8(str));
                }
                dicRes.build();
            }

            perf10("string " + len + " com value:", begin);


        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
//        Month month=(Month)obj;
    }

    private static void perf(String desc, long begin) {
        log.info(desc + " " + ((System.nanoTime() - begin)) / 1000_000 + "ms");
    }
    private static void perf10(String desc, long begin) {
        log.info(desc + " " + ((System.nanoTime() - begin)) /times /1000_1000.0 + "ms");
    }

}
