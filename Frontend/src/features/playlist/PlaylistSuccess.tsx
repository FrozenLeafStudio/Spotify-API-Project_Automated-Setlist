import React, { useState } from "react";
import { FaSpotify } from "react-icons/fa";
import {
  MdCheckCircle,
  MdLink,
  MdOutlineShare,
  MdFavoriteBorder,
  MdArrowBack,
} from "react-icons/md";
import { QRCodeSVG } from "qrcode.react";
import "./PlaylistSuccess.css";

type PlaylistSuccessProps = {
  name: string;
  spotifyUrl: string;
  trackCount: number;
  coverImages: string[];
  onBuildAnother?: () => void;
};

export const PlaylistSuccess: React.FC<PlaylistSuccessProps> = ({
  name,
  spotifyUrl,
  trackCount,
  coverImages,
  onBuildAnother,
}) => {
  const [copied, setCopied] = useState(false);

  const copyLink = async () => {
    try {
      await navigator.clipboard.writeText(spotifyUrl);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    } catch {
      /* clipboard unavailable */
    }
  };

  const share = async () => {
    if (navigator.share) {
      try {
        await navigator.share({ title: name, url: spotifyUrl });
      } catch {
        /* dismissed */
      }
    } else {
      copyLink();
    }
  };

  return (
    <div className="playlist-success">
      <div className="success-badge">
        <MdCheckCircle />
      </div>
      <p className="success-subtitle">Your playlist is live on Spotify</p>

      {coverImages.length > 0 && (
        <div className="success-cover">
          {coverImages.map((url, i) => (
            <img key={i} src={url} alt="" />
          ))}
        </div>
      )}

      <h3 className="success-name">{name}</h3>
      <p className="success-count">{trackCount} tracks</p>

      <a
        className="open-spotify"
        href={spotifyUrl}
        target="_blank"
        rel="noopener noreferrer"
      >
        <FaSpotify /> Open in Spotify
      </a>

      <div className="success-secondary">
        <button onClick={copyLink}>
          <MdLink /> {copied ? "Copied" : "Copy link"}
        </button>
        <button onClick={share}>
          <MdOutlineShare /> Share
        </button>
      </div>

      <div className="success-qr">
        <QRCodeSVG value={spotifyUrl} size={128} bgColor="#ffffff" />
        <span>
          Scan to open it on your phone, then tap <MdFavoriteBorder /> Save.
        </span>
      </div>

      {onBuildAnother && (
        <button className="build-another" onClick={onBuildAnother}>
          <MdArrowBack /> Build another playlist
        </button>
      )}
    </div>
  );
};
