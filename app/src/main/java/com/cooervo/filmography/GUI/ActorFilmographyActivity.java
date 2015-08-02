package com.cooervo.filmography.GUI;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cooervo.filmography.GUI.adapters.FilmographyAdapter;
import com.cooervo.filmography.GUI.alertdialogs.AlertDialogFragment;
import com.cooervo.filmography.GUI.transformation.RoundedTransformation;
import com.cooervo.filmography.R;
import com.cooervo.filmography.models.Actor;
import com.cooervo.filmography.models.Film;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ActorFilmographyActivity extends AppCompatActivity {

    public static final String TAG = ActorFilmographyActivity.class.getSimpleName();

    @Bind(R.id.actorNameLabel)
    TextView actorLabel;

    @Bind(R.id.actorProfileImage)
    ImageView profileImage;

    @Bind(R.id.recyclerView)
    RecyclerView filmsRecyclerView;

    private Film[] filmography;
    private FilmographyAdapter adapter;

    private Actor actor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actor_filmography);

        ButterKnife.bind(this);

        Intent intent = getIntent();

        if (intent != null) {
            actor = (Actor) intent.getSerializableExtra("actor");
            actorLabel.setText(actor.getName().toString());
            loadActorProfilePicture();
            getFilmographyBasedOn(actor.getId());
        }
    }

    private void getFilmographyBasedOn(int actorID) {

        String apiKey = "deea9711e0770caae3fc592b028bb17e";
        String getFilmographyByActorIdUrl = "http://api.themoviedb.org/3/discover/movie?with_cast=" + actorID + "&sort_by=release_date.asc&api_key=" + apiKey;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(getFilmographyByActorIdUrl)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                alertUserAboutError();
            }

            @Override
            public void onResponse(Response response) throws IOException {

                getFilmographyQuery(response);
            }

        });
    }

    private void getFilmographyQuery(Response response) {
        try {

            final String jsonData = response.body().string();
            Log.v(TAG, "REST response body in JSON: " + jsonData);

            if (response.isSuccessful()) {

                filmography = getFilmographyFrom(jsonData);

                ActorFilmographyActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        if (filmography.length > 0) {
                            adapter = new FilmographyAdapter(ActorFilmographyActivity.this, filmography);
                            filmsRecyclerView.setAdapter(adapter);

                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ActorFilmographyActivity.this);

                            filmsRecyclerView.setLayoutManager(layoutManager);

                            filmsRecyclerView.setHasFixedSize(true);

                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ActorFilmographyActivity.this, "Sorry we didn't find any films related to " + actor.getName(), Toast.LENGTH_LONG).show();

                                }
                            });
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

    private synchronized Film[] getFilmographyFrom(String jsonData) throws JSONException {

        JSONObject results = new JSONObject(jsonData);
        JSONArray data = results.getJSONArray("results");

        Film[] filmography = new Film[data.length()];

        for (int i = 0; i < data.length(); i++) {
            JSONObject jsonFilm = data.getJSONObject(i);
            Film film = new Film();

            film.setTitle(jsonFilm.getString("title"));
            film.setPosterPath(jsonFilm.getString("poster_path"));
            film.setRating(jsonFilm.getDouble("vote_average"));
            film.setVotesAmount(jsonFilm.getInt("vote_count"));
            film.setDate(jsonFilm.getString("release_date"));

            filmography[i] = film;
        }
        return filmography;
    }

    private void alertUserAboutError() {
        DialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }

    private void loadActorProfilePicture() {
        Picasso.with(this)
                .load("https://image.tmdb.org/t/p/w185" + actor.getPicPath())
                .transform(new RoundedTransformation(20, 5))
                .error(R.drawable.noprofile)
                .into(profileImage);
    }


}
