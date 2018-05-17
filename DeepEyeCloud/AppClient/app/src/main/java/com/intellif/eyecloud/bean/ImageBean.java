package com.intellif.eyecloud.bean;

import java.util.List;

/**
 * Created by intellif on 2017/9/21.
 */

public class ImageBean {

    /**
     * id : 1688873864446745
     * time : 1506132430466
     * uri : http://192.168.2.12:80/ifaas/api/uploads/2017-09-23-10-07-10-420_format.jpg
     * faceUri : http://192.168.2.12:80/ifaas/api/uploads/2017-09-23-10-07-10-420_format_f.jpg
     * faces : 4
     * faceList : [{"Rect":{"left":381,"top":719,"right":454,"bottom":792,"setRight":true,"setBottom":true,"setLeft":true,"setTop":true},"Pose":"IF_StrictFrontal","forbiden":0,"setForbiden":true,"rect":{"left":381,"top":719,"right":454,"bottom":792,"setRight":true,"setBottom":true,"setLeft":true,"setTop":true},"setRect":true,"pose":"IF_StrictFrontal","setPose":true},{"Rect":{"left":93,"top":491,"right":190,"bottom":588,"setRight":true,"setBottom":true,"setLeft":true,"setTop":true},"Pose":"IF_StrictFrontal","forbiden":0,"setForbiden":true,"rect":{"left":93,"top":491,"right":190,"bottom":588,"setRight":true,"setBottom":true,"setLeft":true,"setTop":true},"setRect":true,"pose":"IF_StrictFrontal","setPose":true},{"Rect":{"left":174,"top":677,"right":275,"bottom":778,"setRight":true,"setBottom":true,"setLeft":true,"setTop":true},"Pose":"IF_StrictFrontal","forbiden":0,"setForbiden":true,"rect":{"left":174,"top":677,"right":275,"bottom":778,"setRight":true,"setBottom":true,"setLeft":true,"setTop":true},"setRect":true,"pose":"IF_StrictFrontal","setPose":true},{"Rect":{"left":845,"top":404,"right":958,"bottom":517,"setRight":true,"setBottom":true,"setLeft":true,"setTop":true},"Pose":"IF_StrictFrontal","forbiden":0,"setForbiden":true,"rect":{"left":845,"top":404,"right":958,"bottom":517,"setRight":true,"setBottom":true,"setLeft":true,"setTop":true},"setRect":true,"pose":"IF_StrictFrontal","setPose":true}]
     */

    public String id;
    public long time;
    public String uri;
    public String faceUri;
    public int faces;
    public List<FaceListBean> faceList;


    public static class FaceListBean {
        /**
         * Rect : {"left":381,"top":719,"right":454,"bottom":792,"setRight":true,"setBottom":true,"setLeft":true,"setTop":true}
         * Pose : IF_StrictFrontal
         * forbiden : 0
         * setForbiden : true
         * rect : {"left":381,"top":719,"right":454,"bottom":792,"setRight":true,"setBottom":true,"setLeft":true,"setTop":true}
         * setRect : true
         * pose : IF_StrictFrontal
         * setPose : true
         */

        public RectBean Rect;
        public String Pose;
        public int forbiden;
        public boolean setForbiden;
        public RectBeanX rect;
        public boolean setRect;
        public String pose;
        public boolean setPose;

        public static class RectBean {
            /**
             * left : 381
             * top : 719
             * right : 454
             * bottom : 792
             * setRight : true
             * setBottom : true
             * setLeft : true
             * setTop : true
             */

            public int left;
            public int top;
            public int right;
            public int bottom;
            public boolean setRight;
            public boolean setBottom;
            public boolean setLeft;
            public boolean setTop;

        }

        public static class RectBeanX {
            /**
             * left : 381
             * top : 719
             * right : 454
             * bottom : 792
             * setRight : true
             * setBottom : true
             * setLeft : true
             * setTop : true
             */

            public int left;
            public int top;
            public int right;
            public int bottom;
            public boolean setRight;
            public boolean setBottom;
            public boolean setLeft;
            public boolean setTop;

        }
    }
}
