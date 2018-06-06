/**
 * Copyright (C) Zoomdata, Inc. 2012-2017. All rights reserved.
 */
package org.rpis5.chapters.chapter_02.observer;

public interface Observer<T> {
   void observe(T event);
}
