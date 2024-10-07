// src/modules/discogsData.js
/**
 * An array of Discogs types for filtering items.
 * @type {Array<{ value: string, text: string }>}
 */
const discogsTypes = [
  { value: "RELEASE", text: "Release" },
  { value: "MASTER", text: "Master" },
  { value: "", text: "Select a type" },
];

/**
 * An array of Discogs formats for filtering items.
 * @type {Array<{ value: string, text: string }>}
 */
const discogFormats = [
  { value: "", text: "Any Format" },
  { value: "vinyl", text: "Vinyl" },
  { value: "album", text: "Album" },
  { value: "cd", text: "CD" },
  { value: "lp", text: "LP" },
  { value: "compilation", text: "Compilation" },
  { value: "album vinyl", text: "Album Vinyl" },
  { value: "compilation vinyl", text: "Compilation Vinyl" },
];

/**
 * An array of countries for filtering items on Discogs.
 * @type {Array<{ value: string, text: string }>}
 */
const discogCountries = [
  { value: "", text: "Any Country" },
  { value: "EUROPE", text: "Europe" },
  { value: "UK", text: "UK" },
  { value: "US", text: "US" },
  { value: "COSTA_RICA", text: "Costa Rica" },
  { value: "CUBA", text: "Cuba" },
  { value: "ISRAEL", text: "Israel" },
  { value: "BRAZIL", text: "Brazil" },
  { value: "GREECE", text: "Greece" },
  { value: "URUGUAY", text: "Uruguay" },
  { value: "LATVIA", text: "Latvia" },
  { value: "AUSTRIA", text: "Austria" },
  { value: "ALBANIA", text: "Albania" },
  { value: "AUSTRALIA", text: "Australia" },
  { value: "NEW_ZEALAND", text: "New Zealand" },
  { value: "PARAGUAY", text: "Paraguay" },
  { value: "PERU", text: "Peru" },
  { value: "HUNGARY", text: "Hungary" },
  { value: "NETHERLANDS", text: "Netherlands" },
  { value: "CANADA", text: "Canada" },
  { value: "HONG_KONG", text: "Hong Kong" },
  { value: "TRINIDAD", text: "Trinidad" },
  { value: "TOBAGO", text: "Tobago" },
  { value: "ESTONIA", text: "Estonia" },
  { value: "FIJI", text: "Fiji" },
  { value: "PHILIPPINES", text: "Philippines" },
  { value: "GERMANY", text: "Germany" },
  { value: "GEORGIA", text: "Georgia" },
  { value: "NORWAY", text: "Norway" },
  { value: "CROATIA", text: "Croatia" },
  { value: "IRELAND", text: "Ireland" },
  { value: "INDIA", text: "India" },
  { value: "JAPAN", text: "Japan" },
  { value: "INDONESIA", text: "Indonesia" },
  { value: "DENMARK", text: "Denmark" },
  { value: "SOUTH_KOREA", text: "South Korea" },
  { value: "LITHUANIA", text: "Lithuania" },
  { value: "MALAYSIA", text: "Malaysia" },
  { value: "CHINA", text: "China" },
  { value: "KENYA", text: "Kenya" },
  { value: "TURKEY", text: "Turkey" },
  { value: "BULGARIA", text: "Bulgaria" },
  { value: "TAIWAN", text: "Taiwan" },
  { value: "SWEDEN", text: "Sweden" },
  { value: "FINLAND", text: "Finland" },
  { value: "ICELAND", text: "Iceland" },
  { value: "CZECH_REPUBLIC", text: "Czech Republic" },
  { value: "RUSSIAN_FEDERATION", text: "Russian Federation" },
  { value: "BELGIUM", text: "Belgium" },
  { value: "FRANCE", text: "France" },
  { value: "LUXEMBOURG", text: "Luxembourg" },
  { value: "MEXICO", text: "Mexico" },
  { value: "VENEZUELA", text: "Venezuela" },
  { value: "ARGENTINA", text: "Argentina" },
  { value: "SOUTH_AFRICA", text: "South Africa" },
  { value: "COLOMBIA", text: "Colombia" },
  { value: "SLOVENIA", text: "Slovenia" },
  { value: "CHILE", text: "Chile" },
  { value: "ITALY", text: "Italy" },
  { value: "THE_VATICAN", text: "The Vatican" },
  { value: "SAN_MARINO", text: "San Marino" },
  { value: "SERBIA", text: "Serbia" },
  { value: "MONTENEGRO", text: "Montenegro" },
  { value: "SLOVAKIA", text: "Slovakia" },
  { value: "PORTUGAL", text: "Portugal" },
  { value: "SWITZERLAND", text: "Switzerland" },
  { value: "LIECHTENSTEIN", text: "Liechtenstein" },
  { value: "UKRAINE", text: "Ukraine" },
  { value: "ROMANIA", text: "Romania" },
  { value: "POLAND", text: "Poland" },
];

export { discogsTypes, discogFormats, discogCountries };
