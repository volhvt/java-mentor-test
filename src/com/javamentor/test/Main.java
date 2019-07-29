package com.javamentor.test;

import java.io.PrintStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

abstract class Calculator<T> {

    protected T value = null;

    public abstract Calculator<T> addition(T value);

    public abstract Calculator<T> subtraction(T value);

    public abstract Calculator<T> multiplication(T value);

    public abstract Calculator<T> division(T value);

    public abstract String getValue();

    public abstract T validate(T value) throws IllegalArgumentException;

    public Calculator<T> init(T value) {
        this.value = validate(value);
        return this;
    }

    public T getResult() {
        return value;
    }

    public Calculator<T> calc(T number1,T number2,char operation) {
        init(number1);
        switch (operation) {
            case '+':
                addition(number2);
                break;
            case '-':
                subtraction(number2);
                break;
            case '*':
                multiplication(number2);
                break;
            case '/':
                division(number2);
                break;
        }
        return this;
    }

    public static String calc(String s) throws IllegalFormatException {
        Pattern p = Pattern.compile("^([\\-]?[\\d]+)[\\s]?([+\\-/*]{1})[\\s]?([\\-]?[\\d]+)$");
        Matcher m = p.matcher(s);

        if (m.matches()) {
            /*for(int i=0;i<m.groupCount();i++) {
                System.out.println("part " + i + " : " + m.group(i));
            }*/
            ArabicCalculator c = new ArabicCalculator();
            return c.calc(
                    new Integer(m.group(1)),
                    new Integer(m.group(3)),
                    m.group(2).charAt(0)
            ).getValue();
        } else {
            p = Pattern.compile("^([IVXLCDM]+)[\\s]?([+\\-/*]{1})[\\s]?([IVXLCDM]+)$");
            m = p.matcher(s);
            if (m.matches()) {
                /*for(int i=0;i<m.groupCount();i++) {
                    System.out.println("part " + i + " : " + m.group(i));
                }*/
                RomanCalculator c = new RomanCalculator();
                return c.calc(
                        m.group(1),
                        m.group(3),
                        m.group(2).charAt(0)
                ).getValue();
            }
        }
        throw new IllegalArgumentException(s + " - illegal format of expression");
    }
}

class ArabicCalculator extends Calculator<Integer> {

    @Override
    public ArabicCalculator addition(Integer number) {
        value+=validate(number);
        return this;
    }

    @Override
    public ArabicCalculator subtraction(Integer number) {
        value-=validate(number);
        return this;
    }

    @Override
    public ArabicCalculator multiplication(Integer number) {
        value*=validate(number);
        return this;
    }

    @Override
    public ArabicCalculator division(Integer number) {
        value/=validate(number);
        return this;
    }

    @Override
    public String getValue() {
        return "" + getResult();
    }

    @Override
    public Integer validate(Integer number) throws IllegalArgumentException {
        if (number < -32768 || number > 32767)
            throw new IllegalArgumentException(number + " is not in range [-32768,32767]");
        return number;
    }
}

enum RomanNumeral {
    I(1), IV(4), V(5), IX(9), X(10),
    XL(40), L(50), XC(90), C(100),
    CD(400), D(500), CM(900), M(1000);

    private int value;

    RomanNumeral(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static List<RomanNumeral> getReverseSortedValues() {
        return Arrays.stream(values())
                .sorted(Comparator.comparing((RomanNumeral e) -> e.value).reversed())
                .collect(Collectors.toList());
    }

    public static Integer toArabic(String b) throws IllegalArgumentException {
        String romanNumeral = b.toUpperCase();
        int result = 0;

        List<RomanNumeral> romanNumerals = getReverseSortedValues();

        int i = 0;

        while ((romanNumeral.length() > 0) && (i < romanNumerals.size())) {
            RomanNumeral symbol = romanNumerals.get(i);
            if (romanNumeral.startsWith(symbol.name())) {
                result += symbol.getValue();
                romanNumeral = romanNumeral.substring(symbol.name().length());
            } else {
                i++;
            }
        }

        if (romanNumeral.length() > 0) {
            throw new IllegalArgumentException(b + " cannot be converted to a Roman Numeral");
        }

        return result;
    }

    public static String toRoman(int number) {
        if ((number <= 0) || (number > 4000)) {
            throw new IllegalArgumentException(number + " is not in range (0,4000]");
        }

        List<RomanNumeral> romanNumerals = getReverseSortedValues();

        int i = 0;
        StringBuilder sb = new StringBuilder();

        while ((number > 0) && (i < romanNumerals.size())) {
            RomanNumeral currentSymbol = romanNumerals.get(i);
            if (currentSymbol.getValue() <= number) {
                sb.append(currentSymbol.name());
                number -= currentSymbol.getValue();
            } else {
                i++;
            }
        }

        return sb.toString();
    }
}

class RomanCalculator extends ArabicCalculator {

    @Override
    public String getValue() {
        return RomanNumeral.toRoman(getResult());
    }

    public RomanCalculator init(String value) {
        init(RomanNumeral.toArabic(value));
        return this;
    }

    public RomanCalculator addition(String number) {
        addition(RomanNumeral.toArabic(number));
        return this;
    }

    public RomanCalculator subtraction(String number) {
        subtraction(RomanNumeral.toArabic(number));
        return this;
    }

    public RomanCalculator multiplication(String number) {
        multiplication(RomanNumeral.toArabic(number));
        return this;
    }

    public RomanCalculator division(String number) {
        division(RomanNumeral.toArabic(number));
        return this;
    }

    public RomanCalculator calc(String number1,String number2,char operation) {
        calc(RomanNumeral.toArabic(number1),RomanNumeral.toArabic(number2),operation);
        return this;
    }
}

public class Main {

    public static void main(String[] args) {
        PrintStream out = System.out;

        Scanner sc = new Scanner(System.in);
        out.println("________[ SIMPLE CALCULATOR ]________");
        out.println("The operations are supported: +, -, *, /");
        out.println("The numbers are supported: Arabic (1,2,3,4,5 ...) and Roman (I, II, III, IV, V ...)");
        out.print("Enter a mathematical expression (for example: 1+1): ");
        try {
            out.println("Result: " + Calculator.calc(sc.nextLine()));
        } catch (ArithmeticException | IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

}
