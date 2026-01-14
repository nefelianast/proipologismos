# Κρατικός Προϋπολογισμός - Σύστημα Ανάλυσης

Εφαρμογή JavaFX για ανάλυση, οπτικοποίηση και διαχείριση δεδομένων του κρατικού προϋπολογισμού. Η εφαρμογή παρέχει:
- Προβολή και επεξεργασία προϋπολογισμού για τα έτη 2023-2027
- Στατιστική ανάλυση και σύγκριση ετών & κρατών
- Προσομοιώσεις και σενάρια
- AI Assistant για αυτοματοποιημένες αναλύσεις
- Διεθνή δεδομένα 

---

## Οδηγίες Μεταγλώττισης

### Προαπαιτούμενα
- **Java JDK 11** ή νεότερη έκδοση
- **Apache Maven 3.6+** (ή χρήση του Maven Wrapper που περιλαμβάνεται)
- **Internet σύνδεση** (για κατέβασμα dependencies)

### Μεταγλώττιση με Maven

```bash
# Μεταγλώττιση του project
mvn clean compile

# Μεταγλώττιση και εκτέλεση tests
mvn clean test

# Δημιουργία JAR αρχείου
mvn clean package
```

### Μεταγλώττιση με Maven Wrapper (Windows)

```cmd
# Χρήση του Maven Wrapper που περιλαμβάνεται
.\mvnw.cmd clean compile
```

### Ελέγχος Code Quality

```bash
# CheckStyle (έλεγχος μορφοποίησης κώδικα)
mvn checkstyle:check

# SpotBugs (ανίχνευση bugs)
mvn spotbugs:check
```

---

## Οδηγίες Εκτέλεσης

### Εκτέλεση με JavaFX Maven Plugin

```bash
# Απευθείας εκτέλεση με JavaFX
mvn javafx:run
```

### Εκτέλεση με JAR αρχείο

```bash
# Μετά τη μεταγλώττιση (mvn package), εκτέλεση:
# Σημείωση: Χρειάζεται JavaFX SDK εγκατεστημένο τοπικά
java --module-path <javafx-sdk-path>/lib --add-modules javafx.controls,javafx.fxml -jar target/proipologismos-1.0-Release.jar
```

**Σημείωση:** Η εκτέλεση με JAR απαιτεί JavaFX SDK. Προτείνεται η χρήση του `mvn javafx:run` που δεν απαιτεί επιπλέον setup.

### Εκτέλεση με exec plugin

```bash
# Χρειάζεται να ορίσετε το mainClass ως parameter
mvn exec:java -Dexec.mainClass="ui.MainGUI"
```

**Σημείωση:** Προτείνεται η χρήση του `mvn javafx:run` για πιο απλή εκτέλεση.

**Σημείωση:** Η εφαρμογή δημιουργεί αυτόματα τη βάση δεδομένων SQLite κατά την πρώτη εκτέλεση.

---

## Οδηγίες Χρήσης

### Αρχική Είσοδος
1. **Splash Screen**: Εμφανίζεται αυτόματα κατά την εκκίνηση
2. **Σύνδεση ως κυβέρνηση**: Χρησιμοποιήστε τα default credentials:
   - Username: `admin`
   - Password: `admin123`

### Κύριες Λειτουργίες

#### Προβολή Προϋπολογισμού
- Επιλέξτε έτος από το dropdown menu (2023-2027)
- Προβάλετε έσοδα, δαπάνες, υπουργεία και αποκεντρωμένες διοικήσεις

#### Σύγκριση 
- Επιλέξτε δύο έτη ή δύο κράτη για σύγκριση
- Προβάλετε ποσοστιαίες μεταβολές
- Δείτε αναλυτικές συγκρίσεις ανά κατηγορία

#### Προσομοιώσεις
- Τροποποιήστε ποσά σε διάφορες κατηγορίες
- Προβάλετε το αντίκτυπο στις συνολικές δαπάνες/έσοδα
- Αποθηκεύστε σενάρια για μελλοντική χρήση

#### Στατιστική Ανάλυση
- Προβάλετε στατιστικές μετρικές (μέση τιμή, διάμεσος, τυπική απόκλιση)
- Εντοπίστε outliers
- Δείτε γραμμικές παλινδρομήσεις και συσχετίσεις

#### AI Assistant
- Κάντε ερωτήσεις για τον προϋπολογισμό

---

## Δομή Αποθετηρίου

```
proipologismos/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── FetchInternationalDataFromAPIs.java  # API calls για διεθνή δεδομένα
│   │   │   ├── SQLmaker.java             # Δημιουργία βάσης δεδομένων
│   │   │   ├── SQLinserter.java          # Εισαγωγή δεδομένων στη βάση
│   │   │   └── ui/
│   │   │       ├── MainGUI.java           # Entry point (JavaFX Application)
│   │   │       ├── Splash.java            # Splash screen controller
│   │   │       ├── Login.java              # Authentication controller
│   │   │       ├── HomeController.java     # Κύρια οθόνη controller
│   │   │       ├── BudgetEditController.java  # Επεξεργασία προϋπολογισμού
│   │   │       ├── BudgetData.java        # Data access layer (Singleton)
│   │   │       ├── DatabaseConnection.java # Database connection manager
│   │   │       ├── StatisticalAnalysis.java  # Στατιστικές συναρτήσεις
│   │   │       ├── Charts.java            # Δημιουργία γραφημάτων
│   │   │       ├── Comparisons.java       # Σύγκριση ετών
│   │   │       ├── Simulations.java        # Προσομοιώσεις
│   │   │       ├── AIAssistantController.java  # AI assistant UI
│   │   │       ├── AIAssistantService.java    # AI service integration
│   │   │       ├── UserData.java           # User data persistence
│   │   │       ├── Authentication.java     # Authentication logic
│   │   │       ├── AmountFormatter.java    # Μορφοποίηση ποσών
│   │   │       ├── Constraints.java        # Validation constraints
│   │   │       ├── DataConvert.java        # Μετατροπή PDF σε CSV
│   │   │       └── DataDownload.java       # Κατέβασμα PDF από web
│   │   └── resources/
│   │       ├── database/
│   │       │   └── BudgetData.db          # SQLite database
│   │       ├── ui/
│   │       │   ├── SplashScreen.fxml
│   │       │   ├── LoginView.fxml
│   │       │   ├── Home.fxml
│   │       │   ├── BudgetEditView.fxml
│   │       │   ├── AIAssistantView.fxml
│   │       │   └── styles.css
│   │       └── images/
│   │           └── logo.png
│   └── test/
│       └── java/                           # JUnit tests
├── target/                                 # Build output
├── pom.xml                                 # Maven configuration
├── checkstyle.xml                          # CheckStyle rules
├── mvnw.cmd                                # Maven wrapper (Windows)
└── README.md                               # Αυτό το αρχείο
```

---

## Διάγραμμα UML - Σχεδιασμός Κώδικα

### Κύρια Αρχιτεκτονική

```
┌─────────────────────────────────────────────────────────────┐
│                    JavaFX Application Layer                   │
├─────────────────────────────────────────────────────────────┤
│  MainGUI (Application)                                       │
│    │                                                         │
│    ├──> Splash (Controller)                                 │
│    │     └──> Login (Controller)                            │
│    │           └──> HomeController (Main UI)                │
│    │                 ├──> BudgetEditController              │
│    │                 ├──> Charts                            │
│    │                 ├──> Comparisons                       │
│    │                 ├──> Simulations                       │
│    │                 └──> AIAssistantController            │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    Business Logic Layer                      │
├─────────────────────────────────────────────────────────────┤
│  BudgetData (Singleton)                                     │
│    ├──> StatisticalAnalysis                                  │
│    ├──> UserData (Singleton)                                │
│    └──> DatabaseConnection                                   │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    Data Access Layer                         │
├─────────────────────────────────────────────────────────────┤
│  SQLmaker                                                    │
│  SQLinserter                                                 │
│  DatabaseConnection                                          │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    Data Processing Layer                     │
├─────────────────────────────────────────────────────────────┤
│  ui.DataDownload                                             │
│  ui.DataConvert                                              │
│  FetchInternationalDataFromAPIs                             │
└─────────────────────────────────────────────────────────────┘
```

### Κύριες Κλάσεις και Σχέσεις

- **Singleton Pattern**: `BudgetData`, `UserData`
- **MVC Architecture**: Controllers (FXML), Models (BudgetData), Views (FXML files)
- **Factory Pattern**: `DatabaseConnection` για δημιουργία connections
- **Strategy Pattern**: `StatisticalAnalysis` με διάφορες στατιστικές μεθόδους

---

## Δομές Δεδομένων και Αλγόριθμοι

### Δομές Δεδομένων

#### 1. **SQLite Database Schema**
- **Πίνακες ανά έτος**: `revenue_YYYY`, `expenses_YYYY`, `ministries_YYYY`, `decentralized_administrations_YYYY`, `budget_summary_YYYY`
- **Πίνακες χρηστών**: `users` (id, username, password)
- **Διεθνή δεδομένα**: `international_indicators`, `international_budgets`, `published_years`
- **Foreign Keys**: Συνδέσεις μεταξύ πινάκων μέσω `budget_summary`

#### 2. **In-Memory Data Structures**
- **Collections**:
  - `List<CategoryInfo>`: Κατηγορίες προϋπολογισμού
  - `Map<String, Double>`: Key-value pairs για γραφήματα
  - `ObservableList`: JavaFX collections για real-time updates
- **Singleton Instances**: `BudgetData`, `UserData`

### Αλγόριθμοι

#### 1. **Στατιστική Ανάλυση** (`StatisticalAnalysis.java`)
- **Μέση Τιμή (Mean)**: O(n) - γραμμική πολυπλοκότητα
- **Διάμεσος (Median)**: O(n log n) - λόγω sorting
- **Τυπική Απόκλιση**: O(n) - υπολογισμός variance και square root
- **Συσχέτιση (Correlation)**: O(n) - Pearson correlation coefficient
- **Γραμμική Παλινδρόμηση**: O(n) - Least Squares Method
- **Outlier Detection**: O(n log n) - χρήση IQR (Interquartile Range)
- **Quartiles**: O(n log n) - sorting και υπολογισμός Q1, Q2, Q3

#### 2. **Data Processing**
- **PDF Parsing**: Apache PDFBox για εξαγωγή κειμένου - O(pages × lines)
- **CSV Conversion**: Linear parsing με regex matching
- **Database Queries**: SQL SELECT/INSERT/UPDATE - εξαρτάται από indexes

#### 3. **UI Algorithms**
- **Chart Generation**: JavaFX Charts API - O(n) για n data points
- **Data Filtering**: Stream API filtering - O(n)
- **Sorting**: Collections.sort() - O(n log n)

#### 4. **Authentication**
- **Password Verification**: String comparison - O(1)
- **Session Management**: In-memory storage

---

## Πρόσθετη Τεχνική Τεκμηρίωση

### Testing

#### Test Coverage
- **Unit Tests**: JUnit 5 για business logic
- **Test Classes**:
  - `SQLinserterTest.java` (root test directory)
  - `SQLmakerTest.java` (root test directory)
  - `ui/AIAssistantServiceTest.java`
  - `ui/AmountFormatterTest.java`
  - `ui/AuthenticationTest.java`
  - `ui/BudgetDataTest.java`
  - `ui/BudgetEditControllerTest.java`
  - `ui/ComparisonsTest.java`
  - `ui/ConstraintsTest.java`
  - `ui/DatabaseConnectionTest.java`
  - `ui/DataConvertTest.java`
  - `ui/DataDownloadTest.java`
  - `ui/HomeControllerTest.java`
  - `ui/SimulationsTest.java`
  - `ui/StatisticalAnalysisTest.java`
  - `ui/UserDataTest.java`

#### Εκτέλεση Tests
```bash
mvn test
```

### Code Quality Tools

#### CheckStyle
- **Configuration**: `checkstyle.xml`
- **Rules**: Google Java Style Guide adaptations
- **Execution**: `mvn checkstyle:check`

#### SpotBugs
- **Static Analysis**: Ανίχνευση potential bugs
- **Configuration**: `pom.xml` (effort: Max, threshold: Low)
- **Execution**: `mvn spotbugs:check`

### JavaDoc Documentation

Ο κώδικας περιλαμβάνει JavaDoc σχόλια για:
- **Public Classes**: Περιγραφή λειτουργίας
- **Public Methods**: Παράμετροι, επιστρεφόμενες τιμές, exceptions
- **Complex Algorithms**: Επεξηγήσεις λογικής

**Παράδειγμα**:
```java
/**
 * Υπολογίζει τον συντελεστή συσχέτισης μεταξύ δύο μεταβλητών
 * χρησιμοποιώντας τον τύπο Pearson.
 * 
 * @param xValues οι τιμές της πρώτης μεταβλητής
 * @param yValues οι τιμές της δεύτερης μεταβλητής
 * @return ο συντελεστής συσχέτισης (-1 έως 1) ή NaN αν τα δεδομένα δεν είναι έγκυρα
 */
public static double calculateCorrelation(double[] xValues, double[] yValues)
```

### Dependencies

#### Κύριες Βιβλιοθήκες
- **JavaFX 17.0.10**: GUI framework
- **SQLite JDBC 3.51.1.0**: Database driver
- **Apache PDFBox 2.0.30**: PDF processing
- **OkHttp 4.12.0**: HTTP client για API calls
- **JUnit Jupiter 5.11.0-M2**: Testing framework
- **JSON 20230227**: JSON processing

### Build Configuration

- **Java Version**: 11
- **Maven Compiler Plugin**: 3.5.1
- **JavaFX Maven Plugin**: 0.0.8
- **Project Version**: 1.0 Release
- **Encoding**: UTF-8

### Database Schema Details

#### Κύριοι Πίνακες (για κάθε έτος 2023-2027)
- `revenue_YYYY`: 14 στήλες (taxes, social_contributions, transfers, κ.λπ.)
- `expenses_YYYY`: 15 στήλες (employee_benefits, social_benefits, interest, κ.λπ.)
- `ministries_YYYY`: 23 υπουργεία + total_ministries
- `decentralized_administrations_YYYY`: 7 αποκεντρωμένες διοικήσεις
- `budget_summary_YYYY`: Συνοπτικός πίνακας με foreign keys

#### Διεθνή Δεδομένα
- `international_indicators`: country_code, country_name, year, indicator, value
- `international_budgets`: country_code, country_name, year, total_gdp, total_revenue, total_expenses, budget_balance

### Performance Considerations

- **Database Indexing**: Primary keys σε όλους τους πίνακες
- **Connection Pooling**: Singleton pattern για database connections
- **Lazy Loading**: Δεδομένα φορτώνονται on-demand
- **Caching**: BudgetData singleton cache για συχνά χρησιμοποιούμενα δεδομένα

### Security

- **Password Storage**: Plain text (για demo - σε production θα χρησιμοποιηθεί hashing)
- **SQL Injection Prevention**: PreparedStatements για όλες τις queries
- **Input Validation**: Constraints class για validation

