import React, { useState, useEffect } from 'react';
import {
  Paper,
  Typography,
  TextField,
  MenuItem,
  Button,
  Box,
  CircularProgress,
  Grid
} from '@mui/material';
import axios from 'axios';

const CreateFile = ({ user, showModal }) => {
  const [formData, setFormData] = useState({
    title: '',
    status: '',
    currentOfficer: '',
    courseCode: '',
    examSession: ''
  });
  const [officers, setOfficers] = useState([]);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (user?.role === 'admin') {
      axios
        .get('http://localhost:8080/api/users?roles=officer,admin', {
          headers: { 'User-Id': user.userId }
        })
        .then(response => setOfficers(response.data))
        .catch(error => showModal(`Error fetching officers: ${error.response?.data?.message || error.message}`, 'error'));
    }
  }, [user, showModal]);

  const validateForm = () => {
    const newErrors = {};
    if (!formData.title.trim()) newErrors.title = 'Title is required';
    if (!formData.status) newErrors.status = 'Status is required';
    if (!formData.currentOfficer) newErrors.currentOfficer = 'Current Officer is required';
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateForm()) {
      showModal('Please fill all required fields', 'error');
      return;
    }
    setIsSubmitting(true);
    console.log('Sending payload:', formData); // Debug log
    try {
      await axios.post('http://localhost:8080/api/files/register', formData, {
        headers: { 'User-Id': user?.userId || '' }
      });
      showModal('File created successfully', 'success');
      setFormData({ title: '', status: '', currentOfficer: '', courseCode: '', examSession: '' });
      setErrors({});
    } catch (error) {
      const errorMessage = error.response?.data?.message || error.response?.data || error.message;
      console.error('Error response:', error.response); // Debug log
      showModal(`Error creating file: ${errorMessage}`, 'error');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleReset = () => {
    setFormData({ title: '', status: '', currentOfficer: '', courseCode: '', examSession: '' });
    setErrors({});
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    setErrors({ ...errors, [e.target.name]: '' });
  };

  if (!user) {
    return <Typography color="error">User not authenticated</Typography>;
  }

  return (
    <Paper elevation={3} sx={{ p: 3, maxWidth: 600, mx: 'auto', mt: 4 }}>
      <Typography variant="h5" gutterBottom fontWeight="bold" aria-label="Create File Form">
        Create File
      </Typography>
      
      <Box component="form" onSubmit={handleSubmit} sx={{ mt: 2 }}>
        <Grid container spacing={2}>
          <Grid item xs={12}>
            <TextField
              fullWidth
              label="Title"
              name="title"
              value={formData.title}
              onChange={handleChange}
              margin="normal"
              variant="outlined"
              required
              error={!!errors.title}
              helperText={errors.title}
              aria-required="true"
            />
          </Grid>
          
          <Grid item xs={12}>
            <TextField
              fullWidth
              select
              label="Status"
              name="status"
              value={formData.status}
              onChange={handleChange}
              margin="normal"
              variant="outlined"
              required
              error={!!errors.status}
              helperText={errors.status}
              aria-required="true"
            >
              <MenuItem value="">Select Status</MenuItem>
              <MenuItem value="Draft">Draft</MenuItem>
              <MenuItem value="In Progress">In Progress</MenuItem>
              <MenuItem value="Completed">Completed</MenuItem>
            </TextField>
          </Grid>
          
          <Grid item xs={12}>
            <TextField
              fullWidth
              select
              label="Current Officer"
              name="currentOfficer"
              value={formData.currentOfficer}
              onChange={handleChange}
              margin="normal"
              variant="outlined"
              required
              error={!!errors.currentOfficer}
              helperText={errors.currentOfficer}
              aria-required="true"
              disabled={user.role !== 'admin'}
            >
              <MenuItem value="">Select Officer</MenuItem>
              {officers.length > 0 ? (
                officers.map(officer => (
                  <MenuItem key={officer.userId} value={officer.userId}>
                    {officer.username || `Officer (${officer.userId})`}
                  </MenuItem>
                ))
              ) : (
                <MenuItem disabled>No officers available</MenuItem>
              )}
            </TextField>
          </Grid>
          
          <Grid item xs={12}>
            <TextField
              fullWidth
              label="Course Code"
              name="courseCode"
              value={formData.courseCode}
              onChange={handleChange}
              margin="normal"
              variant="outlined"
            />
          </Grid>
          
          <Grid item xs={12}>
            <TextField
              fullWidth
              label="Exam Session"
              name="examSession"
              value={formData.examSession}
              onChange={handleChange}
              margin="normal"
              variant="outlined"
            />
          </Grid>
          
          <Grid item xs={12} sm={6}>
            <Button
              type="submit"
              variant="contained"
              color="primary"
              disabled={isSubmitting}
              fullWidth
              size="large"
              aria-label="Submit Create File Form"
            >
              {isSubmitting ? <CircularProgress size={24} /> : 'Create File'}
            </Button>
          </Grid>
          
          <Grid item xs={12} sm={6}>
            <Button
              variant="outlined"
              color="secondary"
              onClick={handleReset}
              fullWidth
              size="large"
              disabled={isSubmitting}
              aria-label="Reset Form"
            >
              Reset
            </Button>
          </Grid>
        </Grid>
      </Box>
    </Paper>
  );
};

export default CreateFile;