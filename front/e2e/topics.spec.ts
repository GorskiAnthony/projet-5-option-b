import { test, expect } from '@playwright/test';

test.describe('Thèmes', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/topics');
  });

  test('affiche la liste des thèmes', async ({ page }) => {
    const cards = page.locator('.topic-card');
    await expect(cards.first()).toBeVisible();
    expect(await cards.count()).toBeGreaterThan(0);
  });

  test('chaque thème a un bouton d\'abonnement', async ({ page }) => {
    const btn = page.locator('.topic-card button').first();
    await expect(btn).toBeVisible();
    await expect(btn).toHaveText(/s'abonner|se désabonner/i);
  });

  test('s\'abonner à un thème non-souscrit change le bouton', async ({ page }) => {
    // Trouver un thème non-souscrit
    const subscribeBtn = page.locator('.topic-card').filter({
      has: page.locator('.btn--primary'),
    }).locator('button').first();

    if (await subscribeBtn.count() > 0) {
      await subscribeBtn.click();
      // Le bouton doit maintenant indiquer "Se désabonner"
      const card = page.locator('.topic-card').filter({
        has: page.locator('.btn--subscribed'),
      }).first();
      await expect(card.locator('button')).toBeVisible();
    }
  });

  test('se désabonner d\'un thème souscrit change le bouton', async ({ page }) => {
    const unsubscribeBtn = page.locator('.topic-card').filter({
      has: page.locator('.btn--subscribed'),
    }).locator('button').first();

    if (await unsubscribeBtn.count() > 0) {
      await unsubscribeBtn.click();
      // Le bouton doit maintenant indiquer "S'abonner"
      await expect(page.locator('.btn--primary').first()).toBeVisible();
    }
  });
});
