package com.example.ade.slametinpolisiapp1;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ade.slametinpolisiapp1.Common.Common;
import com.example.ade.slametinpolisiapp1.Model.FCMResponse;
import com.example.ade.slametinpolisiapp1.Model.Notification;
import com.example.ade.slametinpolisiapp1.Model.Sender;
import com.example.ade.slametinpolisiapp1.Model.Token;
import com.example.ade.slametinpolisiapp1.Remote.IFCMService;
import com.example.ade.slametinpolisiapp1.Remote.IGoogleAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustommerCall extends AppCompatActivity {

    TextView txtTime, txtDistance, txtAddress;

    Button btnCancel,btnAccept;

    MediaPlayer mediaPlayer;

    IGoogleAPI mService;
    IFCMService mFCMService;

    String customerId;

    double lat,lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custommer_call);

        mService = Common.getGoogleAPI();
        mFCMService = Common.getFCMService();

        txtTime = (TextView) findViewById(R.id.txtTime);
        txtDistance = (TextView) findViewById(R.id.txtDistance);
        txtAddress = (TextView) findViewById(R.id.txtAddress);

        btnAccept = (Button)findViewById(R.id.btnAccept);
        btnCancel = (Button)findViewById(R.id.btnDecline);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(customerId))
                    cancelBooking(customerId);
            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustommerCall.this,DriverTracking.class);
                intent.putExtra("lat",lat);
                intent.putExtra("lng",lng);

                startActivity(intent);
                finish();
            }
        });


        mediaPlayer = MediaPlayer.create(this, R.raw.ringtone);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        if (getIntent() != null) {
            lat = getIntent().getDoubleExtra("lat", -1.0);
            lng = getIntent().getDoubleExtra("lng", -1.0);
            customerId = getIntent().getStringExtra("customer");

            getDirection(lat,lng);
        }
    }

    private void cancelBooking(String customerId) {
        Token token = new Token(customerId);

        Notification notification = new Notification("Notice!","Mohon Maaf Pihak Kepolisian Sedang Sibuk :)");
        Sender sender = new Sender(token.getToken(),notification);

        mFCMService.sendMessage(sender)
                .enqueue(new Callback<FCMResponse>() {
                    @Override
                    public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                        if(response.body().success == 1)
                        {
                            Toast.makeText(CustommerCall.this,"Permintaan Ditolak",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<FCMResponse> call, Throwable t) {

                    }
                });

    }

    private void getDirection(double lat,double lng) {

        String requestApi = null;
        try {

            requestApi = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "mode=driving&" +
                    "transit_routing_preference=less_driving&" +
                    "origin=" + Common.mLastLocation.getLatitude() + "," + Common.mLastLocation.getLongitude() + "&" +
                    "destination="+lat+","+lng+"&"+
                    "key=" + getResources().getString(R.string.google_direction_api);
            Log.d("Slametin", requestApi);
            mService.getPath(requestApi)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                JSONArray routes = jsonObject.getJSONArray("routes");

                                JSONObject object = routes.getJSONObject(0);

                                JSONArray legs = object.getJSONArray("legs");

                                JSONObject legsObject = legs.getJSONObject(0);

                                //get jarak
                                JSONObject distance = legsObject.getJSONObject("distance");
                                txtDistance.setText(distance.getString("text"));

                                //get Waktu Tempuh
                                JSONObject time = legsObject.getJSONObject("duration");
                                txtTime.setText(time.getString("text"));

                                //get Alamat
                                String address = legsObject.getString("end_address");
                                txtAddress.setText(address);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(CustommerCall.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        mediaPlayer.release();
        super.onStop();
    }

    @Override
    protected void onPause() {
        mediaPlayer.release();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer.start();
    }
}
