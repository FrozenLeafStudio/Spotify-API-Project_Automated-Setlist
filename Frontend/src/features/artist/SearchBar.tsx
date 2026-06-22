import React, { useState, useEffect, useRef } from "react";
import { MdSearch, MdClose } from "react-icons/md";
import { Artist } from "../../models/Artist";
import { suggestArtists } from "../../services/ArtistService";
import "./SearchBar.css";

type SearchBarProps = {
  onArtistSelect: (artist: Artist) => void; // fired when the user picks an artist
};

const DEBOUNCE_MS = 280;
const MIN_CHARS = 2;

const capitalize = (str: string) =>
  str
    ? str
        .split(" ")
        .map((w) => w.charAt(0).toUpperCase() + w.slice(1).toLowerCase())
        .join(" ")
    : "";

const SearchBar: React.FC<SearchBarProps> = ({ onArtistSelect }) => {
  const [input, setInput] = useState("");
  const [results, setResults] = useState<Artist[]>([]);
  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [hasQueried, setHasQueried] = useState(false);
  const [activeIndex, setActiveIndex] = useState(-1);

  const containerRef = useRef<HTMLDivElement>(null);
  const abortRef = useRef<AbortController | null>(null);
  const skipFetchRef = useRef(false); // suppress the fetch triggered by selecting

  // Debounced suggestion fetch, cancelling any in-flight request as the user types.
  useEffect(() => {
    if (skipFetchRef.current) {
      skipFetchRef.current = false;
      return;
    }

    const query = input.trim();
    if (query.length < MIN_CHARS) {
      abortRef.current?.abort();
      setResults([]);
      setOpen(false);
      setHasQueried(false);
      setLoading(false);
      return;
    }

    setLoading(true);
    const handle = setTimeout(async () => {
      abortRef.current?.abort();
      const controller = new AbortController();
      abortRef.current = controller;
      try {
        const data = await suggestArtists(query, controller.signal);
        const artists = data.map((a) => new Artist(a));
        setResults(artists);
        setActiveIndex(-1);
        setHasQueried(true);
        setOpen(true);
      } catch (err) {
        const e = err as { code?: string; name?: string };
        if (e?.code === "ERR_CANCELED" || e?.name === "CanceledError") return;
        console.error("Error fetching artist suggestions: ", err);
        setResults([]);
        setHasQueried(true);
        setOpen(true);
      } finally {
        if (!controller.signal.aborted) setLoading(false);
      }
    }, DEBOUNCE_MS);

    return () => clearTimeout(handle);
  }, [input]);

  // Close the dropdown when clicking outside the search bar.
  useEffect(() => {
    const onClickOutside = (e: MouseEvent) => {
      if (containerRef.current && !containerRef.current.contains(e.target as Node)) {
        setOpen(false);
      }
    };
    document.addEventListener("mousedown", onClickOutside);
    return () => document.removeEventListener("mousedown", onClickOutside);
  }, []);

  const select = (artist: Artist) => {
    abortRef.current?.abort();
    skipFetchRef.current = true;
    setInput(capitalize(artist.name));
    setResults([]);
    setOpen(false);
    setActiveIndex(-1);
    onArtistSelect(artist);
  };

  const clear = () => {
    abortRef.current?.abort();
    setInput("");
    setResults([]);
    setOpen(false);
    setHasQueried(false);
    setActiveIndex(-1);
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "ArrowDown") {
      e.preventDefault();
      if (!open && results.length) setOpen(true);
      setActiveIndex((i) => Math.min(i + 1, results.length - 1));
    } else if (e.key === "ArrowUp") {
      e.preventDefault();
      setActiveIndex((i) => Math.max(i - 1, 0));
    } else if (e.key === "Enter") {
      e.preventDefault();
      if (results.length === 0) return;
      select(results[activeIndex >= 0 ? activeIndex : 0]);
    } else if (e.key === "Escape") {
      setOpen(false);
    }
  };

  return (
    <div className="search-bar-container-wrapper" ref={containerRef}>
      <div className="search-bar-container">
        <MdSearch id="search-icon" />
        <input
          className="search-input"
          type="text"
          role="combobox"
          aria-expanded={open}
          aria-controls="artist-suggestions"
          aria-autocomplete="list"
          aria-activedescendant={
            activeIndex >= 0 ? `artist-option-${activeIndex}` : undefined
          }
          autoComplete="off"
          placeholder="Type an artist name to search setlists..."
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={handleKeyDown}
          onFocus={() => {
            if (results.length) setOpen(true);
          }}
        />
        {input && (
          <button
            type="button"
            className="clear-button"
            onClick={clear}
            aria-label="Clear search"
          >
            <MdClose />
          </button>
        )}
      </div>

      {open && (
        <ul
          className="search-suggestions"
          id="artist-suggestions"
          role="listbox"
        >
          {loading && <li className="suggestion-status">Searching…</li>}
          {!loading && hasQueried && results.length === 0 && (
            <li className="suggestion-status">No artists found</li>
          )}
          {!loading &&
            results.map((artist, i) => (
              <li
                key={artist.mbid || `${artist.name}-${i}`}
                id={`artist-option-${i}`}
                role="option"
                aria-selected={i === activeIndex}
                className={`suggestion-item ${i === activeIndex ? "active" : ""}`}
                onMouseEnter={() => setActiveIndex(i)}
                onMouseDown={(e) => {
                  e.preventDefault(); // keep input focus through the click
                  select(artist);
                }}
              >
                <MdSearch className="suggestion-icon" />
                <span className="suggestion-name">{capitalize(artist.name)}</span>
                {artist.disambiguation && (
                  <span className="suggestion-disambiguation">
                    {artist.disambiguation}
                  </span>
                )}
              </li>
            ))}
        </ul>
      )}
    </div>
  );
};

export default SearchBar;
