package domain;

public class Skill {

    private double value;
    
    private double stdDev;
    
    public Skill(double value, double stdDev) {
        this.value = value;
        this.stdDev = stdDev;
    }

    public void setValue(double value) {
        this.value = value;
    }
    
    public void setStdDev(double stdDev) {
        this.stdDev = stdDev;
    }

    public double getValue() {
        return value;
    }

    public double getStdDev() {
        return stdDev;
    }
}
