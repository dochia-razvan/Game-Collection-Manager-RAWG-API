/** Repository pentru userGame. Ofera metode pentru interactiunea cu baza de date pentru entitatea UserGame.
 * @author Dochia Dimitrie Razvan
 * @version 12 Ianuarie 2024
 */

package com.dochia.gestiuneColectieJocuri.repositories;

import com.dochia.gestiuneColectieJocuri.models.Game;
import com.dochia.gestiuneColectieJocuri.models.UserGame;
import com.dochia.gestiuneColectieJocuri.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserGameRepository extends JpaRepository<UserGame, Long> {

    // Metoda pentru a gasi toate inregistrarile UserGame asociate unui utilizator
    List<UserGame> findByUser(Users user);

    // Metoda pentru a gasi o inregistrare UserGame specifica bazata pe utilizator si joc
    Optional<UserGame> findByUserAndGame(Users user, Game game);
}
