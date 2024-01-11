import axios from "axios";

export default async function deleteSetlists(req, res) {
  try {
    const response = await axios.post(
      "https://api.frozenleafstudio.com/api/v1/setlists/db/purge"
    );

    res.status(200).json(response.data);
  } catch (error) {
    console.error("Error triggering token refresh:", error);
    res.status(500).send("Failed to trigger token refresh");
  }
}
