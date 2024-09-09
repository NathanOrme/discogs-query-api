import { discogsTypes, discogFormats, discogCountries } from './discogsData';

test('discogsTypes contains expected values', () => {
  expect(discogsTypes).toEqual([
    { value: "RELEASE", text: "Release" },
    { value: "MASTER", text: "Master" },
    { value: "", text: "Select a type" }
  ]);
});

test('discogFormats contains expected values', () => {
  expect(discogFormats).toEqual([
    { value: "", text: "Any Format" },
    { value: "vinyl", text: "Vinyl" },
    { value: "album", text: "Album" },
    { value: "lp", text: "LP" },
    { value: "compilation", text: "Compilation" },
    { value: "album vinyl", text: "Album Vinyl" },
    { value: "compilation vinyl", text: "Compilation Vinyl" }
  ]);
});

test('discogCountries contains expected values', () => {
  expect(discogCountries).toEqual([
    { value: "", text: "Any Country" },
    { value: "EUROPE", text: "Europe" },
    { value: "UK", text: "UK" },
    { value: "US", text: "US" },
    { value: "COSTA_RICA", text: "Costa Rica" },
    // Add more values as needed
  ]);
});
