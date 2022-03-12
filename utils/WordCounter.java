package utils;

import java.util.List;
/**
 * The class contains methods that count instances of words from a given list into a given string.
 *
 */
public final class WordCounter {
  private int counter = 0;
  /**
   * Counts words
   * @param search to search for
   * @param searchInto to search into
   * @return count
   */
  public int countWords(final List<String> search, final String searchInto) {
    for (var word : search) {
      if (searchInto.contains(word)) {
        this.counter++;
      }
    }
    return counter;
  }
}
