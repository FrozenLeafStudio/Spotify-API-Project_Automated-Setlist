import axios from "axios";
import { API_ROOT } from "./apiConfig";

const BASE_URL = `${API_ROOT}/setlists`;

export const searchSetlists = async (mbid: string, pageNum: number) => {
  try {
    const response = await axios.get(`${BASE_URL}/search`, {
      params: { artistMbid: mbid, pageNumber: pageNum },
    });
    return response.data;
  } catch (error) {
    console.error("Error searching setlists: ", error);
    return null;
  }
};
