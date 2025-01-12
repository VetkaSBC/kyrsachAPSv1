import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

class RequestGenerator {
    private final int generatorId;
    private final double lambda;  // Параметр интенсивности для Пуассоновского процесса
    private int requestsAmount;
    private int removedRequestsCount = 0;
    private double nextRequestTime = 0; // Время, когда следующая заявка должна быть сгенерирована
    private final Random random = new Random();

    private static final String[] descriptions = {
            "Fix printer", "Install software", "Hard drive issue", "Board burned out",
            "Network issue", "Upgrade of accessories", "Viruses cleaning",
            "Replace hardware", "Upgrade OS"
    };

    public RequestGenerator(int generatorId, double lambda) {
        this.generatorId = generatorId;
        this.lambda = lambda;  // Средняя интенсивность генерации заявок (заявки в секунду)
    }

    public int getRequestsAmount() {
        return requestsAmount;
    }

    public int getRemovedRequestsCount() {
        return removedRequestsCount;
    }

    public int getGeneratorId() {
        return generatorId;
    }

    public void incrementRemovedRequestsCount() {
        removedRequestsCount++;
    }

    public boolean isValidRequest(double currentTime) {
        // Проверяем, пришло ли время генерировать следующую заявку
        if (currentTime >= nextRequestTime) {
            // Устанавливаем время для следующей заявки
            nextRequestTime = currentTime + generateExponential(lambda);
            return true;
        }
        return false;
    }

    public Request generateRequest(boolean informationOutput) {
        requestsAmount++;
        int id = requestsAmount; // ID заявки (можно заменить, если нужно глобальное)
        String description = descriptions[random.nextInt(descriptions.length)];
        if (informationOutput) {
            System.out.println("Generated Request ID: " + id + ", Description: " + description);
        }
        return new Request(id, description, generatorId);
    }

    private double generateExponential(double lambda) {
        // Генерируем случайное число из экспоненциального распределения
        return -Math.log(1 - random.nextDouble()) / lambda;
    }
}