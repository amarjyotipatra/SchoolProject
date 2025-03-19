# SchoolProject

SchoolProject is a Spring Boot-based web application designed to manage school-related data, including students, teachers, principals, subjects, and scores. It provides a RESTful API for interacting with the data and includes role-based access control for different user types.

## Features

*   **User Management:**
    *   Create and manage Principals, ClassTeachers, and Children.
    *   Role-based access control (Principal, ClassTeacher, Child).
    *   Secure login with username and password.
*   **Score Management:**
    *   Record and view student scores for different subjects.
    *   Calculate average scores per subject per class.
    *   Identify top and bottom 3 scores per subject per class.
    *   View and sort students of a class based on their score in a particular subject.
*   **Data Analysis:**
    *   Calculate cumulative average scores across subjects and classes.
    *   Generate data for bar charts (cumulative averages).
*   **Efficient Data Handling:**
    *   Uses DTOs (Data Transfer Objects) for efficient data transfer.
    *   Uses ORM (Object-Relational Mapping) with Spring Data JPA for database management.
    *   Caching for improved performance.
* **UI**:
    * Basic UI created using React and Bootstrap.
    * Login page for different users.
    * Different dashboard for different users.

## Technologies Used

*   **Backend:**
    *   Spring Boot (Java)
    *   Spring Data JPA (ORM)
    *   Spring Security (Authentication and Authorization)
    *   MySQL (Database)
    *   Redis (Caching)
    *   Lombok (Code Generation)
*   **Frontend:**
    *   React
    *   Bootstrap
    *   Webpack
    *   Babel
    *   Axios

## Setup and Installation

### Backend (Spring Boot)

1.  **Prerequisites:**
    *   Java Development Kit (JDK) 17 or higher
    *   MySQL Database
    *   Redis
    *   Gradle
2.  **Clone the Repository:**
3.  **Configure Database:**
   *   Open `src/main/resources/application.properties`.
   *   Update the database connection details (URL, username, password) to match your MySQL setup.
   *   Update the redis connection details.
    