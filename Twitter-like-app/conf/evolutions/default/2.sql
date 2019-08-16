# Posts schema

# --- !Ups

CREATE TABLE posts (
  id varchar(36) NOT NULL,
  user_id varchar(36) NOT NULL,
  text VARCHAR(255) NOT NULL,
  parent_post_id varchar(36),
  posted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

# --- !Downs

DROP TABLE IF EXISTS posts;