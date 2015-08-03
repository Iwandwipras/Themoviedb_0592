package com.cooervo.filmography.GUI.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cooervo.filmography.GUI.transformation.RoundedTransformation;
import com.cooervo.filmography.R;
import com.cooervo.filmography.models.Film;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Create the basic adapter extending from RecyclerView.Adapter
 * note that we specify the custom ViewHolder which gives us access to our
 */
public class FilmographyAdapter extends RecyclerView.Adapter<FilmographyAdapter.ViewHolder> {

    private List<Film> films;
    private Context context;

    public FilmographyAdapter(Context ctx, List<Film> filmsArray) {
        context = ctx;
        films = filmsArray;
    }

    /**
     * Provides a direct reference to each of the views within a data item
     * Used to cache the views within the item layout for fast access
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView filmTitle;
        public ImageView posterImage;
        public TextView dateValue;


        public ViewHolder(View itemView) {
            super(itemView);
            filmTitle = (TextView) itemView.findViewById(R.id.filmTitleLabel);
            posterImage = (ImageView) itemView.findViewById(R.id.filmPosterImage);
            dateValue = (TextView) itemView.findViewById(R.id.dateValue);
        }
    }

    //This method inflates a layout from XML and returning the holder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Inflate the custom layout
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.filmography_list_item,
                        parent,
                        false);

        return new ViewHolder(view);
    }

    // Populates data into the item through holder
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Film film = films.get(position);

        holder.filmTitle.setText(film.getTitle());
        holder.dateValue.setText(film.getDate() + "");
        Picasso.with(context)
                .load("https://image.tmdb.org/t/p/w185" + film.getPosterPath())
                .transform(new RoundedTransformation(20, 5))
                .error(R.drawable.noprofile)
                .into(holder.posterImage);
    }

    @Override
    public int getItemCount() {
        if (films == null) {
            return 0;
        }
        return films.size();
    }

}
