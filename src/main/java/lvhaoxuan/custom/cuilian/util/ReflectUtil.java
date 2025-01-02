package lvhaoxuan.custom.cuilian.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import lvhaoxuan.custom.cuilian.NewCustomCuiLianPro;

public class ReflectUtil {
    public ReflectUtil() {
    }

    public static Object create(Class<?> clazz, ParamGroup... args) {
        try {
            Class<?>[] types = new Class[args.length];
            Object[] objs = new Object[args.length];

            for(int i = 0; i < args.length; ++i) {
                types[i] = args[i].type;
                objs[i] = args[i].obj;
            }

            Constructor<?> cons = clazz.getConstructor(types);
            cons.setAccessible(true);
            return cons.newInstance(objs);
        } catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException ex) {
            if (NewCustomCuiLianPro.debug) {
                Logger.getLogger(ReflectUtil.class.getName()).log(Level.SEVERE, (String)null, ex);
            }

            return null;
        }
    }

    public static Object getField(Object obj, String fieldName, boolean declared) {
        try {
            Field field = null;
            if (declared) {
                field = obj.getClass().getDeclaredField(fieldName);
            } else {
                field = obj.getClass().getField(fieldName);
            }

            field.setAccessible(true);
            return field.get(obj);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException ex) {
            if (NewCustomCuiLianPro.debug) {
                Logger.getLogger(ReflectUtil.class.getName()).log(Level.SEVERE, (String)null, ex);
            }

            return null;
        }
    }

    public static Object getField(Class<?> clazz, Object obj, String fieldName, boolean declared) {
        try {
            Field field = null;
            if (declared) {
                field = obj.getClass().getDeclaredField(fieldName);
            } else {
                field = obj.getClass().getField(fieldName);
            }

            field.setAccessible(true);
            return field.get(obj);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException ex) {
            if (NewCustomCuiLianPro.debug) {
                Logger.getLogger(ReflectUtil.class.getName()).log(Level.SEVERE, (String)null, ex);
            }

            return null;
        }
    }

    public static void setField(Object obj, String fieldName, Object fieldObj, boolean declared) {
        try {
            Field field = null;
            if (declared) {
                field = obj.getClass().getDeclaredField(fieldName);
            } else {
                field = obj.getClass().getField(fieldName);
            }

            field.setAccessible(true);
            field.set(obj, fieldObj);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException ex) {
            if (NewCustomCuiLianPro.debug) {
                Logger.getLogger(ReflectUtil.class.getName()).log(Level.SEVERE, (String)null, ex);
            }
        }

    }

    public static void setField(Class<?> clazz, Object obj, String fieldName, Object fieldObj, boolean declared) {
        try {
            Field field = null;
            if (declared) {
                field = obj.getClass().getDeclaredField(fieldName);
            } else {
                field = obj.getClass().getField(fieldName);
            }

            field.setAccessible(true);
            field.set(obj, fieldObj);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException ex) {
            if (NewCustomCuiLianPro.debug) {
                Logger.getLogger(ReflectUtil.class.getName()).log(Level.SEVERE, (String)null, ex);
            }
        }

    }

    public static Object doMethod(Object obj, String methodName, ParamGroup... args) {
        try {
            Class<?>[] types = new Class[args.length];
            Object[] objs = new Object[args.length];

            for(int i = 0; i < args.length; ++i) {
                types[i] = args[i].type;
                objs[i] = args[i].obj;
            }

            Method method = obj.getClass().getMethod(methodName, types);
            method.setAccessible(true);
            return method.invoke(obj, objs);
        } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException ex) {
            if (NewCustomCuiLianPro.debug) {
                Logger.getLogger(ReflectUtil.class.getName()).log(Level.SEVERE, (String)null, ex);
            }

            return null;
        }
    }

    public static Object doMethod(Class<?> clazz, Object obj, String methodName, ParamGroup... args) {
        try {
            Class<?>[] types = new Class[args.length];
            Object[] objs = new Object[args.length];

            for(int i = 0; i < args.length; ++i) {
                types[i] = args[i].type;
                objs[i] = args[i].obj;
            }

            Method method = clazz.getMethod(methodName, types);
            method.setAccessible(true);
            return method.invoke(obj, objs);
        } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException ex) {
            if (NewCustomCuiLianPro.debug) {
                Logger.getLogger(ReflectUtil.class.getName()).log(Level.SEVERE, (String)null, ex);
            }

            return null;
        }
    }

    public static Object doStaticMethod(Class<?> clazz, String methodName, ParamGroup... args) {
        try {
            Class<?>[] types = new Class[args.length];
            Object[] objs = new Object[args.length];

            for(int i = 0; i < args.length; ++i) {
                types[i] = args[i].type;
                objs[i] = args[i].obj;
            }

            Method method = clazz.getMethod(methodName, types);
            method.setAccessible(true);
            return method.invoke((Object)null, objs);
        } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException ex) {
            if (NewCustomCuiLianPro.debug) {
                Logger.getLogger(ReflectUtil.class.getName()).log(Level.SEVERE, (String)null, ex);
            }

            return null;
        }
    }

    public static Object getEnum(Class<?> clazz, String fieldName) {
        try {
            Object[] objects = clazz.getEnumConstants();

            for(Object obj : objects) {
                if (doMethod(obj, "name").equals(fieldName)) {
                    return obj;
                }
            }
        } catch (IllegalArgumentException | SecurityException ex) {
            if (NewCustomCuiLianPro.debug) {
                Logger.getLogger(ReflectUtil.class.getName()).log(Level.SEVERE, (String)null, ex);
            }
        }

        return null;
    }
}
