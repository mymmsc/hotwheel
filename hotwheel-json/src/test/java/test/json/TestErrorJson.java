package test.json;

import org.mymmsc.api.context.JsonAdapter;
import test.json.bean.CreditInfo;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by wangfeng on 2016/11/21.
 */
public class TestErrorJson {

    private final static Class<?> classGenericType = sun.reflect.generics.reflectiveObjects.TypeVariableImpl.class;

    private static Class<?> getFieldClass(Class<?> clazz, int index) {

        //Class<?> cls = clazz.getGenericSuperclass();
        Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (index >= params.length || index < 0) {
            throw new RuntimeException("Index outof bounds");
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }
        return (Class) params[index];
    }

    /**
     * 判断字段是否泛型类型
     *
     * @param field
     * @return
     */
    private static boolean isGeneric(Field field) {
        boolean bRet = false;
        System.out.println("field name=" + field.getName());

        if (field.getGenericType().getClass() != java.lang.Class.class) {
            bRet = true;
        }
        return bRet;
    }

    /**
     * 获取field的类型，如果是复合对象，获取的是泛型的类型
     *
     * @param field
     * @return
     */
    private static Class getFieldClass(Field field) {
        Class fieldClazz = field.getType();

        Type fc = field.getGenericType(); // 关键的地方，如果是List类型，得到其Generic的类型
        System.out.println(fc.getClass());
        if(fc.getClass() == classGenericType) {
            System.out.println("generics");
        }
        Object[] os = fc.getClass().getSigners();
        String fcName = fc.getClass().getName();
        System.out.println("fcName=" + fcName);
        fcName = fc.toString();
        System.out.println("fcName=" + fcName);
        if (fc instanceof ParameterizedType) // 如果是泛型参数的类型
        {
            ParameterizedType pt = (ParameterizedType) fc;

            fieldClazz = (Class) pt.getActualTypeArguments()[0]; //得到泛型里的class类型对象。
        }
        System.out.println("----------");
        return fieldClazz;
    }
    public static void main(String[] args) {
        String body = "{\"error\":{\"returnCode\":\"0\",\"returnMessage\":\"操作成功\",\"returnUserMessage\":\"\"},\"data\":{}}";
        JsonAdapter json = null;
        try {
            json = JsonAdapter.parse(body);
            if (json != null) {
                //TypeReference<InnerApiResult<CreditInfo[]>> clazz = new TypeReference<InnerApiResult<CreditInfo[]>>(){};
                //InnerApiResult<CreditInfo[]> obj = new InnerApiResult<CreditInfo[]>();
                //System.out.println(obj.getClass().getName());
                InnerResult<CreditInfo> creditInfoResponse = json.get(InnerResult.class, CreditInfo.class);
                Field field = InnerResult.class.getDeclaredField("data");
                System.out.println(isGeneric(field));
                field = InnerResult.class.getDeclaredField("error");
                System.out.println(isGeneric(field));
                field = InnerResult.class.getDeclaredField("a1");
                System.out.println(isGeneric(field));
                field = InnerResult.class.getDeclaredField("b1");
                System.out.println(isGeneric(field));
                if (creditInfoResponse != null && creditInfoResponse.data != null) {
/*
                    for (CreditInfo creditInfo : (CreditInfo[]) creditInfoResponse.data) {
                        System.out.println("id = " + creditInfo.getId());
                    }
                    */
                }

            }
        } catch (Exception e) {

        } finally {
            json.close();
        }

    }
}
