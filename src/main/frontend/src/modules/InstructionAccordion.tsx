import React from 'react';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';

const steps = [
  { label: 'Barcode', description: 'Enter the barcode for the product you are searching for.' },
  { label: 'Artist', description: 'Enter the name of the artist you are searching for.' },
  { label: 'Track (optional)', description: 'Enter the name of the track. Leave blank if not needed.' },
  { label: 'Album (optional)', description: 'Enter the name of the album. Leave blank if not needed.' },
  { label: 'Format (optional)', description: 'Select the format of the release (e.g., Vinyl, Compilation).' },
  { label: 'Add Query', description: 'Click "Add Another Query" to add more search criteria.' },
  { label: 'Username (optional)', description: 'Add your username to cross-reference your Discogs collection.' },
  { label: 'Search', description: 'Click "Search" to submit your queries and view results.' },
];

// Simplified step rendering without MUI List for reliable tests
// Removed List, ListItem, ListItemText to directly render with Typography
const InstructionAccordion: React.FC = () => (
  <Box sx={{ my: 2 }}>
    <Typography variant="h6">How to Use This Search Tool</Typography>
    {steps.map(step => (
      <Box key={step.label} sx={{ my: 1 }}>
        <Typography variant="subtitle1">{step.label}</Typography>
        <Typography variant="body2">{step.description}</Typography>
      </Box>
    ))}
  </Box>
);

export default InstructionAccordion;
