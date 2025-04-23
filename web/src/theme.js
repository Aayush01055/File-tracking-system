import { createTheme } from '@mui/material/styles';

const theme = createTheme({
  palette: {
    primary: {
      main: '#6C63FF', // Modern purple
    },
    secondary: {
      main: '#FF6584', // Modern pink
    },
    background: {
      default: '#F9FAFB',
      paper: '#FFFFFF'
    }
  },
  shape: {
    borderRadius: 12, // Rounded corners
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          textTransform: 'none',
          fontWeight: 500,
          letterSpacing: 0.5,
          padding: '8px 20px'
        }
      }
    }
  }
});

export default theme;