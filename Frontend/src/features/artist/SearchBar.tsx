import React, { FormEvent, useState } from "react";
import { MdSearch } from "react-icons/md";
import "./SearchBar.css";

type Props = {
  onSearch: (searchTerm: string) => void; //prop to handle search action
};
export default function SearchBar({ onSearch }: Props) {
  const [input, setInput] = useState("");
  const handleSubmit = (e: FormEvent) => {
    e.preventDefault(); //prevent default form submission
    onSearch(input);
  };
  return (
    <form className="search-bar-container" onSubmit={handleSubmit}>
      <MdSearch id="search-icon" />
      <input
        className="search-input"
        placeholder="Type Artist name to search setlists..."
        type="search"
        value={input}
        onChange={(e) => setInput(e.target.value)}
      />
      <button type="submit">Search</button>
    </form>
  );
}
