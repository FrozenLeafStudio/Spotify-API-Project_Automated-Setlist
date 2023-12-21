import React from "react";
import { AppTrack } from "../../models/AppTrack"; // Make sure this path is correct
import "./PlaylistItems.css"; // Make sure to create and style this CSS file

type PlaylistItemsProps = {
  tracks: AppTrack[];
  showMissingTracks: boolean;
};

const PlaylistItems: React.FC<PlaylistItemsProps> = ({
  tracks,
  showMissingTracks,
}) => {
  return (
    <ul className="track-list">
      {tracks.map(
        (track, index) =>
          (track.trackFound || showMissingTracks) && (
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
          )
      )}
    </ul>
  );
};

export default PlaylistItems;
