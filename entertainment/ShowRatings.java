package entertainment;

import java.util.HashMap;
import java.util.Map;
/**
 * The class contains information and methods concerning Show ratings
 *
 */
public final class ShowRatings {
  private Map<String, ShowRating> showRatings = new HashMap<>();
  /**
   * checks if season of show was rated
   * @param title of Show
   * @param seasonNumber of Show
   * @return true if rated
   */
  public boolean wasRated(final String title, final int seasonNumber) {
    if (!showRatings.containsKey(title)) {
      return false;
    } else {
      ShowRating ratingList = showRatings.get(title);
      return ratingList.wasRated(seasonNumber);
    }
  }
  /**
   * adds rating to show
   * @param title of Show to add rating to
   * @param seasonNumber, number of season to add rating to
   * @param grade, grade to add as rating
   */
  public void addRating(final String title, final int seasonNumber, final double grade) {
    ShowRating ratingList;
    if (!showRatings.containsKey(title)) {
      ratingList = new ShowRating();

    } else {
      ratingList = showRatings.get(title);
      showRatings.remove(title);
      ratingList.addRating(seasonNumber, grade);
      showRatings.put(title, ratingList);
    }
  }
  /**
   * Gets the list of ratings of show
   * @param title of Show to get rating for
   * @return list of ratings
   */
  public double getRatingOfShow(final String title) {
    ShowRating ratingList = showRatings.get(title);
    if (null != ratingList) {
      return ratingList.getRating(title);
    }
    return 0;
  }

  public Map<String, ShowRating> getRatings() {
    return this.showRatings;
  }
}
