import React, { useState } from 'react';

import { 
  Container,
  TextField,
  Button,
  Typography,
  List,
  ListItem,
  ListItemText,
  Box,
  Paper,
  Chip,
  Divider,
  IconButton,
  InputAdornment,
  Skeleton,
  useTheme
} from '@mui/material';
import { Search, Refresh, Event, Person } from '@mui/icons-material';
import axios from 'axios';
import { format } from 'date-fns';

function Audit() {
  const [fileId, setFileId] = useState('');
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(false);
  const theme = useTheme();

  const fetchAuditLogs = async () => {
    setLoading(true);
    try {
      const response = await axios.get(`http://localhost:8080/api/audit/${fileId}`);
      setLogs(response.data);
    } catch (error) {
      console.error('Error fetching audit logs:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      fetchAuditLogs();
    }
  };

  const getActionColor = (action) => {
    switch (action.toLowerCase()) {
      case 'create': return 'success';
      case 'delete': return 'error';
      case 'update': return 'warning';
      default: return 'primary';
    }
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Paper elevation={0} sx={{ 
        p: 4, 
        borderRadius: 4,
        background: theme.palette.mode === 'dark' ? theme.palette.background.paper : '#f8fafc',
      }}>
        <Box display="flex" justifyContent="space-between" alignItems="center" mb={4}>
          <Typography variant="h4" fontWeight="600">
            Audit Trail
          </Typography>
          <Box display="flex" gap={2}>
            <IconButton onClick={fetchAuditLogs} color="primary">
              <Refresh />
            </IconButton>
          </Box>
        </Box>
        
        <Box display="flex" gap={2} mb={4}>
          <TextField
            fullWidth
            variant="outlined"
            label="Search by File ID"
            value={fileId}
            onChange={(e) => setFileId(e.target.value)}
            onKeyPress={handleKeyPress}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <Search />
                </InputAdornment>
              ),
            }}
            sx={{
              '& .MuiOutlinedInput-root': {
                borderRadius: 3,
              }
            }}
          />
          <Button 
            variant="contained" 
            onClick={fetchAuditLogs}
            disabled={!fileId || loading}
            sx={{
              borderRadius: 3,
              px: 4,
              textTransform: 'none',
              fontWeight: '600'
            }}
          >
            Search
          </Button>
        </Box>
        
        {loading ? (
          <Box>
            {[...Array(5)].map((_, index) => (
              <Skeleton key={index} variant="rounded" height={80} sx={{ mb: 2 }} />
            ))}
          </Box>
        ) : logs.length > 0 ? (
          <Paper elevation={0} sx={{ borderRadius: 3, overflow: 'hidden' }}>
            <List disablePadding>
              {logs.map((log, index) => (
                <React.Fragment key={log.logId}>
                  <ListItem sx={{ 
                    py: 3,
                    backgroundColor: theme.palette.mode === 'dark' ? 
                      theme.palette.grey[900] : theme.palette.grey[50]
                  }}>
                    <Box sx={{ display: 'flex', flexDirection: 'column', width: '100%' }}>
                      <Box display="flex" justifyContent="space-between" alignItems="center" mb={1}>
                        <Box display="flex" alignItems="center" gap={1}>
                          <Chip 
                            label={log.action} 
                            color={getActionColor(log.action)}
                            size="small"
                            sx={{ fontWeight: '600' }}
                          />
                          <Typography variant="body2" color="text.secondary">
                            File ID: {log.fileId || 'N/A'}
                          </Typography>
                        </Box>
                        <Typography variant="caption" color="text.secondary">
                          {format(new Date(log.timestamp), 'PPpp')}
                        </Typography>
                      </Box>
                      
                      <Typography variant="body1" fontWeight="500" mb={1}>
                        {log.details || 'No additional details'}
                      </Typography>
                      
                      <Box display="flex" gap={2}>
                        <Box display="flex" alignItems="center" gap={0.5}>
                          <Person fontSize="small" color="action" />
                          <Typography variant="body2" color="text.secondary">
                            {log.userId || 'Unknown user'}
                          </Typography>
                        </Box>
                        <Box display="flex" alignItems="center" gap={0.5}>
                          <Event fontSize="small" color="action" />
                          <Typography variant="body2" color="text.secondary">
                            {format(new Date(log.timestamp), 'PP')}
                          </Typography>
                        </Box>
                      </Box>
                    </Box>
                  </ListItem>
                  {index < logs.length - 1 && <Divider />}
                </React.Fragment>
              ))}
            </List>
          </Paper>
        ) : (
          <Box textAlign="center" py={6}>
            <Typography variant="h6" color="text.secondary">
              {fileId ? 'No audit logs found' : 'Enter a File ID to search for audit logs'}
            </Typography>
          </Box>
        )}
      </Paper>
    </Container>
  );
}

export default Audit;