package org.rpis5.chapters.chapter_03.rxjava_reactivestreams;

import org.reactivestreams.Publisher;
import rx.RxReactiveStreams;

import org.springframework.stereotype.Service;

@Service
public class RxFileService implements FileService {

    @Override
    public void writeTo(                                            
            String file,                                                 
            Publisher<String> content                                    
    ) {                                                             

        AsyncFileSubscriber rxSubscriber =
                new AsyncFileSubscriber(file);                            

        content.subscribe(RxReactiveStreams.toSubscriber(rxSubscriber));
    }
}
