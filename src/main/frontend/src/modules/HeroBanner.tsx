import React from 'react';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';

const HeroBanner: React.FC = () => (
  <Box
    sx={{
      width: '100%',
      py: 8,
      mb: 4,
      background: 'linear-gradient(90deg, #2196f3 0%, #21cbf3 100%)',
      color: '#fff',
      textAlign: 'center',
    }}
  >
    <Typography variant="h2" component="h1" gutterBottom>
      Discogs Query
    </Typography>
    <Typography variant="h5">
      Search and explore vinyl releases effortlessly
    </Typography>
  </Box>
);

export default HeroBanner;
