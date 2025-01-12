class UniformDistribution {
    private final double min;
    private final double max;

    public UniformDistribution(double min, double max) {
        if (min >= max) {
            throw new IllegalArgumentException("Min must be less than max");
        }
        this.min = min;
        this.max = max;
    }

    public double generateServiceTime() {
        return min + (Math.random() * (max - min)); // Генерация случайного числа в диапазоне [min, max]
    }
}