import axios from "axios";

const BASE_URL = "https://api.frozenleafstudio.com/api/v1/setlists";
//const BASE_URL = "http://localhost:8080/api/v1/setlists";

export const searchSetlists = async (mbid: string, pageNum: number) => {
  try {
    const response = await axios.get(`${BASE_URL}/search`, {
      params: { artistMbid: mbid, pageNumber: pageNum },
    });
    return response.data;
  } catch (error) {
    console.error("Error searching artists: ", error);
    throw error;
  }
};
