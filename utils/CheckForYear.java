package utils;

import java.util.List;
/**
 * The class contains methods that parse String to Int.
 * Gets the input year given as String and casts it to Int
 */
public final class CheckForYear {
  /**
   * casts string year into int
   * @param filterYear, list of years given as filters
   * @return year
   */
  public int checkYear(final List<String> filterYear) {
    int year = 0;
    for (var temp : filterYear) {
      if (temp != null) {
        year = Integer.parseInt(temp);
      }
    }
    return year;
  }
}
