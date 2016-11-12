package org.hotwheel.dao.dsmp;

import java.util.List;

/**
 * Created by wangfeng on 2016/11/7.
 */
public interface IOverdueMessageDao {
    int countMessage();
    boolean clearExpired();
    boolean clearError(List<Integer> idList);
}
