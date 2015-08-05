package com.cooervo.filmography.controllers.http;

/**
 * Singleton class to store the MovieDbUrl for theMovieDB API
 */
public class MovieDbUrl {

    private volatile static MovieDbUrl uniqueInstance;

    private final String url = "http://api.themoviedb.org/3/";
    private final String API_KEY = "deea9711e0770caae3fc592b028bb17e";
    private String actorQuery;
    private String filmographyQuery;


    private MovieDbUrl() {
    }

    public static MovieDbUrl getInstance() {
        if (uniqueInstance == null) {
            synchronized (MovieDbUrl.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new MovieDbUrl();
                }
            }
        }
        return uniqueInstance;
    }

    public String getActorQuery(String nameToSearch){
        return url + "search/person?api_key=" + API_KEY + "&query=" + nameToSearch + "&sort_by=popularity";
    }

    public String getFilmographyQuery(int actorId){
        return url + "person/" + actorId + "?api_key=" + API_KEY + "&append_to_response=credits";
    }

}
