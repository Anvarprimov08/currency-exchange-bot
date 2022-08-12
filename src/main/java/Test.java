import com.google.common.primitives.UnsignedLong;

import static java.lang.Math.*;

public class Test {
    public static void main(String[] args) {
        if (true) {
            String v = "p10.123435678876";
            Double d = 0.;
            try {
                d = Double.parseDouble(v);
                System.out.println(d);
            } catch (NumberFormatException e) {
                return;
            }
            System.out.println("in the if");
        }
        System.out.println("outside of the if");
    }
}
