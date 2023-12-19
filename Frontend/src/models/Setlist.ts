export class Setlist {
  setlistID: string = "";
  eventDate: string = "";
  artist: {
    mbid: string;
    name: string;
    url: string;
  } = { mbid: "", name: "", url: "" }; // Matches an artist in the Artists Collection
  venue: {
    name: string;
    city: {
      name: string;
      state: string;
      stateCode: string;
      country: {
        code: string;
        name: string;
      };
    };
    url: string;
  } = {
    name: "",
    city: {
      name: "",
      state: "",
      stateCode: "",
      country: { code: "", name: "" },
    },
    url: "",
  };
  tourName: string = "";
  sets: {
    set: {
      name: string;
      song: {
        name: string;
        info: string;
        tape: boolean;
        cover: {
          name: string;
          mbid: string;
          sortName: string;
          url: string;
        } | null;
        with: {
          mbid: string;
          name: string;
          url: string;
        } | null;
      }[];
    }[];
    url: string;
  } = { set: [], url: "" };
  url: string = "";
  constructor(initializer: Partial<Setlist> = {}) {
    Object.assign(this, initializer);
  }
}
