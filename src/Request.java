class Request {
    private final int id;
    private final int generatorId;
    private final String description;

    public Request(int id, String description, int generatorId) {
        this.id = id;
        this.description = description;
        this.generatorId=generatorId;
    }

    public int getGeneratorId() {
        return generatorId;
    }

    public  String getDescription(){
        return this.description;
    }

    public int getId() {
        return id;
    }
}