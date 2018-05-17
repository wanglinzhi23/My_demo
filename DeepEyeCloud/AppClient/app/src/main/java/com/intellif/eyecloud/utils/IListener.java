package com.intellif.eyecloud.utils;

import com.intellif.eyecloud.bean.post.ListenerBean;

/**
 * Created by intellif on 2017/9/29.
 */

public interface IListener {
    void notifyAllActivity(ListenerBean bean);
}
