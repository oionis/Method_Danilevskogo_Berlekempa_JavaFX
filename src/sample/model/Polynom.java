package sample.model;




public class Polynom {
    private Integer j;
    private Integer mainPart;
    private String T;

    public Polynom(Integer j, Integer mainPart, String t) {
        this.j = j;
        this.mainPart = mainPart;
        T = t;
    }

    @Override
    public String toString() {
        return j + " " + mainPart + T;

    }

    public Integer getJ() {
        return j;
    }

    public Integer getMainPart() {
        return mainPart;
    }

    public String getT() {
        return T;
    }

    public void setJ(Integer j) {
        this.j = j;
    }

    public void setMainPart(Integer mainPart) {
        this.mainPart = mainPart;
    }

    public void setT(String t) {
        T = t;
    }
}
