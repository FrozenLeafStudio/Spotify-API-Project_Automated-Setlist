import React from "react";
import { Artist } from "../../models/Artist";
import { Setlist } from "../../models/Setlist";

type SearchResultsProps = {
  artist: Artist | null;
};

const ArtistSearchResults: React.FC<SearchResultsProps> = ({ artist }) => {
  if (!artist) {
    return <div>No Artist Selected</div>;
  }
  return (
    <div className="search-results">
      <h2>Artist Details</h2>
      <p>
        <strong>Name:</strong> {artist.name}
      </p>
      <p>
        <strong>Sort Name:</strong> {artist.sortName}
      </p>
      <p>
        <strong>MBID:</strong> {artist.mbid}
      </p>
      <p>
        <strong>TMID:</strong> {artist.tmid || "N/A"}
      </p>
      <p>
        <strong>Disambiguation:</strong> {artist.disambiguation || "N/A"}
      </p>
      <p>
        <strong>URL:</strong>{" "}
        <a href={artist.url} target="_blank" rel="noopener noreferrer">
          {artist.url}
        </a>
      </p>

      <h3>Setlists</h3>
      {artist.setlists && artist.setlists.length > 0 ? (
        <ul>
          {artist.setlists.map((setlist, index) => (
            <li key={index}>
              {setlist.songs /* Adjust as per Setlist class structure */}
            </li>
          ))}
        </ul>
      ) : (
        <p>No setlists available.</p>
      )}
    </div>
  );
};
export default ArtistSearchResults;
