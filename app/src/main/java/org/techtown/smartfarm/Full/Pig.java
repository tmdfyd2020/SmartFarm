package org.techtown.smartfarm.Full;

public class Pig {
    private String id;
    private String temp;
    private String condition;

    public Pig() { }

    public Pig(String id, String temp) {
        this.id = id;
        this.temp = temp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
