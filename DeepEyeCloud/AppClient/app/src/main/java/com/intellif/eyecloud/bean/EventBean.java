package com.intellif.eyecloud.bean;

import java.util.List;

/**
 * Created by intellif on 2017/9/22.
 */

public class EventBean {

    /**
     * created : 1503928337000
     * updated : null
     * id : 618
     * realName : laib55
     * birthday : null
     * nation : null
     * realGender : 2
     * cid : df465
     * address : null
     * photoData : http://192.168.2.66/IFaceServer/spring-boot_restful/uploads/2017-08-28-21-52-04-443_format_f.jpg
     * crimeType : -1
     * crimeAddress : null
     * description : ssssssssssssss
     * ruleId : -1
     * identity : -1
     * bankId : 2
     * status : 1
     * owner : superuser
     * ownerStation : 科学馆核心区
     * important : -1
     * arrest : -1
     * similarSuspect : 0
     * inStation : 0
     * history : 0
     * type : 0
     * isUrgent : 0
     * fkType : 0
     * pushObject : null
     * crimeName : null
     * index : null
     * events : [{"id":3915,"personId":618,"faceId":"26458758336251708","send":0,"confidence":0.9536682367324829,"imageData":"http://192.168.2.15/store2_0/FaceWareHouse/src_0_1/20170831/20170831T215043_5611_5650_16234.jpg","scene":"http://192.168.2.15/store2_0/ImgWareHouse/src_0_1/20170831/20170831T215043_5611.jpg","cameraId":"5","areaId":"6","cameraName":"dfgdg","geoString":"gsdas","status":null,"time":1504187443000},{"id":3895,"personId":618,"faceId":"26177283359534359","send":0,"confidence":0.929836094379425,"imageData":"http://192.168.2.15/store2_0/FaceWareHouse/src_0_1/20170829/20170829T124118_691_696_13354.jpg","scene":"http://192.168.2.15/store2_0/ImgWareHouse/src_0_1/20170829/20170829T124118_691.jpg","cameraId":"5","areaId":"6","cameraName":"dfgdg","geoString":"gsdas","status":null,"time":1503981678000},{"id":3893,"personId":618,"faceId":"26177283359533482","send":0,"confidence":0.9236871004104614,"imageData":"http://192.168.2.15/store2_0/FaceWareHouse/src_0_1/20170829/20170829T083403_57_57_12976.jpg","scene":"http://192.168.2.15/store2_0/ImgWareHouse/src_0_1/20170829/20170829T083403_57.jpg","cameraId":"4","areaId":"6","cameraName":"asf1","geoString":"sdfdsf1","status":null,"time":1503966843000}]
     * starttime : 2017-08-23
     * endtime : 2100-01-01
     */

    public long created;
    public Object updated;
    public String id;
    public String realName;
    public Object birthday;
    public Object nation;
    public int realGender;
    public String cid;
    public Object address;
    public String photoData;
    public int crimeType;
    public Object crimeAddress;
    public String description;
    public int ruleId;
    public int identity;
    public int bankId;
    public int status;
    public String owner;
    public String ownerStation;
    public int important;
    public int arrest;
    public int similarSuspect;
    public int inStation;
    public int history;
    public int type;
    public int isUrgent;
    public int fkType;
    public Object pushObject;
    public Object crimeName;
    public Object index;
    public String starttime;
    public String endtime;
    public List<EventsBean> events;


    public static class EventsBean {
        /**
         * id : 3915
         * personId : 618
         * faceId : 26458758336251708
         * send : 0
         * confidence : 0.9536682367324829
         * imageData : http://192.168.2.15/store2_0/FaceWareHouse/src_0_1/20170831/20170831T215043_5611_5650_16234.jpg
         * scene : http://192.168.2.15/store2_0/ImgWareHouse/src_0_1/20170831/20170831T215043_5611.jpg
         * cameraId : 5
         * areaId : 6
         * cameraName : dfgdg
         * geoString : gsdas
         * status : null
         * time : 1504187443000
         */

        public int id;
        public int personId;
        public String faceId;
        public int send;
        public double confidence;
        public String imageData;
        public String scene;
        public String cameraId;
        public String areaId;
        public String cameraName;
        public String geoString;
        public Object status;
        public long time;
    }
}
