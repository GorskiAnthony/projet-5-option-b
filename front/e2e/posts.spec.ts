import { test, expect } from '@playwright/test';

test.describe('Articles', () => {
  test('affiche le formulaire de création d\'article', async ({ page }) => {
    await page.goto('/posts/create');

    await expect(page.locator('#post-topic')).toBeVisible();
    await expect(page.locator('#post-title')).toBeVisible();
    await expect(page.locator('#post-content')).toBeVisible();
    await expect(page.getByRole('button', { name: /créer/i })).toBeVisible();
  });

  test('le bouton Créer est désactivé si le formulaire est vide', async ({ page }) => {
    await page.goto('/posts/create');
    await expect(page.getByRole('button', { name: /créer/i })).toBeDisabled();
  });

  test('créer un article redirige vers le feed', async ({ page }) => {
    await page.goto('/posts/create');

    // Attendre que les thèmes soient chargés dans le select
    await page.waitForFunction(() => {
      const select = document.querySelector('#post-topic') as HTMLSelectElement;
      return select && select.options.length > 1;
    });

    await page.locator('#post-topic').selectOption({ index: 1 });
    await page.locator('#post-title').fill('Article e2e Playwright');
    await page.locator('#post-content').fill('Contenu de test créé par Playwright.');
    await page.getByRole('button', { name: /créer/i }).click();

    await expect(page).toHaveURL(/\/posts\/\d+/);
  });

  test('cliquer sur un article ouvre son détail', async ({ page }) => {
    await page.goto('/feed');

    const firstCard = page.locator('app-post-card').first();
    await expect(firstCard).toBeVisible();
    await firstCard.click();

    await expect(page).toHaveURL(/\/posts\/\d+/);
  });
});
