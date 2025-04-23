import('tailwindcss').Config
module.exports = {
  content: ["./src/**/*.{js,jsx,ts,tsx}"],
  theme: {
    extend: {
      colors: {
        primary: '#3498db',
        secondary: '#2c3e50',
        accent: '#e74c3c',
      },
    },
  },
  plugins: [],
};