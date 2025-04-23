import React, { useState } from 'react';
import axios from 'axios';
import { 
  Box, 
  TextField, 
  Button, 
  Typography, 
  Paper, 
  Grid, 
  Avatar,
  CircularProgress,
  Chip
} from '@mui/material';
import { Search as SearchIcon } from '@mui/icons-material';
import { motion } from 'framer-motion';

const TrackFile = ({ user, showModal }) => {
  // Properly declare all state variables
  const [fileId, setFileId] = useState('');
  const [file, setFile] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  // Define the track file handler function
  const handleTrackFile = async () => {
    if (!fileId) {
      showModal('Please enter a file ID', 'error');
      return;
    }
    setIsLoading(true);
    try {
      const response = await axios.get(`http://localhost:8080/api/files/${fileId}`, {
        headers: { 'User-Id': user.userId }
      });
      setFile(response.data);
      showModal('File loaded successfully');
    } catch (error) {
      showModal('Error: ' + (error.response?.data || error.message), 'error');
      setFile(null);
    } finally {
      setIsLoading(false);
    }
  };

  // Helper function for status colors
  const getStatusColor = (status) => {
    switch (status) {
      case 'Completed': return 'success';
      case 'In Progress': return 'info';
      default: return 'default';
    }
  };

  return (
    <Paper sx={{ p: 4, borderRadius: 3 }}>
      <Typography variant="h5" sx={{ fontWeight: 700, mb: 3 }}>
        Track File
      </Typography>
      
      <Box display="flex" gap={2} mb={4}>
        <TextField
          fullWidth
          label="File ID"
          value={fileId}
          onChange={(e) => setFileId(e.target.value)}
          InputProps={{
            startAdornment: <SearchIcon color="action" sx={{ mr: 1 }} />,
            sx: { borderRadius: 2 }
          }}
        />
        <Button
          component={motion.button}
          whileHover={{ scale: 1.02 }}
          variant="contained"
          onClick={handleTrackFile}
          disabled={isLoading}
          sx={{ height: 56 }}
        >
          {isLoading ? <CircularProgress size={24} /> : 'Track'}
        </Button>
      </Box>
      
      {file && (
        <Paper elevation={0} sx={{ p: 3, borderRadius: 2, bgcolor: 'background.default' }}>
          <Grid container spacing={2}>
            <Grid item xs={12} sm={6}>
              <Typography variant="subtitle2" color="text.secondary">
                Title
              </Typography>
              <Typography variant="body1">{file.title}</Typography>
            </Grid>
            <Grid item xs={12} sm={6}>
              <Typography variant="subtitle2" color="text.secondary">
                Status
              </Typography>
              <Chip
                label={file.status}
                color={getStatusColor(file.status)}
                size="small"
                sx={{ borderRadius: 1 }}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <Typography variant="subtitle2" color="text.secondary">
                Current Officer
              </Typography>
              <Box display="flex" alignItems="center" gap={1}>
                <Avatar sx={{ width: 24, height: 24, fontSize: '0.75rem' }}>
                  {file.currentOfficer?.charAt(0)}
                </Avatar>
                <Typography>{file.currentOfficer}</Typography>
              </Box>
            </Grid>
            <Grid item xs={12} sm={6}>
              <Typography variant="subtitle2" color="text.secondary">
                Course Code
              </Typography>
              <Typography>{file.courseCode}</Typography>
            </Grid>
            <Grid item xs={12} sm={6}>
              <Typography variant="subtitle2" color="text.secondary">
                Exam Session
              </Typography>
              <Typography>{file.examSession}</Typography>
            </Grid>
          </Grid>
        </Paper>
      )}
    </Paper>
  );
};

export default TrackFile;