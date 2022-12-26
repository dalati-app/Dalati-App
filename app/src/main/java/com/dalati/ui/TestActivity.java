package com.dalati.ui;

import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dalati.R;
import com.dalati.ui.models.Category;
import com.dalati.ui.models.Report;
import com.dalati.ui.models.Type;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TestActivity extends AppCompatActivity {
    List<Type> typeList;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {

                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .build();
                    MediaType mediaType = MediaType.parse("application/json");
                    RequestBody body = RequestBody.create(mediaType, "{\r\n    \"message\": \"Hu xdgdf  \\n OTP CODE 14477\",\r\n    \"to\": \"+966578202307\",\r\n    \"bypass_optout\": true,\r\n    \"sender_id\": \"SMSto\",\r\n    \"callback_url\": \"https://example.com/callback/handler\"\r\n}");
                    Request request = new Request.Builder()
                            .url("https://api.sms.to/sms/send")
                            .method("POST", body)
                            .addHeader("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczovL2F1dGg6ODA4MC9hcGkvdjEvdXNlcnMvYXBpL2tleS9nZW5lcmF0ZSIsImlhdCI6MTY3MTgzMzE3MywibmJmIjoxNjcxODMzMTczLCJqdGkiOiJXMWpTckplSDZWRkR0Y2FlIiwic3ViIjo0MDY0MTIsInBydiI6IjIzYmQ1Yzg5NDlmNjAwYWRiMzllNzAxYzQwMDg3MmRiN2E1OTc2ZjcifQ.EepuGxhwptHWNw_SShmoIu144Oo12b2OFXNLjXwnUZ8")
                            .addHeader("Content-Type", "application/json")
                            .build();
                    try {
                        Response response = client.newCall(request).execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();


        databaseReference = FirebaseDatabase.getInstance().getReference();



    }
}