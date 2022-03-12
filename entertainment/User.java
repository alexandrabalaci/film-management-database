package entertainment;

import java.util.ArrayList;
import java.util.Map;
/**
 * The class contains information about entity User
 *
 */
public final class User {
  private Map<String, Double> showRatings;
  public User(final String username) {
  }
  /**
   * Checks if user has seen title
   * @param history of user to check into
   * @param title of Video to check
   * @return truth value
   */
  public boolean hasSeen(final Map<String, Integer> history, final String title) {
    return history.containsKey(title);
  }
  /**
   * Checks if title exists in favourite list
   * @param favourites of user to check into
   * @param title of Video
   * @return truth value
   */
  public boolean hasFavourite(final ArrayList<String> favourites, final String title) {
    for (String element : favourites) {
      if (element.contains(title)) {
        return true;
      }
    }
    return false;
  }
  /**
   * Checks if user has rated movie
   * @param ratings of user
   * @param title of Video
   * @return truth value
   */
  public boolean hasRated(final Map<String, Integer> ratings, final String title) {
    return ratings.containsKey(title);
  }

  public Map<String, Double> getShowRating() {
    return this.showRatings;
  }
}
