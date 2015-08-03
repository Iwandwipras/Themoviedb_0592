package com.cooervo.filmography.GUI;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.cooervo.filmography.GUI.alertdialogs.AlertDialogFragment;
import com.cooervo.filmography.GUI.alertdialogs.NoInternetConnectionDialog;
import com.cooervo.filmography.R;
import com.cooervo.filmography.models.Actor;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    @Bind(R.id.nameEditText)
    EditText nameLabel;
    @Bind(R.id.lastnameEditText)
    EditText lastNameLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.search_button)
    public void searchIconClick() {

        String name = nameLabel.getText().toString();
        String lastName = lastNameLabel.getText().toString();

        if (name.length() > 2 && lastName.length() > 2) {
            String toSearch = name + "+" + lastName;
            getQuery(toSearch);

            nameLabel.setText("");
            lastNameLabel.setText("");

        } else {
            Toast.makeText(this, "Invalid name or last name", Toast.LENGTH_SHORT).show();
        }
    }

    private void getQuery(final String query) {
        String apiKey = "deea9711e0770caae3fc592b028bb17e";
        String searchActorByNameUrl = "http://api.themoviedb.org/3/search/person?api_key=" + apiKey + "&query=" + query;

        if (isNetworkAvailable()) {

            //OkHTTP Networking lib async call to theMovieDB API
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(searchActorByNameUrl)
                    .build();

            Call call = client.newCall(request);

            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    alertUserAboutError();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    getActorQuery(response, query);
                }

            });

        } else {
            NoInternetConnectionDialog dialog = new NoInternetConnectionDialog();
            dialog.show(getFragmentManager(), "no_internet_error_dialog");

        }

    }

    private void getActorQuery(Response response, final String query) {
        try {

            final String jsonData = response.body().string();
            Log.v(TAG, "REST response body in JSON: " + jsonData);

            if (response.isSuccessful()) {

                final Actor actor = getActorFrom(jsonData);

                MainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (actor == null) {
                            notFoundToast(query);
                        } else {
                            goToFilmographyActivity(actor);
                        }
                    }
                });

            } else {
                alertUserAboutError();
            }

        } catch (IOException e) {

            Log.d(TAG, "IOexception caught: ", e);

        } catch (JSONException e) {
            Log.d(TAG, "JSONexception caught: ", e);

        }
    }

    private void goToFilmographyActivity(Actor actor) {
        Intent i = new Intent(this, ActorFilmographyActivity.class);
        i.putExtra("actor", actor);

        startActivity(i);
    }

    private void notFoundToast(String query) {
        String displayQuery = query.replaceAll("\\+", " ");
        Toast.makeText(MainActivity.this, "Sorry we didn't found anything for " + displayQuery + " please try again", Toast.LENGTH_SHORT).show();
    }


    private Actor getActorFrom(String jsonData) throws JSONException {

        JSONObject results = new JSONObject(jsonData);
        JSONArray data = results.getJSONArray("results");

        if (data.length() == 0) {
            return null;
        }


        JSONObject jsonActor = data.getJSONObject(0);
        Actor actor = new Actor();
        actor.setId(jsonActor.getInt("id"));
        actor.setName(jsonActor.getString("name"));
        actor.setProfilePicturePath(jsonActor.getString("profile_path"));


        return actor;
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;

        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }

        return isAvailable;
    }

    private void alertUserAboutError() {
        DialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }

}
