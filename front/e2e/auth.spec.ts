import { test, expect } from '@playwright/test';

test.describe('Authentification', () => {
  test('connexion valide redirige vers le feed', async ({ page }) => {
    await page.goto('/login');
    await page.locator('#identifier').fill('user@mdd.com');
    await page.locator('#password').fill('Test1234!');
    await page.locator('button[type="submit"]').click();

    await expect(page).toHaveURL(/\/feed/);
  });

  test('connexion avec mauvais mot de passe affiche une erreur', async ({ page }) => {
    await page.goto('/login');
    await page.locator('#identifier').fill('user@mdd.com');
    await page.locator('#password').fill('mauvaisMotDePasse');
    await page.locator('button[type="submit"]').click();

    await expect(page.locator('[role="alert"]')).toBeVisible();
    await expect(page).not.toHaveURL(/\/feed/);
  });

  test('inscription avec un nouvel email redirige vers le feed', async ({ page }) => {
    const timestamp = Date.now();
    const email = `test${timestamp}@mdd.com`;
    const username = `user${timestamp}`;

    await page.goto('/register');
    await page.locator('#username').fill(username);
    await page.locator('#email').fill(email);
    await page.locator('#password').fill('Test1234!');
    await page.locator('button[type="submit"]').click();

    await expect(page).toHaveURL(/\/feed/);
  });

  test('inscription avec email déjà utilisé affiche une erreur', async ({ page }) => {
    await page.goto('/register');
    await page.locator('#username').fill('doublon');
    await page.locator('#email').fill('user@mdd.com');
    await page.locator('#password').fill('Test1234!');
    await page.locator('button[type="submit"]').click();

    await expect(page.locator('[role="alert"]')).toBeVisible();
  });

  test('une route protégée redirige vers la landing si non connecté', async ({ page }) => {
    await page.goto('/feed');
    await expect(page).not.toHaveURL(/\/feed/);
  });
});
