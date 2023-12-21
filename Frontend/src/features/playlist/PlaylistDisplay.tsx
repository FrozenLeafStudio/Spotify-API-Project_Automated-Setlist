import React, { useState } from "react";
import "./playlist.css";
import { Playlist } from "../../models/Playlist";
import { Setlist } from "../../models/Setlist";
import { AppTrack } from "../../models/AppTrack";

type PlaylistItemsProps = {
  tracks: AppTrack[];
  showMissingTracks: boolean;
};

const PlaylistItems: React.FC<PlaylistItemsProps> = ({
  tracks,
  showMissingTracks,
}) => {
  return (
    <ul>
      {tracks.map((track, index) =>
        track.trackFound || showMissingTracks ? (
          <li
            key={index}
            className={`track ${
              track.trackFound ? "found-track" : "missing-track"
            }`}
          >
            {track.trackFound && (
              <img
                src={track.albumImageUrl}
                alt={track.albumName}
                className="album-image"
              />
            )}
            <div className="track-details">
              <div className="track-name">{track.songName}</div>
              <div className="track-info">
                <em>{track.details}</em>
              </div>
            </div>
          </li>
        ) : null
      )}
    </ul>
  );
};

type PlaylistDetailsProps = {
  name: string;
  description: string;
  onOpenModal: () => void;
};

const PlaylistDetails: React.FC<PlaylistDetailsProps> = ({
  name,
  description,
  onOpenModal,
}) => {
  return (
    <div className="playlist-info">
      <h3>{name}</h3>
      <p>{description}</p>
      <button onClick={onOpenModal}>View Setlist Structure</button>
    </div>
  );
};
const formatSetlistForModal = (setlist: Setlist) => {
  return setlist.sets.set.map((namedSet, index) => (
    <div key={index} className="named-set">
      <h4>{namedSet.name || "Unnamed Set"}</h4>
      <ul>
        {namedSet.song.map((song, songIndex) => (
          <li key={songIndex}>
            {song.name} {song.info && <em>({song.info})</em>}
          </li>
        ))}
      </ul>
    </div>
  ));
};
type ModalProps = {
  isOpen: boolean;
  onClose: () => void;
  children: React.ReactNode;
};

const Modal: React.FC<ModalProps> = ({ isOpen, onClose, children }) => {
  if (!isOpen) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="modal-body">{children}</div>
        <div className="modal-footer">
          <button onClick={onClose}>Close</button>
        </div>
      </div>
    </div>
  );
};

type PlaylistActionsProps = {
  onPlaylistCreation: (event: React.MouseEvent<HTMLButtonElement>) => void;
  onToggleMissingTracks: () => void;
  includeCovers: boolean;
  showMissingTracks: boolean;
};

const PlaylistActions: React.FC<PlaylistActionsProps> = ({
  onPlaylistCreation,
  onToggleMissingTracks,
  includeCovers,
  showMissingTracks,
}) => {
  return (
    <div className="playlist-actions">
      <label>
        Include Cover Songs
        <input
          type="checkbox"
          checked={includeCovers}
          onChange={onToggleMissingTracks}
        />
      </label>
      <button className="toggle-not-found" onClick={onToggleMissingTracks}>
        {showMissingTracks ? "Hide Missing Tracks" : "Show Missing Tracks"}
      </button>
      <button onClick={onPlaylistCreation}>Create Playlist</button>
    </div>
  );
};

type PlaylistResultsProps = {
  spotifyPlaylist: Playlist | null;
  createSpotifyPlaylist: (
    playlistId: string,
    includeCovers: boolean
  ) => Promise<void>;
  setlist: Setlist;
  className?: string;
};

const PlaylistDisplay: React.FC<PlaylistResultsProps> = ({
  spotifyPlaylist,
  createSpotifyPlaylist,
  setlist,
  className,
}) => {
  const [showMissingTracks, setShowMissingTracks] = useState(false);
  const [includeCovers, setIncludeCovers] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);

  if (!spotifyPlaylist) {
    return <div>No Playlist Available</div>;
  }

  const handlePlaylistCreation = (
    event: React.MouseEvent<HTMLButtonElement>
  ) => {
    event.stopPropagation();
    if (spotifyPlaylist && spotifyPlaylist.setlistID) {
      createSpotifyPlaylist(spotifyPlaylist.setlistID, includeCovers);
    }
  };

  return (
    <div className={`playlist-container ${className}`}>
      <PlaylistDetails
        name={spotifyPlaylist.name}
        description={spotifyPlaylist.description}
        onOpenModal={() => setIsModalOpen(true)}
      />
      <PlaylistActions
        onPlaylistCreation={handlePlaylistCreation}
        onToggleMissingTracks={() => setShowMissingTracks(!showMissingTracks)}
        includeCovers={includeCovers}
        showMissingTracks={showMissingTracks}
      />
      <PlaylistItems
        tracks={spotifyPlaylist.tracks}
        showMissingTracks={showMissingTracks}
      />
      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)}>
        {setlist && formatSetlistForModal(setlist)}
      </Modal>
    </div>
  );
};

export default PlaylistDisplay;
