package main;

import actor.ActorsAwards;
import checker.Checker;
import checker.Checkstyle;
import common.Constants;
import utils.CheckForYear;
import utils.SearchList;
import utils.SortMap;
import utils.WordCounter;
import entertainment.MovieRatings;
import entertainment.Season;
import entertainment.ShowRatings;
import entertainment.Video;
import entertainment.User;
import fileio.UserInputData;
import fileio.SerialInputData;
import fileio.ActorInputData;
import fileio.ActionInputData;
import fileio.ShowInput;
import fileio.MovieInputData;
import fileio.InputLoader;
import fileio.Writer;
import fileio.Input;

import org.json.simple.JSONArray;

import java.io.File;

import java.io.IOException;

import java.nio.file.Files;

import java.nio.file.Path;

import java.nio.file.Paths;

import java.util.*;

/** The entry point to this homework. It runs the checker that tests your implentation. */
public final class Main {
  /** for coding style */
  private Main() {}

  /**
   * Call the main checker and the coding style checker
   *
   * @param args from command line
   * @throws IOException in case of exceptions to reading / writing
   */
  public static void main(final String[] args) throws IOException {
    File directory = new File(Constants.TESTS_PATH);
    Path path = Paths.get(Constants.RESULT_PATH);
    if (!Files.exists(path)) {
      Files.createDirectories(path);
    }

    File outputDirectory = new File(Constants.RESULT_PATH);

    Checker checker = new Checker();
    checker.deleteFiles(outputDirectory.listFiles());

    for (File file : Objects.requireNonNull(directory.listFiles())) {
      String filepath = Constants.OUT_PATH + file.getName();
      File out = new File(filepath);
      boolean isCreated = out.createNewFile();
      if (isCreated) {
        action(file.getAbsolutePath(), filepath);
      }
    }

    checker.iterateFiles(Constants.RESULT_PATH, Constants.REF_PATH, Constants.TESTS_PATH);
    Checkstyle test = new Checkstyle();
    test.testCheckstyle();
  }

  /**
   * @param filePath1 for input file
   * @param filePath2 for output file
   * @throws IOException in case of exceptions to reading / writing
   */
  public static void action(final String filePath1, final String filePath2) throws IOException {
    InputLoader inputLoader = new InputLoader(filePath1);
    Input input = inputLoader.readData();

    Writer fileWriter = new Writer(filePath2);
    JSONArray arrayResult = new JSONArray();

    // TODO add here the entry point to your implementation
    // created (and populate) lists used to access and store relevant data
    List<UserInputData> users = input.getUsers();
    MovieRatings movieRatings = new MovieRatings();
    ShowRatings showRatings = new ShowRatings();
    List<ActorInputData> actors = input.getActors();
    ArrayList<SerialInputData> shows = (ArrayList<SerialInputData>) input.getSerials();
    ArrayList<MovieInputData> movies = (ArrayList<MovieInputData>) input.getMovies();
    List<String> usersRatings = new ArrayList<>();
    List<String> totalFavourites = new ArrayList<>();

    for (ActionInputData action : input.getCommands()) {

      if (action.getActionType().equals("command")) {
        // parse users
        for (UserInputData temp : users) {
          String username = action.getUsername();
          if (action.getType().equals("favorite")) {
            if (username.equals(temp.getUsername())) {
              Map<String, Integer> history = temp.getHistory();
              List<String> favoriteMovies = temp.getFavoriteMovies();
              // check if user has already added title, and display error
              if (favoriteMovies.contains(action.getTitle())) {
                arrayResult.add(
                    fileWriter.writeFile(
                        action.getActionId(),
                        "",
                        "error -> " + action.getTitle() + " is already in favourite list"));
                break;
              }
              // check if user has seen title and then add to favourite if true
              if (history.containsKey(action.getTitle())) {
                totalFavourites.add(action.getTitle());
                favoriteMovies.add(action.getTitle());
                arrayResult.add(
                    fileWriter.writeFile(
                        action.getActionId(),
                        "",
                        "success -> " + action.getTitle() + " was added as favourite"));
                break;
              }
              // else display error
              arrayResult.add(
                  fileWriter.writeFile(
                      action.getActionId(), "", "error -> " + action.getTitle() + " is not seen"));
              break;
            }
          }

          if (action.getType().equals("view")) {
            if (username.equals(temp.getUsername())) {
              Map<String, Integer> history = temp.getHistory();
              String title = action.getTitle();
              // check if user has seen title, and if yes increment no. of views and update history
              if (history.containsKey(title)) {
                history.computeIfPresent(action.getTitle(), (k, v) -> v + 1);
              } else {
                // else add title to history
                history.put(title, 1);
              }
              int views = history.get(title);
              arrayResult.add(
                  fileWriter.writeFile(
                      action.getActionId(),
                      "",
                      "success -> " + title + " was viewed with total views of " + views));
              break;
            }
          }

          if (action.getType().equals("rating")) {
            if (username.equals(temp.getUsername())) {
              String title = action.getTitle();
              Map<String, Integer> history = temp.getHistory();
              // check if user has seen title, and if false display error
              if (!history.containsKey(title)) {
                arrayResult.add(
                    fileWriter.writeFile(
                        action.getActionId(), "", "error -> " + title + " is not seen"));
                break;
              }
              // if user has seen title, check if title has seasons
              if (history.containsKey(title)) {
                if (action.getSeasonNumber() == 0) {
                  // if false, add rating
                  usersRatings.add(temp.getUsername());
                  movieRatings.addRating(title, action.getGrade());
                  arrayResult.add(
                      fileWriter.writeFile(
                          action.getActionId(),
                          "",
                          "success -> "
                              + action.getTitle()
                              + " was rated with "
                              + action.getGrade()
                              + " by "
                              + action.getUsername()));
                  break;
                } else {
                  // if yes, check if season was already rated
                  usersRatings.add(temp.getUsername());
                  if (showRatings.wasRated(action.getTitle(), action.getSeasonNumber())) {
                    // if true, display error
                    arrayResult.add(
                        fileWriter.writeFile(
                            action.getActionId(),
                            "",
                            "error -> " + title + " has been already rated"));
                    break;
                  }
                  if (!showRatings.wasRated(title, action.getSeasonNumber())) {
                    // if false, rate season
                    showRatings.addRating(title, action.getSeasonNumber(), action.getGrade());
                    arrayResult.add(
                        fileWriter.writeFile(
                            action.getActionId(),
                            "",
                            "success -> "
                                + action.getTitle()
                                + " was rated with "
                                + action.getGrade()
                                + " by "
                                + action.getUsername()));
                    break;
                  }
                }
              }
            }
          }
        }
      }

      if (action.getActionType().equals("query")) {
        // get filters
        List<String> filterYear = action.getFilters().get(0);
        List<String> filterGenre = action.getFilters().get(1);
        List<String> queryResult = new ArrayList<>();
        int totalGenres = filterGenre.size();
        // get query number
        int numberQuery = action.getNumber();
        // check if year is not null
        CheckForYear check = new CheckForYear();
        int year = check.checkYear(filterYear);

        if (action.getObjectType().equals("actors")) {
          if (action.getCriteria().equals("awards")) {
            Map<String, Integer> mapOfAwards = new HashMap<>();
            List<String> awards = action.getFilters().get(3); // input filters
            int noAwardsTotal = awards.size();
            // parse actor list and count no. of awards for each one of them
            for (var actor : actors) {
              int noAwards = 0;
              Map<ActorsAwards, Integer> actorAwards = actor.getAwards();
              for (var award : awards) {
                //noinspection SuspiciousMethodCalls
                if (actorAwards.containsKey(award)) {
                  noAwards++;
                }
              }
              // if actor has at least the required awards, add to map
              if (noAwards == noAwardsTotal) {
                int actorNoAwards = actorAwards.values().stream().reduce(0, Integer::sum);
                mapOfAwards.put(actor.getName(), actorNoAwards);
              }
            }
            // sort map by number of awards then make list with sorted titles
            Map<String, Integer> sortedMap = SortMap.sortByValue(mapOfAwards, action.getSortType());
            List<String> finalList = new ArrayList<>(sortedMap.keySet());
            arrayResult.add(
                fileWriter.writeFile(action.getActionId(), "", "Query result: " + finalList));
          }

          if (action.getCriteria().equals("filter_description")) {
            // get filters
            List<List<String>> keywords = action.getFilters();
            int noWordsTotal = keywords.size();
            List<String> wordList = keywords.get(2);
            List<String> finalList = new ArrayList<>();

            for (var actor : actors) {
              // get description and make it lowercase
              String desc = actor.getCareerDescription();
              String description = desc.toLowerCase();
              WordCounter counter = new WordCounter();
              // add to final list all actors that have all of the required words in description
              int noWords = counter.countWords(wordList, description);
              if (noWords == noWordsTotal) {
                finalList.add(actor.getName());
              }
            } // sort list by input sort order
            if (action.getSortType().equals("asc")) {
              finalList.sort(Comparator.naturalOrder());
            } else {
              finalList.sort(Collections.reverseOrder());
            }
            arrayResult.add(
                fileWriter.writeFile(action.getActionId(), "", "Query result: " + finalList));
          }
        }

        if (action.getCriteria().equals("longest")) {
          Map<String, Integer> results = new HashMap<>();
          if (action.getObjectType().equals("movies")) {
            SearchList list = new SearchList();
            // parse movie list and get duration for each movie
            for (var movie : movies) {
              Video thisMovie = new Video(movie.getTitle());
              int number = list.countWords(filterGenre, movie.getGenres());
              // add to map all movies that fit required year and genre
              thisMovie.addByFilter(
                  results, year, filterGenre, movie.getYear(), movie.getDuration(), number);
            }
            // sort map then make list with sorted titles
            Map<String, Integer> sortedMap = SortMap.sortByValue(results, action.getSortType());
            List<String> finalList = new ArrayList<>(sortedMap.keySet());
            // if list is smaller then required number, print all
            if (finalList.size() < numberQuery) {
              arrayResult.add(
                  fileWriter.writeFile(action.getActionId(), "", "Query result: " + finalList));
            } else {
              // else print first numberQuery titles
              for (int i = 0; i < numberQuery; i++) {
                queryResult.add(finalList.get(i));

                arrayResult.add(
                    fileWriter.writeFile(action.getActionId(), "", "Query result: " + queryResult));
              }
            }
          }

          if (action.getObjectType().equals("shows")) {
            SearchList list = new SearchList();
            // parse show list and get duration for each title
            for (var show : shows) {
              int number = list.countWords(filterGenre, show.getGenres());
              ArrayList<Season> seasons = show.getSeasons();
              Video thisShow = new Video(show.getTitle());
              int duration = thisShow.getDuration(seasons);
              // add to map all shows that fit required year and genre
              thisShow.addByFilter(results, year, filterGenre, show.getYear(), duration, number);
            }
            // sort map then make list with sorted titles
            Map<String, Integer> sortedMap = SortMap.sortByValue(results, action.getSortType());
            List<String> finalList = new ArrayList<>(sortedMap.keySet());
            // if list is smaller then required number, print all
            if (finalList.size() < numberQuery) {
              arrayResult.add(
                  fileWriter.writeFile(action.getActionId(), "", "Query result: " + finalList));
            } else {
              // else print first numberQuery titles
              for (int i = 0; i < numberQuery; i++) {
                queryResult.add(finalList.get(i));
              }
              arrayResult.add(
                  fileWriter.writeFile(action.getActionId(), "", "Query result: " + queryResult));
            }
          }
        }
        if (action.getCriteria().equals("favorite")) {
          Map<String, Integer> results = new HashMap<>();
          if (action.getObjectType().equals("movies")) {

            // parse movie list and count title appearances in user favourite list
            // then add to map
            for (var movie : movies) {
              String title = movie.getTitle();
              Video thisMovie = new Video(title);
              int views = 0;
              for (var temp : users) {
                List<String> favourites = temp.getFavoriteMovies();
                if (favourites.contains(movie.getTitle())) {
                  views++;
                  results.put(title, views);
                }
              }
              SearchList list = new SearchList();
              List<String> genres = movie.getGenres();
              int number = list.countWords(filterGenre, genres);
              // remove from list movies that dont respect the year or the genre required
              thisMovie.removeByFilter(results, year, filterGenre, movie.getYear(), number);
            }
            // sort map by no. views
            SortMap sorted = new SortMap();
            Map<String, Integer> sortedMap = sorted.sortByValue(results, action.getSortType());
            List<String> finalList = new ArrayList<>(sortedMap.keySet());
            // if title list is smaller than the required no., print all
            if (finalList.size() < numberQuery) {
              arrayResult.add(
                  fileWriter.writeFile(action.getActionId(), "", "Query result: " + finalList));
            } else {
              // else print first numberQuery titles
              for (int i = 0; i < numberQuery; i++) {
                queryResult.add(finalList.get(i));
              }
              arrayResult.add(
                  fileWriter.writeFile(action.getActionId(), "", "Query result: " + queryResult));
            }
          }
          if (action.getObjectType().equals("shows")) {
            // parse show list and count title appearances in user favourite list
            // then add to map
            for (var show : shows) {
              String title = show.getTitle();
              Video thisShow = new Video(title);
              int views;
              for (var temp : users) {
                ArrayList favourites = temp.getFavoriteMovies();
                User thisUser = new User(temp.getUsername());
                if (thisUser.hasFavourite(favourites, title)) {
                  views = thisShow.viewsCounter();
                  results.put(title, views);
                }
              }
              // remove from list movies that dont respect the year or the genre required
              List<String> genres = show.getGenres();
              SearchList list = new SearchList();
              int number = list.countWords(filterGenre, genres);
              thisShow.removeByFilter(results, year, filterGenre, show.getYear(), number);
            }
            // sort map by no. views
            SortMap sorted = new SortMap();
            Map<String, Integer> sortedMap = sorted.sortByValue(results, action.getSortType());
            List<String> finalList = new ArrayList<>(sortedMap.keySet());
            // if title list is smaller than the required no., print all
            if (finalList.size() < numberQuery) {
              arrayResult.add(
                  fileWriter.writeFile(action.getActionId(), "", "Query result: " + finalList));
            } else {
              // else print first numberQuery titles
              for (int i = 0; i < numberQuery; i++) {
                queryResult.add(finalList.get(i));
              }
              arrayResult.add(
                  fileWriter.writeFile(action.getActionId(), "", "Query result: " + queryResult));
            }
          }
        }
        if (action.getCriteria().equals("most_viewed")) {
          if (action.getObjectType().equals("movies")) {
            SearchList list = new SearchList();
            Map<String, Integer> tempResults = new HashMap<>();
            // parse movie list and count number of total views for each movie
            // then add to map
            for (var movie : movies) {
              String title = movie.getTitle();
              int views = 0;
              for (var temp : users) {
                Map<String, Integer> history = temp.getHistory();
                if (history.containsKey(movie.getTitle())) {
                  views++;
                  tempResults.put(title, views);
                }
              }
              // remove all titles that do not respect the given year of genre
              List<String> genres = movie.getGenres();
              int number = list.countWords(filterGenre, genres);
              if (filterGenre.get(0) != null && year != 0) {
                if (number != totalGenres && movie.getYear() != year
                    || (number == totalGenres && movie.getYear() != year)
                    || (number != totalGenres)) {
                  tempResults.remove(title);
                }
              }
              if (year == 0 && filterGenre.get(0) != null) {
                if (number != totalGenres) {
                  tempResults.remove(title);
                }
              }
            }
            // sort map then make list with sorted titles
            SortMap sorted = new SortMap();
            Map<String, Integer> sortedMap = sorted.sortByValue(tempResults, action.getSortType());
            List<String> finalList = new ArrayList<>(sortedMap.keySet());
            // if list is smaller then required number, print all
            if (finalList.size() < numberQuery) {
              arrayResult.add(
                  fileWriter.writeFile(action.getActionId(), "", "Query result: " + finalList));
            } else {
              // else print first numberQuery titles
              for (int i = 0; i < numberQuery; i++) {
                queryResult.add(finalList.get(i));
              }
              arrayResult.add(
                  fileWriter.writeFile(action.getActionId(), "", "Query result: " + queryResult));
            }
          }

          if (action.getObjectType().equals("shows")) {
            SearchList list = new SearchList();
            Map<String, Integer> tempResults = new HashMap<>();
            // parse show list and count number of total views for each movie
            // then add to map
            for (var show : shows) {
              String title = show.getTitle();
              int views = 0;
              for (var temp : users) {
                Map<String, Integer> history = temp.getHistory();
                if (history.containsKey(show.getTitle())) {
                  views++;
                  tempResults.put(title, views);
                }
              }
              // remove all titles that do not respect the given year of genre
              List<String> genres = show.getGenres();
              int number = list.countWords(filterGenre, genres);
              if (filterGenre.get(0) != null && year != 0) {
                if (number != totalGenres && show.getYear() != year
                    || (number == totalGenres && show.getYear() != year)
                    || (number != totalGenres)) {
                  tempResults.remove(title);
                }
              }
              if (year == 0 && filterGenre.get(0) != null) {
                if (number != totalGenres) {
                  tempResults.remove(title);
                }
              }
            }
            // sort map then make list with sorted titles
            SortMap sorted = new SortMap();
            Map<String, Integer> sortedMap = sorted.sortByValue(tempResults, action.getSortType());
            List<String> finalList = new ArrayList<>(sortedMap.keySet());
            // if list is smaller then required number, print all
            if (finalList.size() < numberQuery) {
              arrayResult.add(
                  fileWriter.writeFile(action.getActionId(), "", "Query result: " + finalList));
            } else {
              // else print first numberQuery titles
              for (int i = 0; i < numberQuery; i++) {
                queryResult.add(finalList.get(i));
              }
              arrayResult.add(
                  fileWriter.writeFile(action.getActionId(), "", "Query result: " + queryResult));
            }
          }
        }

        if (action.getCriteria().equals("ratings")) {
          Map<String, Double> results = new HashMap<>();
          if (action.getObjectType().equals("movies")) {
            SearchList list = new SearchList();
            // parse movie list and get average rating for each movie
            // then add to map
            for (var movie : movies) {
              if (movieRatings.wasRated(movie.getTitle())) {
                double rating = movieRatings.getRatingOfMovie(movie.getTitle());
                results.put(movie.getTitle(), rating);
              }
              // remove all titles that do not respect the given year of genre
              List<String> genres = movie.getGenres();
              int number = list.countWords(filterGenre, genres);
              if (filterGenre.get(0) != null && year != 0) {
                if (number != totalGenres && movie.getYear() != year
                    || (number == totalGenres && movie.getYear() != year)
                    || (number != totalGenres)) {
                  results.remove(movie.getTitle());
                }
              }
              if (year == 0 && filterGenre.get(0) != null) {
                if (number != totalGenres) {
                  results.remove(movie.getTitle());
                }
              }
            }
            // sort map then make list with sorted titles
            SortMap sorted = new SortMap();
            Map<String, Double> sortedMap = sorted.sortByValueDouble(results, action.getSortType());
            List<String> finalList = new ArrayList<>(sortedMap.keySet());
            // if list is smaller then required number, print all
            if (finalList.size() < numberQuery) {
              arrayResult.add(
                  fileWriter.writeFile(action.getActionId(), "", "Query result: " + finalList));
            } else {
              // else print first numberQuery titles
              for (int i = 0; i < numberQuery; i++) {
                queryResult.add(finalList.get(i));
              }
              arrayResult.add(
                  fileWriter.writeFile(action.getActionId(), "", "Query result: " + queryResult));
            }
          }
          if (action.getObjectType().equals("shows")) {
            SearchList list = new SearchList();
            // parse show list and get average rating for each show
            // then add to map
            for (var show : shows) {
              if (showRatings.wasRated(show.getTitle(), action.getSeasonNumber())) {
                double rating = showRatings.getRatingOfShow(show.getTitle());
                results.put(show.getTitle(), rating);
              }
              // remove all titles that do not respect the given year of genre
              List<String> genres = show.getGenres();
              int number = list.countWords(filterGenre, genres);
              if ((number != totalGenres && show.getYear() != year)
                  || (number == totalGenres && show.getYear() != year)
                  || (number != totalGenres)) {
                results.remove(show.getTitle());
              }
            }
            // sort map then make list with sorted titles
            SortMap sorted = new SortMap();
            Map<String, Double> sortedMap = sorted.sortByValueDouble(results, action.getSortType());
            List<String> finalList = new ArrayList<>(sortedMap.keySet());
            // if list is smaller then required number, print all
            if (finalList.size() < numberQuery) {
              arrayResult.add(
                  fileWriter.writeFile(action.getActionId(), "", "Query result: " + finalList));
            } else {
              // else print first numberQuery titles
              for (int i = 0; i < numberQuery; i++) {
                queryResult.add(finalList.get(i));
              }
              arrayResult.add(
                  fileWriter.writeFile(action.getActionId(), "", "Query result: " + queryResult));
            }
          }
        }
        if (action.getObjectType().equals("users") && action.getCriteria().equals("num_ratings")) {
          Map<String, Integer> activeUsers = new HashMap<>();
          // parse list of users that gave ratings and count how many times each username appears
          // add to map username and its number of appearances (that is not 0)
          for (var user : users) {
            String username = user.getUsername();
            int number = 0;
            for (var word : usersRatings) {
              if (word.contains(username)) {
                number++;
              }
            }
            if (number != 0) {
              activeUsers.put(user.getUsername(), number);
            }
          }
          // sort map by number of ratings, then make list with sorted users
          SortMap sorted = new SortMap();
          Map<String, Integer> sortedMap = sorted.sortByValue(activeUsers, action.getSortType());
          List<String> finalList = new ArrayList<>(sortedMap.keySet());
          // if list is smaller then required number, print all
          if (finalList.size() < numberQuery) {
            arrayResult.add(
                fileWriter.writeFile(action.getActionId(), "", "Query result: " + finalList));
          } else {
            // else print first numberQuery titles
            for (int i = 0; i < numberQuery; i++) {
              queryResult.add(finalList.get(i));
            }
            arrayResult.add(
                fileWriter.writeFile(action.getActionId(), "", "Query result: " + queryResult));
          }
        }
      }

      if (action.getActionType().equals("recommendation")) {
        if (action.getType().equals("standard")) {
          int found = 0;
          for (var user : users) {
            if (action.getUsername().equals(user.getUsername())) {
              // get user history
              Map<String, Integer> history = user.getHistory();
              // parse movies list and print first movie that was not seen
              for (int i = 0; i < movies.size(); i++) {
                ShowInput s = movies.get(i);
                if (!history.containsKey(s.getTitle())) {
                  found = 1;
                  arrayResult.add(
                      fileWriter.writeFile(
                          action.getActionId(),
                          "",
                          "StandardRecommendation result: " + s.getTitle()));
                  break;
                }
              }
              // parse shows list and print first title that was not seen
              if (found == 0) {
                for (ShowInput s : shows) {
                  if (!history.containsKey(s.getTitle())) {
                    arrayResult.add(
                        fileWriter.writeFile(
                            action.getActionId(),
                            "",
                            "StandardRecommendation result: " + s.getTitle()));
                    break;
                  }
                }
              }
            }
          }
        }
        if (action.getType().equals("best_unseen")) {
          Map<String, Double> resultsMovies = new HashMap<>();
          Map<String, Double> resultsShows = new HashMap<>();
          // create map of all rated movies
          for (var movie : movies) {
            if (movieRatings.wasRated(movie.getTitle())) {
              double rating = movieRatings.getRatingOfMovie(movie.getTitle());
              resultsMovies.put(movie.getTitle(), rating);
            }
          }
          // create map of all rated shows
          for (var show : shows) {
            if (showRatings.wasRated(show.getTitle(), show.getNumberSeason())) {
              double rating = showRatings.getRatingOfShow(show.getTitle());
              resultsShows.put(show.getTitle(), rating);
            }
          }
          // concatenate maps & then sort by highest rating
          resultsMovies.putAll(resultsShows);
          SortMap sorted = new SortMap();
          Map<String, Double> sortedMap = sorted.sortByValueDouble(resultsMovies, "desc");
          // create list of sorted titles
          List<String> orderedList = new ArrayList<>(sortedMap.keySet());

          for (var user : users) {
            Map<String, Integer> history = user.getHistory();
            // remove title from list if user has already seen it
            if (action.getUsername().equals(user.getUsername())) {
              orderedList.removeIf(history::containsKey);
            }
            // if list is empty, add all videos that haven't been seen
            if (orderedList.isEmpty()) {
              for (var movie : movies) {
                if (!history.containsKey(movie.getTitle())) {
                  orderedList.add(movie.getTitle());
                }
              }
              for (var show : shows) {
                if (!history.containsKey(show.getTitle())) {
                  orderedList.add(show.getTitle());
                }
              }
            }
          }
          // print first title in list
          if (!orderedList.isEmpty()) {
            String s = orderedList.get(0);
            arrayResult.add(
                fileWriter.writeFile(
                    action.getActionId(), "", "BestRatedUnseenRecommendation result: " + s));
          }
        }

        if (action.getType().equals("popular")) {
          outerloop:
          for (var user : users) {
            // check if user has the required subscription type
            if (user.getSubscriptionType().equals("BASIC")) {
              arrayResult.add(
                  fileWriter.writeFile(
                      action.getActionId(), "", "PopularRecommendation cannot be applied!"));
              break;
            }
            // create list with all of the existing genres
            // count how many times each genre appears in database and put in map
            if (action.getUsername().equals(user.getUsername())) {
              Map<String, Integer> popularGenres = new HashMap<>();
              List<String> allGenres =
                  Arrays.asList(
                      "Action",
                      "Adventure",
                      "Drama",
                      "Comedy",
                      "Crime",
                      "Romance",
                      "War",
                      "History",
                      "Thriller",
                      "Mystery",
                      "Family",
                      "Horror",
                      "Fantasy",
                      "Science fiction",
                      "Action & Adventure",
                      "Sci-fi & Fantasy",
                      "Animation",
                      "Kids",
                      "Western",
                      "Tv Movie");
              for (var genre : allGenres) {
                int apparitions = 0;
                for (var movie : movies) {
                  List<String> genres = movie.getGenres();
                  if (genres.contains(genre)) {
                    apparitions++;
                  }
                }
                popularGenres.put(genre, apparitions);
                int apparitionsShows = 0;
                for (var show : shows) {
                  List<String> genres = show.getGenres();
                  if (genres.contains(genre)) {
                    apparitionsShows++;
                  }
                }
                popularGenres.put(genre, apparitionsShows);
              }
              // sort map by number of apparitions
              SortMap sorted = new SortMap();
              Map<String, Integer> sortedMap = sorted.sortByValue(popularGenres, "desc");
              List<String> finalList = new ArrayList<>(sortedMap.keySet());
              Map<String, Integer> history = user.getHistory();

              // if final list of videos is not empty, print first title not seen
              if (!finalList.isEmpty()) {
                for (int i = 0; i < finalList.size(); i++) {
                  for (var movie : movies) {
                    if (!history.containsKey(movie.getTitle())) {
                      arrayResult.add(
                          fileWriter.writeFile(
                              action.getActionId(),
                              "",
                              "PopularRecommendation result: " + movie.getTitle()));
                      break outerloop;
                    }
                  }

                  for (var show : shows) {
                    if (!user.getHistory().containsKey(show.getTitle())) {
                      arrayResult.add(
                          fileWriter.writeFile(
                              action.getActionId(),
                              "",
                              "PopularRecommendation result: " + show.getTitle()));
                      break outerloop;
                    }
                  }
                }
              }
            }
          }
        }
        if (action.getType().equals("favorite")) {
          String username = action.getUsername();
          for (var user : users) {
            // create list of favourite movies of all users except the one given at input
            if (!username.equals(user.getUsername())) {
              List<String> favourites = user.getFavoriteMovies();
              totalFavourites.addAll(favourites);
            }
          }
          // parse all movies in database and count how many times each one appears, then add to map
          Map<String, Integer> results = new HashMap<>();
          for (var movie : movies) {
            int number = 0;
            if (totalFavourites.contains(movie.getTitle())) {
              number++;
            }
            if (number != 0) {
              results.put(movie.getTitle(), number);
            }
          }
          // parse all shows in database and count how many times each one appears, then add to map
          for (var show : shows) {
            int counter = 0;
            if (totalFavourites.contains(show.getTitle())) {
              counter++;
            }
            if (counter != 0) {
              results.put(show.getTitle(), counter);
            }
          }
          // sort map by number of apparitions
          SortMap sorted = new SortMap();
          Map<String, Integer> sortedMap = sorted.sortByValue(results, "desc");
          List<String> list = new ArrayList<>(sortedMap.keySet());
          loop:
          for (var user : users) {
            // get history of input user, then remove all titles that have been seen from results
            if (username.equals(user.getUsername())) {
              Map<String, Integer> history = user.getHistory();
              list.removeIf(history::containsKey);
            }
            // if final list of videos is not empty, print first title
            if (!list.isEmpty()) {
              String s = list.get(0);
              arrayResult.add(
                  fileWriter.writeFile(
                      action.getActionId(), "", "FavoriteRecommendation result: " + s));
              break loop;
            }

            if (list.isEmpty()) {
              List<MovieInputData> films = input.getMovies();
              List<SerialInputData> serials = input.getSerials();
              // else parse all videos again, and print the first not seen one
              for (var movie : films) {
                if (username.equals(user.getUsername())) {
                  if (user.getHistory().containsKey(movie.getTitle())) {
                    //noinspection SuspiciousMethodCalls
                    films.remove(movie.getTitle());
                  } else {
                    arrayResult.add(
                        fileWriter.writeFile(
                            action.getActionId(),
                            "",
                            "FavoriteRecommendation result: " + movie.getTitle()));
                  }
                  break loop;
                }
              }
              for (var show : shows) {
                if (username.equals(user.getUsername())) {
                  if (user.getHistory().containsKey(show.getTitle())) {
                    serials.remove(show);
                    if (serials.isEmpty()) {
                      arrayResult.add(
                          fileWriter.writeFile(
                              action.getActionId(),
                              "",
                              "FavoriteRecommendation cannot be applied!"));
                      break loop;
                    }
                  } else {
                    arrayResult.add(
                        fileWriter.writeFile(
                            action.getActionId(),
                            "",
                            "FavoriteRecommendation result: " + show.getTitle()));
                    break loop;
                  }
                }
              }
            }
          }
        }

        if (action.getType().equals("search")) {
          for (var user : users) {
            // check if input user has the required subscription type
            if (user.getSubscriptionType().equals("BASIC")) {
              arrayResult.add(
                  fileWriter.writeFile(
                      action.getActionId(), "", "SearchRecommendation cannot be applied!"));
              break;
            } else {
              // create list with all of the existing genres
              List<String> searchKeys = Collections.singletonList(action.getGenre());
              List<String> allGenres =
                  Arrays.asList(
                      "Action",
                      "Adventure",
                      "Drama",
                      "Comedy",
                      "Crime",
                      "Romance",
                      "War",
                      "History",
                      "Thriller",
                      "Mystery",
                      "Family",
                      "Horror",
                      "Fantasy",
                      "Science fiction",
                      "Action & Adventure",
                      "Sci-fi & Fantasy",
                      "Animation",
                      "Kids",
                      "Western",
                      "Tv Movie");
              SearchList list = new SearchList();
              // verify if the input genre exists in the list
              int number = list.countWords(searchKeys, allGenres);
              if (number == 0) {
                // if not, display error
                arrayResult.add(
                    fileWriter.writeFile(
                        action.getActionId(), "", "SearchRecommendation cannot be applied!"));
                break;
              }
            }
            // create map of the rated movies with their average rating
            Map<String, Double> resultsMovies = new HashMap<>();
            String genre = action.getGenre();
            if (action.getUsername().equals(user.getUsername())) {
              for (var movie : movies) {
                if (movie.getGenres().contains(genre)) {
                  if (!user.getHistory().containsKey(movie.getTitle())) {
                    if (movieRatings.wasRated(movie.getTitle())) {
                      double rating = movieRatings.getRatingOfMovie(movie.getTitle());
                      resultsMovies.put(movie.getTitle(), rating);
                    } else {
                      resultsMovies.put(movie.getTitle(), 0.0);
                    }
                  }
                }
              }
              // create map of the rated shows with their average rating
              Map<String, Double> resultsShows = new HashMap<>();
              for (var show : shows) {
                if (show.getGenres().contains(genre)) {
                  if (!user.getHistory().containsKey(show.getTitle())) {
                    if (showRatings.wasRated(show.getTitle(), show.getNumberSeason())) {
                      double rating = showRatings.getRatingOfShow(show.getTitle());
                      resultsShows.put(show.getTitle(), rating);
                    } else {
                      resultsShows.put(show.getTitle(), 0.0);
                    }
                  }
                }
              }

              // concatenate the maps so that we have a complete map of all the videos
              resultsMovies.putAll(resultsShows);
              SortMap sorted = new SortMap();
              // sort map by the rating
              Map<String, Double> sortedMap = sorted.sortByValueDouble(resultsMovies, "desc");
              List<String> finalList = new ArrayList<>(sortedMap.keySet());

              if (!finalList.isEmpty()) {
                arrayResult.add(
                    fileWriter.writeFile(
                        action.getActionId(), "", "SearchRecommendation result: " + finalList));
                break;
              } else {
                arrayResult.add(
                    fileWriter.writeFile(
                        action.getActionId(), "", "SearchRecommendation cannot be applied!"));
              }
            }
          }
        }
      }
    }

    fileWriter.closeJSON(arrayResult);
  }
}
