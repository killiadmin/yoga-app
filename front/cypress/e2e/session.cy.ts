/// <reference types="Cypress" />

describe('Sessions page', () => {
  const ADMIN = {
    id: 1,
    token: 'jwt',
    type: 'Bearer',
    username: 'admin@yoga.fr',
    firstName: 'Test',
    lastName: 'User',
    admin: true,
  };

  const USER = {
    id: 2,
    token: 'jwt',
    type: 'Bearer',
    username: 'user@yoga.fr',
    firstName: 'User',
    lastName: 'Test',
    admin: false,
  };

  const SESSION = {
    id: 1,
    name: 'TEST session',
    date: '2025-06-26T12:45:41',
    teacher_id: 1,
    description: 'TEST description',
    users: [2],
    createdAt: '2024-01-13T14:24:33',
    updatedAt: '2024-01-26T09:20:22',
  };

  const SESSIONS = [SESSION];

  const EDITED_SESSION = Object.assign({}, SESSION, { name: 'EDITED TEST session' });

  const TEACHERS = [
    {
      id: 1,
      lastName: 'TEACHER1',
      firstName: 'Yoga1',
      createdAt: '2025-06-26T12:45:41',
      updatedAt: '2025-06-26T12:45:41',
    },
    {
      id: 2,
      lastName: 'TEACHER2',
      firstName: 'Yoga2',
      createdAt: '2025-06-26T12:45:41',
      updatedAt: '2025-06-26T12:45:41',
    },
  ];

  // Runs before each test to set up API intercepts
  beforeEach(() => {
    cy.intercept('GET', '/api/session', (req) => {
      req.reply(SESSIONS);
    });

    cy.intercept('POST', '/api/session', (req) => {
      SESSIONS.push(SESSION);
      req.reply(SESSION);
    });

    cy.intercept('GET', '/api/session/' + SESSION.id, SESSION);

    cy.intercept('DELETE', '/api/session/' + SESSION.id, (req) => {
      SESSIONS.splice(0, 1);
      req.reply(EDITED_SESSION);
    });

    cy.intercept('PUT', '/api/session/' + SESSION.id, (req) => {
      SESSIONS.splice(0, 1, EDITED_SESSION);
      req.reply(EDITED_SESSION);
    });

    cy.intercept('GET', '/api/teacher', TEACHERS);
  });

  describe('Admin mode', () => {
    // Before each admin test, visit login page and perform login
    beforeEach(() => {
      cy.visit('/login');

      cy.intercept('POST', '/api/auth/login', ADMIN);

      cy.get('input[formControlName=email]').type('admin@yoga.fr');
      cy.get('input[formControlName=password]').type('password123!{enter}{enter}');

      cy.url().should('include', '/sessions');
    });

    it('Actions sessions', () => {

      // Creation card
      cy.get('mat-card').should('have.length', 2);

      cy.get('mat-card-title').should('contain', SESSION.name);

      // Fill in the creation form fields
      cy.get('button[mat-raised-button] span').contains('Create').click();
      cy.get('input[formControlName="name"]').type(SESSION.name);
      cy.get('input[formControlName="date"]').type(SESSION.date.split('T')[0]);
      cy.get('mat-select[formControlName="teacher_id"]').click();
      cy.get('mat-option').contains(TEACHERS[0].firstName).click();

      cy.get('textarea[formControlName="description"]').type(SESSION.description);

      cy.get('button[mat-raised-button]').contains('Save').click();
      cy.get('snack-bar-container').contains('Session created !').should('exist');

      cy.get('snack-bar-container button span').contains('Close').click();

      cy.get('mat-card').should('have.length', 3);

      // Edition card
      cy.get('button[mat-raised-button] span').contains('Edit').click();

      // Change the session name and save
      cy.get('input[formControlName="name"]').clear().type('EDITED TEST session');
      cy.get('button[mat-raised-button]').contains('Save').click();

      cy.get('snack-bar-container').contains('Session updated !').should('exist');
      cy.get('snack-bar-container button span').contains('Close').click();

      cy.get('mat-card-title').should('contain', EDITED_SESSION.name);

      // Deletion card
      cy.get('button').contains('Detail').click();

      cy.get('button').contains('Delete').click();

      // Verify success notification for deletion
      cy.get('snack-bar-container').contains('Session deleted !').should('exist');
      cy.get('snack-bar-container button span').contains('Close').click();

      cy.get('mat-card').should('have.length', 2);
    });
  });

  describe('User mode', () => {
    // Before each user test, visit login page and perform login as a user
    beforeEach(() => {
      cy.visit('/login');

      cy.intercept('POST', '/api/auth/login', USER);

      // Fill email and password fields, then submit
      cy.get('input[formControlName=email]').type('user@yoga.fr');
      cy.get('input[formControlName=password]').type('password321!{enter}{enter}');

      cy.intercept('GET', '/api/session', (req) => {
        req.reply(SESSIONS);
      });

      // Confirm URL includes '/sessions' after login
      cy.url().should('include', '/sessions');
    });

    it('Validates restrictions for users', () => {
      cy.get('button[mat-raised-button] span').contains('Edit').should('not.exist');
      cy.get('button').contains('Delete').should('not.exist');
    });
  });
});
