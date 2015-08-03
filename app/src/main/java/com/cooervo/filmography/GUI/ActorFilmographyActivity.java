package com.cooervo.filmography.GUI;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Typeface;
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
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Child activity of MainActivity which uses a RecyclerView to
 * display as a list and sorted by date the complete
 * filmography by actor/actress.
 */
public class ActorFilmographyActivity extends AppCompatActivity {

    public static final String TAG = ActorFilmographyActivity.class.getSimpleName();

    //The views binded with library Butterknife
    @Bind(R.id.actorNameLabel)
    TextView actorLabel;
    @Bind(R.id.actorProfileImage)
    ImageView profileImage;
    @Bind(R.id.recyclerView)
    RecyclerView filmsRecyclerView;

    //The actor we received from MainActivity will be used in this activity to
    //get the actors full philmography
    private Actor actor;
    //List to load the films by actor
    private List<Film> filmography;
    private FilmographyAdapter adapter;


    private static final String API_KEY = "deea9711e0770caae3fc592b028bb17e";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actor_filmography);

        ButterKnife.bind(this);

        Intent intent = getIntent();

        if (intent != null) {

            initializeAndSetActorName(intent);
            loadActorProfilePicture();
            getAllFilmographyForActor();

        }
        setTypeFaces();
    }

    private void getAllFilmographyForActor() {
        try {
            getFilmsForActor();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    private void initializeAndSetActorName(Intent intent) {
        actor = (Actor) intent.getSerializableExtra("actor");
        actorLabel.setText(actor.getName().toString());
    }

    /**
     * Method that uses OkHttp library to retrieve all the filmography
     * for current actor
     *
     * @throws IOException
     */
    private void getFilmsForActor() throws IOException {

        String url = "https://api.themoviedb.org/3/person/" + actor.getId() + "?api_key=" + API_KEY + "&append_to_response=credits";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                alertUserAboutError();
            }

            @Override
            public void onResponse(Response response) throws IOException {

                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    try {
                        filmography = parseFilmsFrom(jsonData); //Parse the film from the JSON data in response
                        Collections.sort(filmography, new FilmComparator()); //Sort the filmography based on Date field


                        ActorFilmographyActivity.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                populateRecyclerView();
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }

            /**
             * This method is in charge of populating the Recycler View with the data
             * from filmography arraylist
             */
            private void populateRecyclerView() {
                if (filmography.size() > 0) {

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

            private List<Film> parseFilmsFrom(String jsonData) throws JSONException {

                List<Film> tempFilmList = new ArrayList<>();

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
                        film.setRole(jsonFilm.getString("character"));
                        film.setFormattedDate(dateString);

                        tempFilmList.add(film);
                    }
                }
                return tempFilmList;

            }
        });


    }

    private void alertUserAboutError() {
        DialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }

    /**
     * We use picasso library to make easier to retrieve images from a URL
     * and apply a default image if there's no image for the Actor/Actoress
     */
    private void loadActorProfilePicture() {
        Picasso.with(this)
                .load("https://image.tmdb.org/t/p/w185" + actor.getPicPath())
                .transform(new RoundedTransformation(20, 5))
                .error(R.drawable.noprofile)
                .into(profileImage);
    }

    private void setTypeFaces() {
        Typeface latoBlack = Typeface.createFromAsset(getAssets(), "fonts/Lato-Black.ttf");
        actorLabel.setTypeface(latoBlack);
    }
}

