package interfaces;

import entity.Contact;
import java.util.List;

/**
 * Created by iters on 8/15/17.
 */
public interface ContactService {
    List<Contact> findAll();
    Contact findById(Long id);
    Contact save(Contact contact);
}