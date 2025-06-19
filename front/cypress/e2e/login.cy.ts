/// <reference types="Cypress" />

describe('Login page', () => {
  const email = 'input[formControlName=email]';
  const password = 'input[formControlName=password]';
  const loginButton = 'button[type="submit"]';
  const errorMessage = '.error';
  const logout = '.link';

  beforeEach(() => {
    cy.visit('/login');
  });


  it('should sucessfully let the user log in', () => {
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 200,
      body: {
        id: 1,
        username: 'User',
        firstName: 'Test',
        lastName: 'Testuser',
        admin: true,
      },
    }).as('login');

    cy.intercept('GET', '/api/session', {
      statusCode: 200,
      body: []
    }).as('session');

    cy.get(email).type('killian.filatre@yoga.fr');
    cy.get(password).type('password123!');
    cy.get(loginButton).click();

    cy.wait('@login');
    cy.wait('@session');

    cy.url().should('include', '/sessions');
  });


  it('should return error if one of the inputs is not valid', () => {
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 401,
      body: {error: 'Invalid credentials'}
    }).as('loginError');

    cy.get(email).type('user@yoga.fr');
    cy.get(password).type('invalid');
    cy.get(loginButton).click();

    cy.wait('@loginError');
    cy.get(errorMessage).should('be.visible');

    cy.url().should('include', '/login');
  });


  it('should be able to log out the user after logging in', () => {
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 200,
      body: {
        id: 1,
        username: 'User',
        firstName: 'test',
        lastName: 'Usertest',
        admin: true,
      },
    }).as('login');

    cy.intercept('GET', '/api/session', {
      statusCode: 200,
      body: []
    }).as('session');

    cy.get(email).type('test@yoga.fr');
    cy.get(password).type('password123!');
    cy.get(loginButton).click();

    cy.wait('@login');
    cy.wait('@session');

    cy.url().should('include', '/sessions');

    cy.get(logout).contains('Logout').click();

    cy.url().should('include', '/');
  });
});
