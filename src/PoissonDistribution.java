import java.util.Random;

public class PoissonDistribution {
    private final Random random = new Random();
    private final double lambda;

    public PoissonDistribution(double lambda) {
        this.lambda = lambda;
    }

    public int generateRequestCount() {
        double L = Math.exp(-lambda);
        int k = 0;
        double p = 1.0;
        do {
            k++;
            p *= random.nextDouble();
        } while (p > L);
        return k - 1;
    }
}