/** Repository pentru user. Ofera metode pentru interactiunea cu baza de date pentru entitatea Users.
 * @author Dochia Dimitrie Razvan
 * @version 12 Ianuarie 2024
 */

package com.dochia.gestiuneColectieJocuri.repositories;

import com.dochia.gestiuneColectieJocuri.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long> {

    // Metoda pentru a gasi un utilizator dupa numele de utilizator
    Users findByUsername(String username);

    // Metoda pentru a gasi un utilizator dupa adresa de email
    Users findByEmail(String email);
}
