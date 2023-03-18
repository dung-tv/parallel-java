import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestCombinations {
    public static void main(String[] args) {
        String arr1[] = {"a", "b", "c"};
        List<String> arr = IntStream.rangeClosed(1, 66000)
                .boxed().map(r -> r.toString()).collect(Collectors.toList());;

//        System.out.println("START: " + Instant.now());
//        List<String> tmpArrPassing = new ArrayList<>();
//        List<String> result1 = arr.stream()
//                .flatMap(s1 -> arr.stream().parallel()
//                        .filter(s2 -> {
//                            tmpArrPassing.add(s1);
//                            return !s1.equals(s2) && !tmpArrPassing.contains(s2);
//                        })
//                                .map(s3 -> {
//                                    return s1 + "-" + s3;
//                                })
//                )
//                .collect(Collectors.toList());
//        System.out.println("STOP: " + Instant.now());
//        System.out.println(result1.size());

        System.out.println("START: " + Instant.now());
        Set<String> result2 = arr.stream().parallel()
                .flatMap(s1 -> arr.stream().parallel()
                        .filter(s -> !s1.equals(s))
                        .map(s3 -> {
                            List<String> pairStr = new LinkedList<>();
                            pairStr.add(s1);
                            pairStr.add(s3);
                            return pairStr;
                        })
                ).map(s2 -> {
                    List<String> l = s2.stream().sorted((a, b) -> {
                        double aD = Double.parseDouble(a);
                        double bD = Double.parseDouble(b);
                        return Double.compare(aD, bD);
                    }).collect(Collectors.toList());
//                    System.out.println(l);
                    return String.join("-", l);
                })
                .collect(Collectors.toSet());
        System.out.println("STOP: " + Instant.now());
        System.out.println(result2.size());
    }
}