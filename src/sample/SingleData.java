package sample;

import sample.model.Polynom;

public class SingleData {
    private static SingleData test;
    public String PSP;
    public Polynom polynom;
    public Polynom polynomB;
    public int pow;
    public int powB;
    public int period;

    public long [][]arrA;
    public long [][]arrB;
    public long [][]arrS;
    public long [][]arrS_first;

    private SingleData() {

    }
    public static SingleData getTest(){
        if (test==null){
            test=new SingleData();
        }
        return test;
    }





}
