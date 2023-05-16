import org.junit.Test;

public class LocalVarGC {

    @Test
    public void localVarGc1() {
        byte[] a = new byte[6 * 1024 * 1024];
        System.gc();
    }

    public void localVarGc2() {
        byte[] a = new byte[6 * 1024 * 1024];
        a=null;
        System.gc();
    }

    public void localVarGc3() {
        {
            byte[] a = new byte[6 * 1024 * 1024];
        }
        System.gc();
    }

    public void localVarGc4() {
        {
            byte[] a = new byte[6 * 1024 * 1024];
        }
        System.gc();
    }
    public void localVarGc5() {
        localVarGc1();
        System.gc();
    }

    public static void main(String[] args) {
        LocalVarGC localVarGC = new LocalVarGC();
        localVarGC.localVarGc2();
    }

}
