import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertTrue;

public class BeanUtilsTest extends TestCase {

    ObjectChild to;
    ObjectChild from;
    AnotherObject anotherObject;

    @Before
    public void setUp() throws Exception {
        to = new ObjectChild();
        from = new ObjectChild();
        from.setS("WOW");
        from.setaLong(100L);
        from.setAnotherInteger(111);
        from.setNumber(10);
        from.today = LocalDate.now();
        anotherObject = new AnotherObject();
        anotherObject.setNumber(12312);
    }

    @Test
    public void testAssignForNull() throws Exception {
        try {
            BeanUtils.assign(null,null);
            fail("Passing null must cause exception");
        } catch (RuntimeException ex) {
            System.err.println("");
        }
        BeanUtils.assign(to,from);
        assertTrue("Public setters are not set",to.getS().equals("WOW")&&
                to.getNumber().equals(from.getNumber())&&
                to.getAnotherInteger()==from.getAnotherInteger());
        assertTrue("Must be set only public setters",to.today == null);
        assertTrue("Must be set only public setters (even if getter is public)",to.getaLong()==null);
        BeanUtils.assign(to,anotherObject);
        assertTrue("To can accept ONLY compatible types", to.getNumber().equals(anotherObject.getNumber()));
    }



    @After
    public void tearDown() throws Exception {
        to = null;
        from = null;
    }

    static class ObjectParent {
        Number number;
        String s;
        LocalDate today;
        Float f;

        public Number getNumber() {
            return number;
        }

        public void setNumber(Number number) {
            this.number = number;
        }

        public String getS() {
            return s;
        }

        public void setS(String s) {
            this.s = s;
        }

        private LocalDate getToday() {
            return today;
        }

        private void setToday(LocalDate today) {
            this.today = today;
        }
    }

    static class ObjectChild extends ObjectParent {
        Long aLong;
        int anotherInteger;

        private Long getaLong() {
            return aLong;
        }

        public void setaLong(Long aLong) {
            this.aLong = aLong;
        }

        public int getAnotherInteger() {
            return anotherInteger;
        }

        public void setAnotherInteger(int anotherInteger) {
            this.anotherInteger = anotherInteger;
        }

        public Double getObject() {
            return 64.d;
        }

        public void setObject(Float f) {

        }
    }

    static class AnotherObject {
        Integer number;

        public Integer getNumber() {
            return number;
        }

        public void setNumber(Integer number) {
            this.number = number;
        }
    }

}