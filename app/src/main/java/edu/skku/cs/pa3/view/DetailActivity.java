package edu.skku.cs.pa3.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import edu.skku.cs.pa3.R;
import edu.skku.cs.pa3.StepListAdapter;
import edu.skku.cs.pa3.model.AddLike;
import edu.skku.cs.pa3.model.Steps;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DetailActivity extends AppCompatActivity {
    private StepListAdapter listAdapter;
    private ListView listView;
    private Button likeButton;
    private String email, title, image;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        id = intent.getIntExtra("id", 0);
        title = intent.getStringExtra("title");
        image = intent.getStringExtra("image");
        Log.i("test", "id: " + id + "email: " + email);

        listView = findViewById(R.id.listView);
        listAdapter = new StepListAdapter();
        listView.setAdapter(listAdapter);

        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder builder = HttpUrl.parse("https://api.spoonacular.com/recipes/" + id + "/analyzedInstructions").newBuilder();
        builder.addQueryParameter("apiKey", "644a503313de4685ab88325146e8b5e9");
        String url = builder.build().toString();

        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String res = response.body().string();
                Gson gson = new GsonBuilder().create();
                final Steps[] stepsList = gson.fromJson(res, Steps[].class);
                Log.i("TEST", stepsList[0].getSteps()[0].getStep());

                DetailActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listAdapter.setItems(stepsList[0].getSteps());
                        Log.i("TEST", listAdapter.getItem(3).getStep());
                        listAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

        likeButton = findViewById(R.id.likeButton);
        likeButton.setOnClickListener(view -> {
            OkHttpClient client2 = new OkHttpClient();

            AddLike data = new AddLike();
            data.setEmail(email);
            data.setNewLike(id + "#" + title + "#" + image);

            Gson gson = new Gson();
            String json = gson.toJson(data, AddLike.class);

            HttpUrl.Builder builder2 = HttpUrl.parse("https://8mjpn0w8ob.execute-api.ap-northeast-2.amazonaws.com/map/addlike").newBuilder();
            String url2 = builder2.build().toString();

            Request request2 = new Request.Builder().url(url2).post(RequestBody.create(json, MediaType.parse("application/json"))).build();

            client2.newCall(request2).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                }
            });

        });

    }
}