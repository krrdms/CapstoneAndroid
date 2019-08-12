package net.hckble.testapp3x.network;
import net.hckble.testapp3x.model.OUI;
import net.hckble.testapp3x.model.OUIqry;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface GetOIUDataService {

    @GET("/macouilookup")
    Call<List<OUI>> get_oui();

    @Headers({"Content-Type: application/json", "Cache-Control: max-age=640000"})
    @POST("/macouilookup")
    Call<OUI> lookup_oui(@Body OUIqry o);
}

