import { AppTrack } from "./AppTrack";

export class Playlist {
  playlist_id: string = "";
  name: string = "";
  description: string = "";
  tracks: AppTrack[] = []; // Assuming AppTrack is another class you've defined
  imageUrl: string = "";
  spotifyUrl: string = "";
  setlistID: string = "";

  constructor(initializer: Partial<Playlist> = {}) {
    Object.assign(this, initializer);
  }
}
