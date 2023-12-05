export class Setlist {
  setlistID: string = "";
  eventDate: string = "";
  mbid: string = ""; // Matches an artist in the Artists Collection
  venueName: string = "";
  venueLocation: string = "";
  tourName: string = "";
  url: string = "";
  songs: string[] = []; // List of song names

  constructor(initializer: Partial<Setlist> = {}) {
    Object.assign(this, initializer);
  }
}
