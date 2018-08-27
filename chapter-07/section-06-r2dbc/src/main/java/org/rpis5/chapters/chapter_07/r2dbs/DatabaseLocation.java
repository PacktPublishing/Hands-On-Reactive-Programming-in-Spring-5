package org.rpis5.chapters.chapter_07.r2dbs;

import lombok.Value;

@Value
public class DatabaseLocation {
   private final String host;
   private final Integer port;
   private final String database;
   private final String user;
   private final String password;
}
