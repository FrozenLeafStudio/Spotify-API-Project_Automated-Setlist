import axios from "axios";

const BACKEND_URL = "https://api.frozenleafstudio.com/api/v1/setlists/db/purge";

// Scheduled (daily) by Vercel Cron — see vercel.json. Vercel signs cron
// requests with `Authorization: Bearer ${CRON_SECRET}`, so we reject anything
// that isn't the scheduler. The backend itself also requires admin auth, which
// we supply via Basic auth from server-side env vars.
export default async function purgeDB(req, res) {
  if (req.headers.authorization !== `Bearer ${process.env.CRON_SECRET}`) {
    return res.status(401).json({ error: "Unauthorized" });
  }

  const username = process.env.BACKEND_ADMIN_USERNAME;
  const password = process.env.BACKEND_ADMIN_PASSWORD;
  if (!username || !password) {
    console.error("purgeDB: missing BACKEND_ADMIN_USERNAME/PASSWORD env vars");
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
    const backendStatus = error.response?.status;
    console.error(
      "purgeDB: backend purge failed:",
      backendStatus,
      error.message
    );
    // Surface the backend's real status (e.g. 401 admin-auth failure) so it's
    // visible directly in the cron log instead of being masked as a blanket 502.
    if (backendStatus) {
      return res
        .status(backendStatus)
        .json({ error: "Backend rejected purge", backendStatus });
    }
    return res.status(502).json({ error: "Could not reach backend" });
  }
}
