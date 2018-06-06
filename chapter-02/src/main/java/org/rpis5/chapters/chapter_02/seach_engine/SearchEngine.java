/**
 * Copyright (C) Zoomdata, Inc. 2012-2018. All rights reserved.
 */
package org.rpis5.chapters.chapter_02.seach_engine;

import java.net.URL;
import java.util.List;

public interface SearchEngine {
   List<URL> seach(String query, int limit);
}
