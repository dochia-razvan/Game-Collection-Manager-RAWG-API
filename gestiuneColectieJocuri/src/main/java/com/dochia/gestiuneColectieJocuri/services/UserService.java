/** Service pentru gestionarea operațiunilor legate de utilizatori
 * @author Dochia Dimitrie Razvan
 * @version 12 Ianuarie 2024
 */

package com.dochia.gestiuneColectieJocuri.services;

import com.dochia.gestiuneColectieJocuri.models.Users;
import com.dochia.gestiuneColectieJocuri.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service // Marcheaza aceasta clasa ca un serviciu in cadrul arhitecturii Spring
public class UserService {

    @Autowired // Injecteaza automat dependinta UserRepository
    private UserRepository userRepository;

    // Metoda pentru a salva un utilizator in baza de date
    public void saveUser(Users user) {
        userRepository.save(user); // Salveaza obiectul user in baza de date
    }

    // Metoda pentru a gasi un utilizator dupa numele de utilizator
    public Users findByUsername(String username) {
        return userRepository.findByUsername(username); // Returneaza utilizatorul corespunzator
    }

    // Metoda pentru a gasi un utilizator dupa adresa de email
    public Users findByEmail(String email) {
        return userRepository.findByEmail(email); // Returneaza utilizatorul corespunzator
    }
}

