// src/App.tsx

import React, { useState, useCallback } from 'react';
import Container from '@mui/material/Container';
import Box from '@mui/material/Box';
import Header from './modules/Header';
import InstructionAccordion from './modules/InstructionAccordion';
import QueryFields from './modules/QueryFields';
import SearchForm from './modules/SearchForm';
import CheapestItem from './modules/CheapestItem';
import Results from './modules/Results';
import HeroBanner from './modules/HeroBanner';
import { Query, QueryResult } from './modules/types';
import Stepper from '@mui/material/Stepper';
import Step from '@mui/material/Step';
import StepLabel from '@mui/material/StepLabel';
import Button from '@mui/material/Button';

const App: React.FC = () => {
  const [queries, setQueries] = useState<Query[]>([{ id: 1 }]);
  const [cheapestItems, setCheapestItems] = useState<any[]>([]);
  const [response, setResponse] = useState<QueryResult[]>([]);
  const steps = ['Queries', 'Search', 'Results', 'Cheapest Items'];
  const [activeStep, setActiveStep] = useState<number>(0);
  const handleNext = () => setActiveStep(prev => Math.min(prev + 1, steps.length - 1));
  const handleBack = () => setActiveStep(prev => Math.max(prev - 1, 0));

  const handleQueriesChange = useCallback((newQueries: Query[]) => {
    setQueries(newQueries);
  }, []);

  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      <HeroBanner />
      <Header />
      <Box sx={{ my: 2 }}>
        <InstructionAccordion />
      </Box>
      <Stepper activeStep={activeStep} alternativeLabel sx={{ mb: 3 }}>
        {steps.map(label => (
          <Step key={label}>
            <StepLabel>{label}</StepLabel>
          </Step>
        ))}
      </Stepper>
      <Box>
        {activeStep === 0 && <QueryFields onQueriesChange={handleQueriesChange} />}
        {activeStep === 1 && (
          <SearchForm
            queries={queries}
            setResponse={setResponse}
            onCheapestItemsChange={setCheapestItems}
            onStepComplete={handleNext}
          />
        )}
        {activeStep === 2 && <Results response={response} />}
        {activeStep === 3 && <CheapestItem items={cheapestItems} />}
      </Box>
      <Box sx={{ display: 'flex', pt: 2 }}>
        <Button disabled={activeStep === 0} onClick={handleBack}>
          Back
        </Button>
        <Box sx={{ flex: '1 1 auto' }} />
        <Button disabled={activeStep === steps.length - 1} onClick={handleNext}>
          Next
        </Button>
      </Box>
    </Container>
  );
};

export default App;
