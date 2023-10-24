package test.rc;

import org.junit.Test;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Optional的API测试用例
 */
public class OptionalDemo {
    @Test
    public void of() {
        //创建一个正常的属性值
        Optional<String> hello = Optional.of("hello");
        System.out.println(hello.get());
    }

    @Test
    public void ofNullable() {
        String str = null;
        Optional<String> optionalStr = Optional.ofNullable(str);
        //判断是否不为空，不为空返回true
        System.out.println(optionalStr.isPresent()); // 输出：false
    }

    @Test
    public void orElse() {
        String str = null;
        Supplier supplier = () -> "test";
        //为空，则获取Supplier的值
        String result = Optional.ofNullable(str).orElseGet(supplier);
        System.out.println(result);
    }

    @Test
    public void orElseThrow() {
        try {
            //为空，则抛出异常
            Optional.ofNullable(null).orElseThrow(RuntimeException::new);
        } catch (Exception e) {
            System.out.println("空指针抛出异常");
        }

    }

    @Test
    public void flatMap() {
        Optional<String> hello = Optional.of("Hello");
        Optional<String> empty = Optional.empty();
        Optional<String> world = Optional.of("world");

        Optional<String> Helloa = Optional.of("Helloa");

        Optional<String> s = Stream.of(hello, empty, world,Helloa)
                .flatMap(Optional::stream)
                .reduce((s1, s2) -> s1 + s2);
        System.out.println(s.get());

    }
}
