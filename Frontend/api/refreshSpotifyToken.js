import axios from "axios";

const BACKEND_URL =
  "https://api.frozenleafstudio.com/api/v1/playlists/refresh-token";

// Scheduled (daily) by Vercel Cron — see vercel.json. Note the backend also
// refreshes the Spotify token lazily on-demand (SpotifyTokenService
// .getCurrentAccessToken), so this is a safety-net top-up, not the only path.
export default async function refreshSpotifyToken(req, res) {
  if (req.headers.authorization !== `Bearer ${process.env.CRON_SECRET}`) {
    return res.status(401).json({ error: "Unauthorized" });
  }

  const username = process.env.BACKEND_ADMIN_USERNAME;
  const password = process.env.BACKEND_ADMIN_PASSWORD;
  if (!username || !password) {
    console.error(
      "refreshSpotifyToken: missing BACKEND_ADMIN_USERNAME/PASSWORD env vars"
    );
    return res.status(500).json({ error: "Server misconfigured" });
  }

  const basicAuth =
    "Basic " + Buffer.from(`${username}:${password}`).toString("base64");

  try {
    const response = await axios.post(BACKEND_URL, null, {
      headers: { Authorization: basicAuth },
    });
    return res.status(200).json(response.data);
  } catch (error) {
    console.error(
      "refreshSpotifyToken: backend refresh failed:",
      error.response?.status,
      error.message
    );
    return res.status(502).json({ error: "Failed to refresh Spotify token" });
  }
}
