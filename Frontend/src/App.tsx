import { useEffect, useState, useRef } from "react";
import { searchArtists } from "./services/ArtistService";
import { Artist } from "./models/Artist";
import { Setlist } from "./models/Setlist";
import { searchSetlists } from "./services/SetlistService";
import {
  createPlaylists,
  searchPlaylists,
  initiateAuthorization,
} from "./services/PlaylistService";
import { Playlist } from "./models/Playlist";
import { Modal } from "./features/playlist/Modal";
import AdminLogin from "./features/admin/AdminLogin";
import SearchBar from "./features/artist/SearchBar";
import ArtistSearchResults from "./features/artist/ArtistSearchResults";
import SetlistDisplay from "./features/setlist/SetlistDisplay";
import PlaylistDisplay from "./features/playlist/PlaylistDisplay";
import "./App.css";
import { Loading } from "./features/style/Loading";

function App() {
  const [artist, setArtist] = useState<Artist | null>(null);
  const prevArtistRef = useRef<Artist | null>(null);
  const [searchSubmitted, setSearchSubmitted] = useState(false);
  const [setlists, setSetlists] = useState<Setlist[] | null>([]);
  const [selectedSetlist, setSelectedSetlist] = useState<Setlist | null>(null);
  const [setlistsExist, setSetlistsExist] = useState(false);
  const [playlist, setPlaylist] = useState<Playlist | null>(null);
  const [playlistExist, setPlaylistExist] = useState(false);
  const [isAdminModalOpen, setIsAdminModalOpen] = useState<boolean>(false);
  const [authUrl, setAuthUrl] = useState<string>("");
  const [isPlaylistLoading, setIsPlaylistLoading] = useState(false);
  const [currentPage, setCurrentPage] = useState<number>(1);

  useEffect(() => {
    const keySequence: string[] = ["Control", "Alt", "Shift", "A"];
    let keyPressed: string[] = [];

    const keyDownHandler = (event: KeyboardEvent) => {
      keyPressed.push(event.key);
      if (keyPressed.length > keySequence.length) {
        keyPressed.shift();
      }

      if (keySequence.every((key, index) => key === keyPressed[index])) {
        setIsAdminModalOpen(true);
        keyPressed = [];
      }
    };

    window.addEventListener("keydown", keyDownHandler);

    return () => window.removeEventListener("keydown", keyDownHandler);
  }, []);

  const handleSearchSubmit = async (searchTerm: string) => {
    const artistData = await searchArtists(searchTerm);
    const newArtist = new Artist(artistData);
    setSearchSubmitted(true);

    if (newArtist.name !== prevArtistRef.current?.name) {
      // Reset states if the artist has changed
      setSetlists([]);
      setSetlistsExist(false);
      setPlaylist(null);
      setPlaylistExist(false);
      setSelectedSetlist(null);
      setCurrentPage(1);
      setIsPlaylistLoading(false);
    }
    setArtist(newArtist);
    prevArtistRef.current = newArtist;
    const setlistData = await searchSetlists(newArtist.mbid, 1);
    setSetlists(setlistData);
    setSetlistsExist(true);
  };
  const fetchMoreSetlists = async () => {
    if (artist && artist.mbid && setlists) {
      try {
        const newPage = currentPage + 1;
        const additionalSetlists = await searchSetlists(artist.mbid, newPage);
        if (additionalSetlists.length > 0) {
          setSetlists([...setlists, ...additionalSetlists]);
          setCurrentPage(newPage);
        }
      } catch (error) {
        console.error("Error fetching more setlists: ", error);
      }
    }
  };
  const handlePlaylistSearch = async (returnedSetlist: Setlist) => {
    if (playlistExist) {
      setPlaylist(null);
      setPlaylistExist(false);
    }
    setIsPlaylistLoading(true);

    try {
      if (!artist?.name) {
        return null;
      }
      setSelectedSetlist(returnedSetlist);
      const playlistData = await searchPlaylists(
        returnedSetlist.setlistID,
        artist.name
      );
      const newPlaylist = new Playlist(playlistData);

      setPlaylist(newPlaylist);
      setPlaylistExist(true);
      setIsPlaylistLoading(false);
    } catch (error) {
      console.error("Unable to search for playlists: ", error);
      setIsPlaylistLoading(false);
    }
  };
  const PlayistCreation = async (
    playlistId: string,
    includeCovers: boolean
  ) => {
    try {
      const playlistData = await createPlaylists(playlistId, includeCovers);
      const newPlaylist = new Playlist(playlistData);
      setPlaylist(newPlaylist);
      console.log(playlist);
    } catch (error) {
      console.error("Unable to search for Artist: ", error);
    }
  };
  const handleAdminSubmit = async (username: string, password: string) => {
    try {
      const authData = await initiateAuthorization(username, password);
      setAuthUrl(authData.url);
    } catch (error) {
      console.error("Error during Spotify authorization: ", error);
    }
  };

  const closeModal = () => {
    setIsAdminModalOpen(false);
    setAuthUrl("");
  };
  return (
    <>
      <div className="App">
        <div
          className={`main-container ${
            searchSubmitted ? "search-submitted" : ""
          }`}
        >
          <SearchBar onSearchSubmit={handleSearchSubmit} />
          {setlistsExist && (
            <div className="main-content">
              <ArtistSearchResults artistSearch={artist} />
              <div
                className={`setlist-playlist-container ${
                  playlistExist || isPlaylistLoading ? "active" : ""
                }`}
              >
                <SetlistDisplay
                  setlists={setlists}
                  handleClick={handlePlaylistSearch}
                  fetchMoreSetlists={fetchMoreSetlists} // Pass fetchMoreSetlists function
                  className={playlistExist || isPlaylistLoading ? "active" : ""}
                />
                {isPlaylistLoading ? (
                  <Loading />
                ) : (
                  selectedSetlist && (
                    <PlaylistDisplay
                      spotifyPlaylist={playlist}
                      setlist={selectedSetlist}
                      createSpotifyPlaylist={PlayistCreation}
                      className={playlistExist ? "active" : ""}
                    />
                  )
                )}
              </div>
            </div>
          )}
        </div>
      </div>
      <Modal isOpen={isAdminModalOpen} onClose={closeModal}>
        <AdminLogin onAdminSubmit={handleAdminSubmit} />
        {authUrl && (
          <div>
            <p>
              Please go to this URL to authorize:{" "}
              <a href={authUrl} target="_blank" rel="noopener noreferrer">
                {authUrl}
              </a>
            </p>
            <button onClick={closeModal}>Close</button>
          </div>
        )}
      </Modal>
    </>
  );
}

export default App;
