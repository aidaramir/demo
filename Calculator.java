import java.util.InputMismatchException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;


public class Calculator {

    private static final String[] ROMAN_NUMBERS = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};

    public static void main(String[] args) {
		System.out.println("Input: ");
        Scanner reader = new Scanner(System.in);
        String[] input = reader.nextLine().split("\\s+");
        if (input.length == 3) {
            String symbol = input[1];
            boolean roman = false;
            int a = 0;
            int b = 0;
            if (isArabic(input[0]) && isArabic(input[2])) {
                a = Integer.parseInt(input[0]);
                b = Integer.parseInt(input[2]);
            } else if (isRoman(input[0]) && isRoman(input[2])) {
                a = NumeralConverter.romanToArabic(input[0]);
                b = NumeralConverter.romanToArabic(input[2]);
                roman = true;
            } else {
                System.out.println("Нужно вести правильно....");
            }
            if (!(isInRange(a) && isInRange(b))) {
                throw new InputMismatchException("Число за пределами лимитов [1, 10]");
            }
            int result = calculate(a, b, symbol);
            System.out.println("Output ");
            System.out.println(roman ? NumeralConverter.arabicToRoman(result) : result);
        } else System.out.println("Нужно вести правильно....");
    }

    private static Integer calculate(int a, int b, String symbol) {
        switch (symbol) {
            case "+":
                return a + b;
            case "-":
                return a - b;
            case "/":
                return a / b;
            case "*":
                return a * b;
            default:
                throw new InputMismatchException("Неверный знак операции");
        }
    }

    private static boolean isArabic(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isRoman(String input) {
        for (String roman : ROMAN_NUMBERS) {
            if (input.equals(roman)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isInRange(int a) {
        return a >= 1 && a <= 10;
    }

}

class NumeralConverter {

    private static final int MIN_ARABIC = 1;
    private static final int MAX_ARABIC = 100;

    private static final Map<String, Integer> ROMAN_MAPPING = new LinkedHashMap<>();
    private static final Map<Integer, String> STOPS = new LinkedHashMap<>();

    static {
        ROMAN_MAPPING.put("C", 100);
        ROMAN_MAPPING.put("L", 50);
        ROMAN_MAPPING.put("X", 10);
        ROMAN_MAPPING.put("V", 5);
        ROMAN_MAPPING.put("I", 1);
        ROMAN_MAPPING.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(entry -> STOPS.put(entry.getValue(), entry.getKey()));
    }


    public static int romanToArabic(String roman) {
        int sum = 0;
        int prev = 0;
        int comp = 0;
        int iend = -1;
        for (int i = roman.length() - 1; i >= 0; i--) {
            int currentChar = ROMAN_MAPPING.get(roman.substring(i, i + 1));
            if (prev != 0) {
                if (comp == 0) {
                    if (currentChar < prev) {
                        comp = prev;
                        iend = i + 1;
                    } else {
                        sum += currentChar;
                    }
                } else if (currentChar >= comp) {
                    sum -= romanToArabic(roman.substring(i + 1, iend));
                    comp = 0;
                    sum += currentChar;
                }
            } else {
                sum += currentChar;
            }
            if (i == 0 && comp != 0) {
                sum -= romanToArabic(roman.substring(i, iend));
            }
            prev = currentChar;
        }
        return sum;
    }

    public static String arabicToRoman(int n) {
        if (isInRange(n)) {
            for (String character : ROMAN_MAPPING.keySet()) {
                int val = ROMAN_MAPPING.get(character);
                if (val == n) {
                    return character;
                }
                for (int sub : STOPS.keySet()) {
                    if (n == val - sub) {
                        return STOPS.get(sub) + character;
                    }
                }
                if (n > val) {
                    int diff = 0;
                    StringBuilder sb = new StringBuilder(character);
                    while (val + diff < n) {
                        int finalDiff = diff;
                        var largestEntry = ROMAN_MAPPING.entrySet().stream()
                                .filter(entry -> entry.getValue() <= val)
                                .filter(entry -> entry.getValue() <= n - val - finalDiff)
                                .reduce((e1, e2) -> e1.getValue() >= e2.getValue() ? e1 : e2)
                                .get();
                        sb.append(largestEntry.getKey());
                        diff += largestEntry.getValue();
                    }
                    return sb.toString();
                }
            }
        }
        throw new NumberFormatException("Number is out of range");
    }

    private static boolean isInRange(int n) {
        return n >= MIN_ARABIC && n <= MAX_ARABIC;
    }

}