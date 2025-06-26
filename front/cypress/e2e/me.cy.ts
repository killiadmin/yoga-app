/// <reference types="Cypress" />

describe('Account page', () => {
  const SELECTORS = {
    emailInput: 'input[formControlName=email]',
    passwordInput: 'input[formControlName=password]',
    accountLink: 'span.link',
    cardTitle: 'mat-card-title h1',
    deleteButton: 'button[mat-raised-button]',
    snackBar: 'simple-snack-bar'
  };

  const ADMIN_USER = {
    id: 1,
    token: 'jwt',
    type: 'Bearer',
    email: 'admin@yoga.fr',
    firstName: 'Test',
    lastName: 'User',
    admin: true,
    createdAt: '2025-06-26T12:45:41',
    updatedAt: '2025-06-26T12:45:41',
  };

  const NOADMIN_USER = {
    id: 2,
    token: 'jwt',
    type: 'Bearer',
    email: 'user@yoga.fr',
    firstName: 'User',
    lastName: 'Test',
    admin: false,
    createdAt: '2025-06-26T12:45:41',
    updatedAt: '2025-06-26T12:45:41',
  };

  const loginAs = (user, email) => {
    cy.visit('/login');
    cy.intercept('POST', '/api/auth/login', user);
    cy.intercept('GET', '/api/user/' + user.id, user);

    cy.get(SELECTORS.emailInput).type(email);
    cy.get(SELECTORS.passwordInput).type('password123{enter}{enter}');
    cy.url().should('include', '/sessions');
  };

  const verifyUserProfile = (user, isAdmin = false) => {
    cy.get(SELECTORS.accountLink).contains('Account').click();
    cy.url().should('include', '/me');

    cy.get(SELECTORS.cardTitle).should('contain', 'User information');

    cy.get('p').each(($el, index) => {
      cy.log(`p[${index}]: ${$el.text()}`);
    });

    cy.contains('p', 'Name:').should('contain', user.firstName + ' ' + user.lastName.toUpperCase());
    cy.get('p').eq(1).should('contain', 'Email: ' + user.email);

    if (isAdmin) {
      cy.get('p').eq(2).should('contain', 'You are admin');
      cy.get(SELECTORS.deleteButton).should('not.exist');
    } else {
      cy.get('p').eq(2).should('contain', 'Delete my account:');
      cy.get(SELECTORS.deleteButton).should('exist');
    }

    cy.get('p').eq(3).should('contain', 'Create at:  June 26, 2025');
    cy.get('p').eq(4).should('contain', 'Last update:  June 26, 2025');
  };


  describe('connection as administrator', () => {
    beforeEach(() => {
      loginAs(ADMIN_USER, 'admin@yoga.fr');
    });

    it('should show admin profile without delete button', () => {
      verifyUserProfile(ADMIN_USER, true);
    });
  });


  describe('connection as non-administrator', () => {

    beforeEach(() => {
      loginAs(NOADMIN_USER, 'user@yoga.fr');
    });

    it('should show user profile with delete button and allow deletion', () => {
      cy.intercept('DELETE', '/api/user/' + NOADMIN_USER.id, {
        statusCode: 200,
        body: {}
      }).as('deleteUser');

      cy.get(SELECTORS.accountLink).contains('Account').click();
      cy.url().should('include', '/me');

      cy.get(SELECTORS.cardTitle).should('contain', 'User information');
      cy.get(SELECTORS.deleteButton).click();

      cy.wait('@deleteUser');
      cy.get(SELECTORS.snackBar).should('contain', 'Your account has been deleted !');
      cy.url().should('include', '/');
    });
  });
});
