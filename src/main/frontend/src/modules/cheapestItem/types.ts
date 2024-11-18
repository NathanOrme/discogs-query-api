/**
 * Interface for item details.
 */
export interface Item {
  title: string;
  lowestPrice?: number | null;
  numberForSale?: number | null;
  country?: string;
  uri?: string;
}
