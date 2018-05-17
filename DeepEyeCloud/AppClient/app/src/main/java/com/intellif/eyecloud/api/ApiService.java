package com.intellif.eyecloud.api;

/**
 * Created by intellif on 2017/9/18.
 */

import com.intellif.eyecloud.bean.UserBean;
import com.squareup.okhttp.ResponseBody;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;
import rx.Observable;

/**
 * 网络请求管理类
 */
public interface  ApiService {

    //用户登录
    @Headers({"Authorization:Basic Y2xpZW50YXBwOjEyMzQ1Ng==",
            "Content-Type:application/x-www-form-urlencoded"})
    @POST("oauth/token")
    Observable<UserBean> userLogin(@Query("username") String username,
                                   @Query("password") String password,
                                   @Query("grant_type") String grant_type,
                                   @Query("scope") String scope,
                                   @Query("client_secret") String client_secret,
                                   @Query("client_id") String client_id);
    //条件查询门店信息
    @POST("intellif/area/query")
    Observable<ResponseBody> getArea(@Header("Authorization") String header, @Body RequestBody requestBody);
    //查询设备
    @POST("intellif/camera/query")
    Observable<ResponseBody> getManage(@Header("Authorization") String header, @Body RequestBody requestBody);
    //添加设备
    @POST("intellif/camera")
    Observable<ResponseBody> addCamera(@Header("Authorization") String header,@Body RequestBody requestBody);
    //上传人脸接口
    @Multipart
    @POST("intellif/image/upload/true?type=1")
    Observable<UserBean> uploadFile(@Header("Authorization") String header,@Part("file") MultipartBody.Part file);
    //布控用户
    @POST("intellif/person/detail")
    Observable<UserBean> addBk(@Header("Authorization") String header, @Body RequestBody requestBody);
    //查看布控用户/api/intellif/alarm/station/query
    @POST("/api/intellif/alarm/station/query")
    Observable<ResponseBody> queryBK(@Header("Authorization") String header, @Body RequestBody requestBody);

}
