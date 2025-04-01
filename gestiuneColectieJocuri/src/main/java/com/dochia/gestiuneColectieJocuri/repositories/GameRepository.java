/** Repository pentru joc
 * @author Dochia Dimitrie Razvan
 * @version 12 Ianuarie 2024
 */

package com.dochia.gestiuneColectieJocuri.repositories;

import com.dochia.gestiuneColectieJocuri.models.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {
    // Metoda pentru a gasi un joc in baza de date bazat pe titlu si anul lansarii
    Game findByTitleAndReleaseYear(String title, String releaseYear);
}

