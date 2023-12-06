import React from "react";
import { Artist } from "../../models/Artist";
import { Setlist } from "../../models/Setlist";

type SearchResultsProps = {
  artist: Artist | null;
};

const ArtistSearchResults: React.FC<SearchResultsProps> = ({ artist }) => {
  const capitalizeWords = (str: string) => {
    if (!str) return "";

    return str
      .split(" ")
      .map((word) => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
      .join(" ");
  };
  if (!artist) {
    return <div>No Artist Selected</div>;
  }
  return (
    <div className="search-results">
      <h4>Artist: {<p>{capitalizeWords(artist.name)}</p>}</h4>
      <p>
        <strong>Sort Name:</strong> {artist.sortName}
      </p>
    </div>
  );
};
export default ArtistSearchResults;
