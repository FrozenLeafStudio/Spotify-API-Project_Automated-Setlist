package com.frozenleafstudio.dev.AutomatedSetlist.Playlist;

// coverType: "auto" (Spotify mosaic), "album" (coverValue = image URL), or "upload" (coverValue = base64 JPEG).
public record PlaylistCreateRequest(String setlistId, boolean includeCovers, String coverType, String coverValue) {}
