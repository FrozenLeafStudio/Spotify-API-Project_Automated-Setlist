import React, { useState } from "react";
import { MdSearch } from "react-icons/md";
import "./SearchBar.css";
import "react-datepicker/dist/react-datepicker.css";

type SearchBarProps = {
  onSearchSubmit: (searchTerm: string) => void; //prop to handle search action
};
const SearchBar: React.FC<SearchBarProps> = ({ onSearchSubmit }) => {
  const [input, setInput] = useState("");
  const handleSubmit = () => {
    onSearchSubmit(input);
  };
  return (
    <div className="search-bar-container">
      <MdSearch id="search-icon" />
      <input
        className="search-input"
        placeholder="Type Artist name to search setlists..."
        type="text"
        value={input}
        onChange={(e) => setInput(e.target.value)}
      />
      <button type="submit" className="search-button" onClick={handleSubmit}>
        Search
      </button>
    </div>
  );
};
export default SearchBar;

/*   
      button and datepicker to be used future phases (after MVP)
      icon = MdCalendarMonth
      import DatePicker from "react-datepicker";
      const selectedDate = (date: Date) => {setDate(date);};
      const [date, setDate] = useState(new Date());
      const [isOpen, setIsOpen] = useState(false);
      <button id="btn-no-style" onClick={() => setIsOpen(!isOpen)}>
        <MdCalendarMonth id="calendar-icon" />
      </button>
      {isOpen ? <DatePicker selected={date} onChange={selectedDate} /> : null} */
