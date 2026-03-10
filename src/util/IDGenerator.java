package util;

public class IDGenerator {

    public static String generateId (String prefix, int currentNumber){
        int nextNumber = currentNumber + 1;
        return String.format("%s%06d", prefix, nextNumber);
    }
}