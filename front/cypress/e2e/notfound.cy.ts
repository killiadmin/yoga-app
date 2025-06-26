/// <reference types="Cypress" />

describe('NotFoundComponent', () => {
  beforeEach(() => {
    cy.visit('/not-found');
  });

  it('should display the not found page', () => {
    cy.get('app-not-found').should('be.visible');
  });
});
