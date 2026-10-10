# PathPilot AI frontend

Responsive React 18 + Vite application for the PathPilot career planning platform. The frontend uses the Spring Boot API at `http://localhost:8081` by default and does not modify the backend or contain credentials.

## Run locally

1. Start the Spring Boot backend on port `8081`.
2. In this folder, copy `.env.example` to `.env` if you need to change the API target.
3. Install dependencies and start Vite:

   ```sh
   npm install
   npm run dev
   ```

4. Open the URL printed by Vite (normally `http://localhost:5173`).

Vite proxies `/api` requests to `http://localhost:8081` by default, avoiding browser CORS restrictions during local development even if Vite selects a port other than `5173`. Change `VITE_API_PROXY_TARGET` to point the dev proxy at another backend. For a deployed frontend, set `VITE_API_BASE_URL` to the backend URL and configure that backend to allow the deployed origin.

Production build: `npm run build`. Local preview: `npm run preview`.

## Connected backend APIs

In development, API paths are sent through the Vite proxy; in production, set `VITE_API_BASE_URL` to the backend URL. Authentication uses the backend-issued `token` and `user`; requests include an `Authorization` bearer token. The token and summary user are stored in browser local storage.

| Feature | API |
|---|---|
| Registration / sign in / current identity | `POST /api/auth/register`, `POST /api/auth/login`, `GET /api/auth/me` |
| Career catalog and detail | `GET /api/careers`, `GET /api/careers/{id}`, `GET /api/careers/{id}/skills` |
| Skill match | `GET /api/careers/{id}/skill-gap` |
| Learning library | `GET /api/resources`, `GET /api/careers/{id}/resources` |
| Student profile and skills | `GET/PUT /api/students/me/profile`, `GET/POST /api/students/me/skills`, `DELETE /api/students/me/skills/{skillId}` |
| Career recommendations | `GET /api/recommendations` |
| Roadmaps | `GET /api/roadmaps/me`, `POST /api/roadmaps/generate?careerId={id}`, `PATCH /api/roadmaps/me/{roadmapId}/steps/{stepId}/complete?completed={boolean}` |
| Progress | `GET /api/progress/me` |
| Admin catalog management | `POST/PUT/DELETE /api/admin/careers[/{id}]`, `POST/PUT/DELETE /api/admin/resources[/{id}]` |

The backend enforces authorization for protected APIs; route guards in this frontend improve navigation UX and do not replace server authorization. Admin catalog controls are available only to users whose backend-provided roles include `ROLE_ADMIN`. The UI currently provides create/delete controls; backend update routes exist but are not exposed in this version.

## Demo-only areas

Interview prompts are static practice content. Resume selection stays local in the browser and is not reviewed or uploaded. The internship tracker is stored in browser local storage and does not sync to the server. Career assistant is a private reflection/notes screen without a chatbot or generated responses. These screens are explicitly labeled in the UI.

## Backend APIs needed for productized demo-only features

Add authenticated APIs for internship application CRUD; resume upload/storage/review (with an explicit privacy and retention policy); interview practice sessions and any scoring/feedback; and an AI assistant conversation endpoint. None of those features is presented as connected.
