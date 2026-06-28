import { test, expect } from "@playwright/test";

// AC-016: recommendations are displayed in-app at the TOP of the listing page
// and within the car detail page ("You may also like"). No email/outbound.

test("listing page shows a recommendation strip at the top", async ({ page }) => {
  await page.goto("/");

  // The first section on the page is the recommendation strip (rendered above filters/results).
  const strip = page.locator("section").first();
  await expect(strip).toBeVisible();
  await expect(
    strip.getByRole("heading", { name: /Recommended for you|Popular right now/ })
  ).toBeVisible();

  // There must be at least one car card rendered in the strip.
  await expect(strip.getByRole("link").first()).toBeVisible();
});

test("car detail page shows 'You may also like' recommendations", async ({ page }) => {
  await page.goto("/");

  // Open the first listing in the results grid.
  const firstCar = page.getByRole("link").filter({ hasText: /¥|\d{4}/ }).first();
  await firstCar.click();

  await expect(page).toHaveURL(/\/cars\/\d+/);
  await expect(page.getByRole("heading", { name: "You may also like" })).toBeVisible();
});

test("recommendations are in-app only (no outbound email links)", async ({ page }) => {
  await page.goto("/");
  // No mailto/email-trigger anchors should be used to deliver recommendations.
  await expect(page.locator('a[href^="mailto:"]')).toHaveCount(0);
});
