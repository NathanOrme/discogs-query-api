import React from 'react';
import Accordion from '@mui/material/Accordion';
import AccordionSummary from '@mui/material/AccordionSummary';
import AccordionDetails from '@mui/material/AccordionDetails';
import Typography from '@mui/material/Typography';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemText from '@mui/material/ListItemText';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';

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

const InstructionAccordion: React.FC = () => (
  <Accordion defaultExpanded>
    <AccordionSummary expandIcon={<ExpandMoreIcon />}>
      <Typography variant="h6">How to Use This Search Tool</Typography>
    </AccordionSummary>
    <AccordionDetails>
      <List>
        {steps.map(step => (
          <ListItem key={step.label} divider>
            <ListItemText primary={step.label} secondary={step.description} />
          </ListItem>
        ))}
      </List>
    </AccordionDetails>
  </Accordion>
);

export default InstructionAccordion;
