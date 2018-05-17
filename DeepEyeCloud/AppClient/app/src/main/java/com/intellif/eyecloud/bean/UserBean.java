package com.intellif.eyecloud.bean;

import java.util.List;

/**
 * Created by intellif on 2017/9/8.
 */

public class UserBean {

    /**
     * access_token : ca59a6b4-7083-4bb4-bacb-ba87a6ec03d6
     * token_type : bearer
     * refresh_token : 5a07d81f-1285-43ba-a054-902fa331cee1
     * expires_in : 33495
     * scope : read write
     * oauth_AIK_role_info_s : [{"created":1456299127000,"updated":1456299127000,"resIds":"1,100,200,300,400,500,600,700,800","id":1,"name":"SUPER_ADMIN","cnName":"超级管理员","modules":"1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23","authority":"SUPER_ADMIN"}]
     * oauth_AIK_oauth_res_s : [{"id":0,"uri":"","cnName":"数据共享","scopes":""},{"id":0,"uri":"","cnName":"布控功能","scopes":""},{"id":0,"uri":"","cnName":"高级管理","scopes":""},{"id":0,"uri":"","cnName":"数据挖掘","scopes":""},{"id":0,"uri":"","cnName":"数据统计","scopes":""},{"id":0,"uri":"","cnName":"报警导出","scopes":""},{"id":0,"uri":"","cnName":"离线布控","scopes":""},{"id":0,"uri":"","cnName":"人像1:1比对","scopes":""},{"id":0,"uri":"","cnName":"通用","scopes":""}]
     * haslogin :
     * oauth_AIK_user_info : {"created":1462930628000,"updated":1464402138000,"id":1,"name":"superuser","login":"superuser","password":"e10adc3949ba59abbe56e057f20f883e","email":null,"remark":null,"gender":0,"mobile":"","age":0,"post":"一级警员","policeStationId":1,"policeStationNames":null,"policeStationName":null,"cameraRights":"","faceId":"0","startTime":"1970-01-01 00:00:00","endTime":"2050-01-01 00:00:00","areaIds":null,"cameraIds":null,"opened":null,"resIds":"1,100,201","roleTypeName":"MIDDLE_ADMIN","specialSign":0,"cTypeIds":"1,2,3,4","zone":null,"roleIds":"1"}
     * oauth_AIK_user_detail : {"created":null,"updated":null,"id":1,"name":"superuser","login":"superuser","password":"e10adc3949ba59abbe56e057f20f883e","email":null,"remark":null,"gender":0,"mobile":"","age":0,"post":"一级警员","policeStationId":1,"policeStationNames":null,"policeStationName":null,"cameraRights":"","faceId":"0","startTime":"1970-01-01 00:00:00","endTime":"2050-01-01 00:00:00","areaIds":null,"cameraIds":null,"opened":null,"resIds":"1,100,201","roleTypeName":"MIDDLE_ADMIN","specialSign":0,"cTypeIds":"1,2,3,4","zone":null,"roleIds":"1","username":"superuser","enabled":true,"authorities":[{"created":1456299127000,"updated":1456299127000,"resIds":"1,100,200,300,400,500,600,700,800","id":1,"name":"SUPER_ADMIN","cnName":"超级管理员","modules":"1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23","authority":"SUPER_ADMIN"}],"accountNonLocked":true,"accountNonExpired":true,"credentialsNonExpired":true}
     */

    public String access_token;
    public String token_type;
    public String refresh_token;
    public int expires_in;
    public String scope;
    public String haslogin;
    public OauthAIKUserInfoBean oauth_AIK_user_info;
    public OauthAIKUserDetailBean oauth_AIK_user_detail;
    public List<OauthAIKRoleInfoSBean> oauth_AIK_role_info_s;
    public List<OauthAIKOauthResSBean> oauth_AIK_oauth_res_s;
    public String error;
    public String error_description;

    public static class OauthAIKUserInfoBean {
        /**
         * created : 1462930628000
         * updated : 1464402138000
         * id : 1
         * name : superuser
         * login : superuser
         * password : e10adc3949ba59abbe56e057f20f883e
         * email : null
         * remark : null
         * gender : 0
         * mobile :
         * age : 0
         * post : 一级警员
         * policeStationId : 1
         * policeStationNames : null
         * policeStationName : null
         * cameraRights :
         * faceId : 0
         * startTime : 1970-01-01 00:00:00
         * endTime : 2050-01-01 00:00:00
         * areaIds : null
         * cameraIds : null
         * opened : null
         * resIds : 1,100,201
         * roleTypeName : MIDDLE_ADMIN
         * specialSign : 0
         * cTypeIds : 1,2,3,4
         * zone : null
         * roleIds : 1
         */

        public long created;
        public long updated;
        public int id;
        public String name;
        public String login;
        public String password;
        public Object email;
        public Object remark;
        public int gender;
        public String mobile;
        public int age;
        public String post;
        public int policeStationId;
        public Object policeStationNames;
        public Object policeStationName;
        public String cameraRights;
        public String faceId;
        public String startTime;
        public String endTime;
        public Object areaIds;
        public Object cameraIds;
        public Object opened;
        public String resIds;
        public String roleTypeName;
        public int specialSign;
        public String cTypeIds;
        public Object zone;
        public String roleIds;


    }

    public static class OauthAIKUserDetailBean {
        /**
         * created : null
         * updated : null
         * id : 1
         * name : superuser
         * login : superuser
         * password : e10adc3949ba59abbe56e057f20f883e
         * email : null
         * remark : null
         * gender : 0
         * mobile :
         * age : 0
         * post : 一级警员
         * policeStationId : 1
         * policeStationNames : null
         * policeStationName : null
         * cameraRights :
         * faceId : 0
         * startTime : 1970-01-01 00:00:00
         * endTime : 2050-01-01 00:00:00
         * areaIds : null
         * cameraIds : null
         * opened : null
         * resIds : 1,100,201
         * roleTypeName : MIDDLE_ADMIN
         * specialSign : 0
         * cTypeIds : 1,2,3,4
         * zone : null
         * roleIds : 1
         * username : superuser
         * enabled : true
         * authorities : [{"created":1456299127000,"updated":1456299127000,"resIds":"1,100,200,300,400,500,600,700,800","id":1,"name":"SUPER_ADMIN","cnName":"超级管理员","modules":"1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23","authority":"SUPER_ADMIN"}]
         * accountNonLocked : true
         * accountNonExpired : true
         * credentialsNonExpired : true
         */

        public Object created;
        public Object updated;
        public int id;
        public String name;
        public String login;
        public String password;
        public Object email;
        public Object remark;
        public int gender;
        public String mobile;
        public int age;
        public String post;
        public int policeStationId;
        public Object policeStationNames;
        public Object policeStationName;
        public String cameraRights;
        public String faceId;
        public String startTime;
        public String endTime;
        public Object areaIds;
        public Object cameraIds;
        public Object opened;
        public String resIds;
        public String roleTypeName;
        public int specialSign;
        public String cTypeIds;
        public Object zone;
        public String roleIds;
        public String username;
        public boolean enabled;
        public boolean accountNonLocked;
        public boolean accountNonExpired;
        public boolean credentialsNonExpired;
        public List<AuthoritiesBean> authorities;
    }

        public static class AuthoritiesBean {
            /**
             * created : 1456299127000
             * updated : 1456299127000
             * resIds : 1,100,200,300,400,500,600,700,800
             * id : 1
             * name : SUPER_ADMIN
             * cnName : 超级管理员
             * modules : 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23
             * authority : SUPER_ADMIN
             */

            public long created;
            public long updated;
            public String resIds;
            public int id;
            public String name;
            public String cnName;
            public String modules;
            public String authority;


    }

    public static class OauthAIKRoleInfoSBean {
        /**
         * created : 1456299127000
         * updated : 1456299127000
         * resIds : 1,100,200,300,400,500,600,700,800
         * id : 1
         * name : SUPER_ADMIN
         * cnName : 超级管理员
         * modules : 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23
         * authority : SUPER_ADMIN
         */

        public long created;
        public long updated;
        public String resIds;
        public int id;
        public String name;
        public String cnName;
        public String modules;
        public String authority;


    }

    public static class OauthAIKOauthResSBean {
        /**
         * id : 0
         * uri :
         * cnName : 数据共享
         * scopes :
         */

        public int id;
        public String uri;
        public String cnName;
        public String scopes;


    }
}
