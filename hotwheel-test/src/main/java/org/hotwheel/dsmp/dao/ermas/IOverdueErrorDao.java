package org.hotwheel.dsmp.dao.ermas;

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
    public List<String> getDirtyAndErrorData(@Param("limit") int limit);
    public List<String> getAllDirtyAndErrorData();

    public List<String> getAllLossFriends();

    /**
     * 拉取部分债务人的好友缺少债权人
     * @return
     */
    public List<String> getPartialDebtorLossCreditor();
}
