export class AppTrack {
  songUri: string = "";
  songName: string = "";
  artistName: string = "";
  albumName: string = "";
  albumImageUrl: string = "";

  constructor(initializer: Partial<AppTrack> = {}) {
    Object.assign(this, initializer);
  }
}
