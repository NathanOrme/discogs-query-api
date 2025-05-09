import { Query, Entry, QueryResult, ResultsProps } from './types';

describe('Types', () => {
  describe('Query interface', () => {
    it('should allow creating a query with minimal properties', () => {
      const query: Query = { id: 1 };
      expect(query.id).toBe(1);
    });

    it('should allow creating a complete query object', () => {
      const query: Query = {
        id: 1,
        artist: 'Test Artist',
        barcode: '123456789',
        album: 'Test Album',
        track: 'Test Track',
        format: 'vinyl',
        country: 'US',
        types: 'RELEASE',
      };

      expect(query.id).toBe(1);
      expect(query.artist).toBe('Test Artist');
      expect(query.barcode).toBe('123456789');
      expect(query.album).toBe('Test Album');
      expect(query.track).toBe('Test Track');
      expect(query.format).toBe('vinyl');
      expect(query.country).toBe('US');
      expect(query.types).toBe('RELEASE');
    });
  });

  describe('Entry interface', () => {
    it('should allow creating an entry with all properties', () => {
      const entry: Entry = {
        id: '123',
        title: 'Test Title',
        format: ['Vinyl', 'LP'],
        country: 'US',
        year: '2023',
        uri: 'https://example.com',
        numberForSale: 5,
        lowestPrice: 19.99,
      };

      expect(entry.id).toBe('123');
      expect(entry.title).toBe('Test Title');
      expect(entry.format).toEqual(['Vinyl', 'LP']);
      expect(entry.country).toBe('US');
      expect(entry.year).toBe('2023');
      expect(entry.uri).toBe('https://example.com');
      expect(entry.numberForSale).toBe(5);
      expect(entry.lowestPrice).toBe(19.99);
    });

    it('should allow null for lowestPrice', () => {
      const entry: Entry = {
        id: '123',
        lowestPrice: null,
      };

      expect(entry.lowestPrice).toBeNull();
    });
  });

  describe('QueryResult interface', () => {
    it('should allow creating a query result with results', () => {
      const entry: Entry = { id: '123', title: 'Test' };
      const queryResult: QueryResult = {
        results: {
          query1: [entry],
        },
      };

      expect(queryResult.results.query1).toHaveLength(1);
      expect(queryResult.results.query1[0].id).toBe('123');
    });
  });

  describe('ResultsProps interface', () => {
    it('should accept an array of QueryResult objects', () => {
      const entry: Entry = { id: '123', title: 'Test' };
      const queryResult: QueryResult = {
        results: {
          query1: [entry],
        },
      };

      const props: ResultsProps = {
        response: [queryResult],
      };

      expect(props.response).toHaveLength(1);
      expect(props.response[0].results.query1[0].id).toBe('123');
    });
  });
});
