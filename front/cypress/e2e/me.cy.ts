/// <reference types="Cypress" />

describe('Account page', () => {
  const SELECTORS = {
    emailInput: 'input[formControlName=email]',
    passwordInput: 'input[formControlName=password]',
    accountLink: 'span.link',
    cardTitle: 'mat-card-title h1',
    deleteButton: 'button[mat-raised-button]',
    backButton: 'button',
    snackBar: 'simple-snack-bar'
  };

  const EXPECTED_TEXTS = {
    userInfo: 'User information',
    createDate: 'Create at:  June 26, 2025',
    updateDate: 'Last update:  June 26, 2025',
    adminStatus: 'You are admin',
    deleteAccount: 'Delete my account:',
    deleteSuccess: 'Your account has been deleted !'
  };

  const PARAGRAPH_INDEX = {
    name: 0,
    email: 1,
    status: 2,
    createDate: 3,
    updateDate: 4
  };

  const adminUser = {
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

  const noAdminUser = {
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

  // Utility function to configure common intercepts
  const setupCommonIntercepts = () => {
    cy.intercept('GET', '/api/session', []);
    cy.intercept('DELETE', '/api/user');
  };

  // Utility function for connection
  const loginAs = (user, email) => {
    cy.visit('/login');
    cy.intercept('POST', '/api/auth/login', user);
    cy.intercept('GET', `/api/user/${user.id}`, user);

    cy.get(SELECTORS.emailInput).type(email);
    cy.get(SELECTORS.passwordInput).type('password123{enter}{enter}');
    cy.url().should('include', '/sessions');
  };

  // Utility function to check the user profile
  const verifyUserProfile = (user, isAdmin = false) => {
    cy.get(SELECTORS.accountLink).contains('Account').click();
    cy.url().should('include', '/me');

    cy.get(SELECTORS.cardTitle).should('contain', EXPECTED_TEXTS.userInfo);
    cy.get('p').eq(PARAGRAPH_INDEX.name).should('contain', `Name: ${user.firstName} ${user.lastName.toUpperCase()}`);
    cy.get('p').eq(PARAGRAPH_INDEX.email).should('contain', `Email: ${user.email}`);

    if (isAdmin) {
      cy.get('p').eq(PARAGRAPH_INDEX.status).should('contain', EXPECTED_TEXTS.adminStatus);
      cy.get(SELECTORS.deleteButton).should('not.exist');
    } else {
      cy.get('p').eq(PARAGRAPH_INDEX.status).should('contain', EXPECTED_TEXTS.deleteAccount);
      cy.get(SELECTORS.deleteButton).should('exist');
    }

    cy.get('p').eq(PARAGRAPH_INDEX.createDate).should('contain', EXPECTED_TEXTS.createDate);
    cy.get('p').eq(PARAGRAPH_INDEX.updateDate).should('contain', EXPECTED_TEXTS.updateDate);
  };

  // Utility function to test the return button
  const verifyBackNavigation = () => {
    cy.window().then((win) => {
      cy.stub(win.history, 'back').as('historyBack');
    });
    cy.get(SELECTORS.backButton).contains('arrow_back').click();
    cy.get('@historyBack').should('have.been.called');
  };

  beforeEach(() => {
    setupCommonIntercepts();
  });

  describe('connection as administrator', () => {
    beforeEach(() => {
      loginAs(adminUser, 'admin@yoga.fr');
    });

    it('should show admin profile without delete button and navigation to back', () => {
      verifyUserProfile(adminUser, true);
      verifyBackNavigation();
    });
  });

  describe('connection as not administrator', () => {
    beforeEach(() => {
      loginAs(noAdminUser, 'user@yoga.fr');
    });

    it('should delete user account and redirect to home', () => {
      cy.intercept('DELETE', `/api/user/${noAdminUser.id}`, {
        statusCode: 200,
        body: {}
      }).as('deleteUser');

      cy.get(SELECTORS.accountLink).contains('Account').click();
      cy.url().should('include', '/me');

      cy.get(SELECTORS.cardTitle).should('contain', EXPECTED_TEXTS.userInfo);
      cy.get(SELECTORS.deleteButton).click();

      cy.wait('@deleteUser');
      cy.get(SELECTORS.snackBar).should('contain', EXPECTED_TEXTS.deleteSuccess);
      cy.url().should('include', '/');
    });
  });
});
