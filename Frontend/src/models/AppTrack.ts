export class AppTrack {
  trackFound: boolean = false;
  songUri: string = "";
  songName: string = "";
  artistName: string = "";
  albumName: string = "";
  albumImageUrl: string = "";

  constructor(initializer: Partial<AppTrack> = {}) {
    Object.assign(this, initializer);
  }
}
