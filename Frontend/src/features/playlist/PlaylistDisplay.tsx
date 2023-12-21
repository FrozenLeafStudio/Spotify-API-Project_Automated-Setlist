import React, { useState } from "react";
import { Playlist } from "../../models/Playlist";
import { Setlist } from "../../models/Setlist";
import { PlaylistItems } from "./PlaylistItems"; // Adjust the path as necessary
import { PlaylistDetails } from "./PlaylistDetails"; // Adjust the path as necessary
import { Modal } from "./Modal"; // Adjust the path as necessary
import { PlaylistActions } from "./PlaylistActions"; // Adjust the path as necessary
import "./playlist.css";

type PlaylistResultsProps = {
  spotifyPlaylist: Playlist | null;
  createSpotifyPlaylist: (
    playlistId: string,
    includeCovers: boolean
  ) => Promise<void>;
  setlist: Setlist;
  className?: string;
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
const PlaylistDisplay: React.FC<PlaylistResultsProps> = ({
  spotifyPlaylist,
  createSpotifyPlaylist,
  setlist,
  className,
}) => {
  const [showMissingTracks, setShowMissingTracks] = useState(false);
  const [includeCovers, setIncludeCovers] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);

  if (!spotifyPlaylist) {
    return <div>No Playlist Available</div>;
  }

  const handlePlaylistCreation = (
    event: React.MouseEvent<HTMLButtonElement>
  ) => {
    event.stopPropagation();
    createSpotifyPlaylist(spotifyPlaylist.setlistID, includeCovers);
  };
  const onToggleMissingTracks = () => {
    setShowMissingTracks(!showMissingTracks);
  };

  return (
    <div className={`playlist-container ${className}`}>
      <PlaylistDetails
        name={spotifyPlaylist.name}
        description={spotifyPlaylist.description}
        onOpenModal={() => setIsModalOpen(true)}
        //spotifyURL={spotifyPlaylist.spotifyUrl}
      />
      <PlaylistItems
        tracks={spotifyPlaylist.tracks}
        showMissingTracks={showMissingTracks}
        includeCovers={includeCovers}
      />
      <PlaylistActions
        onPlaylistCreation={handlePlaylistCreation}
        onToggleMissingTracks={onToggleMissingTracks}
        showMissingTracks={showMissingTracks}
        setIncludeCovers={setIncludeCovers}
        includeCovers={includeCovers}
      />
      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)}>
        {setlist && formatSetlistForModal(setlist)}
      </Modal>
    </div>
  );
};

export default PlaylistDisplay;
