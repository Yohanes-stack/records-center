import org.junit.Test;

/**
 * 栈的深度，用两个不同的参数方法来测试
 */
public class StackDeep {
    private int count = 0;

    public void recursion(long a, long b, long c) {
        count++;
        recursion(a, b, c);
    }

    public void recursion() {
        count++;
        recursion();
    }

    @Test
    public void test1() {
        try{
            recursion();
        }catch (Throwable e){
            System.out.println(count);
        }


    }
    @Test
    public void test2() {
        try{
            recursion(1L,2L,3L);
        }catch (Throwable e){
            System.out.println(count);
        }


    }
}
