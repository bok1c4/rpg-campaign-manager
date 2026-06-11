import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// U dev modu Vite radi na 5173 i proksira /api na Spring Boot (8080).
// U produkciji se build (dist/) servira direktno iz Spring Boot-a.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': 'http://localhost:8080'
    }
  },
  build: {
    outDir: 'dist'
  }
})
