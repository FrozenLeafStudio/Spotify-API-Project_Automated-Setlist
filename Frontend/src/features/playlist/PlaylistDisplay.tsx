import React, { useState } from "react";
import { Playlist } from "../../models/Playlist";
import { Setlist } from "../../models/Setlist";
import { PlaylistItems } from "./PlaylistItems"; // Adjust the path as necessary
import { PlaylistDetails } from "./PlaylistDetails"; // Adjust the path as necessary
import { Modal } from "./Modal"; // Adjust the path as necessary
import { PlaylistActions } from "./PlaylistActions"; // Adjust the path as necessary
import "./PlaylistDisplay.css";

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

  const getAlbumImagesForMosaic = () => {
    return spotifyPlaylist.tracks
      .filter((track) => track.trackFound && !track.tape && !track.cover)
      .slice(0, 4)
      .map((track) => track.albumImageUrl);
  };

  const albumImages = getAlbumImagesForMosaic();

  return (
    <div className={`playlist-display ${className}`}>
      <div className="playlist-info-card">
        <PlaylistDetails
          name={spotifyPlaylist.name}
          description={spotifyPlaylist.description}
          spotifyURL={spotifyPlaylist ? spotifyPlaylist.spotifyUrl : ""}
          albumImages={albumImages}
        />

        <PlaylistActions
          onPlaylistCreation={handlePlaylistCreation}
          setIncludeCovers={setIncludeCovers}
          includeCovers={includeCovers}
        />
      </div>
      <div className="playlist-items">
        <PlaylistItems
          tracks={spotifyPlaylist.tracks}
          includeCovers={includeCovers}
          onOpenModal={() => setIsModalOpen(true)}
        />
      </div>
      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)}>
        {setlist && formatSetlistForModal(setlist)}
      </Modal>
    </div>
  );
};

export default PlaylistDisplay;
