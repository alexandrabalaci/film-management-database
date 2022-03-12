package utils;

import java.util.List;
/**
 * The class contains methods that search for occurrences of list elements in another list.
 *
 */
public final class SearchList {
  /**
   * Gets the word count
   * @param search, list of elements to search for
   * @param searchInto, list to search into
   * @return count
   */
  public int countWords(final List<String> search, final List<String> searchInto) {
    int counter = 0;
    for (var word : search) {
      if (searchInto.contains(word)) {
        counter++;
      }
    }
    return counter;
  }
}
