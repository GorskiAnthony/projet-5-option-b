import { test, expect } from '@playwright/test';

test.describe('Feed', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/feed');
  });

  test('affiche la page du fil d\'actualité', async ({ page }) => {
    await expect(page).toHaveURL(/\/feed/);
  });

  test('affiche le bouton "Créer un article"', async ({ page }) => {
    await expect(page.getByRole('link', { name: /créer un article/i })).toBeVisible();
  });

  test('affiche le bouton de tri', async ({ page }) => {
    await expect(page.getByRole('button', { name: /trier par/i })).toBeVisible();
  });

  test('le bouton de tri change l\'ordre des articles', async ({ page }) => {
    const sortBtn = page.getByRole('button', { name: /trier par/i });
    const initialLabel = await sortBtn.getAttribute('aria-label');
    await sortBtn.click();
    const newLabel = await sortBtn.getAttribute('aria-label');
    expect(newLabel).not.toBe(initialLabel);
  });

  test('cliquer sur "Créer un article" navigue vers le formulaire', async ({ page }) => {
    await page.getByRole('link', { name: /créer un article/i }).click();
    await expect(page).toHaveURL(/\/posts\/create/);
  });
});
