package entertainment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * The class contains methods that instantiate ShowRating Object.
 *
 */
public final class ShowRating {
  private Map<Integer, Double> ratings = new HashMap<>();
  private List<Double> allRatings = new ArrayList<>();
  private int duration = 0;

  public ShowRating() { }
  /**
   * checks if season of show was rated
   * @param seasonNumber of the Show
   * @return true if rated
   */
  public boolean wasRated(final int seasonNumber) {
    return ratings.containsKey(seasonNumber);
  }
  /**
   * Adds rating to show
   * @param seasonNumber, number of the season to add rating to
   * @param grade to be given to Movie
   */
  public void addRating(final int seasonNumber, final double grade) {
    ratings.put(seasonNumber, grade);
  }
  /**
   * Gets total duration of show
   * @param seasons, list of seasons of Show
   * @return duration
   */
  public int getDuration(final ArrayList<Season> seasons) {
    for (var season : seasons) {
      duration = duration + season.getDuration();
    }
    return duration;
  }
  /**
   * Gets the average rating of show
   * @param title of Show
   * @return averageRatinng
   */
  public double getRating(final String title) {
    double averageRating = 0.0;
    double sum = 0.0;
    if (ratings.size() > 0) {
      for (Integer key : ratings.keySet()) {
        sum = sum + ratings.get(key);
        averageRating = sum / ratings.size();
      }
    }
    allRatings.add(averageRating);
    return averageRating;
  }
  /**
   * Gets the final rating of show
   * @param title of Show
   * @return medie
   */
  public double getFinalRating(final String title) {
    double medie = 0.0;
    double suma = 0.0;
    if (allRatings.size() > 0) {
      for (var grade : allRatings) {
        suma = suma + grade;
        medie = medie / allRatings.size();
      }
    }
    return medie;
  }
}
