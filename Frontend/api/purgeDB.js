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
    console.error(
      "purgeDB: backend purge failed:",
      error.response?.status,
      error.message
    );
    return res.status(502).json({ error: "Failed to purge setlists DB" });
  }
}
