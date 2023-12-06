import axios from "axios";

const BASE_URL = "https://api.frozenleafstudio.com/api/v1/playlists";

export const searchPlaylists = async (searchTerm: string) => {
  try {
    const response = await axios.get(`${BASE_URL}/search`, {
      params: { artistName: searchTerm },
    });
    return response.data;
  } catch (error) {
    console.error("Error searching artists: ", error);
    throw error;
  }
};
