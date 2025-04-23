import {
    Drawer,
    Box,
    List,
    ListItem,
    ListItemButton,
    ListItemIcon,
    ListItemText,
    Typography,
    Divider,
    Avatar
  } from '@mui/material';
  import {
    Description as FileIcon,
    Edit as EditIcon,
    PersonAdd as PersonAddIcon,
    Search as SearchIcon,
    Assessment as AuditIcon,
    Dashboard as DashboardIcon
  } from '@mui/icons-material';
  
  const Sidebar = ({ user, view, setView }) => {
    const menuItems = [
      ...(user.role === 'admin' ? [
        { id: 'create', label: 'Create File', icon: <FileIcon /> },
        { id: 'update', label: 'Update File', icon: <EditIcon /> },
        { id: 'register', label: 'Register User', icon: <PersonAddIcon /> },
      ] : []),
      { id: 'track', label: 'Track File', icon: <SearchIcon /> },
      { id: 'search', label: 'Search', icon: <SearchIcon /> },
      ...(user.role === 'admin' ? [
        { id: 'audit', label: 'Audit', icon: <AuditIcon /> },
      ] : []),
    ];
  
    return (
      <Drawer
        variant="permanent"
        sx={{
          width: 260,
          flexShrink: 0,
          '& .MuiDrawer-paper': {
            width: 260,
            boxSizing: 'border-box',
            borderRight: 'none',
            bgcolor: 'background.paper',
            boxShadow: '2px 0 16px rgba(0,0,0,0.05)'
          },
        }}
      >
        <Box sx={{ p: 3, display: 'flex', alignItems: 'center', gap: 2 }}>
          <Avatar sx={{ bgcolor: 'primary.main' }}>
            <DashboardIcon />
          </Avatar>
          <Typography variant="h6" fontWeight="bold">
            FTMS
          </Typography>
        </Box>
        <Divider />
        <List sx={{ px: 2 }}>
          {menuItems.map((item) => (
            <ListItem key={item.id} disablePadding>
              <ListItemButton
                selected={view === item.id}
                onClick={() => setView(item.id)}
                sx={{
                  borderRadius: 2,
                  mb: 0.5,
                  '&.Mui-selected': {
                    bgcolor: 'primary.light',
                    color: 'primary.main',
                    '& .MuiListItemIcon-root': {
                      color: 'primary.main'
                    }
                  }
                }}
              >
                <ListItemIcon sx={{ minWidth: 40 }}>
                  {item.icon}
                </ListItemIcon>
                <ListItemText 
                  primary={item.label} 
                  primaryTypographyProps={{ fontWeight: 500 }}
                />
              </ListItemButton>
            </ListItem>
          ))}
        </List>
        <Divider sx={{ mt: 'auto' }} />
        <Box sx={{ p: 2, textAlign: 'center' }}>
          <Typography variant="caption" color="text.secondary">
            v1.0.0
          </Typography>
        </Box>
      </Drawer>
    );
  };


    export default Sidebar;
    