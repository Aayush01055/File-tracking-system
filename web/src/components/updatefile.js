import React, { useState, useEffect } from 'react';
import {
  Paper,
  Typography,
  TextField,
  MenuItem,
  Button,
  Box,
  CircularProgress
} from '@mui/material';
import axios from 'axios';

const UpdateFile = ({ user, showModal }) => {
  const [fileId, setFileId] = useState('');
  const [formData, setFormData] = useState({
    title: '',
    status: '',
    currentOfficer: '',
    courseCode: '',
    examSession: ''
  });
  const [officers, setOfficers] = useState([]);
  const [isFormEnabled, setIsFormEnabled] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    if (user.role === 'admin') {
      axios
        .get('http://localhost:8080/api/users?roles=officer,admin', {
          headers: { 'User-Id': user.userId }
        })
        .then(response => setOfficers(response.data))
        .catch(error => showModal('Error fetching officers: ' + (error.response?.data || error.message), 'error'));
    }
  }, [user.userId, user.role, showModal]);

  const handleLoadFile = async () => {
    if (!fileId) {
      showModal('Please enter a file ID', 'error');
      return;
    }
    setIsLoading(true);
    try {
      const response = await axios.get(`http://localhost:8080/api/files/${fileId}`, {
        headers: { 'User-Id': user.userId }
      });
      setFormData({
        title: response.data.title,
        status: response.data.status,
        currentOfficer: response.data.currentOfficer,
        courseCode: response.data.courseCode,
        examSession: response.data.examSession
      });
      setIsFormEnabled(true);
      showModal('File loaded successfully');
    } catch (error) {
      showModal('Error: ' + (error.response?.data || error.message), 'error');
    } finally {
      setIsLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.status || !formData.currentOfficer) {
      showModal('Status and officer are required', 'error');
      return;
    }
    try {
      await axios.patch(`http://localhost:8080/api/files/${fileId}`, formData, {
        headers: { 'User-Id': user.userId }
      });
      showModal('File updated successfully');
    } catch (error) {
      showModal('Error: ' + (error.response?.data || error.message), 'error');
    }
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  return (
    <Box>
      <Typography variant="h5" gutterBottom fontWeight="bold">
        Update File
      </Typography>
      
      <Box display="flex" gap={2} alignItems="flex-end" mb={3}>
        <TextField
          fullWidth
          label="File ID"
          value={fileId}
          onChange={(e) => setFileId(e.target.value)}
          variant="outlined"
        />
        <Button
          variant="contained"
          onClick={handleLoadFile}
          disabled={isLoading}
          sx={{ height: 56 }}
        >
          {isLoading ? <CircularProgress size={24} /> : 'Load'}
        </Button>
      </Box>
      
      <Box component="form" onSubmit={handleSubmit}>
        <TextField
          fullWidth
          label="Title"
          name="title"
          value={formData.title}
          onChange={handleChange}
          margin="normal"
          variant="outlined"
          disabled={!isFormEnabled}
        />
        
        <TextField
          fullWidth
          select
          label="Status"
          name="status"
          value={formData.status}
          onChange={handleChange}
          margin="normal"
          variant="outlined"
          disabled={!isFormEnabled}
        >
          <MenuItem value="">Select Status</MenuItem>
          <MenuItem value="Draft">Draft</MenuItem>
          <MenuItem value="In Progress">In Progress</MenuItem>
          <MenuItem value="Completed">Completed</MenuItem>
        </TextField>
        
        <TextField
          fullWidth
          select
          label="Current Officer"
          name="currentOfficer"
          value={formData.currentOfficer}
          onChange={handleChange}
          margin="normal"
          variant="outlined"
          disabled={!isFormEnabled}
        >
          <MenuItem value="">Select Officer</MenuItem>
          {officers.map(officer => (
            <MenuItem key={officer.userId} value={officer.userId}>
              {officer.username || officer.userId}
            </MenuItem>
          ))}
        </TextField>
        
        <TextField
          fullWidth
          label="Course Code"
          name="courseCode"
          value={formData.courseCode}
          onChange={handleChange}
          margin="normal"
          variant="outlined"
          disabled={!isFormEnabled}
        />
        
        <TextField
          fullWidth
          label="Exam Session"
          name="examSession"
          value={formData.examSession}
          onChange={handleChange}
          margin="normal"
          variant="outlined"
          disabled={!isFormEnabled}
        />
        
        <Box mt={3}>
          <Button
            type="submit"
            variant="contained"
            color="primary"
            disabled={!isFormEnabled}
            fullWidth
            size="large"
          >
            Update File
          </Button>
        </Box>
      </Box>
    </Box>
  );
};

export default UpdateFile;