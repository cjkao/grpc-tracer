package cj.poc.db;

//import com.google.auto.value.AutoValue;

//import javax.persistence.Tuple;

//import static cj.poc.db.AutoCustomer.field.*;

//@AutoValue
abstract class BuilderCustomer {
     enum field{firstName,lastName,street,city};
//    public static Builder builder() {
//        return new AutoValue_BuilderCustomer.Builder();
//    }

//    @AutoValue.Builder
    public abstract static class Builder {
        abstract Builder firstName(String _x);

        abstract Builder lastName(String _x);
        abstract Builder street(String _x);
        abstract Builder city(String _x);
        abstract Builder numberOfLegs(int _x);

        abstract BuilderCustomer build();
    }


    public abstract String firstName();
    public abstract String lastName();
   public abstract String street();
 public   abstract String city();
 public   abstract int numberOfLegs();
}