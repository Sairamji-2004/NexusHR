# NexusHR Frontend

React + Vite. Talks to Auth/Employee (port 8081) and Payroll (port 8084).

## Setup
```
npm install
npm run dev
```
Opens on http://localhost:5173

## Before it works with your real backend
- `src/context/AuthContext.jsx` — adjust the shape of the login response to match your actual Auth service's JSON (currently expects `{ token }` or `{ data: { token } }`).
- `src/api/api.js` — update `AUTH_API` / `EMPLOYEE_API` if your Auth+Employee endpoints differ from `/api/auth` and `/api/employees`.
- CORS: make sure the Auth (8081) and Payroll (8084) services both allow origin `http://localhost:5173`.

## Pages
- `/login` — sign in, stores JWT in localStorage
- `/dashboard` — landing page after login
- `/employees` — employee list from Employee service
- `/payroll` — generate payroll + view all payslips
