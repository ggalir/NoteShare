import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api/auth': 'http://localhost:8081',
      '/api/notes': 'http://localhost:8082',
      '/api/share': 'http://localhost:8083',
    },
  },
})
