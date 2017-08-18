package repository;

import entity.Contact;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by iters on 8/15/17.
 */

/*
public interface ContactRepository extends CrudRepository<Contact, Long> {
}
*/

public interface ContactRepository extends PagingAndSortingRepository<Contact, Long> {
}