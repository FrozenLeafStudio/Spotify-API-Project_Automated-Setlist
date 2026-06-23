import React, { useState } from "react";
import { Playlist } from "../../models/Playlist";
import { Setlist } from "../../models/Setlist";
import { PlaylistItems } from "./PlaylistItems";
import { PlaylistDetails } from "./PlaylistDetails";
import { PlaylistSuccess } from "./PlaylistSuccess";
import { Modal } from "./Modal";
import { PlaylistActions } from "./PlaylistActions";
import "./PlaylistDisplay.css";

type PlaylistResultsProps = {
  spotifyPlaylist: Playlist | null;
  createSpotifyPlaylist: (
    playlistId: string,
    includeCovers: boolean
  ) => Promise<void>;
  setlist: Setlist | null;
  onClose?: () => void;
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
  onClose,
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
  const foundCount = spotifyPlaylist.tracks.filter((t) => t.trackFound).length;

  let body;
  if (foundCount === 0) {
    body = (
      <div className="playlist-empty">
        <p>No tracks from this setlist could be matched on Spotify.</p>
        {setlist && (
          <a href={setlist.url} target="_blank" rel="noopener noreferrer">
            View this setlist on setlist.fm
          </a>
        )}
      </div>
    );
  } else if (spotifyPlaylist.spotifyUrl) {
    body = (
      <PlaylistSuccess
        name={spotifyPlaylist.name}
        spotifyUrl={spotifyPlaylist.spotifyUrl}
        trackCount={foundCount}
        coverImages={albumImages}
        onBuildAnother={onClose}
      />
    );
  } else {
    body = (
      <>
        <div className="playlist-info-card">
          <PlaylistDetails
            name={spotifyPlaylist.name}
            description={spotifyPlaylist.description}
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
      </>
    );
  }

  return (
    <div className={`playlist-display ${className}`}>
      {onClose && <div className="sheet-handle" onClick={onClose} />}
      {body}
      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)}>
        {setlist && formatSetlistForModal(setlist)}
      </Modal>
    </div>
  );
};

export default PlaylistDisplay;
