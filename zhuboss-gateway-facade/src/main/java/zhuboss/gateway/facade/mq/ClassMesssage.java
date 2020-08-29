package zhuboss.gateway.facade.mq;

public class ClassMesssage {
    private String className;
    private Object message;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }
}
