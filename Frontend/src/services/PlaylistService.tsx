import axios from "axios";

const BASE_URL = "https://api.frozenleafstudio.com/api/v1/playlists";
//const BASE_URL = "http://localhost:8080/api/v1/playlists";

const searchPlaylists = async (show: string, artist: string) => {
  try {
    const response = await axios.get(`${BASE_URL}/search`, {
      params: { setlistId: show, artistName: artist },
    });
    return response.data;
  } catch (error) {
    console.error("Error searching playlists: ", error);
    throw error;
  }
};

const createPlaylists = async (playlist: string, covers: boolean) => {
  try {
    const response = await axios.get(`${BASE_URL}/create`, {
      params: { setlistId: playlist, includeCovers: covers },
    });
    return response.data;
  } catch (error) {
    console.error("Error creating playlists: ", error);
    throw error;
  }
};

const initiateAuthorization = async (username: string, password: string) => {
  const authHeader = "Basic " + btoa(username + ":" + password);

  try {
    const response = await axios.get(`${BASE_URL}/auth`, {
      headers: {
        Authorization: authHeader,
      },
    });
    return response.data; // The response should contain the URL for Spotify authorization
  } catch (error) {
    console.error("Error initiating Spotify authorization: ", error);
    throw error;
  }
};

export { createPlaylists, searchPlaylists, initiateAuthorization };
