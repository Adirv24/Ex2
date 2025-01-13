import junit.framework.Assert;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class Ex2SheetTest
{
    @Test
    void evalTest()
    {
        Ex2Sheet a=new Ex2Sheet(3,2);
        a.set(1,0,"=55+6");
        a.set(1,1,"=A0");
        a.set(2,0,"=A0+A1");
        a.set(2,1,"=Hello");
        if(a.eval(2,2)=="122") assertTrue(true);
        else assertFalse(false);
    }

    @Test
    void isInTest()
    {
        Ex2Sheet a=new Ex2Sheet(3,4);
        int x=5;
        int y=2;
        if(a.isIn(x,y)==true) assertTrue(true);
        else assertFalse(false);

    }
    @Test
    void valueTest()
    {
        Ex2Sheet a=new Ex2Sheet(1,5);
        a.set(2,3,"=55+6");
        if(a.value(0,1)=="61") assertTrue(true);
        else assertFalse(false);

    }



}
