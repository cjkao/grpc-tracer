package cj.poc.db;

//import com.google.auto.value.AutoValue;
//import org.eclipse.microprofile.config.inject.ConfigProperty;
//
//import javax.inject.Inject;
//import javax.persistence.Tuple;
//
//import static cj.poc.db.AutoCustomer.field.*;

//@AutoValue
abstract class AutoCustomer {
//     enum field{firstName,lastName,street,city};
//    @Inject
//    @ConfigProperty(name = "customer")
    String sql; //="select firstName, lastName,street,city from CUSTOMER";


//    static AutoCustomer create(Tuple arr) {
//        return new AutoValue_AutoCustomer(
//                arr.get(0,String.class),
//                arr.get(1,String.class),
//                arr.get(2,String.class),
//                arr.get(3,String.class),
//
//                2);
//    }
//    static AutoCustomer create2(Tuple arr) {
//        return new AutoValue_AutoCustomer(arr.get(firstName.toString(),String.class),
//                arr.get(lastName.toString(),String.class),
//                arr.get(street.toString(),String.class),
//                arr.get(city.toString(),String.class),
//                2);
//    }
//    abstract String firstName();
//    abstract String lastName();
//    abstract String street();
//    abstract String city();
//    abstract int numberOfLegs();
}