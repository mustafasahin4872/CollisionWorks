package helpers.utils;

public class Index {

    private int i = 0;
    private int select = 0;
    private int N;

    public Index(int N) {
        this.N = N;
    }

    public void setN(int n) {
        N = n;
    }

    // TODO: FIX THIS MESS WITH 5 DIFFERENT INCREMENT FUNCTIONS
    public void add(int n) {
        if (n>=0) increment(n);
        else decrement(-n);
    }

    public void increment(int n) {
        if (n < 0) {
            System.out.println("INCREMENT OPERATION USED WITH NEGATIVE VALUE");
            return;
        }
        i += n;
        i %= N;
    }

    public void increment() {
        increment(1);
    }

    public void decrement(int n) {
        if (n < 0) {
            System.out.println("DECREMENT OPERATION USED WITH NEGATIVE VALUE");
            return;
        }
        i -= n;
        i %= N;
        i += N;
        i %= N;
    }

    public void decrement() {
        decrement(1);
    }

    public void setValue(int i) {
        this.i = i;
    }

    public int getCurrent() {
        return i;
    }

    public int getSelect() {
        return select;
    }

    public void select() {
        if (select == i) {
            select = 0;
        } else {
            select = i;
        }
    }
}
