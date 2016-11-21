package test.json.bean;

/**
 * Created by wangfeng on 2016/11/21.
 */
public class CreditInfo {
    /*
* "id": "28",
            "user_id": "1025",
            "id_card": "1231231",
            "coll_date": "1214841600",
            "coll_time": "1217312320",
            "credit_type": "C004002",
            "overdue": "1",
            "count": "1",
            "credit_company": "上海资信"
* */
    private String id;
    private String user_id;
    private String id_card;
    private String coll_date;
    private String coll_time;
    private String credit_type;
    private String overdue;
    private String count;
    private String credit_company;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getId_card() {
        return id_card;
    }

    public void setId_card(String id_card) {
        this.id_card = id_card;
    }

    public String getColl_date() {
        return coll_date;
    }

    public void setColl_date(String coll_date) {
        this.coll_date = coll_date;
    }

    public String getColl_time() {
        return coll_time;
    }

    public void setColl_time(String coll_time) {
        this.coll_time = coll_time;
    }

    public String getCredit_type() {
        return credit_type;
    }

    public void setCredit_type(String credit_type) {
        this.credit_type = credit_type;
    }

    public String getOverdue() {
        return overdue;
    }

    public void setOverdue(String overdue) {
        this.overdue = overdue;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getCredit_company() {
        return credit_company;
    }

    public void setCredit_company(String credit_company) {
        this.credit_company = credit_company;
    }
}
