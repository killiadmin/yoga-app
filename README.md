## Killiadmin – Yoga App

A complete application with Front-End (Angular) and Back-End (Spring Boot) to manage a yoga studio.

## Initialization of the project

### Clone the deposit

```bash
git clone https://github.com/killiadmin/yoga-app.git
cd killiadmin
```

## Launch the application :

### Front-end

```bash
cd front
npm install
npm run start
```

### Back-end

```bash
cd ../back
mvn install
```

### Configuration (.env)
Create a .env file at the next location :

```bash
back/src/main/resources/.env
```

Add your own values :
```
MYSQL_USER=
MYSQL_PASSWORD=
JWT_SECRET=
JWT_EXPIRATION_MS=
```

Start the back-end

```bash
mvn spring-boot:run
```

### Database - MySQL

Launch the MySQL server (port 3306).

Execute the SQL script :
```bash
ressources/sql/script.sql
```

Default admin account:

```bash
login: yoga@studio.com
```
```bash
password: test!1234
```

## Tests

### E2E (Cypress)

```bash
npm run e2e

npm run e2e:run
```

Cover report:
                                     
```bash
front/coverage/lcov-report/index.html
```

### Tests Unitaires (Jest)

```bash
npm run test:coverage
```

Cover report:

```bash
front/coverage/jest/lcov-report/index.html
```

### Backend (Junit)

```bash
mvn clean test
```

```bash
open target/site/jacoco/index.html
```

Cover report:

```bash
back/target/site/jacoco/index.html
```
