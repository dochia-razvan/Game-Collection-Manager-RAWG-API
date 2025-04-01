/** Controller pentru gestionarea globala a erorilor. Acest controller intercepteaza si gestioneaza exceptiile aruncate in cadrul aplicatiei,
 * oferind raspunsuri HTTP adecvate.
 * @author Dochia Dimitrie Razvan
 * @version 12 Ianuarie 2024
 */

package com.dochia.gestiuneColectieJocuri.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Indica faptul ca aceasta clasa ofera sfaturi globale pentru toate controalele REST.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Metoda pentru gestionarea exceptiilor de tip IllegalArgumentException.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        // Returneaza un raspuns cu statusul 400 (Bad Request) si mesajul exceptiei.
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    // Metoda pentru gestionarea tuturor celorlalte exceptii care nu sunt specificate.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        // Afiseaza detalii despre exceptie in consola pentru depanare.
        ex.printStackTrace();
        // Returneaza un raspuns cu statusul 500 (Internal Server Error) si un mesaj generic de eroare.
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
    }
}
