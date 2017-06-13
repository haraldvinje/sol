import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by haraldvinje on 12-Jun-17.
 */
public class MainTest {
    @Test
    public void addTwoIntegers() throws Exception {
        Main m = new Main();
        int a = 3;
        int b = 2;
        assertEquals(m.addTwoIntegers(a,b), a+b);
    }

}