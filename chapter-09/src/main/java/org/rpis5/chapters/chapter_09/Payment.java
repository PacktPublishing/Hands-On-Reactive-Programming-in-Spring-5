package org.rpis5.chapters.chapter_09;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@Wither
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
	@Id String id;
	String user;
}
