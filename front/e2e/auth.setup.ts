import { test as setup } from '@playwright/test';

setup('authenticate', async ({ page }) => {
  await page.goto('/login');
  await page.locator('#identifier').fill('user@mdd.com');
  await page.locator('#password').fill('Test1234!');
  await page.locator('button[type="submit"]').click();
  await page.waitForURL('**/feed');

  // S'abonner au premier thème disponible pour que le feed ne soit pas vide
  await page.goto('/topics');
  await page.waitForSelector('.topic-card');
  const subscribeBtn = page.locator('.topic-card').filter({
    has: page.locator('.btn--primary'),
  }).locator('button').first();
  if (await subscribeBtn.count() > 0) {
    await subscribeBtn.click();
    await page.waitForTimeout(500);
  }

  await page.context().storageState({ path: 'e2e/.auth/user.json' });
});
