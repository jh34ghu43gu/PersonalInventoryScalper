package steam;


import java.util.Map;

import in.dragonbra.javasteam.types.KeyValue;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import steam.model.TimeQuery;

//From https://github.com/steevp/UpdogFarmer/blob/f476c77aae1f1553456dc240c4c2407234f56dad/app/src/main/java/com/steevsapps/idledaddy/steam/SteamAPI.java#L17
public interface SteamAPI {

    @FormUrlEncoded
    @POST("ISteamUserAuth/AuthenticateUser/v1/")
    Call<KeyValue> authenticateUser(@FieldMap(encoded = true) Map<String,String> args);

    @FormUrlEncoded
    @POST("ITwoFactorService/QueryTime/v0001")
    Call<TimeQuery> queryServerTime(@Field("steamid") String steamId);
}
