package org.hotwheel.controller;

import org.mymmsc.api.io.ActionStatus;
import org.mymmsc.j2ee.HttpController;
import org.mymmsc.j2ee.annotation.Controller;
import org.mymmsc.j2ee.annotation.RequestMapping;
import org.mymmsc.j2ee.annotation.ResponseBody;

/**
 * Created by wangfeng on 2016/11/7.
 */
@Controller
@RequestMapping("/innerApi")
public class TestController extends HttpController {
    @RequestMapping("//v2/status")
    @ResponseBody
    public ActionStatus getStatus(String a, String time, String time1) {
        ActionStatus as = new ActionStatus();
        as.set(0, "SUCCESS");

        return as;
    }
}
