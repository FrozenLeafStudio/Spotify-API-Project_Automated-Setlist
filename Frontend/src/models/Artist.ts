import { Setlist } from "./Setlist";

export class Artist {
  id: string = "";
  mbid: string = ""; // Musicbrainz Identifier
  tmid?: string = ""; // Ticket Master Identifier
  name: string = "";
  sortName: string = "";
  disambiguation?: string; // Optional, represents artist genre
  url: string = "";
  setlists?: Setlist[]; // Optional, initially not populated

  constructor(initializer: Partial<Artist> = {}) {
    Object.assign(this, initializer);
  }
}
