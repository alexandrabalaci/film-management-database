package utils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import static java.util.stream.Collectors.toMap;
/**
 * The class contains methods that sort maps.
 *
 */
public class SortMap {
  /**
   * sorts map by given order
   * @param map to sort
   * @param order, sort type
   * @return sorted map
   */
  public static Map<String, Integer> sortByValue(
      final Map<String, Integer> map, final String order) {
    Map<String, Integer> sorted = map;
    switch (order) {
      case "desc":
        sorted =
            map.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(
                    toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e2,
                        LinkedHashMap::new));
        break;
      case "asc":
        sorted =
            map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(
                    toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));

        break;
      default:
    }
    return sorted;
  }
  /**
   * sorts map by given order
   * @param map to sort
   * @param order, sort type
   * @return sorted map
   */
  public static Map<String, Double> sortByValueDouble(
      final Map<String, Double> map, final String order) {
    Map<String, Double> sorted = map;
    switch (order) {
      case "asc":
        sorted =
            map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .sorted((Map.Entry.comparingByKey()))
                .collect(
                    toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e2,
                        LinkedHashMap::new));
        break;

      case "desc":
        sorted =
            map.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .sorted((Map.Entry.comparingByKey()))
                .collect(
                    toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e2,
                        LinkedHashMap::new));
        break;
      default:
    }
    return sorted;
  }
}
