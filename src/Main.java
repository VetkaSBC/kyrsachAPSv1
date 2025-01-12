import java.util.Locale;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("If you want to enter your data, press E, otherwise any other button");
        String getIt = in.next().toLowerCase(Locale.ROOT);

        int queueBufferCapacity = 10;
        int technicianQuantity = 20;
        int requestQuantity = 4200;
        int generatorsAmount = 25;
        double lambda = 1.0; // Интенсивность пуассоновского потока

        if (getIt.equals("e")) {
            System.out.println("Enter the buffer capacity (only the number >0 is accepted):");
            queueBufferCapacity = in.nextInt();

            System.out.println("Enter the number of technicians (only the number >0 is accepted):");
            technicianQuantity = in.nextInt();

            System.out.println("Enter the number of requests (only the number >0 is accepted):");
            requestQuantity = in.nextInt();

            System.out.println("Enter the lambda value for Poisson distribution (e.g., 2.0):");
            lambda = in.nextDouble();

            if (queueBufferCapacity <= 0 || technicianQuantity <= 0 || requestQuantity <= 0 || lambda <= 0) {
                System.out.println("Invalid input!");
                return;
            }
        }

        System.out.println("In this example we have " + technicianQuantity + " technicians, "
                + requestQuantity + " requests, buffer capacity = " + queueBufferCapacity + ", lambda = " + lambda);

        MainSystem system = new MainSystem(queueBufferCapacity, technicianQuantity, requestQuantity, generatorsAmount);
        system.run(lambda); // Передаем `lambda` вместо границ равномерного распределения
        in.close();
    }
}