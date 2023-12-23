import React, { useState } from "react";
import { AppTrack } from "../../models/AppTrack";
import "./PlaylistItems.css";

type PlaylistItemsProps = {
  tracks: AppTrack[];
  includeCovers: boolean;
  onOpenModal: () => void;
};

export const PlaylistItems: React.FC<PlaylistItemsProps> = ({
  tracks,
  includeCovers,
  onOpenModal,
}) => {
  const [showMissingTracks, setShowMissingTracks] = useState(false);

  const toggleMissingTracks = () => {
    setShowMissingTracks(!showMissingTracks);
  };

  return (
    <div className="playlist-items-container">
      <div className="buttons-container">
        <button className="button" onClick={onOpenModal}>
          View Setlist Structure
        </button>
        <button className="button" onClick={toggleMissingTracks}>
          {showMissingTracks ? "Hide" : "Show"} Missing Tracks
        </button>
      </div>
      <ul className="playlist-songs">
        {tracks.map((track, index) => {
          const shouldDisplay =
            (track.trackFound || showMissingTracks) &&
            (includeCovers || !track.cover);
          return shouldDisplay ? (
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
                {track.details && (
                  <div className="track-info">
                    <em>{track.details}</em>
                  </div>
                )}
              </div>
            </li>
          ) : null;
        })}
      </ul>
    </div>
  );
};
