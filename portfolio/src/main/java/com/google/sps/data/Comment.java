
package com.google.sps.data;

/** A comment in the list of comments. */
public final class Comment {
  private final long id;
  private final String username;
  private final String text;
  private final long timestamp;
  private final String userEmail;

  public Comment(long id, String username, String text, long timestamp, String userEmail) {
    this.id = id;
    this.username = username;
    this.text = text;
    this.timestamp = timestamp;
    this.userEmail = userEmail;
  }
}
