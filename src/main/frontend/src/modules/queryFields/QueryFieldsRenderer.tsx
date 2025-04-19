// src/modules/QueryFieldsRenderer.tsx

import React from 'react';
import { Query } from './QueryFieldsTypes';
import { discogFormats } from './../DiscogsData';
import Box from '@mui/material/Box';
import TextField from '@mui/material/TextField';
import FormControl from '@mui/material/FormControl';
import InputLabel from '@mui/material/InputLabel';
import Select from '@mui/material/Select';
import MenuItem from '@mui/material/MenuItem';
import Button from '@mui/material/Button';

export const renderQueryFields = (
  query: Query,
  index: number,
  handleInputChange: (index: number, field: keyof Query, value: string) => void,
  removeQuery: (index: number) => void,
  queryCount: number
) => (
  <Box key={index} sx={{ border: 1, borderRadius: 1, p: 2, mb: 2 }}>
    <Box
      sx={{
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        mb: 1,
      }}
    >
      <span>Query {index + 1}</span>
      {queryCount > 1 && (
        <Button
          variant="outlined"
          color="error"
          size="small"
          onClick={() => removeQuery(index)}
        >
          Remove
        </Button>
      )}
    </Box>
    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
      {renderInputField(
        'Artist:',
        'artist',
        query.artist,
        index,
        handleInputChange
      )}
      {renderInputField(
        'Barcode:',
        'barcode',
        query.barcode,
        index,
        handleInputChange
      )}
      {renderInputField(
        'Album (optional):',
        'album',
        query.album,
        index,
        handleInputChange
      )}
      {renderInputField(
        'Track (optional):',
        'track',
        query.track,
        index,
        handleInputChange
      )}
      {renderSelectField(
        'Format (optional):',
        'format',
        query.format,
        discogFormats,
        index,
        handleInputChange
      )}
    </Box>
  </Box>
);

const renderInputField = (
  label: string,
  field: keyof Query,
  value: string,
  index: number,
  handleInputChange: (index: number, field: keyof Query, value: string) => void
) => (
  <TextField
    id={`${field}-${index}`}
    label={label}
    value={value}
    onChange={(e) => handleInputChange(index, field, e.target.value)}
    variant="outlined"
    size="small"
    fullWidth
    margin="normal"
  />
);

const renderSelectField = (
  label: string,
  field: keyof Query,
  value: string,
  options: { value: string; text: string }[],
  index: number,
  handleInputChange: (index: number, field: keyof Query, value: string) => void
) => (
  <FormControl variant="outlined" size="small" fullWidth margin="normal">
    <InputLabel id={`${field}-${index}-label`}>{label}</InputLabel>
    <Select
      labelId={`${field}-${index}-label`}
      id={`${field}-${index}`}
      value={value}
      onChange={(e) => handleInputChange(index, field, e.target.value)}
      label={label}
    >
      {options.map((option) => (
        <MenuItem key={option.value} value={option.value}>
          {option.text}
        </MenuItem>
      ))}
    </Select>
  </FormControl>
);
