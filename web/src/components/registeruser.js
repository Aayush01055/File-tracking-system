import React, { useState } from 'react';
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

const RegisterUser = ({ user, showModal }) => {
  const [formData, setFormData] = useState({
    username: '',
    password: '',
    role: ''
  });
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);
    
    if (!formData.username || !formData.password || !formData.role) {
      showModal('All fields are required', 'error');
      setIsSubmitting(false);
      return;
    }
    
    try {
      const response = await axios.post('http://localhost:8080/api/auth/register', formData, {
        headers: { 'User-Id': user.userId }
      });
      showModal(`User ${response.data.username} registered successfully`);
      setFormData({ username: '', password: '', role: '' });
    } catch (error) {
      showModal('Error: ' + (error.response?.data || error.message), 'error');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  return (
    <Box>
      <Typography variant="h5" gutterBottom fontWeight="bold">
        Register User
      </Typography>
      
      <Box component="form" onSubmit={handleSubmit} sx={{ mt: 2 }}>
        <TextField
          fullWidth
          label="Username"
          name="username"
          value={formData.username}
          onChange={handleChange}
          margin="normal"
          variant="outlined"
        />
        
        <TextField
          fullWidth
          label="Password"
          name="password"
          type="password"
          value={formData.password}
          onChange={handleChange}
          margin="normal"
          variant="outlined"
        />
        
        <TextField
          fullWidth
          select
          label="Role"
          name="role"
          value={formData.role}
          onChange={handleChange}
          margin="normal"
          variant="outlined"
        >
          <MenuItem value="">Select Role</MenuItem>
          <MenuItem value="admin">Admin</MenuItem>
          <MenuItem value="officer">Officer</MenuItem>
          <MenuItem value="guest">Guest</MenuItem>
        </TextField>
        
        <Box mt={3}>
          <Button
            type="submit"
            variant="contained"
            color="primary"
            disabled={isSubmitting}
            fullWidth
            size="large"
          >
            {isSubmitting ? <CircularProgress size={24} /> : 'Register'}
          </Button>
        </Box>
      </Box>
    </Box>
  );
};

export default RegisterUser;