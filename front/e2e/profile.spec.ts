import { test, expect } from '@playwright/test';

test.describe('Profil', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/profile');
  });

  test('affiche le formulaire de profil pré-rempli', async ({ page }) => {
    await expect(page.locator('#profile-username')).toBeVisible();
    await expect(page.locator('#profile-email')).toBeVisible();
    // Les champs sont pré-remplis avec les données de l'utilisateur connecté
    await expect(page.locator('#profile-username')).not.toHaveValue('');
    await expect(page.locator('#profile-email')).not.toHaveValue('');
  });

  test('affiche la section abonnements', async ({ page }) => {
    await expect(page.getByRole('heading', { name: /abonnements/i })).toBeVisible();
  });

  test('le bouton Sauvegarder est actif quand le formulaire est valide', async ({ page }) => {
    await expect(page.getByRole('button', { name: /sauvegarder/i })).toBeEnabled();
  });

  test('sauvegarder le profil affiche un message de succès', async ({ page }) => {
    // Récupère la valeur actuelle et la re-soumet sans modification
    const currentUsername = await page.locator('#profile-username').inputValue();
    await page.locator('#profile-username').fill(currentUsername);
    await page.getByRole('button', { name: /sauvegarder/i }).click();

    await expect(page.locator('[role="status"]')).toBeVisible();
    await expect(page.locator('[role="status"]')).toHaveText(/sauvegardé/i);
  });
});
