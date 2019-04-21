package cj.poc.db;

import cj.grpc.DictRes;
import cj.grpc.Row;
import org.apache.commons.beanutils.ConstructorUtils;
import org.slf4j.Logger;

import javax.persistence.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Satya Choudhury
 */

public class HDictDB {
    private static final String PERSISTENCE_UNIT_NAME = "people";
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(HDictDB.class);
    private static EntityManagerFactory factory;
    private static int times=10000;

    public DictRes getRes(cj.grpc.Query query) {
        var em = DBManagerHelper.getEntityManager();
        Query q = em.createNativeQuery("select firstName, lastName,street,city from CUSTOMER", Tuple.class);
        var dictRes=DictRes.newBuilder();//.addStr(str).build());
        try {
            List<Tuple> todoList = q.getResultList();
            for (var todo : todoList) {
                int len=todo.toArray().length;
                var row=Row.newBuilder();
                for(int cnt=0;cnt<len;cnt++){
                    row.addStr(todo.get(cnt,String.class));
                }
               dictRes.addRow(row.build()) ;
            }
        }catch (Exception e){
            log.error("query exception",e);
        }
        return dictRes.build();
    }
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

//            var custList4 = new ArrayList<AutoCustomer>();
//            begin = System.nanoTime();
//            for (var todo : todoList) {
//                custList4.add(AutoValue_AutoCustomer.create2(todo));
//            }
//            perf("enum auto value:", begin);

//            begin = System.nanoTime();
//
//            custList4.clear();
//            for (var todo : todoList) {
//                custList4.add(AutoCustomer.create(todo));
//            }
//            perf("auto value:", begin);

//            begin = System.nanoTime();


//            for (int j = 0; j < times; j++) {
//                var builderList = new ArrayList<BuilderCustomer>();
//                for (var todo : todoList) {
//                    builderList.add(BuilderCustomer.builder().firstName(todo.get(0).toString())
//                            .lastName(todo.get(1).toString())
//                            .city(todo.get(2).toString())
//                            .street(todo.get(3).toString()).numberOfLegs(2)
//                            .build());
//                }
//            }
//            perf10("Object builder cost:", begin);
            System.gc();
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
            }
            perf10("str raw pb builder len:" + len + " cost:", begin);


//            begin = System.nanoTime();
//            StringBuilder sb = new StringBuilder();
//            len = 0;
//            for (int j = 0; j < times; j++) {
//                var custList7 = new ArrayList<DictRes>();
//                for (var todo : todoList) {
//                    sb.append(todo.get(0, String.class))
//                            .append("|||"+j)
//                            .append(todo.get(1, String.class))
//                            .append("|||"+j)
//                            .append(todo.get(2, String.class))
//                            .append("|||"+j)
//                            .append(todo.get(3, String.class));
//                    var res = DictRes.newBuilder().addStr(sb.toString()).build();
//                    custList7.add(res);
//                    sb.setLength(0);
//                }
//            }
//            perf10("strbu " + len + " com value:", begin);
//
//            begin = System.nanoTime();
//            for (int j = 0; j < times; j++) {
//                var custList8 = new ArrayList<DictRes>();
//                for (var todo : todoList) {
//                    var str = todo.get(0, String.class) +
//                            "|||" +j+
//                            todo.get(1, String.class) +
//                            "|||" +j+
//                            todo.get(2, String.class) +
//                            "|||" +j+
//                            todo.get(3, String.class);
//                    custList8.add(DictRes.newBuilder().addStr(str).build());
//                }
//            }
//            perf10("string " + len + " com value:", begin);

//            RelationalJMapper<Customer> mapper
//             = new RelationalJMapper<>(Customer.class,"x.xml");
//            var retC=mapper.manyToOne(todoList.get(0));
//            log.info(retC.toString());

//        em.getTransaction().commit();
            // Retrieve all customers

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
