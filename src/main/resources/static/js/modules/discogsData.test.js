import { discogsTypes, discogFormats, discogCountries } from './discogsData';

describe('discogsData', () => {
  test('should export discogsTypes array', () => {
    expect(discogsTypes).toBeInstanceOf(Array);
    expect(discogsTypes.length).toBeGreaterThan(0);
    expect(discogsTypes[0]).toHaveProperty('value');
    expect(discogsTypes[0]).toHaveProperty('text');
  });

  test('should export discogFormats array', () => {
    expect(discogFormats).toBeInstanceOf(Array);
    expect(discogFormats.length).toBeGreaterThan(0);
    expect(discogFormats[0]).toHaveProperty('value');
    expect(discogFormats[0]).toHaveProperty('text');
  });

  test('should export discogCountries array', () => {
    expect(discogCountries).toBeInstanceOf(Array);
    expect(discogCountries.length).toBeGreaterThan(0);
    expect(discogCountries[0]).toHaveProperty('value');
    expect(discogCountries[0]).toHaveProperty('text');
  });
});
