/** Controller pentru pagina home
 * Acest controller gestioneaza cererile HTTP pentru pagina principala (home).
 * Adauga informatii in model despre starea de autentificare a utilizatorului.
 * @author Dochia Dimitrie Razvan
 * @version 12 Ianuarie 2024
 */

package com.dochia.gestiuneColectieJocuri.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // Indica faptul ca aceasta clasa este un controller Spring.
public class HomeController {

    // Gestionarea cererilor GET pentru ruta "/".
    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        // Verifica daca utilizatorul este autentificat, verificand daca exista un atribut "user" in sesiune.
        boolean isLoggedIn = session.getAttribute("user") != null;
        // Adauga informatia despre starea de autentificare in model, pentru a fi folosita in view.
        model.addAttribute("isLoggedIn", isLoggedIn);
        // Returneaza numele paginii "home", care va fi redat de framework-ul de view.
        return "home";
    }
}
