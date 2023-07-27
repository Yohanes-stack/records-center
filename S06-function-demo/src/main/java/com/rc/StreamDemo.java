package com.rc;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Stream流的API测试用例
 * 转换操作：map()，filter()，sorted()，distinct()；
 * <p>
 * 合并操作：concat()，flatMap()；
 * <p>
 * 并行处理：parallel()；
 * <p>
 * 聚合操作：reduce()，collect()，count()，max()，min()，sum()，average()；
 * <p>
 * 其他操作：allMatch(), anyMatch(), forEach()。
 */
public class StreamDemo {
    @Test
    public void of() {
        //通过of进行创建Stream流
        Stream<String> stream = Stream.of("A", "B", "C", "D");
        print(stream);
    }

    @Test
    public void create() {
        //基于数组的方式
        Stream<String> stream1 = Arrays.stream(new String[]{"A", "B", "C", "D"});
        //基于集合的方式
        Stream<String> stream2 = List.of("A", "B", "C").stream();
        print(stream1);
        print(stream2);
    }

    @Test
    public void generate() {
        //基于supplier创建的stream会不断的调用supplier的get方法产生下一个元素，这种stream保存的不是元素，是一种算法
        Stream<Integer> generate = Stream.generate(() -> 0);
        print(generate.limit(20));
        //可以基于supplier来做一个，生成自然数的
        Stream generate1 = Stream.generate(new NatualSupplier());
        //如果用list做一个自然数集合，所占用的内存是巨大的，用supplier则不会，因为全是实时计算出来的
        //因为用generate生成的是一个无限序列 因此需要先将它变成有限序列，通过limit()则可以截取前若干个元素
        print(generate1.limit(20));
    }

    @Test
    public void fileStream() {
        //直接将file转换为Stream流来进行读取
        try (Stream<String> lines = Files.lines(Paths.get("/Users/yohanes/Documents/workDocuments/private-info.txt"))) {
            print(lines);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void splitAsStream() {
        Pattern p = Pattern.compile("\\s+");
        Stream<String> s = p.splitAsStream("The quick brown fox jumps over the lazy dog");
        print(s);
    }

    @Test
    public void typeStream() {
        //因为Java的范型不支持基本类型，所以我们无法用Stream<int>这样的类型，会发生编译错误。为了保存int，只能使用Stream<Integer>，
        // 但这样会产生频繁的装箱、拆箱操作。为了提高效率，Java标准库提供了IntStream、LongStream和DoubleStream这三种使用基本类型的Stream，
        // 它们的使用方法和范型Stream没有大的区别，设计这三个Stream的目的是提高运行效率：
        IntStream intStream = IntStream.of(1, 2, 3, 4);
        print(intStream);
        //注意 流只能被消费一次，在print中已经被消费过了
//        LongStream longStream = intStream.asLongStream();
        LongStream longStream1 = List.of("1", "2", "3").stream().mapToLong(Long::parseLong);
        print(longStream1);
    }

    @Test
    public void map() {
        //map就是将当前数 转换为另外一个数
        // 1 2 3 4 5
        // 通过计算处理
        // x x x x x
        Stream<Integer> squareStream = Stream.of(1, 2, 3, 4, 5).map(x -> x * x);
        print(squareStream);
        //map不仅能完成数学计算，同样对于字符串操作，也是非常有用的
        print(List.of("  Apple ", " pear ", " ORANGE", " BaNaNa ")
                .stream()
                .map(String::trim)
                .map(String::toLowerCase));
        //也可以完成类型转换 ，实际开发我认为最常见的三种功能
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Stream<LocalDate> localDateStream = List.of("2021-01-01", "2020-01-01", "2023-01-03", "2024-05-01")
                .stream()
                .map(item -> LocalDate.parse(item, dateTimeFormatter));
        print(localDateStream);
    }

    /**
     * 所谓filter()操作，就是对一个Stream的所有元素一一进行测试，不满足条件的就被“滤掉”了，剩下的满足条件的元素就构成了一个新的Stream
     */
    @Test
    public void filter() {
        print(IntStream.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
                .filter(n -> n % 2 != 0));

        print(Stream.generate(new LocalDateSupplier())
                .limit(31)
                .filter(ldt -> ldt.getDayOfWeek() == DayOfWeek.SATURDAY || ldt.getDayOfWeek() == DayOfWeek.SUNDAY));

    }

    /**
     * map和filter 都是转换方法，而reduce则是聚合方法，把一个stream的所有元素按照聚合函数聚合成一个结果
     */
    @Test
    public void reduce() {
        //reduce不指定默认值时，则会返回Optional函数
        Optional<Integer> reduce = Stream.of(1, 2, 3, 4, 5).reduce((x, y) -> x - y);
        System.out.println(reduce.get());
        //指定了默认值，则会返回具体的聚合对象
        Integer reduce1 = Stream.of(1, 2, 3, 4, 5).reduce(1, (acc, n) -> acc + n);
        System.out.println(reduce1);

        //读取配置文件，转换为map结构
        List<String> props = List.of("profile=native", "debug=true", "logging=warn", "interval=500");

        Map<String, String> reduce2 = props.stream()
                .map(kv -> {
                    String[] ss = kv.split("\\=", 2);
                    return Map.of(ss[0], ss[1]);
                })
                .reduce(new HashMap<>(), (kv, k) -> {
                    kv.putAll(k);
                    return kv;
                });
        reduce2.forEach((k, v) -> {
            System.out.println(k + " = " + v);
        });
    }

    @Test
    public void group() {
        List<String> list = List.of("Apple", "Banana", "Blackberry", "Coconut", "Avocado", "Cherry", "Apricots");
        //分组 将首字母相同的分为一组
        Map<String, List<String>> collect = list.stream()
                .collect(Collectors.groupingBy(s -> s.substring(0, 1), Collectors.toList()));
        collect.forEach((k, v) -> {
            System.out.println(k + " = " + v);
        });
    }

    @Test
    public void flatMap() {
        Stream<List<Integer>> s = Stream.of(
                Arrays.asList(1, 2, 3),
                Arrays.asList(4, 5, 6),
                Arrays.asList(7, 8, 9));
        //将 Stream<List<Integer>> 转换为 Stream<Integer>
        //flatMap就是将每个元素 都转换为Stream，然后合并成为一个新的Stream
        Stream<Integer> stream = s.flatMap(item -> item.stream());
        print(stream);
    }

    /**
     * 通常情况下，对Stream的元素进行处理是单线程的，即一个一个元素进行处理。
     * 但是很多时候，我们希望可以并行处理Stream的元素，因为在元素数量非常大的情况，并行处理可以大大加快处理速度。
     */
    @Test
    public void parallel() {
        Stream<String> s = List.of("Apple", "Banana", "Blackberry", "Coconut", "Avocado", "Cherry", "Apricots").stream();
        String[] result = s.parallel() // 变成一个可以并行处理的Stream
                .sorted() // 可以进行并行排序
                .toArray(String[]::new);
        for (String s1 : result) {
            System.out.println(s1);
        }
    }

    @Test
    public void other() {
        List<String> collect = List.of("A", "B", "C", "D", "E", "F")
                .stream()
                //跳过
                .skip(2)
                //截取前某干个
                .limit(3)
                .collect(Collectors.toList());
        System.out.println(collect);
        //排序
        List<String> list = List.of("Orange", "apple", "Banana")
                .stream()
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList());
        System.out.println(list);
        //合并
        Stream<String> s1 = List.of("A", "B", "C").stream();
        Stream<String> s2 = List.of("D", "E").stream();
        Stream<String> concat = Stream.concat(s1, s2);
        List<String> collect1 = concat.collect(Collectors.toList());
        System.out.println(collect1);
        //求最大数
        //两种方式
        Optional<Integer> max = Stream.of(1, 2, 3, 4, 5, 6).max(Comparator.comparingInt(x -> x));
        System.out.println(max.get());
        //求最小数
        Optional<Integer> min = Stream.of(1, 2, 3, 5, 6, 7).min(Comparator.comparingInt(x -> x));
        System.out.println(min.get());


        String[] words = {"apple", "banana", "coconut", "date"};

        // 获取最长的单词
        String longestWord = Arrays.stream(words)
                .max((a1, a2) -> a1.length() - a2.length())
                .orElse("");
        System.out.println("Longest word: " + longestWord);

        // 获取最短的单词
        String shortestWord = Arrays.stream(words)
                .min((a1, a2) -> a1.length() - a2.length())
                .orElse("");

        System.out.println(longestWord);

        System.out.println(shortestWord);
    }

    /**
     * 匹配
     */
    @Test
    public void match() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        //如果所有元素都满足，则返回 true；如果有任意一个元素不满足，则返回 false。
        boolean allEven = numbers.stream().allMatch(n -> n % 2 == 0);
        System.out.println(allEven); // false

        List<String> strings = Arrays.asList("hello", "world", "Java");
        //流中是否存在任意一个元素满足给定的条件。如果存在则返回 true，否则返回 false
        boolean hasUpperCase = strings.stream().anyMatch(s -> s.toUpperCase().equals(s));
        System.out.println(hasUpperCase); // true

        //实际用途
        Object o = new Object();
        boolean anyMatchStatus = Stream.of(new AMatch(), new BMatch()).anyMatch(x -> x.test(o));


        System.out.println(anyMatchStatus);
        boolean allMatch = Stream.of(new AMatch(), new BMatch()).allMatch(x -> x.test(o));

        System.out.println(allMatch);
    }


    class NatualSupplier implements Supplier {
        int n = 0;

        @Override
        public Object get() {
            n++;
            return n;
        }
    }

    class LocalDateSupplier implements Supplier<LocalDate> {
        LocalDate start = LocalDate.of(2020, 1, 1);
        int n = -1;

        public LocalDate get() {
            n++;
            return start.plusDays(n);
        }
    }


    public void print(Stream stream) {
        stream.forEach(System.out::println);
    }

    public void print(IntStream stream) {
        stream.forEach(System.out::println);
    }

    public void print(LongStream stream) {
        stream.forEach(System.out::println);
    }


}
