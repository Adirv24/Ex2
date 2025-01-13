import junit.framework.Assert;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SCellTest
{
    @Test
    void DoesValidEntryTest()
    {
        int count=0;
        boolean ok;
        SCell[] cells=new SCell[5];
        cells[0]=new SCell("Hello");
        cells[1]=new SCell("=5");
        cells[2]=new SCell("=5+A3");
        cells[3]=new SCell("");
        cells[4]=new SCell("=3");
        int[] Outputs = {1,2,3,-1,2};
        for(int i=0;i<cells.length;i=i+1)
        {

            if(cells[i].getType()==Outputs[i]) count++;

        }
        if(count==5)
        {
            ok=true;
            assertTrue(ok);
        }
        else
        {
            ok=false;
            assertFalse(ok);
        }
    }
    @Test
    void computeFormulaTest()
    {
        int count=0;
        boolean ok;
        String[] StringsToTest = {"=5+3", "=3*2+3", "=4", "=0*4"};
        double[] Outputs = {8.0, 9.0, 4.0 ,0.0};
        SCell c1;
        for(int i=0;i<StringsToTest.length;i=i+1)
        {

            if(SCell.computeFormula(StringsToTest[i])==Outputs[i]) count++;

        }
        if(count==4)
        {
            ok=true;
            assertTrue(ok);
        }
        else
        {
            ok=false;
            assertFalse(ok);
        }
    }
    @Test
    void isFormulaTest()
    {
        int count=0;
        boolean ok;
        String[] StringsToTest = {"=A1+A2", "3", "=Ax", "="};
        int[] Outputs = {3, 2, 1 ,1};
        SCell c1;
        for(int i=0;i<StringsToTest.length;i=i+1)
        {
            c1=new SCell(StringsToTest[i]);
            if(c1.getType()==Outputs[i]) count++;

        }
        if(count==4)
        {
            ok=true;
            assertTrue(ok);
        }
        else
        {
            ok=false;
            assertFalse(ok);
        }
    }
    @Test
    void toStringTest()
    {
        SCell c1=new SCell("Initial Text");
        String ExpectedOutput="Initial Text";
        if(c1.toString()==ExpectedOutput)
        {
            boolean ok=true;
            assertTrue(ok);
        }
        else
        {
            boolean ok=false;
            assertFalse(ok);
        }
        SCell c2=new SCell("=5+3");
        String UnExpectedOutput="8";
        if(c2.toString()==UnExpectedOutput)
        {
            boolean ok=false;
            assertFalse(ok);
        }
        else
        {
            boolean ok=true;
            assertTrue(ok);
        }
    }

}
