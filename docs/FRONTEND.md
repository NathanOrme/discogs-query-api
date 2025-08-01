# Frontend Architecture Documentation

## Overview

The Discogs Query API frontend is a modern React 19 application built with TypeScript, Material-UI, and Vite. It provides a user-friendly step-by-step wizard interface for searching the Discogs catalog and displaying enriched results with marketplace data.

## Technology Stack

- **React 19**: Latest React with concurrent features
- **TypeScript**: Strict type checking and modern JavaScript features
- **Material-UI v7**: Comprehensive component library with dark theme
- **Vite**: Fast build tool and development server
- **Jest + React Testing Library**: Comprehensive testing framework

## Project Structure

```
src/main/frontend/
├── src/
│   ├── components/           # Reusable UI components
│   │   ├── bars/            # Bar-related components
│   │   └── buttons/         # Button components
│   ├── modules/             # Feature-based modules
│   │   ├── queryFields/     # Query form management
│   │   ├── results/         # Results display
│   │   ├── cheapestItem/    # Cheapest item display
│   │   └── utils/           # Utility components
│   ├── pages/               # Page components (future routing)
│   ├── utils/               # General utilities
│   ├── App.tsx              # Main application component
│   ├── index.tsx            # Application entry point
│   └── types.ts             # Global type definitions
├── public/                  # Static assets
├── build/                   # Production build output
├── package.json             # Dependencies and scripts
├── tsconfig.json           # TypeScript configuration
├── vite.config.ts          # Vite build configuration
└── jest.config.cjs         # Jest testing configuration
```

## Core Architecture Patterns

### 1. Step-Based Wizard Pattern

The application implements a linear workflow using Material-UI's Stepper component:

```typescript
const steps = ['Queries', 'Search', 'Results', 'Cheapest Items'];
const [activeStep, setActiveStep] = useState<number>(0);

// Step navigation
const handleNext = () => setActiveStep((prev) => Math.min(prev + 1, steps.length - 1));
const handleBack = () => setActiveStep((prev) => Math.max(prev - 1, 0));
```

**Step Flow**:
1. **Queries** - Configure search parameters with dynamic form fields
2. **Search** - Execute search with optional Discogs username
3. **Results** - Display organized search results with marketplace data
4. **Cheapest Items** - Show summary of cheapest available items

### 2. Component Architecture

#### Main Components

**App.tsx** - Root component managing global state and navigation
```typescript
const App: React.FC = () => {
  const [queries, setQueries] = useState<Query[]>([{ id: 1 }]);
  const [cheapestItems, setCheapestItems] = useState<any[]>([]);
  const [response, setResponse] = useState<QueryResult[]>([]);
  
  // Conditional rendering based on active step
  return (
    <Container maxWidth="md">
      <Stepper activeStep={activeStep}>
        {/* Step indicators */}
      </Stepper>
      {activeStep === 0 && <QueryFields onQueriesChange={handleQueriesChange} />}
      {activeStep === 1 && <SearchForm queries={queries} setResponse={setResponse} />}
      {/* ... other steps */}
    </Container>
  );
};
```

#### Module Components

**QueryFields** - Dynamic query form management
- Add/remove query functionality
- Form validation with Material-UI components
- Responsive grid layout for form fields

**SearchForm** - API interaction component
- HTTP requests to backend API
- Loading states and error handling
- Progress indication during search

**Results** - Search results display
- Organized display of search results
- Marketplace data integration
- Export functionality for JSON data

**CheapestItem** - Summary component
- Cheapest item calculation and display
- Price comparison across results
- Currency formatting

### 3. State Management

#### Local State Pattern
- Uses React hooks (useState, useCallback, useEffect)
- Props drilling for parent-child communication
- Controlled components for all form inputs

#### State Flow
```typescript
// Top-level state in App.tsx
queries → QueryFields (editing) → SearchForm (execution) → Results (display)
                                                         ↓
                                              CheapestItem (summary)
```

#### Key State Objects
```typescript
interface Query {
  id?: number;
  artist?: string;
  album?: string;
  track?: string;
  format?: string;
  country?: string;
  types?: string;
  barcode?: string;
}

interface QueryResult {
  originalQuery: Query;
  results: Record<string, Entry[]>;
  cheapestItem?: CheapestItemType;
}
```

## Component Development Patterns

### 1. TypeScript Integration

#### Interface-First Design
```typescript
interface QueryFieldsProps {
  onQueriesChange: (queries: Query[]) => void;
}

interface SearchFormProps {
  queries: Query[];
  setResponse: (response: QueryResult[]) => void;
  onCheapestItemsChange: (items: any[]) => void;
  onStepComplete: () => void;
}
```

#### Type Safety Features
- Strict TypeScript configuration
- Optional properties for flexible APIs
- Proper callback typing
- Generic type constraints where applicable

### 2. Material-UI Integration

#### Theme System
```typescript
// Global dark theme application
<ThemeProvider theme={createTheme({ palette: { mode: 'dark' } })}>
  <CssBaseline />
  <App />
</ThemeProvider>
```

#### Component Styling
```typescript
// sx prop pattern for consistent styling
<Box sx={{ 
  display: 'flex', 
  pt: 2,
  gap: 2 
}}>
  <Button disabled={activeStep === 0} onClick={handleBack}>
    Back
  </Button>
</Box>
```

#### Responsive Design
- Container maxWidth for optimal reading
- Grid system for form layouts
- Mobile-friendly component sizing

### 3. Error Handling

#### Error Boundary Pattern
```typescript
// ErrorMessage utility component
const ErrorMessage: React.FC<{ message: string }> = ({ message }) => (
  <Alert severity="error" sx={{ mt: 2 }}>
    {message}
  </Alert>
);
```

#### API Error Handling
- Network error detection
- User-friendly error messages
- Graceful degradation for missing data

## Testing Strategy

### Testing Framework Setup
```javascript
// jest.config.cjs
module.exports = {
  testEnvironment: 'jsdom',
  setupFilesAfterEnv: ['<rootDir>/src/setupTests.ts'],
  moduleNameMapping: {
    '\\.(css|less|scss|sass)$': 'identity-obj-proxy'
  },
  transform: {
    '^.+\\.(ts|tsx)$': 'ts-jest'
  }
};
```

### Testing Patterns

#### Component Isolation
```typescript
// Mock child components for focused testing
jest.mock('./modules/HeroBanner', () => () => 
  <div data-testid="hero-banner">Hero Banner</div>
);

describe('App Component', () => {
  it('renders all main components', () => {
    render(<App />);
    expect(screen.getByTestId('hero-banner')).toBeInTheDocument();
  });
});
```

#### User Interaction Testing
```typescript
it('navigates between steps correctly', () => {
  render(<App />);
  
  // Test forward navigation
  fireEvent.click(screen.getByText('Next'));
  expect(screen.getByTestId('search-form')).toBeInTheDocument();
  
  // Test backward navigation
  fireEvent.click(screen.getByText('Back'));
  expect(screen.getByTestId('query-fields')).toBeInTheDocument();
});
```

#### API Integration Testing
```typescript
// Mock API calls for predictable testing
global.fetch = jest.fn().mockResolvedValue({
  ok: true,
  json: () => Promise.resolve(mockApiResponse)
});
```

### Test Coverage Areas
- Component rendering verification
- User interaction flows
- Form validation and submission
- API error handling
- State management correctness

## Build Configuration

### Vite Configuration

#### Custom Plugins for CRA Compatibility
```typescript
// Environment variable support
function envPlugin(): Plugin {
  return {
    name: 'env-plugin',
    config(_, { mode }) {
      const env = loadEnv(mode, '.', ['REACT_APP_', 'NODE_ENV', 'PUBLIC_URL']);
      return {
        define: Object.fromEntries(
          Object.entries(env).map(([key, value]) => [
            `process.env.${key}`,
            JSON.stringify(value)
          ])
        )
      };
    }
  };
}
```

#### SVG as React Components
```typescript
// SVGR integration for SVG imports
function svgrPlugin(): Plugin {
  return {
    name: 'svgr-plugin',
    async transform(code, id) {
      if (filter(id)) {
        const { transform } = await import('@svgr/core');
        // Transform SVG to React component
      }
    }
  };
}
```

### Development Scripts
```json
{
  "scripts": {
    "start": "vite",                    // Development server
    "build": "vite build",              // Production build
    "test": "jest --config jest.config.cjs", // Run tests
    "serve": "serve -s build",          // Serve production build
    "format": "prettier --write .",     // Code formatting
    "dev": "vite"                       // Alternative dev command
  }
}
```

## Development Workflow

### Local Development
1. **Start Development Server**:
   ```bash
   cd src/main/frontend
   yarn start
   ```
   - Hot module replacement
   - TypeScript error reporting
   - Automatic browser refresh

2. **API Integration**:
   - Dynamic backend URL detection
   - Development: `http://localhost:9090`
   - Production: Environment-specific URLs

### Testing Workflow
```bash
# Run all tests
yarn test

# Watch mode for development
yarn test --watch

# Coverage report
yarn test --coverage

# Specific test files
yarn test QueryFields.test.tsx
```

### Build Process
```bash
# Production build
yarn build

# Preview production build
yarn serve
```

## Performance Optimizations

### React Optimizations
- **useCallback**: Prevents unnecessary re-renders
- **Controlled Components**: Efficient state updates
- **Component Memoization**: Strategic use of React.memo (where needed)

### Bundle Optimizations
- **Tree Shaking**: Vite automatically removes unused code
- **Code Splitting**: Dynamic imports for large components (opportunity)
- **Asset Optimization**: Vite handles image and asset optimization

### Material-UI Optimizations
- **Component Imports**: Direct component imports to reduce bundle size
- **Theme Caching**: Efficient theme application
- **CSS-in-JS**: Runtime styling optimization

## Future Enhancements

### Recommended Improvements
1. **State Management**: Consider Context API or Zustand for complex state
2. **Routing**: Implement React Router for URL-based navigation
3. **Error Boundaries**: Add error boundary components
4. **Code Splitting**: Implement dynamic imports for larger components
5. **PWA Features**: Add service worker and offline capabilities
6. **Accessibility**: Enhanced ARIA support and keyboard navigation

### Potential Features
- **Real-time Updates**: WebSocket integration for live search results
- **Advanced Filtering**: Client-side filtering and sorting
- **Export Options**: Multiple export formats (CSV, PDF)
- **Favorites System**: Save and manage favorite searches
- **Dark/Light Theme Toggle**: User preference management

## Best Practices

### Code Organization
- Feature-based module organization
- Consistent naming conventions
- Clear separation of concerns
- Comprehensive TypeScript usage

### Component Design
- Single responsibility principle
- Props interface definition
- Error boundary integration
- Accessibility considerations

### Testing Strategy
- Component isolation testing
- User-centric test queries
- Comprehensive mocking strategy
- Regular test maintenance

This frontend architecture provides a solid foundation for a modern React application with room for future growth and enhancement.