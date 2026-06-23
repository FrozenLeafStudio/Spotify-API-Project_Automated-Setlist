import React from "react";
import "./PlaylistDetails.css";

type PlaylistDetailsProps = {
  name: string;
  description: string;
  albumImages: string[];
};

export const PlaylistDetails: React.FC<PlaylistDetailsProps> = ({
  name,
  description,
  albumImages,
}) => {
  return (
    <div className="playlist-details">
      <div className="album-mosaic">
        {albumImages.map((imageUrl, index) => (
          <img
            key={index}
            src={imageUrl}
            alt={`Album Art ${index + 1}`}
            className="album-image"
          />
        ))}
      </div>
      <h3 className="playlist-title">{name}</h3>
      <p className="playlist-description">{description}</p>
    </div>
  );
};
