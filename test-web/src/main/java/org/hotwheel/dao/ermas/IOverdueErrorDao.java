package org.hotwheel.dao.ermas;

import org.apache.ibatis.annotations.Param;

import java.util.List;

//import org.springframework.stereotype.Repository;

/**
 * Created by wangfeng on 16/4/10.
 */
//@Repository("overdueErrorDao")
public interface IOverdueErrorDao {

    /**
     * 拉取脏错数据
     * @return
     */
    List<String> getDirtyAndErrorData(@Param("limit") int limit);
    List<String> getAllDirtyAndErrorData();

    List<String> getAllLossFriends();

    /**
     * 拉取部分债务人的好友缺少债权人
     * @return
     */
    List<String> getPartialDebtorLossCreditor();
}
