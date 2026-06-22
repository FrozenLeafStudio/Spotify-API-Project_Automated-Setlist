import axios from "axios";
import { Artist } from "../models/Artist";

const BASE_URL = "https://api.frozenleafstudio.com/api/v1/artists";
//const BASE_URL = "http://localhost:8080/api/v1/artists";

export const searchArtists = async (searchTerm: string) => {
  try {
    const response = await axios.get(`${BASE_URL}/search`, {
      params: { artistName: searchTerm },
    });
    return response.data;
  } catch (error) {
    console.error("Error searching artists: ", error);
    return null;
  }
};

// Typeahead suggestions. Pass an AbortSignal so stale in-flight requests can be
// cancelled when the user keeps typing. Throws on real errors (incl. cancellation,
// which the caller is expected to ignore).
export const suggestArtists = async (
  artistName: string,
  signal?: AbortSignal
): Promise<Artist[]> => {
  const response = await axios.get(`${BASE_URL}/suggest`, {
    params: { artistName },
    signal,
  });
  return response.data ?? [];
};
