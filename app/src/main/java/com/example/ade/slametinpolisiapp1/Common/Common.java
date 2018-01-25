package com.example.ade.slametinpolisiapp1.Common;

import android.location.Location;

import com.example.ade.slametinpolisiapp1.Remote.FCMClient;
import com.example.ade.slametinpolisiapp1.Remote.IFCMService;
import com.example.ade.slametinpolisiapp1.Remote.IGoogleAPI;
import com.example.ade.slametinpolisiapp1.Remote.RetrofitClient;

/**
 * Created by Ade on 21/01/2018.
 */

public class Common {


    public static final String driver_tbl = "Drivers";
    public static final String user_driver_tbl = "Users";
    public static final String user_rider_tbl = "Riders";
    public static final String pickup_request_tbl = "PickupRequest";
    public static final String token_tbl = "Tokens";

   public static Location mLastLocation = null;

    public static final String baseURL = "https://maps.googleapis.com";
    public static final String fcmURL = "https://fcm.googleapis.com/";

    public static IGoogleAPI getGoogleAPI()
    {
        return RetrofitClient.getClient(baseURL).create(IGoogleAPI.class);
    }

    public static IFCMService getFCMService()
    {
        return FCMClient.getClient(fcmURL).create(IFCMService.class);
    }
}
