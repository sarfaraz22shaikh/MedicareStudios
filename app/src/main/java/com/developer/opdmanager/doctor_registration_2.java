package com.developer.opdmanager;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import cz.msebera.android.httpclient.Header;

public class doctor_registration_2 extends AppCompatActivity {
    private AutoCompleteTextView locationAutoComplete;
    private static final String GEOAPIFY_API_KEY = "fcf00d17afd34484b42fb22d582ad728";
    Button register;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_registration_2);
        register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(doctor_registration_2.this, dashboard.class);
                startActivity(intent);
            }
        });
        TextView specialization = findViewById(R.id.specialization);
        Drawable drawable = getResources().getDrawable(R.drawable.care);
        drawable.setBounds(0, 0, 70, 70);  // width and height in pixels
        specialization.setCompoundDrawables(drawable, null, null, null);
        TextView professional_exp = findViewById(R.id.professional_exp);
        Drawable drawable2 = getResources().getDrawable(R.drawable.consumerresearch);
        drawable2.setBounds(0, 0, 60, 60);  // width and height in pixels
        professional_exp.setCompoundDrawables(drawable2, null, null, null);
        locationAutoComplete = findViewById(R.id.locationAutoComplete);
        Drawable drawable3 = getResources().getDrawable(R.drawable.pinmap);
        drawable3.setBounds(0, 0, 60, 60);  // width and height in pixels
        locationAutoComplete.setCompoundDrawables(drawable3, null, null, null);

        locationAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 2) {  // Start fetching suggestions after the user types at least 3 characters
                    fetchLocationSuggestions(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // Fetch location suggestions using Geoapify Places API
    private void fetchLocationSuggestions(String query) {
        String url = "https://api.geoapify.com/v1/geocode/autocomplete?text=" + query + "&apiKey=" + GEOAPIFY_API_KEY;

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray features = response.getJSONArray("features");
                    List<String> suggestions = new ArrayList<>();

                    // Loop through the results and add the location names to the suggestions list
                    for (int i = 0; i < features.length(); i++) {
                        JSONObject feature = features.getJSONObject(i);
                        JSONObject properties = feature.getJSONObject("properties");
                        String name = properties.getString("formatted");
                        suggestions.add(name);
                    }

                    // Set the suggestions to the AutoCompleteTextView
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(doctor_registration_2.this, android.R.layout.simple_dropdown_item_1line, suggestions);
                    locationAutoComplete.setAdapter(adapter);
                    locationAutoComplete.showDropDown();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("Geoapify", "Failed to fetch location suggestions", throwable);
            }
        });
    }
}
