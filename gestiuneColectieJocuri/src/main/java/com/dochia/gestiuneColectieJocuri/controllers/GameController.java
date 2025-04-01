/** Controller pentru jocuri. Acest controller gestioneaza rutele pentru operatiunile legate de jocuri, incluzand adaugarea, stergerea, actualizarea si vizualizarea detaliilor jocurilor.
 * @author Dochia Dimitrie Razvan
 * @version 12 Ianuarie 2024
 */

package com.dochia.gestiuneColectieJocuri.controllers;

import com.dochia.gestiuneColectieJocuri.models.Game;
import com.dochia.gestiuneColectieJocuri.models.UserGame;
import com.dochia.gestiuneColectieJocuri.models.Users;
import com.dochia.gestiuneColectieJocuri.repositories.UserGameRepository;
import com.dochia.gestiuneColectieJocuri.services.GameService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/games")
public class GameController {

    @Autowired
    private GameService gameService; // Injectare GameService pentru logica jocurilor.

    @Autowired
    private UserGameRepository userGameRepository; // Injectare UserGameRepository pentru acces la baza de date.

    // Ruta pentru listarea jocurilor colectate de utilizator.
    @GetMapping("/list")
    public String listGames(Model model, HttpSession session) {
        Users user = (Users) session.getAttribute("user"); // Preia utilizatorul din sesiune.
        if (user == null) {
            return "redirect:/"; // Redirectioneaza la pagina principala daca utilizatorul nu este autentificat.
        }
        List<UserGame> userGames = gameService.findUserGamesByUser(user); // Preia jocurile utilizatorului.

        // Calculeaza procentajul de completare pentru fiecare joc.
        for (UserGame userGame : userGames) {
            if (userGame.getGame().getAchievementsCount() > 0) {
                double completionPercentage =
                        (100.0 * userGame.getUserAchievementsCount()) / userGame.getGame().getAchievementsCount();
                userGame.setCompletionPercentage(completionPercentage);
            } else {
                userGame.setCompletionPercentage(0.0); // Seteaza 0% daca nu exista realizari.
            }
        }

        // Seteaza magazinele disponibile pentru fiecare joc.
        for (UserGame userGame : userGames) {
            String[] stores = userGame.getGame().getStores().split(",");
            userGame.setAvailableStores(List.of(stores));
        }

        model.addAttribute("games", userGames); // Adauga lista de jocuri in model.
        model.addAttribute("isLoggedIn", true); // Indica daca utilizatorul este autentificat.
        return "games"; // Returneaza view-ul "games".
    }

    // Ruta pentru adaugarea unui joc la colectia utilizatorului.
    @PostMapping("/add")
    @ResponseBody
    public String addGame(@RequestBody Game gameData, HttpSession session) {
        Users user = (Users) session.getAttribute("user"); // Preia utilizatorul din sesiune.
        if (user == null) {
            return "redirect:/"; // Redirectioneaza la pagina principala daca utilizatorul nu este autentificat.
        }

        try {
            gameService.checkAndSaveGameForUser(gameData, user); // Verifica si salveaza jocul.
            return "Game added successfully!"; // Mesaj de succes.
        } catch (Exception e) {
            e.printStackTrace();
            return "Error adding game: " + e.getMessage(); // Returneaza eroarea in caz de esec.
        }
    }

    // Ruta pentru adaugarea unui joc folosind un payload mai generic.
    @PostMapping("/add2")
    @ResponseBody
    public String addGame2(@RequestBody Map<String, Object> requestData, HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            return "redirect:/"; // Redirectioneaza daca utilizatorul nu este autentificat.
        }

        try {
            Long gameId = Long.parseLong(requestData.get("id").toString()); // Preia ID-ul jocului din payload.
            String gameTitle = requestData.get("title").toString(); // Preia titlul jocului.
            Game game = gameService.findGameById(gameId); // Gaseste jocul in baza de date.

            gameService.addGameToUserCollection(game, user); // Adauga jocul la colectia utilizatorului.

            return "Game added to your collection successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error adding game: " + e.getMessage(); // Returneaza eroarea in caz de esec.
        }
    }

    // Ruta pentru adaugarea unui joc in baza de date.
    @PostMapping("/addToDatabase")
    @ResponseBody
    public ResponseEntity<?> addGameToDatabase(@RequestBody Game gameData) {
        try {
            Game existingGame = gameService.checkAndSaveGameToDatabase(gameData); // Verifica si salveaza jocul.

            return ResponseEntity.ok(existingGame); // Returneaza jocul salvat.
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding game to database: " + e.getMessage());
        }
    }

    // Ruta pentru actualizarea detaliilor unui joc din colectia utilizatorului.
    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<String> updateGameDetails(@RequestBody Map<String, Object> updateData, HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in.");
        }

        // Verifica daca ID-ul si campul sunt prezente in payload.
        if (!updateData.containsKey("id") || updateData.get("id") == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("UserGame ID is required.");
        }
        if (!updateData.containsKey("field") || updateData.get("field") == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Field to update is required.");
        }

        try {
            gameService.updateUserGameDetails(updateData); // Actualizeaza detaliile jocului.
            return ResponseEntity.ok("Update successful!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + e.getMessage());
        }
    }

    // Ruta pentru stergerea unui joc din colectia utilizatorului.
    @DeleteMapping("/delete/{userGameId}")
    @ResponseBody
    public ResponseEntity<String> deleteGame(@PathVariable(required = false) Long userGameId, HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in.");
        }

        if (userGameId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid UserGame ID.");
        }

        try {
            gameService.deleteUserGame(userGameId, user); // Sterge jocul.
            return ResponseEntity.ok("Game deleted successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting game: " + e.getMessage());
        }
    }

    // Ruta pentru vizualizarea detaliilor unui joc.
    @GetMapping("/details/{id}")
    public String viewGameDetails(@PathVariable Long id, Model model, HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        boolean loggedIn = user != null; // Verifica daca utilizatorul este autentificat.

        try {
            Game game = gameService.findGameById(id); // Gaseste jocul in baza de date.
            UserGame userGame = null;

            if (loggedIn) {
                userGame = gameService.findUserGameByUserAndGame(user, game); // Gaseste UserGame asociat.

                if (userGame != null) {
                    String[] stores = game.getStores().split(",");
                    userGame.setAvailableStores(List.of(stores));
                }
            }

            if (loggedIn) {
                model.addAttribute("isLoggedIn", true);
            }
            model.addAttribute("game", game);
            model.addAttribute("userGame", userGame);
            model.addAttribute("userHasGame", userGame != null);

            return "game-details"; // Returneaza view-ul pentru detaliile jocului.
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/error"; // Redirectioneaza la pagina de eroare.
        }
    }

    // Ruta pentru actualizarea starii de "favorit" a unui joc.
    @PostMapping("/updateFavorite")
    @ResponseBody
    public ResponseEntity<?> updateFavorite(@RequestBody Map<String, Object> payload, HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in.");
        }

        Long userGameId = Long.parseLong(payload.get("id").toString());
        boolean favorite = Boolean.parseBoolean(payload.get("favorite").toString());

        try {
            gameService.updateFavoriteStatus(userGameId, favorite, user); // Actualizeaza starea de favorit.
            return ResponseEntity.ok("Favorite status updated successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating favorite status: " + e.getMessage());
        }
    }

}
