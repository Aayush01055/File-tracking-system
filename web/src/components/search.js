import React, { useState } from 'react';
import axios from 'axios';
import { 
  Box, 
  TextField, 
  Button, 
  Table, 
  TableBody, 
  TableCell, 
  TableContainer, 
  TableHead, 
  TableRow, 
  Chip,
  Typography,
  Paper,
  CircularProgress
} from '@mui/material';
import { Search as SearchIcon } from '@mui/icons-material';
import { motion } from 'framer-motion';

const Search = ({ user, showModal }) => {
  // Properly declare all state variables
  const [query, setQuery] = useState('');
  const [files, setFiles] = useState([]);
  const [isSearching, setIsSearching] = useState(false);

  // Define the search handler function
  const handleSearch = async () => {
    if (!query) {
      showModal('Please enter a search query', 'error');
      return;
    }
    setIsSearching(true);
    try {
      const response = await axios.get(`http://localhost:8080/api/files/search?query=${query}`, {
        headers: { 'User-Id': user.userId }
      });
      setFiles(response.data);
      showModal(`Found ${response.data.length} files`);
    } catch (error) {
      showModal('Error: ' + (error.response?.data || error.message), 'error');
      setFiles([]);
    } finally {
      setIsSearching(false);
    }
  };

  return (
    <Paper 
      component={motion.div}
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      sx={{ 
        p: 4, 
        borderRadius: 3,
        boxShadow: '0 8px 32px rgba(0,0,0,0.05)'
      }}
    >
      <Typography variant="h5" sx={{ 
        fontWeight: 700, 
        mb: 3,
        color: 'text.primary'
      }}>
        Search Files
      </Typography>
      
      <Box display="flex" gap={2} mb={4}>
        <TextField
          fullWidth
          variant="outlined"
          placeholder="Search by title or status"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          InputProps={{
            startAdornment: <SearchIcon color="action" sx={{ mr: 1 }} />,
            sx: { borderRadius: 2 }
          }}
        />
        <Button
          component={motion.button}
          whileHover={{ scale: 1.02 }}
          whileTap={{ scale: 0.98 }}
          variant="contained"
          onClick={handleSearch}
          disabled={isSearching}
          startIcon={isSearching ? <CircularProgress size={20} /> : null}
        >
          {isSearching ? 'Searching...' : 'Search'}
        </Button>
      </Box>
      
      {files.length > 0 ? (
        <TableContainer 
          component={Paper}
          elevation={0}
          sx={{ 
            borderRadius: 2,
            border: '1px solid',
            borderColor: 'divider'
          }}
        >
          <Table>
            <TableHead sx={{ bgcolor: 'background.default' }}>
              <TableRow>
                <TableCell sx={{ fontWeight: 600 }}>ID</TableCell>
                <TableCell sx={{ fontWeight: 600 }}>Title</TableCell>
                <TableCell sx={{ fontWeight: 600 }}>Status</TableCell>
                <TableCell sx={{ fontWeight: 600 }}>Officer</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {files.map(file => (
                <TableRow 
                  key={file.id}
                  hover
                  sx={{ '&:last-child td': { borderBottom: 0 } }}
                >
                  <TableCell>{file.id}</TableCell>
                  <TableCell>{file.title}</TableCell>
                  <TableCell>
                    <Chip
                      label={file.status}
                      color={
                        file.status === 'Completed' ? 'success' : 
                        file.status === 'In Progress' ? 'info' : 'default'
                      }
                      size="small"
                      sx={{ borderRadius: 1 }}
                    />
                  </TableCell>
                  <TableCell>{file.currentOfficer}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      ) : (
        <Box 
          textAlign="center" 
          p={4}
          sx={{ bgcolor: 'background.default', borderRadius: 2 }}
        >
          <SearchIcon sx={{ fontSize: 48, color: 'text.disabled', mb: 2 }} />
          <Typography variant="h6" color="text.secondary">
            {query ? 'No files found' : 'Enter a search query'}
          </Typography>
          <Typography variant="body2" color="text.secondary">
            {query ? 'Try a different search term' : 'Search by title or status'}
          </Typography>
        </Box>
      )}
    </Paper>
  );
};

export default Search;
