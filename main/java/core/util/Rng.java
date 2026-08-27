package core.util;

import java.util.Random;

public class Rng {
    private static final Random RANDOM = new Random();

    public static boolean nextBoolean(double probability) {
        if (probability < 0 || probability > 1)
            throw new IllegalArgumentException("Probability should be between 0 and 1");
        return RANDOM.nextDouble() < probability;
    }

    public static int nextInt(int endRange) {
        if (endRange < 1)
            throw new IllegalArgumentException("Random range too small");
        return RANDOM.nextInt(endRange);
    }

    public static int between(int startRange, int endRange) {
        if (startRange >= endRange)
            throw new IllegalArgumentException("Range should be positive");
        return RANDOM.nextInt(endRange - startRange + 1) + startRange;
    }
}
