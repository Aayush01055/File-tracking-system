import React, { useState } from 'react';
import { Container, Paper, Box, Typography, Avatar, Button,ThemeProvider, CssBaseline } from '@mui/material';
import { Logout } from '@mui/icons-material';
import Login from './components/login';
import CreateFile from './components/cretatefile';
import UpdateFile from './components/updatefile';
import RegisterUser from './components/registeruser';
import TrackFile from './components/filetracking';
import Audit from './components/audit';
import Sidebar from './components/sidebar';
import Search from './components/search';
import theme from './theme';



const App = () => {

  const [user, setUser] = useState({
    userId: localStorage.getItem('userId') || '',
    username: localStorage.getItem('username') || '',
    role: localStorage.getItem('role') || ''
  });
  const [view, setView] = useState(user.role === 'admin' ? 'create' : 'track');
  const [modal, setModal] = useState({ isOpen: false, message: '', type: 'success' });

  const handleLogout = () => {
    setUser({ userId: '', username: '', role: '' });
    localStorage.removeItem('userId');
    localStorage.removeItem('username');
    localStorage.removeItem('role');
  };

  const showModal = (message, type = 'success') => {
    setModal({ isOpen: true, message, type });
    setTimeout(() => setModal({ isOpen: false, message: '', type: 'success' }), 3000);
  };

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
    <Container maxWidth="xl" disableGutters sx={{ minHeight: '100vh', bgcolor: 'background.default' }}>
      {modal.isOpen && (
        <Box sx={{
          position: 'fixed',
          top: 16,
          right: 16,
          p: 2,
          borderRadius: 1,
          boxShadow: 3,
          zIndex: 1300,
          display: 'flex',
          alignItems: 'center',
          bgcolor: modal.type === 'success' ? 'success.main' : 'error.main',
          color: 'common.white'
        }}>
          <Typography>{modal.message}</Typography>
        </Box>
      )}
      
      {user.userId ? (
        <Box display="flex">
          <Sidebar user={user} view={view} setView={setView} />
          
          <Box flexGrow={1} p={3}>
            <Box display="flex" justifyContent="space-between" alignItems="center" mb={4}>
              <Typography variant="h4" fontWeight="bold">
                File Tracking System
              </Typography>
              <Box display="flex" alignItems="center" gap={2}>
                <Box display="flex" alignItems="center" gap={1}>
                  <Avatar sx={{ bgcolor: 'primary.light', width: 32, height: 32 }}>
                    {user.username.charAt(0).toUpperCase()}
                  </Avatar>
                  <Typography variant="body1">{user.username}</Typography>
                </Box>
                <Button
                  variant="outlined"
                  startIcon={<Logout />}
                  onClick={handleLogout}
                  sx={{ textTransform: 'none' }}
                >
                  Logout
                </Button>
              </Box>
            </Box>
            
            <Paper elevation={0} sx={{ p: 3, borderRadius: 2 }}>
              {view === 'create' && user.role === 'admin' && <CreateFile user={user} showModal={showModal} />}
              {view === 'update' && user.role === 'admin' && <UpdateFile user={user} showModal={showModal} />}
              {view === 'register' && user.role === 'admin' && <RegisterUser user={user} showModal={showModal} />}
              {view === 'track' && <TrackFile user={user} showModal={showModal} />}
              {view === 'search' && <Search user={user} showModal={showModal} />}
              {view === 'audit' && user.role === 'admin' && <Audit user={user} showModal={showModal} />}
            </Paper>
          </Box>
        </Box>
      ) : (
        <Login setUser={setUser} showModal={showModal} />
      )}
    </Container>
    </ThemeProvider>
  );
};

export default App;