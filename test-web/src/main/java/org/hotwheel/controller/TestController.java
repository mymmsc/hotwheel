package org.hotwheel.controller;

import org.hotwheel.beans.factory.annotation.Autowired;
import org.hotwheel.dao.dsmp.IOverdueMessageDao;
import org.hotwheel.dao.ermas.IOverdueErrorDao;
import org.mymmsc.api.io.ActionStatus;
import org.mymmsc.j2ee.HttpController;
import org.mymmsc.j2ee.annotation.Controller;
import org.mymmsc.j2ee.annotation.RequestMapping;
import org.mymmsc.j2ee.annotation.ResponseBody;

import java.util.List;

/**
 * Created by wangfeng on 2016/11/7.
 */
@Controller
@RequestMapping("/innerApi")
public class TestController extends HttpController {

    @Autowired
    private IOverdueErrorDao overdueError;

    @Autowired
    private IOverdueMessageDao overdueMessage;

    @RequestMapping("//v2/status")
    @ResponseBody
    public ActionStatus getStatus(String a, String time, String time1) {
        ActionStatus as = new ActionStatus();
        List<String> listError = overdueError.getAllDirtyAndErrorData();
        //sqlSession.commit();
        if(listError != null) {
            for (String uuid : listError) {
                System.out.println("cuishou:" + uuid);
            }
        } else {
            System.out.println("cuishou:" +"null");
        }
        int count = overdueMessage.countMessage();
        System.out.println("dsmp-count:" + count);
        as.set(0, "SUCCESS");

        return as;
    }
}
