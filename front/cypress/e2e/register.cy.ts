/// <reference types="Cypress" />

describe('Register page', () => {
  const firstName = 'input[formcontrolname="firstName"]';
  const lastName = 'input[formcontrolname="lastName"]';
  const email = 'input[formcontrolname="email"]';
  const password = 'input[formcontrolname="password"]';
  const submitButton = 'button[type="submit"]';
  const errorMessage = '.error';
  beforeEach(() => {
    cy.visit('/register');
  });


  it('Should create account with success', () => {
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 201,
      body: { message: 'User created successfully' }
    }).as('register');

    cy.get('mat-card-title').should('be.visible');
    cy.contains('First name').should('be.visible');
    cy.contains('Last name').should('be.visible');
    cy.contains('Email').should('be.visible');
    cy.contains('Password').should('be.visible');
    cy.contains('Submit').should('be.visible');

    cy.get(firstName).type('Killian');
    cy.get(lastName).type('Filatre');
    cy.get(email).type('killian.filatre@yoga.fr');
    cy.get(password).type('password');

    cy.get(submitButton).click();

    cy.wait('@register');

    cy.url().should('include', '/login');
  });


  it('Should handle registration error', () => {
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 400,
      body: { error: 'Registration failed' }
    }).as('registerError');

    cy.get(firstName).type('Killian');
    cy.get(lastName).type('Filatre');
    cy.get(email).type('killian.filatre@yoga.fr');
    cy.get(password).type('password');

    cy.get(submitButton).click();

    cy.wait('@registerError');

    cy.get(errorMessage).should('be.visible');

    cy.url().should('include', '/register');
  });


  it('should show error for a required field not properly filled', () => {
    cy.get(firstName).should('not.be.disabled');
    cy.get(lastName).should('not.be.disabled');
    cy.get(email).should('not.be.disabled');
    cy.get(password).should('not.be.disabled');

    cy.get(firstName).type('User');
    cy.get(lastName).type('Test');
    cy.get(email).type('invalid');
    cy.get(password).type('password123!');

    cy.get(submitButton).should('be.disabled');

    cy.get(firstName).clear();
    cy.get(firstName).blur();

    cy.get(submitButton).should('be.disabled');
  });


});
