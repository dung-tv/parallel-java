import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TestCombinations {
    public static void main(String[] args) {
        String arr1[] = {"a", "b", "c"};

        List<String> tmpArrPassing = new ArrayList<>();
        List<String> result = Arrays.stream(arr1)
                .flatMap(s1 -> Arrays.stream(arr1).parallel()
                        .filter(s2 -> {
                            tmpArrPassing.add(s1);
                            return !s1.equals(s2) && !tmpArrPassing.contains(s2);
                        })
                                .map(s3 -> {
                                    System.out.println(s1 + s3);
                                    return s1 + s3;
                                })
                )
                .collect(Collectors.toList());
        System.out.println(result);
    }
}