/** Controller pentru autentificare. Acest controller gestioneaza operatiunile legate de autentificare si inregistrare a utilizatorilor.
 * @author Dochia Dimitrie Razvan
 * @version 12 Ianuarie 2024
 */

package com.dochia.gestiuneColectieJocuri.controllers;

import com.dochia.gestiuneColectieJocuri.models.Users;
import com.dochia.gestiuneColectieJocuri.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.regex.Pattern;

@Controller
public class AuthController {

    @Autowired
    private UserService userService; // Injectarea serviciului pentru operatiuni legate de utilizatori.

    // Verifica daca un email are un format valid.
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"; // Expresia regex pentru validare email.
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches(); // Returneaza true daca email-ul este valid.
    }

    // Endpoint pentru procesarea inregistrarii utilizatorilor.
    @PostMapping("/signup")
    public String signup(@ModelAttribute Users user, RedirectAttributes redirectAttributes) {
        try {
            // Verifica daca toate campurile sunt completate.
            if (user.getUsername() == null || user.getUsername().trim().isEmpty() ||
                    user.getEmail() == null || user.getEmail().trim().isEmpty() ||
                    user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("signupError", "All fields must be completed.");
                redirectAttributes.addFlashAttribute("signupInput", user);
                redirectAttributes.addFlashAttribute("showSignupModal", true);
                return "redirect:/";
            }

            // Verifica daca email-ul are un format valid.
            if (!isValidEmail(user.getEmail())) {
                redirectAttributes.addFlashAttribute("signupError", "Invalid email format. Please enter a valid email.");
                redirectAttributes.addFlashAttribute("signupInput", user);
                redirectAttributes.addFlashAttribute("showSignupModal", true);
                return "redirect:/";
            }

            // Verifica daca numele de utilizator este deja folosit.
            if (userService.findByUsername(user.getUsername()) != null) {
                redirectAttributes.addFlashAttribute("signupError", "Username is already in use.");
                redirectAttributes.addFlashAttribute("signupInput", user);
                redirectAttributes.addFlashAttribute("showSignupModal", true);
                return "redirect:/";
            }

            // Verifica daca email-ul este deja folosit.
            if (userService.findByEmail(user.getEmail()) != null) {
                redirectAttributes.addFlashAttribute("signupError", "Email is already in use.");
                redirectAttributes.addFlashAttribute("signupInput", user);
                redirectAttributes.addFlashAttribute("showSignupModal", true);
                return "redirect:/";
            }

            // Cripteaza parola si salveaza utilizatorul in baza de date.
            user.setPassword(DigestUtils.sha256Hex(user.getPassword()));
            userService.saveUser(user);
        } catch (Exception e) {
            // Gestionarea erorilor generale.
            redirectAttributes.addFlashAttribute("signupError", "An error occurred during signup. Please try again.");
            redirectAttributes.addFlashAttribute("signupInput", user);
            redirectAttributes.addFlashAttribute("showSignupModal", true);
            return "redirect:/";
        }

        // Daca inregistrarea este reusita, afiseaza un mesaj de succes.
        redirectAttributes.addFlashAttribute("signupSuccess", "Account created successfully! Please log in.");
        redirectAttributes.addFlashAttribute("showSignupModal", true);
        return "redirect:/";
    }

    // Endpoint pentru accesarea colectiei de jocuri a utilizatorului autentificat.
    @GetMapping("/games")
    public String yourCollection(HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("user") == null) { // Verifica daca utilizatorul este autentificat.
            redirectAttributes.addFlashAttribute("loginError", "You need to be logged in to access your collection.");
            redirectAttributes.addFlashAttribute("showLoginModal", true);

            session.setAttribute("redirectAfterLogin", "/games"); // Salveaza URL-ul pentru redirectionare dupa autentificare.
            return "redirect:/";
        }

        return "forward:/games/list"; // Redirectioneaza catre lista de jocuri.
    }

    // Endpoint pentru procesarea autentificarii utilizatorului.
    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            // Verifica daca toate campurile sunt completate.
            if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("loginError", "All fields must be completed.");
                redirectAttributes.addFlashAttribute("loginInput", username);
                redirectAttributes.addFlashAttribute("showLoginModal", true);
                return "redirect:/";
            }

            Users user;
            if (username.contains("@")) {
                user = userService.findByEmail(username); // Cauta utilizatorul dupa email.
            } else {
                user = userService.findByUsername(username); // Cauta utilizatorul dupa username.
            }

            // Verifica daca utilizatorul exista in baza de date.
            if (user == null) {
                redirectAttributes.addFlashAttribute("loginError", username.contains("@") ?
                        "The email is incorrect." : "The username is incorrect.");
                redirectAttributes.addFlashAttribute("loginInput", username);
                redirectAttributes.addFlashAttribute("showLoginModal", true);
                return "redirect:/";
            }

            // Verifica daca parola este corecta.
            if (!user.getPassword().equals(DigestUtils.sha256Hex(password))) {
                redirectAttributes.addFlashAttribute("loginError", "The password is incorrect.");
                redirectAttributes.addFlashAttribute("loginInput", username);
                redirectAttributes.addFlashAttribute("showLoginModal", true);
                return "redirect:/";
            }

            session.setAttribute("user", user); // Salveaza utilizatorul in sesiune.

            String redirectUrl = (String) session.getAttribute("redirectAfterLogin");
            if (redirectUrl == null || redirectUrl.isEmpty()) {
                redirectUrl = "/"; // Redirectioneaza la pagina principala daca nu exista alta redirectionare.
            }
            session.removeAttribute("redirectAfterLogin");
            return "redirect:" + redirectUrl;

        } catch (Exception e) {
            // Gestionarea erorilor in timpul autentificarii.
            redirectAttributes.addFlashAttribute("loginError", "An error occurred during login. Please try again.");
            redirectAttributes.addFlashAttribute("loginInput", username);
            redirectAttributes.addFlashAttribute("showLoginModal", true);
            return "redirect:/";
        }
    }

    // Endpoint pentru delogarea utilizatorului.
    @GetMapping("/signout")
    public String signout(HttpSession session) {
        session.invalidate(); // Invalideaza sesiunea curenta.
        return "redirect:/"; // Redirectioneaza la pagina principala.
    }
}
