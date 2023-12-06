import axios from "axios";

const BASE_URL = "https://api.frozenleafstudio.com/api/v1/setlists";

export const searchSetlists = async (mbid: string) => {
  try {
    const response = await axios.get(`${BASE_URL}/search`, {
      params: { artistMbid: mbid, startDate: "2023-01-01" },
    });
    return response.data;
  } catch (error) {
    console.error("Error searching artists: ", error);
    throw error;
  }
};
