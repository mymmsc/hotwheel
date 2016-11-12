package org.hotwheel.j2ee.scheduler;

import org.mymmsc.api.assembly.Api;

import java.io.File;


/**
 * 催收业务数据批量
 *
 * Created by wangfeng on 16/8/21.
 */
public class TaskBatchThread extends ServiceRunnable {

    private File productFile = null;
    private File orderFile = null;
    private File repayFile = null;



    @Override
    protected void service() {
        while (isRunning()) {
            Api.sleep(100);
        }
    }

}
