# CardSwap - Gestió d'Intercanvi de Cartes Col·leccionables

## 📚 Descripció
CardSwap és una aplicació mòbil nativa per a dispositius Android que facilita l'intercanvi i la compra/venda de cartes col·leccionables. Amb una interfície intuïtiva i basada en els principis de Material Design, CardSwap busca connectar col·leccionistes de tot el món de manera senzilla i segura.

### 🏆 Objectius
- Proporcionar una plataforma per gestionar col·leccions de cartes.
- Fomentar el comerç directe i transparent entre usuaris.
- Crear una experiència d'usuari fluïda i amigable.

---

## 🚀 Funcionalitats principals
### 1. **Gestió d'usuaris**
- **Login i registre**: Inici de sessió i registre amb correu electrònic.
- **Recuperació de contrasenya**: Recupera la contrasenya en cas d'oblit.
- **Gestió del perfil**: Edició de dades personals i imatge de perfil.
- **Rol d'administrador**:
  - Activar/desactivar usuaris.
  - Llistar, modificar i eliminar usuaris.

### 2. **Intercanvi i compra/venda de cartes**
- Publicació d'anuncis per intercanviar o vendre cartes.
- Cerca avançada amb filtres per col·lecció, expansió i característiques.
- Sistema d'intercanvi directe entre usuaris.

---

## 🛠️ Tecnologies utilitzades
### **Frontend Mobile**
- **Kotlin**: Llenguatge de programació principal.
- **Jetpack Compose**: Implementació de la interfície gràfica.
- **Arquitectura MVVM**: Gestió de l'estat amb `MutableStateFlow` i `StateFlow`.

### **Backend**
- **Spring Boot**: Desenvolupament de serveis RESTful per gestionar la lògica de negoci i persistència.

### **Control de versions**
- **Git/GitLab**: Gestió del codi font i col·laboració en equip.
  - Branques: `main`, `developer`, `feature/*`, `bugfix/*`.
  - Merge requests amb eliminació automàtica de branques fusionades.

### **Base de dades**
- Persistència de dades amb bases de dades relacionals i/o NoSQL.

---

## 🎨 Requeriments d'interfície (UI/UX)
1. Idiomes: Català, Castellà i Anglès.
2. Compliment de directrius de **Material Design**:
   - Disseny coherent de colors, fonts i icones.
   - Responsivitat per a diferents mides de pantalla.
3. Accessibilitat:
   - Etiquetes descriptives per elements interactius.
4. Navegació:
   - Menú de navegació **Bottom Navigation** per accés a funcionalitats principals.

---

## 🔒 Requeriments operatius
- Compatible amb Android emuladors i dispositius físics.
- Fluïdesa en totes les operacions.
- Gestió d'excepcions amb missatges descriptius per a l'usuari.
- Logs disponibles per monitoratge i depuració.
- Validació i seguretat de totes les dades d'entrada de l'usuari.

---

## 👥 Equip de desenvolupament
- **Carlos Mendoza Jiménez** - [GitHub](enlace)
- **David Escarrer** - [GitHub](enlace)
- **Antonio Oliva** - [GitHub](enlace)

---

## 📅 Planificació inicial
1. **Primera setmana**:
   - Configuració del repositori GitLab.
   - Establir l'estructura de branques.
   - Implementació de les bases del backend amb Spring Boot.
2. **Segona setmana**:
   - Disseny de l'arquitectura MVVM al frontend.
   - Desenvolupament inicial de la interfície amb Jetpack Compose.
3. **Tercera setmana**:
   - Integració frontend-backend via serveis RESTful.
   - Proves de funcionalitat i usabilitat.
4. **Quarta setmana**:
   - Depuració final, documentació i lliurament del projecte.

---

## 📝 Notes
Aquest document és inicial i està subjecte a canvis a mesura que es desenvolupi el projecte. Si teniu suggeriments o dubtes, no dubteu a contactar amb algun membre de l'equip.

