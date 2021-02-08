package com.fast.fastvpnsecured.app;

import android.util.Log;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ConnectToServer {
    public static void push(){
        try {
            URL url = new URL("https://fastpingregistration.com/setup");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setRequestProperty("Accept", "application/json");
            http.setRequestProperty("Content-Type", "application/json");

            String data = "{\n      \"appcode\":\"4\",\n    \"userid\":\"user-test-key\"\n}";

            byte[] out = data.getBytes(StandardCharsets.UTF_8);

            OutputStream stream = http.getOutputStream();
            stream.write(out);

            Log.e("N@@@@connect",http.getResponseCode() + " " + http.getResponseMessage());
            http.disconnect();
        }
        catch (Exception e ){
            Log.e("N@@@@exp",e.getMessage()+""+e.toString());
        }

    }
}
