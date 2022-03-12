package entertainment;

import java.util.ArrayList;
import java.util.List;
/**
 * The class contains methods that instantiate MovieRating Object.
 *
 */
public final class MovieRating {

  private List<Double> ratings = new ArrayList<>();

  public MovieRating() { }
  /**
   * checks if movie was rated
   * @param title of Movie
   * @return true if rated
   */
  public boolean wasRated(final int title) {
    return ratings.contains(title);
  }
  /**
   * Adds rating to movie
   * @param grade to be given to Movie
   */
  public void addRating(final double grade) {
    ratings.add(grade);
  }
  /**
   * Gets the average rating of movie
   * @param title of Movie
   * @return averageRating
   */
  public double getRating(final String title) {
    double averageRating = 0.0;
    double sum = 0.0;
    if (ratings.size() > 0) {
      for (var grade : ratings) {
        sum = sum + grade;
        averageRating = sum / ratings.size();
      }
    }
    return averageRating;
  }
}
