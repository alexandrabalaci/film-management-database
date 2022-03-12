package entertainment;

import java.util.HashMap;
import java.util.Map;
/**
 * The class contains information and methods concerning Movie ratings
 *
 */
public final class MovieRatings {
  private final Map<String, MovieRating> movieRatings = new HashMap<>();
  /**
   * checks if movie was rated
   * @param title of Movie
   * @return true if rated
   */
  public boolean wasRated(final String title) {
    if (!movieRatings.containsKey(title)) {
      return false;
    }
    return true;
  }
  /**
   * adds rating to movie
   * @param title of Movie
   * @param grade to be given to Movie
   */
  public void addRating(final String title, final double grade) {
    if (!movieRatings.containsKey(title)) {
      MovieRating ratingList = new MovieRating();
      ratingList.addRating(grade);
      movieRatings.put(title, ratingList);

    } else {
      MovieRating ratingList = movieRatings.get(title);
      ratingList.addRating(grade);
      movieRatings.put(title, ratingList);
    }
  }
  /**
   * Gets the list of ratings of movie
   * @param title of Movie
   * @return list of ratings
   */
  public double getRatingOfMovie(final String title) {
    MovieRating ratingList = movieRatings.get(title);
    if (null != ratingList) {
      return ratingList.getRating(title);
    }
    return 0;
  }
}
