import React, { useState } from "react";
import "./PlaylistInfoCard.css"; // Make sure to create and style this CSS file
import { Playlist } from "../../models/Playlist";
import { Setlist } from "../../models/Setlist";
import { AppTrack } from "../../models/AppTrack";

type PlaylistInfoCardProps = {
  spotifyPlaylist: Playlist;
  setlist: Setlist;
  includeCovers: boolean;
  onIncludeCoversChange: (includeCovers: boolean) => void;
};

const PlaylistInfoCard: React.FC<PlaylistInfoCardProps> = ({
  spotifyPlaylist,
  setlist,
  includeCovers,
  onIncludeCoversChange,
}) => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [showMissingTracks, setShowMissingTracks] = useState(false);

  const createSpotifyPlaylist = async (
    playlistId: string,
    includeCovers: boolean
  ) => {
    // Implement the logic to create a Spotify playlist
    // This function can be passed down from a parent component or implemented here
  };

  const handlePlaylistCreation = (
    event: React.MouseEvent<HTMLButtonElement>
  ) => {
    event.stopPropagation();
    if (spotifyPlaylist && spotifyPlaylist.setlistID) {
      createSpotifyPlaylist(spotifyPlaylist.setlistID, includeCovers);
    }
  };

  const toggleMissingTracks = () => setShowMissingTracks(!showMissingTracks);

  return (
    <div className="playlist-info-card">
      <div className="playlist-info">
        <h3>{spotifyPlaylist.name}</h3>
        <p>{spotifyPlaylist.description}</p>
        <button onClick={() => setIsModalOpen(true)}>
          View Setlist Structure
        </button>
      </div>

      <div className="playlist-actions">
        <label>
          Include Cover Songs
          <input
            type="checkbox"
            checked={includeCovers}
            onChange={(e) => onIncludeCoversChange(e.target.checked)}
          />
        </label>
        <button onClick={toggleMissingTracks}>
          {showMissingTracks ? "Hide Missing Tracks" : "Show Missing Tracks"}
        </button>
        <button onClick={handlePlaylistCreation}>
          Create Spotify Playlist
        </button>
      </div>

      {isModalOpen && (
        <div className="modal-overlay" onClick={() => setIsModalOpen(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-body">
              {/* Here you would render the setlist details */}
              {setlist &&
                setlist.sets.set.map((set, index) => (
                  <div key={index} className="named-set">
                    <h4>{set.name || "Unnamed Set"}</h4>
                    <ul>
                      {set.song.map((song, songIndex) => (
                        <li key={songIndex}>
                          {song.name} {song.info && <em>({song.info})</em>}
                        </li>
                      ))}
                    </ul>
                  </div>
                ))}
            </div>
            <div className="modal-footer">
              <button onClick={() => setIsModalOpen(false)}>Close</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default PlaylistInfoCard;
