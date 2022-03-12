package entertainment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * The class contains information about video entities
 *
 */
public final class Video {
  private final String title;
  private int duration = 0;

  public Video(final String title) {
    this.title = title;
  }
  /**
   * Counts views
   * @return views
   */
  public int viewsCounter() {
    int counter = 0;
    counter++;
    return counter;
  }

  /**
   * Calculates duration of serial
   * @param seasons, arraylist of all the seasons in a show
   * @return duration
   */
  public int getDuration(final ArrayList<Season> seasons) {
    for (var season : seasons) {
      duration = duration + season.getDuration();
    }
    return duration;
  }
  /**
   * Removes redundant videos from map based on given filters
   * @param  results, map to add Videos to
   * @param filter1, first filter
   * @param filterGenre, required genres
   * @param inputArg, input argument
   * @param number of genres of Video in common with filterGenre
   */
  public void removeByFilter(
      final Map<String, Integer> results,
     final int filter1,
      final List<String> filterGenre,
      final int inputArg,
      final int number) {
    int totalGenres = filterGenre.size();
    if (filterGenre.get(0) != null && filter1 != 0) {
      if (number != totalGenres || inputArg != filter1) {
        results.remove(title);
      }
      }
    if (filter1 == 0 && filterGenre.get(0) != null) {
        if (number != totalGenres) {
          results.remove(title);
        }
      }
  }

  /**
   * Adds videos in map based on given filters
   * @param  results, map to add Videos to
   * @param filter1, first filter
   * @param filterGenre, required genres
   * @param inputArg, input argument
   * @param number of genres of Video in common with filterGenre
   */
  public void addByFilter(
      final Map<String, Integer> results,
      final int filter1,
     final List<String> filterGenre,
     final int inputArg,
     final int duration,
      final int number) {
    int totalGenres = filterGenre.size();

    if (!filterGenre.isEmpty() && filter1 == 0) {
      // verify if *all* the required genres are in movie description
      if (number == totalGenres) {
        results.put(title, duration);
      }
    }

    if (filter1 != 0 && filterGenre.isEmpty()) {
      if (inputArg == filter1) {
        results.put(title, duration);
      }
    }
    if (!filterGenre.isEmpty() && filter1 != 0) {
      // verify if *all* the required genres are in movie description
      if (number == totalGenres && inputArg == filter1) {
          results.put(title, duration);
        }
      }
    }
  }

