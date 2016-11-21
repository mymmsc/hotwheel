package test.json;

import org.mymmsc.api.context.JsonAdapter;
import test.json.bean.CreditInfo;

/**
 * Created by wangfeng on 2016/11/21.
 */
public class TestErrorJson {

    public static void main(String[] args) {
        String body = "{\"error\":{\"returnCode\":\"0\",\"returnMessage\":\"操作成功\",\"returnUserMessage\":\"\"},\"data\":\"null\"}";
        JsonAdapter json = null;
        try {
            json = JsonAdapter.parse(body);
            if (json != null) {
                //TypeReference<InnerApiResult<CreditInfo[]>> clazz = new TypeReference<InnerApiResult<CreditInfo[]>>(){};
                //InnerApiResult<CreditInfo[]> obj = new InnerApiResult<CreditInfo[]>();
                //System.out.println(obj.getClass().getName());
                InnerApiResult creditInfoResponse = json.get(InnerApiResult.class, CreditInfo[].class);
                if (creditInfoResponse != null && creditInfoResponse.data != null) {

                    for (CreditInfo creditInfo :
                            (CreditInfo[]) creditInfoResponse.data) {
                        System.out.println("id = " + creditInfo.getId());
                    }
                }
            }
        } catch (Exception e) {

        } finally {
            json.close();
        }

    }
}
