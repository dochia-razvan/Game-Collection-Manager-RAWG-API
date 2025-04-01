/** Controller pentru gestionarea interactiunilor cu API-ul RAWG
 * Se ocupa de cautarea si obtinerea detaliilor despre jocuri folosind API-ul RAWG.
 * @author Dochia Dimitrie Razvan
 * @version 12 Ianuarie 2024
 */

package com.dochia.gestiuneColectieJocuri.controllers;

import com.dochia.gestiuneColectieJocuri.models.Game;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rawg") // Rutele specifice interactiunii cu API-ul RAWG.
public class RawgAPIController {

    private final String API_KEY = "d5f53a22639f475689e122c25b2ffc2b"; // Cheia API pentru autentificare.

    // Endpoint pentru cautarea jocurilor.
    @GetMapping("/search")
    public ResponseEntity<?> searchGames(@RequestParam String query) {
        String url = "https://api.rawg.io/api/games?key=" + API_KEY + "&search=" + query; // Construieste URL-ul pentru cautare.
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            // Verifica daca raspunsul este de succes si returneaza datele.
            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> data = response.getBody();
                if (data != null && data.containsKey("results")) {
                    return ResponseEntity.ok(data); // Returneaza rezultatele cautarii.
                }
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No results found."); // Returneaza mesaj daca nu exista rezultate.
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while searching games."); // Eroare generica.
        }
    }

    // Endpoint pentru obtinerea detaliilor despre un joc specific.
    @GetMapping("/details/{id}")
    public ResponseEntity<?> getGameDetails(@PathVariable Long id) {
        String rawgApiUrl = "https://api.rawg.io/api/games/" + id + "?key=" + API_KEY; // Construieste URL-ul pentru detalii joc.
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(rawgApiUrl, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> gameData = response.getBody();

                // Verifica daca datele jocului sunt disponibile.
                if (gameData == null || gameData.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Game details not found.");
                }

                // Extrage datele necesare si construieste un obiect Game.
                String title = getOrDefault(gameData, "name", "N/A");
                String releaseYear = getOrDefault(gameData, "released", "N/A");
                String description = getOrDefault(gameData, "description_raw", "N/A");
                String esrbRating = getNestedOrDefault(gameData, "esrb_rating", "name", "N/A");
                String image = getOrDefault(gameData, "background_image", "https://treesa.org/wp-content/uploads/2017/02/300px-No_image_available.svg.png");
                String website = getOrDefault(gameData, "website", "N/A");

                String genres = extractListField(gameData, "genres", "name");
                String developers = extractListField(gameData, "developers", "name");
                String publishers = extractListField(gameData, "publishers", "name");
                String platforms = extractNestedListField(gameData, "platforms", "platform", "name");
                String pcRequirements = extractPCRequirements(gameData);
                String stores = extractNestedListField(gameData, "stores", "store", "name");
                String tags = extractFilteredListField(gameData, "tags", "language", "eng", "name");

                Integer metacriticScore = (Integer) gameData.getOrDefault("metacritic", 0);
                Boolean tba = (Boolean) gameData.getOrDefault("tba", false);
                String lastUpdateDate = getOrDefault(gameData, "updated", "N/A");
                Integer achievementsCount = (Integer) gameData.getOrDefault("achievements_count", 0);
                String metacriticURL = getOrDefault(gameData, "metacritic_url", "N/A");

                Game game = new Game();
                game.setTitle(title);
                game.setReleaseYear(releaseYear);
                game.setDescription(description);
                game.setEsrbRating(esrbRating);
                game.setImage(image);
                game.setWebsite(website);
                game.setGenre(genres);
                game.setDevelopers(developers);
                game.setPublishers(publishers);
                game.setPlatforms(platforms);
                game.setPCrequirements(pcRequirements);
                game.setStores(stores);
                game.setTags(tags);
                game.setMetacriticScore(metacriticScore);
                game.setTba(tba);
                game.setLastUpdateDate(lastUpdateDate);
                game.setAchievementsCount(achievementsCount);
                game.setMetacriticURL(metacriticURL);

                return ResponseEntity.ok(game); // Returneaza obiectul Game.
            } else {
                return ResponseEntity.status(response.getStatusCode()).body("Error fetching game details.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching game details."); // Eroare generica.
        }
    }

    // Metoda pentru extragerea unei valori dintr-o mapa.
    private String getStringValue(Map<String, Object> data, String key, String defaultValue) {
        return Optional.ofNullable(data.get(key))
                .map(Object::toString)
                .orElse(defaultValue);
    }

    // Extrage valori dintr-o lista simpla de obiecte.
    private String extractListField(Map<String, Object> gameData, String field, String subField) {
        if (gameData.containsKey(field)) {
            List<Map<String, Object>> items = (List<Map<String, Object>>) gameData.get(field);
            if (items.isEmpty()) {
                return "N/A";
            }
            return items.stream()
                    .map(item -> getStringValue(item, subField, "Unknown"))
                    .collect(Collectors.joining(", "));
        }
        return "N/A";
    }

    // Extrage valori dintr-o lista de obiecte ce contin campuri nested.
    private String extractNestedListField(Map<String, Object> gameData, String field, String nestedField, String subField) {
        if (gameData.containsKey(field)) {
            return ((List<Map<String, Object>>) gameData.get(field)).stream()
                    .map(item -> {
                        Map<String, Object> nested = (Map<String, Object>) item.get(nestedField);
                        return getStringValue(nested, subField, "Unknown");
                    })
                    .collect(Collectors.joining(", "));
        }
        return "N/A";
    }

    // Extrage valori filtrate pe baza unui camp specific.
    private String extractFilteredListField(Map<String, Object> gameData, String field, String filterField, String filterValue, String subField) {
        if (gameData.containsKey(field)) {
            return ((List<Map<String, Object>>) gameData.get(field)).stream()
                    .filter(item -> filterValue.equals(item.get(filterField)))
                    .map(item -> getStringValue(item, subField, "Unknown"))
                    .collect(Collectors.joining(", "));
        }
        return "N/A";
    }

    // Extrage cerinte PC din datele platformei.
    private String extractPCRequirements(Map<String, Object> gameData) {
        if (gameData.containsKey("platforms")) {
            return ((List<Map<String, Object>>) gameData.get("platforms")).stream()
                    .filter(platform -> {
                        Map<String, Object> platformData = (Map<String, Object>) platform.get("platform");
                        return platformData != null && "pc".equals(platformData.get("slug"));
                    })
                    .map(platform -> {
                        Map<String, Object> requirements = (Map<String, Object>) platform.get("requirements");
                        return getStringValue(requirements, "minimum", "N/A");
                    })
                    .findFirst()
                    .orElse("N/A");
        }
        return "N/A";
    }

    // Obtine valoarea unui camp sau returneaza un fallback.
    private String getOrDefault(Map<String, Object> data, String key, String defaultValue) {
        return Optional.ofNullable(data.get(key))
                .map(Object::toString)
                .filter(value -> !value.isEmpty())
                .orElse(defaultValue);
    }

    // Obtine o valoare dintr-un camp nested sau returneaza fallback.
    private String getNestedOrDefault(Map<String, Object> data, String key, String nestedKey, String defaultValue) {
        if (data.containsKey(key)) {
            Map<String, Object> nested = (Map<String, Object>) data.get(key);
            return nested != null ? getOrDefault(nested, nestedKey, defaultValue) : defaultValue;
        }
        return defaultValue;
    }
}
