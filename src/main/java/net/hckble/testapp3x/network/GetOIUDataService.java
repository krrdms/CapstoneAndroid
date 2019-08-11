package net.hckble.testapp3x.network;
import android.app.DownloadManager;

import net.hckble.testapp3x.model.OUI;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface GetOIUDataService {
    @Headers({"Content-Type: application/json", "Cache-Control: max-age=640000"})
    @POST("/macouilookup")
    Call<OUI> lookup_oui(@Body RequestBody json);
}
