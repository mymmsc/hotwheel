package org.hotwheel.beans;

import org.hotwheel.assembly.Api;

/**
 * 多态值
 * <p>
 * Created by wangfeng on 2016/11/13.
 * @since 2.1.7
 */
public class EValue {
    private String value;

    public EValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public int toInt() {
        return Integer.parseInt(this.value);
    }

    public long toLong() {
        return Long.parseLong(this.value);
    }

    public String[] toStringArray(String regex) {
        return this.value.split(regex);
    }

    public boolean toBoolean() {
        boolean bRet = false;
        if (!Api.isEmpty(value)) {
            if("true,on".indexOf(value.toLowerCase()) >= 0) {
                bRet = true;
            } else if ("false,off".indexOf(value.toLowerCase()) >= 0) {
                bRet = false;
            }
        }
        return bRet;
    }
}
