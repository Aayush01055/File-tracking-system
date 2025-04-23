import React from 'react';
import { AppBar, Toolbar, Typography, Button } from '@mui/material';
import { Link } from 'react-router-dom';

function Navbar() {
  return (
    <AppBar position="static">
      <Toolbar>
        <Typography variant="h6" style={{ flexGrow: 1 }}>FTMS</Typography>
        <Button color="inherit" component={Link} to="/track">Track Files</Button>
        <Button color="inherit" component={Link} to="/search">Search</Button>
        <Button color="inherit" component={Link} to="/create">Create File</Button>
        <Button color="inherit" component={Link} to="/audit">Audit</Button>
        <Button color="inherit" component={Link} to="/login">Login</Button>
      </Toolbar>
    </AppBar>
  );
}

export default Navbar;
