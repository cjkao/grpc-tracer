package cj.poc.db;

        import cj.grpc.DictRes;
        import cj.grpc.Row;
        import com.google.protobuf.ByteString;
        import lombok.extern.slf4j.Slf4j;

        import java.sql.Connection;
        import java.sql.DriverManager;
        import java.sql.ResultSet;
        import java.sql.SQLException;
        import java.sql.Statement;
        import java.util.ArrayList;

/**
 *
 * @author Satya Choudhury
 */
@Slf4j
public class DictDB {

    public static void getCustomers() {
        Connection conn = null;
        String db = "jdbc:hsqldb:file:/Users/peter/japi/hsqldb-2.4.1/hsqldb/data/data;ifexists=true";
        String user = "SA";
        String password = "sa";

        try {
            // Create database connection
            conn = DriverManager.getConnection(db, user, password);

            // Create and execute statement
            Statement stmt = conn.createStatement();
            ResultSet rs =  stmt.executeQuery("select firstName, lastName,street,city from CUSTOMER");

            // Loop through the data and print all artist names
            var begin=System.nanoTime();
            var custList6=new ArrayList();
            while(rs.next()) {
//               var by=rs.getString(1);
//                rs.getBytes(1);
                DictRes res=DictRes.newBuilder()
                        .addRow(Row.newBuilder().addStr(rs.getString(1)))
                        .addRow(Row.newBuilder().addStr(rs.getString(2)))
                        .addRow(Row.newBuilder().addStr(rs.getString(3)))
                        .addRow(Row.newBuilder().addStr(rs.getString(4)))

                        .build();
                custList6.add(res);
//                System.out.println("Customer Name: " + rs.getString("firstName") + " " + rs.getString("lastName"));
            }
            perf("len:"+" pb value:", begin);
            // Clean up
            rs.close();
            stmt.close();
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        finally {
            try {
                // Close connection
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Retrieve all customers
        getCustomers();
    }
    private static void perf(String desc,long begin){
        log.info(desc + " "+ ((System.nanoTime()-begin))/1000_000 +"ms");
    }

}