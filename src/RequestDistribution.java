import java.util.ArrayList;
import java.util.List;

class RequestDistribution {
    private final List<Technician> technicians;
    private Buffer buffer;
    private ArrayList<RequestGenerator> generators;
    private int lastTechnicianId = 0;
    private boolean informationOutput;

    public RequestDistribution(List<Technician> technicians, Buffer buffer, boolean informationOutput,ArrayList<RequestGenerator> generators ) {
        this.technicians = technicians;
        this.buffer = buffer;
        this.informationOutput= informationOutput;
        this.generators=generators;
    }

    public boolean distributeRequests(Request requestGet) {
        // Проверка, что запрос не равен null
        if (requestGet == null) {
            System.out.println("Request is null, skipping assignment.");
            return false;  // Если запрос пустой, не обрабатываем его
        }

        Technician technician = getFreeTechnician();
        if (buffer.isEmpty() && technician != null) {
            technician.processRequest(requestGet, informationOutput);
            if (informationOutput) {
                System.out.println("Assigned Request " + requestGet.getId() + " with description " + requestGet.getDescription() + " to Technician " + technician.getName());
            }
        } else {
            boolean getBool = false;
            int getId = buffer.addRequest(requestGet);
            if (getId == 0) {
                getBool = true;
            } else {
                for (RequestGenerator generator : generators) {
                    if (generator.getGeneratorId() == getId) {
                        generator.incrementRemovedRequestsCount();
                        break;
                    }
                }
            }
            if (technician != null) {
                Request requestToDo = buffer.getNextRequest();
                technician.processRequest(requestToDo, informationOutput);
                buffer.removeRequest(requestToDo.getId());
                if (informationOutput) {
                    System.out.println("Assigned Request " + requestToDo.getId() + " with description " + requestGet.getDescription() + " to Technician " + technician.getName());
                }
            }
            return getBool;
        }
        if (informationOutput) {
            buffer.printAllRequests();
        }
        return true;
    }

    public void printBufferDetails() {
        if (buffer.isEmpty()) {
            System.out.println("The request buffer is empty.");
            return;
        }
        System.out.println("\nInformation about buffer:");
        System.out.printf("%-10s %-30s%n", "Request ID", "Description");
        System.out.println("=".repeat(40));
        for (int i = 0; i < buffer.getMaxCapacity(); i++) {
            Request request = buffer.get(i);
            if (request == null) {
                System.out.printf("%-10s %-30s%n", "null", "No request");
            } else {
                System.out.printf("%-10d %-30s%n", request.getId(), request.getDescription());
            }
        }
        System.out.println("-".repeat(40));
    }

    public void updateTime(){
        for (Technician technician: technicians){
            if (!technician.isAvailable()){
                technician.updateServiceTime(informationOutput);
            }
        }
    }

    public Technician getFreeTechnician() {
        Technician technician= null;
        for (int i=lastTechnicianId; i<technicians.size();i++){
            technician=technicians.get(i);
            if (technician.isAvailable()){
                lastTechnicianId=i++;
                return technician;
            }
        }
        for (int i=0; i<lastTechnicianId;i++){
            technician=technicians.get(i);
            if (technician.isAvailable()){
                return technician;
            }
        }
        return null;
    }
}