// Root URL for all backend API calls.
// Override locally via VITE_API_BASE_URL (see .env.development); falls back to
// production so `npm run build` needs no configuration.
export const API_ROOT =
  import.meta.env.VITE_API_BASE_URL ?? "https://api.frozenleafstudio.com/api/v1";
