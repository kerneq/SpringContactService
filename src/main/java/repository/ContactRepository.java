package repository;

import entity.Contact;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by iters on 8/15/17.
 */
public interface ContactRepository extends CrudRepository<Contact, Long> {

}