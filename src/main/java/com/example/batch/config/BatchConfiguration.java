package com.example.batch.config;
import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.batch.model.Book;
import com.example.batch.processor.BookItemProcessor;
import com.example.batch.repo.BookRepository;


@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	
    private final BookRepository bookRepository;
    private JobRepository jobRepository;
    private PlatformTransactionManager transactionManager;
    //private final DataSource dataSource;

    public BatchConfiguration( BookRepository bookRepository,
    		JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		
		this.bookRepository = bookRepository;
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
		//this.dataSource = dataSource;
	}

    
    @Bean
	public BookItemProcessor processor() {
		return new BookItemProcessor();
	}
    

    @Bean
    public FlatFileItemReader<Book> reader() {
        return new FlatFileItemReaderBuilder<Book>()
                .name("bookItemReader")
                .resource(new ClassPathResource("data.csv"))
                .linesToSkip(1)
                .delimited()
                .names("id", "title", "author", "genre", "publicationYear", "price")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Book>() {{
                    setTargetType(Book.class);
                }})
                .build();
    }
    
    
	@Bean
    public ItemWriter<Book> writer() {
        return items -> {
            for (Book item : items) {
                // Save each item to the database
                // You may use Spring Data JPA repository or EntityManager to save the data
                // For simplicity, assume you have a BookRepository class for saving
                // book entities to the database
                bookRepository.save(item);
            }
        };
    }

    @Bean
    public Step step() {
        return new StepBuilder("csv-step", jobRepository)
            .<Book, Book>chunk(10, transactionManager)
            .reader(reader())
            .processor(processor())
            .writer(writer())
            //.taskExecutor(taskExecutor())
            .build();
        }

    @Bean
    public Job runJob() {
        return new JobBuilder("InsertBook", jobRepository)
            .start(step())
            .build();
    }
   
}
