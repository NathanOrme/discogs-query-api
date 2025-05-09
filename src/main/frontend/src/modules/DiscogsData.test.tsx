import { discogsTypes, discogFormats } from './DiscogsData';

describe('DiscogsData', () => {
  describe('discogsTypes', () => {
    it('should contain the correct types', () => {
      expect(discogsTypes).toEqual([
        { value: 'RELEASE', text: 'Release' },
        { value: 'MASTER', text: 'Master' },
        { value: '', text: 'Select a type' },
      ]);
    });

    it('should have the expected length', () => {
      expect(discogsTypes.length).toBe(3);
    });
  });

  describe('discogFormats', () => {
    it('should contain the correct formats', () => {
      expect(discogFormats).toEqual([
        { value: '', text: 'Any Format' },
        { value: 'vinyl', text: 'Vinyl' },
        { value: 'album', text: 'Album' },
        { value: 'cd', text: 'CD' },
        { value: 'lp', text: 'LP' },
        { value: 'compilation', text: 'Compilation' },
        { value: 'album vinyl', text: 'Album Vinyl' },
        { value: 'compilation vinyl', text: 'Compilation Vinyl' },
        { value: 'all vinyls', text: 'All Vinyl Options' },
      ]);
    });

    it('should have the expected length', () => {
      expect(discogFormats.length).toBe(9);
    });

    it('should include a default empty option', () => {
      const emptyOption = discogFormats.find(format => format.value === '');
      expect(emptyOption).toBeDefined();
      expect(emptyOption?.text).toBe('Any Format');
    });
  });
});
