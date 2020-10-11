package com.winnie.filemanager_android.net;

import com.winnie.filemanager_android.common.Result;
import com.winnie.filemanager_android.model.FileUploadResDTO;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface ApiService {
    /**
     * 上传文件
     *
     * @param map
     * @param number
     * @param date
     * @return
     */
    @Multipart
    @POST("/file/v1/upload")
    Observable<Response<Result<FileUploadResDTO>>> uploadFile(@PartMap Map<String, RequestBody> map,
                                                              @Query("type") Integer type,
                                                              @Query("number") String number,
                                                              @Query("date") Long date);


    /**
     * 测试服务器连接
     *
     * @return
     */
    @GET("/file/v1/test")
    Observable<Response<Result<Boolean>>> testServer();

}
