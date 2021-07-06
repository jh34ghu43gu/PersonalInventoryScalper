package steam.model;

import com.google.gson.annotations.SerializedName;

//From https://github.com/steevp/UpdogFarmer/blob/f476c77aae1f1553456dc240c4c2407234f56dad/app/src/main/java/com/steevsapps/idledaddy/steam/model/TimeQuery.java#L5
public class TimeQuery {
    @SerializedName("response")
    private TimeResponse response;

    public TimeResponse getResponse() {
        return response;
    }
}