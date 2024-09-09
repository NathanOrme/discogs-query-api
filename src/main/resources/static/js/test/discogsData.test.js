import { discogsTypes, discogFormats, discogCountries } from './../modules/discogsData.js';  // Adjust the path if needed

describe('Discogs Data', () => {
test('discogsTypes should have correct length and values', () => {
expect(discogsTypes.length).toBe(3);
expect(discogsTypes).toContainEqual({ value: "RELEASE", text: "Release" });
expect(discogsTypes).toContainEqual({ value: "MASTER", text: "Master" });
expect(discogsTypes).toContainEqual({ value: "", text: "Select a type" });
});

test('discogFormats should have correct length and values', () => {
expect(discogFormats.length).toBeGreaterThan(0);
expect(discogFormats).toContainEqual({ value: "vinyl", text: "Vinyl" });
expect(discogFormats).toContainEqual({ value: "album", text: "Album" });
expect(discogFormats).toContainEqual({ value: "lp", text: "LP" });
// Add more checks as needed
});

test('discogCountries should have correct length and values', () => {
expect(discogCountries.length).toBeGreaterThan(0);
expect(discogCountries).toContainEqual({ value: "UK", text: "UK" });
expect(discogCountries).toContainEqual({ value: "US", text: "US" });
expect(discogCountries).toContainEqual({ value: "JAPAN", text: "Japan" });
// Add more checks as needed
});
});