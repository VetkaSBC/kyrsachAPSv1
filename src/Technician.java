class Technician {
    private final int id;
    private final String fullName;
    private boolean isAvailable;
    private int currentRequestId;
    private double remainingServiceTime;
    private UniformDistribution uniformDistribution; // Используем равномерное распределение
    private int doneRequestsAmount = 0;
    private double totalBusyTime = 0.0;
    private double timeLefted = 0.0;
    private final boolean type;

    public Technician(int id, String fullName, double minServiceTime, double maxServiceTime, boolean type) {
        this.id = id;
        this.fullName = fullName;
        this.isAvailable = true;
        this.currentRequestId = -1;
        this.remainingServiceTime = 0.0;
        this.uniformDistribution = new UniformDistribution(minServiceTime, maxServiceTime); // Новый способ генерации
        this.type = type;
    }

    public int getId(){
        return id;
    }

    public String getName() {
        return fullName;
    }

    public int getDoneRequestsAmount(){
        return doneRequestsAmount;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public int getCurrentRequestId() {
        return currentRequestId;
    }

    public void processRequest(Request request, boolean informationOutput) {
        this.isAvailable = false;
        this.currentRequestId = request.getId();
        if (type) {
            this.remainingServiceTime = uniformDistribution.generateServiceTime() - timeLefted;
        } else {
            this.remainingServiceTime = uniformDistribution.generateServiceTime() / 2 - timeLefted;
        }
        timeLefted = 0;
        if (informationOutput) {
            System.out.println("Technician " + fullName + " is processing request " + request.getId() + "  service time " + remainingServiceTime);
        }
    }


    public  double getTotalBusyTime(){
        return totalBusyTime;
    }

    public void updateServiceTime(boolean informationOutput){
        if (!isAvailable){
            totalBusyTime++;
            if (remainingServiceTime<1){
                timeLefted=1-remainingServiceTime;
                completeRequest();
                if (informationOutput){
                    System.out.println("Technician "+ fullName+ " is free for now");
                }
            }else{
                remainingServiceTime-=1;
            }
        }
    }

    private void completeRequest() {
        doneRequestsAmount++;
        isAvailable = true;
        currentRequestId = -1;
    }
}