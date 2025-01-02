package lvhaoxuan.custom.cuilian.util;

public class ParamGroup {
    public Object obj;
    public Class<?> type;

    public ParamGroup(Object obj, Class<?> type) {
        this.obj = obj;
        this.type = type;
    }

    public ParamGroup(Object obj) {
        this.obj = obj;
        if (obj != null) {
            this.type = obj.getClass();
        } else {
            this.type = Object.class;
        }

    }
}
