package com.example.batch.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.example.batch.model.Book;

public class BookItemProcessor implements ItemProcessor<Book, Book> {

	private static final Logger log = LoggerFactory.getLogger(BookItemProcessor.class);

	@Override
	public Book process(final Book book) {
		book.setId("B-" + book.getId());
		//Convert If Needed
		log.info("{}",book);
		return book;
	}

}