/** Javascript
 * @author Dochia Dimitrie Razvan
 * @version 12 Ianuarie 2024
 */

// Functie pentru a reseta modalul, stergand valorile introduse si mesajele de eroare/succes
function resetModal(modal) {
    // Selecteaza toate input-urile din interiorul modalului pentru a le reseta
    const inputs = modal.querySelectorAll('input');

    // Selecteaza elementul ce contine mesajul de eroare, daca exista
    const errorMessage = modal.querySelector('.error');

    // Selecteaza elementul ce contine mesajul de succes, daca exista
    const successMessage = modal.querySelector('.success');

    // Reseteaza valoarea fiecarui input din modal la un string gol
    inputs.forEach(input => input.value = '');

    // Daca exista un mesaj de eroare, il sterge
    if (errorMessage) errorMessage.innerHTML = '';

    // Daca exista un mesaj de succes, il sterge
    if (successMessage) successMessage.innerHTML = '';
}


function enableEdit(icon) { // Functie pentru activarea editarii unui camp
    const parentCell = icon.parentElement; // Selecteaza celula parinte a iconului
    const inputOrSelect = parentCell.querySelector('input, select'); // Cauta un input sau un select in celula parinte
    const saveButton = parentCell.querySelector('.save-btn'); // Gaseste butonul "Save" din celula parinte

    if (inputOrSelect) { // Daca exista un input sau un select
        inputOrSelect.disabled = false; // Activeaza campul pentru editare
        inputOrSelect.focus(); // Mutarea focusului pe camp pentru editare imediata
    }

    if (saveButton) { // Daca exista un buton de tip "Save"
        icon.classList.add('hidden'); // Ascunde iconul de editare
        saveButton.classList.remove('hidden'); // Afiseaza butonul "Save" pentru a confirma editarea
    }
}


async function saveEdit(button, field, userGameId) { // Functie asincrona pentru salvarea unei editari a unui camp specific
    const parentCell = button.parentElement; // Selecteaza celula parinte a butonului
    const input = parentCell.querySelector('input, select'); // Gaseste campul de input sau select din celula parinte
    const editButton = parentCell.querySelector('.edit-icon'); // Gaseste icon-ul de editare din celula parinte
    const updatedValue = input.tagName === "SELECT" ? input.value : input.value.trim() || "N/A"; // Determina valoarea actualizata a campului
    const gameRow = button.closest('tr'); // Gaseste randul asociat din tabel
    const titleCell = gameRow.querySelector('td:nth-child(3)'); // Gaseste celula cu titlul jocului
    const title = titleCell ? titleCell.querySelector('span').textContent : 'Unknown Game'; // Extrage titlul jocului sau returneaza un fallback

    const fieldMapping = { // Mapare pentru a lega campurile din interfata cu cele din backend
        "Preferred Store": "stores",
        "Hours Played": "hoursPlayed",
        "Personal Rating": "personalRating",
        "Review": "review",
        "Favorite": "favorite",
        "Completion Status": "completionStatus",
        "Achievements Count": "userAchievementsCount"
    };

    const backendField = fieldMapping[field]; // Determina numele campului folosit in backend

    if (!backendField) { // Verifica daca campul este valid
        localStorage.setItem("popupMessage", `Error: Unknown field '${field}'`); // Mesaj de eroare daca campul nu este recunoscut
        localStorage.setItem("popupType", "error");
        window.location.reload(); // Reincarca pagina pentru a reveni la starea initiala
        return;
    }

    try {
        const response = await fetch(`/games/update`, { // Trimite o cerere POST la backend pentru a actualiza campul
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                id: userGameId, // ID-ul jocului asociat
                field: backendField, // Campul care trebuie actualizat
                value: updatedValue, // Noua valoare a campului
            }),
        });

        if (!response.ok) { // Daca raspunsul nu este OK, arunca o eroare
            const errorMessage = await response.text();
            throw new Error(errorMessage || `Failed to update '${field}' for game ${title}.`);
        }

        input.disabled = true; // Dezactiveaza inputul pentru a preveni editari suplimentare
        button.classList.add("hidden"); // Ascunde butonul de salvare
        editButton.classList.remove("hidden"); // Afiseaza iconul de editare

        localStorage.setItem("popupMessage", `Successfully updated '${field}' for game ${title}`); // Seteaza un mesaj de succes
        localStorage.setItem("popupType", "success");
        window.location.reload(); // Reincarca pagina pentru a reflecta modificarile
    } catch (error) { // Captureaza si gestioneaza erorile
        localStorage.setItem("popupMessage", `Error updating '${field}' for game ${title}: ${error.message}`); // Seteaza un mesaj de eroare
        localStorage.setItem("popupType", "error");
        window.location.reload(); // Reincarca pagina pentru a anula modificarile
    }
}


async function searchGames() { // Functie asincrona pentru cautarea jocurilor in API-ul RAWG
    const query = document.getElementById('searchGameInput').value; // Preia valoarea introdusa in input-ul de cautare
    const resultsContainer = document.getElementById('searchResults'); // Containerul unde vor fi afisate rezultatele
    resultsContainer.innerHTML = ''; // Goleste rezultatele anterioare din container

    if (!query.trim()) return; // Daca input-ul este gol sau contine doar spatii, nu efectueaza cautarea

    try {
        const response = await fetch(`/api/rawg/search?query=${query}`); // Trimite o cerere catre endpoint-ul de cautare RAWG API
        if (!response.ok) throw new Error('Failed to fetch games from RAWG API'); // Verifica daca raspunsul este valid
        const data = await response.json(); // Parseaza raspunsul in format JSON
        resultsContainer.innerHTML = ''; // Asigura ca containerul de rezultate este gol

        if (data.results && data.results.length > 0) { // Verifica daca sunt rezultate in raspunsul API
            data.results.forEach(game => { // Parcurge fiecare joc gasit in rezultate
                const imageUrl = game.background_image || 'https://treesa.org/wp-content/uploads/2017/02/300px-No_image_available.svg.png'; // Daca imaginea jocului lipseste, seteaza o imagine implicita

                const gameElement = document.createElement('div'); // Creeaza un div pentru fiecare joc
                gameElement.className = 'game-result'; // Adauga o clasa pentru stilizare
                gameElement.innerHTML = ` 
                    <img src="${imageUrl}" alt="${game.name}" width="100"> <!-- Adauga imaginea jocului -->
                    <p>${game.name}</p> <!-- Afiseaza numele jocului -->
                    <small>(${game.released || 'Unknown'})</small> <!-- Afiseaza data lansarii sau un fallback -->
                    <button onclick="fetchGameDetailsAndAddGame(${game.id}, '${game.name}')">Add</button> <!-- Buton pentru a adauga jocul -->
                `;
                resultsContainer.appendChild(gameElement); // Adauga elementul jocului in containerul de rezultate
            });
        } else {
            resultsContainer.innerHTML = '<p>No games found. Please try another search.</p>'; // Mesaj afisat daca nu sunt jocuri gasite
        }
    } catch (error) {
        resultsContainer.innerHTML = '<p>Error fetching games. Please try again later.</p>'; // Mesaj afisat in cazul unei erori
    }
}

async function fetchGameDetailsAndAddGame(gameId, gameTitle) { // Functie asincrona pentru a prelua detaliile unui joc si a-l adauga in colectie
    try {
        const response = await fetch(`/api/rawg/details/${gameId}`); // Trimite o cerere pentru a obtine detalii despre joc din API-ul RAWG
        if (!response.ok) { // Verifica daca raspunsul este valid
            throw new Error(`Failed to fetch details for game ${gameTitle}`); // Arunca o eroare daca cererea a esuat
        }

        const gameData = await response.json(); // Parseaza raspunsul in format JSON

        const response2 = await fetch('/games/add', { // Trimite o cerere POST pentru a adauga jocul in colectia utilizatorului
            method: 'POST', // Metoda HTTP POST pentru adaugare
            headers: { 'Content-Type': 'application/json' }, // Seteaza tipul de continut pentru JSON
            body: JSON.stringify(gameData), // Trimite detaliile jocului in corpul cererii
        });

        if (response2.ok) { // Verifica daca cererea de adaugare a fost realizata cu succes
            document.getElementById('addGameModal').style.display = 'none'; // Inchide modalul de adaugare
            localStorage.setItem("popupMessage", `Game ${gameData.title} added successfully!`); // Salveaza un mesaj de succes in localStorage
            localStorage.setItem("popupType", "success"); // Seteaza tipul mesajului ca fiind de succes
            window.location.reload(); // Reincarca pagina pentru a reflecta modificarile
        } else {
            throw new Error(`There was an error adding the game ${gameData.title}. Please try again later.`); // Arunca o eroare daca adaugarea a esuat
        }
    } catch (error) { // Prinde orice eroare aparuta pe parcurs
        localStorage.setItem("popupMessage", `${error.message}`); // Salveaza mesajul de eroare in localStorage
        localStorage.setItem("popupType", "error"); // Seteaza tipul mesajului ca fiind de eroare
        window.location.reload(); // Reincarca pagina pentru a afisa eroarea utilizatorului
    }
}


async function liveSearchGames() { // Functie asincrona pentru cautare live a jocurilor
    const query = document.getElementById("searchInput").value.trim(); // Obtine valoarea introdusa in campul de cautare si elimina spatiile
    const resultsContainer = document.getElementById("searchResultsContainer"); // Selecteaza containerul unde vor fi afisate rezultatele

    if (!query) { // Verifica daca campul de cautare este gol
        resultsContainer.innerHTML = ""; // Goleste continutul containerului de rezultate
        resultsContainer.classList.remove("visible"); // Ascunde containerul daca nu exista o cautare
        return; // Iese din functie daca nu exista termen de cautare
    }

    try {
        const response = await fetch(`/api/rawg/search?query=${query}`); // Trimite o cerere la API pentru a cauta jocuri pe baza termenului
        if (!response.ok) throw new Error("Failed to fetch search results."); // Verifica daca raspunsul este valid

        const data = await response.json(); // Parseaza raspunsul in format JSON
        const games = data.results; // Extrage lista de rezultate

        resultsContainer.innerHTML = ""; // Goleste rezultatele anterioare

        if (games && games.length > 0) { // Verifica daca exista rezultate in raspuns
            games.forEach((game) => { // Parcurge fiecare joc din rezultate
                const gameElement = document.createElement("div"); // Creeaza un element HTML pentru fiecare joc
                gameElement.className = "game-result"; // Adauga o clasa pentru stilizare
                gameElement.innerHTML = ` 
                    <img src="${game.background_image || 'https://treesa.org/wp-content/uploads/2017/02/300px-No_image_available.svg.png'}" alt="${game.name}"> 
                    <p>${game.name}</p>
                    <small>(${game.released || 'Unknown'})</small>
                `; // Adauga imaginea, numele si data lansarii jocului

                gameElement.addEventListener("click", () => { // Adauga un event listener pentru click pe fiecare rezultat
                    addGameAndRedirect(game.id, game.name); // Apeleaza functia pentru adaugarea jocului si redirectionare
                });

                resultsContainer.appendChild(gameElement); // Adauga elementul jocului in containerul de rezultate
            });

            resultsContainer.classList.add("visible"); // Afiseaza containerul daca exista rezultate
        } else {
            resultsContainer.innerHTML = "<p>No results found</p>"; // Afiseaza un mesaj daca nu exista rezultate
            resultsContainer.classList.add("visible"); // Afiseaza containerul chiar daca nu exista rezultate
        }
    } catch (error) { // In cazul unei erori
        resultsContainer.innerHTML = "<p>Error fetching search results.</p>"; // Afiseaza un mesaj de eroare in container
        resultsContainer.classList.add("visible"); // Asigura afisarea containerului chiar si in caz de eroare
    }
}

async function addGameAndRedirect(gameId, gameTitle) { // Functie asincrona pentru a adauga un joc si a redirectiona utilizatorul
    try {
        const response = await fetch(`/api/rawg/details/${gameId}`); // Trimite o cerere pentru detaliile jocului bazat pe ID-ul acestuia
        if (!response.ok) throw new Error(`Failed to fetch details for game ${gameTitle}`); // Arunca o eroare daca raspunsul nu este valid

        const gameData = await response.json(); // Parseaza datele jocului din raspunsul API

        const addGameResponse = await fetch("/games/addToDatabase", { // Trimite o cerere POST pentru a adauga jocul in baza de date
            method: "POST",
            headers: { "Content-Type": "application/json" }, // Specifica tipul de continut ca JSON
            body: JSON.stringify(gameData), // Trimite datele jocului in corpul cererii
        });

        if (!addGameResponse.ok) { // Verifica daca cererea POST a avut succes
            throw new Error(`Failed to add game ${gameTitle} to the database.`); // Arunca o eroare daca jocul nu a fost adaugat
        }

        const addedGame = await addGameResponse.json(); // Parseaza raspunsul pentru a obtine detalii despre jocul adaugat

        window.location.href = `/games/details/${addedGame.id}`; // Redirectioneaza utilizatorul la pagina de detalii a jocului
    } catch (error) { // In cazul unei erori
        localStorage.setItem("popupMessage", `Please try again later!`); // Stocheaza un mesaj de eroare in localStorage
        localStorage.setItem("popupType", "error"); // Marcheaza tipul mesajului ca eroare
        window.location.reload(); // Reincarca pagina pentru a actualiza interfata
    }
}


function handleDeleteClick(element) { // Functie pentru a gestiona click-ul pe butonul de stergere al unui joc
    const gameTitle = element.getAttribute("data-title"); // Obtine titlul jocului din atributul 'data-title'
    const userGameId = element.getAttribute("data-id"); // Obtine ID-ul jocului din atributul 'data-id'

    if (!userGameId || userGameId === "null") { // Verifica daca ID-ul jocului este valid
        return; // Opreste executia daca ID-ul nu este valid
    }

    currentGameId = userGameId; // Stocheaza ID-ul jocului curent intr-o variabila globala

    openDeleteModal(gameTitle, userGameId); // Deschide modalul de confirmare a stergerii, transmitand titlul si ID-ul jocului
}

function showPopup(message, type = "success") { // Functie pentru a afisa un popup informativ
    const popup = document.getElementById("popupContainer"); // Obtine containerul popup-ului
    const popupMessage = document.getElementById("popupMessage"); // Obtine elementul unde va fi afisat mesajul

    popupMessage.textContent = message; // Afiseaza mesajul primit ca argument
    popup.className = `popup show ${type}`; // Seteaza clasa popup-ului pentru a indica tipul mesajului (ex: succes, eroare)

    setTimeout(() => { // Dupa 5 secunde
        popup.classList.remove("show"); // Elimina clasa de afisare a popup-ului
    }, 5000);

    const closeButton = document.getElementById("popupClose"); // Obtine butonul de inchidere al popup-ului
    if (closeButton) { // Verifica daca butonul exista
        closeButton.onclick = () => { // Adauga un handler pentru click
            popup.classList.remove("show"); // Ascunde popup-ul cand se apasa butonul de inchidere
        };
    }
}


async function toggleFavorite(favoriteElement) { // Functie asincrona pentru a comuta starea de "favorit" al unui joc
    const userGameId = favoriteElement.getAttribute("data-id"); // Obtine ID-ul jocului din atributul 'data-id'
    const isFavorite = favoriteElement.src.includes("filled-star.png"); // Verifica daca jocul este marcat ca favorit pe baza imaginii curente

    try {
        const response = await fetch(`/games/updateFavorite`, { // Trimite o cerere POST pentru actualizarea starii de "favorit"
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                id: userGameId, // ID-ul jocului care trebuie actualizat
                favorite: !isFavorite // Starea opusa fata de cea curenta
            }),
        });

        if (!response.ok) { // Verifica daca raspunsul este valid
            const error = await response.text(); // Extrage mesajul de eroare din raspuns
            throw new Error(error || "Failed to update favorite status."); // Arunca o eroare daca actualizarea a esuat
        }

        favoriteElement.src = isFavorite // Actualizeaza imaginea in functie de noua stare de "favorit"
            ? "/photos/empty-star.png" // Daca nu mai este favorit, arata steaua goala
            : "/photos/filled-star.png"; // Daca devine favorit, arata steaua plina

        localStorage.setItem("popupMessage", `Favorite status updated successfully!`); // Stocheaza un mesaj de succes in LocalStorage
        localStorage.setItem("popupType", "success"); // Seteaza tipul popup-ului ca fiind "success"
        window.location.reload(); // Reincarca pagina pentru a reflecta modificarile
    } catch (error) { // Gestioneaza erorile
        localStorage.setItem("popupMessage", `Error: ${error.message}`); // Stocheaza mesajul de eroare in LocalStorage
        localStorage.setItem("popupType", "error"); // Seteaza tipul popup-ului ca fiind "error"
        window.location.reload(); // Reincarca pagina pentru a afisa eroarea
    }
}

function toggleFavoriteGameDetails(icon) { // Functie pentru a comuta starea de "favorit" din detaliile unui joc
    const isFavorite = icon.src.includes('filled-star.png'); // Verifica daca jocul este favorit pe baza imaginii
    icon.src = isFavorite ? '/photos/empty-star.png' : '/photos/filled-star.png'; // Schimba imaginea in functie de starea de "favorit"

    icon.style.width = '35px'; // Seteaza latimea imaginii la 35px
    icon.style.height = '35px'; // Seteaza inaltimea imaginii la 35px
}

async function saveUserGame() { // Functie asincrona pentru a salva modificarile facute pe un joc specific al utilizatorului
    const userGameContainer = document.querySelector('.user-game-container'); // Selecteaza containerul jocului utilizatorului

    const userGameId = userGameContainer.getAttribute('data-id'); // Obtine ID-ul jocului din atributul 'data-id'

    // Selecteaza diferite elemente editabile din interfata pentru colectarea datelor
    const preferredStoreDropdown = userGameContainer.querySelector('.editable-input');
    const selectedStore = preferredStoreDropdown ? preferredStoreDropdown.value : 'N/A'; // Daca nu este selectat niciun magazin, returneaza 'N/A'

    const hoursPlayedInput = userGameContainer.querySelector('input[type="number"][min="0"]'); // Selecteaza campul pentru orele jucate
    const personalRatingInput = userGameContainer.querySelector('input[type="number"][min="1"]'); // Selecteaza campul pentru ratingul personal
    const reviewTextarea = userGameContainer.querySelector('textarea'); // Selecteaza campul pentru review-ul utilizatorului
    const favoriteIcon = userGameContainer.querySelector('.favorite-icon2'); // Selecteaza pictograma "favorit"
    const completionStatusSelect = userGameContainer.querySelector('select:not(.editable-input)'); // Selecteaza campul pentru statusul completarii
    const achievementsInput = userGameContainer.querySelector('input[min="0"]'); // Selecteaza campul pentru numarul de realizari obtinute

    // Creeaza un obiect care contine toate campurile si valorile lor curente
    const fields = {
        stores: selectedStore, // Magazinul selectat
        hoursPlayed: hoursPlayedInput ? parseFloat(hoursPlayedInput.value) || 0 : 0, // Orele jucate (0 daca nu exista valoare)
        personalRating: personalRatingInput ? parseFloat(personalRatingInput.value) || 0 : 0, // Ratingul personal (0 daca nu exista valoare)
        review: reviewTextarea ? reviewTextarea.value.trim() : 'N/A', // Review-ul (trimite 'N/A' daca e gol)
        favorite: favoriteIcon ? favoriteIcon.src.includes('filled-star.png') : false, // Statusul de "favorit"
        completionStatus: completionStatusSelect ? completionStatusSelect.value : 'Not Started', // Statusul completarii (default: 'Not Started')
        userAchievementsCount: achievementsInput ? parseInt(achievementsInput.value, 10) || 0 : 0, // Numarul de realizari obtinute (0 daca nu exista valoare)
    };

    try {
        // Itereaza peste toate campurile si trimite fiecare modificare catre server
        for (const [field, value] of Object.entries(fields)) {
            const response = await fetch('/games/update', { // Trimite o cerere POST pentru fiecare camp
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ id: userGameId, field, value }), // Datele trimise includ ID-ul jocului, numele campului si valoarea acestuia
            });

            if (!response.ok) { // Verifica daca raspunsul este valid
                const error = await response.text(); // Extrage mesajul de eroare din raspuns
                throw new Error(error); // Arunca o eroare daca actualizarea a esuat
            }
        }
        // Daca toate actualizarile au reusit, salveaza un mesaj de succes si reincarca pagina
        localStorage.setItem("popupMessage", `Game updated successfully!`);
        localStorage.setItem("popupType", "success");
        window.location.reload(); // Reincarca pagina pentru a reflecta modificarile
    } catch (err) {
        // Gestioneaza erorile si afiseaza un mesaj corespunzator in cazul unei probleme
        localStorage.setItem("popupMessage", `Error: ${err.message}`);
        localStorage.setItem("popupType", "error");
        window.location.reload();
    }
}


async function handleAddGame(button) { // Functie asincrona pentru a adauga un joc in colectia utilizatorului
    const gameId = button.getAttribute("data-id"); // Obtine ID-ul jocului din atributul butonului
    const gameTitle = button.getAttribute("data-title"); // Obtine titlul jocului din atributul butonului

    try {
        const response = await fetch('/games/add2', { // Trimite o cerere POST catre server pentru a adauga jocul
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ id: gameId, title: gameTitle }), // Include ID-ul si titlul jocului in corpul cererii
        });

        if (!response.ok) { // Verifica daca raspunsul serverului este valid
            const errorMessage = await response.text(); // Extrage mesajul de eroare din raspuns, daca exista
            throw new Error(errorMessage || 'Failed to add game.'); // Arunca o eroare daca cererea a esuat
        }

        // Daca adaugarea a avut succes, seteaza un mesaj de succes in localStorage si reincarca pagina
        localStorage.setItem("popupMessage", `Game ${gameTitle} has been added to your collection!`);
        localStorage.setItem("popupType", "success");
        window.location.reload(); // Reincarca pagina pentru a reflecta modificarile
    } catch (error) {
        // In cazul unei erori, seteaza un mesaj corespunzator si reincarca pagina
        localStorage.setItem("popupMessage", `Error adding game ${gameTitle}: ${error.message}`);
        localStorage.setItem("popupType", "error");
        window.location.reload();
    }
}

function closeModal() { // Functie pentru a inchide modalul de stergere
    const deleteModal = document.getElementById("deleteModal"); // Selecteaza modalul de stergere
    deleteModal.style.display = "none"; // Ascunde modalul
}

function openDeleteModal(gameTitle, userGameId) { // Functie pentru a deschide modalul de stergere
    const deleteModal = document.getElementById("deleteModal"); // Selecteaza modalul de stergere
    const deleteModalMessage = document.getElementById("deleteModalMessage"); // Selecteaza mesajul din modal

    deleteModalMessage.textContent = `Are you sure you want to delete the game "${gameTitle}" from your collection?`; // Seteaza textul mesajului
    deleteModal.style.display = "block"; // Afiseaza modalul
}

function openSignup() { // Functie pentru a deschide modalul de inregistrare
    const loginModal = document.getElementById('loginModal'); // Selecteaza modalul de autentificare
    const signupModal = document.getElementById('signupModal'); // Selecteaza modalul de inregistrare

    if (loginModal) { // Daca modalul de autentificare este deschis
        loginModal.style.display = 'none'; // Inchide modalul de autentificare
    }

    if (signupModal) { // Daca modalul de inregistrare exista
        signupModal.style.display = 'block'; // Afiseaza modalul de inregistrare
    }
}

document.addEventListener('DOMContentLoaded', function () { // Asteapta incarcarea completa a DOM-ului pentru a initializa functionalitatile

    // Selecteaza elementele necesare pentru modalele de login, signup si adaugare de joc
    const loginModal = document.getElementById('loginModal');
    const signupModal = document.getElementById('signupModal');
    const addGameModal = document.getElementById('addGameModal');

    // Butoane principale pentru autentificare, inregistrare si adaugare joc
    const loginBtn = document.getElementById('loginBtn');
    const signupBtn = document.getElementById('signupBtn');
    const addGameBtn = document.getElementById('addGameBtn');
    const searchBtn = document.getElementById('searchBtn');

    // Butoane pentru inchiderea modalelor de login si signup
    const closeLogin = document.getElementById('closeLogin');
    const closeSignup = document.getElementById('closeSignup');

    // Link pentru deschiderea modalului de inregistrare din cel de login
    const openSignupLink = document.getElementById('openSignupLink');
    const successPopup = document.getElementById('successPopup');
    const closePopup = document.getElementById('closePopup');

    // Elemente pentru modalul de sign out
    const signOutBtn = document.getElementById('signOutBtn');
    const signOutModal = document.getElementById('signOutModal');
    const confirmSignOut = document.getElementById('confirmSignOut');
    const cancelSignOut = document.getElementById('cancelSignOut');
    const signOutWarning = document.getElementById('signOutWarning');

    // Butonul pentru colectia utilizatorului
    const yourCollectionButton = document.querySelector('.your-collection');

    // Elemente pentru modalul de stergere
    const deleteModal = document.getElementById('deleteModal');
    const deleteModalMessage = document.getElementById('deleteModalMessage');
    const confirmDeleteBtn = document.getElementById('confirmDeleteBtn');
    const cancelDeleteBtn = document.getElementById('cancelDeleteBtn');

    // Preia mesajele popup din localStorage
    const popupMessage = localStorage.getItem("popupMessage");
    const popupType = localStorage.getItem("popupType") || "success";

    // Elemente pentru bara de cautare
    const searchInput = document.getElementById('searchInput');
    const resultsContainer = document.getElementById('searchResultsContainer');

    let currentGameId = null; // Variabila globala pentru stocarea ID-ului jocului curent

    // Eveniment pentru a inchide modalele atunci cand utilizatorul face clic in afara lor
    window.addEventListener('click', (event) => {
        if (event.target === loginModal) { // Daca clicul este in afara modalului de login
            resetModal(loginModal);
            loginModal.style.display = 'none';
        }

        if (event.target === signupModal) { // Daca clicul este in afara modalului de inregistrare
            resetModal(signupModal);
            signupModal.style.display = 'none';
        }

        if (event.target === addGameModal) { // Daca clicul este in afara modalului de adaugare joc
            addGameModal.style.display = 'none';
            const searchInput = document.getElementById('searchGameInput');
            const resultsContainer = document.getElementById('searchResults');
            if (searchInput) searchInput.value = ''; // Reseteaza campul de cautare
            if (resultsContainer) resultsContainer.innerHTML = ''; // Goleste rezultatele cautarii
        }

        if (event.target === signOutModal) { // Daca clicul este in afara modalului de sign out
            signOutModal.style.display = 'none';
        }

        if (event.target === deleteModal) { // Daca clicul este in afara modalului de stergere
            deleteModal.style.display = 'none';
        }
    });

    // Afiseaza modalul pentru adaugarea unui joc
    if (addGameBtn) {
        addGameBtn.addEventListener('click', () => {
            addGameModal.style.display = 'block';
        });
    }

    // Configurarea evenimentelor pentru modalele de login si signup
    if (loginBtn && signupBtn) {
        loginBtn.addEventListener('click', () => {
            loginModal.style.display = 'block'; // Afiseaza modalul de login
        });

        signupBtn.addEventListener('click', () => {
            signupModal.style.display = 'block'; // Afiseaza modalul de inregistrare
        });

        closeLogin.addEventListener('click', () => {
            resetModal(loginModal);
            loginModal.style.display = 'none'; // Inchide modalul de login
        });

        closeSignup.addEventListener('click', () => {
            resetModal(signupModal);
            signupModal.style.display = 'none'; // Inchide modalul de inregistrare
        });

    }

    // Configurarea popup-urilor de succes
    if (closePopup && successPopup) {
        resetModal(signupModal);
        signupModal.style.display = 'none';
        closePopup.addEventListener('click', () => {
            successPopup.style.display = 'none'; // Inchide popup-ul de succes
        });

        setTimeout(() => {
            successPopup.style.opacity = '0'; // Ascunde popup-ul dupa 3 secunde
            setTimeout(() => {
                successPopup.style.display = 'none';
            }, 500);
        }, 3000);
    }

    // Configurarea linkului pentru a deschide modalul de inregistrare din cel de login
    if (openSignupLink) {
        openSignupLink.addEventListener('click', (event) => {
            event.preventDefault(); // Previne comportamentul implicit al linkului
            loginModal.style.display = 'none';
            signupModal.style.display = 'block';
        });
    }

    // Configurarea modalului de sign out
    if (signOutBtn) {
        signOutBtn.addEventListener('click', () => {
            if (window.location.pathname === '/games' || window.location.pathname.startsWith('/games/details')) {
                signOutWarning.style.display = 'block'; // Afiseaza avertizarea doar pe anumite pagini
            } else {
                signOutWarning.style.display = 'none';
            }

            signOutModal.style.display = 'block'; // Afiseaza modalul de sign out
        });
    }

    if (confirmSignOut) { // Daca utilizatorul confirma sign out
        confirmSignOut.addEventListener('click', () => {
            window.location.href = '/signout'; // Redirectioneaza catre sign out
        });
    }

    if (cancelSignOut) { // Inchide modalul de sign out fara a actiona
        cancelSignOut.addEventListener('click', () => {
            signOutModal.style.display = 'none';
        });
    }

    // Configurarea butonului pentru colectia utilizatorului
    if (yourCollectionButton) {
        yourCollectionButton.addEventListener('click', function (event) {
            event.preventDefault(); // Previne redirectionarea implicita
            window.location.href = '/games'; // Redirectioneaza catre colectia utilizatorului
        });
    }

    // Functie globala pentru a deschide modalul de stergere
    window.openDeleteModal = function (gameTitle, userGameId) {
        currentGameId = userGameId;
        const deleteModal = document.getElementById("deleteModal");
        const deleteModalMessage = document.getElementById("deleteModalMessage");

        deleteModalMessage.textContent = `Are you sure you want to delete the game "${gameTitle}" from your collection?`; // Seteaza mesajul din modal
        deleteModal.style.display = "block"; // Afiseaza modalul
    };

    // Confirma stergerea jocului
    if (confirmDeleteBtn) {
        confirmDeleteBtn.addEventListener("click", async () => {
            try {
                const response = await fetch(`/games/delete/${currentGameId}`, { method: "DELETE" });

                if (!response.ok) {
                    throw new Error("Failed to remove game from your collection."); // Arunca eroare daca stergerea nu a reusit
                }

                localStorage.setItem("popupMessage", "Game removed from your collection."); // Seteaza mesaj de succes
                localStorage.setItem("popupType", "success");
                window.location.reload(); // Reincarca pagina
            } catch (error) {
                localStorage.setItem("popupMessage", "Error: ${error.message}"); // Seteaza mesaj de eroare
                localStorage.setItem("popupType", "error");
                window.location.reload();
            } finally {
                closeModal(); // Inchide modalul
            }
        });
    }

    if (cancelDeleteBtn) { // Inchide modalul de stergere fara a actiona
        cancelDeleteBtn.addEventListener('click', () => {
            if (deleteModal) {
                deleteModal.style.display = 'none';
            }
        });
    }

    // Afiseaza popup-ul daca exista un mesaj in localStorage
    if (popupMessage) {
        showPopup(popupMessage, popupType);
        localStorage.removeItem("popupMessage");
        localStorage.removeItem("popupType");
    }

    // Inchide bara de cautare cand utilizatorul face clic in afara acesteia
    document.addEventListener('click', (event) => {
        const isClickInsideSearchBar = searchInput.contains(event.target) || resultsContainer.contains(event.target);

        if (!isClickInsideSearchBar) {
            searchInput.value = '';
            resultsContainer.classList.remove('visible');
            resultsContainer.innerHTML = '';
        }
    });

    // Afiseaza rezultatele cautarii cand bara de cautare este focalizata
    searchInput.addEventListener('focus', () => {
        if (resultsContainer.children.length > 0) {
            resultsContainer.classList.add('visible');
        }
    });

}); // Finalul functiei care ruleaza la incarcarea DOM-ului

