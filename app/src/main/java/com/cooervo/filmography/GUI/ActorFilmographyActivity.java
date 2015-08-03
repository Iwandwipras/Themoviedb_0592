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
import com.cooervo.filmography.models.FilmComparator;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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

    private List<Film> filmography;
    private int arrayIndex;
    private FilmographyAdapter adapter;

    private Actor actor;

    private static final String API_KEY = "deea9711e0770caae3fc592b028bb17e";
    private int totalPages;
    OkHttpClient client = new OkHttpClient();
    private List<String> JSONs = new ArrayList<>();


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

            try {
                getFilmsForActor();

            } catch (IOException e) {

                e.printStackTrace();
            }

        }
    }

    private void getFilmsForActor() throws IOException {

        String url = "https://api.themoviedb.org/3/person/" + actor.getId() + "?api_key=" + API_KEY + "&append_to_response=credits";

        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {

                if (response.isSuccessful()) {
                    String jsonData = response.body().string();

                    filmography = new ArrayList<>();

                    try {
                        JSONObject jsonResponse = new JSONObject(jsonData);
                        JSONObject credits = jsonResponse.getJSONObject("credits");
                        JSONArray cast = credits.getJSONArray("cast");

                        Log.v(TAG, "All filmography by actor " + cast);

                        for (int i = 0; i < cast.length(); i++) {

                            JSONObject jsonFilm = cast.getJSONObject(i);

                            Film film = new Film();

                            String dateString = jsonFilm.getString("release_date");

                            if (dateString != null && !dateString.equals("null")) {

                                film.setTitle(jsonFilm.getString("title"));
                                film.setPosterPath(jsonFilm.getString("poster_path"));
                                film.setFormattedDate(dateString);

                                filmography.add(film);
                            }
                        }

                        ActorFilmographyActivity.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                if (filmography.size() > 0) {

                                    Collections.sort(filmography, new FilmComparator());

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

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }
        });
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

 /*
    private void getFilmographySizeAndPaginationSize() {
        String filmographyByActorIdUrl = "http://api.themoviedb.org/3/discover/movie?with_cast="
                + actor.getId() + "&sort_by=release_date.asc&api_key=" + API_KEY + "&page=" + 1;

        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(filmographyByActorIdUrl)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                alertUserAboutError();
            }

            @Override
            public void onResponse(Response response) throws IOException {

                final String jsonData = response.body().string();
                Log.v(TAG, "REST raw response for page " + 1 + jsonData);

                try {
                    JSONObject results = new JSONObject(jsonData);
                    totalPages = results.getInt("total_pages");

                    Log.v(TAG, "total pages=" + totalPages);

                    filmography = new ArrayList<Film>();

                    getFullFilmography();

                    for (Film film : filmography) {
                        Log.v("printing", film.getTitle() + " " + film.getDate());

                    }

                    ActorFilmographyActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void getFilmsForActor() {
                            if (filmography.size() > 0) {
                                adapter = new FilmographyAdapter(ActorFilmographyActivity.this, filmography);
                                filmsRecyclerView.setAdapter(adapter);

                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ActorFilmographyActivity.this);

                                filmsRecyclerView.setLayoutManager(layoutManager);

                                filmsRecyclerView.setHasFixedSize(true);

                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void getFilmsForActor() {
                                        Toast.makeText(ActorFilmographyActivity.this, "Sorry we didn't find any films related to " + actor.getName(), Toast.LENGTH_LONG).show();

                                    }
                                });
                            }
                        }
                    });


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void getFullFilmography() {

        for (int i = 1; i <= totalPages; i++) {
            getResponseForPage(i);
        }
    }

    private void getResponseForPage(final int i) {
        final int pageNum = i;
        String filmographyByActorIdUrl = "http://api.themoviedb.org/3/discover/movie?with_cast="
                + actor.getId() + "&sort_by=release_date.asc&api_key=" + API_KEY + "&page=" + pageNum;


        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(filmographyByActorIdUrl)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                alertUserAboutError();
            }

            @Override
            public void onResponse(Response response) throws IOException {

                final String jsonData = response.body().string();
                Log.v(TAG, "ActorID= " + actor.getId() + " filmography for page " + pageNum + jsonData);

                try {
                    JSONObject results = new JSONObject(jsonData);
                    JSONArray data = results.getJSONArray("results");

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jsonFilm = data.getJSONObject(i);
                        Film film = new Film();

                        film.setTitle(jsonFilm.getString("title"));
                        film.setPosterPath(jsonFilm.getString("poster_path"));
                        film.setRating(jsonFilm.getDouble("vote_average"));
                        film.setVotesAmount(jsonFilm.getInt("vote_count"));
                        film.setDate(jsonFilm.getString("release_date"));

                        filmography.add(film);
                        Log.v("arraylist" , "adding film " + film.getTitle() + "" + arrayIndex++ + " arraylist size=" + filmography.size());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    */