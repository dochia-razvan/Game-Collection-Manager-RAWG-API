/**
 * Service pentru gestionarea logicii aplicatiei legate de jocuri.
 * Contine operatiuni pentru gestionarea colectiilor de jocuri ale utilizatorilor.
 * @author Dochia Dimitrie Razvan
 * @version 12 Ianuarie 2024
 */

package com.dochia.gestiuneColectieJocuri.services;

import com.dochia.gestiuneColectieJocuri.models.Game;
import com.dochia.gestiuneColectieJocuri.models.UserGame;
import com.dochia.gestiuneColectieJocuri.models.Users;
import com.dochia.gestiuneColectieJocuri.repositories.GameRepository;
import com.dochia.gestiuneColectieJocuri.repositories.UserGameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository; // Repository pentru accesarea datelor despre jocuri

    @Autowired
    private UserGameRepository userGameRepository; // Repository pentru accesarea datelor despre legatura utilizator-joc

    private static final Map<String, String> FIELD_NAMES = new HashMap<>(); // Map pentru traducerea numelor de campuri

    static {
        // Asocierea campurilor interne cu denumirile lor prietenoase
        FIELD_NAMES.put("stores", "Preferred Store");
        FIELD_NAMES.put("hoursPlayed", "Hours Played");
        FIELD_NAMES.put("personalRating", "Personal Rating");
        FIELD_NAMES.put("review", "Review");
        FIELD_NAMES.put("favorite", "Favorite");
        FIELD_NAMES.put("completionStatus", "Completion Status");
        FIELD_NAMES.put("userAchievementsCount", "Achievements Count");
    }

    // Metoda pentru obtinerea unei denumiri prietenoase a unui camp
    private String getFriendlyFieldName(String field) {
        return FIELD_NAMES.getOrDefault(field, "Unknown Field");
    }

    // Metoda care verifica datele unui joc si il salveaza in baza de date pentru un utilizator
    public void checkAndSaveGameForUser(Game game, Users user) {
        // Asigurarea ca toate campurile jocului au valori valide sau implicite
        if (game.getTitle() == null || game.getTitle().isEmpty()) game.setTitle("N/A");
        if (game.getGenre() == null || game.getGenre().isEmpty()) game.setGenre("N/A");
        if (game.getReleaseYear() == null || game.getReleaseYear().isEmpty()) game.setReleaseYear("N/A");
        if (game.getImage() == null || game.getImage().isEmpty())
            game.setImage("https://treesa.org/wp-content/uploads/2017/02/300px-No_image_available.svg.png");
        if (game.getDescription() == null || game.getDescription().isEmpty()) game.setDescription("N/A");
        if (game.getDevelopers() == null || game.getDevelopers().isEmpty()) game.setDevelopers("N/A");
        if (game.getPublishers() == null || game.getPublishers().isEmpty()) game.setPublishers("N/A");
        if (game.getPlatforms() == null || game.getPlatforms().isEmpty()) game.setPlatforms("N/A");
        if (game.getStores() == null || game.getStores().isEmpty()) game.setStores("N/A");
        if (game.getMetacriticScore() == null || game.getMetacriticScore() == 0) game.setMetacriticScore(0);
        if (game.getEsrbRating() == null || game.getEsrbRating().isEmpty()) game.setEsrbRating("N/A");
        if (game.getWebsite() == null || game.getWebsite().isEmpty()) game.setWebsite("N/A");
        if (game.getTags() == null || game.getTags().isEmpty()) game.setTags("N/A");
        if (game.getPCrequirements() == null || game.getPCrequirements().isEmpty()) game.setPCrequirements("N/A");
        if (game.getLastUpdateDate() == null || game.getLastUpdateDate().isEmpty()) game.setLastUpdateDate("N/A");
        if (game.getAchievementsCount() == null || game.getAchievementsCount() == 0) game.setAchievementsCount(0);
        if (game.getTba() == null || !game.getTba()) game.setTba(false);
        if (game.getMetacriticURL() == null || game.getMetacriticURL().isEmpty()) game.setMetacriticURL("N/A");

        // Cauta jocul in baza de date dupa titlu si anul lansarii
        Game existingGame = gameRepository.findByTitleAndReleaseYear(game.getTitle(), game.getReleaseYear());
        if (existingGame == null) {
            existingGame = gameRepository.save(game); // Salveaza jocul in baza de date daca nu exista
        }

        // Daca utilizatorul nu are deja acest joc in colectie, adauga o noua intrare
        if (userGameRepository.findByUserAndGame(user, existingGame).isEmpty()) {
            UserGame userGame = new UserGame();
            userGame.setUser(user); // Asociaza utilizatorul
            userGame.setGame(existingGame); // Asociaza jocul
            userGame.setStores("N/A");
            userGame.setHoursPlayed(0.0);
            userGame.setPersonalRating(0.0);
            userGame.setReview("N/A");
            userGame.setFavorite(false);
            userGame.setCompletionStatus("Not Started");
            userGame.setUserAchievementsCount(0);
            userGameRepository.save(userGame); // Salveaza in baza de date
        }
    }

    public Game checkAndSaveGameToDatabase(Game game) {
        if (game.getTitle() == null || game.getTitle().isEmpty()) game.setTitle("N/A");
        if (game.getGenre() == null || game.getGenre().isEmpty()) game.setGenre("N/A");
        if (game.getReleaseYear() == null || game.getReleaseYear().isEmpty()) game.setReleaseYear("N/A");
        if (game.getImage() == null || game.getImage().isEmpty())
            game.setImage("https://treesa.org/wp-content/uploads/2017/02/300px-No_image_available.svg.png");
        if (game.getDescription() == null || game.getDescription().isEmpty()) game.setDescription("N/A");
        if (game.getDevelopers() == null || game.getDevelopers().isEmpty()) game.setDevelopers("N/A");
        if (game.getPublishers() == null || game.getPublishers().isEmpty()) game.setPublishers("N/A");
        if (game.getPlatforms() == null || game.getPlatforms().isEmpty()) game.setPlatforms("N/A");
        if (game.getStores() == null || game.getStores().isEmpty()) game.setStores("N/A");
        if (game.getMetacriticScore() == null || game.getMetacriticScore() == 0) game.setMetacriticScore(0);
        if (game.getEsrbRating() == null || game.getEsrbRating().isEmpty()) game.setEsrbRating("N/A");
        if (game.getWebsite() == null || game.getWebsite().isEmpty()) game.setWebsite("N/A");
        if (game.getTags() == null || game.getTags().isEmpty()) game.setTags("N/A");
        if (game.getPCrequirements() == null || game.getPCrequirements().isEmpty()) game.setPCrequirements("N/A");
        if (game.getLastUpdateDate() == null || game.getLastUpdateDate().isEmpty()) game.setLastUpdateDate("N/A");
        if (game.getAchievementsCount() == null || game.getAchievementsCount() == 0) game.setAchievementsCount(0);
        if (game.getTba() == null || !game.getTba()) game.setTba(false);
        if (game.getMetacriticURL() == null || game.getMetacriticURL().isEmpty()) game.setMetacriticURL("N/A");

        // Cautam jocul in baza de date dupa titlu si anul lansarii
        Game existingGame = gameRepository.findByTitleAndReleaseYear(game.getTitle(), game.getReleaseYear());
        // Daca jocul nu exista in baza de date, il salvam
        if (existingGame == null) {
            existingGame = gameRepository.save(game);
        }
        // Returnam jocul gasit sau creat
        return existingGame;
    }

    public void updateUserGameDetails(Map<String, Object> updateData) {
        // Verificam daca datele primite pentru actualizare sunt nule
        if (updateData == null) {
            throw new IllegalArgumentException("Update data cannot be null.");
        }

        // Extragem informatiile necesare din mapa primita
        Long userGameId = updateData.get("id") != null ? Long.valueOf(updateData.get("id").toString()) : null;
        String field = updateData.get("field") != null ? updateData.get("field").toString() : null;
        String value = updateData.get("value") != null ? updateData.get("value").toString().trim() : "";

        // Verificam daca ID-ul sau campul de actualizat sunt nule
        if (userGameId == null) {
            throw new IllegalArgumentException("UserGame ID cannot be null.");
        }
        if (field == null) {
            throw new IllegalArgumentException("Field to update cannot be null.");
        }

        // Obtinem un nume de camp prietenos pentru mesaje de eroare
        String friendlyFieldName = getFriendlyFieldName(field);

        // Cautam jocul utilizatorului dupa ID
        UserGame userGame = userGameRepository.findById(userGameId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid UserGame ID"));

        try {
            // Actualizam valoarea campului specific in functie de tipul acestuia
            switch (field) {
                case "stores":
                    userGame.setStores(value.isEmpty() || value.equals("N/A") ? "N/A" : value);
                    break;

                case "hoursPlayed":
                    double hours = value.isEmpty() ? 0 : Double.parseDouble(value);
                    if (hours < 0) throw new IllegalArgumentException(friendlyFieldName + " cannot be negative.");
                    userGame.setHoursPlayed(Math.round(hours * 10.0) / 10.0);
                    break;

                case "personalRating":
                    double rating = value.isEmpty() ? 0 : Double.parseDouble(value);
                    if (rating < 0 || rating > 10)
                        throw new IllegalArgumentException(friendlyFieldName + " must be between 0 and 10.");
                    userGame.setPersonalRating(Math.round(rating * 10.0) / 10.0);
                    break;

                case "review":
                    userGame.setReview(value.isEmpty() ? "N/A" : value);
                    break;

                case "favorite":
                    userGame.setFavorite(Boolean.parseBoolean(value));
                    break;

                case "completionStatus":
                    if (!List.of("Not Started", "In Progress", "Completed").contains(value)) {
                        throw new IllegalArgumentException(friendlyFieldName + " is invalid.");
                    }
                    userGame.setCompletionStatus(value);
                    break;

                case "userAchievementsCount":
                    int achievements = value.isEmpty() ? 0 : Integer.parseInt(value);
                    if (achievements < 0)
                        throw new IllegalArgumentException(friendlyFieldName + " cannot be negative.");
                    if (achievements > userGame.getGame().getAchievementsCount()) {
                        throw new IllegalArgumentException(friendlyFieldName + " cannot exceed the total Achievements Count.");
                    }
                    userGame.setUserAchievementsCount(achievements);
                    break;

                default:
                    throw new IllegalArgumentException("Invalid field: " + friendlyFieldName);
            }

            // Salvam modificarile in baza de date
            userGameRepository.save(userGame);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(friendlyFieldName + " must be a valid number.");
        }
    }

    public void deleteUserGame(Long userGameId, Users user) {
        // Verificam daca ID-ul jocului utilizatorului este valid
        if (userGameId == null) {
            throw new IllegalArgumentException("Invalid UserGame ID.");
        }

        // Cautam jocul utilizatorului dupa ID
        UserGame userGame = userGameRepository.findById(userGameId)
                .orElseThrow(() -> new IllegalArgumentException("UserGame not found for ID: " + userGameId));

        // Verificam daca utilizatorul are permisiunea de a sterge acest joc
        if (!userGame.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Unauthorized deletion attempt.");
        }

        // Stergem jocul din colectia utilizatorului
        userGameRepository.delete(userGame);
    }

    public void updateFavoriteStatus(Long userGameId, boolean favorite, Users user) {
        // Cautam jocul utilizatorului dupa ID
        UserGame userGame = userGameRepository.findById(userGameId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid UserGame ID"));

        // Verificam daca utilizatorul are permisiunea de a actualiza acest joc
        if (!userGame.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Unauthorized action.");
        }

        // Actualizam statutul de "Favorite"
        userGame.setFavorite(favorite);
        userGameRepository.save(userGame);
    }

    public List<UserGame> findUserGamesByUser(Users user) {
        // Returnam toate jocurile asociate utilizatorului
        return userGameRepository.findByUser(user);
    }

    public Game findGameById(Long id) {
        // Cautam un joc in baza de date dupa ID-ul sau
        return gameRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Game not found with ID: " + id));
    }

    public UserGame findUserGameByUserAndGame(Users user, Game game) {
        // Cautam relatia dintre utilizator si joc, daca exista
        return userGameRepository.findByUserAndGame(user, game)
                .orElse(null);
    }

    public void addGameToUserCollection(Game game, Users user) {
        // Verificam daca jocul exista deja in colectia utilizatorului
        if (userGameRepository.findByUserAndGame(user, game).isPresent()) {
            throw new IllegalArgumentException("Game already exists in your collection.");
        }

        // Cream o noua intrare pentru jocul utilizatorului
        UserGame userGame = new UserGame();
        userGame.setUser(user);
        userGame.setGame(game);
        userGame.setStores("N/A");
        userGame.setHoursPlayed(0.0);
        userGame.setPersonalRating(0.0);
        userGame.setReview("N/A");
        userGame.setFavorite(false);
        userGame.setCompletionStatus("Not Started");
        userGame.setUserAchievementsCount(0);

        // Salvam jocul in colectia utilizatorului
        userGameRepository.save(userGame);
    }
}



