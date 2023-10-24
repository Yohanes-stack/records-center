package test;

public abstract class AbstractTemplate implements Template {


    @Override
    public String print() {
        System.out.println("日志收集...");
        return doPrint();
    }

    public abstract String doPrint();
}
