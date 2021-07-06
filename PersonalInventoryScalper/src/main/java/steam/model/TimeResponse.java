package steam.model;

import com.google.gson.annotations.SerializedName;

//From https://github.com/steevp/UpdogFarmer/blob/f476c77aae1f1553456dc240c4c2407234f56dad/app/src/main/java/com/steevsapps/idledaddy/steam/model/TimeResponse.java#L5
public class TimeResponse {
    @SerializedName("server_time")
    private long serverTime;

    public long getServerTime() {
        return serverTime;
    }
}
