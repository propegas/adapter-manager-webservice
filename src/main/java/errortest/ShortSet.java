package errortest;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by vgoryachev on 14.04.2016.
 * Package: PACKAGE_NAME.
 */
public class ShortSet {

    private ShortSet() {
        // default
    }

    public static void main (String[] args) {
        Set<Short> s = new HashSet<>();
        for (short i = 0; i < 100; i++) {
            s.add(i);
            s.remove( (short) (i - 1));
        }

    }
}