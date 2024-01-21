import React from "react";
import { Artist } from "../../models/Artist";
import "./Results.css";
type SearchResultsProps = {
  artistSearch: Artist | null;
};

const ArtistSearchResults: React.FC<SearchResultsProps> = ({
  artistSearch,
}) => {
  const capitalizeWords = (str: string) => {
    if (!str) return "";

    return str
      .split(" ")
      .map((word) => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
      .join(" ");
  };
  if (!artistSearch) {
    return <div>No Artist found in setlist.fm</div>;
  }
  return (
    <div className="artist-name-container">
      <h3>{capitalizeWords(artistSearch.name)}</h3>
    </div>
  );
};
export default ArtistSearchResults;
