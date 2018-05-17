package com.intellif.eyecloud.bean;

import java.io.Serializable;

/**
 * Created by intellif on 2017/9/8.
 */

public class RecordBean implements Serializable{

    /**
     * alarmId : 3898
     * userId : 145
     * bName : 呃呃呃
     * faceId : 1970510757786989
     * alarmBigurl : http://intellif-hd.oss-cn-hangzhou.aliyuncs.com/FaceWareHouse/supermarket/gd/kxg/ImgWareHouse/src_0_3/20170930/20170930T123612_195.jpg
     * alarmSmallurl : http://intellif-hd.oss-cn-hangzhou.aliyuncs.com/FaceWareHouse/supermarket/gd/kxg/FaceWareHouse/src_0_3/20170930/20170930T123612_195_197_59952.jpg
     * taskId : 13
     * blackId : 1077
     * blackImageId : 1970349280775899
     * blackSmallurl : http://39.108.169.236/ifaas/api/uploads/2017-09-30-12-03-01-786_format_f.jpg
     * blackBigurl : 0
     * cameraName : 七楼办公室左侧
     * created : 1506746157000
     * processTime : 1507710230000
     * threshold : 0.8533931
     * type : 2
     */

    public String alarmId;
    public int userId;
    public int id;
    public String bName;
    public long faceId;
    public String alarmBigurl;
    public String alarmSmallurl;
    public int taskId;
    public int blackId;
    public long blackImageId;
    public String blackSmallurl;
    public String blackBigurl;
    public String cameraName;
    public String description;
    public long created;
    public long processTime;
    public double threshold;
    public int type;
    public String address;
}
