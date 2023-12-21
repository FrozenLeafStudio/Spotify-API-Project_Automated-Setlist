export class AppTrack {
  trackFound: boolean = false;
  songUri: string = "";
  songName: string = "";
  artistName: string = "";
  albumName: string = "";
  albumImageUrl: string = "";
  details: string = "";
  tape: boolean = false;
  cover: boolean = false;

  constructor(initializer: Partial<AppTrack> = {}) {
    Object.assign(this, initializer);
  }
}
