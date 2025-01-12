import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.Arrays;
import java.util.Random;

class MainSystem {
    private final int queueBufferCapacity;
    private final int technicianQuantity;
    private final int requestsQuantity;
    private final int generatorsAmount;

    private int time=0;
    private int deniedRequests = 0;

    public MainSystem(int queueBufferCapacity, int technicianQuantity, int requestsQuantity, int generatorsAmount) {
        this.requestsQuantity=requestsQuantity;
        this.queueBufferCapacity = queueBufferCapacity;
        this.technicianQuantity = technicianQuantity;
        this.generatorsAmount=generatorsAmount;
    }

    public void run(double lambda) {
        double currentTime = 0.0; // Текущее время в системе
        Scanner in = new Scanner(System.in);
        boolean isStepMode = false;

        System.out.println("Enter 'A' for automatic mode or 'S' for step-by-step mode:");
        String mode = in.next().toLowerCase(Locale.ROOT);
        if ("s".equals(mode)) {
            isStepMode = true;
        }

        // Создаем буфер заявок
        Buffer buffer = new Buffer(queueBufferCapacity);

        // Создаем список техников
        List<Technician> technicians = new ArrayList<>();
        for (int i = 1; i <= technicianQuantity; i++) {
            boolean isHighPriority = i <= technicianQuantity / 2; // Половина техников с высоким приоритетом
            double minServiceTime = isHighPriority ? 2.0 : 1.0; // Пример: минимальное время для техников
            double maxServiceTime = isHighPriority ? 5.0 : 3.0; // Пример: максимальное время для техников
            technicians.add(new Technician(i, "Technician " + i, minServiceTime, maxServiceTime, isHighPriority));
        }

        // Создаем генераторы заявок
        ArrayList<RequestGenerator> generators = new ArrayList<>();
        for (int i = 0; i < generatorsAmount; i++) {
            generators.add(new RequestGenerator(i, lambda)); // Генератор заявок с пуассоновским распределением
        }

        // Инициализируем распределение заявок
        RequestDistribution distribution = new RequestDistribution(technicians, buffer, isStepMode, generators);

        int deniedRequests = 0; // Счетчик отклоненных заявок
        int generatedRequests = 0; // Общее количество созданных заявок

        // Основной цикл симуляции
        while (generatedRequests < requestsQuantity) {
            for (RequestGenerator generator : generators) {
                if (generator.isValidRequest(currentTime)) {
                    Request request = generator.generateRequest(isStepMode);
                    boolean success = distribution.distributeRequests(request);
                    if (!success) {
                        deniedRequests++;
                    }
                    generatedRequests++;
                    if (generatedRequests >= requestsQuantity) {
                        break;
                    }
                }
            }

            // Обновляем состояние системы
            distribution.updateTime();
            currentTime += 1.0; // Шаг времени

            if (isStepMode) {
                System.out.println("Time: " + currentTime);
                buffer.printAllRequests();
                in.nextLine(); // Ждем ввода для перехода к следующему шагу
            }
        }

        in.close();

        // Вывод статистики
        printStatistics(technicians, lambda, generators, deniedRequests, currentTime);
    }

    private void printStatistics(List<Technician> technicians, double lambda, ArrayList<RequestGenerator> generators, int deniedRequests, double totalTime) {
        double rejectionProbability = (double) deniedRequests / requestsQuantity;
        double totalBusyTime = technicians.stream()
                .mapToDouble(Technician::getTotalBusyTime).sum();
        double averageLoad = totalBusyTime / (totalTime * technicianQuantity);

        System.out.println("\nSummary Statistics:");
        String summaryHeader = "| %-10s | %-10s | %-15s | %-15s | %-15s | %-25s | %-20s | %-25s |%n";
        System.out.printf(summaryHeader,
                "Requests", "Generators", "Technicians", "Buffer size", "Time in system",
                "Rejection probability", "Average workload", "Lambda");
        System.out.println("=".repeat(120));
        System.out.printf("| %-10d | %-10d | %-15d | %-15d | %-15.1f | %-25.2f | %-20.2f | %-25.2f |%n",
                requestsQuantity, generatorsAmount, technicianQuantity, queueBufferCapacity, totalTime,
                rejectionProbability * 100, averageLoad * 100, lambda);
        System.out.println("-".repeat(120));

        System.out.println("\nTechnician Workloads:");
        String techHeader = "| %-4s | %-15.15s | %-12.12s | %-8.8s |%n";
        System.out.printf(techHeader, "ID", "Name", "Busy Time", "Load (%)");
        System.out.println("=".repeat(52));
        String techData = "| %-4d | %-15.15s | %-12.1f | %-8.1f |%n";
        for (Technician tech : technicians) {
            double techLoad = (tech.getTotalBusyTime() / totalTime) * 100;
            System.out.printf(techData, tech.getId(), tech.getName(), tech.getTotalBusyTime(), techLoad);
        }
        System.out.println("-".repeat(52));

        System.out.println("\nRequest Generators Info:");
        String genHeader = "| %-4s | %-20.20s | %-20.20s | %-15.15s | %-20.15s |%n";
        System.out.printf(genHeader, "ID", "Created Requests", "Removed Requests", "Remaining Requests", "Rejection Probability (%)");
        System.out.println("=".repeat(95));
        String genData = "| %-4d | %-20d | %-20d | %-15d | %-20.2f |%n";
        for (RequestGenerator generator : generators) {
            int createdRequests = generator.getRequestsAmount();
            int removedRequests = generator.getRemovedRequestsCount();
            int remainingRequests = createdRequests - removedRequests;
            double rejectionRate = createdRequests > 0 ? (double) removedRequests / createdRequests * 100 : 0.0;
            System.out.printf(genData, generator.getGeneratorId(), createdRequests, removedRequests, remainingRequests, rejectionRate);
        }
        System.out.println("-".repeat(95));
    }
}